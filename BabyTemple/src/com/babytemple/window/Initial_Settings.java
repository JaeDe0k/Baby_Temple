package com.babytemple.window;

public class Initial_Settings {
	private String serverAddress;
	private int TCPport;
	private int UDPport;
	private String dbURL;
	private String dbID;
	private String dbPassword;
	
	public Initial_Settings() {
		this.serverAddress = "localhost"; //네트워크 통신 서버
		this.TCPport = 5001; // TCP 포트 ( 회원 정보 및 ROOM 관리 ) 
		this.UDPport = 5002; // UDP 포트 ( 게임 실시간 네트워크 관리 )
		this.dbURL = "jdbc:mysql://localhost:3306/BT?"; // DB주소
		this.dbID = "root"; // DB 아이디
		this.dbPassword = "root"; // DB 비밀번호
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