package application.model;

import java.net.URL;

public class PeticionHttp extends Thread {

	String peticion;

	public PeticionHttp(String msg) {
		super(msg);
		setMensaje(msg);
		this.setDaemon(true);
	}

	public void run() {
		try {
			/*if(Main.getInstance().pet){
				System.out.println("Peticion desechada: " + Main.getInstance().pet);
				return;
			}
			else{
			Main.getInstance().pet=true;*/
			RobotData.getInstance().sem.acquire();
			URL peticion = new URL(this.peticion);
			peticion.openStream();
			RobotData.getInstance().sem.release();
			//Main.getInstance().pet=false;
			System.out.println("Peticion " + this.peticion + " enviada correctamente");
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void setMensaje(String msj) {
		this.peticion = msj;
	}
}
