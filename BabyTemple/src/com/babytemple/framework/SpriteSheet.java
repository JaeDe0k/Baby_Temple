package com.babytemple.framework;
import java.awt.image.BufferedImage;

public class SpriteSheet {
private BufferedImage image;

	public SpriteSheet(BufferedImage image) {
		this.image = image;
	}
	
	//���ϴ� ��ġ�� ũ���� �̹����� �ڸ������� �޼ҵ�
	public BufferedImage grabImage(int col, int row, int width, int height) {
		BufferedImage img = image.getSubimage((col*width)-width, (row*height)-height, width, height);
		return img;
	}
}
