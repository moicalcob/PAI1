import hashlib
import os.path as path 
import os 
from pathlib import Path
from _sha256 import sha256

path = "archivos"
pathTemp = "/Users/amine/OneDrive - UNIVERSIDAD DE SEVILLA/INGLES FIRST"

def generarHash():
    datos= {}
    with os.scandir(pathTemp) as ficheros:
        for fichero in ficheros: 
            if (not fichero.is_dir()):
                hash_file = generandoHash(pathTemp +"/"+fichero.name)
                datos[fichero.name] = hash_file
    print(datos)
    return datos
               
def generandoHash(archivo):
    try: 
        sha256 = hashlib.sha256()
        with open(archivo, "rb") as f:
            for bloque in iter(lambda: f.read(65536), b""):
                sha256.update(bloque)
            return sha256.hexdigest()
                        
    except Exception as e: 
        print ("ERROR: %s" % (e))
        return ""
    except: 
        print("Error desconocido")
    
    
    
if __name__ == "__main__":
    generarHash()