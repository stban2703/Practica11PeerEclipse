package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

import processing.core.PApplet;

public class Main extends PApplet {

	public static void main(String[] args) {
		PApplet.main("main.Main");

	}

	private DatagramSocket socket;
	private InetAddress android;
	private int[][] matriz;
	private ArrayList<Jugada> jugadas;
	private boolean finJuego;
	private boolean terminaTurno;
	private String turno;

	public void settings() {
		size(900, 700);
	}

	public void setup() {
		matriz = new int[3][3];
		jugadas = new ArrayList<Jugada>();
		finJuego = false;
		terminaTurno = false;
		turno = "Es tu turno, eres la X";

		// 1. Hilo de recepcion
		new Thread(() -> {
			try {
				android = InetAddress.getByName("192.168.0.2");
				socket = new DatagramSocket(5000);

				while (true) {
					// Si es un Datagram de recepcion, solo le ponemos dos parametros
					byte[] buffer = new byte[100]; // -> Entero que puede representar 256, ASCII, 8 bit, bit -> -0 o 1
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

					// Esta linea, se queda esperando por paquetes
					socket.receive(packet);

					// El mensaje queda en el paquete, despues de la recepcion
					String json = new String(packet.getData()).trim();
					// System.out.println(json);

					// Recibir jugada
					if (json.startsWith("{") && terminaTurno && !finJuego) {
						// Recibir jugada
						Gson gson = new Gson();
						Jugada jugada = gson.fromJson(json, Jugada.class);
						jugadas.add(jugada);

						turno = "Es tu turno, eres la X";

						int fila = (int) jugada.getPosX();
						int col = (int) jugada.getPosY();

						matriz[fila][col] = 2;
						terminaTurno = false;
					}

				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}).start();

	}

	public void draw() {

		background(255);
		textAlign(CENTER, TOP);
		fill(0);
		textSize(30);
		text(turno, width / 2, 0);

		// Pintar tablero
		for (int fila = 0; fila < 3; fila++) {
			for (int col = 0; col < 3; col++) {
				fill(0, 50, 120);
				rect(col * 200 + 150, fila * 200 + 50, 180, 180);
			}
		}

		// Pintar jugada
		for (int i = 0; i < jugadas.size(); i++) {
			float x = jugadas.get(i).getPosX();
			float y = jugadas.get(i).getPosY();
			String simbolo = jugadas.get(i).getSimbolo();

			textAlign(CENTER, TOP);
			fill(255);
			textSize(200);

			text(simbolo, x * 200 + 240, y * 200 + 25);

		}

		// Victoria
		if ((matriz[0][0] == 1 && matriz[0][1] == 1 && matriz[0][2] == 1)
				|| (matriz[1][0] == 1 && matriz[1][1] == 1 && matriz[1][2] == 1)
				|| (matriz[2][0] == 1 && matriz[2][1] == 1 && matriz[2][2] == 1)
				|| (matriz[0][0] == 1 && matriz[1][0] == 1 && matriz[2][0] == 1)
				|| (matriz[0][1] == 1 && matriz[1][1] == 1 && matriz[2][1] == 1)
				|| (matriz[0][2] == 1 && matriz[1][2] == 1 && matriz[2][2] == 1)
				|| (matriz[0][0] == 1 && matriz[1][1] == 1 && matriz[2][2] == 1)
				|| (matriz[0][2] == 1 && matriz[1][1] == 1 && matriz[2][0] == 1)) {
			turno = "Ganaste";
			finJuego = true;

			// Derrota
		} else if ((matriz[0][0] == 2 && matriz[0][1] == 2 && matriz[0][2] == 2)
				|| (matriz[1][0] == 2 && matriz[1][1] == 2 && matriz[1][2] == 2)
				|| (matriz[2][0] == 2 && matriz[2][1] == 2 && matriz[2][2] == 2)
				|| (matriz[0][0] == 2 && matriz[1][0] == 2 && matriz[2][0] == 2)
				|| (matriz[0][1] == 2 && matriz[1][1] == 2 && matriz[2][1] == 2)
				|| (matriz[0][2] == 2 && matriz[1][2] == 2 && matriz[2][2] == 2)
				|| (matriz[0][0] == 2 && matriz[1][1] == 2 && matriz[2][2] == 2)
				|| (matriz[0][2] == 2 && matriz[1][1] == 2 && matriz[2][0] == 2)) {
			turno = "Perdiste";
			finJuego = true;
			
			// Empate
		} else if ((matriz[0][0] != 0 && matriz[0][1] != 0 && matriz[0][2] != 0)
				&& (matriz[1][0] != 0 && matriz[1][1] != 0 && matriz[1][2] != 0)
				&& (matriz[2][0] != 0 && matriz[2][1] != 0 && matriz[2][2] != 0)
				&& (matriz[0][0] != 0 && matriz[1][0] != 0 && matriz[2][0] != 0)
				&& (matriz[0][1] != 0 && matriz[1][1] != 0 && matriz[2][1] != 0)
				&& (matriz[0][2] != 0 && matriz[1][2] != 0 && matriz[2][2] != 0)
				&& (matriz[0][0] != 0 && matriz[1][1] != 0 && matriz[2][2] != 0)
				&& (matriz[0][2] != 0 && matriz[1][1] != 0 && matriz[2][0] != 0)) {
			turno = "Empate";
			finJuego = true;
		}

	}

	public void mousePressed() {
		// Enviar jugada
		for (int col = 0; col < 3; col++) {
			for (int fila = 0; fila < 3; fila++) {

				float posX = fila * 200 + 150;
				float posY = col * 200 + 50;
				float ancho = 180;

				// Zona sensible
				if (mouseX > posX && mouseX < posX + ancho && mouseY > posY && mouseY < posY + ancho && !terminaTurno
						&& matriz[fila][col] == 0 && !finJuego) {
					Jugada jugada = new Jugada(fila, col, "X");
					jugadas.add(jugada);
					turno = "Esperando al jugador 2...";
					Gson gson = new Gson();
					String json = gson.toJson(jugada);
					enviarMensaje(json);
					matriz[fila][col] = 1;
					terminaTurno = true;
				}

			}
		}
	}

	public void enviarMensaje(String mensaje) {
		// 2. Hilo de envio
		new Thread(() -> {
			try {
				byte[] buffer = mensaje.getBytes();
				// El paquete tiene 4 parametros
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, android, 5000);
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

}
