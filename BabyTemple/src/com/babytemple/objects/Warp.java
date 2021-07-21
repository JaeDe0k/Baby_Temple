package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.window.Block_Handler;
import com.babytemple.window.Handler;

public class Warp extends GameObject {
	Block_Handler block_handler;
	Handler handler;
	private Stream<GameObject> stream;
	int a, b, w, h;
	private BufferedImage img = new BufferedImageLoader().loadImage("/white.png");
	public Warp(int x, int y, ID id,Handler handler,int a, int b,int w, int h) {
		super(x, y, id);
		this.handler = handler;
		this.a = a;
		this.a = b;
		this.w = w;
		this.h = h;
	}
	
	
	public void tick() { 
		stream = handler.getObject().parallelStream();

		stream.forEach(e -> {
			if(e.getId() == ID.Player && getBounds().intersects(e.getBounds())) {
					e.setX(a);
					e.setY(b);
				}
		});
	}
		

	public void render(Graphics g) { //메모리상그리는거
		g.drawImage(img,x, y, w, h, null);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}
	
	
}
