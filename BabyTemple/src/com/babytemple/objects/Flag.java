package com.babytemple.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;

public class Flag extends GameObject{
	public Flag(int x, int y, ID id) {
		super(x, y, id);
	}
	
	public void tick() {
		
	}
	
	public void render(Graphics g) {
		if(this.id == ID.angelEnemy)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.BLACK);
		g.fillRect(x,y,32,64);
//		g.setColor(Color.RED);
//		g.drawRect(x+11,y,10,64);
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x+11,y,10,64);
	}
}
