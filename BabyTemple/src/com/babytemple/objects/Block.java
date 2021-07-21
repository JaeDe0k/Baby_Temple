package com.babytemple.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.framework.Texture;
import com.babytemple.window.Game;

public class Block extends GameObject {
	
	private Texture tex = Game.getInstance();
	private int type;
	public Block(int x, int y, int type , ID id) {
		super(x, y, id);
		this.type = type;
	}

	public void tick() {
	}

	public void render(Graphics g) {
		
		if(type == 1)
			g.drawImage(tex.block[0], x, y,20,20, null);
		
		if(type == 2)
			g.drawImage(tex.block[1], x, y,20,20, null);
		
		if(type == 3) {
			g.setColor(Color.red);
			g.drawRect(x, y, 20, 20);
		}
	}

	public Rectangle getBounds() {
		//type에 따른 충돌범위 적용
		if(type == 0)
			return new Rectangle(0, 0, 0, 0);
		return new Rectangle(x, y, 20, 20);
	}

}
