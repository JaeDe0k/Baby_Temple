package com.babytemple.objects;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.stream.Stream;

import com.babytemple.framework.Animation;
import com.babytemple.framework.BT_USER_DB;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.framework.Texture;
import com.babytemple.window.Block_Handler;
import com.babytemple.window.Game;
import com.babytemple.window.Handler;

public class Player extends GameObject {
	private Handler handler;
	private BT_USER_DB userDB; //DB��ü
	private Stream<GameObject> stream;
	private int width = 50; //ĳ���� ���� ũ��
	private int height = 50; // ĳ���� ���� ũ��
	private float gravity = 0.5f; //�߷�
	private final float MAX_SPEED =3; // �ִ볫�ϼӵ�
	private int facing; //0=����, 1=������
	private Texture tex = Game.getInstance();
	private Block_Handler block_handler;
	private Animation playerWalkJump, playerWalkStop, playerWalkLeft, playerWalkRight; //ĳ���� �ִϸ��̼� ����, ������
	private boolean aoo;
	//���� ID type�� �̿��� õ��,�Ǹ��� ����, ���� �� ��ü�� �浹Ȯ���� ���� handler�鰪 ���޹ޱ�
	public Player(int x, int y, ID id, Handler handler, ID type, Block_Handler block_handler, BT_USER_DB userDB) {
		super(x, y, id);
		this.handler = handler;
		this.userDB = userDB;
		this.type = type;
		this.block_handler = block_handler;
		this.aoo = true;
		if(type == ID.Angel) {
			playerWalkStop =  new Animation(5,tex.Angel[8]);
			playerWalkJump = new Animation(5,tex.Angel[9]);
			playerWalkLeft = new Animation(5, tex.Angel[0], tex.Angel[1],tex.Angel[2],tex.Angel[3]);
			playerWalkRight = new Animation(5, tex.Angel[4], tex.Angel[5],tex.Angel[6],tex.Angel[7]);
			}
		else if(type == ID.Devil){
			playerWalkStop =  new Animation(5,tex.Devil[8]);
			playerWalkJump = new Animation(5,tex.Devil[9]);
			playerWalkLeft = new Animation(5, tex.Devil[0], tex.Devil[1],tex.Devil[2],tex.Devil[3]);
			playerWalkRight = new Animation(5, tex.Devil[4], tex.Devil[5],tex.Devil[6],tex.Devil[7]);
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
		if(this.isRight()) velX = 2;
		else if (!this.isLeft()) velX=0;
		if(this.isLeft()) velX = -2;
		else if (!this.isRight()) velX=0;
		
		
		Collision(); //�浹����
		playerWalkLeft.runAnimation(); //���ʸ��
		playerWalkRight.runAnimation(); //�����ʸ��
		playerWalkJump.runAnimation(); //�������
		playerWalkStop.runAnimation(); //�����ִ� ���
	}
	
	//�浹����
	private void Collision() {
		stream = handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_ev(e); //ev ��ü �浹����
			Collision_Elevator(e); //Device ��ü �浹����
			Collision_Death(e); //�����ü �浹����
			Collision_clear(e);
			Collision_hideBlock(e);
			Collision_door(e);
		});
		
