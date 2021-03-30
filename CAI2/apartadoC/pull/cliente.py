import socket
import sys

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = ('localhost', 10000)
print ('Conectande a %s por el puerto %s' % server_address)
sock.connect(server_address)

try:
    
    # Send data
    print ('Enviando el mensaje.  Seguramente será respondido.')
    sock.send(('Este es el mensaje.  Debe ser respondido.').encode('utf-8'))

    # Look for the response
    #amount_received = 0
    #amount_expected = len(message)
    
    #while amount_received < amount_expected:
    data = sock.recv(1024).decode('utf-8')
        #amount_received += len(data)
    print ('Este es el mensaje que se recive desde el servidor "%s"' % data)

finally:
    print ('closing socket')
    sock.close()