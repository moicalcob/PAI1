from multiprocessing import Process, JoinableQueue
from queue import Empty
import time

'''
INPUT  PARAMETERS: 10 Peticiones de solicitud
OUTPUT PARAMETERS: 10 Acuse de recibo de solicitudes

DETECTED PROBLEM:  El consumidor ha agotado su tiempo antes de haber terminado de generar todas las solicitudes el productor
'''


def consumidor(que, pid):
    while True:
        try:
            item = que.get(timeout=2)
            print("Proceso %s consume: %s. Ingreso aceptado" % (pid, item))
            que.task_done()
        except Empty:
            break
    print('Consumidor %s ha hecho' % pid)


def producer(sequence, que):
    for item in sequence:
        print('Nueva tarea: ', item)
        que.put(item)
        time.sleep(1.99999999999999999)


if __name__ == '__main__':
    # En este ejemplo el productor crea nuevas tareas superando la capacidad de los consumidores, se perderán datos.
    que = JoinableQueue()

    # create two consumer process
    cons_p1 = Process(target=consumidor, args=(que, 1))
    cons_p1.start()
    cons_p2 = Process(target=consumidor, args=(que, 2))
    cons_p2.start()

    names = ['Moises', 'Álvaro', 'Amine', 'Luis', 'Juan', 'Alcora', 'María', 'Carmen', 'Rosario', 'Clara']
    numbers = [i for i in range(10)]
    taks = ['Ingreso de %s€ a la cuenta de %s' % (i, names[i % len(names)]) for i in range(15)]
    producer(taks, que)

    que.join()

    cons_p1.join()
    cons_p2.join()
