package serverNetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tcpServer {
	private ExecutorService executorService; //스레드풀인 ExecutorService 필드를 선언
	private ServerSocketChannel serverSocketChannel; //클라이언트 연결을 수락하는 ServerSocketChannel 필드 선언
	 /*연결된 클라이언트를 저장하는 List<Client> 타입의 connections 필드를 선언한다.
	  * 보통 현재 멀티스레드 환경이 아니라면 ArrayList를 사용하는것이 바람직하지만,
	  * ArrayList는 동기화가 되지않은 상태이기 때문에 Thread에 안전한 Vector로 초기화 해준다. */
	private List<Client> connections = new Vector<Client>();
	private ServerGui gui;
	private Charset charset = Charset.forName("UTF-8");
	private Initial_Settings Initial = new Initial_Settings();
	private String address;
	private int port;
	
	tcpServer(ServerGui gui) {
		this.gui = gui;
	}
	
	//UDP서버 - 연결주소 및 포트 설정
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getTCPport();
	}
	
	void startServer() { //서버 시작 코드
		this.setting();
		executorService = Executors.newFixedThreadPool(1000); //CPU 코어 수에 맞게 스레드를 생성해서 관리하는 ExcutorService를 생성
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //ServerSocketChannel을 정적 메소드인 open()으로 생성
			serverSocketChannel.configureBlocking(true); //기본적으로 블로킹 방식으로 동작하지만, 명시적으로 설정한다.
			serverSocketChannel.bind(new InetSocketAddress(address, port)); // IP(도메인)및 바인딩포트 적용해 서버소켓을 구성
		} catch(Exception e) {
			System.out.println("이미 사용되고 있는 포트번호 입니다."); //해당 포트를 이미 다른 프로그램에서 사용하고 있을때.
			if(serverSocketChannel.isOpen())
				stopServer(); //서버종료
			return; //startServer() 메소드 종료
		}
		
		//ServerSocketChannel은 반복해서 클라이언트 연결 요청을 기다려야 하므로 스레드풀의 작업스레드상에서 accept() 메소드를 반복적으로 호출해주어야한다.
		Runnable runnable = new Runnable() { //연결 수락 작업을 Runnable로 정의
			public void run() { 
				gui.tcpDisplay.append("[서버시작]\n");
				gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
				gui.tcpButton.setText("tcpStop");
				
				while(true) { //반복적으로 클라이언트의 연결을 수락하기 위해 무한 루프 작성
					try {
						SocketChannel socketChannel = serverSocketChannel.accept(); //클라이언트의 연결 요청을 기다린다. [연결 요청이 들어오면 연결을 수락하고 통신용 SocketChannel을 리턴한다.]
						String message1 = "[연결 수락: " + socketChannel.getRemoteAddress() + ": " +Thread.currentThread().getName()+"]"; //"[연결수락: 클라이언트 IP: 작업스레드 이름]"으로 문자열을 생성
						gui.tcpDisplay.append(message1+"\n");
						gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
						Client client = new Client(socketChannel); //연결된 socketChannel를 이용해 Client 객체 생성
						connections.add(client); //connections 컬렉션에 추가
						
						String message2 = "[연결 개수: " + connections.size() + "]"; //"[연결개수: 현재 관리되고 있는 Client 객체 수]"로 문자열을 생성
						gui.tcpDisplay.append(message2+"\n");
						gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
					} catch(Exception e) {
						if(serverSocketChannel.isOpen()) //예외 발생시 serverSocketChannel이 열려 있는지 확인한다.
							stopServer(); //stopServer()호출
						break; //무한 루프를 종료시킨다.
					}
				}
			};
		};
		executorService.submit(runnable); //연결 수락 작업을 스레드풀에서 처리하기 위해 excutorService의 submit()을 호출한다.
	}
	
	void stopServer() { //서버 종료 코드
		try {
			Iterator<Client> iterator = connections.iterator(); //connections 컬렉션으로부터 반복자를 얻어낸다.
			while(iterator.hasNext()) { //while문으로 반복자를 반복
				Client client = iterator.next(); //Client를 하나씩 얻는다.
				client.socketChannel.close(); //Client가 가지고 있는 SocketChannel을 닫는다.
				iterator.remove(); //connections 컬렉션에서 Client를 제거
			}
			
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()) //serverSocketChannel이 null이 아니고 열려있으면
				serverSocketChannel.close(); //serverSocketChannel을 닫는다.
			
			if(executorService!=null && !executorService.isShutdown()) //excutorService가 null이 아니고 종료상태가 아니면 
				executorService.shutdown(); //excutorService를 종료한다.
			
			ServerGui.roomList.clear(); //룸리스트 초기화
			
			gui.tcpDisplay.append("[서버 멈춤]\n");
			gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
			gui.tcpButton.setText("tcpStart");
		} catch (Exception e) {}
	}
	
	class Client { //데이터 통신 코드 (연결된 클라이언트를 표현)
		public String userID;
		SocketChannel socketChannel; //통신용 SocketChannel을 필드로 선언
		Client(SocketChannel socketChannel) { //생성자
			this.socketChannel = socketChannel;
			receive(); //클라이언트 데이터를 받기 위한 receive를 호출한다.
		}

		//스레드풀의 작업 스레드가 처리하도록 Runnable로 클라이언트로부터 데이터를 받는 작업을 정의한다.
		void receive() { //데이터 받기 코드
			Runnable runnable = new Runnable() {
				public void run() {
					
					String roomList = "@roomList";

					for(roomInfo room : ServerGui.roomList) {
						if(room.getFull()==2)
							roomList +="#"+room.getRoomName()+"[FULL]";
						else
							roomList +="#"+room.getRoomName();
					}
					
					for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
						client.send(roomList);
					
					while(true) { //무한 반복
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(10000); //100개의 바이트를 저장할 수 있는 ByteBuffer를 생성한다.
							
							
							//socketChannel의 read() 메소드를 호출하여 클라이언트가 데이터를 보내기 전까지 블로킹된다.
							//클라이언트가 비정상 종료를 했을 경우 IOException 발생
							//데이터를 받으면 byteBuffer에 저장하고 받은 바이트 개수를 readByteCount에 저장한다.
							int readByteCount = socketChannel.read(byteBuffer); 
							
							//클라이언트가 정상적으로 SocketChannel의 close()를 호출했을 경우 -1을 리턴하기 때문에 IOException을 강제로 발생 시킨다.
							if(readByteCount == -1)
								throw new IOException();
							
							//정상적으로 데이터를 받을 경우, "[요청 처리: 클라이언트 IP: 작업스레드이름]"으로 구성된 문자열을 생성
							String message = "[요청 처리: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
							gui.tcpDisplay.append(message+"\n");
							gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
							
							//데이터가 저장된 byteBuffer의 flip()메소드를 호출해서 위치 속성값을 변경한다.
							//flip은 ByteBuffer에 저장한 후 그 데이터를 읽기 위해서 반드시 써줘야하는데
							//limit를 현재  position으로 설정 후, position을 0으로 설정하는 함수이다.
							//position의 위치에서부터 읽고 쓰기를 하기 때문에 flip은 적절하게 써줘야한다.
							byteBuffer.flip();
							String data = charset.decode(byteBuffer).toString();//UTF-8로 디코딩한 문자열을 얻는다.
							
							if(data.matches("^@chat.*")) {
								data = data.substring(5, data.length()); //@chat를 제거하고 해당 Client에 userID를 저장
								for(Client client : connections) //모든 클라이언트에게 보내기 위해 connections에 저장된 Client를 하나씩 얻는다.
									client.send(data); // send()메소드를 호출한다.
							} else if(data.matches("^@nick.*")) { //@nick로 들어오는 데이터면
								userID = data.substring(5, data.length()); //@nick를 제거하고 해당 Client에 userID를 저장
								String userList = "@userList"; //모든 클라이언트 이름을 저장할 배열
								for(Client client : connections) //모든 클라이언트 userID를 userList에 저장
									userList += "@"+client.userID; //
								for(Client client : connections) //모든 클라이언트에게 보내기 위해 connections에 저장된 Client를 하나씩 얻는다.
									client.send(userList); // send()메소드를 호출한다.
								gui.tcpDisplay.append("[유저 접속]"+userID+"\n"); //현재 접속 Client 출력
							} else if(data.matches("^@room.*")) { //@room으로 들어오는 데이터면
								data = data.substring(5, data.length()); //@room를 제거하고 해당 Client에 userID를 저장
								StringTokenizer infos = new StringTokenizer(data, "#", false);
								String roomName = infos.nextToken();
								String userID = infos.nextToken();
								boolean Overlap = false;
								for(roomInfo list : ServerGui.roomList) {
									if(list.getRoomName().equals(roomName) || list.getUserID()[0].equals(userID))
										Overlap = true;
								}
								if(!Overlap) {
									ServerGui.roomList.add(new roomInfo(roomName, userID));
									Collections.sort(ServerGui.roomList);
									
									String room_List = "@roomList";
									for(roomInfo list : ServerGui.roomList)
										room_List +="#"+list.getRoomName();
									
									for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
										client.send(room_List);
									
									String over = "#CreateRoom";
									send(over);
								} else {
									String over = "#Overlap";
									send(over);
								}
								gui.tcpDisplay.append("[방 생성 :"+data+"]\n");
							} else if (data.matches("^@delRoom.*")) {
								data = data.substring(8, data.length());
								StringTokenizer infos = new StringTokenizer(data, "#", false);
								String roomName = infos.nextToken();
								String userID = infos.nextToken();
								for(roomInfo list : ServerGui.roomList) {
									if(list.getRoomName().equals(roomName)) {
										if(list.getUserID()[0].equals(userID)) {
											ServerGui.roomList.remove(list);
											Collections.sort(ServerGui.roomList);
											
											String room_List = "@roomList";
											
											for(roomInfo roomlist : ServerGui.roomList)
												room_List +="#"+roomlist.getRoomName();
											
											for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
												client.send(room_List);
											
											break;
										} else if(list.getUserID()[1].equals(userID)) {
											list.setFull(1);
											list.setP2Address(null);
											list.setUserID(new String[] {list.getUserID()[0],""});
											Collections.sort(ServerGui.roomList);
											
											String room_List = "@roomList";
											
											for(roomInfo roomlist : ServerGui.roomList)
												room_List +="#"+roomlist.getRoomName();
											
											for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
												client.send(room_List);
											
											break;
										}
										
									}
								}
							} else if(data.matches("^@Enterroom.*")) {
								data = data.substring(10, data.length()); //@room를 제거하고 해당 Client에 userID를 저장
								StringTokenizer infos = new StringTokenizer(data, "#", false);
								String roomName = infos.nextToken();
								String userIDa = infos.nextToken();
								boolean full = false;
								for(roomInfo list : ServerGui.roomList) {
									if(list.getRoomName().equals(roomName) && list.getFull() == 1) {
										list.setUserID(new String[] {list.getUserID()[0],userIDa});
										list.setFull(2);
										
										String over = "#EnterRoom";
										send(over);
										full = true;
										break;
									}
								}
								if(!full) {
									String over = "#FullRoom";
									send(over);
									
									Collections.sort(ServerGui.roomList);
									
									String room_List = "@roomList";
									for(roomInfo roomlist : ServerGui.roomList) {
										if(roomlist.getFull()==2)
											room_List +="#"+roomlist.getRoomName()+"[FULL]";
										else
											room_List +="#"+roomlist.getRoomName();
									}
									
									for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
										client.send(room_List);
								}
							} else if(data.matches("^@Quickentry.*")) {
								data = data.substring(11, data.length());
								boolean full = false;
								for(roomInfo list : ServerGui.roomList) {
									if(list.getFull() == 1) {
										list.setUserID(new String[] {list.getUserID()[0],data});
										list.setFull(2);
										
										String over = "#Quickentry"+list.getRoomName();
										send(over);
										full = true;
										break;
									}
								}
								if(!full) {
									String over = "#QuickFull";
									send(over);
									
									Collections.sort(ServerGui.roomList);
									
									String room_List = "@roomList";
									for(roomInfo roomlist : ServerGui.roomList) {
										if(roomlist.getFull()==2)
											room_List +="#"+roomlist.getRoomName()+"[FULL]";
										else
											room_List +="#"+roomlist.getRoomName();
									}
									
									for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
										client.send(room_List);
								}
							}
						} catch(Exception e) {
							try {
								connections.remove(Client.this); //예외가 발생하면 connections에서 현재 Client객체를 제거한다.(클라이언트와 통신이 안될때 발생)
								//"[클라이언트 통신 안됨: 클라이언트IP:작업스레드이름]"으로 구성된 문자열 생성
								String message = "[클라이언트 통신 안됨: " + Thread.currentThread().getName() + "]";
								gui.tcpDisplay.append(message+"\n");
								gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
								socketChannel.close(); //socketChannel을 닫는다.
								
								String userList = "@userList"; //모든 클라이언트 이름을 저장할 배열
								for(Client client : connections) //모든 클라이언트 userID를 userList에 저장
									userList += "@"+client.userID; //
								for(Client client : connections) //모든 클라이언트에게 보내기 위해 connections에 저장된 Client를 하나씩 얻는다.
									client.send(userList); // send()메소드를 호출한다.
								
							} catch (IOException e2) {}
							break;
						}
					}
				}
			};
			executorService.submit(runnable); //스레드풀에서 처리하기 위해 submit()을 호출한다.
		}
		
		void send(String data) { //데이터 전송 코드
			Runnable runnable = new Runnable() { //데이터를 클라이언트로 보내는 작업을 Runnable로 생성한다.
				public void run() {
					try {
						ByteBuffer byteBuffer = charset.encode(data); //매개값으로 받은 data 문자열로부터 UTF-8로 인코딩한 ByteBuffer를 얻는다.
						socketChannel.write(byteBuffer); //SocketChannel의 write()메소드를 호출한다. 전송
					} catch(Exception e) {
						try {
							String message = "[클라이언트 통신 안됨: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
							gui.tcpDisplay.append(message+"\n");
							gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
							
							connections.remove(Client.this); //예외가 발생한 현재 Client 객체를 connections 컬렉션에서 제거한다.
							socketChannel.close(); //socketChannel를 닫는다.
							
							String userList = "@userList"; //모든 클라이언트 이름을 저장할 배열
							for(Client client : connections) //모든 클라이언트 userID를 userList에 저장
								userList += "@"+client.userID; //
							for(Client client : connections) //모든 클라이언트에게 보내기 위해 connections에 저장된 Client를 하나씩 얻는다.
								client.send(userList); // send()메소드를 호출한다.
						} catch (IOException e2) {}
					}
				}
			};
			executorService.submit(runnable); //스레드풀에서 처리하기 위해 submit()을 호출한다.
		}
	}
	
	void resetRoom(String roomName, String userID) {
		Runnable runnable = new Runnable() {
			public void run() {
				for(roomInfo list : ServerGui.roomList) {
					if(list.getRoomName().equals(roomName)) {
						if(list.getUserID()[0].equals(userID)) {
							ServerGui.roomList.remove(list);
							Collections.sort(ServerGui.roomList);
							
							String room_List = "@roomList";
							
							for(roomInfo roomlist : ServerGui.roomList)
								room_List +="#"+roomlist.getRoomName();
							
							for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
								client.send(room_List);
							
							break;
						} else if(list.getUserID()[1].equals(userID)) {
							list.setFull(1);
							list.setP2Address(null);
							list.setUserID(new String[] {list.getUserID()[0],""});
							Collections.sort(ServerGui.roomList);
							
							String room_List = "@roomList";
							
							for(roomInfo roomlist : ServerGui.roomList)
								room_List +="#"+roomlist.getRoomName();
							
							for(Client client : connections) //모든 클라이언트에 룸리스트 보내기
								client.send(room_List);
							
							break;
						}
						
					}
				}
			}
		};
		executorService.submit(runnable); //스레드풀에서 처리하기 위해 submit()을 호출한다.
	}
}