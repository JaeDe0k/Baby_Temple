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
	
	//1�ʿ� 60�� ����
	public void tick() {
		count++; 			// 1�ʸ��� 60�� ����
		if(count == 60) {   // ī��Ʈ�� 60�� �ɶ�����
			s += 1;  		// 1�� ����
			count = 0;  	//�׸��� ī��Ʈ�� �ٽ� 0����
			if(s == 60) {   // �ʰ� 60�� �Ǹ�
				m += 1;     //���� 1�о� �߰�
				s = 0;      //�ʴ� �ٽ� 0����
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