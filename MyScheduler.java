
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
  private Vector colas; //se encarga de guardar las colas de procesos.
 
  public MyScheduler() throws Exception {
  // Leer los datos desde un archivo separado por comas
    leerDatos();
  // Ejecutar el primer algoritmo: FCFS en q0, sin desalojo
    //fcfs(1, 1);
    //sjf(1,1);
    //priority(1,1);
    rr(1,1);
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
       cola_actual = cola;
       
       // Codigo del profe

       Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
       ordenarVector(actual);
       String ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_fcfs.txt";
       File f = new File(ruta);
       FileWriter fw = new FileWriter(f);
       PrintWriter pw = new PrintWriter(fw);
       
       pw.println(" / FCFS Algorithm /");
        pw.println(" ");
       pw.println(" == Ready Queue: ==");

       pw.println("q" + cola_actual + ":");
       float tiempo_actual = 0f;
       float tiempo_requerido = 0f;
       
       for(int i = 0; i < actual.size(); i++) {
          Proceso p = (Proceso) actual.elementAt(i);
          tiempo_requerido += p.getRequired();
          tiempo_requerido += context_change;
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
  	
    cola_actual = cola;

  	Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
    ordenarVector(actual);
    String ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_sjf.txt";
    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(" / SJF Algorithm /");
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
    
    cola_actual = cola;

    Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
    ordenarVector(actual);
    String ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_priority.txt";
    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(" / Priority Algorithm /");
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
    
    cola_actual = cola;

    Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
    ordenarVector(actual);
    String ruta = "/Users/aaronvegu/Desktop/ms/Scheduler/archivos/resultado_rr.txt";
    File f = new File(ruta);
    FileWriter fw = new FileWriter(f);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(" / Round Robin Algorithm /");
    pw.println(" ");
    pw.println(" == Ready Queue: ==");

    pw.println("q" + cola_actual + ":");
    float tiempo_actual = 0f;
    float tiempo_requerido = 0f;
       
    for(int i = 0; i < actual.size(); i++) {
      Proceso p = (Proceso) actual.elementAt(i);
      tiempo_requerido += p.getRequired();
      tiempo_requerido += context_change;
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
  
  
  public static void main(String [] args) {
    try {
     MyScheduler ms = new MyScheduler();
    } catch (Exception e) {
      e.printStackTrace();
      
      System.exit(0);
     }
   
   }

}
