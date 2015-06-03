package com.karacasoft.tetris.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.karacasoft.tetris.Game;
import com.karacasoft.tetris.ai.AIPlayer;
import com.karacasoft.tetris.model.KeyMap;
import com.karacasoft.tetris.sound.SoundManager;

public class MenuPanel extends JPanel implements KeyListener{

	private static final long serialVersionUID = 1L;

	private ArrayList<String> menuItems = new ArrayList<String>();
	
	private int currentMenuItem = 0;
	
	public static final int MENU_TEXT_POSITION_X = 200;
	public static final int MENU_TEXT_POSITION_Y = 400;
	
	public static final float MENU_TEXT_SIZE = 25f;
	
	private boolean remapping = false;
	private int remapPlayer = 1;
	
	private MenuFrame frame;
	
	public MenuPanel(){
		this.setBackground(new Color(40, 40, 40, 255));
		menuItems.add("SINGLE PLAYER");
		menuItems.add("MULTIPLAYER");
		menuItems.add("AI PLAY");
		menuItems.add("REMAP KEYS");
		menuItems.add("EXIT");
		this.addKeyListener(this);
		this.setFocusable(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(g instanceof Graphics2D)
		{
			Graphics2D g2 = (Graphics2D) g;
			
			Font f = new Font("SANS", Font.PLAIN, (int)MENU_TEXT_SIZE);
			try {
				f = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/pixelated.ttf"));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			if(remapping)
			{
				KeyMap keyMap = KeyMap.getPlayer1();
				if(remapPlayer == 1)
					keyMap = KeyMap.getPlayer1();
				else
					keyMap = KeyMap.getPlayer2();
				g2.setFont(f.deriveFont(2 * MENU_TEXT_SIZE / 3));
				g2.setColor(Color.WHITE);
				int width = g2.getFontMetrics().stringWidth("PLAYER " + remapPlayer + 
						" : PRESS A KEY FOR " + KeyMap.keyToString(keyMap.getCurrentRemappedKey()) + " KEY");
				g2.drawString("PLAYER " + remapPlayer + 
						" : PRESS A KEY FOR " + KeyMap.keyToString(keyMap.getCurrentRemappedKey()) + " KEY",
						MENU_TEXT_POSITION_X - width / 2, MENU_TEXT_POSITION_Y);
			}else{
				g2.setFont(f.deriveFont(MENU_TEXT_SIZE));
				g2.setColor(Color.WHITE);
				int width = g2.getFontMetrics().stringWidth("<  " + menuItems.get(currentMenuItem) + "  >");
				g2.drawString("<  " + menuItems.get(currentMenuItem) + "  >",
						MENU_TEXT_POSITION_X - width / 2, MENU_TEXT_POSITION_Y);
			}
			
			
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(remapping)
		{
			SoundManager.getInstance().playAudio("menu_move");
			KeyMap map = KeyMap.getPlayer1();
			if(!map.remapNextKey(e.getKeyCode()))
			{
				if(remapPlayer == 1)
				{
					remapPlayer = 2;
					repaint();
					return;
				}
				map = KeyMap.getPlayer2();
				if(!map.remapNextKey(e.getKeyCode()))
				{
					remapping = false;
				}
			}
			repaint();
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			SoundManager.getInstance().playAudio("menu_move");
			if(currentMenuItem != 0) currentMenuItem--;
			else currentMenuItem = menuItems.size() - 1;
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			SoundManager.getInstance().playAudio("menu_move");
			if(currentMenuItem != menuItems.size() - 1) currentMenuItem++;
			else currentMenuItem = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			SoundManager.getInstance().playAudio("menu_select");
			switch (currentMenuItem) {
			case 0:
				startGame();
				break;
			case 1:
				startMultiplayer();
				break;
			case 2:
				startAIGame();
				break;
			case 3:
				remapping = true;
				break;
			case 4:
				this.setVisible(false);
				frame.dispose();
				break;
			default:
				break;
			}
		}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			this.setVisible(false);
			frame.dispose();
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	public void startGame()
	{
		Game g = new Game();
		g.start();
	}
	
	public void startMultiplayer()
	{
		Game g = new Game();
		Game g2 = new Game();
		g2.setKeyMap(KeyMap.getPlayer2());
		g.setOpponentGame(g2);
		g2.setOpponentGame(g);
		g2.getGameFrame().setLocation(g2.getGameFrame().getLocation().x + 500, g2.getGameFrame().getLocation().y);
		g.start();
		g2.start();
	}
	
	public void startAIGame()
	{
		Game g = new Game();
		AIPlayer player = new AIPlayer(g);
		g.start();
		player.getAiThread().start();
	}
	
	public void setFrame(MenuFrame frame) {
		this.frame = frame;
	}
	
	
	
}
