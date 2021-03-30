import socket
import sys
from base64 import b64encode

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 10000)
print ('Arrancando en el host %s con el puerto %s' % server_address)
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print ('Esperando la conexión')
    connection, client_address = sock.accept()

    try:
        print ('Conectando desde ', client_address)

        data = b64encode(connection.recv(1024))
        print ('Se ha recibido lo siguiente desde el cliente "%s"' % data)
        if data:
            print ('Enviando los siguientes datos de vuelta al cliente: ')
            connection.send(data)
        else:
            print ('No hay mas datos por parte del cliente ', client_address)
            break
            
    finally:
        # Clean up the connection
        connection.close()

