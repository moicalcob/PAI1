En este caso se ha creado un servicio de streaming en el que se envía constantemente el resultadod del time.time, para ello se ha
creado también un buffer. El posible fallo de integridad seria en el caso en el que el tamaño de lo que queramos enviar sea superior al
límite del buffer. Otro posible fallo sería que el cliente y servidor usaran distintos métodos de codificación al enviar datos.
En este caso dado el streaming de datos no fallaría.    