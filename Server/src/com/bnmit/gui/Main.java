
package com.bnmit.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.bnmit.dao.ServerDAO;
import com.bnmit.ecc.core.ECC;
import com.bnmit.ecc.core.EllipticCurve;
import com.bnmit.ecc.core.KeyPair;
import com.bnmit.otp.LamportOTP;
import com.bnmit.server.Server;

public class Main extends JFrame {

	public ServerSocket serverSocket;
	public Server server;
	public JPanel contentPane;
	public JTextField un_login1;
	public JTextField un_reg;
	public JPanel registerPanel = new JPanel();
	public JPanel loginPanel = new JPanel();
	public ServerDAO serverdao = new ServerDAO();
	public JTextField un_login;
	public JPasswordField passwordField;
	public JPasswordField pwd_login;
	public JPasswordField pwd_login1;
	public JLabel welcomemsg = new JLabel("You haven't logged in");
	public JPanel emptyPanel1 = new JPanel();
	public JPanel emptyPanel = new JPanel();
	public JPanel registerSuccess = new JPanel();
	public JPanel registrationFailed = new JPanel();
	public JPanel menuPanel = new JPanel();
	public JPanel homePanel = new JPanel();
	public JPanel welcomemsgpanel = new JPanel();
	public JLabel loginerror1 = new JLabel("");
	public JPanel rightPanel = new JPanel();
	public JPanel serverStartupPanel = new JPanel();
	public JTextField serverPort;
	public JButton serverstart = new JButton("START SERVER");
	public Main frame;
	public JLabel startedservertext = new JLabel("");
	public JPanel communicationPanel = new JPanel();
	public JPanel ECCKeyspanel = new JPanel();
	public JLabel connectionStatusText = new JLabel("");
	public JLabel clientDetailsText = new JLabel("");
	public JTextField currentOTP;
	public JTextField inverseOTP;
	public JTextField previousOTP;
	public JTextArea currentMessage = new JTextArea();
	public JTextArea replyMessage = new JTextArea();

	public JTextArea previousMessage = new JTextArea();
	public JButton btnDecrypt = new JButton("Decrypt");
	public JButton btnReply = new JButton("Reply");

	public BigInteger b1 = null;
	public BigInteger b2 = null;
	public int factor;

	public ECC ecc = null;
	public LamportOTP lamportOTP = null;

	public KeyPair keyPair = null;
	public JTextArea xPub = new JTextArea();
	public JTextArea yPub = new JTextArea();
	public JTextArea Priv = new JTextArea();
	public JPanel otpsPanel = new JPanel();

	public Map<String, String> otpMap = new LinkedHashMap<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		frame = this;

