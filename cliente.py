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

import schedule
import time

from utils import generate_mac, generate_hash_file


def get_constants(prefix):
    return {
        getattr(socket, n): n
        for n in dir(socket)
        if n.startswith(prefix)
    }


families = get_constants('AF_')
types = get_constants('SOCK_')
protocols = get_constants('IPPROTO_')

root = '/Users/moises/Downloads/prueba'
token = 1233224242


def cargar_cliente():
    # Create a TCP/IP socket
    sock = socket.create_connection(('localhost', 10001))

    # Recorremos los archivos
    lst = []
    pattern = "*.*"
    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                lst.append((os.path.join(path, name)))
    # Generamos el hash de los archivos
    for file in lst:
        hash = generate_hash_file(file)
        data = {
            'file': file,
            'hash': hash,
            'token': token
        }
        client_mac = generate_mac(token, file, hash)
        result = json.dumps(data).encode()
        # Send data
        sock.sendall(result)
        amount_received = 0
        data = sock.recv(1024)
        print(data)
        amount_received += len(data)
        response = json.loads(data)
        if client_mac == response['mac']:
            print('VERIFICATION OLE')
        else:
            print('MAL')
    print('closing socket')
    sock.close()

cargar_cliente()

# schedule.every().day.at("10:30").do(cargar_cliente)
# schedule.every(10).seconds.do(cargar_cliente)

# while 1:
#     schedule.run_pending()
#     time.sleep(1)
