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

public class DB2 extends GameObject {
	Block_Handler block_handler;
	Handler handler;
	private BufferedImage img = new BufferedImageLoader().loadImage("/white.png");
	private Stream<GameObject> stream;
	boolean b =true;
	int tempY;
	int z;
	int w;
	int h;
	int oa;
	public DB2(int x, int y, int z, int oa, ID id, Block_Handler block_handler, boolean cb, int w, int h) {
		super(x, y, id);
		this.block_handler = block_handler;
		this.z =z ;
		this.oa=oa;
		this.w=w;
		this.h=h;
		this.tempY = y;
	}
	
	
	public void tick() { 
		y = tempY;
		// ------ 자동으로 움직이는 조건
		stream = block_handler.getObject().parallelStream();
		
		stream.forEach(e -> {
			if(e.getId() == ID.Block) {
				//TOP
				if(getBounds().intersects(e.getBounds())) {
					b=!b;
				}
				if(this.getX()>z) { //최대 낮이
					velX = -1;
					x += velX;
					b=!b;
				}//일단 이것만 수정한거 맞지? 그럼상관없어 이ㅓ랑
				
				if(this.getX()<oa){ //최대 높이
					velX = 1;
					x += velX;
					b=!b;
				}
			}
		});
			if (b) {
				
				velX = 1; x += velX;
				
			}
			if (!b) { velX = -1; x += velX;}
		// ---------------------------- 
 }
		

	public void render(Graphics g) { //메모리상그리는거
		g.drawImage(img,x, y, w, h, null);
	}

	public Rectangle getBounds() {
		return new Rectangle(x,y,w,h);
	}
	
	
}
