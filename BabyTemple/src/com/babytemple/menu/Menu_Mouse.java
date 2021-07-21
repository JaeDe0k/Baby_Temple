package com.babytemple.menu;

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

public class Menu_Mouse extends MouseAdapter {
   private BufferedImageLoader loader = new BufferedImageLoader();
   private BufferedImage rank_img = loader.loadImage("/pop-up.png"); // �˾����
   private Game game;
   private Font fontA;
   private int x = 441;
   private int y = 200;
   private int w = 300;
   private int h = 104;
   private int type = 0; //0�⺻, 1��ŷ, 2����
   private int r_x=260 ;
   private int r_y = 330;
   private String RankList[] = new String[12];
   private BT_USER_DB userDB;
   private int stage=1;

   public Menu_Mouse(Game game, BT_USER_DB userDB) {
      this.game = game;
      this.fontA = game.fontA;
      this.userDB = userDB;
   }
   
   public void mousePressed(MouseEvent e) {
      int mx = e.getX();
      int my = e.getY();
      if(Game.gameState == STATE.Menu) {
         if (type == 0) {
            if(mouseOver(mx, my, x, y, w, h)) {
            	game.removeMouseListener(this);
                game.getbasicRoom();
            	Game.gameState = STATE.Room;
                System.out.println("���ӻ���");
            }
            
            if(mouseOver(mx, my, x, y+450, w, h)) {
               game.removeMouseListener(this);
               game.getLogin();
               Game.gameState = STATE.Login;
               Game.userID = "";
				String data = "@nick"+Game.userID;
				game.getWindow().getTcpClient().send(data);
               System.out.println("�α׾ƿ�");
            }
            
            if(mouseOver(mx, my, x, y+150, w, h)) {
            	selectRank();
            	type=1;
            	System.out.println("��ŷ");
            }
            
            if(mouseOver(mx, my, x, y+300, w, h)) {
            	type=2;
            	System.out.println("����");
            }
            
         } else if (type == 1) {
            if(mouseOver(mx, my, 980, 250, 30, 30)) {
               System.out.println("���ư���");
               type=0;
            }
            if(mouseOver(mx, my, 342, 620, 120, 50)) {
                System.out.println("��������1 ��ŷ");
                stage=1;
                selectRank();
            }
            if(mouseOver(mx, my, 545, 620, 120, 50)) {
                System.out.println("��������2 ��ŷ");
                stage=2;
                selectRank();
            }
            if(mouseOver(mx, my, 745, 620, 120, 50)) {
                System.out.println("��������3 ��ŷ");
                stage=3;
                selectRank();
            }
         } else if (type == 2) {
        	 if(mouseOver(mx, my, 980, 250, 30, 30)) {
        		 System.out.println("���ư���");
                 type=0;
             }
        	 
         }
         
      }
   }
   
   public void selectRank() {
	   RankList = userDB.selectRank(stage, Game.userID);
   }
   
   private boolean mouseOver(int mx, int my, int x, int y, int w, int h) {
      if(mx > x && mx < x + w) {
         if(my > y && my < y + h) {
            return true;
         }else return false;
      }else return false;
   }
   
