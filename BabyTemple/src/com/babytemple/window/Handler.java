package com.babytemple.window;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.babytemple.framework.BT_USER_DB;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.objects.DB;
import com.babytemple.objects.DB2;
import com.babytemple.objects.Flag;
import com.babytemple.objects.Item;
import com.babytemple.objects.Magma;
import com.babytemple.objects.Player;
import com.babytemple.objects.Timer;
import com.babytemple.objects.Warp;
import com.babytemple.objects.door;
import com.babytemple.objects.door2;
import com.babytemple.objects.hideBlock;
import com.babytemple.objects.isClear;
import com.babytemple.objects.moveBlock;
import com.babytemple.objects.roomPlayer;
import com.babytemple.objects.스위치;

public class Handler {
	//게임오브젝트를 상속받는 객체를 LinkedList에 저장
	private LinkedList<GameObject> object = new LinkedList<>();
	
	//게임오브젝트 데이터를 입력받아 forEach메소드로 각 객체들을 요소에 대입하여 호출하기 위해 스트림선언
	//람다식을 적용한 컬렉션과 같은 저장요소를 반복적으로 처리하기 기능이 있음.
	//Stream은 Immutable 하다. 다시 말해 원본의 데이터를 변경하지 않는다.
	//Stream의 연산은 layz 하다. 즉 필요 할 때만 연산함으로 효율적인 처리가 가능하다.
	//Stream은 재사용이 불가능하다.
	//객체를 저장할때 stream()은 순차스트림, parallelStream()은 병렬스트림이기 때문에 멀티스레드 프로그래밍을 쉽게해준다.
	private Stream<GameObject> stream;
	private ID PLAY, ROOMPLAY;
	//맵을 따로 관리하기위한 블럭핸들러 클래스 생성
	private Block_Handler block_handler;
	private BT_USER_DB userDB;
	private isClear isclear = null;
	
	public static boolean death = false;
	public static boolean clear = false;
	
	public static int score = 0;
	public static boolean clearAngel = false;
	public static boolean clearDevil = false;
	Handler(Block_Handler block_handler, BT_USER_DB userDB, isClear isclear) {
		this.block_handler = block_handler; //다른곳에 있는 블럭핸들러 객체를 전달받아 같이 사용.
		this.isclear = isclear;
		this.userDB = userDB;
	}
	
	//모든 객체의 변화를 주는 메소드.(Game클래스에서 이 메소드를 실행시킴)
	public void tick() {
		if(!clear)
			for(int i=0; i < object.size();i++)
				object.get(i).tick();
	}
	
	//모든 객체의 그려주는 메소드.(Game클래스에서 이 메소드를 실행시킴)
	public void render(Graphics g) {
		stream = object.parallelStream();
		stream.forEach(e -> e.render(g));
		
		if(clear)
			isclear.render(g);
	}
	
	//키조작 및 위치 서버 중개
	public void netwrokKeyPressed(String player, boolean jumping, float velY, boolean left, boolean right, int x, int y) {
		switch (player) {
			case "P1":
				PLAY = ID.Angel;
				ROOMPLAY = ID.NONETWORK;
				break;
			case "P2":
				PLAY = ID.Devil;
				ROOMPLAY = ID.NONETWORK;
				break;
			case "P1ROOM":
				PLAY = ID.NONETWORK;
				ROOMPLAY = ID.roomAngel;
				break;
			case "P2ROOM":
				PLAY = ID.NONETWORK;
				ROOMPLAY = ID.roomDevil;
				break;
		}
		stream = object.parallelStream();
		stream.forEach(e -> {
			if(e.getType() == PLAY || e.getType() == ROOMPLAY) {
				e.setJumping(jumping);
				e.setVelY(velY);
				e.setLeft(left);
				e.setRight(right);
				e.setX(x);
				e.setY(y);
			}
		});
	}
	
