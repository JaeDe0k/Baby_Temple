package com.babytemple.window;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.babytemple.framework.AddFont;
import com.babytemple.framework.BT_USER_DB;
import com.babytemple.framework.BufferedImageLoader;
import com.babytemple.framework.KeyInput;
import com.babytemple.framework.Music;
import com.babytemple.framework.Texture;
import com.babytemple.login.Login;
import com.babytemple.login.Login_Key;
import com.babytemple.menu.Menu_Mouse;
import com.babytemple.objects.isClear;
import com.babytemple.room.RoomList;

public class Game extends Canvas implements Runnable {
	//��� Class�� UID�� ������ �ִµ�, Class�� ������ ����Ǹ� UID�� ���� �ٲ�⿡
	//����ȭ�Ͽ� ����ϰ� UID������ ����Ѱ� �������� Ȯ���ϴµ� �� ���� �ٲ�� �Ǹ� �ٸ� Class�� �ν��ϰ� �ȴ�.
	//�̸� �����ϱ� ���� ���������� �̸� ��ø� ���ִ� �κ��� �� �κ��̴�.
	//����ȭ: Heap�� ��ġ�� ��ü�� ��� ������ ���·� ����� �۾�
	//������ȭ: ����ȭ�� ��ü�� �ٽ� Heap�� �ֱ� ���� �۾�
	//����ȭ�� ����� ��ü�� Attribute�� ��. (�޼ҵ�� ���� �ּҰ��� �ʿ��ϴ�.)
	private static final long serialVersionUID = 1L;
	
	private boolean isRunning = false; //���� ������ ���ۿ��θ� Ȯ���ϱ� ���� ���
	private Thread thread; //��Ƽ�½�ŷ�� ���� ������ ���� (�����尣 ������ �����͸� ����)
	private Handler handler; //LinkedList�� GameObject ��ü���� ���� �� tick, render �޼ҵ� Ȱ��
	
	public Handler getHandler() {
		return handler;
	}

	private Block_Handler block_handler; //��Ŭ������ �����Ѵٴ°� ����� ���� �ڵ�� ������ Ŭ����
	private isClear isclear = null;
	private static Texture tex; //���� �̹����� �����ϰ� �ִ� Texture Ŭ������ �������� ������ GameŬ�������� ����
	private BufferedImage map = null; //�������� ��� �̹����� �����ϴ� ��
	private BufferedImage main_img = null; //���� �̹����� �����ϴ� ��
	private BufferedImage menu_img = null; //�޴� �̹����� �����ϴ� ��
	private BufferedImageLoader loader = new BufferedImageLoader(); // �����̹����� �̹����� �ҷ����� ���ϰ� �и����ѵ�(����ó�������� �ڵ尡 ����� ����)
	private BufferedImage music_img_on = null;
	private BufferedImage music_img_off = null;
	private Menu_Mouse menu; //�޴� Ŭ���� ����
	private Login login; //�α��� Ŭ���� ����
	private Window window; //JFrame ������ ���� ����
	private RoomList room;
	private BT_USER_DB userDB;
	private KeyInput keyinput;
	private Login_Key login_key;
	
	// ���ǵ�
	private Music musicMain;
	private Music musicLevel1;
	private Music musicLevel2;
	private Music musicLevel3;
	
	public static int LEVEL = 1; //�������� ������ ���� ����(Handler, Block_HandlerŬ�������� ���)
	public static STATE gameState = STATE.Login; // ���ӽ��� ȭ�� ���� (���� ���� �� ������ ���� �־��)
	public Font fontA = new AddFont().getA(); //AddFontŬ������ ����� ������ ��Ʈ�� ����Ҽ� �ְ� fontA�� ����
	public Font fontB = new AddFont().getB();
	public static String userID = "root";
	public static int userIcon = 0;
	public static String p1ID = "";
	public static String p2ID = "";
	public static int p1Icon = 0;
	public static int p2Icon = 0;
	public int networkCount=0;

	//���α׷� ȭ�� ��ȯ�� ���� ȣ�� ������ ���� Enum Ȱ��
	public enum STATE { Login, Menu, Room, Game }
	
