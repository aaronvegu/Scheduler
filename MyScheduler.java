
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
  private int cpus; //numer de cpus
  private int queues;//colas que habra
  private int cola_actual; //cola actual
  private Vector colas; //se encarga de guardar las colas de procesos.
 
  public MyScheduler() throws Exception {
// Leer los datos desde un archivo separado por comas
    leerDatos();
// Ejecutar el primer algoritmo: FCFS en q0, sin desalojo
    fcfs(1, 1);
    //sjf(1,1);
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
  System.out.println("Leidas " + contador + " lineas de datos!");
}

// FCFS Algorithm   
private void fcfs(int cola, int procesadores) throws Exception {
  
   if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
 
   if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");
     cola_actual = cola;
     
     // Codigo del profe

     Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
     ordenarVector(actual);
     String ruta = "/Users/aaronvegu/Desktop/ms/archivos/resultado_fcfs.txt";
     File f = new File(ruta);
     FileWriter fw = new FileWriter(f);
     PrintWriter pw = new PrintWriter(fw);
     
     System.out.println("q" + cola_actual + ":");
     float tiempo_actual = 0f;
     float tiempo_requerido = 0f;
     
     for(int i = 0; i < actual.size(); i++) {
        Proceso p = (Proceso) actual.elementAt(i);
        tiempo_requerido = tiempo_requerido + p.getRequired();
        System.out.println(p.toString());
        
     }
     
     float quantums = 0f;
     int index = 0;
     int procesoesperado = 0;
     int tiempo_s = 0;
     
     for(float i = 0; i <= tiempo_requerido; i++) {
    	 
    	tiempo_s = (int) (tiempo_s + context_change);
    	 
    	 Proceso proceso1 = (Proceso) actual.elementAt(index);	 
    	 if(i == 0) pw.print("0|idle");
    	 
    	 else if(i%quantum==0) {	
    		 quantums += quantum + context_change;
    		 
    	       pw.print("\n"+quantums);
    	       procesoesperado++;
    	       	 
    	 }

    	 if(index!=procesoesperado) {
    	 proceso1.setRequierd(proceso1.getRequired()-1);
    	 pw.print("|"+proceso1.getID()+"|X|");
    	 
    	 }
    	 
    	 if(proceso1.getRequired()==0) {
    		 index++;
    		 //quantums+=quantum+context_change;
    		 }
    	
     }
     
     fw.close();
     pw.close();
     int tiempo_total = (int) (tiempo_s + tiempo_requerido);
     System.out.println("tiempo requerido: " + tiempo_total ); 
     System.out.println("Quantums requeridos: " + tiempo_requerido);
}
  
// SJF Algorithm  
private void sjf(int cola, int procesadores) throws Exception {
	  
  // Excepciones
	 if(cola > queues || cola < 1) throw new Exception("Error: Numero de cola invalido!");
	  
	 if(procesadores > cpus) throw new Exception("Error: Numero de procesadores invalido!");
	     cola_actual = cola;

	     Vector actual = (Vector) ((Vector) colas.elementAt(cola_actual-1)).clone();
	     //.ordenarVector2(actual);
	     String ruta = "/Users/aaronvegu/Desktop/ms/archivos/resultado_sjf.txt";
	     File f = new File(ruta);
	     FileWriter fw = new FileWriter(f);
	     PrintWriter pw = new PrintWriter(fw);
	     
	     System.out.println("q" + cola_actual + ":");
	     float tiempo_actual = 0f;
	     float tiempo_requerido = 0f;

       // pw.print("Escritura de prueba fuera del tiempo"); // Escritura de prueba
	     
	     for(int i = 0; i < actual.size(); i++) {
	         Proceso p = (Proceso) actual.elementAt(i);
	         tiempo_requerido = tiempo_requerido + p.getRequired();
	         System.out.println(p.toString());
	         
	      }
	      
	      float quantums=0f;
	      int index=0;
	      int procesoesperado=0;
	      int tiempo_s= 0;
        float menorTrabajo = 0; // 
	      
	      for(float t = 1; t <= tiempo_requerido; t++) {  // Mientras haya tiempo de ejecución
	     	   
          // pw.print("Escritura de prueba dentro del tiempo"); // Escritura de prueba

  	     	tiempo_s = (int) (tiempo_s + context_change);

  	     	Proceso proceso1 = (Proceso) actual.elementAt((int) index);
  	     	 
          if (proceso1.getArrival() <= t) { // Nos aseguramos de que el proceso ya haya llegado, bajo su segundo indicado
            if (index == 0) { // Esta condicion solo se cumple con el primer proceso
              menorTrabajo = proceso1.getRequired(); // Tomamos como variable a comparar el trabajo requerido del primer proceso que arriva
              pw.print(t + "|" + proceso1.getID()); // Escribimos la ejecucion del primer proceso en arrivar
                for (int j = 0; j <= actual.size(); j++) {
                  Proceso pActual = (Proceso) actual.elementAt(j);
                  if (pActual.getArrival() <= t) {
                    System.out.println("Sí entro y el proceso tiene una llegada de: " + pActual.getArrival());
                  }
                }
            } else {

            }
          }

  	     	if(t == 0) {
  	     		 pw.print("0|idle");
  	     	} else if(t%quantum==0) {	
  	     		 quantums+=quantum+context_change;
  	     		 
  	     	       pw.print("\n"+quantums);
  	     	       procesoesperado++;
  	     	 }
  	     	
  	     	if(index!=procesoesperado) {
  	     	   proceso1.setRequierd(proceso1.getRequired()-1);
  	     	   pw.print("|"+proceso1.getID()+"|X|") ;
  	     	 
  	     	}
  	     	 
  	     	if(proceso1.getRequired()==0) {	 
  	     		 index++; 
  	     		 quantums+=quantum+context_change;
  	     	}  
	     	
	      }
	      
	      fw.close();
        pw.close();
	      int tiempo_total=(int) (tiempo_s+tiempo_requerido);
	      System.out.println("tiempo requerido: "+tiempo_total); 
	      System.out.println("Quantums requeridos: "+tiempo_requerido);
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
