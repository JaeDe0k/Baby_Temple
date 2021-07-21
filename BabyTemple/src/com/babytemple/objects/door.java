package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;

public class door extends GameObject {
	private BufferedImage img = new BufferedImageLoader().loadImage("/red.png");
	public door(int x, int y, ID id) {
		super(x, y, id);
		falling = true;
	}

	//값변경
	public void tick() {
		
	}

	//화면에 표시해주는거
	public void render(Graphics g) {
		if(falling) 
			g.drawImage(img, 350, 360, 10, 60, null);
	}

	public Rectangle getBounds() {
		if(jumping)
			return new Rectangle(350, 360, 10, 60);
		else
			return new Rectangle(0, 0, 0, 0);
	}

}