	//������(�ѹ��� �����)
	public Game() {
	    this.addMouseListener(new MouseAdapter() {
	    	public void mousePressed(MouseEvent e) {
	    		int mx = e.getX();
	    		int my = e.getY();
	    		if(mouseOver(mx, my, 1150, 5, 32,32)) {
	    			if(Music.isAllLoop) {
	    				closeMusic();
		    			Music.isAllLoop = false;
	    			} else {
	    				if(gameState != STATE.Game) {
	    					musicMain = new Music("MainMusic.mp3");
	    					musicMain.start();
	    				} else if ( LEVEL == 1) {
	    					musicLevel1 = new Music("stage1music.mp3");
	    					musicLevel1.start();
	    				} else if ( LEVEL == 2) {
	    					musicLevel2 = new Music("stage2music.mp3");
	    					musicLevel2.start();
	    				} else if ( LEVEL == 3) {
	    					musicLevel3 = new Music("stage3music.mp3");
	    					musicLevel3.start();
	    				}
	    				Music.isAllLoop = true;
	    			}	
	    		}
	    	}
	    	private boolean mouseOver(int mx, int my, int x, int y, int w, int h) {
	    		if (mx > x && mx < x + w) {
	    			if (my > y && my < y + h) {
	    				return true;
	    			} else
	    				return false;
	    		} else
	    			return false;
	    	}
	    });
	    
	    
		userDB = new BT_USER_DB();
		window = new Window(1200, 960, "BabyTemple",this); //Window Ŭ������ ������ JFrame�� ����
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","1"); //����ó���� ������ �������������� �ý��� Property�� ����
		map = loader.loadImage("/map5.png"); // �������� ��� ����
		main_img = loader.loadImage("/main.png"); //���� ��� ����
		menu_img = loader.loadImage("/menu.png"); //�޴� ��� ����
		music_img_on = loader.loadImage("/musicon.png"); //�������̹���
		music_img_off = loader.loadImage("/musicoff.png"); //���������̹���
		tex = new Texture(); //�ؽ��� ����
		block_handler = new Block_Handler(); //���ڵ鷯 ����
		this.isclear = new isClear(this, window);
		
		handler = new Handler(block_handler, userDB, isclear); //�ڵ鷯 ����(���ڵ鷯 ��ü ����)
		//���� ���� ���Ǽ��� ���� IF�� (���� ȭ�� ���� �� ������ ���)
		
		window.getTcpClient().startClient();
		
		if(gameState == STATE.Login) {
			musicMain = new Music("MainMusic.mp3");
			musicMain.start();
			getLogin();
			} else if(gameState == STATE.Menu) {
				musicMain = new Music("MainMusic.mp3");
				musicMain.start();
				getMenu();
			} else if(gameState == STATE.Room) {
				musicMain = new Music("MainMusic.mp3");
				musicMain.start();
				getRoom();
			} else if(gameState == STATE.Game) {
				gameListener();
				switchLevel(LEVEL);
			}
		
		
        start(); // thread�� �����ϰ� �����ϱ����� �޼ҵ� ����.
	}
	
	// thread ����
	private void start() {
		isRunning = true; //run�޼ҵ忡�� �ݺ������ ���� true
		thread = new Thread(this); //thread ����
		thread.start(); //thread ����
	}
	
