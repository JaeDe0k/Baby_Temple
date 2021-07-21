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
	
	//TCP서버 - 연결주소 및 포트 설정
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getTCPport();
	}
	
	public void startClient() {
		Thread thread = new Thread() { //receive()에서 블로킹이 일어나기 때문에 새로운 작업 스레드를 생성한다.
			public void run() {
				try {
					socketChannel = SocketChannel.open();
					socketChannel.configureBlocking(true);
					socketChannel.connect(new InetSocketAddress(address, port)); //해당 주소:포트로 연결
					
					String message = "[연결 완료]: " + socketChannel.getRemoteAddress() + "]";
					
					if(Game.gameState != STATE.Login) { //로그인 상태가 아니면 첫실행시 자신의 닉네임을 서버에 전송 (서버에서 유저 목록 업데이트)
						String data = "@nick"+Game.userID;
						window.getTcpClient().send(data);
					}
					
					
					
					System.out.println(message);
				} catch (IOException e) {
					String message = "[서버 통신 안됨]";
					System.out.println(message);
					if(socketChannel.isOpen()) //socketChannel이 열려있으면 stopClient()호출
						stopClient();
					return; //스레드 종료
				}
				receive();
			}
		};
		thread.start();
	}

	public void stopClient() {
		try {
			String message = "[TCP 연결 끊음]";
			System.out.println(message);
			
			if(socketChannel!=null && socketChannel.isOpen()) //socketChannel 필드가 null이 아니고, 현재 열려있는 경우
				socketChannel.close(); //socketChannel을 닫는다.
		} catch(IOException e) {}
	}
	
	// startClient()에서 생성한 작업 스레드상에서 호출이 된다.
	public void receive() { //서버에서 데이터 받는 역할
		while(true) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(10000); //길이가 100으로 생성
				
				//서버가 비정상적으로 종료했을 경우 IOException 발생
				int readByteCount = socketChannel.read(byteBuffer);
				
				//서버가 정상적으로 Socket의 close()를 호출했을 경우
				if(readByteCount == -1)
					throw new IOException();
				
				byteBuffer.flip(); //ByteBuffer의 위치 속성값을 변경
				String data = charset.decode(byteBuffer).toString(); //UTF-8로 디코딩된 문자열을 data에 저장
				if(data.matches("^@userList.*")) { //새로운 유저가 들어오면 유저리스트를 업데이트
					data = data.substring(9, data.length()); //@userList문장을 제거하고 저장
					StringTokenizer userListDate = new StringTokenizer(data, "@", false); //@를  구분자로 둔다.
					String userList[] = new String[userListDate.countTokens()]; //구분자의 개수만큼 배열크기 설정
					for(int a=0;userListDate.hasMoreTokens();a++)  //배열에 각 userList목록을 저장
						userList[a] = userListDate.nextToken();
					window.userList.setListData(userList); //유저목록 초기화
				} else if(data.matches("^@roomList.*")) { //방 목록을 서버에서 받아와 업데이트
					data = data.substring(9, data.length());
					StringTokenizer roomListDate = new StringTokenizer(data, "#", false);
					String roomList[] = new String[roomListDate.countTokens()];
					for(int a=0;roomListDate.hasMoreTokens();a++)  
						roomList[a] = roomListDate.nextToken();
					window.roomList.setListData(roomList); //방 목록 업데이트
				} else if(data.matches("#CreateRoom")) { //서버에 방만들기를 허락했을때 오는 데이터
					Game.p1ID = Game.userID;
					Game.p1Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom(); //방 이동 및 생성
					
					String p1data = "@udpCreate"+Game.userID;
					window.getUdpClient().send(p1data); //서버에 자기방 정보 업데이트
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient(); //더미패킷 전송시작
				} else if(data.matches("#Overlap")) { //이미 존재하는 방 일때
					window.textRoomField.setText("이미 존재하는 방이름입니다.");
				} else if(data.matches("#EnterRoom")) { //방입장 허용 데이터
					Game.p2ID = Game.userID;
					Game.p2Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom();
					
					String p2data = "@udpEnter"+Game.userID;
					window.getUdpClient().send(p2data); //UDP정보 송신
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient();
					window.getGame().RgetRoom().isfull = "";
				} else if(data.matches("#FullRoom")) { //방 제한
					window.getGame().RgetRoom().isfull = "방이 꽉 찼습니다.";
				} else if(data.matches("^#Quickentry.*")) { //방입장 허용 데이터
					data = data.substring(11, data.length());
					window.getGame().RgetRoom().setRoomName(data);
					Game.p2ID = Game.userID;
					Game.p2Icon = Game.userIcon;
					window.getGame().RgetRoom().createRoom();
					
					String p2data = "@udpEnter"+Game.userID;
					window.getUdpClient().send(p2data); //UDP정보 송신
					window.getUdpClient().reaminStop();
					window.getUdpClient().remainClient();
					window.getGame().RgetRoom().isfull = "";
				} else if(data.matches("#QuickFull")) { //방 제한
					window.getGame().RgetRoom().isfull = "빈방이 없거나 방 자체가 없습니다.";
				} else if(RoomList.roomStatus!=3 && Game.gameState != STATE.Game) { //식별자가 없으면 전체채팅으로 데이터 업데이트
					String message = "[받기 완료]";
					System.out.println(message);
					window.textarea.append(data+"\n");
					
					window.ViewText.getVerticalScrollBar().setValue(window.ViewText.getVerticalScrollBar().getMaximum());
				}	
			} catch (Exception e) {
				String message = "[서버 통신 안됨]";
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
					String message = "[보내기 완료]";
					System.out.println(message);
				} catch(Exception e) {
					String message = "[서버 통신 안됨]";
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
