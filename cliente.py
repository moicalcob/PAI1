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

root = '/Users/moises/Downloads'
token = 1233224242

# Create a TCP/IP socket
sock = socket.create_connection(('localhost', 10000))

try:

    #Recorremos los archivos
    lst = []
    pattern = "*.*"
    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                lst.append((os.path.join(path, name)))

    #Generamos el hash de los archivos
    for file in lst:
        hash = generate_hash_file(file)

        data = {
            'file': file,
            'hash': hash,
            'token': token
        }
        result = json.dumps(data).encode()

        # Send data
        sock.sendall(result)

        amount_received = 0
        amount_expected = len(result)

        while amount_received < amount_expected:
            data = sock.recv(1024)
            amount_received += len(data)
            print('received {!r}'.format(data))
finally:
    print('closing socket')
    sock.close()