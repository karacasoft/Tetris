package com.karacasoft.tetris.model;

import java.awt.Color;
import java.awt.Image;

/**
 * The basic unit of the game canvas.
 * @author Triforce
 *
 */
public class Cell {
	private int positionX;
	private int positionY;
	
	private Image image;
	private Color shadeColor;
	
	private long destroyAnimFrameTimer = 0;
	private boolean destroyed = false;
	
	public Cell() {}
	
	public Cell(int posX, int posY, Image image, Color shadeColor)
	{
		this.positionX = posX;
		this.positionY = posY;
		this.image = image;
		this.shadeColor = shadeColor;
	}
	
	public int getPositionX() {
		return positionX;
	}
	
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}
	
	public int getPositionY() {
		return positionY;
	}
	
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public long getDestroyAnimFrameTimer() {
		return destroyAnimFrameTimer;
	}

	public void setDestroyAnimFrameTimer(long destroyAnimFrameTimer) {
		this.destroyAnimFrameTimer = destroyAnimFrameTimer;
	}
	
	public Image getDestroyAnimationCurrentFrame()
	{
		int frame = Math.min(Math.max((int) (destroyAnimFrameTimer / 15 + 1), 1), 11);
		return AssetManager.getInstance().getImage("block_disappear" + frame);
	}

	public Color getShadeColor() {
		return shadeColor;
	}

	public void setShadeColor(Color shadeColor) {
		this.shadeColor = shadeColor;
	}
	
}
