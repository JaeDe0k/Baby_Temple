package com.babytemple.framework;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class AddFont {
	private Font a = getRix3D();
	private Font b = getMaplestory();
	
	public Font getRix3D() {
	    try {
	    	//입력스트림을 이용해 Font에 저장
	        InputStream is = this.getClass().getResourceAsStream("/RixVideoGame 3D.ttf");
	        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(100f);
	        return font;
	    } catch (FontFormatException | IOException ex) {
	        return null;
	    }
	}
	
	public Font getMaplestory() {
	    try {
	    	//입력스트림을 이용해 Font에 저장
	        InputStream is = this.getClass().getResourceAsStream("/Maplestory.ttf");
	        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(100f);
	        return font;
	    } catch (FontFormatException | IOException ex) {
	        return null;
	    }
	}

	public Font getA() {
		return a;
	}
	public Font getB() {
		return b;
	}
}