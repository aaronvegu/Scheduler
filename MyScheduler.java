
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class MyScheduler {

  private FileReader fr; //lee el archivo
  private BufferedReader br;// espacio donde se guardan los datos (temporalmente)
  private float quantum = 0; //es el tiempo que se le da un proceso para ejecutarse 
  private float context_change; //variables
  private int cpus; //numero de cpus
  private int queues;//colas que habra
  private int cola_actual; //cola actual
  private Vector colas, vectorUnico, actual, cpu0, cpu1; //se encarga de guardar las colas de procesos.
  private String ruta, titulo;
 
  public MyScheduler() throws Exception {
  // Leer los datos desde un archivo separado por comas
    leerDatos();
  // Ejecutar el primer algoritmo: FCFS en q0, sin desalojo
    fcfs(1, 3);
    //sjf(1,1);
    //priority(1,1);
    //rr(1,1);
   }

  private void leerDatos() throws Exception { 
   
    fr = new FileReader("prueba.txt");
    br = new BufferedReader(fr);
    int contador = 0;
    String paraComparar = "";
    while(br.ready()) {
    	
       String partes[] ;
   
       
       paraComparar = br.readLine();
   
       if(paraComparar.startsWith("quantum,") && quantum == 0f) {
      	 
          partes = paraComparar.split(", ");
    
          quantum = Float.parseFloat(partes[1]);
          
       if(quantum <= 0f) throw new Exception("Error: el quantum es invalido!");
          contador++;
       }
       
       if(paraComparar.startsWith("context_change,") && context_change == 0f) {
         partes = paraComparar.split(", ");
         context_change = Float.parseFloat(partes[1]);
       if(context_change < 0f) throw new Exception("Error: el tiempo de cambio de contexto es invalido!");
         contador++;
       }
       
       if(paraComparar.startsWith("cpu,") && cpus == 0f) {
         partes = paraComparar.split(", ");
         cpus = Integer.parseInt(partes[1]);
     
       if(cpus < 1) throw new Exception("Error: el numero de CPU's es invalido!");
         contador++;
       } 
       
       if(paraComparar.startsWith("queues,") && queues == 0) {
         partes = paraComparar.split(", ");
         queues = Integer.parseInt(partes[1]);
     
       if(queues < 1) throw new Exception("Error: el numero de colas es invalido!");
         colas = new Vector();
         contador++;
       }
       
       if(paraComparar.startsWith("q,")) {
         partes = paraComparar.split(", ");
         cola_actual = Integer.parseInt(partes[1]);
    
       if(cola_actual < 1) throw new Exception("Error: el numero de cola es invalido!");
         colas.add(new Vector());
         contador++;
       }
      
       if(paraComparar.startsWith("p") && cola_actual > 0) {
         Proceso p = new Proceso(paraComparar);
         ((Vector) colas.elementAt(cola_actual-1)).add(p);

         contador++;
       }
      
       if(paraComparar.equals("end.")) {
          contador++;
       }
    }
    fr.close();
    br.close();
    System.out.println("¡Leidas " + contador + " lineas de datos!");
  }

  // FCFS Algorithm   
  private void fcfs(int cola, int procesadores) throws Exception {
    
     if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
   
     if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");

     if(cpus > 2) throw new Exception("Error: Numero de CPU's no soportado, intente con 2");
       
       // Analizamos si debemos trabajar multicolas o se ingreso solo una cola
       if (cola > 1) {

         vectorUnico = new Vector(); // Instanciamos el Vector Unico que contendra todas las colas
         vectorUnico = crearColaUnica(cola); // Guardamos el vector unico recibido del metodo

         actual = vectorUnico; // Guardamos el vectorUnico en el Vector actual
         ordenarVector(actual); // Se ordena el Vector que contiene la cola
         cola_actual = 1;
         ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_fcfs_MQ.txt";
         titulo = " / FCFS Multi-Queue Algorithm /";


         System.out.println("Se corre el algoritmo en modo multicola");

       } else {

        cola_actual = cola;
        actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
        ordenarVector(actual);
        ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_fcfs.txt";
        titulo = " / FCFS NA Algorithm /";

        System.out.println("Se corre el algoritmo con una sola cola");

       }

       ordenarCpus(actual);
       System.out.println("Cola de procesos del CPU0: " + cpu0);
       System.out.println("Cola de procesos del CPU1: " + cpu1);
       
       File f = new File(ruta);
       FileWriter fw = new FileWriter(f);
       PrintWriter pw = new PrintWriter(fw);
       
       pw.println(titulo);
       pw.println(" ");
       pw.println(" == Ready Queue: ==");

       pw.println("q" + cola_actual + ":");
       float tiempo_actual = 0f;
       float tiempo_requerido = 0f;
       
       for(int i = 0; i < actual.size(); i++) {
          Proceso p = (Proceso) actual.elementAt(i);
          tiempo_requerido += p.getRequired() + (p.getRequired() * context_change);
          pw.println(p.toString());
        }

       pw.println(" == End of Queue ==");
       pw.println(" ");

       float quantums = 0f;
       int index = 0;
       int procesoesperado = 0;
       int tiempo_s = 0;
       float tiempoInicio = 0;
       int time = 0;
       int bandera = 0;

       Proceso primerProceso = (Proceso) actual.elementAt(0);
       tiempoInicio = primerProceso.getArrival();
       tiempoInicio += (tiempoInicio * context_change);

       while(time <= tiempo_requerido) {

        for (int i = 0; i < actual.size(); i++) {
          Proceso procesoActual = (Proceso) actual.elementAt(i);
          if ((procesoActual.getArrival() <= time) && (procesoActual.getRequired() > 0)) {
            while(procesoActual.getRequired() > 0) {
              pw.print(time + " | " + procesoActual.getID() + " | ");
              time += context_change;
              pw.print(time + " | X | ");
              procesoActual.run(quantum);
              time++;
              bandera = 1;
            }
          }
        }

        if (bandera == 0) {
          pw.print(time + " | IDLE | ");
          time += context_change;
          pw.print(time + " | X | ");
          time++;
        }

       }

       pw.println(" ");
       pw.println(" ");
       pw.println("Tiempo de Inicio: " + tiempoInicio);
       pw.println("Tiempo Final: " + (time - 1));
       pw.println(" ");
       pw.println("----- END OF ALGORITHM -----");

       fw.close();
       pw.close();

              
  }
  
  // SJF Algorithm
  private void sjf(int cola, int procesadores) throws Exception {
  	  
    // Excepciones
  	if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
  	  
    if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");
  	
      // Analizamos si debemos trabajar multicolas o se ingreso solo una cola
     if (cola > 1) {

       vectorUnico = new Vector(); // Instanciamos el Vector Unico que contendra todas las colas
       vectorUnico = crearColaUnica(cola); // Guardamos el vector unico recibido del metodo

       actual = vectorUnico; // Guardamos el vectorUnico en el Vector actual
       ordenarVector(actual); // Se ordena el Vector que contiene la cola
       cola_actual = 1;
       ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_sjf_MQ.txt";
       titulo = " / SJF Multi-Queue Algorithm /";


       System.out.println("Se corre el algoritmo en modo multicola");

     } else {

      cola_actual = cola;
      actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
      ordenarVector(actual);
      ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_sjf.txt";
      titulo = " / SJF NA Algorithm /";

      System.out.println("Se corre el algoritmo con una sola cola");

     }
    
    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(titulo);
    pw.println(" ");
    pw.println(" == Ready Queue: ==");
  	     
    pw.println("q" + cola_actual + ":");
    float tiempo_actual = 0f;
    float ejecucionTotal = 0f;

    for(int i = 0; i < actual.size(); i++) {
      Proceso p = (Proceso) actual.elementAt(i);
      ejecucionTotal = ejecucionTotal + p.getRequired();
      pw.println(p.toString());
    }

    pw.println(" == End of Queue ==");
    pw.println(" ");

    // Obtencion del tiempo de inicio de ejecucion
    Proceso primerProceso = (Proceso) actual.elementAt(0); // Obtenemos el primer proceso de la cola
    float tiempoInicio = primerProceso.getArrival(); // Y como la cola esta ordenada segun llegada del proceso, el arrival del primer proceso en cola sera el tiempo de inicio de ejecucion

    // Declaracion de variables a usar en el core del algoritmo
    int st = 0, pt = 0, pf = 0; // Starting Time, Procesos Totales y Procesos Finalizados


    pt = actual.size(); // Obtenemos el numero total de procesos en cola

    while(true) {

      int indicador = pt; // El indicador se iguala al numero de procesos de la cola
      int min = 999; // Y el valor minimo actual es un numero grande, para que el primer proceso en entrar sea siempre el que tiene minimo trabajo

      if (pf == pt) // Si procesos finalizados = procesos totales, romper el ciclo
        break;

      for (int i = 0; i < actual.size(); i++) { // Recorremos la cola de procesos

        Proceso procesoActual = (Proceso) actual.elementAt(i); // Obtenemos el proceso actual de la cola

        if ((procesoActual.getArrival() <= st) && (procesoActual.getRequired() > 0) && (procesoActual.getRequired() < min)) {
          min = (int) procesoActual.getRequired(); // El trabajo minimo ahora es el required del proceso actual
          indicador = i; // Al guardar el index del proceso que entro en la condicion en el indicador, estamos indicando que proceso debe ser ejecutado
        }

      }

      if (indicador == pt) { // Si el indicador es igual a pt, significa que no entro ningun proceso, por lo tanto marcamos un IDLE y aumentamos el tiempo

        pw.print(st + " | IDLE | "); // Indicamos el IDLE en cpu
        st += context_change;
        pw.print(st + " | X | ");
        st++; // Aumentamos el tiempo

      } else { // De tener proceso a ejecutar, pasamos a su ejecucion:

        Proceso procesoARecorrer = (Proceso) actual.elementAt(indicador); // Se instancia el proceso a recorrer

        while(procesoARecorrer.getRequired() > 0) { // Mientras el proceso aun tenga recursos por ejecutar:
          //int tiempoContexto = st + context_change;
          pw.print(st + " | " + procesoARecorrer.getID() + " | "); // Imprimos la ejecucion
          st += context_change;
          pw.print(st + " | X | "); // Imprimimos el cambio de contexto
          procesoARecorrer.run(quantum); // Hacemos la ejecucion del proceso
          st++;
        }

        pf++; // Indicamos la finalizacion del proceso

      }
         
    }

    pw.println(" | END |");
    pw.println(" ");
    pw.println("Tiempo de Inicio: " + tiempoInicio);
    pw.println("Tiempo Final: " + (st - 1));
    pw.println(" ");
    pw.println("----- END OF ALGORITHM -----");
  	      
  	fw.close();
    pw.close();
  }

  // Priority Algorithm
  private void priority(int cola, int procesadores) throws Exception {

    // Excepciones
    if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
      
    if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");
    
    // Analizamos si debemos trabajar multicolas o se ingreso solo una cola
     if (cola > 1) {

       vectorUnico = new Vector(); // Instanciamos el Vector Unico que contendra todas las colas
       vectorUnico = crearColaUnica(cola); // Guardamos el vector unico recibido del metodo

       actual = vectorUnico; // Guardamos el vectorUnico en el Vector actual
       ordenarVector(actual); // Se ordena el Vector que contiene la cola
       cola_actual = 1;
       ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_priority_MQ.txt";
       titulo = " / Priority Multi-Queue Algorithm /";


       System.out.println("Se corre el algoritmo en modo multicola");

     } else {

      cola_actual = cola;
      actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
      ordenarVector(actual);
      ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_priority.txt";
      titulo = " / Priority NA Algorithm /";

      System.out.println("Se corre el algoritmo con una sola cola");

     }
    
    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(titulo);
    pw.println(" ");
    pw.println(" == Ready Queue: ==");

    pw.println("q" + cola_actual + ":");
    float tiempo_actual = 0f;
    float ejecucionTotal = 0f;

    for(int i = 0; i < actual.size(); i++) {
      Proceso p = (Proceso) actual.elementAt(i);
      ejecucionTotal = ejecucionTotal + p.getRequired();
      pw.println(p.toString());
    }

    pw.println(" == End of Queue ==");
    pw.println(" ");

    // Obtencion del tiempo de inicio de ejecucion
    Proceso primerProceso = (Proceso) actual.elementAt(0); // Obtenemos el primer proceso de la cola
    float tiempoInicio = primerProceso.getArrival(); // Y como la cola esta ordenada segun llegada del proceso, el arrival del primer proceso en cola sera el tiempo de inicio de ejecucion

    // Declaracion de variables a usar en el core del algoritmo
    int st = 0, pt = 0, pf = 0; // Starting Time, Procesos Totales y Procesos Finalizados


    pt = actual.size(); // Obtenemos el numero total de procesos en cola

    while(true) {

      int indicador = pt; // El indicador se iguala al numero de procesos de la cola
      int min = 999; // Y el valor minimo actual es un numero grande, para que el primer proceso en entrar sea siempre el que tiene mayor prioridad

      if (pf == pt) // Si procesos finalizados = procesos totales, romper el ciclo
        break;

      for (int i = 0; i < actual.size(); i++) { // Recorremos la cola de procesos

        Proceso procesoActual = (Proceso) actual.elementAt(i); // Obtenemos el proceso actual de la cola

        if ((procesoActual.getArrival() <= st) && (procesoActual.getRequired() > 0) && (procesoActual.getPriority() < min)) {
          min = (int) procesoActual.getPriority(); // La mayor prioridad ahora es la prioridad del proceso que logro entrar
          indicador = i; // Al guardar el index del proceso que entro en la condicion en el indicador, estamos indicando que proceso debe ser ejecutado
        }

      }

      if (indicador == pt) { // Si el indicador es igual a pt, significa que no entro ningun proceso, por lo tanto marcamos un IDLE y aumentamos el tiempo

        pw.print(st + " | IDLE | "); // Indicamos el IDLE en cpu
        st += context_change;
        pw.print(st + " | X | ");
        st++; // Aumentamos el tiempo

      } else { // De tener proceso a ejecutar, pasamos a su ejecucion:

        Proceso procesoARecorrer = (Proceso) actual.elementAt(indicador); // Se instancia el proceso a recorrer

        while(procesoARecorrer.getRequired() > 0) { // Mientras el proceso aun tenga recursos por ejecutar:
          //int tiempoContexto = st + context_change;
          pw.print(st + " | " + procesoARecorrer.getID() + " | "); // Imprimos la ejecucion
          st += context_change;
          pw.print(st + " | X | "); // Imprimimos el cambio de contexto
          procesoARecorrer.run(quantum); // Hacemos la ejecucion del proceso
          st++;
        }

        pf++; // Indicamos la finalizacion del proceso

      }
         
    }

    pw.println(" | END |");
    pw.println(" ");
    pw.println("Tiempo de Inicio: " + tiempoInicio);
    pw.println("Tiempo Final: " + (st - 1));
    pw.println(" ");
    pw.println("----- END OF ALGORITHM -----");
          
    fw.close();
    pw.close();


  }
 
  // Round Robin Algorithm
  private void rr(int cola, int procesadores) throws Exception {

    // Excepciones
    if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
      
    if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");
    
     // Analizamos si debemos trabajar multicolas o se ingreso solo una cola
     if (cola > 1) {

       vectorUnico = new Vector(); // Instanciamos el Vector Unico que contendra todas las colas
       vectorUnico = crearColaUnica(cola); // Guardamos el vector unico recibido del metodo

       actual = vectorUnico; // Guardamos el vectorUnico en el Vector actual
       ordenarVector(actual); // Se ordena el Vector que contiene la cola
       cola_actual = 1;
       ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_rr_MQ.txt";
       titulo = " / Round Robin Multi-Queue Algorithm /";


       System.out.println("Se corre el algoritmo en modo multicola");

     } else {

      cola_actual = cola;
      actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
      ordenarVector(actual);
      ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_rr.txt";
      titulo = " / Round Robin Algorithm /";

      System.out.println("Se corre el algoritmo con una sola cola");

     }

    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(titulo);
    pw.println(" ");
    pw.println(" == Ready Queue: ==");

    pw.println("q" + cola_actual + ":");
    float tiempo_actual = 0f;
    float tiempo_requerido = 0f;
       
    for(int i = 0; i < actual.size(); i++) {
      Proceso p = (Proceso) actual.elementAt(i);
      tiempo_requerido += p.getRequired() + (p.getRequired() * context_change);
      pw.println(p.toString());
    }

    pw.println(" == End of Queue ==");
    pw.println(" ");

    float quantums = 0f;
    int index = 0;
    int procesoesperado = 0;
    int tiempo_s = 0;
    float tiempoInicio = 0;
    int time = 0;
    int bandera = 0;

   Proceso primerProceso = (Proceso) actual.elementAt(0);
   tiempoInicio = primerProceso.getArrival();
   tiempoInicio += (tiempoInicio * context_change);

   while(time <= tiempo_requerido) {

    for (int i = 0; i < actual.size(); i++) {
      Proceso procesoActual = (Proceso) actual.elementAt(i);
      if ((procesoActual.getArrival() <= time) && (procesoActual.getRequired() > 0)) {
        
          pw.print(time + " | " + procesoActual.getID() + " | ");
          time += context_change;
          pw.print(time + " | X | ");
          procesoActual.run(quantum);
          time++;
          bandera = 1;
        
      }
    }

    if (bandera == 0) {
      pw.print(time + " | IDLE | ");
      time += context_change;
      pw.print(time + " | X | ");
      time++;
    }

   }

   pw.println(" ");
   pw.println(" ");
   pw.println("Tiempo de Inicio: " + tiempoInicio);
   pw.println("Tiempo Final: " + (time - 1));
   pw.println(" ");
   pw.println("----- END OF ALGORITHM -----");

   fw.close();
   pw.close();

  }
  
  // Ordenar vector
  public void ordenarVector(Vector actual) {
  	  
  	     for(int i = 1; i <= actual.size(); i++) {
  	          for(int j = actual.size()-1; j >= i; j--) { 
  	    		 Proceso proceso2=(Proceso) actual.elementAt(j-1);
  	    		 Proceso x;
  	        	 Proceso proceso1=(Proceso) actual.elementAt(j);
  	    		 if( proceso2.getArrival()>proceso1.getArrival()) {
  	       			 x = proceso2;
  	    			 actual.set(j-1, proceso1);
  	    			 actual.set(j, x);
  	    			 
  	    		 }
  	    		 	 
  	    	 }
  	     }	     
  }

  // Creador de cola unica para hacer uso de Multiqueue Scheduling
  private Vector crearColaUnica(int cola) throws Exception {

    if (cola > queues) throw new Exception("Error: Las cosas no coinciden con las colas del archivo");
    if (cola < 2) throw new Exception("Error: Debes agregar dos o mas colas");

    Vector recipiente = new Vector(); // Instanciamos el vector donde se guardaran todas las colas
      
    for (int i = 0; i < colas.size(); i++) { // Recorremos el arreglo con las colas leidas del archivo
      Vector vectorActual = (Vector) colas.elementAt(i);
      
      for (int j = 0; j < vectorActual.size(); j++) { // Con la cola actual, se recorre segun su longitud
        recipiente.add(vectorActual.elementAt(j)); // Y se agregan sus procesos al vector unico
      }
      
    }

    System.out.println("¡Vector unico creado!");
    System.out.println("Vector: " + recipiente);
    
    return recipiente;

  }
  
  // Multi-CPU's
  private void ordenarCpus(Vector colaDeProcesos) {

    cpu0 = new Vector(); // Cola de procesos del CPU0
    cpu1 = new Vector(); // Cola de procesos del CPU1

    float t = 0, d0 = 0, d1 = 0;
    int pf = 0;

    while(pf <= colaDeProcesos.size()) {

      for (int i = 0; i < colaDeProcesos.size(); i++) { // Recorremos la cola de procesos

        Proceso procesoActual = (Proceso) colaDeProcesos.elementAt(i); // Se instancia el proceso actual a trabajar

        if (d0 <= d1) { // Cuando el CPU0 tenga mejor o igual disponibilidad que el CPU1, trabajamos sobre él:
          // Acciones realizadas sobre el CPU0
          if (procesoActual.getArrival() <= d0) { // Si el arrival es menor o igual a la disponibilidad del CPU0:

            d0 += procesoActual.getRequired(); // Actualizamos el tiempo de diosponibilidad del proceso
            cpu0.add(procesoActual); // Guardamos el proceso actual en la cola del CPU0
            pf++; // Indicamos que hemos terminado el reacomodo de un proceso
            System.out.println("Proceso " + procesoActual.getID() + " acomodado en CPU0");


          } else { // Si el arrival del proceso no coincide con el tiempo de disponibilidad del proceso:

            d0 = (procesoActual.getArrival() + procesoActual.getRequired()); // entonces a la disponibilidad le sumamos el length y arrival del proceso actual
            cpu0.add(procesoActual); // Guardamos el proceso actual en la cola del CPU0
            pf++; // Indicamos que hemos terminado el reacomodo de un proceso
            System.out.println("Proceso " + procesoActual.getID() + " acomodado en CPU0");

          }
          
        } else { // De tener mejor disponibilidad el CPU1, trabajamos sobre él
          // Acciones realizadas sobre el CPU0
          if (procesoActual.getArrival() <= d1) { // Si el arrival es menor o igual a la disponibilidad del CPU1:

            d1 += procesoActual.getRequired(); // Actualizamos el tiempo de diosponibilidad del proceso
            cpu1.add(procesoActual); // Guardamos el proceso actual en la cola del CPU0
            pf++; // Indicamos que hemos terminado el reacomodo de un proceso
            System.out.println("Proceso " + procesoActual.getID() + " acomodado en CPU1");


          } else { // Si el arrival del proceso no coincide con el tiempo de disponibilidad del proceso:

            d1 = (procesoActual.getArrival() + procesoActual.getRequired()); // entonces a la disponibilidad le sumamos el length y arrival del proceso actual
            cpu1.add(procesoActual); // Guardamos el proceso actual en la cola del CPU0
            pf++; // Indicamos que hemos terminado el reacomodo de un proceso
            System.out.println("Proceso " + procesoActual.getID() + " acomodado en CPU1");

          }

        }

      }

    }

    System.out.println("¡El reacomodo de procesos ha terminado!");

  }
  
  public static void main(String [] args) {
    try {
     MyScheduler ms = new MyScheduler();
    } catch (Exception e) {
      e.printStackTrace();
      
      System.exit(0);
     }
   
   }

}
