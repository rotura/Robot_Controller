package application.model;

import java.net.URL;

public class PeticionHttp extends Thread {

	String peticion;

	public PeticionHttp(String msg) {
		super(msg);
		setMensaje(msg);
	}

	public void run() {
		try {
			URL peticion = new URL(this.peticion);
			peticion.openStream();
			peticion = null;
			System.out.println("Peticion " + this.peticion + " enviada correctamente");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void setMensaje(String msj) {
		this.peticion = msj;
	}
}
