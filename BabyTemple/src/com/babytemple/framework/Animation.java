package com.babytemple.framework;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Animation {
	private int speed; //프레임제한 (속도)
	private int frames; //프레임수 (이미지 수)
	
	private int index = 0; //현재상태
	private int count = 0; //업데이트상태
	
	private BufferedImage[] images; // 이미지 배열 생성
	private BufferedImage currentImg; // 실제 표현 이미지
	
	//가변인수(Varargs)를 이용한 메소드
	public Animation(int speed, BufferedImage... args) {
		this.speed = speed;
		images = new BufferedImage[args.length];
		for(int i=0;i<args.length;i++) {
			images[i] = args[i];
		}
		frames = args.length;
	}
	
	//런애니메이션. (index 속도가 speed 보다 높을경우)
	public void runAnimation() {
		index++;
		if(index > speed) {
			index =0;
			nextFrame();
		}
	}
	
	//다음 캐릭터 장면 전환후 카운터가 프레임보다 높아지면 count 초기화
	public void nextFrame() {
		for(int i=0; i<frames;i++) {
			if(count == i)
				currentImg = images[i];
		}
		count++;
		if(count > frames)
			count=0;
	}
	
	public void drawAnimation(Graphics g, int x, int y, int scaleX, int scaleY) {
		g.drawImage(currentImg, x,y,scaleX,scaleY, null);
	}
}
