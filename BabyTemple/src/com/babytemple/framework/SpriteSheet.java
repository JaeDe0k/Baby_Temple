package com.babytemple.framework;
import java.awt.image.BufferedImage;

public class SpriteSheet {
private BufferedImage image;

	public SpriteSheet(BufferedImage image) {
		this.image = image;
	}
	
	//원하는 위치와 크기의 이미지를 자르기위한 메소드
	public BufferedImage grabImage(int col, int row, int width, int height) {
		BufferedImage img = image.getSubimage((col*width)-width, (row*height)-height, width, height);
		return img;
	}
}
