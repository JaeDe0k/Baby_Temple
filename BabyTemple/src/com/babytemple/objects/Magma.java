package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.window.Handler;

public class Magma extends GameObject{
    Handler handler;
    private BufferedImage img;
    int w ;
    int h ;

    public Magma(int x, int y, ID id , int w , int h) {
        super(x, y, id);
        img = new BufferedImageLoader().loadImage("/Magma1.png");
        this.w=w;
        this.h=h;
    }

    public void tick() { 

    }

    public void render(Graphics g) { //메모리상그리는거
        
        g.drawImage(img, x, y, w, h, null); 
    }

    public Rectangle getBounds() {
        return new Rectangle(x,y,w,h);
    }
}