import socket
import hashlib
import json
import os.path as path 
import os 
from pathlib import Path
from _sha256 import sha256
from datetime import date
from datetime import datetime 
import sqlite3
import sys,os
import csv
from django.template.defaultfilters import length

from utils import explorar_directorios_server, create_table, drop_database, extraer_hash, generate_mac

# root = '/Users/amine/OneDrive - UNIVERSIDAD DE SEVILLA/INGLES FIRST'
root = '/Users/moises/Downloads/prueba/prueba1'

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# Bind the socket to the port
server_address = ('localhost', 10001)
print('Inciando servidor en {} puerto {}'.format(*server_address))
sock.bind(server_address)
# Listen for incoming connections
sock.listen(1)
os.remove('database.db')
print('He borrado el archivo de bases de datos antiguo')
conn = sqlite3.connect('database.db')
print('Me he conectado a Base de Datos')
c = conn.cursor()
#drop_database(c)
create_table(c)
print('He creado la Tabla en Base de Datos')
print('Indexando archivos y generando hashes')
explorar_directorios_server(root, c, conn)
print()
print()

while True:
    print('Esperando conexión')
    connection, client_address = sock.accept()
    print('Conexión recibida desde', client_address)
    # Receive the data in small chunks and retransmit it
    while True:
        data = connection.recv(1024)
        if data:
            info = json.loads(data)
            datos = extraer_hash(info['file'], info['hash'], c)
            #print(datos)
            if(len(datos) > 3):
                response = {
                    'hash': '',
                    'mac': '',
                    'status':'VERIFICATION_HASH_FAIL'
                }
                encoded_response = json.dumps(response).encode()
                #print(encoded_response)
                connection.sendall(encoded_response)
            else:
                #print("VERIFICACIÓN CORRECTA DEL ARCHIVO: " + datos[0])
                mac = generate_mac(info['token'], info['file'], info['hash'])
                response = {
                    'hash': datos[1],
                    'mac': mac,
                    'status': 'VERIFICATION_HASH_OK'
                }
                encoded_response = json.dumps(response).encode()
                connection.sendall(encoded_response)
        else:
            print('No se han recibido datos', client_address)
            break