	// thread Join
	private void stop() {
		isRunning = false; //run�޼ҵ忡�� ���̻� �ݺ����� �ʰ� false
		try {
			thread.join(); //�ش� thread�� ����Ǳ� ���� ��ٷȴ� �������� �Ѿ��.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// thread�� ���� �Ǵ� ��
	public void run() {
		//�����ӿ� focus ��û
		this.requestFocus();
		long lastTime = System.nanoTime(); //���� ���뼼���� ���� ��ȯ
		double amountOfTicks = 60.0; //�ʴ� 60�� tick()�� �����ϱ� ���� ���� *���Ӽӵ��� ������ �ִ� ����*
		double ns = 1000000000 / amountOfTicks; // 60���� ������ �� 16666666������ ��ŭ tick()�� ȣ��� �ð����̰� �����Ѵ�. (1��(10��ns)/60)
		double delta = 0; //��Ÿ������ �̿��� ������ ������ ������ �ð��� �����ʷ� ã�� ���ϴ� �ð���ŭ ���� �����Ű�� ���� ���� 
		long timer = System.currentTimeMillis(); //1�ʸ��� �󸶳� tick()�� render()�� ȣ��Ǿ����� Ȯ���ϱ����� ������ �ϳ�.
		int frames = 0; //render() ȣ�� Ƚ�� �� �ݺ����� �󸶳� ���ư������� �˱����� ��.
		int updates = 0; //tick() ȣ�� Ƚ���� �˱����� ��
		while(isRunning) { //���ѷ��� (���� isRunning�� false���� �ʴ��̻�)
			long now = System.nanoTime(); //�������� �Ϸ��ϴµ� �ɸ� �ð����� ���
			// �Ѹ���� ��ŸŸ�ӿ� ���� ���ذ� �־���Ѵ�.
			delta += (now - lastTime) / ns;
			lastTime = now; //������ ���۽ð�
			
			while(delta >= 1) { // 1/60�ʰ� ������ ȣ���ϴ� ���� �� 16666666�����ʸ��� ����
				tick(); // �� and ��ȭ �� ������Ʈ 60�ʸ��� (���Ӻ�ȭ�� ���⼭ ����)
				updates++; //tick() ����Ƚ�� 
				render(); //�׷��� ��� ȭ�鿡 �ѷ��ֱ� (ȭ�� ��¿� ������ ��)
				frames++; //render() ����Ƚ�� ��(ȭ����� FPS)
				delta--; //delta �� �ʱ�ȭ
			}
			
			if(System.currentTimeMillis() - timer > 1000) { // (1/1000)�� - timer > 1000 �̶�� ���� (timer�ð����κ��� 1�ʸ��� ����)
				System.out.println("FPS: " + frames + "  TICKS: "+ updates);
				timer += 1000; //timer �ð��� 1�� �������ش�. (1�ʸ��� FPS�� TICKS ȣ��Ƚ���� ����ϱ����ؼ�)
				frames = 0; //�ʱ�ȭ (1�ʸ���)
				updates = 0; //�ʱ�ȭ (1�ʸ���)
				
				window.restartTcpClient(); //1�ʸ��� ��Ʈ��ũ Ȯ��
			}
		}
		stop(); //isRunning �� false�� �Ǿ�� ���ѹݺ��� �����ǰ� stop() �޼ҵ带 ����
	}
	
	//���� �� ��ȭ�ϴ� ������ �����ϱ� ���� �޼ҵ��ε� ȣ��Ƚ���� ���� ���Ӽӵ��� ������ �ش�. (������ ���� ��� ��ǻ�͸��� �ӵ��� �޶�����.)
	public void tick() {
		if(gameState == STATE.Game)
			handler.tick();	
		else if(gameState == STATE.Login)
			login.tick();
		else if(gameState == STATE.Room)
			room.tick();
	}
	
	public void closeMusic() {
		try {
		musicMain.close();
		}catch(Exception e1) {}
		
		try {
		musicLevel1.close();
		}catch(Exception e1) {}
		
		try {
		musicLevel2.close();
		}catch(Exception e1) {}
		
		try {
		musicLevel3.close();
		}catch(Exception e1) {}
	}
	
	//ȭ�鿡 �̹����� �ѷ��ֱ� ���� �޼ҵ��ε� render ȣ��Ƚ���� ���������� FPS�� ������ ���ɸ��°� ó�� ���̰� �ȴ�.
	public void render() {
		// Ʈ���� ���۸� ���� (�޸𸮻� �̹������ۿ� ��ȭó���� �ǽ��� �Ŀ� ������ü�� ȭ�鿡 �ѷ��ִ� ���)
		// ��ǻ�Ϳ����� �׷����� ǥ���ϱ� ���� ���� �ð����� ȭ���� �ѷ�����ϴµ�. (�������� �������� ���� ���ϴ�.)
		// �̹����� �Ѹ��� ����, �����ִ� ���� ȭ���� �����̴� ���� �� flickering�� �߻��ϴµ�
		// ��ǻ�Ͱ� ȭ���� �Ѹ��°Ͱ�, ȭ���� �׷��ִ°��� ���� ���ÿ� �̷������� ���, ���� ��ܿ������� �׷����°��ε�. �ΰ��� ������ ����
		// �̰��� �ʹ� ���� �Ǳ� ����, ���� �� �׷��� �κ��� ���İ��� ä������ �����̴� ��ó�� ���̴°� ����.
		// �ذ��� ���� BufferStrategyŬ������ �̿��� �ش� ���۽������ ������ 2�� ������ �����̴�.
		// ���->(�߰�)->������ ������ ����ȴ�. (3���� �����ϸ� �߰�ȭ�鵵 �����ȴ�. ������ ��������Strategy�� ����ϸ� �� ����.)
		// ���÷��� <--  Front B  <- Show -> Back B <-- Rendering  [�̷� ���·� ������۸��� �̷������.] Page Flipping
		// http://cris.joongbu.ac.kr/course/java/api/java/awt/image/BufferStrategy.html [api ����]
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics(); //BufferStrategy��ü�� getDrawGraphics()�޼ҵ带 ���� �׷����� ��´�.
		
		//////////// Draw ���� ////////////////////////////////////////////
		
		
		//���ӽ������� ���¿� ���� �� Ŭ������ render()�޼ҵ� �׷��� ȣ��
		if(gameState == STATE.Game) {
			g.drawImage(map, 0, 0, 1200, 960, null);
			block_handler.render(g);
			handler.render(g);
		}else if(gameState == STATE.Menu) {
			g.drawImage(menu_img, 0, 0, 1200, 960, null);
			menu.render(g);
		}else if(gameState == STATE.Room) {
			g.drawImage(menu_img, 0, 0, 1200, 960, null);
			room.render(g);
		}else if(gameState == STATE.Login) {
			g.drawImage(main_img, 0, 0, 1200, 960, null);
			login.render(g);
		}
		
		if(Music.isAllLoop)
			g.drawImage(music_img_on, 1150, 5, 32,32, null);
		else
			g.drawImage(music_img_off, 1150, 5, 32,32, null);
		/////////////////////////////////////////////////////////////////
		
		g.dispose(); //Graphics�� �ڿ��� �����ϴ� ��� (�޸� ���� ����)
		bs.show(); //������� �׷����� �̹����� �������� ����(Display)�ش�.
	}
	
	public static Texture getInstance() {
		return tex;
	}
	
	public static void main(String[] args) {
		new Game();
	}
	
	public void getMenu() {
		menu= new Menu_Mouse(this, userDB);
		this.addMouseListener(menu);
	}
	
	public void getLogin() {
		login = new Login(this, userDB);
		login_key = new Login_Key(login);
		this.addMouseListener(login);
		this.addKeyListener(login_key);
	}
	
	public void loginRemove() {
		this.removeKeyListener(login_key);
		this.removeMouseListener(login);
	}
	
	public void gameListener() {
		keyinput = new KeyInput(handler, window);
		this.addMouseListener(isclear);
		this.addKeyListener(keyinput);
	}
	
	public void gameRemoveListener() {
		this.removeKeyListener(keyinput);
		this.removeMouseListener(isclear);
	}
	
	public void getRoom() {
		if(room == null)
			room = new RoomList(this, window, handler);
		this.addMouseListener(room);
		if(RoomList.roomStatus == 3) {
			room.createRoom();
		}
		closeMusic();
		musicMain = new Music("MainMusic.mp3");
		musicMain.start();
	}
	
	public RoomList RgetRoom() {
		return room;
	}
	
	public void getbasicRoom() {
		room = new RoomList(this, window, handler);
		this.addMouseListener(room);
	}
	
	public void switchLevel(int num) {
		closeMusic();
		LEVEL = num;
		if ( LEVEL == 1) {
			musicLevel1 = new Music("stage1music.mp3");
			musicLevel1.start();
		} else if ( LEVEL == 2) {
			musicLevel2 = new Music("stage2music.mp3");
			musicLevel2.start();
		} else if ( LEVEL == 3) {
			musicLevel3 = new Music("stage3music.mp3");
			musicLevel3.start();
		}
		block_handler.switchLevel(); //�� ��ü ���� �� Block_handlerŬ������ object�� ����
		handler.switchLevel(); // �ش� ���������� �ʿ��� ��ü ���� �� handlerŬ������ object�� ����
	}
	
	public void clearLevel() {
		ChatPanel.textareaTeam.setText("�� ä�ù��Դϴ�.\n");
		window.textareaTeam.setText("�� ä�ù��Դϴ�.\n");
		block_handler.clearLevel();
		handler.clearLevel();
	}
	
	public BT_USER_DB getUserDB() {
		return userDB;
	}
	
	public Window getWindow() {
		return window;
	}
	
	public void gameExit() {
		this.clearLevel();
		this.gameRemoveListener();
		ChatPanel.textareaTeam.setText("");
		if(window.getWidth() > 1300)
			window.reduce();
		this.getRoom();
		Game.gameState = STATE.Room;
	}

}
