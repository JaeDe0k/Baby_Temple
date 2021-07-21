package com.babytemple.login;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login_Key extends KeyAdapter {
	private Login login;

	public Login_Key(Login login) {
		this.login = login;
	}
	
	public void keyPressed(KeyEvent e) {
		int type = login.getType();
		boolean join = login.isJoin();
		boolean pwFind = login.isPwFind();
		
		if (!join && !pwFind) {
			if(type == 0) {
				String tempS = login.getNickText();
				int length = login.getNickText().length();
				if(length < 12 && (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A' || e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a' || e.getKeyChar() <= '9' && e.getKeyChar() >= '0' ))
					login.setNickText(tempS + e.getKeyChar());
				if(e.getKeyCode() == 8 && length != 0) {
					login.setNickText((String)(tempS.substring(0, length-1)));
				}
			}else if(type == 1) {
				String tempS = login.getPwText();
				int length = login.getPwText().length();
				if(length < 12 && (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A' || e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a' || e.getKeyChar() <= '9' && e.getKeyChar() >= '0' ))
					login.setPwText(tempS + e.getKeyChar());
				if(e.getKeyCode() == 8 && length != 0) {
					login.setPwText((String)(tempS.substring(0, length-1)));
				}
			}
		}
		
		if(join) {
			if(type == 0) {
				String tempS = login.getJ_nickText();
				int length = login.getJ_nickText().length();
				if(length < 12 && (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A' || e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a' || e.getKeyChar() <= '9' && e.getKeyChar() >= '0' ))
					login.setJ_nickText(tempS + e.getKeyChar());
				if(e.getKeyCode() == 8 && length != 0) {
					login.setJ_nickText((String)(tempS.substring(0, length-1)));
				}
			}else if(type == 1) {
				String tempS = login.getJ_pwText();
				int length = login.getJ_pwText().length();
				if(length < 12 && (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A' || e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a' || e.getKeyChar() <= '9' && e.getKeyChar() >= '0' ))
					login.setJ_pwText(tempS + e.getKeyChar());
				if(e.getKeyCode() == 8 && length != 0) {
					login.setJ_pwText((String)(tempS.substring(0, length-1)));
				}
			}
		}
		
		if(pwFind) {
			if(type == 0) {
				String tempS = login.getJ_nickText();
				int length = login.getJ_nickText().length();
				if(length < 12 && (e.getKeyChar() <= 'Z' && e.getKeyChar() >= 'A' || e.getKeyChar() <= 'z' && e.getKeyChar() >= 'a' || e.getKeyChar() <= '9' && e.getKeyChar() >= '0' ))
					login.setJ_nickText(tempS + e.getKeyChar());
				if(e.getKeyCode() == 8 && length != 0) {
					login.setJ_nickText((String)(tempS.substring(0, length-1)));
				}
			}
		}
		
	}
}