package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.window.Game;
import com.babytemple.window.Handler;
import com.babytemple.window.Game.STATE;

public class 스위치 extends GameObject {
	private BufferedImage[] img = new BufferedImage[2];
	private Stream<GameObject> stream;
	private Handler handler;
	private GameObject Player1;
	private GameObject Player2;
	private GameObject moveBlock;
	private int type = 0;
	public 스위치(int x, int y, ID id, Handler handler, int typeInt) {
		super(x, y, id);
		img[0] = new BufferedImageLoader().loadImage("/switch_on.png");
		img[1] = new BufferedImageLoader().loadImage("/switch.png");
		this.handler = handler;
		this.typeInt = typeInt; //이거에 따라 움직이는게 다름
	}

	public void tick() {
		if(Game.gameState == STATE.Game) {
			stream = handler.getObject().parallelStream();
			stream.forEach(e -> {
				if(e.getType()==ID.Angel)
					Player1 = e;
				if(e.getType()==ID.Devil)
					Player2 = e;
				if(e.getId()==ID.moveBlock)
					moveBlock = e;
			});
			if (Player1 != null && Player2 != null && typeInt == 0)
				Collision_스위치(Player1, Player2);
			else if (Player1 != null && Player2 != null && typeInt == 1)
				Collision_도어();
		}
	}
	
	public void Collision_도어() {
		stream = handler.getObject().parallelStream();
		if(getBounds().intersects(Player1.getBounds()) || getBounds().intersects(Player2.getBounds()) || getBounds().intersects(moveBlock.getBounds())) {
			type = 0;
			stream.forEach(e -> {
				if(e.getId() == ID.Door) {
					e.setFalling(false);
					e.setJumping(false);
				}
			});
		}else if(!getBounds().intersects(Player1.getBounds()) && !getBounds().intersects(Player2.getBounds()) && !getBounds().intersects(moveBlock.getBounds()) ) {
			type = 1;
			stream.forEach(e -> {
				if(e.getId() == ID.Door) {
					e.setFalling(true);
					e.setJumping(true);
				}
			});
		}
		
}
	
	public void Collision_스위치(GameObject Player1,GameObject Player2) {
			stream = handler.getObject().parallelStream();
			if(getBounds().intersects(Player1.getBounds()) || getBounds().intersects(Player2.getBounds())) {
				type = 0;
				stream.forEach(e -> {
					if(e.getId() == ID.EV && e.getY() < 450) {
						e.setVelY(1);
					}
					else if(e.getId() == ID.EV) {
						e.setVelY(0);
					}
				});
			}else if(!getBounds().intersects(Player1.getBounds()) && !getBounds().intersects(Player2.getBounds())) {
				type = 1;
				stream.forEach(e -> {
					if(e.getId() == ID.EV && e.getY() > 360)
						e.setVelY(-1);
					else if(e.getId() == ID.EV) 
						e.setVelY(0);
				});
			}
			
	}
	
	public void render(Graphics g) {
		g.drawImage(img[type], x, y, 30, 20, null);
	}

	public Rectangle getBounds() {
		return new Rectangle(x,y,30,20);
	}

}
