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
import com.babytemple.objects.����ġ;

public class Handler {
	//���ӿ�����Ʈ�� ��ӹ޴� ��ü�� LinkedList�� ����
	private LinkedList<GameObject> object = new LinkedList<>();
	
	//���ӿ�����Ʈ �����͸� �Է¹޾� forEach�޼ҵ�� �� ��ü���� ��ҿ� �����Ͽ� ȣ���ϱ� ���� ��Ʈ������
	//���ٽ��� ������ �÷��ǰ� ���� �����Ҹ� �ݺ������� ó���ϱ� ����� ����.
	//Stream�� Immutable �ϴ�. �ٽ� ���� ������ �����͸� �������� �ʴ´�.
	//Stream�� ������ layz �ϴ�. �� �ʿ� �� ���� ���������� ȿ������ ó���� �����ϴ�.
	//Stream�� ������ �Ұ����ϴ�.
	//��ü�� �����Ҷ� stream()�� ������Ʈ��, parallelStream()�� ���Ľ�Ʈ���̱� ������ ��Ƽ������ ���α׷����� �������ش�.
	private Stream<GameObject> stream;
	private ID PLAY, ROOMPLAY;
	//���� ���� �����ϱ����� ���ڵ鷯 Ŭ���� ����
	private Block_Handler block_handler;
	private BT_USER_DB userDB;
	private isClear isclear = null;
	
	public static boolean death = false;
	public static boolean clear = false;
	
	public static int score = 0;
	public static boolean clearAngel = false;
	public static boolean clearDevil = false;
	Handler(Block_Handler block_handler, BT_USER_DB userDB, isClear isclear) {
		this.block_handler = block_handler; //�ٸ����� �ִ� ���ڵ鷯 ��ü�� ���޹޾� ���� ���.
		this.isclear = isclear;
		this.userDB = userDB;
	}
	
	//��� ��ü�� ��ȭ�� �ִ� �޼ҵ�.(GameŬ�������� �� �޼ҵ带 �����Ŵ)
	public void tick() {
		if(!clear)
			for(int i=0; i < object.size();i++)
				object.get(i).tick();
	}
	
	//��� ��ü�� �׷��ִ� �޼ҵ�.(GameŬ�������� �� �޼ҵ带 �����Ŵ)
	public void render(Graphics g) {
		stream = object.parallelStream();
		stream.forEach(e -> e.render(g));
		
		if(clear)
			isclear.render(g);
	}
	
