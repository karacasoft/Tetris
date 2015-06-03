package com.karacasoft.tetris;
/**
 * Main cycle thread for the game.
 * 
 * In one cycle of game,
 * - The time passed will be calculated
 * - {@link Game#render(long)} method will be called.
 * - The thread will wait for 20 milliseconds.
 * 
 * @author Triforce
 *
 */
public class GameThread extends Thread {

	/**
	 * Context reference
	 */
	private Game game;
	private long lastUpdateTime = 0;
	private boolean gameRunning = true;
	
	public GameThread(Game game) {
		this.game = game;
	}
	
	@Override
	public void run() {
		try {
			while(gameRunning)
			{
				if(lastUpdateTime == 0)
				{
					lastUpdateTime = System.currentTimeMillis();
				}
				long dTime = System.currentTimeMillis() - lastUpdateTime;
				this.game.render(dTime);
				lastUpdateTime = System.currentTimeMillis();
				
				Thread.sleep(20);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}
	
	public boolean isGameRunning() {
		return gameRunning;
	}
	
}
