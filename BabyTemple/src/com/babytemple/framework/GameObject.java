package com.babytemple.framework;

import java.awt.Graphics;
import java.awt.Rectangle;

//���� ������Ʈ �߻� Ŭ���� [�����Ǵ� ��� ��ü�� �⺻�� �ȴ�.]
public abstract class GameObject {
	protected int x, y; //��ǥ����
	protected float velX = 0, velY = 0; //�̵��ӵ�
	protected ID id; //��ü ID
	protected ID type; //���� ID�� [���� ���Ǽ��� ���� ���� ������ enum Ŭ���� ����]
	protected int typeInt; //Ÿ��
	//[����Ǵ� �����̹Ƿ� �� �� �����ʿ�]
	protected boolean cb = true; //Green ��ü
	protected boolean ab = true; //DB ��ü
	
	//[ĳ���� ���� �� ���� ���� --> ����(����)������]
	protected boolean right = false, left = false;
	protected boolean jumping = true;
	protected boolean falling = true;

	//[��� ��ü�� �����ڿ� (��ġ)��ǥ, ��üID�� �⺻�����Ѵ�.]
	public GameObject(int x, int y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	//�߻�ȭ �޼ҵ� *(�޼ҵ� �������̵� �ʿ�)* tick:��ȭ�� ��(������, ��) render:ȭ�� ǥ��  Rectangle: �浹���� �뵵
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();
	
	//[GameObjectŬ������ ��� ������ ������]
	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public ID getType() {
		return type;
	}
	
	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}
}
