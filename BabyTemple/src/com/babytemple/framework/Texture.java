package com.babytemple.framework;
import java.awt.image.BufferedImage;

public class Texture {
	private SpriteSheet bs, Angel_s, Devil_s, Monster_s, icon_s; //SpriteSheet클래스를 이용해 애니메이션을 위해 다양한 프레임크기로 개별 비트맵으로 동적으로 절단하기 위해 사용하기위해 사용
	
	//원본 이미지를 저장할 곳
	private BufferedImage block_sheet = null;
	private BufferedImage Angel_sheet = null;
	private BufferedImage Devil_sheet = null;
	private BufferedImage Monster_sheet = null;
	private BufferedImage Icon_sheet = null;
	
	//블럭 종류만큼 추가
	public BufferedImage[] block = new BufferedImage[2];
	//캐릭터 모션 만큼
	public BufferedImage[] Angel = new BufferedImage[10];
	//캐릭터 모션 만큼
	public BufferedImage[] Devil = new BufferedImage[10];
	//몬스터 수 만큼
	public BufferedImage[] Monster = new BufferedImage[2];
	//아이콘 수 만큼
	public BufferedImage[] Icon = new BufferedImage[16];
	
	public Texture() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			block_sheet = loader.loadImage("/block_sheet.png");
			Angel_sheet = loader.loadImage("/Angel_sheet.png");
			Devil_sheet = loader.loadImage("/Devil_sheet.png");
			Monster_sheet = loader.loadImage("/monster.png");
			Icon_sheet = loader.loadImage("/icon_sheet.png");
		}catch(Exception e) {
			e.printStackTrace();
		}
		//개별 비트맵으로 자르기위해 SpriteSheet클래스에 이미지를 저장
		bs = new SpriteSheet(block_sheet);
		Angel_s = new SpriteSheet(Angel_sheet);
		Devil_s = new SpriteSheet(Devil_sheet);
		Monster_s = new SpriteSheet(Monster_sheet);
		icon_s = new SpriteSheet(Icon_sheet);
		getTextures(); //그림을 절단해서 저장하기 위한 메소드
		getIconTextures();
	}
	
	private void getIconTextures() {
		int count=0;
		for(int i=1; i<=4;i++) {
			for(int j=1; j<=4;j++) {
				Icon[count] = icon_s.grabImage(j, i, 208, 208);
				count++;
			}
		}
	}
	
	private void getTextures() {
		//좌표를 기준으로 (크기)20,20기준으로 (2,1)에 위치한 그림 자르기 
		block[0] = bs.grabImage(2, 1, 20, 20);
		block[1] = bs.grabImage(3, 1, 20, 20);
		
		//몬스터
		Monster[0] = Monster_s.grabImage(1, 1, 1600, 1125);
		Monster[1] = Monster_s.grabImage(2, 1, 1600, 1125);
		
		
		//RIGHT
		Angel[0] = Angel_s.grabImage(1, 1, 256, 256);
		Angel[1] = Angel_s.grabImage(2, 1, 256, 256);
		Angel[2] = Angel_s.grabImage(3, 1, 256, 256);
		Angel[3] = Angel_s.grabImage(4, 1, 256, 256);
		
		//LEFT
		Angel[4] = Angel_s.grabImage(1, 2, 256, 256);
		Angel[5] = Angel_s.grabImage(2, 2, 256, 256);
		Angel[6] = Angel_s.grabImage(3, 2, 256, 256);
		Angel[7] = Angel_s.grabImage(4, 2, 256, 256);
		
		//정면, 점프
		Angel[8] = Angel_s.grabImage(1, 3, 256, 256);
		Angel[9] = Angel_s.grabImage(2, 3, 256, 256);
		
		
		//RIGHT
		Devil[0] = Devil_s.grabImage(1, 1, 256, 256);
		Devil[1] = Devil_s.grabImage(2, 1, 256, 256);
		Devil[2] = Devil_s.grabImage(3, 1, 256, 256);
		Devil[3] = Devil_s.grabImage(4, 1, 256, 256);
		
		//LEFT
		Devil[4] = Devil_s.grabImage(1, 2, 256, 256);
		Devil[5] = Devil_s.grabImage(2, 2, 256, 256);
		Devil[6] = Devil_s.grabImage(3, 2, 256, 256);
		Devil[7] = Devil_s.grabImage(4, 2, 256, 256);
		
		//정면, 점프
		Devil[8] = Devil_s.grabImage(1, 3, 256, 256);
		Devil[9] = Devil_s.grabImage(2, 3, 256, 256);
	}
}
