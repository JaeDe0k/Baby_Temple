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
	private BT_USER_DB userDB; //DB객체
	private Stream<GameObject> stream;
	private int width = 50; //캐릭터 가로 크기
	private int height = 50; // 캐릭터 높이 크기
	private float gravity = 0.5f; //중력
	private final float MAX_SPEED =3; // 최대낙하속도
	private int facing; //0=왼쪽, 1=오른쪽
	private Texture tex = Game.getInstance();
	private Block_Handler block_handler;
	private Animation playerWalkJump, playerWalkStop, playerWalkLeft, playerWalkRight; //캐릭터 애니메이션 왼쪽, 오른쪽
	private boolean aoo;
	//보조 ID type을 이용해 천사,악마를 선택, 블럭과 각 객체의 충돌확인을 위해 handler들값 전달받기
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
		if(this.isRight()) velX = 2;
		else if (!this.isLeft()) velX=0;
		if(this.isLeft()) velX = -2;
		else if (!this.isRight()) velX=0;
		
		
		Collision(); //충돌감지
		playerWalkLeft.runAnimation(); //왼쪽모션
		playerWalkRight.runAnimation(); //오른쪽모션
		playerWalkJump.runAnimation(); //점프모션
		playerWalkStop.runAnimation(); //멈춰있는 모션
	}
	
	//충돌감지
	private void Collision() {
		stream = handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_ev(e); //ev 객체 충돌여부
			Collision_Elevator(e); //Device 객체 충돌여부
			Collision_Death(e); //사망객체 충돌여부
			Collision_clear(e);
			Collision_hideBlock(e);
			Collision_door(e);
		});
		
		//블럭 객체 충돌여부
		stream = block_handler.getObject().parallelStream();
		stream.forEach(e -> {
			Collision_Block(e);
		});
	}
	
	public void Collision_door(GameObject e) {
		if(e.getId()==ID.Door) {
			//BOTTOM
			if(getBoundsBottom().intersects(e.getBounds())) {
				y = e.getY() - height+1; //캐릭터크기 +1
				velY=0;
				this.setFalling(false);
				this.setJumping(false); 
			}else 
				this.setFalling(true);
			//TOP
			if(getBoundsTop().intersects(e.getBounds())) {
				y = e.getY() + 20 ; // Block 크기
				velY=0;
			}
			//RIGHT
			if(getBoundsRight().intersects(e.getBounds())) {
				x = e.getX() - width +4; //캐릭터 크기영향
			}
			//LEFT
			if(getBoundsLeft().intersects(e.getBounds())) {
				x = e.getX()+10; //Block 크기
			}
		}
	}
	
	public void Collision_hideBlock(GameObject e) {
		if(e.getId()==ID.hideBlock) {
			if(getBoundsTop().intersects(e.getBounds())) {
				y = e.getY() + 20 ; // 해당객체 높이만큼
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(e.getBounds())) {
				y = e.getY() - height +1; // 캐릭터크기만큼 y값에서 올리기 +1은 점프인식빠르게
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
				x = e.getX() - width +4; //캐릭터 크기영향
			}
			
			//LEFT
			if(getBoundsLeft().intersects(e.getBounds())) {
				x = e.getX() + 60; //Block 크기
			}
		}
	}
	
	public void Collision_ev(GameObject tempObject) {
		if(tempObject.getId() == ID.EV) {
			//TOP
			if(getBoundsTop().intersects(tempObject.getBounds())) {
				y = tempObject.getY() + 10 ; // 해당객체 높이만큼
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - height +1; // 캐릭터크기만큼 y값에서 올리기 +1은 점프인식빠르게
				velY=0;
				this.setFalling(false);
				this.setJumping(false);
			}else 
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - width +4; //캐릭터 크기영향

			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 96; //Block 크기
			}
		}
	}
	
	//충돌시 죽음 (미해결버그 딜레이가 없을 시 오류 발생)
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
			//스위치 레벨
			if(getBounds().intersects(tempObject.getBounds())) {
				Handler.clearAngel = true;
			} else
				Handler.clearAngel = false;
		} else if(this.type == ID.Devil && tempObject.getId() == ID.devilEnemy) {
			//스위치 레벨
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
	
	//엘레베이터
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
	//충돌시 올라섬
	public void Collision_Block(GameObject tempObject) {
		if(tempObject.getId() == ID.Block) {
			//TOP
			if(getBoundsTop().intersects(tempObject.getBounds())) {
				y = tempObject.getY() + 20 ; // Block 크기
				velY=0;
			}
			//BOTTOM
			if(getBoundsBottom().intersects(tempObject.getBounds())) {
				y = tempObject.getY() - height+1; //캐릭터크기 +1
				velY=0;
				this.setFalling(false);
				this.setJumping(false); 
			}else 
				this.setFalling(true);
			
			//RIGHT
			if(getBoundsRight().intersects(tempObject.getBounds())) {
				x = tempObject.getX() - width +4; //캐릭터 크기영향
			}
			
			//LEFT
			if(getBoundsLeft().intersects(tempObject.getBounds())) {
				x = tempObject.getX() + 16; //Block 크기
			}
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
	}

	//전체영역
	public Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	//오른쪽 영역
	public Rectangle getBoundsRight() {
		return new Rectangle(x+width-10,y+10,5,height-20);
	}
	//왼쪽 영역
	public Rectangle getBoundsLeft() {
		return new Rectangle(x+5,y+10,5,height-20);
	}
	//캐릭터 윗부분
	public Rectangle getBoundsTop() {
		return new Rectangle(x+(width/2)-((width/2)/2),y,width/2,height/2);
	}
	//캐릭터 아래부분 영역
	public Rectangle getBoundsBottom() {
		return new Rectangle(x+(width/2)-((width/2)/2),y+(height/2),width/2,height/2);
	}

}