		setTitle("B.N.M.I.T - Server");
		setBackground(Color.BLACK);
		setForeground(Color.GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 874, 626);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 235, 205));
		contentPane.setForeground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblIntegratingLamportsOtp = new JLabel(
				"Integrating Lamport's One Time Password Authentication Scheme with Elliptic Curve Cryptography ");
		lblIntegratingLamportsOtp.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblIntegratingLamportsOtp.setBounds(152, 11, 1117, 47);
		contentPane.add(lblIntegratingLamportsOtp);

		JLabel lblAshokKumarLearning = new JLabel("B.N.M. Institute of Technology");
		lblAshokKumarLearning.setFont(new Font("EucrosiaUPC", Font.BOLD, 36));
		lblAshokKumarLearning.setBounds(511, 65, 398, 41);
		contentPane.add(lblAshokKumarLearning);

		JLabel lblFinalYearProject = new JLabel("Final year project work 2016");
		lblFinalYearProject.setFont(new Font("Verdana", Font.BOLD, 14));
		lblFinalYearProject.setBounds(566, 111, 234, 33);
		contentPane.add(lblFinalYearProject);

		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		leftPanel.setBackground(new Color(255, 255, 255));
		leftPanel.setBounds(71, 247, 466, 266);
		contentPane.add(leftPanel);
		leftPanel.setLayout(new CardLayout(0, 0));

		emptyPanel1.setBackground(new Color(255, 255, 255));
		leftPanel.add(emptyPanel1, "name_21489220140447");
		emptyPanel1.setLayout(null);

		JLabel lblAlreadyRegistered = new JLabel("Already Registered? ");
		lblAlreadyRegistered.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblAlreadyRegistered.setBounds(22, 27, 151, 39);
		emptyPanel1.add(lblAlreadyRegistered);

		JButton btnLoginHere = new JButton("Login here");
		btnLoginHere.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnLoginHere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emptyPanel1.setVisible(false);
				loginPanel.setVisible(true);
			}
		});
		btnLoginHere.setBounds(25, 81, 148, 39);
		emptyPanel1.add(btnLoginHere);
		loginPanel.setBackground(new Color(255, 255, 255));

		leftPanel.add(loginPanel, "name_20956230706807");
		loginPanel.setLayout(null);
		JLabel loginerror = new JLabel("");

		JButton btnNewButton = new JButton("LOGIN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String un = un_login1.getText();
					String pwd = pwd_login1.getText();
					if (un.trim().equals("") || pwd.trim().equals("")) {
						loginerror.setText("Invalid Credentials");

					} else {
						if (serverdao.isValidUser(un, pwd)) {
							welcomemsg.setText("Welcome " + un);
							hideAllLeftPanels();
							menuPanel.setVisible(true);
							hideAllRightPanels();
							homePanel.setVisible(true);

						} else {
							loginerror.setText("Invalid Credentials");
						}
					}
				} catch (Exception e2) {
					loginerror.setText("Something went wrong. Please try again later.");
				}
			}
		});
		btnNewButton.setFont(new Font("Verdana", Font.BOLD, 14));
		btnNewButton.setBounds(10, 207, 219, 39);
		loginPanel.add(btnNewButton);

		un_login1 = new JTextField();
		un_login1.setBounds(10, 47, 219, 39);
		loginPanel.add(un_login1);
		un_login1.setColumns(10);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(10, 28, 79, 14);
		loginPanel.add(lblNewLabel);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 108, 79, 14);
		loginPanel.add(lblPassword);

		pwd_login1 = new JPasswordField();
		pwd_login1.setBounds(10, 133, 219, 39);
		loginPanel.add(pwd_login1);

		loginerror.setForeground(new Color(153, 102, 0));
		loginerror.setBounds(10, 182, 219, 14);
		loginPanel.add(loginerror);

		menuPanel.setBackground(new Color(255, 255, 255));
		leftPanel.add(menuPanel, "name_23793413983383");
		menuPanel.setLayout(null);

		JButton btnNewButton_1 = new JButton("SERVER START UP");
		btnNewButton_1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideAllRightPanels();
				serverStartupPanel.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(53, 22, 346, 50);
		menuPanel.add(btnNewButton_1);

		JButton btnOtps = new JButton("COMMUNICATION");
		btnOtps.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnOtps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideAllRightPanels();
				communicationPanel.setVisible(true);
			}
		});
		btnOtps.setBounds(53, 83, 346, 50);
		menuPanel.add(btnOtps);

		JButton btnECCKeys = new JButton("ECC KEYS");
		btnECCKeys.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnECCKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideAllRightPanels();
				ECCKeyspanel.setVisible(true);

			}
		});
		btnECCKeys.setBounds(53, 144, 346, 50);
		menuPanel.add(btnECCKeys);

		JButton otpBtn = new JButton("OTPs");
		otpBtn.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		otpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				hideAllRightPanels();
				otpsPanel.setVisible(true);

			}
		});
		otpBtn.setBounds(53, 205, 346, 50);
		menuPanel.add(otpBtn);

		rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.setBounds(631, 247, 583, 404);
		contentPane.add(rightPanel);
		rightPanel.setLayout(new CardLayout(0, 0));

		emptyPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(emptyPanel, "name_21305060089118");
		emptyPanel.setLayout(null);

		JLabel lblNewUser = new JLabel("New User?");
		lblNewUser.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewUser.setBounds(30, 24, 137, 27);
		emptyPanel.add(lblNewUser);

		JButton btnRegister_1 = new JButton("Register here");
		btnRegister_1.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		btnRegister_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				emptyPanel.setVisible(false);
				registerPanel.setVisible(true);
			}
		});
		btnRegister_1.setBounds(30, 68, 158, 48);
		emptyPanel.add(btnRegister_1);
		otpsPanel.setBackground(Color.LIGHT_GRAY);

		rightPanel.add(otpsPanel, "name_215129052982470");
		otpsPanel.setLayout(null);

		JLabel lblListOfAll = new JLabel("List of all OTPs and Inverse OTPs received");
		lblListOfAll.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblListOfAll.setBounds(32, 26, 341, 24);
		otpsPanel.add(lblListOfAll);

		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(31, 51, 354, 254);
		otpsPanel.add(scrollPane_6);

		JTextArea otpsList = new JTextArea();
		otpsList.setLineWrap(true);
		scrollPane_6.setViewportView(otpsList);
		registerPanel.setBackground(new Color(255, 255, 255));

		rightPanel.add(registerPanel, "name_21216370466605");
		registerPanel.setLayout(null);

		JLabel label = new JLabel("Username");
		label.setBounds(104, 22, 79, 14);
		registerPanel.add(label);

		un_reg = new JTextField();
		un_reg.setColumns(10);
		un_reg.setBounds(104, 47, 219, 39);
		registerPanel.add(un_reg);

		JLabel label_1 = new JLabel("Password");
		label_1.setBounds(104, 103, 79, 14);
		registerPanel.add(label_1);

		JButton btnRegister = new JButton("REGISTER");
		JLabel regerrormsg = new JLabel("");
		JLabel validationfail = new JLabel("");
		JPasswordField pwd_reg = new JPasswordField();

		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String un = un_reg.getText();
				String pwd = pwd_reg.getText();
				if (un.trim().equals("") || pwd.trim().equals("")) {
					validationfail.setText("Both are mandatory fields");
				} else {
					try {
						serverdao.register(un, pwd);
						registerPanel.setVisible(false);
						registerSuccess.setVisible(true);

					} catch (Exception e1) {
						e1.printStackTrace();
						regerrormsg.setText(e1.getMessage());
						regerrormsg.setVisible(true);
						registrationFailed.setVisible(true);

						registerPanel.setVisible(false);

					}
				}

			}
		});
		btnRegister.setFont(new Font("Verdana", Font.BOLD, 14));
		btnRegister.setBounds(104, 209, 219, 39);
		registerPanel.add(btnRegister);

		validationfail.setForeground(new Color(51, 102, 0));
		validationfail.setBounds(104, 184, 219, 14);
		registerPanel.add(validationfail);

		pwd_reg.setBounds(104, 128, 219, 39);
		registerPanel.add(pwd_reg);
		registrationFailed.setBackground(Color.WHITE);
		rightPanel.add(registrationFailed, "name_22428719623088");
		registrationFailed.setLayout(null);

		JLabel lblSomethingWentWrong = new JLabel("Something went wrong while registering the user");
		lblSomethingWentWrong.setBounds(24, 29, 331, 14);
		registrationFailed.add(lblSomethingWentWrong);

		JButton btnTryAgain = new JButton("TRY AGAIN");
		btnTryAgain.setBounds(24, 224, 219, 31);
		btnTryAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				registrationFailed.setVisible(false);
				registerPanel.setVisible(true);
			}
		});
		btnTryAgain.setFont(new Font("Verdana", Font.BOLD, 14));
		registrationFailed.add(btnTryAgain);

		regerrormsg.setBounds(24, 73, 331, 124);
		registrationFailed.add(regerrormsg);

		registerSuccess.setBackground(new Color(255, 255, 255));
		rightPanel.add(registerSuccess, "name_22197669727934");
		registerSuccess.setLayout(null);

		JLabel lblRegistrationSuccessful = new JLabel("Registration Successful.");
		lblRegistrationSuccessful.setBounds(24, 29, 226, 14);
		registerSuccess.add(lblRegistrationSuccessful);

		JLabel lblPleaseLoginHere = new JLabel("Please login here");
		lblPleaseLoginHere.setBounds(24, 54, 124, 14);
		registerSuccess.add(lblPleaseLoginHere);

		un_login = new JTextField();
		un_login.setColumns(10);
		un_login.setBounds(24, 98, 219, 31);
		registerSuccess.add(un_login);

		JLabel label_2 = new JLabel("Username");
		label_2.setBounds(24, 79, 79, 14);
		registerSuccess.add(label_2);

		JLabel label_3 = new JLabel("Password");
		label_3.setBounds(24, 140, 79, 14);
		registerSuccess.add(label_3);

		JButton button = new JButton("LOGIN");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String un = un_login.getText();
					String pwd = pwd_login.getText();
					if (un.trim().equals("") || pwd.trim().equals("")) {
						loginerror1.setText("Invalid Credentials");

					} else {
						if (serverdao.isValidUser(un, pwd)) {
							welcomemsg.setText("Welcome " + un);
							hideAllLeftPanels();
							menuPanel.setVisible(true);
							hideAllRightPanels();
							homePanel.setVisible(true);

						} else {
							loginerror1.setText("Invalid Credentials");
						}
					}
				} catch (Exception e2) {
					loginerror1.setText("Something went wrong. Please try again later.");
				}
			}
		});
		button.setFont(new Font("Verdana", Font.BOLD, 14));
		button.setBounds(24, 234, 219, 31);
		registerSuccess.add(button);

		pwd_login = new JPasswordField();
		pwd_login.setBounds(24, 165, 219, 31);
		registerSuccess.add(pwd_login);

		loginerror1.setForeground(new Color(153, 102, 0));
		loginerror1.setBounds(24, 209, 219, 14);
		registerSuccess.add(loginerror1);

		homePanel.setBackground(Color.LIGHT_GRAY);
		rightPanel.add(homePanel, "name_24373501569303");
		ECCKeyspanel.setBackground(Color.LIGHT_GRAY);

		rightPanel.add(ECCKeyspanel, "name_183419502380653");
		ECCKeyspanel.setLayout(null);

		JLabel lblPublicKey = new JLabel("Public Key");
		lblPublicKey.setBounds(20, 38, 162, 14);
		ECCKeyspanel.add(lblPublicKey);

		JLabel lblPrivateKey = new JLabel("Private Key");
		lblPrivateKey.setBounds(20, 193, 111, 14);
		ECCKeyspanel.add(lblPrivateKey);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(20, 218, 346, 47);
		ECCKeyspanel.add(scrollPane_5);
		Priv.setLineWrap(true);
		Priv.setEditable(false);
		scrollPane_5.setViewportView(Priv);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(20, 121, 346, 47);
		ECCKeyspanel.add(scrollPane_4);
		yPub.setLineWrap(true);
		yPub.setEditable(false);
		scrollPane_4.setViewportView(yPub);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(18, 63, 348, 47);
		ECCKeyspanel.add(scrollPane_3);
		xPub.setLineWrap(true);
		xPub.setEditable(false);
		scrollPane_3.setViewportView(xPub);
		communicationPanel.setBackground(Color.LIGHT_GRAY);

		rightPanel.add(communicationPanel, "name_181955284339878");
		communicationPanel.setLayout(null);

		JLabel lblCurrentMessageRecevied = new JLabel("Current Message recevied");
		lblCurrentMessageRecevied.setBounds(27, 36, 218, 14);
		communicationPanel.add(lblCurrentMessageRecevied);

		JLabel lblPreviousMessageRecevied = new JLabel("Previous Message recevied");
		lblPreviousMessageRecevied.setBounds(27, 249, 218, 14);
		communicationPanel.add(lblPreviousMessageRecevied);

		JLabel lblOtp = new JLabel("OTP:");
		lblOtp.setBounds(28, 120, 46, 14);
		communicationPanel.add(lblOtp);

		JLabel label_4 = new JLabel("OTP:");
		label_4.setBounds(27, 316, 46, 14);
		communicationPanel.add(label_4);
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					byte[] ptBytes = ECC.decrypt(currentMessage.getText().getBytes("ISO-8859-1"),
							frame.keyPair.getPrivateKey());

					String msg = new String(ptBytes);

					StringTokenizer st = new StringTokenizer(msg, "##$$");
					String newotp = st.nextToken();
					currentOTP.setText(newotp);
					if (previousOTP.getText().trim().length() > 0) {
						inverseOTP.setText(String.valueOf(lamportOTP.inverserfunction(Double.parseDouble(newotp))));
						otpMap.put(newotp, String.valueOf(lamportOTP.inverserfunction(Double.parseDouble(newotp))));
					} else {
						otpMap.put(newotp, " - ");
					}
					currentMessage.setText(st.nextToken());

					String data = "Sl. No \t OTP \t\t Inverse OTP";
					Iterator<String> it = otpMap.keySet().iterator();
					int i = 0;
					while (it.hasNext()) {
						i++;
						String otp = it.next();
						String inverseotp = otpMap.get(otp);
						data += "\n" + i + "\t" + otp + "\t\t" + inverseotp;
					}
					otpsList.setText(data);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnDecrypt.setEnabled(false);
		btnDecrypt.setBounds(302, 125, 89, 23);
		communicationPanel.add(btnDecrypt);

		JLabel lblLamportImportFx = new JLabel("Lamport Inverse f(x):");
		lblLamportImportFx.setBounds(28, 145, 116, 14);
		communicationPanel.add(lblLamportImportFx);
		btnReply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String reply = replyMessage.getText();
					if (b1 == null || b2 == null) {
						EllipticCurve ec = EllipticCurve.NIST_P_192;
						Random rnd = new Random(System.currentTimeMillis());
						b1 = new BigInteger(ec.getP().bitLength(), rnd);
						b2 = new BigInteger(ec.getP().bitLength(), rnd);
						Random r = new Random();
						int Low = 10;
						int High = 100;
						factor = r.nextInt(High - Low) + Low;
						reply = reply + "##$$" + b1 + "##$$" + b2 + "##$$" + factor;
						ecc = new ECC();
						keyPair = ecc.generateKeyPair2(ec, b1, b2);
						lamportOTP = new LamportOTP(factor);

						xPub.setText(String.valueOf(keyPair.getPublicKey().getKey().x));
						yPub.setText(String.valueOf(keyPair.getPublicKey().getKey().y));

						Priv.setText(String.valueOf(keyPair.getPrivateKey().getKey()));

						server.writeData(reply);

					} else {
						if (ecc != null) {

							byte[] ctBytes = ecc.encrypt(reply.getBytes(), keyPair.getPublicKey());

							String ct = new String(ctBytes, "ISO-8859-1");
							server.writeData(ct);
						} else {

							server.writeData(reply);
						}

					}
					replyMessage.setEnabled(false);
					btnReply.setEnabled(false);
					String current = currentMessage.getText();
					currentMessage.setText("");
					previousMessage.setText(current);
					previousOTP.setText(currentOTP.getText());
					currentOTP.setText("");
					inverseOTP.setText("");
					replyMessage.setText("");
					btnDecrypt.setEnabled(false);
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});

		btnReply.setEnabled(false);
		btnReply.setBounds(302, 215, 89, 23);
		communicationPanel.add(btnReply);

		JSeparator separator = new JSeparator();
		separator.setBounds(66, 219, 1, 2);
		communicationPanel.add(separator);
		connectionStatusText.setFont(new Font("Tahoma", Font.BOLD, 12));

		connectionStatusText.setForeground(Color.BLACK);
		connectionStatusText.setBackground(Color.LIGHT_GRAY);
		connectionStatusText.setBounds(27, 11, 370, 24);
		communicationPanel.add(connectionStatusText);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 53, 361, 56);
		communicationPanel.add(scrollPane);
		currentMessage.setEditable(false);
		currentMessage.setLineWrap(true);

		scrollPane.setViewportView(currentMessage);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(27, 267, 364, 44);
		communicationPanel.add(scrollPane_2);
		previousMessage.setEditable(false);
		previousMessage.setLineWrap(true);

		scrollPane_2.setViewportView(previousMessage);

		currentOTP = new JTextField();
		currentOTP.setEditable(false);
		currentOTP.setBounds(138, 117, 107, 20);
		communicationPanel.add(currentOTP);
		currentOTP.setColumns(10);

		inverseOTP = new JTextField();
		inverseOTP.setEditable(false);
		inverseOTP.setColumns(10);
		inverseOTP.setBounds(138, 142, 107, 20);
		communicationPanel.add(inverseOTP);

		previousOTP = new JTextField();
		previousOTP.setEditable(false);
		previousOTP.setColumns(10);
		previousOTP.setBounds(138, 313, 107, 20);
		communicationPanel.add(previousOTP);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(138, 170, 253, 34);
		communicationPanel.add(scrollPane_1);
		replyMessage.setEnabled(false);
		replyMessage.setLineWrap(true);

		scrollPane_1.setViewportView(replyMessage);
		serverStartupPanel.setBackground(Color.LIGHT_GRAY);

		rightPanel.add(serverStartupPanel, "name_18584900329956");
		serverStartupPanel.setLayout(null);

		JLabel lblPortNumber = new JLabel("Port Number: ");
		lblPortNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPortNumber.setBounds(39, 41, 98, 26);
		serverStartupPanel.add(lblPortNumber);

		serverPort = new JTextField();
		serverPort.setBounds(160, 43, 170, 24);
		serverStartupPanel.add(serverPort);
		serverPort.setColumns(10);

		serverstart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					startedservertext.setText("Trying to listen to " + serverPort.getText() + " port");
					server = new Server(frame, Integer.parseInt(serverPort.getText()));
					serverPort.setEnabled(false);
					serverstart.setEnabled(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					startedservertext.setText("Error while server start up : " + e.getMessage());

				}
			}
		});
		serverstart.setBounds(157, 100, 173, 34);
		serverStartupPanel.add(serverstart);

		startedservertext.setBounds(39, 159, 355, 23);
		serverStartupPanel.add(startedservertext);

		clientDetailsText.setFont(new Font("Tahoma", Font.BOLD, 12));
		clientDetailsText.setBounds(39, 222, 332, 68);
		serverStartupPanel.add(clientDetailsText);

		welcomemsgpanel.setBackground(new Color(255, 255, 255));
		welcomemsgpanel.setBounds(1002, 179, 212, 41);
		contentPane.add(welcomemsgpanel);
		welcomemsgpanel.setLayout(null);

		welcomemsg.setBounds(10, 11, 203, 14);
		welcomemsgpanel.add(welcomemsg);

		JLabel lblServerTool = new JLabel("Server Tool");
		lblServerTool.setForeground(new Color(0, 51, 204));
		lblServerTool.setFont(new Font("Times New Roman", Font.BOLD, 28));
		lblServerTool.setBounds(71, 174, 153, 25);
		contentPane.add(lblServerTool);
		
		JLabel lblecc1 = new JLabel("<html>\r\nPUSHPALATHA S <br/> 1BG13CS415 <br/><br/>\r\nSHREE LAVANYA P <br/> 1BG13CS419\r\n</html>");
		lblecc1.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblecc1.setBounds(71, 568, 128, 98);
		contentPane.add(lblecc1);
		
		JLabel label_5 = new JLabel("<html>\r\nSOWMYA S <br/> 1BG13CS421 <br/><br/>\r\nYASHASWINI N <br/> 1BG13CS423\r\n</html>");
		label_5.setFont(new Font("Times New Roman", Font.BOLD, 14));
		label_5.setBounds(264, 558, 138, 118);
		contentPane.add(label_5);
		
		JLabel lblDevelopers = new JLabel("Developers");
		lblDevelopers.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblDevelopers.setBounds(71, 524, 128, 33);
		contentPane.add(lblDevelopers);
	}

	public void hideAllLeftPanels() {
		loginPanel.setVisible(false);
		emptyPanel1.setVisible(false);
		menuPanel.setVisible(false);
	}

	public void hideAllRightPanels() {
		registerPanel.setVisible(false);
		emptyPanel.setVisible(false);
		registrationFailed.setVisible(false);
		registerSuccess.setVisible(false);
		homePanel.setVisible(false);
		serverStartupPanel.setVisible(false);
		communicationPanel.setVisible(false);
		ECCKeyspanel.setVisible(false);
		otpsPanel.setVisible(false);
	}
}
