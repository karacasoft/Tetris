package com.karacasoft.tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;

import com.karacasoft.tetris.Game.State;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.Tetromino;
import com.karacasoft.tetris.sound.SoundManager;
/**
 * Defines how the game controls will work.
 * 
 * {@link #inputBuffer} in this class holds the key codes for {@link KeyEvent}s.
 * We can easily inject input into the game via that object and make AI's play
 * with real controllers.
 * 
 * @author triforce
 *
 */
public class GameController implements KeyListener{

	public enum Key
	{
		OK,
		CANCEL,
		LEFT,
		RIGHT,
		INSTANT_DROP,
		DROP,
		ROTATE_LEFT,
		ROTATE_RIGHT,
		HOLD
	}
	
	/**
	 * Context reference.
	 */
	private Game game;
	
	private LinkedList<Key> inputBuffer = new LinkedList<Key>();
	private boolean inputEnabled = false;
	
	private boolean inputBufferMode = true;
	
	public GameController(Game game) {
		this.game = game;
	}
	
	/**
	 * Does work according to the input given.
	 * @param e Given key event.
	 */
	public void processInput(Key e)
	{
		switch (game.getGameState()) {
		case NOT_STARTED:
			
			break;
		case RUNNING:
			stdInputControl(e);
			break;
		case LANDING_A_PIECE:
			stdInputControl(e);
			break;
		case LINE_CLEARING:
			
			break;
		case PAUSED:
			if(e == Key.OK)
			{
				game.getGameFrame().setVisible(false);
				game.getGameFrame().dispose();
				if(game.getOpponentGame() != null)
				{
					game.getOpponentGame().setOpponentGame(null);
					game.getOpponentGame().getGameController().setInputBufferMode(true);
					game.getOpponentGame().getGameController().setInputEnabled(true);
				}
			}
			else if(e == Key.CANCEL)
			{
				game.setGameState(State.RUNNING);
				if(game.getOpponentGame() != null)
				{
					game.getOpponentGame().setGameState(State.RUNNING);
					game.getOpponentGame().getGameController().setInputBufferMode(true);
					game.getOpponentGame().getGameController().setInputEnabled(true);
				}
					
			}
			break;
		case LOST:
			if(e == Key.OK)
			{
				game.getGameFrame().setVisible(false);
				game.getGameFrame().dispose();
				if(game.getOpponentGame() != null)
				{
					game.getOpponentGame().getGameFrame().setVisible(false);
					game.getOpponentGame().getGameFrame().dispose();
				}
			}
			break;
		case WIN:
			if(e == Key.OK)
			{
				game.getGameFrame().setVisible(false);
				game.getGameFrame().dispose();
				if(game.getOpponentGame() != null)
				{
					game.getOpponentGame().getGameFrame().setVisible(false);
					game.getOpponentGame().getGameFrame().dispose();
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * Handles the input standardly.
	 * @param e Key
	 */
	private void stdInputControl(Key e)
	{
		if(e == Key.LEFT)
		{
			SoundManager.getInstance().playAudio("move_piece");
			Tetromino current = game.getGameView().getCurrentTetromino(); 
			boolean dontMoveFlag = false;
			for (Cell c : current.getCellState().getCells()) {
				if(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX() - 1,
						current.getPositionY() + c.getPositionY()) != null
						||
				   current.getPositionX() + c.getPositionX() - 1 < 0)
				{
					dontMoveFlag = true;
					break;
				}
			}
			if(dontMoveFlag)
			{
				//Don't move, lel
			}else{
				current.setPositionX(current.getPositionX() - 1);
				game.setLandingTimer(0);
				if(game.getGameState() == Game.State.LANDING_A_PIECE)
					game.setGameState(Game.State.RUNNING);
			}
		}else if(e == Key.RIGHT)
		{
			SoundManager.getInstance().playAudio("move_piece");
			Tetromino current = game.getGameView().getCurrentTetromino(); 
			boolean dontMoveFlag = false;
			for (Cell c : current.getCellState().getCells()) {
				if(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX() + 1,
						current.getPositionY() + c.getPositionY()) != null
						||
				   current.getPositionX() + c.getPositionX() + 1 >= 10)
				{
					dontMoveFlag = true;
					break;
				}
			}
			if(dontMoveFlag)
			{
				//Don't move, lel
			}else{
				current.setPositionX(current.getPositionX() + 1);
				game.setLandingTimer(0);
				if(game.getGameState() == Game.State.LANDING_A_PIECE)
					game.setGameState(Game.State.RUNNING);
			}
		}else if(e == Key.INSTANT_DROP){
			while(game.getGameState() != Game.State.LANDING_A_PIECE)
			{
				game.applyGravity();
				game.setLandingTimer(Game.LANDING_TIME - 100);
			}
		}else if(e == Key.DROP){
			game.applyGravity();
		}
		else if(e == Key.HOLD)
		{
			if(!game.isHoldUsed())
			{
				Tetromino t = game.getGameView().getCurrentTetromino();
				if(game.getHeldTetromino() == null)
				{
					game.getGameView().setCurrentTetromino(game.getTetrominoFactory().next());
				}else{
					game.getGameView().setCurrentTetromino(game.getHeldTetromino());
				}
				t.setPositionX(3);
				t.setPositionY(-2);
				game.setHeldTetromino(t);
				game.setHoldUsed(true);
			}
		}
		else if(e == Key.ROTATE_LEFT)
		{
			SoundManager.getInstance().playAudio("move_piece");
			Tetromino current = game.getGameView().getCurrentTetromino(); 
			ArrayList<Cell> cellList = game.getGameView().getCurrentTetromino().getCellState().getPreviousState().getCells();
			boolean kickLeft = false;
			boolean kickRight = false;
			boolean dontMoveFlag = false;
			for (Cell c : cellList) {
				if(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX(),
						current.getPositionY() + c.getPositionY()) != null
						||
				   current.getPositionX() + c.getPositionX() < 0
						||
				   current.getPositionX() + c.getPositionX() >= 10)
				{
					kickLeft = true;
					kickRight = true;
					for (Cell c2 : cellList) {
						if(game.getGameView().getTileMap().getCellAt(
								current.getPositionX() + c2.getPositionX() - 1,
								current.getPositionY() + c2.getPositionY()) != null
								||
						   current.getPositionX() + c2.getPositionX() - 1 < 0
						   		||
						   current.getPositionX() + c2.getPositionX() - 1 >= 10)
						{
							kickLeft = false;
						}
					}
					if(!kickLeft)
					{
						for (Cell c2 : cellList) {
							if(game.getGameView().getTileMap().getCellAt(
									current.getPositionX() + c2.getPositionX() + 1,
									current.getPositionY() + c2.getPositionY()) != null
									||
							   current.getPositionX() + c2.getPositionX() + 1 >= 10
									||
							   current.getPositionX() + c2.getPositionX() + 1 < 0)
							{
								kickRight = false;
								break;
							}
						}
						if(!kickRight)
						{
							dontMoveFlag = true;
							break;
						}
					}else{
						break;
					}
					
				}
			}
			
			int kickUp = 0;
			int maxKickUp = 0;
			
			int sideKickAmount = 0;
			if(kickLeft)
				sideKickAmount = -1;
			else if(kickRight)
				sideKickAmount = 1;
			
			for (Cell c : cellList) {
				while(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX() + sideKickAmount,
						current.getPositionY() + c.getPositionY() - kickUp) != null
						||
				   current.getPositionY() + c.getPositionY() - kickUp >= 20)
				{
					kickUp++;
					maxKickUp = Math.max(kickUp, maxKickUp);
				}
			}
			
			if(dontMoveFlag)
			{
				//Don't move, lel
			}else{
				current.rotateLeft();
				if(kickLeft)
					current.setPositionX(current.getPositionX() - 1);
				else if(kickRight)
					current.setPositionX(current.getPositionX() + 1);
				
				current.setPositionY(current.getPositionY() - maxKickUp);
				game.setLandingTimer(0);
				if(game.getGameState() == Game.State.LANDING_A_PIECE)
					game.setGameState(Game.State.RUNNING);
			}
		}
		else if(e == Key.ROTATE_RIGHT)
		{
			SoundManager.getInstance().playAudio("move_piece");
			Tetromino current = game.getGameView().getCurrentTetromino(); 
			ArrayList<Cell> cellList = game.getGameView().getCurrentTetromino().getCellState().getNextState().getCells();
			boolean kickLeft = false;
			boolean kickRight = false;
			boolean dontMoveFlag = false;
			for (Cell c : cellList) {
				if(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX(),
						current.getPositionY() + c.getPositionY()) != null
						||
				   current.getPositionX() + c.getPositionX() < 0
				   		||
				   current.getPositionX() + c.getPositionX() >= 10)
				{
					kickLeft = true;
					kickRight = true;
					for (Cell c2 : cellList) {
						if(game.getGameView().getTileMap().getCellAt(
								current.getPositionX() + c2.getPositionX() - 1,
								current.getPositionY() + c2.getPositionY()) != null
								||
						   current.getPositionX() + c2.getPositionX() - 1 < 0
								||
						   current.getPositionX() + c2.getPositionX() - 1 >= 10)
						{
							kickLeft = false;
						}
					}
					if(!kickLeft)
					{
						for (Cell c2 : cellList) {
							if(game.getGameView().getTileMap().getCellAt(
									current.getPositionX() + c2.getPositionX() + 1,
									current.getPositionY() + c2.getPositionY()) != null
									||
							   current.getPositionX() + c2.getPositionX() + 1 >= 10
									||
							   current.getPositionX() + c2.getPositionX() + 1 < 0)
							{
								kickRight = false;
								break;
							}
						}
						if(!kickRight)
						{
							dontMoveFlag = true;
							break;
						}
					}else{
						break;
					}
					
				}
			}
			
			int kickUp = 0;
			int maxKickUp = 0;
			
			int sideKickAmount = 0;
			if(kickLeft)
				sideKickAmount = -1;
			else if(kickRight)
				sideKickAmount = 1;
			for (Cell c : cellList) {
				kickUp = 0;
				while(game.getGameView().getTileMap().getCellAt(
						current.getPositionX() + c.getPositionX() + sideKickAmount,
						current.getPositionY() + c.getPositionY() - kickUp) != null
						||
				   current.getPositionY() + c.getPositionY() - kickUp >= 20)
				{
					kickUp++;
					maxKickUp = Math.max(kickUp, maxKickUp);
				}
			}
			
			if(dontMoveFlag)
			{
				//Don't move, lel
			}else{
				current.rotateRight();
				if(kickLeft)
					current.setPositionX(current.getPositionX() - 1);
				else if(kickRight)
					current.setPositionX(current.getPositionX() + 1);

				current.setPositionY(current.getPositionY() - maxKickUp);
				game.setLandingTimer(0);
				
				if(game.getGameState() == Game.State.LANDING_A_PIECE)
					game.setGameState(Game.State.RUNNING);
			}
		} else if(e == Key.CANCEL)
		{
			game.setGameState(Game.State.PAUSED);
			if(game.getOpponentGame() != null)
			{
				game.getOpponentGame().setGameState(State.PAUSED);
				game.getOpponentGame().getGameController().setInputBufferMode(false);
				game.getOpponentGame().getGameController().setInputEnabled(false);
			}
		}
	}
	
	/**
	 * Injects the given key input to the game.
	 * @param event key input
	 */
	public void dispatchKeyEvent(KeyEvent event)
	{
		if(inputBufferMode)
		{
			Key k;
			if((k = game.getKeyMap().getKey(event.getKeyCode())) != null)
				inputBuffer.add(k);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_J)
		{
			game.getAi().setAiblocked(!game.getAi().isAiblocked());
		}
		if(game.getOpponentGame() != null)
		{
			game.getOpponentGame().getGameController().dispatchKeyEvent(arg0);
		}
		if(inputBufferMode)
		{
			Key k;
			if((k = game.getKeyMap().getKey(arg0.getKeyCode())) != null)
				inputBuffer.add(k);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	/**
	 * Processes all the inputs in one cycle of the game.
	 */
	public void processAllInputs()
	{
		while(!inputBuffer.isEmpty())
		{
			processInput(inputBuffer.poll());
		}
	}
	
	public void setInputEnabled(boolean inputEnabled) {
		this.inputEnabled = inputEnabled;
	}

	public boolean isInputEnabled()
	{
		return inputEnabled;
	}
	
	public void setInputBufferMode(boolean inputBufferMode) {
		this.inputBufferMode = inputBufferMode;
	}
	
	public boolean isInputBufferModeEnabled()
	{
		return this.inputBufferMode;
	}
	
	public LinkedList<Key> getInputBuffer() {
		return inputBuffer;
	}
}
