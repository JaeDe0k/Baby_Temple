package com.babytemple.window;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.objects.Block;

public class Block_Handler {
	private LinkedList<GameObject> object = new LinkedList<>(); //����ü ����
	private Stream<GameObject> stream; //ǥ�� ����ȭ
	private BufferedImageLoader loader = new BufferedImageLoader(); //�̹����δ�Ŭ������ �̹��� path ����ȭ.
	private BufferedImage level1 = null; //��������1
	private BufferedImage level2 = null; //��������2
	
	//���� ��ȭ�� ��� �Ǿ tick()�޼ҵ�� ���� ���� �ʾҴ�.
	
	public void render(Graphics g) {
		stream = object.parallelStream();
		stream.forEach(e -> e.render(g));
	}
	
	private void LoadImageLevel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		//�̹����� �ȼ�RGB���� ���� ����ġ (�������� �߰� ����ȭ�� ���� �۾�)
		for(int xx=0; xx<h; xx++) {
			for(int yy=0; yy<w; yy++) {
				//RGB ���� �����̹Ƿ� �޸𸮿��� 4����Ʈ�� ǥ�õȴ�. [00000001 00000002 00000003 00000004]
				                                       //  ���İ�       ��������      �����        �Ķ�����
				int pixel = image.getRGB(xx, yy);
				// 00000001 00000002 (0xff(255) & ��Ʈ������ 00000002)  ��Ʈ������ &�� ��� (0xff�� 255���� ��Ÿ��)
				int red = (pixel >> 16) & 0xff;
				// 00000001 00000002 00000003 (& ��Ʈ������ 00000003) 
				int green = (pixel >> 8) & 0xff;
				//00000001 00000002 00000003 00000004 (& ��Ʈ������ 00000004)
				int blue = (pixel) & 0xff;
				
				//���� RGB���� �� �ش���ǥ�� Block ��ü �߰�
				if(red == 255 && green == 255 & blue == 255)
					addObject(new  Block(xx*20,yy*20,1,ID.Block));
				if(red == 255 && green == 0 & blue == 0) 
					addObject(new  Block(xx*20,yy*20,2,ID.Block));
				if(red == 0 && green == 0 & blue == 255);
			}
		}
	}
	
	//����ġ����
	public void switchLevel() {
		clearLevel();
		
		switch(Game.LEVEL) {
			case 1:
				level1 = loader.loadImage("/back01.png"); // ����ȼ� ��ġ(RGB) �̹��� �ҷ�����
				LoadImageLevel(level1);
				break;
			case 2:
				level2 = loader.loadImage("/back02.png");
				LoadImageLevel(level2);
				break;
			case 3:
				level2 = loader.loadImage("/back03.png");
				LoadImageLevel(level2);
				break;
		}
	}
	
	public void clearLevel() {
		object.clear();
	}
	
	public void addObject(GameObject tempObject) {
		object.add(tempObject);
	}
	
	public void removeObject(GameObject tempObject) {
		object.remove(tempObject);
	}
	
	public LinkedList<GameObject> getObject() {
		return object;
	}
	
	public Block_Handler getHandler() {
		return this;
	}
}