	//Ű���� �� ��ġ ���� �߰�
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
		this.addObject(new roomPlayer(500, 150, ID.Player, ID.roomAngel)); //ĳ���� ����
		this.addObject(new roomPlayer(500, 150, ID.Player, ID.roomDevil)); //ĳ���� ����
	}
	
	// ������ ���� ��ü����.(���������� �þ ���� ����� �޼ҵ�) 
	public void LoadImageLevel() {			
		//this.addObject(new Green(400, 800, ID.Monster,this,true)); // ��ü ���� (����)
				this.addObject(new Timer(560,20,ID.Null)); // Ÿ�̸�
				this.addObject(new Flag(40,360,ID.angelEnemy)); //���
				this.addObject(new Flag(90,360,ID.devilEnemy)); //���
				
				this.addObject(new hideBlock(340, 375,ID.hideBlock));
				this.addObject(new hideBlock(630, 630,ID.hideBlock));
				this.addObject(new hideBlock(590, 560,ID.hideBlock));
				this.addObject(new Warp(20, 885,ID.Warp, this, 100, 100, 180, 15));
				this.addObject(new Player(270, 870, ID.Player, this, ID.Angel, block_handler, userDB)); //ĳ���� ����
				this.addObject(new Player(300, 870, ID.Player, this, ID.Devil, block_handler, userDB)); //ĳ���� ����
				
				this.addObject(new DB2(560, 130 ,880, 110, ID.Device,  block_handler,true,100,10)); // ��ü ���� (����������)
				
				this.addObject(new Item(400, 90, ID.Item, this, ID.angelItem)); //������ ������� ������
				this.addObject(new Item(320, 500, ID.Item, this, ID.angelItem)); //������ ������� ������
				this.addObject(new Item(380, 800, ID.Item, this, ID.angelItem)); //������ ������� ������
				
				this.addObject(new Item(610, 90, ID.Item, this, ID.devilItem)); //������ ������� ������
				this.addObject(new Item(530, 400, ID.Item, this, ID.devilItem)); //������ ������� ������
				this.addObject(new Item(510, 800, ID.Item, this, ID.devilItem)); //������ ������� ������
				
				this.addObject(new moveBlock(190,280,ID.moveBlock,block_handler, this)); //�̴º�
				this.addObject(new ����ġ(245, 660, ID.����ġ, this,1));
				this.addObject(new door2(20,300, ID.Door));
				
				this.addObject(new Magma(280, 178 , ID.Magma , 500,10)); //������ �״� ���� ��
				this.addObject(new Magma(360, 890 , ID.Magma , 34,15)); //������ �״� ���� ��
				this.addObject(new Magma(460, 890 , ID.Magma , 34,15)); //������ �״� ���� ��
				this.addObject(new Magma(560, 890 , ID.Magma , 34,15)); //������ �״� ���� ��	
	}
	
	public void LoadImageLevel2() {
		this.addObject(new Timer(560,20,ID.Null)); // Ÿ�̸�
//        this.addObject(new Green(400, 800, ID.Monster,this,true)); // ��ü ���� (����)
		this.addObject(new DB(950, 700 ,880,200, ID.Device,block_handler,true,170,10)); // ��ü ���� (����������)
		
		this.addObject(new Warp(20, 820,ID.Warp, this, 100, 100,20, 60));
		
		this.addObject(new Player(200, 100, ID.Player, this, ID.Angel, block_handler, userDB)); //ĳ���� ����
		this.addObject(new Player(100, 100, ID.Player, this, ID.Devil, block_handler, userDB)); //ĳ���� ����
		
		this.addObject(new Magma(300, 260 , ID.Magma , 270,20)); //������ �״� ����
		this.addObject(new Magma(55, 560 , ID.Magma , 235,20)); //������ �״� ����
		this.addObject(new Magma(25, 780 , ID.Magma , 360,20)); //������ �״� ����
		
		this.addObject(new Item(105, 615, ID.Item, this, ID.angelItem)); //������ ������� ������
		this.addObject(new Item(25 ,400, ID.Item, this, ID.angelItem)); //������ ������� ������
		this.addObject(new Item(1130 ,200, ID.Item, this, ID.angelItem)); //������ ������� ������
		
		this.addObject(new Item(1130,400, ID.Item, this, ID.devilItem)); //������ ������� ������
		this.addObject(new Item(205,615, ID.Item, this, ID.devilItem)); //������ ������� ������
		this.addObject(new Item(130 ,400, ID.Item, this, ID.devilItem)); //������ ������� ������
				
		this.addObject(new Flag(600,515,ID.angelEnemy)); //���
		this.addObject(new Flag(650,515,ID.devilEnemy)); //���
		
	}
	
	public void LoadImageLevel3() {
		this.addObject(new Timer(560,20,ID.Null)); // Ÿ�̸�
		// this.addObject(new Green(400, 800, ID.Monster,this,true)); // ��ü ���� (����)
		this.addObject(new DB(185, 700 ,880, 100, ID.Device,block_handler,true,150,15)); // ��ü ���� (����������)
		this.addObject(new Player(23, 845, ID.Player, this, ID.Angel, block_handler, userDB)); //ĳ���� ����
		this.addObject(new Player(23, 845, ID.Player, this, ID.Devil, block_handler, userDB)); //ĳ���� ����
		this.addObject(new Magma(700, 518 , ID.Magma , 455,10)); //������ �״� ����
		this.addObject(new Magma(560, 838 , ID.Magma , 150,10)); //������ �״� ����
		
		this.addObject(new Item(25, 340, ID.Item, this, ID.angelItem)); //������ ������� ������
		this.addObject(new Item(25 ,540, ID.Item, this, ID.angelItem)); //������ ������� ������
		this.addObject(new Item(1130 ,200, ID.Item, this, ID.angelItem)); //������ ������� ������
		
		this.addObject(new Item(1105,400, ID.Item, this, ID.devilItem)); //������ ������� ������
		this.addObject(new Item(700,600, ID.Item, this, ID.devilItem)); //������ ������� ������
		this.addObject(new Item(1130 ,860, ID.Item, this, ID.devilItem)); //������ ������� ������
		
		

		
		this.addObject(new hideBlock(930,140,ID.hideBlock));
		this.addObject(new hideBlock(830,120,ID.hideBlock));
		this.addObject(new ����ġ(50, 140, ID.����ġ, this, 1));
		this.addObject(new door(350,360, ID.Door));
		
		this.addObject(new Flag(400,56,ID.angelEnemy)); //���
		this.addObject(new Flag(450,56,ID.devilEnemy)); //���
		
		
		this.addObject(new moveBlock(550,610,ID.moveBlock,block_handler, this)); //�̴º�
	   }
	
	// ���������� ���� ���� ���� �� ��ü ȣ��
	public void switchLevel() {
		//����� ��簴ü�� �����ϴ� �޼ҵ�
		clearLevel();
		
		//LEVEL�� ���� �޼ҵ� ȣ�� �� �� ����
		switch(Game.LEVEL) {
			case 1: LoadImageLevel();
					break;
			case 2: LoadImageLevel2();
					break;
			case 3: LoadImageLevel3();
					break;
		}
	}
	
	//����� ��� ��ü�� ����
	public void clearLevel() {
		ChatPanel.textareaTeam.setText("�� ä�ù��Դϴ�.\n");
		
		Handler.score = 0;
		
		Handler.death = false;
		Handler.clear = false;
		Handler.clearAngel = false;
		Handler.clearDevil = false;
		object.clear();
	}
	
	//object�� GameObject�� ��ӹ޴� ��ü ����.
	public void addObject(GameObject tempObject) {
		object.add(tempObject);
	}
	
	//�ش� ��ü�� object���� ����
	public void removeObject(GameObject tempObject) {
		object.remove(tempObject);
	}
	
	//object�� ����� ��� ��ü�� ��ȯ
	public LinkedList<GameObject> getObject() {
		return object;
	}
	
}
