package com.karacasoft.tetris.ai;

import com.karacasoft.tetris.Game;
import com.karacasoft.tetris.GameController.Key;

/**
 * Created to execute inputs one by one for AI.
 * 
 * @author Triforce
 *
 */
public class InputSchema {

	private InputSchema next = null;
	private Key key;
	
	private InputCondition condition;
	
	private Game game;
	
	public InputSchema(Game game) {
		this.game = game;
	}
	
	public InputSchema(Game game, Key key)
	{
		this.game = game;
		this.key = key;
	}
	
	public InputSchema addInput(Key key)
	{
		next = new InputSchema(this.game, key);
		return next;
	}
	
	/**
	 * Executes this input. If this input has a condition, then the condition will be checked first.
	 */
	public void execute()
	{
		if(this.key != null)
		{
			if(condition != null)
			{
				if(condition.executeCondition(this))
				{
					this.game.getGameController().getInputBuffer().add(this.key);
				}
			}else{
				this.game.getGameController().getInputBuffer().add(this.key);
			}
		}
	}
	
	public Key getKey() {
		return key;
	}
	
	public void setKey(Key key) {
		this.key = key;
	}
	
	public void setNext(InputSchema next) {
		this.next = next;
	}
	
	public InputSchema getNext() {
		return next;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public InputCondition getCondition() {
		return condition;
	}

	public void setCondition(InputCondition condition) {
		this.condition = condition;
	}
}
