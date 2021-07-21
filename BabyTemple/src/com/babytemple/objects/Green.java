package com.babytemple.objects;
import java.awt.Graphics;
import java.awt.Rectangle;
import com.babytemple.framework.GameObject;
import com.babytemple.framework.ID;
import com.babytemple.framework.Texture;
import com.babytemple.window.Game;
import com.babytemple.window.Handler;

public class Green extends GameObject {
    Handler handler;
    boolean cc =false;
    private Texture tex = Game.getInstance();
    
    public Green(int x, int y, ID id, Handler handler, boolean cb) {
        super(x, y, id);
        this.handler = handler;
        this.cb = cb;
    }
    boolean a=true;


    public void tick() { 
        // ------ 자동으로 움직이는 조건
        if  (cb){
            if (!cc)
                velY = 1;
            cc = true;
            if (x == 800)
                a = true;
            if (x == 920)
                a = false;

            if (a) { velY = 1; x += velY;}
            if (!a) { velY = -1; x += velY;}
        }
        // ---------------------------- 
    }

    public void render(Graphics g) { //메모리상그리는거
       
        g.drawImage(tex.Monster[0], x, y, 128, 80, null);
        g.drawImage(tex.Monster[1], x+128, y, 128, 80, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x,y,30,30);
    }


}