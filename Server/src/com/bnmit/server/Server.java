package com.bnmit.server;

import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.bnmit.ecc.core.ECC;
import com.bnmit.ecc.core.KeyPair;
import com.bnmit.gui.Main;

import java.io.*;
import java.math.BigInteger;

public class Server implements Runnable {
	public ServerSocket serverSocket;
	Socket server;
	DataInputStream in;
	DataOutputStream out;
	Thread t;
	int port;
	Main frame;

	public Server(Main frame, int port) throws IOException {
		this.frame = frame;
		this.port = port;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000000);
		t = new Thread(this);
		t.start();
	}

	public void writeData(String data) throws IOException {
		out.writeUTF(data);
	}

	public String readData() throws IOException {
		return in.readUTF();
	}

	// public static void main(String[] args) throws IOException {
	// Scanner sc = new Scanner(System.in);
	// System.out.print("Enter the port number to listen to : ");
	// int port = Integer.parseInt(sc.nextLine());
	// System.out.println("Listening to .. " + port);
	// Server s = new Server(port);
	// s.serverSocket.getLocalPort();
	// System.out.println("Connected to " + s.server.getRemoteSocketAddress());
	// String msgReceived = "";
	// String msgTobeSent = "";
	// while (!msgReceived.equals("STOP")) {
	// msgReceived = s.readData();
	// System.out.println("Received a msg from client : " + msgReceived);
	// if (!msgReceived.equals("STOP")) {
	// System.out.print("Enter server response : ");
	// msgTobeSent = sc.nextLine();
	// s.writeData(msgTobeSent);
	// }
	// }
	// System.out.println("Client requested for stopping the communication");
	// s.writeData("BYE");
	//
	// s.server.close();
	//
	// }

	@Override
	public void run() {
		try {
			System.out.println("Listening to .. " + port);
			frame.startedservertext.setText("Server started on " + port);
			frame.connectionStatusText.setText("You are not connected to any of the client yet");

			server = serverSocket.accept();
			in = new DataInputStream(server.getInputStream());
			out = new DataOutputStream(server.getOutputStream());
			System.out.println("Connected to " + server.getRemoteSocketAddress());
			frame.connectionStatusText.setText("Communicating with " + server.getRemoteSocketAddress());
			frame.clientDetailsText.setText("<html>Successfully established a communication channel <br/>with "
					+ server.getRemoteSocketAddress() + "</html>");

			String msgReceived = "";
			while (!msgReceived.equals("STOP")) {
				msgReceived = readData();
				if (!msgReceived.equals("STOP")) {
					if (frame.ecc != null && frame.b1 != null && frame.b2 != null) {
						frame.btnDecrypt.setEnabled(true);

						// byte[] ptBytes =
						// ECC.decrypt(msgReceived.getBytes("ISO-8859-1"),
						// frame.keyPair.getPrivateKey());
						// msgReceived = new String(ptBytes);

					}
					
					frame.currentMessage.setText(msgReceived);
					frame.replyMessage.setEnabled(true);
					frame.btnReply.setEnabled(true);

					// msgTobeSent = sc.nextLine();
					// s.writeData(msgTobeSent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			frame.startedservertext.setText("<html>Error while starting server on port. <br/>" + e + "</html>");
			frame.serverPort.setEnabled(true);
			frame.serverstart.setEnabled(true);
		}

	}
}
