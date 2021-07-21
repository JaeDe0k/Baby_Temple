package com.babytemple.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.window.Game;
import com.babytemple.window.Game.STATE;
import com.babytemple.window.Handler;
import com.babytemple.window.Window;

public class isClear extends MouseAdapter {
	private Game game;
	private Window window;
	private BufferedImage gameover = null;
	private BufferedImage clearStar1 = null;
	private BufferedImage clearStar2 = null;
	private BufferedImage clearStar3 = null;
	private BufferedImageLoader loader = new BufferedImageLoader();
	
	public isClear(Game game, Window window){
		this.game = game;
		this.window = window;
		gameover = loader.loadImage("/gameover.png");
		clearStar1 = loader.loadImage("/clearStar1.png");
		clearStar2 = loader.loadImage("/clearStar2.png");
		clearStar3 = loader.loadImage("/clearStar3.png");
	}
	
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		
		if(Game.gameState == STATE.Game && Handler.clear == true) {
			if(mouseOver(mx, my, 707, 201, 41, 42)) {
				System.out.println("나가기");
				gameExit();
			} else if (mouseOver(mx, my, 499, 606, 198, 67) && Handler.death  && Game.p1ID == Game.userID) {
				replay();
			} else {
				if (mouseOver(mx, my, 473, 606, 83, 81) && Game.p1ID == Game.userID)
					replay();
				if (mouseOver(mx, my, 570, 612, 156, 69) && Game.p1ID == Game.userID)
					nextLevel();
			}
			
			
		}
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
	
	public void render(Graphics g) {
		if(Handler.death) {
			g.setColor(new Color(255,0,0,100));
			g.fillRect(0, 0, 1200, 960);
			g.drawImage(gameover, 450, 200, null);
//			g.drawRect(499, 606, 198, 67); //리플레이
		} else if(Handler.score < 3) {
			g.setColor(new Color(255,255,255,100));
			g.fillRect(0, 0, 1200, 960);
			g.drawImage(clearStar1, 450, 200, null);
			g.setColor(Color.RED);
//			g.drawRect(473, 606, 83, 81); //리플레이
//			g.drawRect(570, 612, 156, 69); //넥스트
		} else if(Handler.score >= 3) {
			g.setColor(new Color(255,255,255,100));
			g.fillRect(0, 0, 1200, 960);
			g.drawImage(clearStar2, 450, 200, null);
		} else if(Handler.score >= 6) {
			g.setColor(new Color(255,255,255,100));
			g.fillRect(0, 0, 1200, 960);
			g.drawImage(clearStar3, 450, 200, null);
		}
		
//		g.drawRect(707, 201, 41, 42); //공통 X영역
	}
	
	public void gameExit() {
		window.getUdpClient().send("@GExit");
		
		game.gameExit();
	}
	
	public void replay() {
		window.getUdpClient().send("@replay");
		game.clearLevel();
		game.switchLevel(Game.LEVEL);
	}
	
	public void nextLevel() {
		window.getUdpClient().send("@nextLevel");
		if(Game.LEVEL != 3)
			Game.LEVEL += 1;
		game.switchLevel(Game.LEVEL);
	}
}
