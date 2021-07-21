package com.babytemple.objects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.window.Handler;

public class Item extends GameObject{
    Handler handler;
    private BufferedImage img;
    private Stream<GameObject> stream;
    public Item(int x, int y, ID id, Handler handler, ID type) {
        super(x, y, id);
        this.handler = handler;
        this.type = type;
        if(type == ID.angelItem)
        	img = new BufferedImageLoader().loadImage("/item1.png");
        if(type == ID.devilItem)
		img = new BufferedImageLoader().loadImage("/item2.png");
    }

    public void tick() {
        Collision(); //충돌감지
    }

    public void render(Graphics g) { //메모리상그리는거
    	g.drawImage(img, x ,y, 25, 40, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x,y,20,30);

    }
    private void Collision() {
    	stream = handler.getObject().parallelStream();
		stream.forEach(e -> {
			if(type == ID.angelItem) {
				if(e.getType() == ID.Angel )
	                if(getBounds().intersects(e.getBounds())) {
	                    handler.removeObject(this);
	                    Handler.score++;
	                    System.out.println("천사아이템습득");
	                }
			}
			
			if(type == ID.devilItem) {
				if(e.getType() == ID.Devil )
	                if(getBounds().intersects(e.getBounds())) {
	                    handler.removeObject(this);
	                    Handler.score++;
	                    System.out.println("악마아이템습득");
	                }
			}
		});
    }
}