package com.babytemple.window;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.babytemple.framework.BT_USER_DB;
import com.babytemple.network.tcpClient;
import com.babytemple.network.udpClient;
import com.babytemple.room.RoomList;
import com.babytemple.window.Game.STATE;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	private int extendWidth = 350; //Ȯ��ũ��
	private static boolean extend = false;
	private Game game;
	private Font fontA, fontB;
	private tcpClient tcpclient;
	private udpClient udpclient;
	public ChatPanel ChatP;
	public JScrollPane room_List;
	public JScrollPane user_List;
	public JScrollPane ViewTextTeam;
	public JScrollPane ViewText;
	public JTextField textfield;
	public JTextField textRoomField;
	public JTextField textfieldTeam;
	public JTextArea textarea;
	public JTextArea textareaTeam;
	public JList<String> userList;
	public JList<String> roomList;
	
	public Window(int width, int height, String title, Game game) {
		tcpclient = new tcpClient(this); //Ŭ���̾�Ʈ ����
		udpclient = new udpClient(this);
		udpclient.receive();
		this.game = game;
		this.setLayout(null); //��ġ������ ����
		getContentPane().setBackground(new Color(255,230,153)); //�����ӹ���
		
		/*	                  DB���� ����                                           */
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if(Game.gameState == STATE.Game) {
            		udpclient.send("@GExit");
            		game.gameExit();
            		String data = "@delRoom#"+game.RgetRoom().getRoomName()+"#"+Game.userID;
    				game.getWindow().getTcpClient().send(data); //�����������

    				Game.p1ID="";
    				Game.p2ID="";
    				Game.p1Icon=0;
    				Game.p2Icon=0;
    				game.getWindow().getUdpClient().send("#reset"+Game.userID);
            		game.RgetRoom().outRemoveRoom();
            	} else if(RoomList.roomStatus == 3) {
            		udpclient.send("#reset"+Game.userID);
            		String data = "@delRoom#"+game.RgetRoom().getRoomName()+"#"+Game.userID;
                	tcpclient.send(data); //�����������
            	}
            	
                BT_USER_DB db = game.getUserDB();
                db.close();
                tcpclient.stopClient();
                udpclient.stopClient();
            }
        });
        
		//�ش簪���� â ũ�� ����
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));
		this.pack();
		this.fontA = game.fontA;
		this.fontB = game.fontB;
		
		/*             ���� ���� ä�� ����                      */
		ChatP = new ChatPanel(game, this);
		this.add(ChatP);
		//////////////////////////////////
		
		createRoom();
				
		//���� ���
		game.setBounds(0, 0, 1200, 960);
		this.add(game);
		//ũ�� ���� ����
		this.setResizable(false);
		//â �ݱ� (���α׷� ����)
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//�����츦 �Ű����� ���� ������Ʈ�� ���� ������� ��ġ�� ��������. (null == ������ â�� ����� ���)
		this.setLocationRelativeTo(null);
		//�Ű��������� ���� ������� ǥ�� �� ����
		this.setVisible(true);
		
	}
	
	private void createRoom() {
		Border lineBorder1 = BorderFactory.createLineBorder(new Color(255,222,121), 1); //3px�� �׵θ��� ����
		Border lineBorder2 = BorderFactory.createLineBorder(Color.BLACK, 3); //3px�� �׵θ��� ����
		Border emptyBorder1 = BorderFactory.createEmptyBorder(0, 5, 0, 0); //���� ���հ�輱 �߰�
		Border emptyBorder2 = BorderFactory.createEmptyBorder(0, 0, 0, 0); //���� ���հ�輱 �߰�
		
		/*                           �� ���                                                                                                                      */
		roomList = new JList<String>();
		roomList.setFont(fontA.deriveFont(30f));
		room_List = new JScrollPane(roomList,21, 31);
		room_List.setBounds(68, 160, 697, 340);
		room_List.getViewport().getView().setForeground(Color.BLACK);
		room_List.setBorder(BorderFactory.createCompoundBorder(lineBorder1, emptyBorder2));
		
		JScrollBar scrollBar0 = new JScrollBar(JScrollBar.VERTICAL){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {return true;} };
		room_List.setVerticalScrollBar(scrollBar0);
		room_List.getVerticalScrollBar().setUnitIncrement(12);
		room_List.getViewport().getView().setBackground(new Color(255,222,121));
		
		
		/*                           ���� ���                                                                                                                      */
		userList = new JList<String>();
		userList.setFont(fontB.deriveFont(30f));
		user_List = new JScrollPane(userList,21, 31);
		user_List.setBounds(841, 160, 280, 370);
		user_List.getViewport().getView().setForeground(Color.BLACK);
		user_List.setBorder(BorderFactory.createCompoundBorder(lineBorder1, emptyBorder2));
		
		JScrollBar scrollBar1 = new JScrollBar(JScrollBar.VERTICAL){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {return true;} };
		user_List.setVerticalScrollBar(scrollBar1);
		user_List.getVerticalScrollBar().setUnitIncrement(12);
		user_List.getViewport().getView().setBackground(new Color(255,222,121));
		
		/*                           ��üä�� â                                                                                                                      */
		textarea = new JTextArea();
		textarea.setText("��ü ä�ù��Դϴ�.\n");
		ViewText = new JScrollPane(textarea,21, 31);
		textarea.setFont(fontB.deriveFont(18f));
		textarea.setBorder(emptyBorder2);
		textarea.setLineWrap(true);
		textarea.setEditable(false);

		ViewText.getViewport().getView().setForeground(Color.BLACK); //����
		ViewText.setBorder(lineBorder1);
		ViewText.getViewport().getView().setBackground(new Color(255,222,121));

		JScrollBar scrollBar2 = new JScrollBar(JScrollBar.VERTICAL){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {return true;}};
		ViewText.setVerticalScrollBar(scrollBar2);
		ViewText.getVerticalScrollBar().setUnitIncrement(12);
		ViewText.setBounds(64, 623, 705, 190);
		
		/*                           ��üä�� �Է�â                                                                                                                      */
		textfield = new JTextField();
		textfield.addActionListener(e -> {
			String text = textfield.getText();
			tcpclient.send("@chat["+Game.userID+"]: "+text);
			textfield.setText("");
		});
		textfield.setFont(fontB.deriveFont(16f));
		textfield.setForeground(new Color(0,0,0));
		textfield.setBackground(new Color(255,241,0));
		textfield.setBorder(BorderFactory.createCompoundBorder(lineBorder2, emptyBorder1));
		textfield.setBounds(63, 823, 707, 26);
		
		/*                           �� ���� �Է�â                                                                                                                      */
		textRoomField = new JTextField();
		textRoomField.setFont(fontB.deriveFont(16f));
		textRoomField.setForeground(new Color(0,0,0));
		textRoomField.setBackground(new Color(255,255,255));
		textRoomField.setBorder(BorderFactory.createCompoundBorder(lineBorder2, emptyBorder1));
		textRoomField.setBounds(200, 190, 500, 30);
		
		/*                           ��ä�� â                                                                                                                      */
		textareaTeam = new JTextArea();
		textareaTeam.setText("�� ä�ù��Դϴ�.\n");
		ViewTextTeam = new JScrollPane(textareaTeam,21, 31);
		textareaTeam.setFont(fontB.deriveFont(18f));
		textareaTeam.setBorder(emptyBorder2);
		textareaTeam.setLineWrap(true);
		textareaTeam.setEditable(false);

		ViewTextTeam.getViewport().getView().setForeground(Color.BLACK); //����
		ViewTextTeam.setBorder(lineBorder1);
		ViewTextTeam.getViewport().getView().setBackground(new Color(255,222,121));

		JScrollBar scrollBar3 = new JScrollBar(JScrollBar.VERTICAL){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {return true;}};
		ViewTextTeam.setVerticalScrollBar(scrollBar3);
		ViewTextTeam.getVerticalScrollBar().setUnitIncrement(12);
		ViewTextTeam.setBounds(64, 623, 705, 190);
		
		/*                           ��ä�� �Է�â                                                                                                                      */
		textfieldTeam = new JTextField();
		textfieldTeam.addActionListener(e -> {
			String text = textfieldTeam.getText();
			textareaTeam.append("["+Game.userID+"]: "+text+"\n");
			udpclient.send("@chat["+Game.userID+"]: "+text);
			ViewTextTeam.getVerticalScrollBar().setValue(ViewTextTeam.getVerticalScrollBar().getMaximum());
			textfieldTeam.setText("");
		});
		textfieldTeam.setFont(fontB.deriveFont(16f));
		textfieldTeam.setForeground(new Color(0,0,0));
		textfieldTeam.setBackground(new Color(255,241,0));
		textfieldTeam.setBorder(BorderFactory.createCompoundBorder(lineBorder2, emptyBorder1));
		textfieldTeam.setBounds(63, 823, 707, 26);
		/*                      */
		
		this.add(textfield);
		this.add(ViewText);
		this.add(user_List);
		this.add(room_List);
		this.add(textRoomField);
		this.add(ViewTextTeam);
		this.add(textfieldTeam);
		noVisible();
	}
	
	//Ȯ��ġ��
		public void extend() {
			ChatP.repaint();
			Dimension currentSize = this.getSize();
			int nextWidth = (int)currentSize.getWidth() + extendWidth;
			int nextHeight = (int)currentSize.getHeight();
			Dimension afterSize = new Dimension(nextWidth, nextHeight);
			this.setPreferredSize(afterSize);
			this.setMaximumSize(afterSize);
			this.setMinimumSize(afterSize);
			this.pack();
			extend = true;
		}

		//����ġ��
		public void reduce() {
			ChatP.repaint();
			Dimension currentSize = this.getSize();
			int nextWidth = (int)currentSize.getWidth() - extendWidth;
			int nextHeight = (int)currentSize.getHeight();
			Dimension afterSize = new Dimension(nextWidth, nextHeight);
			this.setPreferredSize(afterSize);
			this.setMaximumSize(afterSize);
			this.setMinimumSize(afterSize);
			this.pack();
			extend = false;
		}

		public boolean isExtend() {
			return extend;
		}
		

		public void yesVisible() {
			this.textfield.setVisible(true);
			this.ViewText.setVisible(true);
			this.user_List.setVisible(true);
			this.room_List.setVisible(true);
			pack();
		}

		public void noVisible() {
			this.user_List.setVisible(false);
			this.textfield.setVisible(false);
			this.ViewText.setVisible(false);
			this.room_List.setVisible(false);
			setCreateRoomRemove();
			noTeamchat();
			pack();
		}
		
		public void noTeamchat() {
			this.ViewTextTeam.setVisible(false);
			this.textfieldTeam.setVisible(false);
			pack();
		}
		public void YesTeamchat() {
			this.ViewTextTeam.setVisible(true);
			this.textfieldTeam.setVisible(true);
			pack();
		}
		
		public void setRoomView() {
			this.room_List.setVisible(true);
			pack();
		}
		public void setRoomRemove() {
			this.room_List.setVisible(false);
			pack();
		}
		
		public void setCreateRoomView() {
			this.textRoomField.setVisible(true);
			pack();
		}
		public void setCreateRoomRemove() {
			this.textRoomField.setVisible(false);
			textRoomField.setText("");
			pack();
		}
		public tcpClient getTcpClient() {
			return tcpclient;
		}
		
		public udpClient getUdpClient() {
			return udpclient;
		}
		
		public void restartTcpClient() {
			if(!tcpclient.getSocketChannel().isOpen()) {
				System.out.println("��õ�");
				tcpclient.startClient();
			}
		}

		public Game getGame() {
			return game;
		}
		
}
