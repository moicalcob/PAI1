import socket
import time


HEADERSIZE = 10

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostname(), 1243))
s.listen(5)
print("Hola soy el servidor y he arrancado. Pero no estoy haciendo nada más que esperar")
while True:
    clientsocket, address = s.accept()
    print(f"La conexión desde la dirección {address} ha sido establecida.")

    msg = "Bienvenido al servidor de INGENIUS-ST-22!"
    msg = f"{len(msg):<{HEADERSIZE}}"+msg

    clientsocket.send(bytes(msg,"utf-8"))

    while True:
        time.sleep(3)
        msg = f"El time es:  {time.time()}"
        msg = f"{len(msg):<{HEADERSIZE}}"+msg

        print(msg)

        clientsocket.send(bytes(msg,"utf-8"))