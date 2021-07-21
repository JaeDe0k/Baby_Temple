package com.babytemple.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import com.babytemple.room.RoomList;
import com.babytemple.window.Game;
import com.babytemple.window.Game.STATE;
import com.babytemple.window.Initial_Settings;
import com.babytemple.window.Window;

public class tcpClient {
	private SocketChannel socketChannel;
	private Charset charset = Charset.forName("UTF-8");
	private Window window;
	private String address;
	private int port;
	private Initial_Settings Initial = new Initial_Settings();;
	
	public tcpClient(Window window) {
		this.setting();
		this.window = window;
	}
	
	//TCP���� - �����ּ� �� ��Ʈ ����
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getTCPport();
	}
	
	public void startClient() {
		Thread thread = new Thread() { //receive()���� ���ŷ�� �Ͼ�� ������ ���ο� �۾� �����带 �����Ѵ�.
			public void run() {
				try {
					socketChannel = SocketChannel.open();
					socketChannel.configureBlocking(true);
					socketChannel.connect(new InetSocketAddress(address, port)); //�ش� �ּ�:��Ʈ�� ����
					
					String message = "[���� �Ϸ�]: " + socketChannel.getRemoteAddress() + "]";
					
					if(Game.gameState != STATE.Login) { //�α��� ���°� �ƴϸ� ù����� �ڽ��� �г����� ������ ���� (�������� ���� ��� ������Ʈ)
						String data = "@nick"+Game.userID;
						window.getTcpClient().send(data);
					}
					
					
					
					System.out.println(message);
				} catch (IOException e) {
					String message = "[���� ��� �ȵ�]";
					System.out.println(message);
					if(socketChannel.isOpen()) //socketChannel�� ���������� stopClient()ȣ��
						stopClient();
					return; //������ ����
				}
				receive();
			}
		};
		thread.start();
	}

	public void stopClient() {
		try {
			String message = "[TCP ���� ����]";
			System.out.println(message);
			
			if(socketChannel!=null && socketChannel.isOpen()) //socketChannel �ʵ尡 null�� �ƴϰ�, ���� �����ִ� ���
				socketChannel.close(); //socketChannel�� �ݴ´�.
		} catch(IOException e) {}
	}
	
	// startClient()���� ������ �۾� ������󿡼� ȣ���� �ȴ�.
	public void receive() { //�������� ������ �޴� ����
		while(true) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(10000); //���̰� 100���� ����
				
				//������ ������������ �������� ��� IOException �߻�
				int readByteCount = socketChannel.read(byteBuffer);
				
				//������ ���������� Socket�� close()�� ȣ������ ���
				if(readByteCount == -1)
					throw new IOException();
				
				byteBuffer.flip(); //ByteBuffer�� ��ġ �Ӽ����� ����
				String data = charset.decode(byteBuffer).toString(); //UTF-8�� ���ڵ��� ���ڿ��� data�� ����
				if(data.matches("^@userList.*")) { //���ο� ������ ������ ��������Ʈ�� ������Ʈ
					data = data.substring(9, data.length()); //@userList������ �����ϰ� ����
					StringTokenizer userListDate = new StringTokenizer(data, "@", false); //@��  �����ڷ� �д�.
					String userList[] = new String[userListDate.countTokens()]; //�������� ������ŭ �迭ũ�� ����
					for(int a=0;userListDate.hasMoreTokens();a++)  //�迭�� �� userList����� ����
						userList[a] = userListDate.nextToken();
					window.userList.setListData(userList); //������� �ʱ�ȭ
				} else if(data.matches("^@roomList.*")) { //�� ����� �������� �޾ƿ� ������Ʈ
					data = data.substring(9, data.length());
					StringTokenizer roomListDate = new StringTokenizer(data, "#", false);
					String roomList[] = new String[roomListDate.countTokens()];
					for(int a=0;roomListDate.hasMoreTokens();a++)  
						roomList[a] = roomListDate.nextToken();
					window.roomList.setListData(roomList); //�� ��� ������Ʈ
				} else if(data.matches("#CreateRoom")) { //������ �游��⸦ ��������� ���� ������
					Game.p1ID = Game.userID;
					Game.p1Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom(); //�� �̵� �� ����
					
					String p1data = "@udpCreate"+Game.userID;
					window.getUdpClient().send(p1data); //������ �ڱ�� ���� ������Ʈ
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient(); //������Ŷ ���۽���
				} else if(data.matches("#Overlap")) { //�̹� �����ϴ� �� �϶�
					window.textRoomField.setText("�̹� �����ϴ� ���̸��Դϴ�.");
				} else if(data.matches("#EnterRoom")) { //������ ��� ������
					Game.p2ID = Game.userID;
					Game.p2Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom();
					
					String p2data = "@udpEnter"+Game.userID;
					window.getUdpClient().send(p2data); //UDP���� �۽�
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient();
					window.getGame().RgetRoom().isfull = "";
				} else if(data.matches("#FullRoom")) { //�� ����
					window.getGame().RgetRoom().isfull = "���� �� á���ϴ�.";
				} else if(data.matches("^#Quickentry.*")) { //������ ��� ������
					data = data.substring(11, data.length());
					window.getGame().RgetRoom().setRoomName(data);
					Game.p2ID = Game.userID;
					Game.p2Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom();
					
					String p2data = "@udpEnter"+Game.userID;
					window.getUdpClient().send(p2data); //UDP���� �۽�
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient();
					window.getGame().RgetRoom().isfull = "";
				} else if(data.matches("#QuickFull")) { //�� ����
					window.getGame().RgetRoom().isfull = "����� ���ų� �� ��ü�� �����ϴ�.";
				} else if(RoomList.roomStatus!=3 && Game.gameState != STATE.Game) { //�ĺ��ڰ� ������ ��üä������ ������ ������Ʈ
					String message = "[�ޱ� �Ϸ�]";
					System.out.println(message);
					window.textarea.append(data+"\n");
					
					window.ViewText.getVerticalScrollBar().setValue(window.ViewText.getVerticalScrollBar().getMaximum());
				}	
			} catch (Exception e) {
				String message = "[���� ��� �ȵ�]";
				System.out.println(message);
				stopClient();
				break;
			}
		}
	}
	
	public void send(String data) {
		Thread thread = new Thread() {
			public void run() {
				try {
					ByteBuffer byteBuffer = charset.encode(data);
					socketChannel.write(byteBuffer);
					String message = "[������ �Ϸ�]";
					System.out.println(message);
				} catch(Exception e) {
					String message = "[���� ��� �ȵ�]";
					System.out.println(message);
					stopClient();
				}
			}
		};
		thread.start();
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
}
