package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;

public class ev extends GameObject {
	private BufferedImage img;
	
	public ev(int x, int y, ID id) {
		super(x, y, id);
		img = new BufferedImageLoader().loadImage("/switch_stick.png");
	}

	//값변경
	public void tick() {
		y += velY;
	}
	
	//화면에 표시해주는거
	public void render(Graphics g) {
		
		g.drawImage(img, x, y, 100, 10, null); 
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 100, 10);
	}

}