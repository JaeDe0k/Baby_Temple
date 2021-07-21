package com.babytemple.framework;

import java.awt.Graphics;
import java.awt.Rectangle;

//게임 오브젝트 추상 클래스 [생성되는 모든 객체의 기본이 된다.]
public abstract class GameObject {
	protected int x, y; //좌표설정
	protected float velX = 0, velY = 0; //이동속도
	protected ID id; //객체 ID
	protected ID type; //보조 ID값 [관리 편의성을 위한 추후 별도의 enum 클래스 생성]
	protected int typeInt; //타입
	//[낭비되는 변수이므로 추 후 삭제필요]
	protected boolean cb = true; //Green 객체
	protected boolean ab = true; //DB 객체
	
	//[캐릭터 조작 및 점프 여부 --> 수정(삭제)생각중]
	protected boolean right = false, left = false;
	protected boolean jumping = true;
	protected boolean falling = true;

	//[모든 객체는 생성자에 (배치)좌표, 객체ID를 기본으로한다.]
	public GameObject(int x, int y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	//추상화 메소드 *(메소드 오버라이드 필요)* tick:변화줄 때(움직임, 등) render:화면 표시  Rectangle: 충돌영역 용도
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();
	
	//[GameObject클래스의 모든 접근자 설정자]
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
