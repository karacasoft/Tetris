package com.karacasoft.tetris;

import java.io.IOException;

import com.karacasoft.tetris.view.MenuFrame;
import com.karacasoft.tetris.view.MenuPanel;
/**
 * Main test class for the game.
 * 
 * @author Triforce
 *
 */
public class MainClass{
	
	public static void main(String[] args) {
		
		MenuFrame menu = new MenuFrame();
		
		MenuPanel panel = new MenuPanel();
		panel.setFrame(menu);
		menu.add(panel);
		
		try {
			Game.loadGameAssets();
		} catch (IOException e) {
			System.err.println("Failed to load game assets. Is assets folder present?"
					+ " Or does Java has the rights to read files?");
			System.exit(-1);
			e.printStackTrace();
		}
		
		menu.setVisible(true);
	}

	
}
