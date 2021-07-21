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
	private ExecutorService executorService; //������Ǯ�� ExecutorService �ʵ带 ����
	private ServerSocketChannel serverSocketChannel; //Ŭ���̾�Ʈ ������ �����ϴ� ServerSocketChannel �ʵ� ����
	 /*����� Ŭ���̾�Ʈ�� �����ϴ� List<Client> Ÿ���� connections �ʵ带 �����Ѵ�.
	  * ���� ���� ��Ƽ������ ȯ���� �ƴ϶�� ArrayList�� ����ϴ°��� �ٶ���������,
	  * ArrayList�� ����ȭ�� �������� �����̱� ������ Thread�� ������ Vector�� �ʱ�ȭ ���ش�. */
	private List<Client> connections = new Vector<Client>();
	private ServerGui gui;
	private Charset charset = Charset.forName("UTF-8");
	private Initial_Settings Initial = new Initial_Settings();
	private String address;
	private int port;
	
	tcpServer(ServerGui gui) {
		this.gui = gui;
	}
	
	//UDP���� - �����ּ� �� ��Ʈ ����
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getTCPport();
	}
	
	void startServer() { //���� ���� �ڵ�
		this.setting();
		executorService = Executors.newFixedThreadPool(1000); //CPU �ھ� ���� �°� �����带 �����ؼ� �����ϴ� ExcutorService�� ����
		
		try {
			serverSocketChannel = ServerSocketChannel.open(); //ServerSocketChannel�� ���� �޼ҵ��� open()���� ����
			serverSocketChannel.configureBlocking(true); //�⺻������ ���ŷ ������� ����������, ��������� �����Ѵ�.
			serverSocketChannel.bind(new InetSocketAddress(address, port)); // IP(������)�� ���ε���Ʈ ������ ���������� ����
		} catch(Exception e) {
			System.out.println("�̹� ���ǰ� �ִ� ��Ʈ��ȣ �Դϴ�."); //�ش� ��Ʈ�� �̹� �ٸ� ���α׷����� ����ϰ� ������.
			if(serverSocketChannel.isOpen())
				stopServer(); //��������
			return; //startServer() �޼ҵ� ����
		}
		
		//ServerSocketChannel�� �ݺ��ؼ� Ŭ���̾�Ʈ ���� ��û�� ��ٷ��� �ϹǷ� ������Ǯ�� �۾�������󿡼� accept() �޼ҵ带 �ݺ������� ȣ�����־���Ѵ�.
		Runnable runnable = new Runnable() { //���� ���� �۾��� Runnable�� ����
			public void run() { 
				gui.tcpDisplay.append("[��������]\n");
				gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
				gui.tcpButton.setText("tcpStop");
				
				while(true) { //�ݺ������� Ŭ���̾�Ʈ�� ������ �����ϱ� ���� ���� ���� �ۼ�
					try {
						SocketChannel socketChannel = serverSocketChannel.accept(); //Ŭ���̾�Ʈ�� ���� ��û�� ��ٸ���. [���� ��û�� ������ ������ �����ϰ� ��ſ� SocketChannel�� �����Ѵ�.]
						String message1 = "[���� ����: " + socketChannel.getRemoteAddress() + ": " +Thread.currentThread().getName()+"]"; //"[�������: Ŭ���̾�Ʈ IP: �۾������� �̸�]"���� ���ڿ��� ����
						gui.tcpDisplay.append(message1+"\n");
						gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
						Client client = new Client(socketChannel); //����� socketChannel�� �̿��� Client ��ü ����
						connections.add(client); //connections �÷��ǿ� �߰�
						
						String message2 = "[���� ����: " + connections.size() + "]"; //"[���ᰳ��: ���� �����ǰ� �ִ� Client ��ü ��]"�� ���ڿ��� ����
						gui.tcpDisplay.append(message2+"\n");
						gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
					} catch(Exception e) {
						if(serverSocketChannel.isOpen()) //���� �߻��� serverSocketChannel�� ���� �ִ��� Ȯ���Ѵ�.
							stopServer(); //stopServer()ȣ��
						break; //���� ������ �����Ų��.
					}
				}
			};
		};
		executorService.submit(runnable); //���� ���� �۾��� ������Ǯ���� ó���ϱ� ���� excutorService�� submit()�� ȣ���Ѵ�.
	}
	
	void stopServer() { //���� ���� �ڵ�
		try {
			Iterator<Client> iterator = connections.iterator(); //connections �÷������κ��� �ݺ��ڸ� ����.
			while(iterator.hasNext()) { //while������ �ݺ��ڸ� �ݺ�
				Client client = iterator.next(); //Client�� �ϳ��� ��´�.
				client.socketChannel.close(); //Client�� ������ �ִ� SocketChannel�� �ݴ´�.
				iterator.remove(); //connections �÷��ǿ��� Client�� ����
			}
			
			if(serverSocketChannel!=null && serverSocketChannel.isOpen()) //serverSocketChannel�� null�� �ƴϰ� ����������
				serverSocketChannel.close(); //serverSocketChannel�� �ݴ´�.
			
			if(executorService!=null && !executorService.isShutdown()) //excutorService�� null�� �ƴϰ� ������°� �ƴϸ� 
				executorService.shutdown(); //excutorService�� �����Ѵ�.
			
			ServerGui.roomList.clear(); //�븮��Ʈ �ʱ�ȭ
			
			gui.tcpDisplay.append("[���� ����]\n");
			gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
			gui.tcpButton.setText("tcpStart");
		} catch (Exception e) {}
	}
	
	class Client { //������ ��� �ڵ� (����� Ŭ���̾�Ʈ�� ǥ��)
		public String userID;
		SocketChannel socketChannel; //��ſ� SocketChannel�� �ʵ�� ����
		Client(SocketChannel socketChannel) { //������
			this.socketChannel = socketChannel;
			receive(); //Ŭ���̾�Ʈ �����͸� �ޱ� ���� receive�� ȣ���Ѵ�.
		}

		//������Ǯ�� �۾� �����尡 ó���ϵ��� Runnable�� Ŭ���̾�Ʈ�κ��� �����͸� �޴� �۾��� �����Ѵ�.
		void receive() { //������ �ޱ� �ڵ�
			Runnable runnable = new Runnable() {
				public void run() {
					
					String roomList = "@roomList";

					for(roomInfo room : ServerGui.roomList) {
						if(room.getFull()==2)
							roomList +="#"+room.getRoomName()+"[FULL]";
						else
							roomList +="#"+room.getRoomName();
					}
					
					for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
						client.send(roomList);
					
					while(true) { //���� �ݺ�
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(10000); //100���� ����Ʈ�� ������ �� �ִ� ByteBuffer�� �����Ѵ�.
							
							
							//socketChannel�� read() �޼ҵ带 ȣ���Ͽ� Ŭ���̾�Ʈ�� �����͸� ������ ������ ���ŷ�ȴ�.
							//Ŭ���̾�Ʈ�� ������ ���Ḧ ���� ��� IOException �߻�
							//�����͸� ������ byteBuffer�� �����ϰ� ���� ����Ʈ ������ readByteCount�� �����Ѵ�.
							int readByteCount = socketChannel.read(byteBuffer); 
							
							//Ŭ���̾�Ʈ�� ���������� SocketChannel�� close()�� ȣ������ ��� -1�� �����ϱ� ������ IOException�� ������ �߻� ��Ų��.
							if(readByteCount == -1)
								throw new IOException();
							
							//���������� �����͸� ���� ���, "[��û ó��: Ŭ���̾�Ʈ IP: �۾��������̸�]"���� ������ ���ڿ��� ����
							String message = "[��û ó��: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
							gui.tcpDisplay.append(message+"\n");
							gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
							
							//�����Ͱ� ����� byteBuffer�� flip()�޼ҵ带 ȣ���ؼ� ��ġ �Ӽ����� �����Ѵ�.
							//flip�� ByteBuffer�� ������ �� �� �����͸� �б� ���ؼ� �ݵ�� ������ϴµ�
							//limit�� ����  position���� ���� ��, position�� 0���� �����ϴ� �Լ��̴�.
							//position�� ��ġ�������� �а� ���⸦ �ϱ� ������ flip�� �����ϰ� ������Ѵ�.
							byteBuffer.flip();
							String data = charset.decode(byteBuffer).toString();//UTF-8�� ���ڵ��� ���ڿ��� ��´�.
							
							if(data.matches("^@chat.*")) {
								data = data.substring(5, data.length()); //@chat�� �����ϰ� �ش� Client�� userID�� ����
								for(Client client : connections) //��� Ŭ���̾�Ʈ���� ������ ���� connections�� ����� Client�� �ϳ��� ��´�.
									client.send(data); // send()�޼ҵ带 ȣ���Ѵ�.
							} else if(data.matches("^@nick.*")) { //@nick�� ������ �����͸�
								userID = data.substring(5, data.length()); //@nick�� �����ϰ� �ش� Client�� userID�� ����
								String userList = "@userList"; //��� Ŭ���̾�Ʈ �̸��� ������ �迭
								for(Client client : connections) //��� Ŭ���̾�Ʈ userID�� userList�� ����
									userList += "@"+client.userID; //
								for(Client client : connections) //��� Ŭ���̾�Ʈ���� ������ ���� connections�� ����� Client�� �ϳ��� ��´�.
									client.send(userList); // send()�޼ҵ带 ȣ���Ѵ�.
								gui.tcpDisplay.append("[���� ����]"+userID+"\n"); //���� ���� Client ���
							} else if(data.matches("^@room.*")) { //@room���� ������ �����͸�
								data = data.substring(5, data.length()); //@room�� �����ϰ� �ش� Client�� userID�� ����
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
									
									for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
										client.send(room_List);
									
									String over = "#CreateRoom";
									send(over);
								} else {
									String over = "#Overlap";
									send(over);
								}
								gui.tcpDisplay.append("[�� ���� :"+data+"]\n");
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
											
											for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
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
											
											for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
												client.send(room_List);
											
											break;
										}
										
									}
								}
							} else if(data.matches("^@Enterroom.*")) {
								data = data.substring(10, data.length()); //@room�� �����ϰ� �ش� Client�� userID�� ����
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
									
									for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
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
									
									for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
										client.send(room_List);
								}
							}
						} catch(Exception e) {
							try {
								connections.remove(Client.this); //���ܰ� �߻��ϸ� connections���� ���� Client��ü�� �����Ѵ�.(Ŭ���̾�Ʈ�� ����� �ȵɶ� �߻�)
								//"[Ŭ���̾�Ʈ ��� �ȵ�: Ŭ���̾�ƮIP:�۾��������̸�]"���� ������ ���ڿ� ����
								String message = "[Ŭ���̾�Ʈ ��� �ȵ�: " + Thread.currentThread().getName() + "]";
								gui.tcpDisplay.append(message+"\n");
								gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
								socketChannel.close(); //socketChannel�� �ݴ´�.
								
								String userList = "@userList"; //��� Ŭ���̾�Ʈ �̸��� ������ �迭
								for(Client client : connections) //��� Ŭ���̾�Ʈ userID�� userList�� ����
									userList += "@"+client.userID; //
								for(Client client : connections) //��� Ŭ���̾�Ʈ���� ������ ���� connections�� ����� Client�� �ϳ��� ��´�.
									client.send(userList); // send()�޼ҵ带 ȣ���Ѵ�.
								
							} catch (IOException e2) {}
							break;
						}
					}
				}
			};
			executorService.submit(runnable); //������Ǯ���� ó���ϱ� ���� submit()�� ȣ���Ѵ�.
		}
		
		void send(String data) { //������ ���� �ڵ�
			Runnable runnable = new Runnable() { //�����͸� Ŭ���̾�Ʈ�� ������ �۾��� Runnable�� �����Ѵ�.
				public void run() {
					try {
						ByteBuffer byteBuffer = charset.encode(data); //�Ű������� ���� data ���ڿ��κ��� UTF-8�� ���ڵ��� ByteBuffer�� ��´�.
						socketChannel.write(byteBuffer); //SocketChannel�� write()�޼ҵ带 ȣ���Ѵ�. ����
					} catch(Exception e) {
						try {
							String message = "[Ŭ���̾�Ʈ ��� �ȵ�: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName() + "]";
							gui.tcpDisplay.append(message+"\n");
							gui.tcpScrollPane.getVerticalScrollBar().setValue(gui.tcpScrollPane.getVerticalScrollBar().getMaximum());
							
							connections.remove(Client.this); //���ܰ� �߻��� ���� Client ��ü�� connections �÷��ǿ��� �����Ѵ�.
							socketChannel.close(); //socketChannel�� �ݴ´�.
							
							String userList = "@userList"; //��� Ŭ���̾�Ʈ �̸��� ������ �迭
							for(Client client : connections) //��� Ŭ���̾�Ʈ userID�� userList�� ����
								userList += "@"+client.userID; //
							for(Client client : connections) //��� Ŭ���̾�Ʈ���� ������ ���� connections�� ����� Client�� �ϳ��� ��´�.
								client.send(userList); // send()�޼ҵ带 ȣ���Ѵ�.
						} catch (IOException e2) {}
					}
				}
			};
			executorService.submit(runnable); //������Ǯ���� ó���ϱ� ���� submit()�� ȣ���Ѵ�.
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
							
							for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
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
							
							for(Client client : connections) //��� Ŭ���̾�Ʈ�� �븮��Ʈ ������
								client.send(room_List);
							
							break;
						}
						
					}
				}
			}
		};
		executorService.submit(runnable); //������Ǯ���� ó���ϱ� ���� submit()�� ȣ���Ѵ�.
	}
}