   public void render(Graphics g) {      
      if(Game.gameState == STATE.Menu) {
         if (type == 0) {
         //����
         g.setColor(Color.BLACK);
         g.drawRect(x, y, w, h);
         g.setFont(fontA.deriveFont(50f));
         g.drawString("���ӽ���", 500, 270);
        
         //��ŷ
         g.setColor(Color.BLACK);
         g.drawRect(x, y+150, w, h);
         g.setFont(fontA.deriveFont(50f));
         g.drawString("��ŷ", 550, 420);
         
         //�ɼ�
         g.setColor(Color.BLACK);
         g.drawRect(x, y+300, w, h);
         g.setFont(fontA.deriveFont(50f));
         g.drawString("����", 535, 570);
         
         //������
         g.setColor(Color.BLACK);
         g.drawRect(x, y+450, w, h);
         g.setFont(fontA.deriveFont(50f));
         g.drawString("�α׾ƿ�", 515, 720);
         
         /* �߾Ӽ� 
         g.setColor(Color.RED);
         g.drawRect(0, 0, 1183, 960);
         g.drawRect(0, 0, 1183/2, 960); 
         */
         } else if (type == 1) { 
            g.drawImage(rank_img, 100, 200, 1000, 500, null);
            g.setFont(fontA.deriveFont(50f));
            g.drawString("��ŷ",r_x+300,r_y-60);
            g.setFont(fontA.deriveFont(30f));
            g.drawString("����",r_x,r_y);
            g.drawString("Ŭ����Ÿ��",r_x+155,r_y);
            g.drawString("���̵�",r_x+378,r_y);
            g.drawString("1��",r_x,r_y+40);
            g.drawString(RankList[0]+"",r_x+155,r_y+40);
            g.drawString(RankList[1]+"",r_x+378,r_y+40);
            g.drawString("2��",r_x,r_y+80);
            g.drawString(RankList[2]+"",r_x+155,r_y+80);
            g.drawString(RankList[3]+"",r_x+378,r_y+80);
            g.drawString("3��",r_x,r_y+120);
            g.drawString(RankList[4]+"",r_x+155,r_y+120);
            g.drawString(RankList[5]+"",r_x+378,r_y+120);
            g.drawString("4��",r_x,r_y+160);
            g.drawString(RankList[6]+"",r_x+155,r_y+160);
            g.drawString(RankList[7]+"",r_x+378,r_y+160);
            g.drawString("5��",r_x,r_y+200);
            g.drawString(RankList[8]+"",r_x+155,r_y+200);
            g.drawString(RankList[9]+"",r_x+378,r_y+200);
            g.drawString("�� ����",r_x+190,r_y+260);
            g.drawString(RankList[10]+"��",r_x+305,r_y+260);
            g.drawString(RankList[11]+"",r_x+390,r_y+260);
            
            g.drawRect(342, 620, 120, 50);
            g.drawString("��������1",350, 650);
            g.drawRect(545, 620, 120, 50);
            g.drawString("��������2",550, 650);
            g.drawRect(745, 620, 120, 50);
            g.drawString("��������3",750, 650);
            
            g.drawString("X",988, 272);
//            g.drawRect(980, 250, 30, 30); //X����
         } else if(type == 2) {
        	 g.drawImage(rank_img, 100, 200, 1000, 500, null);
        	 g.setFont(fontA.deriveFont(50f));
             g.drawString("����",r_x+290,r_y-60);
             
             g.setFont(fontA.deriveFont(40f));
             g.drawString("����Ű",150,320);
             g.drawString("õ��",150,370);
             g.drawString(":",220,370);
             g.drawString("���",245,370);
             g.drawString("����",345,370);
             g.drawString("����",445,370);
             g.drawString("����",545,370);
             
             g.drawString("�Ǹ�",150,420);
             g.drawString(":",220,420);
             g.drawString("W ��",248,420);
             g.drawString("S ��",352,420);
             g.drawString("A ��",452,420);
             g.drawString("D ��",552,420);
             
             
             g.drawString("���ӹ��",150,500);
             g.drawString("���� ��",150,600);
             
             g.setFont(fontA.deriveFont(27f));
             
             g.drawString("�Ʊ� õ��� �Ʊ� �Ǹ��� ������ ������ ������  ������ ���� ��� �����Ͽ� "
              		+ "Ż���ϴ� �̾߱�! ",160,535);             
             g.drawString("����Ű�� ĳ���͸� ������ ���� ������� ��ġ���� �̿��� Ŭ���� ������ "
              		+ "�����ϸ� Ŭ����!",160,635);

             
             g.drawString("X",988, 272);
//             g.drawRect(980, 250, 30, 30); //X����
         }
      }
   }



}