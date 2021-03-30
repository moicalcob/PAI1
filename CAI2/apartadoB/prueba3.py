from multiprocessing import Process, JoinableQueue
import time

'''
INPUT  PARAMETERS: Procesar los cuatro mensajes de canalización
OUTPUT PARAMETERS: Mensaje recibido por el cliente

DETECTED PROBLEM:  La tubería se cierra antes de que se reciban los mensajes
'''


def sender(queue, messages):
    """
    Función para enviar mensajes a la canalización
    """
    for message in messages:
        queue.put(message)
        print("Enviando el mensaje: {}".format(message))
        time.sleep(0.05)


def receiver(queue):
    """
    función para imprimir los mensajes recibidos de otros
    finales de la tuberia
    """
    time.sleep(0.1)
    while not queue.empty():
        message = queue.get()
        if message == "CLOSE PIPE":
            break
        print("Received the message: {}".format(message))


if __name__ == "__main__":
    # messages to be sent
    messages = ["Levantate", "Cepillate los dientes", "Haz los deberes",
                "Saluda a tus profesores del grupo ST-22", "CLOSE PIPE"]

    # creating a queue
    messages_queue = JoinableQueue()

    # creating new processes
    p1 = Process(target=sender, args=(messages_queue, messages))
    p2 = Process(target=receiver, args=(messages_queue,))

    # running processes
    p2.start()
    p1.start()

    # wait until processes finish
    p1.join()
    p2.join() 