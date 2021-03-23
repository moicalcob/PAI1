import socket
import json
import hashlib
import os.path as path
import os
from pathlib import Path
from _sha256 import sha256
from datetime import date
from datetime import datetime
import sqlite3
import sys
import os
import csv
from django.template.defaultfilters import length
from fnmatch import fnmatch
import codecs
import schedule
import time

from utils import generate_mac,explorar_directorios_cliente,generate_hashes_client


def get_constants(prefix):
    return {
        getattr(socket, n): n
        for n in dir(socket)
        if n.startswith(prefix)
    }


families = get_constants('AF_')
types = get_constants('SOCK_')
protocols = get_constants('IPPROTO_')
root = '/Users/amine/OneDrive - UNIVERSIDAD DE SEVILLA/INGLES FIRST'
#root = '/Users/moises/Downloads/prueba'
token = 1233224242

def cargar_cliente():    
    errores = 0
    aciertos = 0
    archivos_corruptos = []
    # Create a TCP/IP socket
    sock = socket.create_connection(('localhost', 10001))
    # Recorremos los archivos
    lst = explorar_directorios_cliente(root)
    # Generamos el hash de los archivos
    datos = generate_hashes_client(lst)
    for file in datos:
        data = {
            'file': file,
            'hash': datos[file],
            'token': token
        }
        client_mac = generate_mac(token, file, datos[file]) #Generamos la mac
        result = json.dumps(data).encode()
        # Send data
        sock.sendall(result)
        amount_received = 0
        data = sock.recv(1024)
        amount_received += len(data)
        response = json.loads(data)
        if(response['status'] == 'VERIFICATION_HASH_FAIL'):
            print('VERIFICATION MAL DEL HASH')
            archivos_corruptos.append(file)
            errores = errores + 1
        else:
            if client_mac == response['mac']:
                aciertos = aciertos + 1 
                print('INTEGRITY_FILE_OK')
            else:
                print('INTEGRITY_FILE_FAIL')
                errores = errores + 1
                if(response['file'] not in archivos_corruptos):
                    archivos_corruptos.append(response['file'])
    print('Porcentaje de integridad aciertos->', porcentaje_integridad(len(datos),aciertos),'1%')
    print('Porcentaje de integridad fallos->', porcentaje_errores(len(datos),errores),'%')
    print('Se han producido ',errores,' fallos de integridad en los archivos: \n')
    
    for file in archivos_corruptos: 
        print('->',file,'\n')
    print('closing socket')
    sock.close()

def porcentaje_integridad(tamaño,aciertos):
    return aciertos * 100 / tamaño
def porcentaje_errores(tamaño,errores):
    return errores * 100 / tamaño

schedule.every().day.at("10:30").do(cargar_cliente)
schedule.every(10).seconds.do(cargar_cliente)

while True:
    schedule.run_pending()
    time.sleep(1)

