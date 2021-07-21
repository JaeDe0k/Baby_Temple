package serverNetwork;

import java.net.InetSocketAddress;

// 게임 대기실 플레이어 정보 및 방장 (2인)
public class roomInfo implements Comparable<roomInfo> {
	private String roomName;
	private String userID[] = {"P1","P2"};
	private InetSocketAddress p1Address;
	private InetSocketAddress p2Address;
	private int full = 1;

	public roomInfo(String roomName, String userID) {		
		this.roomName = roomName;
		this.userID[0] = userID;
	}

	public int compareTo(roomInfo s) {
		if (this.full < s.getFull()) {
			return -1;
		} else if (this.full > s.getFull()) {
			return 1;
		}
		return 0;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String[] getUserID() {
		return userID;
	}

	public void setUserID(String[] userID) {
		this.userID = userID;
	}
	
	public int getFull() {
		return full;
	}

	public void setFull(int full) {
		this.full = full;
	}

	public InetSocketAddress getP1Address() {
		return p1Address;
	}

	public void setP1Address(InetSocketAddress p1Address) {
		this.p1Address = p1Address;
	}

	public InetSocketAddress getP2Address() {
		return p2Address;
	}

	public void setP2Address(InetSocketAddress p2Address) {
		this.p2Address = p2Address;
	}
	
	
}
