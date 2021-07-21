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
	private int width = 120; //캐릭터 가로 크기
	private int height = 120; // 캐릭터 높이 크기
	private float gravity = 1f; //중력
	private final float MAX_SPEED =6; // 최대낙하속도
	private int facing; //0=왼쪽, 1=오른쪽
	private Texture tex = Game.getInstance();
	private Animation playerWalkJump, playerWalkStop, playerWalkLeft, playerWalkRight; //캐릭터 애니메이션 왼쪽, 오른쪽
	
	//보조 ID type을 이용해 천사,악마를 선택, 블럭과 각 객체의 충돌확인을 위해 handler들값 전달받기
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
		//방향 전환 및 속도 영향
		x += velX;
		y += velY;
		
		//캐릭터 이미지의 왼쪽 오른쪽을 확인하기 위한 설정
		if(velX < 0) facing = 1;
		else if(velX > 0) facing = 0;
		
		//중력
		if(this.isFalling() || this.isJumping()) {
			velY += gravity;
			if(velY > MAX_SPEED)
				velY = MAX_SPEED; //최대 중력가속도
		}
		
		//핸들러의 값에 따른 이동
		if(this.isRight()) velX = 5;
		else if (!this.isLeft()) velX=0;
		if(this.isLeft()) velX = -5;
		else if (!this.isRight()) velX=0;
		
		Collision();
		playerWalkLeft.runAnimation(); //왼쪽모션
		playerWalkRight.runAnimation(); //오른쪽모션
		playerWalkJump.runAnimation(); //점프모션
		playerWalkStop.runAnimation(); //멈춰있는 모션
	}
	
	//충돌시 올라섬
	public void Collision() {
		//TOP
		if(getBoundsTop().intersects(getBlockUp())) {
			y = 140 + 12 ; // Block 크기
			velY=0;
		}
		//BOTTOM
		if(getBoundsBottom().intersects(getBlockDown())) {
			y = 539 - height+1; //캐릭터크기 +1
			velY=0;
			this.setFalling(false);
			this.setJumping(false); 
		}else 
			this.setFalling(true);
		
		//RIGHT
		if(getBoundsRight().intersects(getBlockRight())) {
			x = 781 - width +4; //캐릭터 크기영향
		}
		
		//LEFT
		if(getBoundsLeft().intersects(getBlockLeft())) {
			x = 40 + 12; //Block 크기
		}
	}

	public void render(Graphics g) {
		DrawAngel(g); // 천사 or 악마 Draw
//		DrawCollision(g); //캐릭터 충돌 범위 표시
	}
	
	//Angel Draw
	public void DrawAngel(Graphics g) {
		//이미지 좌표및 크기설정
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
	
	//캐릭터 충돌 범위 표시
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
		
		g.drawRect(48, 140, 740, 12); //위
		g.drawRect(48, 539, 740, 12); //아래
		g.drawRect(40, 140, 13, 410); //왼쪽
		g.drawRect(781, 140, 13, 410); //오른쪽
	}

	//전체영역
	public Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	//오른쪽 영역
	public Rectangle getBoundsRight() {
		return new Rectangle(x+width-30,y+10,28,height-20);
	}
	//왼쪽 영역
	public Rectangle getBoundsLeft() {
		return new Rectangle(x+2,y+10,28,height-20);
	}
	//캐릭터 윗부분
	public Rectangle getBoundsTop() {
		return new Rectangle(x+(width/2)-((width/2)/2),y,width/2,height/2);
	}
	//캐릭터 아래부분 영역
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
