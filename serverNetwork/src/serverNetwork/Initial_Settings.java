package serverNetwork;

public class Initial_Settings {
	private String serverAddress;
	private int TCPport;
	private int UDPport;
	
	public Initial_Settings() {
		this.serverAddress = "localhost"; //네트워크 통신 서버 주소 및 도메인
		this.TCPport = 5001; // TCP 포트 ( 회원 정보 및 ROOM 관리 ) 
		this.UDPport = 5002; // UDP 포트 ( 게임 실시간 네트워크 관리 )
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
