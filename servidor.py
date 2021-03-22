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

from utils import explorar_directorios_server, create_table, drop_database, extraer_hash

root = '/Users/moises/Downloads'

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# Bind the socket to the port
server_address = ('localhost', 10000)
print('Inciando servidor en {} puerto {}'.format(*server_address))
sock.bind(server_address)
# Listen for incoming connections
sock.listen(1)

conn = sqlite3.connect('database.db')
c = conn.cursor()
drop_database(c)
create_table(c)
print('Indexando archivos y generando hashes')
explorar_directorios_server(root, c, conn)
print()
print()

while True:
    print('Esperando conexión')
    connection, client_address = sock.accept()
    try:
        print('Conexión recibida desde', client_address)
        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(1024)
            if data:
                info = json.loads(data)
                datos = extraer_hash(data['file'], data['hash'], c)
                print(datos)
                connection.sendall(data)
            else:
                print('No se han recibido datos', client_address)
                break
    finally:
        # Clean up the connection
        connection.close()
        conn.close()