package serverNetwork;

public class Initial_Settings {
	private String serverAddress;
	private int TCPport;
	private int UDPport;
	
	public Initial_Settings() {
		this.serverAddress = "localhost"; //��Ʈ��ũ ��� ���� �ּ� �� ������
		this.TCPport = 5001; // TCP ��Ʈ ( ȸ�� ���� �� ROOM ���� ) 
		this.UDPport = 5002; // UDP ��Ʈ ( ���� �ǽð� ��Ʈ��ũ ���� )
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
