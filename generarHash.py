import hashlib
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
try:
    import pandas as pd
except:
    os.system("pip3 install pandas")
from fnmatch import fnmatch

path = "archivos"
pathTemp = "/Users/amine/OneDrive - UNIVERSIDAD DE SEVILLA/INGLES FIRST"
root = "/Users/moises/Downloads" # it may have many subfolders and files inside

##BASES DE DATOS ##

def create_table():
    c.execute("CREATE TABLE IF NOT EXISTS ficheros(nombre TEXT, hash TEXT, fecha TEXT)")

def insertData(nombre, hash):
    fecha = datetime.now()
    datos = []
    datos.append(nombre)
    datos.append(hash)
    datos.append(fecha)
    c.execute("INSERT INTO ficheros (nombre, hash, fecha) values (?, ?, ?)", datos)   
    conn.commit()

#FUNCION QUE BUSCA UN FICHERO Y SU HASH EN LA BBDD, Si ambos campos exiten OK sino pa tu casa
def cogerHash(nombre,hash):
    c.execute("SELECT * FROM ficheros WHERE nombre = ? AND hash = ?", (nombre,hash))
    rows = c.fetchall()
    if len(rows) == 0:
       print("NO SE HA ENCONTRADO EL ARCHIVO SOLICITADO")
    else:  
       for r in rows: 
           print(r)

## Explora directorios y llama a generteHash Para generar los hashes
def explorarDirectorios():
    lst = []
    pattern = "*.*"        # Note: Use this pattern to get all types of files and folders 
    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                lst.append((os.path.join(path, name)))
    generateHash(lst)

## GENERA EL HASH DE CADA FICHERO Y LO GUARDA EN BBDD
def generateHash(file_list):
    data = {}
    i = 0
    try:
        sha256 = hashlib.sha256()
        for file in file_list: 
            with open(file, "rb") as f:
                for bloque in iter(lambda: f.read(65536), b""):
                    sha256.update(bloque)
            insertData(file, sha256.hexdigest())
            data[file] = sha256.hexdigest()
        print (data)
    except Exception as e: 
        print ("ERROR: %s" % (e))
        return ""
    except: 
        print("Error desconocido")    
    
if __name__ == "__main__":
    conn = sqlite3.connect('database.db')
    c = conn.cursor()
    explorarDirectorios()
    cogerHash("Luis","IOASUEHQADGNQIUEH3258723943KNDIF")
    conn.close()