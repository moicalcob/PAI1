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

# ------------ CLIENTE --------------- #

def challenge(i, b):
    result = []
    while i > 0:
        result.insert(0, i % b)
        i = i // b
    resultado = 0
    for i in result:
        resultado = resultado + i
    return str(resultado)

def explorar_directorios_cliente(root):
    print('He entrado a indexar espera un segundo.')
    lst = []
    pattern = "*.*"        # Note: Use this pattern to get all types of files and folders 
    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                absolute_path = os.path.join(path,name)
                lst.append(absolute_path)
    return lst

def generate_mac(token, file, hash):
    challenge_result = challenge(token, 4)
    mac_to_generate = hash + file + challenge_result
    sha256 = hashlib.sha256()
    sha256.update(mac_to_generate.encode())
    return sha256.hexdigest()

def generate_hashes_client(file_list):    
    data = {}
    try:
        sha256 = hashlib.sha256()
        for file in file_list: 
            with open(file, "rb") as f:
                for bloque in iter(lambda: f.read(65536), b""):
                    sha256.update(bloque)
            data[file] = sha256.hexdigest()
        return data
    except Exception as e: 
        print ("ERROR: %s" % (e))
        return ""
    except: 
        print("Error desconocido")   



# ------------ SERVIDOR --------------- #
def drop_database(cursor):
    cursor.execute("DROP TABLE IF EXISTS ficheros")

def create_table(cursor):
    cursor.execute("CREATE TABLE IF NOT EXISTS ficheros(nombre TEXT, hash TEXT, fecha TEXT)")

def insert_data_server(nombre, hash, cursor, conn):
    fecha = datetime.now()
    datos = []
    datos.append(nombre)
    datos.append(hash)
    datos.append(fecha)
    cursor.execute("INSERT INTO ficheros (nombre, hash, fecha) values (?, ?, ?)", datos)   
    conn.commit()

def extraer_hash(nombre,hash, cursor):
    cursor.execute("SELECT * FROM ficheros WHERE nombre = ? AND hash = ?", (nombre,hash))
    rows = cursor.fetchall()
    if len(rows) == 0:
       return "NO SE HA ENCONTRADO EL ARCHIVO SOLICITADO"
    else:  
       for r in rows:
           return r

def explorar_directorios_server(root, cursor, conn):
    print('He entrado a indexar espera un segundo.')
    lst = []
    pattern = "*.*"        
    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                lst.append((os.path.join(path, name)))
    generate_hashes_server(lst, cursor, conn)

def generate_hashes_server(file_list, cursor, conn):    
    try:
        sha256 = hashlib.sha256()
        for file in file_list: 
            with open(file, "rb") as f:
                for bloque in iter(lambda: f.read(65536), b""):
                    sha256.update(bloque)
            insert_data_server(file, sha256.hexdigest(), cursor, conn)
    except Exception as e: 
        print ("ERROR: %s" % (e))
        return ""
    except: 
        print("Error desconocido")   