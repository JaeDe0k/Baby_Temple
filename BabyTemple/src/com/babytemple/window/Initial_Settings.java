package com.babytemple.window;

public class Initial_Settings {
	private String serverAddress;
	private int TCPport;
	private int UDPport;
	private String dbURL;
	private String dbID;
	private String dbPassword;
	
	public Initial_Settings() {
		this.serverAddress = "localhost"; //��Ʈ��ũ ��� ����
		this.TCPport = 5001; // TCP ��Ʈ ( ȸ�� ���� �� ROOM ���� ) 
		this.UDPport = 5002; // UDP ��Ʈ ( ���� �ǽð� ��Ʈ��ũ ���� )
		this.dbURL = "jdbc:mysql://localhost:3306/BT?"; // DB�ּ�
		this.dbID = "root"; // DB ���̵�
		this.dbPassword = "root"; // DB ��й�ȣ
	}
	
	public String getDbURL() {
		return dbURL;
	}

	public String getDbID() {
		return dbID;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getTCPport() {
		return TCPport;
	}

	public int getUDPport() {
		return UDPport;
	}
}