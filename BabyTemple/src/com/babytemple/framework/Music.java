package com.babytemple.framework;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class Music extends Thread{

   private Player player;
   public static boolean isAllLoop = true;
   public boolean isLoop = true; //°î ¹Ýº¹Àç»ý
   private File file;
   private FileInputStream fis;
   private BufferedInputStream bis;
   public Music(String name) {
      try {
         file = new File(getClass().getResource("/"+ name).toURI());
         fis = new FileInputStream(file);
         bis = new BufferedInputStream(fis);
         player = new Player(bis);
         
      }catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
   
   public int getTime() {
      if (player == null)
         return 0;
      return player.getPosition();
   }
   public void close() {
      isLoop = false;
      player.close();
      this.interrupt();
   }
   public void run() {
      try {
         while(isLoop && isAllLoop) {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            player.play();
         }
      }catch(Exception e) {
         System.out.println(e.getMessage());
      }
      
   }
}