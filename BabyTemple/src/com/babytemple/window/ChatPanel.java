package com.babytemple.window;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ChatPanel extends JPanel {
	public static JScrollPane ViewTextTeam;
	public static JTextField textfieldTeam;
	public static JTextArea textareaTeam = new JTextArea();
	private Game game;
	private Font fontA;
	private Font fontB;
	private Image background = new ImageIcon(getClass().getResource("/gameChat.png")).getImage();
	private ImageIcon button = new ImageIcon(getClass().getResource("/out.png"));
	
	public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background,0,0,getWidth(),getHeight(),this);
        g.setFont(fontA.deriveFont(26f));
        
        g.drawImage(Game.getInstance().Icon[Game.p1Icon], 33, 131, 72, 72, null);
        g.drawString("��"+Game.p1ID, 125, 200);
        
        g.drawImage(Game.getInstance().Icon[Game.p2Icon], 33, 217, 72, 72, null);
        g.drawString("��"+Game.p2ID, 125, 285);
        
    }
	
	private static final long serialVersionUID = 1L;
	ChatPanel(Game game, Window window) {
		this.game = game;
		this.repaint();

		this.fontA = game.fontA;
		this.fontB = game.fontB;
		
		Border lineBorder1 = BorderFactory.createLineBorder(new Color(255,222,121), 1); //3px�� �׵θ��� ����
		Border lineBorder2 = BorderFactory.createLineBorder(Color.BLACK, 3); //3px�� �׵θ��� ����
		Border emptyBorder1 = BorderFactory.createEmptyBorder(0, 5, 0, 0); //���� ���հ�輱 �߰�
		Border emptyBorder2 = BorderFactory.createEmptyBorder(0, 0, 0, 0); //���� ���հ�輱 �߰�
		
		/*                           ��ä�� â                                                                                                                      */
		textareaTeam.setText("�� ä�ù��Դϴ�.\n");
		ViewTextTeam = new JScrollPane(textareaTeam,21, 31);
		textareaTeam.setFont(fontB.deriveFont(18f));
		textareaTeam.setBorder(emptyBorder2);
		textareaTeam.setLineWrap(true);
		textareaTeam.setEditable(false);

		ViewTextTeam.getViewport().getView().setForeground(Color.BLACK); //����
		ViewTextTeam.setBorder(lineBorder1);
		ViewTextTeam.getViewport().getView().setBackground(new Color(255,222,121));

		JScrollBar scrollBar3 = new JScrollBar(JScrollBar.VERTICAL){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {return true;}};
		ViewTextTeam.setVerticalScrollBar(scrollBar3);
		ViewTextTeam.getVerticalScrollBar().setUnitIncrement(12);
		ViewTextTeam.setBounds(33, 350, 267, 490);
		
		/*                           ��ä�� �Է�â                                                                                                                      */
		textfieldTeam = new JTextField();
		textfieldTeam.addActionListener(e -> {
			String text = textfieldTeam.getText();
			textareaTeam.append("["+Game.userID+"]: "+text+"\n");
			window.getUdpClient().send("@Gchat["+Game.userID+"]: "+text);
			ViewTextTeam.getVerticalScrollBar().setValue(ViewTextTeam.getVerticalScrollBar().getMaximum());
			textfieldTeam.setText("");
		});
		textfieldTeam.setFont(fontB.deriveFont(16f));
		textfieldTeam.setForeground(new Color(0,0,0));
		textfieldTeam.setBackground(new Color(255,241,0));
		textfieldTeam.setBorder(BorderFactory.createCompoundBorder(lineBorder2, emptyBorder1));
		textfieldTeam.setBounds(32, 845, 270, 26);
		
		JButton out = new JButton();
		out.setBounds(55, 48, 222, 45);
		out.setIcon(button);
		out.setBorderPainted(false);
		out.addActionListener(e -> {
			window.getUdpClient().send("@GExit");
			
			game.gameExit();
		});
		this.setLayout(null);
		
		this.add(out);
		this.add(ViewTextTeam);
		this.add(textfieldTeam);
		this.setBounds(1200, 0, 334, 921);
	}
	public Game getGame() {
		return game;
	}
}
