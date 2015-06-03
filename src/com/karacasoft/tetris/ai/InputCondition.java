package com.karacasoft.tetris.ai;

import com.karacasoft.tetris.Game.State;
/**
 * Created to execute an input according to a condition
 * 
 * @author Triforce
 *
 */
public class InputCondition {

	private Type type = Type.IF_NOT_Y_POSITION;
	private int parameterValue = 0;
	
	/**
	 * Type of the given condition
	 * 
	 * @author Triforce
	 *
	 */
	public enum Type
	{
		IF_X_POSITION,
		IF_Y_POSITION,
		IF_NOT_X_POSITION,
		IF_NOT_Y_POSITION,
		BEFORE_PLACE,
		REPEAT_BEFORE_PLACEMENT,
		REPEAT_X_TIMES
	}
	
	public InputCondition setParameter(int value)
	{
		this.parameterValue = value;
		return this;
	}
	
	public InputCondition setType(Type type) {
		this.type = type;
		return this;
	}
	
	public Type getType() {
		return type;
	}
	/**
	 * Executes the condition.
	 * @param schema 
	 * @return true if the condition is true
	 */
	public boolean executeCondition(InputSchema schema)
	{
		switch (type) {
		case IF_X_POSITION:
			if(schema.getGame().getGameView().getCurrentTetromino().getPositionX() == parameterValue)
			{
				return true;
			}else{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return false;
			}
		case IF_Y_POSITION:
			if(schema.getGame().getGameView().getCurrentTetromino().getPositionY() == parameterValue)
			{
				return true;
			}else{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return false;
			}
		case IF_NOT_X_POSITION:
			if(schema.getGame().getGameView().getCurrentTetromino().getPositionX() != parameterValue)
			{
				return true;
			}else{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return false;
			}
		case IF_NOT_Y_POSITION:
			if(schema.getGame().getGameView().getCurrentTetromino().getPositionY() != parameterValue)
			{
				return true;
			}else{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return false;
			}
		case BEFORE_PLACE:
			if(schema.getGame().getGameState() == State.LANDING_A_PIECE)
			{
				return true;
			}else{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return false;
			}
		case REPEAT_BEFORE_PLACEMENT:
			if(schema.getGame().getGameState() != State.LANDING_A_PIECE)
			{
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return true;
			}else{
				return false;
			}
		case REPEAT_X_TIMES:
			if(parameterValue > 0)
			{
				parameterValue--;
				InputSchema next = schema.getNext();
				schema.addInput(schema.getKey()).setNext(next);
				schema.getNext().setCondition(this);
				return true;
			}
			return false;
		default:
			throw new UnsupportedOperationException("This kind of condition is not yet supported");
		}
	}
	
}
