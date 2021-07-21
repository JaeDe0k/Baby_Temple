package serverNetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class udpServer {
	private ExecutorService executorService; //������Ǯ�� ExecutorService �ʵ带 ����
	private DatagramChannel datagramChannel;
	private ServerGui gui;
	private Charset charset = Charset.forName("UTF-8");
	private Initial_Settings Initial = new Initial_Settings();
	private String address;
	private int port;

	udpServer(ServerGui gui) {
		this.gui = gui;
	}
	
	//UDP���� - �����ּ� �� ��Ʈ ����
	public void setting() {
		this.address = Initial.getServerAddress();
		this.port = Initial.getUDPport();
	}
	
	void startServer() { //���� ���� �ڵ�
		this.setting();
		
		executorService = Executors.newFixedThreadPool(1000); //CPU �ھ� ���� �°� �����带 �����ؼ� �����ϴ� ExcutorService�� ����
		
		try {
			datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
		    datagramChannel.bind(new InetSocketAddress(address, port));
		} catch(Exception e) {
			System.out.println("�̹� ���ǰ� �ִ� ��Ʈ��ȣ �Դϴ�."); //�ش� ��Ʈ�� �̹� �ٸ� ���α׷����� ����ϰ� ������.
			if(datagramChannel.isOpen())
				stopServer(); //��������
			return; //startServer() �޼ҵ� ����
		}
		
		//ServerSocketChannel�� �ݺ��ؼ� Ŭ���̾�Ʈ ���� ��û�� ��ٷ��� �ϹǷ� ������Ǯ�� �۾�������󿡼� accept() �޼ҵ带 �ݺ������� ȣ�����־���Ѵ�.
		Runnable runnable = new Runnable() { //���� ���� �۾��� Runnable�� ����
			public void run() { 
				gui.udpDisplay.append("[��������]\n");
				gui.udpScrollPane.getVerticalScrollBar().setValue(gui.udpScrollPane.getVerticalScrollBar().getMaximum());
				gui.udpButton.setText("udpStop");
				
				while (true) {
					try {
	                	System.out.println("[���� ����]");
	                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
	                    SocketAddress socketAddress = datagramChannel.receive(byteBuffer);
	                    InetSocketAddress in = (InetSocketAddress) socketAddress;
	                    byteBuffer.flip();
	                    String data = charset.decode(byteBuffer).toString();
	                    
	                    String message = "[�ּ�]: " + socketAddress.toString() + "\n[����]: " + data;
	                    gui.udpDisplay.append(message+"\n");
	                    gui.udpScrollPane.getVerticalScrollBar().setValue(gui.udpScrollPane.getVerticalScrollBar().getMaximum());
	                    
	                    
	                    if(data.matches("^@udpCreate.*")) {
	                    	data = data.substring(10, data.length());
	                    	
		                    for(roomInfo list : ServerGui.roomList) {
		                    	if(list.getUserID()[0].equals(data)) {
		                    		list.setP1Address(in);
		                    	}
		                    }
	                    } else if(data.matches("^@udpEnter.*")) {
	                    	data = data.substring(9, data.length());
	                    	String p2ID = data;
		                    for(roomInfo list : ServerGui.roomList) {
		                    	if(list.getUserID()[1].equals(p2ID)) {
		                    		list.setP2Address(in);
		                    		for(int i=0; i<3; i++) {
			                    		udpSend("@p2p"+list.getP1Address().toString(),list.getP2Address()); //�����ڿ��� �����ּҾ˷���
			                    		udpSend("@p2p"+list.getP2Address().toString(),list.getP1Address());
		                    		}
		                    	}
		                    }
	                    } else if (data.matches("^@delRoom.*")) {
							data = data.substring(8, data.length());
							StringTokenizer infos = new StringTokenizer(data, "#", false);
							String roomName = infos.nextToken();
							String userID = infos.nextToken();
							gui.TCP.resetRoom(roomName, userID);
						}
					} catch(Exception e) {
						System.out.println(e);
						if(datagramChannel.isOpen()) //���� �߻��� serverSocketChannel�� ���� �ִ��� Ȯ���Ѵ�.
							stopServer(); //stopServer()ȣ��
						break; //���� ������ �����Ų��.
					}
                }
				
			};
		};
		executorService.submit(runnable); //���� ���� �۾��� ������Ǯ���� ó���ϱ� ���� excutorService�� submit()�� ȣ���Ѵ�.
	}
	
	void udpSend(String data, InetSocketAddress address) { //������ ���� �ڵ�
		Runnable runnable = new Runnable() { //�����͸� Ŭ���̾�Ʈ�� ������ �۾��� Runnable�� �����Ѵ�.
			public void run() {
					try {
						ByteBuffer byteBuffer = charset.encode(data); //�Ű������� ���� data ���ڿ��κ��� UTF-8�� ���ڵ��� ByteBuffer�� ��´�.
						datagramChannel.send(byteBuffer, address);
					} catch (IOException e) {
						gui.udpDisplay.append("[UDP ���� ����]\n");
						gui.udpScrollPane.getVerticalScrollBar().setValue(gui.udpScrollPane.getVerticalScrollBar().getMaximum());
					}

			}
		};
		executorService.submit(runnable); //������Ǯ���� ó���ϱ� ���� submit()�� ȣ���Ѵ�.
	}
	
	void stopServer() { //���� ���� �ڵ�
		try {			
			if(datagramChannel!=null && datagramChannel.isOpen()) //serverSocketChannel�� null�� �ƴϰ� ����������
				datagramChannel.close(); //serverSocketChannel�� �ݴ´�.
			
			if(executorService!=null && !executorService.isShutdown()) //excutorService�� null�� �ƴϰ� ������°� �ƴϸ� 
				executorService.shutdown(); //excutorService�� �����Ѵ�.
			
			gui.udpDisplay.append("[���� ����]\n");
			gui.udpScrollPane.getVerticalScrollBar().setValue(gui.udpScrollPane.getVerticalScrollBar().getMaximum());
			gui.udpButton.setText("udpStart");
		} catch (Exception e) {}
	}
}
