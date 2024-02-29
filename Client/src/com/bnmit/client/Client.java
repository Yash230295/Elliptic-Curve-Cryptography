package com.bnmit.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import com.bnmit.ecc.core.ECC;
import com.bnmit.ecc.core.EllipticCurve;
import com.bnmit.gui.Main;
import com.bnmit.otp.LamportOTP;

public class Client implements Runnable {
	DataOutputStream out = null;
	DataInputStream in = null;
	Socket client = null;
	String serverName;
	int port;
	Thread t;
	Main frame;

	public Client(Main frame, String serverName, int port) throws UnknownHostException, IOException {
		this.serverName = serverName;
		this.port = port;
		this.frame = frame;
		t = new Thread(this);
		t.start();
	}

	// public static void main(String[] args) {
	// try {
	// Scanner sc = new Scanner(System.in);
	// System.out.print("Enter the server host name: ");
	// String host = sc.nextLine();
	// System.out.println("Enter the port number : ");
	// int port = Integer.parseInt(sc.nextLine());
	// Client c = new Client(host, port);
	// System.out.println("Connected to .." + c.serverName + " on " + c.port + "
	// port");
	// String msgTosend = "";
	// String msgReceived = "";
	// while (!msgTosend.equals("STOP")) {
	// System.out.print("Enter your message: ");
	// msgTosend = sc.nextLine();
	// c.writeData(msgTosend);
	// msgReceived = c.readData();
	// System.out.println("Server says: " + msgReceived);
	// }
	// c.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	void establishConnection(String serverName, int port) throws UnknownHostException, IOException {
		this.serverName = serverName;
		this.port = port;
		client = new Socket(serverName, port);
		OutputStream outToServer = client.getOutputStream();
		out = new DataOutputStream(outToServer);

		InputStream inFromServer = client.getInputStream();
		in = new DataInputStream(inFromServer);

	}

	void close() throws IOException {
		client.close();
	}

	public void writeData(String data) throws Exception {
		out.writeUTF(data);
	}

	public String readData() throws Exception {
		return in.readUTF();
	}

	@Override
	public void run() {
		try {
			client = new Socket(serverName, port);
			OutputStream outToServer = client.getOutputStream();
			out = new DataOutputStream(outToServer);

			InputStream inFromServer = client.getInputStream();
			in = new DataInputStream(inFromServer);
			frame.frm.clientstartmsg.setText("Connected to .." + serverName + " on " + port + " port");
			frame.frm.Serverstatus.setText("Communicating with " + serverName + ": " + port);
			System.out.println("Connected to .." + serverName + " on " + port + " port");

			frame.frm.currentMessage.setEnabled(true);
			frame.frm.btnSendMessage.setEnabled(true);

			while (true) {

				String message = readData();
				if (frame.frm.ecc != null) {
					frame.frm.btnDecrypt.setEnabled(true);
				}
				if (frame.frm.b1 == null || frame.frm.b2 == null) {
					System.out.println("Message .. " + message);
					StringTokenizer st = new StringTokenizer(message, "##$$");

					String m = st.nextToken();
					frame.frm.b1 = new BigInteger(st.nextToken());
					frame.frm.b2 = new BigInteger(st.nextToken());
					frame.frm.factor = Integer.parseInt(st.nextToken());
					EllipticCurve ec = EllipticCurve.NIST_P_192;
					frame.frm.ecc = new ECC();
					frame.frm.keyPair = frame.frm.ecc.generateKeyPair2(ec, frame.frm.b1, frame.frm.b2);
					frame.frm.lamportOTP = new LamportOTP(frame.frm.factor);
					message = m;

					frame.frm.xPub.setText(String.valueOf(frame.frm.keyPair.getPublicKey().getKey().x));
					frame.frm.yPub.setText(String.valueOf(frame.frm.keyPair.getPublicKey().getKey().y));

					frame.frm.Priv.setText(String.valueOf(frame.frm.keyPair.getPrivateKey().getKey()));

				}

				frame.frm.replyMessage.setText(message);
				frame.frm.currentMessage.setEnabled(true);
				frame.frm.btnSendMessage.setEnabled(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
			frame.frm.clientstartmsg
					.setText("<html>Error while connecting to " + serverName + ".<br/> " + e + "</html>");

			frame.frm.serverIP.setEnabled(true);
			frame.frm.serverPort.setEnabled(true);
			frame.frm.startButton.setEnabled(true);
		}
	}
}
