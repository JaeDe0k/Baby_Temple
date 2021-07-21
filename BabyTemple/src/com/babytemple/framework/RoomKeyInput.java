package com.babytemple.framework;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;

import com.babytemple.window.Game;
import com.babytemple.window.Handler;
import com.babytemple.window.Window;

public class RoomKeyInput extends KeyAdapter {
	private Handler handler;
	private Window window;
	private Stream<GameObject> stream;
	private String p1Pressed="";
	private String p2Pressed="";
	private String p1Released="";
	private String p2Released="";
	
	public RoomKeyInput (Handler handler, Window window) {
		this.handler = handler;
		this.window = window;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		stream = handler.getObject().parallelStream();
		stream.forEach(tempObject -> {
			//천사키
			if(tempObject.getType() == ID.roomAngel && Game.p1ID == Game.userID) {
				if(key == KeyEvent.VK_W || key == KeyEvent.VK_A ||  key == KeyEvent.VK_D) {
					if(key == KeyEvent.VK_W && !tempObject.isJumping() && tempObject.velY == 0) {
						tempObject.setJumping(true);
						tempObject.setVelY(-20);
					}
					if(key == KeyEvent.VK_A) tempObject.setLeft(true);
					if(key == KeyEvent.VK_D) tempObject.setRight(true);
					String sendKey = String.format("@KeyPressed#P1ROOM#%b#%f#%b#%b#%d#%d", tempObject.isJumping(),tempObject.getVelY(),tempObject.isLeft(),tempObject.isRight(),tempObject.getX(),tempObject.getY());
					if(!sendKey.equals(p1Pressed)) {
						p1Pressed = sendKey;
						p1Released = "";
						window.getUdpClient().send(p1Pressed);
					}
				}	
			}
			
			//악마키
			if(tempObject.getType() == ID.roomDevil && Game.p2ID == Game.userID) {
				if(key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT ||  key == KeyEvent.VK_RIGHT) {
					if(key == KeyEvent.VK_UP && !tempObject.isJumping() && tempObject.velY == 0) {
						tempObject.setJumping(true);
						tempObject.setVelY(-20);
					}
					if(key == KeyEvent.VK_LEFT) tempObject.setLeft(true);
					if(key == KeyEvent.VK_RIGHT) tempObject.setRight(true);
					String sendKey = String.format("@KeyPressed#P2ROOM#%b#%f#%b#%b#%d#%d", tempObject.isJumping(),tempObject.getVelY(),tempObject.isLeft(),tempObject.isRight(),tempObject.getX(),tempObject.getY());
					if(!sendKey.equals(p2Pressed)) {
						p2Pressed = sendKey;
						p2Released = "";
						window.getUdpClient().send(p2Pressed);
					}
				}
			}
		});
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		//오브젝트 확인
		stream = handler.getObject().parallelStream();
		stream.forEach(tempObject -> {
			//천사
			if(tempObject.getType() == ID.roomAngel && Game.p1ID == Game.userID) {
				if(key == KeyEvent.VK_W || key == KeyEvent.VK_A ||  key == KeyEvent.VK_D) {
					if(key == KeyEvent.VK_A) tempObject.setLeft(false);
					if(key == KeyEvent.VK_D) tempObject.setRight(false); 
					
					String sendKey = String.format("@KeyRelease#P1ROOM#%b#%b#%d#%d", tempObject.isLeft(),tempObject.isRight(),tempObject.getX(),tempObject.getY());
					if(!sendKey.equals(p1Released)) {
						p1Released = sendKey;
						p1Pressed = "";
						window.getUdpClient().send(p1Released);
					}
				}
			}
			
			//악마
			if(tempObject.getType() == ID.roomDevil && Game.p2ID == Game.userID) {
				if(key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT ||  key == KeyEvent.VK_RIGHT) {
					if(key == KeyEvent.VK_LEFT) tempObject.setLeft(false);
					if(key == KeyEvent.VK_RIGHT) tempObject.setRight(false); 
					String sendKey = String.format("@KeyRelease#P2ROOM#%b#%b#%d#%d", tempObject.isLeft(),tempObject.isRight(),tempObject.getX(),tempObject.getY());
					if(!sendKey.equals(p2Released)) {
						p2Released = sendKey;
						p2Pressed = "";
						window.getUdpClient().send(p2Released);
					}
				}
			}
		});
	}
}
