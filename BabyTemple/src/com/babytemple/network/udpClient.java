package com.babytemple.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import com.babytemple.room.RoomList;
import com.babytemple.window.ChatPanel;
import com.babytemple.window.Game;
import com.babytemple.window.Initial_Settings;
import com.babytemple.window.Game.STATE;
import com.babytemple.window.Window;

public class udpClient {
	private DatagramChannel datagramChannel;
	private Charset charset = Charset.forName("UTF-8");
	private Window window;
	private SocketAddress socketAddress;
	private SocketAddress tempsocket;
	private Thread receiveThread;
	private Thread remainThread;
	private String address;
	private int port;
	private Initial_Settings Initial = new Initial_Settings();
	
	public udpClient(Window window) {
		this.setting();
		this.window = window;
		try {datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);} catch (IOException e) {}
		this.socketAddress = new InetSocketAddress(address, port); //Relay UDP����
		this.tempsocket = new InetSocketAddress(address, port);
	}
	
	//UDP���� - �����ּ� �� ��Ʈ ����
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getUDPport();
	}
	
	@SuppressWarnings("deprecation")
	public void reaminStop() {
		remainThread.interrupt();
		remainThread.stop();
	}
	
	public void remainClient() {
		remainThread = new Thread() {
			public void run() {
				setSocketAddress(new InetSocketAddress(address, port));
				while(true) {
					try {
						if(RoomList.roomStatus == 3 || Game.gameState == STATE.Game) { //����� or Game ���϶� ������Ŷ�� 10�ʸ��� �����Ͽ� UDP��������
							String reamin = "#dummy"; //UDP���������� ���� ������Ŷ
							ByteBuffer byteBuffer = charset.encode(reamin);
							datagramChannel.send(byteBuffer, socketAddress);
							Thread.sleep(10000); //30�� ���� UDP������ ���������� �����ϰ� 10�ʸ��� ������Ŷ�� ������ ���� ���.
						}
					} catch (Exception e) {
						if(datagramChannel.isOpen())
							stopClient();
						return; //������ ����
					}
				}
			}
		};
		remainThread.start();
	}

	public void stopClient() {
		try {
			String message = "[UDP ���� ����]";
			System.out.println(message);

			if(datagramChannel!=null && datagramChannel.isOpen())
				datagramChannel.close();
			
			if(socketAddress!=null)
				socketAddress = null;

		} catch(IOException e) {}
	}
	

	public void receive() {
		receiveThread = new Thread() {
			public void run() {
				while(true) {
					try {
						 ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
		                 socketAddress = datagramChannel.receive(byteBuffer);
		                 byteBuffer.flip();
		                 String data = charset.decode(byteBuffer).toString();
		                 System.out.println(data);
		                 
		                 if (data.matches("^@KeyPressed.*")) {
		                	 data = data.substring(11, data.length());
		                	 StringTokenizer infos = new StringTokenizer(data, "#", false);
		                	 
		                	 String player = infos.nextToken(); //P1, P2 ����
		                	 boolean Jumping = infos.nextToken().equals("true");
		                	 float velY = Float.parseFloat(infos.nextToken());
		                	 boolean Left = infos.nextToken().equals("true");
		                	 boolean Right = infos.nextToken().equals("true");
		                	 int x = Integer.parseInt(infos.nextToken());
		                	 int y = Integer.parseInt(infos.nextToken());
		                	 window.getGame().getHandler().netwrokKeyPressed(player, Jumping, velY, Left, Right, x, y);
		                	 
		                 } else if(data.matches("^@KeyRelease.*")) {
		                	 data = data.substring(11, data.length());
		                	 StringTokenizer infos = new StringTokenizer(data, "#", false);
		                	 
		                	 String player = infos.nextToken(); //P1, P2 ����
		                	 boolean Left = infos.nextToken().equals("true");
		                	 boolean Right = infos.nextToken().equals("true");
		                	 int x = Integer.parseInt(infos.nextToken());
		                	 int y = Integer.parseInt(infos.nextToken());
		                	 window.getGame().getHandler().netwrokKeyRelease(player, Left, Right, x, y);
		                	 
		                 }
		                 
		                 if (data.matches("^@chat.*")) { //�����ä��
								data = data.substring(5, data.length());
								window.textareaTeam.append(data+"\n");
								window.ViewTextTeam.getVerticalScrollBar().setValue(window.ViewTextTeam.getVerticalScrollBar().getMaximum());
		                 } else if (data.matches("^@Gchat.*")) { //����ä��
								data = data.substring(6, data.length());
								ChatPanel.textareaTeam.append(data+"\n");
								ChatPanel.ViewTextTeam.getVerticalScrollBar().setValue(window.ViewTextTeam.getVerticalScrollBar().getMaximum());
		                 } else if (data.matches("^#reset.*")) { //�泪���� (*���ϸ��� �� ���� �ʱ�ȭ*)
		                	 data = data.substring(6, data.length());
		                	 String userID = data;
		                	 if(Game.p2ID.equals(userID)) { //Player2�� ������ Player1�� Player ������ �ʱ�ȭ
		                		 Game.p2ID="";
		                		 Game.p2Icon=0;
		                	 } else if(Game.p1ID.equals(userID)) { //Player1�� ������ ��� ���� �ʱ�ȭ �� ������
		                		 Game.p1ID="";
		                		 Game.p2ID="";
		                		 Game.p1Icon=0;
		                		 Game.p2Icon=0;
		                		 
		                		 window.getGame().RgetRoom().outRemoveRoom();
		                	 }
		                	 setSocketAddress(new InetSocketAddress(address, port));
		                 } else if (data.matches("^#gameStart")) { //���ӽ���
		                	 window.getGame().RgetRoom().gameStart();
		                 } else if (data.matches("^#stage.*")) { //�������� ����
		                	 System.out.println("�������� ����");
		                	 data = data.substring(6, data.length());
		                	 RoomList.stageLevel = Integer.parseInt(data);
		                 } else if (data.matches("^@GExit")) { //������ ������
		                	 window.getGame().gameExit();
		                 } else if (data.matches("^@replay")) { //���� ���÷���
		                	 window.getGame().clearLevel();
		                	 window.getGame().switchLevel(Game.LEVEL);
		                 } else if (data.matches("^@nextLevel")) { //���� ��������
		                	 window.getUdpClient().send("@nextLevel");
		                	 if(Game.LEVEL != 3)
		                		 Game.LEVEL += 1;
		                	 window.getGame().switchLevel(Game.LEVEL);
		                 }
		                 
		                 if (data.matches("^@p2p/.*")) { //������� Relay �������� ���� ������ �ּҿ� ��Ʈ�� UDP���� (������ ������ ��ȯ)
								data = data.substring(5, data.length());
								StringTokenizer infos = new StringTokenizer(data, ":", false);
								String address = infos.nextToken();
								int port =  Integer.parseInt(infos.nextToken());
								setSocketAddress(new InetSocketAddress(address, port));
								
								String mydata="";
								if(Game.p1ID.equals(Game.userID)) //�ڽ��� P1�̸� P2���� �ڽ��� ID�� ICON, ���� ���������� �˷���
									mydata = "@p1Info#"+Game.p1ID+"#"+Game.p1Icon+"#"+RoomList.stageLevel;
								else if(Game.p2ID.equals(Game.userID)) { //P2�� P1���� ID�� ICON�� �˷���
									mydata = "@p2Info#"+Game.p2ID+"#"+Game.p2Icon;
								}
								send(mydata);
		                 } else if (data.matches("^@p1Info.*")) { //P2�� P1�� �����͸� �޾ƿ� ���� �� ����
		                	 data = data.substring(7, data.length());
		                	 StringTokenizer info = new StringTokenizer(data, "#", false);
		                	 String userID = info.nextToken();
		                	 String userIcon = info.nextToken();
		                	 int stage = Integer.parseInt(info.nextToken());
		                	 for(int i=0; i<3 ; i++) {
		                		 Game.p1ID = userID;
		                		 Game.p1Icon = Integer.parseInt(userIcon);
		                		 RoomList.stageLevel = stage;
		                		 System.out.println(stage+"[������������]");
		                		 
		                	 }
		                 } else if (data.matches("^@p2Info.*")) { //P1�� P2�� �����͸� �޾ƿ� ���� �� ����
		                	 data = data.substring(7, data.length());
		                	 StringTokenizer info = new StringTokenizer(data, "#", false);
		                	 String userID = info.nextToken();
		                	 String userIcon = info.nextToken();
		                	 for(int i=0; i<3 ; i++) {
		                		 Game.p2ID = userID;
		                		 Game.p2Icon = Integer.parseInt(userIcon);
		                	 }
		                 }
		                 
					} catch (Exception e) {
						System.out.println("[���� ��� �ȵ�]");
						stopClient();
						break;
					}
				}
			}
		};
		receiveThread.start();
	}
	
	public void send(String data) {
		Thread thread = new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					if(!socketAddress.equals(tempsocket)) {
						ByteBuffer byteBuffer = charset.encode(data);
						datagramChannel.send(byteBuffer, socketAddress);
					} else if(socketAddress.equals(tempsocket) && !data.matches("^@KeyPressed.*") && !data.matches("^@KeyRelease.*") && !data.matches("^@chat.*") && !data.matches("^@Gchat.*")) {
						ByteBuffer byteBuffer = charset.encode(data);
						datagramChannel.send(byteBuffer, socketAddress);
					}
					
					if(data.matches("^#reset.*")) { //�� ������ �� ���濡�� #reset �����͸� ������ �ڽ��� ���ϵ� Relay ������ �ʱ�ȭ
						receiveThread.stop();
						setSocketAddress(new InetSocketAddress(address, port));
						receive();
						
						String data = "@delRoom#"+window.getGame().RgetRoom().getRoomName()+"#"+Game.userID;
						send(data);
					}
				} catch(Exception e) {
					System.out.println("[���� ��� �ȵ�]");
					stopClient();
				}
			}
		};
		thread.start();
	}
	
	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}
	
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
}
