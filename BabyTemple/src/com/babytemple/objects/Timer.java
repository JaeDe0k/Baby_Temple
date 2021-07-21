package com.babytemple.objects;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.babytemple.framework.AddFont;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;

public class Timer extends GameObject {
	int m = 0;
	int s =0;
	int count=0;
	public static String msc="00:00:00";
	private Font fontA = new AddFont().getA();
	public Timer(int x, int y, ID id) {
		super(x, y, id);
	}
	
	//1초에 60번 실행
	public void tick() {
		count++; 			// 1초마다 60번 증가
		if(count == 60) {   // 카운트가 60이 될때마다
			s += 1;  		// 1초 증가
			count = 0;  	//그리고 카운트는 다시 0으로
			if(s == 60) {   // 초가 60이 되면
				m += 1;     //분이 1분씩 추가
				s = 0;      //초는 다시 0으로
			}
		}
	}

	
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(fontA.deriveFont(30f));
		msc = String.format("%02d:%02d:%02d",m,s,count);
		g.drawString(msc, x,y);
	}

	public Rectangle getBounds() {
		return null;
	}
}