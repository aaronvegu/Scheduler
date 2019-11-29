
public class Proceso {
private String id;
private int arrival;
private float required;
private int priority;

public Proceso(String s) {
String[] partes = s.split(", ");
id = partes[0];
arrival = Integer.parseInt(partes[1]);
required = Float.parseFloat(partes[2]);
priority = Integer.parseInt(partes[3]);
}
public String toString() {
String r = id + ", " + arrival + ", " + getRequired() + ", " + priority;
return r;
}
public float getArrival() {
	
	return arrival;
}
public void run(float q) {
required = required - q;
}
public float getRequired() {
return required;
}
public void setRequierd(float requerido) {
	required=+requerido;
	
}
public String getID() {
	return id;
}
public int getPriority() {
	
	return priority;
}


}