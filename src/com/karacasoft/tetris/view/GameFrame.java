package com.karacasoft.tetris.view;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

public class GameFrame extends JFrame implements MouseInputListener{
	
	private static final long serialVersionUID = 1L;
	
	private int posX = 250, posY = 400;
	public GameFrame() {
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 255));
		this.setSize(400, 600);
		this.setLocation(300, 300);
		this.addMouseMotionListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	
	
}