		//�� ��ü �浹����
		stream = block_handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_Block(e);
		});
	}
	
	public void Collision_door(GameObject e) {
		if(e.getId()==ID.Door) {
			//BOTTOM
			if(getBoundsBottom().intersects(e.getBounds())) {
				y = e.getY() - height+1; //ĳ����ũ�� +1
				velY=0;
				this.setFalling(false);
				this.setJumping(false); 
			}else 
				this.setFalling(true);
			//TOP
			if(getBoundsTop().intersects(e.getBounds())) {
				y = e.getY() + 20 ; // Block ũ��
				velY=0;
			}
			//RIGHT
			if(getBoundsRight().intersects(e.getBounds())) {
				x = e.getX() - width +4; //ĳ���� ũ�⿵��
			}
			//LEFT
			if(getBoundsLeft().intersects(e.getBounds())) {
				x = e.getX()+10; //Block ũ��
			}
		}
	}
	
	public void Collision_hideBlock(GameObject e) {
		if(e.getId()==ID.hideBlock) {
			if(getBoundsTop().intersects(e.getBounds())) {
				y = e.getY() + 20 ; // �ش簴ü ���̸�ŭ
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(e.getBounds())) {
				y = e.getY() - height +1; // ĳ����ũ�⸸ŭ y������ �ø��� +1�� �����νĺ�����
				velY=0;
				this.setFalling(false);
				this.setJumping(false);
				if(type == ID.Angel)
					e.setFalling(true);
				if(type == ID.Devil)
					e.setJumping(true);
			}else {
				this.setFalling(true);
				if(type == ID.Angel)
					e.setFalling(false);
				if(type == ID.Devil)
					e.setJumping(false);
			}
			
			//RIGHT
			if(getBoundsRight().intersects(e.getBounds())) {
				x = e.getX() - width +4; //ĳ���� ũ�⿵��
			}
			
			//LEFT
			if(getBoundsLeft().intersects(e.getBounds())) {
				x = e.getX() + 60; //Block ũ��
			}
		}
	}
	
	public void Collision_ev(GameObject tempObject) {
		if(tempObject.getId() == ID.EV) {
			//TOP
			if(getBoundsTop().intersects(tempObject.getBounds())) {
				y = tempObject.getY() + 10 ; // �ش簴ü ���̸�ŭ
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - height +1; // ĳ����ũ�⸸ŭ y������ �ø��� +1�� �����νĺ�����
				velY=0;
				this.setFalling(false);
				this.setJumping(false);
			}else 
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - width +4; //ĳ���� ũ�⿵��

			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 96; //Block ũ��
			}
		}
	}
	
	//�浹�� ���� (���ذ���� �����̰� ���� �� ���� �߻�)
	public void Collision_Death(GameObject tempObject) {
		if(tempObject.getId() == ID.Monster) {
			if(getBounds().intersects(tempObject.getBounds())) {
				Handler.death = true;
				Handler.clear = true;
			}
		}
		
		if(tempObject.getId() == ID.Magma) {
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				Handler.death = true;
				Handler.clear = true;
			}
		}
	}
	
	public void Collision_clear(GameObject tempObject) {
		if(this.type == ID.Angel && tempObject.getId() == ID.angelEnemy) {
			//����ġ ����
			if(getBounds().intersects(tempObject.getBounds())) {
				Handler.clearAngel = true;
			} else
				Handler.clearAngel = false;
		} else if(this.type == ID.Devil && tempObject.getId() == ID.devilEnemy) {
			//����ġ ����
			if(getBounds().intersects(tempObject.getBounds())) {
				Handler.clearDevil = true;
			} else
				Handler.clearDevil = false;
		}

		if(Handler.clearAngel && Handler.clearDevil) {
			if(Handler.score == 6 && Game.p1ID == Game.userID && aoo) {
				aoo = false;
				userDB.sendRank(Game.p1ID, Game.p2ID, Timer.msc, Game.LEVEL);
			}
			Handler.death = false;
			Handler.clear = true;
			Handler.clearAngel = false;
			Handler.clearDevil = false;
		}
	}
	
	//����������
	public void Collision_Elevator(GameObject tempObject) {
		if(tempObject.getId() == ID.Device) {
			//TOP
			if(getBoundsTop().intersects(tempObject.getBounds())) {
				y = tempObject.getY() + 22;
				tempObject.setY(y-28);
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - height+2; 
				velY=0;
				this.setFalling(false);
				this.setJumping(false);
			}else
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - width;
			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 150;
			}
		}
	}
	//�浹�� �ö�
	public void Collision_Block(GameObject tempObject) {
		if(tempObject.getId() == ID.Block) {
			//TOP
			if(getBoundsTop().intersects(tempObject.getBounds())) {
				y = tempObject.getY() + 20 ; // Block ũ��
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - height+1; //ĳ����ũ�� +1
				velY=0;
				this.setFalling(false);
				this.setJumping(false); 
			}else 
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - width +4; //ĳ���� ũ�⿵��
			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 16; //Block ũ��
			}
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
	}

	//��ü����
	public Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	//������ ����
	public Rectangle getBoundsRight() {
		return new Rectangle(x+width-10,y+10,5,height-20);
	}
	//���� ����
	public Rectangle getBoundsLeft() {
		return new Rectangle(x+5,y+10,5,height-20);
	}
	//ĳ���� ���κ�
	public Rectangle getBoundsTop() {
		return new Rectangle(x+(width/2)-((width/2)/2),y,width/2,height/2);
	}
	//ĳ���� �Ʒ��κ� ����
	public Rectangle getBoundsBottom() {
		return new Rectangle(x+(width/2)-((width/2)/2),y+(height/2),width/2,height/2);
	}

}
