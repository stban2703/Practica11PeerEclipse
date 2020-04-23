package main;

public class Jugada {
	
	private float posX;
	private float posY;
	private String simbolo;
	
	public Jugada(float posX, float posY, String simbolo) {
		this.posX = posX;
		this.posY = posY;
		this.simbolo = simbolo;
	}

	public Jugada() {

	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

}
