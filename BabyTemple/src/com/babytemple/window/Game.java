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
	//모든 Class는 UID를 가지고 있는데, Class의 내용이 변경되면 UID값 역시 바뀌기에
	//직렬화하여 통신하고 UID값으로 통신한게 정상인지 확인하는데 그 값이 바뀌게 되면 다른 Class로 인식하게 된다.
	//이를 방지하기 위해 고유값으로 미리 명시를 해주는 부분이 이 부분이다.
	//직렬화: Heap에 위치한 객체를 출력 가능한 상태로 만드는 작업
	//역직렬화: 직렬화된 객체를 다시 Heap에 넣기 위한 작업
	//직렬화의 대상은 객체의 Attribute의 값. (메소드는 그저 주소값만 필요하다.)
	private static final long serialVersionUID = 1L;
	
	private boolean isRunning = false; //메인 스레드 동작여부를 확인하기 위해 사용
	private Thread thread; //멀티태스킹을 위한 스레드 생성 (스레드간 동일한 데이터를 공유)
	private Handler handler; //LinkedList로 GameObject 객체들을 저장 및 tick, render 메소드 활용
	
	public Handler getHandler() {
		return handler;
	}

	private Block_Handler block_handler; //블럭클래스만 관리한다는거 빼고는 위와 코드는 동일한 클래스
	private isClear isclear = null;
	private static Texture tex; //여러 이미지를 저장하고 있는 Texture 클래스를 정적으로 선언해 Game클래스에서 공유
	private BufferedImage map = null; //스테이지 배경 이미지를 저장하는 곳
	private BufferedImage main_img = null; //메인 이미지를 저장하는 곳
	private BufferedImage menu_img = null; //메뉴 이미지를 저장하는 곳
	private BufferedImageLoader loader = new BufferedImageLoader(); // 버퍼이미지의 이미지를 불러오기 편하게 분리시켜둠(예외처리때문에 코드가 길어짐 방지)
	private BufferedImage music_img_on = null;
	private BufferedImage music_img_off = null;
	private Menu_Mouse menu; //메뉴 클래스 선언
	private Login login; //로그인 클래스 선언
	private Window window; //JFrame 수정을 위해 넣음
	private RoomList room;
	private BT_USER_DB userDB;
	private KeyInput keyinput;
	private Login_Key login_key;
	
	// 음악들
	private Music musicMain;
	private Music musicLevel1;
	private Music musicLevel2;
	private Music musicLevel3;
	
	public static int LEVEL = 1; //스테이지 선택을 위한 레벨(Handler, Block_Handler클래스에서 사용)
	public static STATE gameState = STATE.Login; // 게임시작 화면 선택 (편한 개발 및 진행을 위해 넣어둠)
	public Font fontA = new AddFont().getA(); //AddFont클래스에 저장된 별도의 폰트를 사용할수 있게 fontA에 저장
	public Font fontB = new AddFont().getB();
	public static String userID = "root";
	public static int userIcon = 0;
	public static String p1ID = "";
	public static String p2ID = "";
	public static int p1Icon = 0;
	public static int p2Icon = 0;
	public int networkCount=0;

	//프로그램 화면 전환에 따른 호출 선택을 위한 Enum 활용
	public enum STATE { Login, Menu, Room, Game }
	
	//생성자(한번만 실행됨)
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
		window = new Window(1200, 960, "BabyTemple",this); //Window 클래스를 생성해 JFrame를 생성
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","1"); //병렬처리할 스레드 개수설정을위해 시스템 Property값 변경
		map = loader.loadImage("/map5.png"); // 스테이지 배경 저장
		main_img = loader.loadImage("/main.png"); //메인 배경 저장
		menu_img = loader.loadImage("/menu.png"); //메뉴 배경 저장
		music_img_on = loader.loadImage("/musicon.png"); //뮤직온이미지
		music_img_off = loader.loadImage("/musicoff.png"); //뮤직오프이미지
		tex = new Texture(); //텍스쳐 생성
		block_handler = new Block_Handler(); //블럭핸들러 생성
		this.isclear = new isClear(this, window);
		
		handler = new Handler(block_handler, userDB, isclear); //핸들러 생성(블럭핸들러 객체 공유)
		//게임 개발 편의성을 위한 IF문 (시작 화면 설정 및 리스너 등록)
		
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
		
		
        start(); // thread를 생성하고 시작하기위한 메소드 실행.
	}
	
	// thread 시작
	private void start() {
		isRunning = true; //run메소드에서 반복허용을 위해 true
		thread = new Thread(this); //thread 생성
		thread.start(); //thread 시작
	}
	
	// thread Join
	private void stop() {
		isRunning = false; //run메소드에서 더이상 반복하지 않게 false
		try {
			thread.join(); //해당 thread가 종료되기 까지 기다렸다 다음으로 넘어간다.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// thread가 실행 되는 곳
	public void run() {
		//프레임에 focus 요청
		this.requestFocus();
		long lastTime = System.nanoTime(); //현재 나노세컨즈 값을 반환
		double amountOfTicks = 60.0; //초당 60번 tick()를 실행하기 위한 변수 *게임속도에 영향을 주니 주의*
		double ns = 1000000000 / amountOfTicks; // 60으로 나누면 약 16666666나노초 만큼 tick()를 호출당 시간차이가 나게한다. (1초(10억ns)/60)
		double delta = 0; //델타변수를 이용해 마지막 프레임 사이의 시간을 나노초로 찾아 원하는 시간만큼 나눠 실행시키기 위한 변수 
		long timer = System.currentTimeMillis(); //1초마다 얼마나 tick()와 render()이 호출되었는지 확인하기위한 변수중 하나.
		int frames = 0; //render() 호출 횟수 및 반복문이 얼마나 돌아갔는지를 알기위해 씀.
		int updates = 0; //tick() 호출 횟수를 알기위해 씀
		while(isRunning) { //무한루프 (따로 isRunning를 false하지 않는이상)
			long now = System.nanoTime(); //프레임을 완료하는데 걸린 시간으로 사용
			// 한마디로 델타타임에 대한 이해가 있어야한다.
			delta += (now - lastTime) / ns;
			lastTime = now; //프레임 시작시간
			
			while(delta >= 1) { // 1/60초가 지나면 호출하는 조건 즉 16666666나노초마다 실행
				tick(); // 값 and 변화 등 업데이트 60초마다 (게임변화를 여기서 실행)
				updates++; //tick() 실행횟수 
				render(); //그래픽 요소 화면에 뿌려주기 (화면 출력에 영향을 줌)
				frames++; //render() 실행횟수 즉(화면송출 FPS)
				delta--; //delta 값 초기화
			}
			
			if(System.currentTimeMillis() - timer > 1000) { // (1/1000)초 - timer > 1000 이라면 실행 (timer시간으로부터 1초마다 실행)
				System.out.println("FPS: " + frames + "  TICKS: "+ updates);
				timer += 1000; //timer 시간을 1초 증가해준다. (1초마다 FPS와 TICKS 호출횟수를 출력하기위해서)
				frames = 0; //초기화 (1초마다)
				updates = 0; //초기화 (1초마다)
				
				window.restartTcpClient(); //1초마다 네트워크 확인
			}
		}
		stop(); //isRunning 가 false가 되어야 무한반복이 중지되고 stop() 메소드를 실행
	}
	
	//게임 내 변화하는 값들을 실행하기 위한 메소드인데 호출횟수에 따라 게임속도에 영향을 준다. (제한이 없을 경우 컴퓨터마다 속도가 달라진다.)
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
	
	//화면에 이미지를 뿌려주기 위한 메소드인데 render 호출횟수가 낮아질수록 FPS가 떨어져 렉걸리는것 처럼 보이게 된다.
	public void render() {
		// 트리플 버퍼링 구현 (메모리상 이미지버퍼에 묘화처리를 실시한 후에 버퍼전체를 화면에 뿌려주는 방식)
		// 컴퓨터에서는 그래픽을 표시하기 위해 일정 시간마다 화면을 뿌려줘야하는데. (움직임이 있을때는 더욱 심하다.)
		// 이미지를 뿌리다 보면, 자주있는 일이 화면이 깜빡이는 현상 즉 flickering가 발생하는데
		// 컴퓨터가 화면을 뿌리는것과, 화면을 그려주는것이 서로 동시에 이러워져서 사실, 좌측 상단에서부터 그려지는것인데. 인간의 눈으로 보면
		// 이것이 너무 빨리 되기 때문, 아직 안 그려진 부분이 순식간에 채워져서 깜빡이는 것처럼 보이는것 같다.
		// 해결을 위해 BufferStrategy클래스를 이용해 해당 버퍼스토로지 영역을 2개 생성한 상태이다.
		// 배경->(중간)->전면의 순으로 진행된다. (3개를 생성하면 중간화면도 구성된다. 하지만 적은버퍼Strategy로 사용하면 더 좋다.)
		// 디스플레이 <--  Front B  <- Show -> Back B <-- Rendering  [이런 형태로 더블버퍼링은 이루어진다.] Page Flipping
		// http://cris.joongbu.ac.kr/course/java/api/java/awt/image/BufferStrategy.html [api 참조]
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics(); //BufferStrategy객체의 getDrawGraphics()메소드를 통해 그래픽을 얻는다.
		
		//////////// Draw 영역 ////////////////////////////////////////////
		
		
		//게임스테이지 상태에 따라 각 클래스의 render()메소드 그래픽 호출
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
		
		g.dispose(); //Graphics의 자원을 해제하는 기능 (메모리 낭비 감소)
		bs.show(); //백버퍼의 그려놓은 이미지를 전면으로 보여(Display)준다.
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
		block_handler.switchLevel(); //블럭 객체 생성 및 Block_handler클래스의 object에 저장
		handler.switchLevel(); // 해당 스테이지에 필요한 객체 생성 및 handler클래스의 object에 저장
	}
	
	public void clearLevel() {
		ChatPanel.textareaTeam.setText("팀 채팅방입니다.\n");
		window.textareaTeam.setText("팀 채팅방입니다.\n");
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
