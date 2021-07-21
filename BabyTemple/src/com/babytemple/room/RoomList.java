package com.babytemple.room;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.RoomKeyInput;
import com.babytemple.framework.Texture;
import com.babytemple.window.Game;
import com.babytemple.window.Game.STATE;
import com.babytemple.window.Handler;
import com.babytemple.window.Window;

public class RoomList extends MouseAdapter {
	private Handler handler;
	private Font fontA;
	private Window window;
	private Game game;
	public static int roomStatus = 0; //0�⺻ 1�游��� 2�����ܼ���
	public String isfull = "";
	private BufferedImage room_img = null; //�� �̹����� �����ϴ� ��
	private BufferedImage room_null = null; //�� �̹����� �����ϴ� ��
	private BufferedImage room_wait = null; //�� ����
	private BufferedImageLoader loader = new BufferedImageLoader();
	private Texture tex = Game.getInstance();
	private int icon_count = 75;
	private int icon_col = 165;
	private int tempIconNum = Game.userIcon;
	public static int stageLevel = 1;

	private String roomName="";
	private RoomKeyInput handlerKey;
	public RoomList(Game game, Window window, Handler handler) {
		this.game = game;
		this.fontA = game.fontA;
		this.window = window;
		this.handler = handler;
		window.yesVisible();
		room_img = loader.loadImage("/room.png"); //�� ��� ����
		room_wait = loader.loadImage("/waitRoom.png"); //�� ��� ����
		room_null = loader.loadImage("/room_null.png"); //�� ��� ����
		handlerKey = new RoomKeyInput(handler, window);
	}
	
