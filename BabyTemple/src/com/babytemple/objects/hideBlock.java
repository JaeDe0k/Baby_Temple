package com.babytemple.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;

public class hideBlock extends GameObject {
	
	
	public hideBlock(int x, int y, ID id) {
		super(x, y, id);
		falling = false;
		jumping = false;
	}

	public void tick() {
	}

	public void render(Graphics g) {
		if(falling || jumping) {
			g.setColor(Color.white);
			g.drawRect(x, y, 60, 20);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 60, 20);
	}

}
