package com.karacasoft.tetris.model;

import java.awt.event.KeyEvent;

import com.karacasoft.tetris.GameController.Key;

/**
 * Holds the keys required to trigger input for the game.
 * 
 * @author Triforce
 *
 */
public class KeyMap {

	private static KeyMap player1;
	private static KeyMap player2;
	
	private int leftKey = KeyEvent.VK_LEFT;
	private int rightKey = KeyEvent.VK_RIGHT;
	private int instantDropKey = KeyEvent.VK_UP;
	private int dropKey = KeyEvent.VK_DOWN;
	private int rotateLeftKey = KeyEvent.VK_N;
	private int rotateRightKey = KeyEvent.VK_M;
	private int holdKey = KeyEvent.VK_K;
	private int exitKey = KeyEvent.VK_ESCAPE;
	private int okKey = KeyEvent.VK_ENTER;
	
	private int remapCount = 0;
	
	private KeyMap()
	{}
	/**
	 * Returns the KeyMap instance for player1.
	 * @return player 1 KeyMap
	 */
	public static KeyMap getPlayer1()
	{
		if(player1 == null) player1 = new KeyMap();
		return player1;
	}
	/**
	 * Returns the KeyMap instance for player2.
	 * @return player 2 KeyMap
	 */
	public static KeyMap getPlayer2()
	{
		if(player2 == null) 
		{
			player2 = new KeyMap();
			player2.leftKey = KeyEvent.VK_A;
			player2.rightKey = KeyEvent.VK_D;
			player2.instantDropKey = KeyEvent.VK_W;
			player2.dropKey = KeyEvent.VK_S;
			player2.rotateLeftKey = KeyEvent.VK_Q;
			player2.rotateRightKey = KeyEvent.VK_E;
			player2.holdKey = KeyEvent.VK_R;
			player2.exitKey = KeyEvent.VK_G;
			player2.okKey = KeyEvent.VK_F;
		}
		return player2;
	}
	
	public Key getKey(int in)
	{
		if(in == leftKey)
		{
			return Key.LEFT;
		}else if(in == rightKey)
		{
			return Key.RIGHT;
		}else if(in == instantDropKey)
		{
			return Key.INSTANT_DROP;
		}else if(in == dropKey)
		{
			return Key.DROP;
		}else if(in == rotateLeftKey)
		{
			return Key.ROTATE_LEFT;
		}else if(in == rotateRightKey)
		{
			return Key.ROTATE_RIGHT;
		}else if(in == exitKey)
		{
			return Key.CANCEL;
		}else if(in == okKey)
		{
			return Key.OK;
		}else if(in == holdKey)
		{
			return Key.HOLD;
		}else{
			//user pressed some other key :)
			return null;
		}
	}

	public int getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(int leftKey) {
		this.leftKey = leftKey;
	}

	public int getRightKey() {
		return rightKey;
	}

	public void setRightKey(int rightKey) {
		this.rightKey = rightKey;
	}

	public int getInstantDropKey() {
		return instantDropKey;
	}

	public void setInstantDropKey(int instantDropKey) {
		this.instantDropKey = instantDropKey;
	}

	public int getDropKey() {
		return dropKey;
	}

	public void setDropKey(int dropKey) {
		this.dropKey = dropKey;
	}

	public int getRotateLeftKey() {
		return rotateLeftKey;
	}

	public void setRotateLeftKey(int rotateLeftKey) {
		this.rotateLeftKey = rotateLeftKey;
	}

	public int getRotateRightKey() {
		return rotateRightKey;
	}

	public void setRotateRightKey(int rotateRightKey) {
		this.rotateRightKey = rotateRightKey;
	}
	
	public int getHoldKey() {
		return holdKey;
	}

	public void setHoldKey(int holdKey) {
		this.holdKey = holdKey;
	}

	public int getExitKey() {
		return exitKey;
	}

	public void setExitKey(int exitKey) {
		this.exitKey = exitKey;
	}

	public int getOkKey() {
		return okKey;
	}

	public void setOkKey(int okKey) {
		this.okKey = okKey;
	}
	
	public Key getCurrentRemappedKey()
	{
		switch (remapCount) {
		case 0:
			return Key.OK;
		case 1:
			return Key.CANCEL;
		case 2:
			return Key.LEFT;
		case 3:
			return Key.RIGHT;
		case 4:
			return Key.DROP;
		case 5:
			return Key.INSTANT_DROP;
		case 6:
			return Key.ROTATE_LEFT;
		case 7:
			return Key.ROTATE_RIGHT;
		case 8:
			return Key.HOLD;
		default:
			return null;
		}
	}
	
	public static String keyToString(Key k)
	{
		switch (k) {
		case OK:
			return "OK";
		case CANCEL:
			return "CANCEL";
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		case DROP:
			return "DROP";
		case INSTANT_DROP:
			return "INSTANT DROP";
		case ROTATE_LEFT:
			return "ROTATE TO LEFT";
		case ROTATE_RIGHT:
			return "ROTATE TO RIGHT";
		case HOLD:
			return "HOLD";
		default:
			return "WHAT?";
		}
	}
	
	public boolean remapNextKey(int keyCode)
	{
		
		if(remapCount < 8)
		{
			remapKey(getCurrentRemappedKey(), keyCode);
			remapCount++;
			return true;
		}else{
			return false;
		}
		
	}
	
	public void remapKey(Key k, int keyCode)
	{
		switch (k) {
		case OK:
			setOkKey(keyCode);
			break;
		case CANCEL:
			setExitKey(keyCode);
			break;
		case LEFT:
			setLeftKey(keyCode);
			break;
		case RIGHT:
			setRightKey(keyCode);
			break;
		case DROP:
			setDropKey(keyCode);
			break;
		case INSTANT_DROP:
			setInstantDropKey(keyCode);
			break;
		case ROTATE_LEFT:
			setRotateLeftKey(keyCode);
			break;
		case ROTATE_RIGHT:
			setRotateRightKey(keyCode);
			break;
		case HOLD:
			setHoldKey(keyCode);
			break;
		default:
			break;
		}
	}

	
}
