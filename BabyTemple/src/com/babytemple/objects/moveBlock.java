package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.window.Block_Handler;
import com.babytemple.window.Handler;

public class moveBlock extends GameObject {
	GameObject Player1;
	GameObject Player2;
	private Stream<GameObject> stream;
	private float gravity = 0.5f; //�߷�
	private final float MAX_SPEED =3; // �ִ볫�ϼӵ�
	private Block_Handler block_handler;
	private Handler handler;
	private BufferedImage img = new BufferedImageLoader().loadImage("/gray.png");
	public moveBlock(int x, int y, ID id, Block_Handler block_handler, Handler handler) {
		super(x, y, id);
		this.block_handler = block_handler;
		this.handler = handler;
	}

	//������
	public void tick() {
		y += velY;
		x += velX;
		
		if(this.isFalling()) {
			velY += gravity;
			if(velY > MAX_SPEED)
				velY = MAX_SPEED; //�ִ� �߷°��ӵ�
		}
		
		//�� ��ü �浹����
		stream = block_handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_Block(e);
		});
		
		stream = handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_Elevator(e); //Device ��ü �浹����
			if(e.getType()==ID.Angel)
				Player1 = e;
			if(e.getType()==ID.Devil)
				Player2 = e;
			
		});
		if (Player1 != null && Player2 != null)
			Collision_Player(Player1, Player2);
	}
	
	public void Collision_Player(GameObject Angel, GameObject Devil) {
		if(getBoundsLeft().intersects(Angel.getBounds()) || getBoundsLeft().intersects(Devil.getBounds()))
			velX = 2;
		else if(getBoundsRight().intersects(Angel.getBounds()) || getBoundsRight().intersects(Devil.getBounds()))
			velX = -2;
		else
			velX = 0;
	}
	
	//����������
	public void Collision_Elevator(GameObject tempObject) {
		if(tempObject.getId() == ID.Device) {
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - 30+2; 
				velY=0;
				this.setFalling(false);
			}else
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - 30;
			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 150;
			}
		}
	}
	
	public void Collision_Block(GameObject tempObject) {
		if(tempObject.getId() == ID.Block) {
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - 30+1; //ĳ����ũ�� +1
				velY=0;
				this.setFalling(false);
			}else 
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - 60; //ĳ���� ũ�⿵��
			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 30; //Block ũ��
			}
		}
	}

	//ȭ�鿡 ǥ�����ִ°�
	public void render(Graphics g) {
		g.drawImage(img, x,y, 30, 30, null);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 30, 30);
	}
	
	//������ ����
	public Rectangle getBoundsRight() {
		return new Rectangle(x+21, y+7, 8, 16);
	}
	
	//���� ����
	public Rectangle getBoundsLeft() {
		return new Rectangle(x, y+7, 8, 16);
	}

	//ĳ���� �Ʒ��κ� ����
	public Rectangle getBoundsBottom() {
		return new Rectangle(x+5, y+20, 20, 9);
	}

}