	public void netwrokKeyRelease(String player, boolean left, boolean right, int x, int y) {
		switch (player) {
			case "P1":
				PLAY = ID.Angel;
				ROOMPLAY = ID.NONETWORK;
				break;
			case "P2":
				PLAY = ID.Devil;
				ROOMPLAY = ID.NONETWORK;
				break;
			case "P1ROOM":
				PLAY = ID.NONETWORK;
				ROOMPLAY = ID.roomAngel;
				break;
			case "P2ROOM":
				PLAY = ID.NONETWORK;
				ROOMPLAY = ID.roomDevil;
				break;
		}
		stream = object.parallelStream();
		stream.forEach(e -> {
			if(e.getType() == PLAY || e.getType() == ROOMPLAY) {
				e.setLeft(left);
				e.setRight(right);
				e.setX(x);
				e.setY(y);
			}
		});
	}
	
	public void loadRoom() {
		this.addObject(new roomPlayer(500, 150, ID.Player, ID.roomAngel)); //캐릭터 생성
		this.addObject(new roomPlayer(500, 150, ID.Player, ID.roomDevil)); //캐릭터 생성
	}
	
	// 레벨에 따른 객체생성.(스테이지가 늘어날 때를 대비한 메소드) 
	public void LoadImageLevel() {			
		//this.addObject(new Green(400, 800, ID.Monster,this,true)); // 객체 생성 (몬스터)
				this.addObject(new Timer(560,20,ID.Null)); // 타이머
				this.addObject(new Flag(40,360,ID.angelEnemy)); //통과
				this.addObject(new Flag(90,360,ID.devilEnemy)); //통과
				
				this.addObject(new hideBlock(340, 375,ID.hideBlock));
				this.addObject(new hideBlock(630, 630,ID.hideBlock));
				this.addObject(new hideBlock(590, 560,ID.hideBlock));
				this.addObject(new Warp(20, 885,ID.Warp, this, 100, 100, 180, 15));
				this.addObject(new Player(270, 870, ID.Player, this, ID.Angel, block_handler, userDB)); //캐릭터 생성
				this.addObject(new Player(300, 870, ID.Player, this, ID.Devil, block_handler, userDB)); //캐릭터 생성
				
				this.addObject(new DB2(560, 130 ,880, 110, ID.Device,  block_handler,true,100,10)); // 객체 생성 (엘레베이터)
				
				this.addObject(new Item(400, 90, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
				this.addObject(new Item(320, 500, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
				this.addObject(new Item(380, 800, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
				
				this.addObject(new Item(610, 90, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
				this.addObject(new Item(530, 400, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
				this.addObject(new Item(510, 800, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
				
				this.addObject(new moveBlock(190,280,ID.moveBlock,block_handler, this)); //미는블럭
				this.addObject(new 스위치(245, 660, ID.스위치, this,1));
				this.addObject(new door2(20,300, ID.Door));
				
				this.addObject(new Magma(280, 178 , ID.Magma , 500,10)); //닿으면 죽는 장판 위
				this.addObject(new Magma(360, 890 , ID.Magma , 34,15)); //닿으면 죽는 장판 밑
				this.addObject(new Magma(460, 890 , ID.Magma , 34,15)); //닿으면 죽는 장판 밑
				this.addObject(new Magma(560, 890 , ID.Magma , 34,15)); //닿으면 죽는 장판 밑	
	}
	
	public void LoadImageLevel2() {
		this.addObject(new Timer(560,20,ID.Null)); // 타이머
//        this.addObject(new Green(400, 800, ID.Monster,this,true)); // 객체 생성 (몬스터)
		this.addObject(new DB(950, 700 ,880,200, ID.Device,block_handler,true,170,10)); // 객체 생성 (엘레베이터)
		
		this.addObject(new Warp(20, 820,ID.Warp, this, 100, 100,20, 60));
		
		this.addObject(new Player(200, 100, ID.Player, this, ID.Angel, block_handler, userDB)); //캐릭터 생성
		this.addObject(new Player(100, 100, ID.Player, this, ID.Devil, block_handler, userDB)); //캐릭터 생성
		
		this.addObject(new Magma(300, 260 , ID.Magma , 270,20)); //닿으면 죽는 장판
		this.addObject(new Magma(55, 560 , ID.Magma , 235,20)); //닿으면 죽는 장판
		this.addObject(new Magma(25, 780 , ID.Magma , 360,20)); //닿으면 죽는 장판
		
		this.addObject(new Item(105, 615, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(25 ,400, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(1130 ,200, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		
		this.addObject(new Item(1130,400, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(205,615, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(130 ,400, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
				
		this.addObject(new Flag(600,515,ID.angelEnemy)); //통과
		this.addObject(new Flag(650,515,ID.devilEnemy)); //통과
		
	}
	
	public void LoadImageLevel3() {
		this.addObject(new Timer(560,20,ID.Null)); // 타이머
		// this.addObject(new Green(400, 800, ID.Monster,this,true)); // 객체 생성 (몬스터)
		this.addObject(new DB(185, 700 ,880, 100, ID.Device,block_handler,true,150,15)); // 객체 생성 (엘레베이터)
		this.addObject(new Player(23, 845, ID.Player, this, ID.Angel, block_handler, userDB)); //캐릭터 생성
		this.addObject(new Player(23, 845, ID.Player, this, ID.Devil, block_handler, userDB)); //캐릭터 생성
		this.addObject(new Magma(700, 518 , ID.Magma , 455,10)); //닿으면 죽는 장판
		this.addObject(new Magma(560, 838 , ID.Magma , 150,10)); //닿으면 죽는 장판
		
		this.addObject(new Item(25, 340, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(25 ,540, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(1130 ,200, ID.Item, this, ID.angelItem)); //닿으면 사라지는 아이템
		
		this.addObject(new Item(1105,400, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(700,600, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
		this.addObject(new Item(1130 ,860, ID.Item, this, ID.devilItem)); //닿으면 사라지는 아이템
		
		

		
		this.addObject(new hideBlock(930,140,ID.hideBlock));
		this.addObject(new hideBlock(830,120,ID.hideBlock));
		this.addObject(new 스위치(50, 140, ID.스위치, this, 1));
		this.addObject(new door(350,360, ID.Door));
		
		this.addObject(new Flag(400,56,ID.angelEnemy)); //통과
		this.addObject(new Flag(450,56,ID.devilEnemy)); //통과
		
		
		this.addObject(new moveBlock(550,610,ID.moveBlock,block_handler, this)); //미는블럭
	   }
	
	// 스테이지에 따른 레벨 선택 및 객체 호출
	public void switchLevel() {
		//저장된 모든객체를 삭제하는 메소드
		clearLevel();
		
		//LEVEL에 따른 메소드 호출 및 값 저달
		switch(Game.LEVEL) {
			case 1: LoadImageLevel();
					break;
			case 2: LoadImageLevel2();
					break;
			case 3: LoadImageLevel3();
					break;
		}
	}
	
	//저장된 모든 객체를 삭제
	public void clearLevel() {
		ChatPanel.textareaTeam.setText("팀 채팅방입니다.\n");
		
		Handler.score = 0;
		
		Handler.death = false;
		Handler.clear = false;
		Handler.clearAngel = false;
		Handler.clearDevil = false;
		object.clear();
	}
	
	//object에 GameObject를 상속받는 객체 저장.
	public void addObject(GameObject tempObject) {
		object.add(tempObject);
	}
	
	//해당 객체를 object에서 제거
	public void removeObject(GameObject tempObject) {
		object.remove(tempObject);
	}
	
	//object에 저장된 모든 객체를 반환
	public LinkedList<GameObject> getObject() {
		return object;
	}
	
}
