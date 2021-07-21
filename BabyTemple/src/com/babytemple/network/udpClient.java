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
		this.socketAddress = new InetSocketAddress(address, port); //Relay UDP서버
		this.tempsocket = new InetSocketAddress(address, port);
	}
	
	//UDP서버 - 연결주소 및 포트 설정
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
						if(RoomList.roomStatus == 3 || Game.gameState == STATE.Game) { //방대기실 or Game 중일때 더미패킷을 10초마다 전송하여 UDP세션유지
							String reamin = "#dummy"; //UDP세션유지를 위한 더미패킷
							ByteBuffer byteBuffer = charset.encode(reamin);
							datagramChannel.send(byteBuffer, socketAddress);
							Thread.sleep(10000); //30초 정도 UDP세션을 유지하지만 안전하게 10초마다 더미패킷을 보내기 위해 사용.
						}
					} catch (Exception e) {
						if(datagramChannel.isOpen())
							stopClient();
						return; //스레드 종료
					}
				}
			}
		};
		remainThread.start();
	}

	public void stopClient() {
		try {
			String message = "[UDP 연결 끊음]";
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
		                	 
		                	 String player = infos.nextToken(); //P1, P2 구분
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
		                	 
		                	 String player = infos.nextToken(); //P1, P2 구분
		                	 boolean Left = infos.nextToken().equals("true");
		                	 boolean Right = infos.nextToken().equals("true");
		                	 int x = Integer.parseInt(infos.nextToken());
		                	 int y = Integer.parseInt(infos.nextToken());
		                	 window.getGame().getHandler().netwrokKeyRelease(player, Left, Right, x, y);
		                	 
		                 }
		                 
		                 if (data.matches("^@chat.*")) { //방대기실채팅
								data = data.substring(5, data.length());
								window.textareaTeam.append(data+"\n");
								window.ViewTextTeam.getVerticalScrollBar().setValue(window.ViewTextTeam.getVerticalScrollBar().getMaximum());
		                 } else if (data.matches("^@Gchat.*")) { //게임채팅
								data = data.substring(6, data.length());
								ChatPanel.textareaTeam.append(data+"\n");
								ChatPanel.ViewTextTeam.getVerticalScrollBar().setValue(window.ViewTextTeam.getVerticalScrollBar().getMaximum());
		                 } else if (data.matches("^#reset.*")) { //방나가기 (*소켓리셋 및 정보 초기화*)
		                	 data = data.substring(6, data.length());
		                	 String userID = data;
		                	 if(Game.p2ID.equals(userID)) { //Player2가 나갈시 Player1는 Player 정보만 초기화
		                		 Game.p2ID="";
		                		 Game.p2Icon=0;
		                	 } else if(Game.p1ID.equals(userID)) { //Player1이 나갈시 모든 정보 초기화 및 방폭파
		                		 Game.p1ID="";
		                		 Game.p2ID="";
		                		 Game.p1Icon=0;
		                		 Game.p2Icon=0;
		                		 
		                		 window.getGame().RgetRoom().outRemoveRoom();
		                	 }
		                	 setSocketAddress(new InetSocketAddress(address, port));
		                 } else if (data.matches("^#gameStart")) { //게임시작
		                	 window.getGame().RgetRoom().gameStart();
		                 } else if (data.matches("^#stage.*")) { //스테이지 선택
		                	 System.out.println("스테이지 조정");
		                	 data = data.substring(6, data.length());
		                	 RoomList.stageLevel = Integer.parseInt(data);
		                 } else if (data.matches("^@GExit")) { //게임중 나가기
		                	 window.getGame().gameExit();
		                 } else if (data.matches("^@replay")) { //게임 리플레이
		                	 window.getGame().clearLevel();
		                	 window.getGame().switchLevel(Game.LEVEL);
		                 } else if (data.matches("^@nextLevel")) { //다음 스테이지
		                	 window.getUdpClient().send("@nextLevel");
		                	 if(Game.LEVEL != 3)
		                		 Game.LEVEL += 1;
		                	 window.getGame().switchLevel(Game.LEVEL);
		                 }
		                 
		                 if (data.matches("^@p2p/.*")) { //방입장시 Relay 서버에서 받은 상대방의 주소와 포트로 UDP연결 (서로의 정보를 교환)
								data = data.substring(5, data.length());
								StringTokenizer infos = new StringTokenizer(data, ":", false);
								String address = infos.nextToken();
								int port =  Integer.parseInt(infos.nextToken());
								setSocketAddress(new InetSocketAddress(address, port));
								
								String mydata="";
								if(Game.p1ID.equals(Game.userID)) //자신이 P1이면 P2에게 자신의 ID와 ICON, 현재 스테이지를 알려줌
									mydata = "@p1Info#"+Game.p1ID+"#"+Game.p1Icon+"#"+RoomList.stageLevel;
								else if(Game.p2ID.equals(Game.userID)) { //P2가 P1에게 ID와 ICON을 알려줌
									mydata = "@p2Info#"+Game.p2ID+"#"+Game.p2Icon;
								}
								send(mydata);
		                 } else if (data.matches("^@p1Info.*")) { //P2가 P1의 데이터를 받아와 저장 및 갱신
		                	 data = data.substring(7, data.length());
		                	 StringTokenizer info = new StringTokenizer(data, "#", false);
		                	 String userID = info.nextToken();
		                	 String userIcon = info.nextToken();
		                	 int stage = Integer.parseInt(info.nextToken());
		                	 for(int i=0; i<3 ; i++) {
		                		 Game.p1ID = userID;
		                		 Game.p1Icon = Integer.parseInt(userIcon);
		                		 RoomList.stageLevel = stage;
		                		 System.out.println(stage+"[스테이지조정]");
		                		 
		                	 }
		                 } else if (data.matches("^@p2Info.*")) { //P1이 P2의 데이터를 받아와 저장 및 갱신
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
						System.out.println("[서버 통신 안됨]");
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
					
					if(data.matches("^#reset.*")) { //방 퇴장할 시 상대방에게 #reset 데이터를 보내고 자신의 소켓도 Relay 서버로 초기화
						receiveThread.stop();
						setSocketAddress(new InetSocketAddress(address, port));
						receive();
						
						String data = "@delRoom#"+window.getGame().RgetRoom().getRoomName()+"#"+Game.userID;
						send(data);
					}
				} catch(Exception e) {
					System.out.println("[서버 통신 안됨]");
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
