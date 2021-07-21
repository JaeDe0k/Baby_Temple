package com.babytemple.login;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BT_USER_DB;
import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.window.Game;
import com.babytemple.window.Game.STATE;

public class Login extends MouseAdapter {
	private Game game;
	private Font fontA;
	private boolean join = false;
	private boolean pwFind = false;
	private int result = -5; // 1����, 0�������, -1���̵����, -2 DB����
	private int x = 480; // �α��� ��ġ x
	private int y = 550; // �α��� ��ġ y
	private int type = -1; // 0 NICK, 1 PW, -1 �Է¾���
	private String nickText = "";
	private String pwView = "";
	private String pwText = "";
	private String J_nickText = "";
	private String J_pwText = "";
	private String Sresult="";
	private int idWhidt, height;
	private BufferedImageLoader loader = new BufferedImageLoader();
	private BufferedImage logo_img = loader.loadImage("/logo.png"); // �ΰ�
	private BufferedImage Join_img = loader.loadImage("/pop-up.png"); // �˾����
	private BufferedImage Devil = loader.loadImage("/Login_Devil.png");
	private BufferedImage Devil_LEFT = loader.loadImage("/Login_Devil_EVE_LEFT.png");
	private BufferedImage Devil_RIGHT = loader.loadImage("/Login_Devil_EVE_RIGHT.png");
	private BufferedImage Devil_MOUSE = loader.loadImage("/Login_Devil_EVE_MOUSE.png");
	private BufferedImage Devil_EVE_BLACK = loader.loadImage("/Login_Devil_EVE_BLACK.png");
	private BT_USER_DB userDB;
	public Login(Game game, BT_USER_DB userDB) {
		this.userDB = userDB;
		this.game = game;
		this.fontA = game.fontA;
	}

	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		
		if(Game.gameState == STATE.Login) {
			//ȸ����������
			if(join) {
				if(mouseOver(mx, my, x+26, y+110, 90, 34)) {
					System.out.println("���� Ŭ��");
					join_result();
					if(result == 1) {
						join = false;
						nickText = J_nickText;
						result = -5;
						J_nickText = "";
						J_pwText = "";
						Sresult="";
					}
				} else if(mouseOver(mx, my, x+137, y+110, 90, 34)) {
					System.out.println("��� Ŭ��");
					type = -1;
					J_nickText = "";
					J_pwText = "";
					join = false;
					Sresult="";
				} else if(mouseOver(mx, my, x+27, y+20, 200, 32)) {
					type = 0;
					System.out.println("�г��� �Է�ĭ Ŭ��");
					Sresult="";
				} else if(mouseOver(mx, my, x+27, y+60, 200, 32)) {
					type = 1;
					System.out.println("PW�Է�ĭ Ŭ��");
					Sresult="";
				}  else {
					type = -1;
					Sresult="";
				}
			} 
			
			if(pwFind) { //��ȣã�� �κ�
				if(mouseOver(mx, my, x+26, y+110, 90, 34)) {
					System.out.println("ã�� Ŭ��");
					pwFind_result();
				} else if(mouseOver(mx, my, x+137, y+110, 90, 34)) {
					System.out.println("��� Ŭ��");
					type = -1;
					J_nickText = "";
					J_pwText = "";
					pwFind = false;
				} else if(mouseOver(mx, my, x+27, y+20, 200, 32)) {
					type = 0;
					System.out.println("ã�� �г��� �Է�ĭ Ŭ��");
				} else {
					type = -1;
				}
			}
			
			if(!join && !pwFind) { //�α��κκ�
				if(mouseOver(mx, my, x+221, y, 100, 71)) {
					login_result();
					if(result >= 0) {
						Game.userIcon = result;
						game.getMenu(); //�޴� ���콺������ �߰�
						game.loginRemove(); //�α��� Ű���帮���� ����
						Game.gameState = STATE.Menu; //�޴���
						Game.userID = nickText;
						String data = "@nick"+nickText;
						game.getWindow().getTcpClient().send(data);
					}
				} else if(mouseOver(mx, my, x, y, 200, 32)) {
					type = 0;
					System.out.println("ID�Է�ĭ Ŭ��");
					Sresult="";
				} else if(mouseOver(mx, my, x, y+40, 200, 32)) {
					type = 1;
					System.out.println("PW�Է�ĭ Ŭ��");
					Sresult="";
				} else if(mouseOver(mx, my, x+171, y+80, 150, 34)) {
					System.out.println("ȸ������ Ŭ��");
					nickText = "";
					pwText = "";
					type = -1;
					result = -5;
					join = true;
					Sresult="";
				} else if(mouseOver(mx, my, x, y + 80, 150, 34)) {
					System.out.println("��ȣã�� Ŭ��");
					nickText = "";
					pwText = "";
					type = -1;
					result = -5;
					pwFind = true;
					Sresult="";
				} else {
					type = -1;
					Sresult="";
				}
			}
		}
	}
	
	
	private void login_result() {
		result = userDB.login(nickText, pwText);
		System.out.println(result);
		if (result == -1) {
			Sresult = "���̵����";
			pwText = "";
		}
		if (result == -2) {
			Sresult = "��й�ȣ����";
			pwText = "";
		}
		if (result >= 0) {
			Sresult = "";
		}
	}
	
	private void join_result() {
		BT_USER_DB userDB = new BT_USER_DB();
		result = userDB.join(J_nickText,J_pwText);
		if(result == -1) {
			Sresult = "���̵��ߺ�";
		}
	}
	
	private void pwFind_result() {
		BT_USER_DB userDB = new BT_USER_DB();
		J_pwText = userDB.pwFind(J_nickText);
	}

	private boolean mouseOver(int mx, int my, int x, int y, int w, int h) {
		if (mx > x && mx < x + w) {
			if (my > y && my < y + h) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public void tick() {
	}

	public void render(Graphics g) {
		if (Game.gameState == STATE.Login) {
			if (type == 0)
				g.drawRect(x - 1, y - 1, 201, 33);
			else if (type == 1)
				g.drawRect(x - 1, y + 39, 201, 33);

			// �ΰ�
			g.drawImage(logo_img, 350, 30, 500, 250, null);
			
			g.drawImage(Devil, 460, 300, null);
			
			g.drawImage(Devil_LEFT, 530+idWhidt/2, 400+height, null);
			g.drawImage(Devil_RIGHT, 530+42+idWhidt/2, 400+height, null);
			g.drawImage(Devil_MOUSE, 530+8+idWhidt/2, 400+32+height, 50, 25, null);
			
			if(type == 0) {
				idWhidt = g.getFontMetrics().stringWidth(nickText+J_nickText);
				if(530+idWhidt/2 > 545 && 530+idWhidt/2 < 560)
					height = 1;
				else
					height = 0;
				if(idWhidt > 80)
					idWhidt = 80;
			} else if(type == 1) { //80
				idWhidt = g.getFontMetrics().stringWidth(pwText+J_pwText);
				g.drawImage(Devil_EVE_BLACK, 500, 395, null);
				height=0;
				if(idWhidt > 80)
					idWhidt = 80;
			} else {
				idWhidt = 42;
				height = -20;
			}
			
			
			
			
			// ID�κ�
			g.setColor(new Color(255, 255, 255));
			g.fillRect(x, y, 200, 32);
			g.setFont(fontA.deriveFont(23f));
			g.setColor(new Color(50, 50, 50));
			g.drawString(nickText, x + 10, y + 24);
			g.drawString("�г���", x - 70, y + 22);

			// PW�κ�
			g.setColor(new Color(255, 255, 255));
			g.fillRect(x, y + 40, 200, 32);
			g.setFont(fontA.deriveFont(23f));
			g.setColor(new Color(50, 50, 50));
			pwView = "";
			for(int i=0;i < pwText.length() ; i++)
				pwView += '*';
			g.drawString(pwView, x + 10, y + 64);
			g.drawString("��ȣ", x - 63, y + 62);

			// �α���
			g.setColor(Color.BLACK);
			g.drawRect(x + 221, y, 100, 71);
			g.setFont(fontA.deriveFont(35f));
			g.drawString("�α���", x + 229, y + 47);

			// ID/PWã��
			g.setColor(Color.BLACK);
			g.drawRect(x, y + 80, 150, 34);
			g.setFont(fontA.deriveFont(28f));
			g.drawString("��ȣã��", x + 31, y + 107);

			// ȸ������
			g.setColor(Color.BLACK);
			g.drawRect(x + 171, y + 80, 150, 34);
			g.setFont(fontA.deriveFont(28f));
			g.drawString("ȸ������", x + 199, y + 107);

			if (join) {
				g.drawImage(Join_img, x - 118, y - 50, null);
				
				if (type == 0)
					g.drawRect(x + 26, y + 19, 201, 33);
				else if (type == 1)
					g.drawRect(x + 26, y + 59, 201, 33);
				
				// ���� �г���
				g.setColor(new Color(255, 255, 255));
				g.fillRect(x+27, y+20, 200, 32);
				g.setFont(fontA.deriveFont(23f));
				g.setColor(new Color(50, 50, 50));
				g.drawString(J_nickText, x + 34, y + 42);
				g.drawString("�г���", x-38, y + 40);

				// ���� ��ȣ
				g.setColor(new Color(255, 255, 255));
				g.fillRect(x+27, y+60, 200, 32);
				g.setFont(fontA.deriveFont(23f));
				g.setColor(new Color(50, 50, 50));
				g.drawString(J_pwText, x + 34, y + 82);
				g.drawString("��ȣ", x - 28, y + 80);

				// ����
				g.setColor(Color.BLACK);
				g.drawRect(x+26, y+110, 90, 34);
				g.setFont(fontA.deriveFont(28f));
				g.drawString("����", x + 47, y + 135);

				// ���
				g.setColor(Color.BLACK);
				g.drawRect(x+137, y+110, 90, 34);
				g.setFont(fontA.deriveFont(28f));
				g.drawString("���", x + 162, y + 135);

			} else if (pwFind) {
				g.drawImage(Join_img, x - 118, y - 50, null);
				
				if (type == 0)
					g.drawRect(x + 26, y + 19, 201, 33);
				else if (type == 1)
					g.drawRect(x + 26, y + 59, 201, 33);
				
				// ã�� �г���
				g.setColor(new Color(255, 255, 255));
				g.fillRect(x+27, y+20, 200, 32);
				g.setFont(fontA.deriveFont(23f));
				g.setColor(new Color(50, 50, 50));
				g.drawString(J_nickText, x + 34, y + 42);
				g.drawString("�г���", x-46, y + 40);

				// ã�� ��ȣ
				g.setColor(new Color(0,0,0,80));
				g.fillRect(x+27, y+60, 200, 32);
				g.setFont(fontA.deriveFont(23f));
				g.setColor(new Color(50, 50, 50));
				g.drawString(J_pwText, x + 34, y + 82);
				g.drawString("��ȣ���", x - 55, y + 81);

				// ã��
				g.setColor(Color.BLACK);
				g.drawRect(x+26, y+110, 90, 34);
				g.setFont(fontA.deriveFont(28f));
				g.drawString("ã��", x + 47, y + 135);

				// ������
				g.setColor(Color.BLACK);
				g.drawRect(x+137, y+110, 90, 34);
				g.setFont(fontA.deriveFont(28f));
				g.drawString("������", x + 149, y + 135);
			}
			
			g.setColor(Color.BLACK);
			g.setFont(fontA.deriveFont(50f));
			g.drawString(Sresult, x+(190-g.getFontMetrics().stringWidth(Sresult))/2, y + 255);
		}
	}
	
	public int getType() {
		return type;
	}

	public String getNickText() {
		return nickText;
	}

	public void setNickText(String nickText) {
		this.nickText = nickText;
	}

	public String getPwText() {
		return pwText;
	}

	public void setPwText(String pwText) {
		this.pwText = pwText;
	}

	public String getJ_nickText() {
		return J_nickText;
	}

	public void setJ_nickText(String j_nickText) {
		J_nickText = j_nickText;
	}

	public String getJ_pwText() {
		return J_pwText;
	}

	public void setJ_pwText(String j_pwText) {
		J_pwText = j_pwText;
	}

	public boolean isJoin() {
		return join;
	}
	
	public boolean isPwFind() {
		return pwFind;
	}


}