	public void createRoom() {
		handler.loadRoom();
		game.addKeyListener(handlerKey);
		window.noVisible();
		roomStatus = 3;
		window.YesTeamchat();
	}
	
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		
		if(Game.gameState == STATE.Room && roomStatus >= 0 && roomStatus <= 2) {
			if(mouseOver(mx, my, 405, 43, 158, 47)) { //��������
				String data = "@Quickentry"+Game.userID;
				game.getWindow().getTcpClient().send(data);
				window.textarea.setText("");
			} else if(mouseOver(mx, my, 586, 43, 163, 47)) { //���ӻ���
				window.setRoomRemove();
				stageLevel = 1;
				roomStatus = 1;
				window.setCreateRoomView();
				window.textarea.setText("");
			} else if(mouseOver(mx, my, 898, 43, 121, 47)) { //������
				window.noVisible();
				game.removeMouseListener(this); //���� ���콺������ ����
				game.getMenu(); //�޴� ���콺������ �߰�
				Game.gameState = STATE.Menu; //�޴���
			}
			
			if(roomStatus == 0) {
				if (mouseOver(mx, my, 674, 505, 101, 28)) {
					this.roomName = window.roomList.getSelectedValue();
					String data = "@Enterroom#"+this.roomName+"#"+Game.userID;
					game.getWindow().getTcpClient().send(data);
				}
			}
			
			if(roomStatus == 1) {
				//�游�����
				if(mouseOver(mx, my, 380, 350, 150, 80)) {
					roomStatus = 0;
					window.setCreateRoomRemove();
					window.setRoomView();
				} else if(mouseOver(mx,my,100, 250, 150, 50)) {
					stageLevel = 1; //��������1����
					System.out.println("��������1");
				}else if(mouseOver(mx,my,280, 250, 155, 50)) {
					stageLevel = 2; //��������2����
					System.out.println("��������2");
				}else if(mouseOver(mx,my,465, 250, 152, 50)) {
					stageLevel = 3; //��������2����
					System.out.println("��������3");
				}else if(mouseOver(mx, my, 180, 350, 155, 80)) {
					this.roomName = window.textRoomField.getText().replace('#', '@'); //#����
					//�游��� �ֱ�
					String data = "@room#"+this.roomName+"#"+Game.userID;
					game.getWindow().getTcpClient().send(data);
					//////////////////////////////////////////////
				}
			} if(roomStatus != 2) {
				if(mouseOver(mx, my, 844, 817, 274, 52)) { //�����ܼ���
					window.setRoomRemove();
					window.setCreateRoomRemove();
					tempIconNum = Game.userIcon;
					roomStatus = 2;
				}
			} else if(roomStatus == 2) {
				if(mouseOver(mx, my, 844, 817, 274, 52)) {
					window.setRoomView();
					roomStatus = 0;
					Game.userIcon = tempIconNum;
					game.getUserDB().setIcon(Game.userID, Game.userIcon); //������ ����
				}else if(mouseOver(mx, my, 75, 165, 120, 120))
					tempIconNum=0;
				else if(mouseOver(mx, my, 195, 165, 120, 120))
					tempIconNum=1;
				else if(mouseOver(mx, my, 315, 165, 120, 120))
					tempIconNum=2;
				else if(mouseOver(mx, my, 435, 165, 120, 120))
					tempIconNum=3;
				else if(mouseOver(mx, my, 555, 165, 120, 120))
					tempIconNum=4;
				else if(mouseOver(mx, my, 75, 285, 120, 120))
					tempIconNum=5;
				else if(mouseOver(mx, my, 195, 285, 120, 120))
					tempIconNum=6;
				else if(mouseOver(mx, my, 315, 285, 120, 120))
					tempIconNum=7;
				else if(mouseOver(mx, my, 435, 285, 120, 120))
					tempIconNum=8;
				else if(mouseOver(mx, my, 555, 285, 120, 120))
					tempIconNum=9;
				else if(mouseOver(mx, my, 75, 405, 120, 120))
					tempIconNum=10;
				else if(mouseOver(mx, my, 195, 405, 120, 120))
					tempIconNum=11;
				else if(mouseOver(mx, my, 315, 405, 120, 120))
					tempIconNum=12;
				else if(mouseOver(mx, my, 435, 405, 120, 120))
					tempIconNum=13;
				else if(mouseOver(mx, my, 555, 405, 120, 120))
					tempIconNum=14;
				else if(Game.gameState == STATE.Room){
						window.setRoomView();
						roomStatus = 0;
				}	
			}
		} else if(Game.gameState == STATE.Room && roomStatus == 3) {
			if(mouseOver(mx, my, 892, 764, 163, 47) && Game.p1ID == Game.userID && (Game.p1ID != "" && Game.p2ID != "") ) { //���ӽ��ۿ����ֱ�
				window.getUdpClient().send("#gameStart");
				gameStart();
				
			} else if (mouseOver(mx, my, 920, 42, 123, 47)) { //������
				window.textareaTeam.setText("");
				String data = "@delRoom#"+this.roomName+"#"+Game.userID;
				game.getWindow().getTcpClient().send(data); //�����������

				Game.p1ID="";
				Game.p2ID="";
				Game.p1Icon=0;
				Game.p2Icon=0;
				window.getUdpClient().send("#reset"+Game.userID);
				
				outRemoveRoom();
			} else if (mouseOver(mx, my, 890, 692, 37, 47) && Game.p1ID == Game.userID) {
				stageLevel = 1;
				window.getUdpClient().send("#stage1");
			} else if (mouseOver(mx, my, 953, 692, 37, 47) && Game.p1ID == Game.userID) {
				stageLevel = 2;
				window.getUdpClient().send("#stage2");
			} else if (mouseOver(mx, my, 1014, 692, 37, 47) && Game.p1ID == Game.userID) {
				stageLevel = 3;
				window.getUdpClient().send("#stage3");
			}
		}
	}
	
	public void gameStart() {
		handler.clearLevel();
		game.clearLevel();
		game.switchLevel(stageLevel); //�� ��ü ���� �� Block_handlerŬ������ object�� ����
		game.removeKeyListener(handlerKey);
		window.noVisible();
		game.removeMouseListener(this); //���� ���콺������ ����
		game.gameListener();
        Game.gameState = STATE.Game;
        System.out.println("���ӽ���");
	}
	
	public void outRemoveRoom() {
		handler.clearLevel();
		game.removeKeyListener(handlerKey);
		
		window.setRoomRemove();
		window.yesVisible();
		roomStatus = 0;
	}
	
	private boolean mouseOver(int mx, int my, int x, int y, int w, int h) {
		if(mx > x && mx < x + w) {
			if(my > y && my < y + h) {
				return true;
			}else return false;
		}else return false;
	}
	
	public void tick() {
		if(roomStatus == 3);
			handler.tick();
	}
	
	public void render(Graphics g) {
		if(Game.gameState == STATE.Room && roomStatus == 3) {
			g.drawImage(room_wait, 5, 5, null);
			g.setFont(fontA.deriveFont(21.5f));
			g.drawString(roomName,142,139);
			
			handler.render(g);
			
			g.drawImage(tex.Icon[Game.p1Icon], 870, 215, 100, 100, null);
			g.setFont(fontA.deriveFont(30f));
			g.drawString(Game.p1ID, 870+(100-g.getFontMetrics().stringWidth(Game.p1ID))/2, 340);
			g.drawImage(tex.Icon[Game.p2Icon], 870, 370, 100, 100, null);
			g.drawString(Game.p2ID, 870+(100-g.getFontMetrics().stringWidth(Game.p2ID))/2, 495);
			
			g.setFont(fontA.deriveFont(50f));
			g.drawString("õ��", 1000, 270);
			g.drawString("�Ǹ�", 1000, 430);
			
			g.setFont(fontA.deriveFont(48f));
			g.drawString("��������", 900, 660);
			
			if(stageLevel == 1)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			g.drawString("1",902,730);

			if(stageLevel == 2)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			g.drawString("2",960,730);
			
			if(stageLevel == 3)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			g.drawString("3",1022,730);
			
			g.setColor(Color.BLACK);
			if(Game.p1ID == Game.userID) {
				g.drawString("���ӽ���", 895, 800);
				g.setFont(fontA.deriveFont(21.5f));
			} else
				g.drawString("�����", 915, 800);
//			g.drawRect(892, 764, 163, 47); //���ӽ��� ����
//			g.drawRect(920, 42, 123, 47); //������ ����
//			g.drawRect(890, 692, 37, 47); //1 ����
//			g.drawRect(953, 692, 37, 47); //2 ����
//			g.drawRect(1014, 692, 37, 47); //3 ����
			
		}
		
		if(Game.gameState == STATE.Room && roomStatus >= 0 && roomStatus <= 2) {
			if (roomStatus == 0) {
				g.setColor(Color.BLACK);
				g.drawImage(room_img, 5, 5, null);
				g.setFont(fontA.deriveFont(22f));
				g.drawString(isfull,70,530);
//				g.drawRect(405, 43, 158, 47); //�������ۿ���
//				g.drawRect(586, 43, 163, 47); //���ӻ�������
//				g.drawRect(898, 43, 121, 47); //�����⿵��
//				g.drawRect(674, 505, 101, 28); //�����ϱ⿵��
			}else if(roomStatus == 1) {
				g.drawImage(room_null, 5, 5, null);
				g.setFont(fontA.deriveFont(22f));
				g.drawString("�� ���� ����",52,139);

				g.setFont(fontA.deriveFont(30f));
				g.drawString("�� ���� ",100,210);
				
				g.setFont(fontA.deriveFont(40f));
				if(stageLevel == 1)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);
				g.drawRect(100, 250, 150, 50);
				g.drawString("��������1",107,285);

				if(stageLevel == 2)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);
				g.drawRect(280, 250, 155, 50);
				g.drawString("��������2",287,285);
				
				if(stageLevel == 3)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLACK);
				g.drawRect(465, 250, 152, 50);
				g.drawString("��������3",470,285);
				
				g.setColor(Color.BLACK);
				g.drawRect(180, 350, 155, 80);
				g.drawString("���� ����",185,400);
				
				g.drawRect(380, 350, 150, 80);
				g.drawString("���� ���",385,400);
				
			} else if (roomStatus == 2) {
				g.drawImage(room_null, 5, 5, null);
				
				g.setFont(fontA.deriveFont(21.5f));
				g.drawString("�� ������ ����",52,139);
				
				icon_count = 75;
				icon_col = 165;
				
				for(int i=0;i<15;i++) {
					g.drawImage(tex.Icon[i], icon_count, icon_col, 120, 120, null);
					if(icon_count == 555) {
						icon_count = 75;
						icon_col += 120;
					} else
						icon_count += 120;
				}
//				������ ���� ��ġ
//				g.drawImage(tex.Icon[0], 75, 165, 120, 120, null);
//				g.drawImage(tex.Icon[1], 195, 165, 120, 120, null);
//				g.drawImage(tex.Icon[2], 315, 165, 120, 120, null);
//				g.drawImage(tex.Icon[3], 435, 165, 120, 120, null);
//				g.drawImage(tex.Icon[4], 555, 165, 120, 120, null);
//				g.drawImage(tex.Icon[5], 75, 285, 120, 120, null);
//				g.drawImage(tex.Icon[6], 195, 285, 120, 120, null);
//				g.drawImage(tex.Icon[7], 315, 285, 120, 120, null);
//				g.drawImage(tex.Icon[8], 435, 285, 120, 120, null);
//				g.drawImage(tex.Icon[9], 555, 285, 120, 120, null);
//				g.drawImage(tex.Icon[10], 75, 405, 120, 120, null);
//				g.drawImage(tex.Icon[11], 195, 405, 120, 120, null);
//				g.drawImage(tex.Icon[12], 315, 405, 120, 120, null);
//				g.drawImage(tex.Icon[13], 435, 405, 120, 120, null);
//				g.drawImage(tex.Icon[14], 555, 405, 120, 120, null);
				
//				g.drawRect(844, 817, 274, 52); //�����ܹ�ư����
				g.drawImage(tex.Icon[tempIconNum], 912, 608, 140, 140, null);
				g.setFont(fontA.deriveFont(30f));
				g.drawString(Game.userID, 912+(140-g.getFontMetrics().stringWidth(Game.userID))/2, 780);
			}
			if (roomStatus != 2) {
//				g.drawRect(844, 817, 274, 52); //�����ܹ�ư����
				g.drawImage(tex.Icon[Game.userIcon], 912, 608, 140, 140, null);
				g.setFont(fontA.deriveFont(30f));
				g.drawString(Game.userID, 912+(140-g.getFontMetrics().stringWidth(Game.userID))/2, 780);
				
			}
//			g.drawRect(37, 116, 758, 436); //���� ��翵�� ���
		}
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
