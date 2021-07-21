package serverNetwork;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// 서버 관리 GUI
public class ServerGui extends JFrame{
	private static final long serialVersionUID = 1L;

	public static List<roomInfo> roomList;

	JTextArea tcpDisplay;
	JScrollPane tcpScrollPane;
	JButton tcpButton;
	
	JTextArea udpDisplay;
	JScrollPane udpScrollPane;
	JButton udpButton;
	
	tcpServer TCP;
	udpServer UDP;
	
	GridLayout gridLayout = new GridLayout(1, 2);
	public ServerGui() {
		roomList = Collections.synchronizedList(new ArrayList<roomInfo>());
		TCP = new tcpServer(this);
		UDP = new udpServer(this);
		this.setSize(800, 400);
		tcpButton = new JButton("tcpStart");
		tcpButton.addActionListener(e-> {
			if(tcpButton.getText() == "tcpStart")
				TCP.startServer();
			if(tcpButton.getText() == "tcpStop")
				TCP.stopServer();
		});
		
		udpButton = new JButton("udpStart");
		udpButton.addActionListener(e-> {
			if(udpButton.getText() == "udpStart")
				UDP.startServer();
			if(udpButton.getText() == "udpStop")
				UDP.stopServer();
		});
		
		tcpDisplay = new JTextArea();
		tcpDisplay.setEditable(false);
		tcpDisplay.setLineWrap(true);
		tcpScrollPane = new JScrollPane(tcpDisplay);
		tcpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); //As needed 즉 필요에의해서 내용이 많아지면 스크롤 바가 생긴다 
		tcpScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //가로 스크롤은 안만든다
		
		udpDisplay = new JTextArea();
		udpDisplay.setEditable(false);
		udpDisplay.setLineWrap(true);
		udpScrollPane = new JScrollPane(udpDisplay);
		
		udpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); //As needed 즉 필요에의해서 내용이 많아지면 스크롤 바가 생긴다 
		udpScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //가로 스크롤은 안만든다
		
		
		JPanel Pbutton = new JPanel();
		Pbutton.setLayout(gridLayout);
		Pbutton.add(tcpButton);
		Pbutton.add(udpButton);
		
		JPanel PDisplay = new JPanel();
		PDisplay.setLayout(gridLayout);
		PDisplay.add(tcpScrollPane);
		PDisplay.add(udpScrollPane);
		
		
		this.add(PDisplay, BorderLayout.CENTER);
		this.add(Pbutton, BorderLayout.SOUTH);
		
		
		this.setTitle("Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	TCP.stopServer();
            	UDP.stopServer();
            }
        });
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		new ServerGui();
	}

}
