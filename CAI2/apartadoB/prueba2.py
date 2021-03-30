from multiprocessing import Process, Queue

'''
INPUT  PARAMETERS: process an ordered queue of 10 elements
OUTPUT PARAMETERS: message of having processed the 10 processes

DETECTED PROBLEM:  not all processes have been processed
'''


def square_list(to_do, done, id_process):
    """
    Función para dar una cola de números para devolver los cuadrados en otra cola
    El proceso que lo realiza está identificado por un ID
    """
    while not to_do.empty():
        elem = to_do.get(timeout=100)
        done.put(elem * elem)
        print('Operación realizada por el proceso ' + id_process)
    print('Tubería vacía para el proceso %s' % id_process)


def print_queue(done):
    """
    Función para imprimir elementos de la cola
    """
    print("Elementos de la cola:")
    while not done.empty():
        print(done.get())
    print("¡La cola ahora está vacía!")


if __name__ == "__main__":
    # creating multiprocessing Queue of initial numbers
    to_do = Queue()
    mylist = [i for i in range(5)]
    [to_do.put(elem) for elem in mylist]

    # creating multiprocessing Queue of square numbers
    done = Queue()

    # creating new processes
    c1 = Process(target=print_queue, args=(done,))
    c1.start()

    p1 = Process(target=square_list, args=(to_do, done, '1'))
    p1.start()

    p2 = Process(target=square_list, args=(to_do, done, '2'))
    p2.start()

    p1.join()
    p2.join()
    c1.join()

