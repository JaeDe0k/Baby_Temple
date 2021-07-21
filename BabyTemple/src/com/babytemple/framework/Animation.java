package com.babytemple.framework;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Animation {
	private int speed; //���������� (�ӵ�)
	private int frames; //�����Ӽ� (�̹��� ��)
	
	private int index = 0; //�������
	private int count = 0; //������Ʈ����
	
	private BufferedImage[] images; // �̹��� �迭 ����
	private BufferedImage currentImg; // ���� ǥ�� �̹���
	
	//�����μ�(Varargs)�� �̿��� �޼ҵ�
	public Animation(int speed, BufferedImage... args) {
		this.speed = speed;
		images = new BufferedImage[args.length];
		for(int i=0;i<args.length;i++) {
			images[i] = args[i];
		}
		frames = args.length;
	}
	
	//���ִϸ��̼�. (index �ӵ��� speed ���� �������)
	public void runAnimation() {
		index++;
		if(index > speed) {
			index =0;
			nextFrame();
		}
	}
	
	//���� ĳ���� ��� ��ȯ�� ī���Ͱ� �����Ӻ��� �������� count �ʱ�ȭ
	public void nextFrame() {
		for(int i=0; i<frames;i++) {
			if(count == i)
				currentImg = images[i];
		}
		count++;
		if(count > frames)
			count=0;
	}
	
	public void drawAnimation(Graphics g, int x, int y, int scaleX, int scaleY) {
		g.drawImage(currentImg, x,y,scaleX,scaleY, null);
	}
}
