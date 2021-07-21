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
	private LinkedList<GameObject> object = new LinkedList<>(); //블럭객체 저장
	private Stream<GameObject> stream; //표현 간편화
	private BufferedImageLoader loader = new BufferedImageLoader(); //이미지로더클래스로 이미지 path 간편화.
	private BufferedImage level1 = null; //스테이지1
	private BufferedImage level2 = null; //스테이지2
	
	//블럭은 변화가 없어도 되어서 tick()메소드는 따로 넣지 않았다.
	
	public void render(Graphics g) {
		stream = object.parallelStream();
		stream.forEach(e -> e.render(g));
	}
	
	private void LoadImageLevel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		//이미지의 픽셀RGB색에 대한 블럭배치 (스테이지 추가 간편화를 위한 작업)
		for(int xx=0; xx<h; xx++) {
			for(int yy=0; yy<w; yy++) {
				//RGB 값은 정수이므로 메모리에서 4바이트로 표시된다. [00000001 00000002 00000003 00000004]
				                                       //  알파값       빨간색값      녹색값        파란색값
				int pixel = image.getRGB(xx, yy);
				// 00000001 00000002 (0xff(255) & 비트연산결과 00000002)  비트연산자 &을 사용 (0xff는 255까지 나타냄)
				int red = (pixel >> 16) & 0xff;
				// 00000001 00000002 00000003 (& 비트연산결과 00000003) 
				int green = (pixel >> 8) & 0xff;
				//00000001 00000002 00000003 00000004 (& 비트연산결과 00000004)
				int blue = (pixel) & 0xff;
				
				//다음 RGB색일 시 해당좌표에 Block 객체 추가
				if(red == 255 && green == 255 & blue == 255)
					addObject(new  Block(xx*20,yy*20,1,ID.Block));
				if(red == 255 && green == 0 & blue == 0) 
					addObject(new  Block(xx*20,yy*20,2,ID.Block));
				if(red == 0 && green == 0 & blue == 255);
			}
		}
	}
	
	//스위치레벨
	public void switchLevel() {
		clearLevel();
		
		switch(Game.LEVEL) {
			case 1:
				level1 = loader.loadImage("/back01.png"); // 배경픽셀 배치(RGB) 이미지 불러오기
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
