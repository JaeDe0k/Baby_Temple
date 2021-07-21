package com.babytemple.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.babytemple.framework.Animation;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.framework.Texture;
import com.babytemple.window.Game;

public class roomPlayer extends GameObject {
	private int width = 120; //ĳ���� ���� ũ��
	private int height = 120; // ĳ���� ���� ũ��
	private float gravity = 1f; //�߷�
	private final float MAX_SPEED =6; // �ִ볫�ϼӵ�
	private int facing; //0=����, 1=������
	private Texture tex = Game.getInstance();
	private Animation playerWalkJump, playerWalkStop, playerWalkLeft, playerWalkRight; //ĳ���� �ִϸ��̼� ����, ������
	
	//���� ID type�� �̿��� õ��,�Ǹ��� ����, ���� �� ��ü�� �浹Ȯ���� ���� handler�鰪 ���޹ޱ�
	public roomPlayer(int x, int y, ID id, ID type) {
		super(x, y, id);
		this.type = type;
		
		if(type == ID.roomAngel) {
			playerWalkStop =  new Animation(4,tex.Angel[8]);
			playerWalkJump = new Animation(4,tex.Angel[9]);
			playerWalkLeft = new Animation(4, tex.Angel[0], tex.Angel[1],tex.Angel[2],tex.Angel[3]);
			playerWalkRight = new Animation(4, tex.Angel[4], tex.Angel[5],tex.Angel[6],tex.Angel[7]);
			}
		else if(type == ID.roomDevil){
			playerWalkStop =  new Animation(4,tex.Devil[8]);
			playerWalkJump = new Animation(4,tex.Devil[9]);
			playerWalkLeft = new Animation(4, tex.Devil[0], tex.Devil[1],tex.Devil[2],tex.Devil[3]);
			playerWalkRight = new Animation(4, tex.Devil[4], tex.Devil[5],tex.Devil[6],tex.Devil[7]);
			}
	}

	public void tick() {
		//���� ��ȯ �� �ӵ� ����
		x += velX;
		y += velY;
		
		//ĳ���� �̹����� ���� �������� Ȯ���ϱ� ���� ����
		if(velX < 0) facing = 1;
		else if(velX > 0) facing = 0;
		
		//�߷�
		if(this.isFalling() || this.isJumping()) {
			velY += gravity;
			if(velY > MAX_SPEED)
				velY = MAX_SPEED; //�ִ� �߷°��ӵ�
		}
		
		//�ڵ鷯�� ���� ���� �̵�
		if(this.isRight()) velX = 5;
		else if (!this.isLeft()) velX=0;
		if(this.isLeft()) velX = -5;
		else if (!this.isRight()) velX=0;
		
		Collision();
		playerWalkLeft.runAnimation(); //���ʸ��
		playerWalkRight.runAnimation(); //�����ʸ��
		playerWalkJump.runAnimation(); //�������
		playerWalkStop.runAnimation(); //�����ִ� ���
	}
	
	//�浹�� �ö�
	public void Collision() {
		//TOP
		if(getBoundsTop().intersects(getBlockUp())) {
			y = 140 + 12 ; // Block ũ��
			velY=0;
		}
		//BOTTOM
		if(getBoundsBottom().intersects(getBlockDown())) {
			y = 539 - height+1; //ĳ����ũ�� +1
			velY=0;
			this.setFalling(false);
			this.setJumping(false); 
		}else 
			this.setFalling(true);
		
		//RIGHT
		if(getBoundsRight().intersects(getBlockRight())) {
			x = 781 - width +4; //ĳ���� ũ�⿵��
		}
		
		//LEFT
		if(getBoundsLeft().intersects(getBlockLeft())) {
			x = 40 + 12; //Block ũ��
		}
	}

	public void render(Graphics g) {
		DrawAngel(g); // õ�� or �Ǹ� Draw
//		DrawCollision(g); //ĳ���� �浹 ���� ǥ��
	}
	
	//Angel Draw
	public void DrawAngel(Graphics g) {
		//�̹��� ��ǥ�� ũ�⼳��
		if(this.isJumping())
			playerWalkJump.drawAnimation(g, x, y, width, height);
		else {
			if(velX != 0) {
				if(facing == 1)
					playerWalkRight.drawAnimation(g, x, y, width, height);
				else
					playerWalkLeft.drawAnimation(g, x, y, width, height);
			}else
				playerWalkStop.drawAnimation(g, x, y, width, height);
		}
	}
	
	//ĳ���� �浹 ���� ǥ��
	public void DrawCollision(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.red);
		g2d.draw(getBounds());
		g.setColor(Color.green);
		g2d.draw(getBoundsRight());
		g.setColor(Color.blue);
		g2d.draw(getBoundsLeft());
		g.setColor(Color.yellow);
		g2d.draw(getBoundsTop());
		g.setColor(Color.pink);
		g2d.draw(getBoundsBottom());
		
		g.drawRect(48, 140, 740, 12); //��
		g.drawRect(48, 539, 740, 12); //�Ʒ�
		g.drawRect(40, 140, 13, 410); //����
		g.drawRect(781, 140, 13, 410); //������
	}

	//��ü����
	public Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	//������ ����
	public Rectangle getBoundsRight() {
		return new Rectangle(x+width-30,y+10,28,height-20);
	}
	//���� ����
	public Rectangle getBoundsLeft() {
		return new Rectangle(x+2,y+10,28,height-20);
	}
	//ĳ���� ���κ�
	public Rectangle getBoundsTop() {
		return new Rectangle(x+(width/2)-((width/2)/2),y,width/2,height/2);
	}
	//ĳ���� �Ʒ��κ� ����
	public Rectangle getBoundsBottom() {
		return new Rectangle(x+(width/2)-((width/2)/2),y+(height/2),width/2,height/2);
	}
	
	public Rectangle getBlockUp() {
		return new Rectangle(48, 140, 740, 12);
	}
	public Rectangle getBlockDown() {
		return new Rectangle(48, 539, 740, 12);
	}
	public Rectangle getBlockLeft() {
		return new Rectangle(40, 140, 13, 410);
	}
	public Rectangle getBlockRight() {
		return new Rectangle(781, 140, 13, 410);
	}

}
