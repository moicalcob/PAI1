import hashlib
import os.path as path 
import os 
from pathlib import Path
from _sha256 import sha256
import sys,os
import csv
try:
    import pandas as pd
except:
    os.system("pip3 install pandas")
from fnmatch import fnmatch

path = "archivos"
pathTemp = "/Users/amine/OneDrive - UNIVERSIDAD DE SEVILLA/INGLES FIRST"
root = "/Users/moises/Downloads" # it may have many subfolders and files inside
def explorarDirectorios():
    lst = []
    pattern = "*.*"        # Note: Use this pattern to get all types of files and folders 
    for path, subdirs, files in os.walk(pathTemp):
        for name in files:
            if fnmatch(name, pattern):
                lst.append((os.path.join(path, name)))
    generateHash(lst)
    
def generateHash(file_list):
    data = {}
    i = 0
    try:
        sha256 = hashlib.sha256()
        for file in file_list: 
            with open(file, "rb") as f:
                for bloque in iter(lambda: f.read(65536), b""):
                    sha256.update(bloque)
            data[file] = sha256.hexdigest()
        print (data)
        return data
#        df = pd.DataFrame({"Fichero;Hash": datos})
#        df.to_csv("hashes2.csv")
    except Exception as e: 
        print ("ERROR: %s" % (e))
        return ""
    except: 
        print("Error desconocido")    
    
if __name__ == "__main__":
    explorarDirectorios()