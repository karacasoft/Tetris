package com.karacasoft.tetris.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import com.karacasoft.tetris.Game;
import com.karacasoft.tetris.model.AssetManager;
import com.karacasoft.tetris.model.Cell;
import com.karacasoft.tetris.model.Tetromino;
import com.karacasoft.tetris.model.TileMap;

public class GameView extends JPanel {

	public static final int GAME_MARGIN_TOP = 80;
	public static final int GAME_MARGIN_LEFT = 60;
	
	public static final int NEXT_TETROMINO_POSITION_X = GAME_MARGIN_LEFT + 50;
	public static final int NEXT_TETROMINO_POSITION_Y = GAME_MARGIN_TOP + 100;
	
	public static final int HELD_TETROMINO_POSITION_X = GAME_MARGIN_LEFT + 50;
	public static final int HELD_TETROMINO_POSITION_Y = 50;
	
	
	public static final int SCORE_POSITION_X = GAME_MARGIN_LEFT;
	public static final int SCORE_POSITION_Y = -20;
	public static final float SCORE_TEXT_SIZE = 32f;
	
	public static final int LEVEL_POSITION_X = 0;
	public static final int LEVEL_POSITION_Y = 0;
	public static final float LEVEL_TEXT_SIZE = 16f;
	public static final float LEVEL_NUMBER_SIZE = 40f;
	
	public static final int LINES_POSITION_X = 0;
	public static final int LINES_POSITION_Y = -100;
	public static final float LINES_TEXT_SIZE = 16f;
	public static final float LINES_NUMBER_SIZE = 40f;
	
	public static final float PAUSE_TEXT_SIZE = 40f;
	public static final float PAUSE_SMALL_TEXT_SIZE = 15f;
	
	public static final float WIN_LOSE_TEXT_SIZE = 60f;
	
	public static final int LEVEL_UP_TEXT_MARGIN_BOTTOM = 100;
	public static final float LEVEL_UP_TEXT_SIZE = 40f;
	
	public static final int MAX_BG_NUMBER = 2;
	
	private static final long serialVersionUID = 1L;
	
	private OnDrawListener onDrawListener;
	
	private TileMap tileMap;
	private Tetromino currentTetromino;
	
	private Game game;
	
	private boolean levelingUp = false;
	private int levelUpTimer = 0;
	
	public GameView(Game game) {
		this.setBackground(Color.BLACK);
		tileMap = new TileMap();
		this.game = game;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graphics = (Graphics2D) g;
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/pixelated.ttf"));
		} catch (IOException e) {
			System.out.println("Font file IOException happened. Using standard font.");
			e.printStackTrace();
		} catch (FontFormatException e) {
			System.out.println("Font file FontFormatException happened. Using standard font.");
			e.printStackTrace();
		}
		
		drawBackground(graphics, font);
		
		drawWalls(graphics, font);
		
		drawCells(graphics, font);
		
		drawCurrentTetromino(graphics, font);
		
		drawShade(graphics, font);
		
		drawNextTetromino(graphics, font);
		
		drawHeldTetromino(graphics, font);
		
		drawText(graphics, font);
		
		if(onDrawListener != null)
			onDrawListener.onDrawCalled(graphics);
	}

	private void drawBackground(Graphics2D graphics, Font font)
	{
		int bg_number = Math.min(MAX_BG_NUMBER, game.getLevel());
		
		if(levelingUp)
		{
			if(levelUpTimer < 40)
			{
				levelUpTimer++;
				if(game.getLevel() <= bg_number)
				{
					int x = Math.min(tileMap.getWidth(), levelUpTimer * (tileMap.getWidth() / 20));
					
					graphics.drawImage(AssetManager.getInstance().getImage("background" + (bg_number - 1)),
							GAME_MARGIN_LEFT + x, GAME_MARGIN_TOP,
							tileMap.getWidth() - x, tileMap.getHeight(), this);
					graphics.drawImage(AssetManager.getInstance().getImage("background" + bg_number),
							GAME_MARGIN_LEFT, GAME_MARGIN_TOP,
							tileMap.getWidth() - (tileMap.getWidth() - x), tileMap.getHeight(), this);
					graphics.setColor(new Color(0, 0, 0, 200));
					graphics.fillRect(GAME_MARGIN_LEFT, GAME_MARGIN_TOP, tileMap.getWidth(), tileMap.getHeight());
				}else{
					graphics.drawImage(AssetManager.getInstance().getImage("background" + bg_number),
							GAME_MARGIN_LEFT, GAME_MARGIN_TOP, tileMap.getWidth(), tileMap.getHeight(), this);
					graphics.setColor(new Color(0, 0, 0, 200));
					graphics.fillRect(GAME_MARGIN_LEFT, GAME_MARGIN_TOP, 200, 400);
				}
				if(font != null) graphics.setFont(font.deriveFont(LEVEL_UP_TEXT_SIZE));
				graphics.setColor(Color.GREEN);
				int winWidth = graphics.getFontMetrics().getWidths()[1]; // all characters have the same width on this font. (I hope...)
				int winHeight = graphics.getFontMetrics().getHeight();
				int winAscent = graphics.getFontMetrics().getAscent();
				
				String levelup = "LEVEL UP";
				
				for(int i = 0; i < levelup.length(); i++)
				{
					char c = levelup.charAt(i);
					int a = (levelUpTimer - 12) / 2 - i;
					int alpha = Math.max(0, Math.min(255, (6 - a) * (255 / 5)));
					Color color;
					switch (i) {
					case 0:
						color = Color.RED;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 1:
						color = Color.ORANGE;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 2:
						color = Color.YELLOW;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 3:
						color = Color.GREEN;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 4:
						color = Color.CYAN;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 5:
						color = Color.BLUE;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					case 6:
						color = Color.BLUE;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					default:
						color = Color.PINK;
						color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
						graphics.setColor(color);
						break;
					}
					
					int dY = Math.max(0, a) + i * 2;
					
					graphics.drawString("" + c, (int) (GAME_MARGIN_LEFT + tileMap.getWidth() / 2 + 1.5f * winWidth * (i - 4)),
							- dY + GAME_MARGIN_TOP - LEVEL_UP_TEXT_MARGIN_BOTTOM + tileMap.getHeight() / 2 - (winHeight - winAscent));
				}
				
			}else{
				// to prevent the flicker after drawing finished.
				graphics.drawImage(AssetManager.getInstance().getImage("background" + bg_number),
						GAME_MARGIN_LEFT, GAME_MARGIN_TOP, tileMap.getWidth(), tileMap.getHeight(), this);
				graphics.setColor(new Color(0, 0, 0, 200));
				graphics.fillRect(GAME_MARGIN_LEFT, GAME_MARGIN_TOP, 200, 400);
				levelingUp = false;
				levelUpTimer = 0;
			}
		}else
		{
			graphics.drawImage(AssetManager.getInstance().getImage("background" + bg_number),
					GAME_MARGIN_LEFT, GAME_MARGIN_TOP, tileMap.getWidth(), tileMap.getHeight(), this);
			graphics.setColor(new Color(0, 0, 0, 200));
			graphics.fillRect(GAME_MARGIN_LEFT, GAME_MARGIN_TOP, 200, 400);
			
		}
	}
	
	private void drawWalls(Graphics2D graphics, Font font) {
		for(int i = 0; i < 20; i++)
		{
			graphics.drawImage(AssetManager.getInstance().getImage("boundary"),
					GAME_MARGIN_LEFT - 20,
					GAME_MARGIN_TOP + i * 20, this);
			graphics.drawImage(AssetManager.getInstance().getImage("boundary"),
					GAME_MARGIN_LEFT + tileMap.getWidth(),
					GAME_MARGIN_TOP + i * 20, this);
		}
		for(int i = -1; i < 11; i++)
		{
			graphics.drawImage(AssetManager.getInstance().getImage("boundary"),
					GAME_MARGIN_LEFT + i * 20,
					GAME_MARGIN_TOP + tileMap.getHeight(), this);
		}
	}
	
	private void drawCells(Graphics2D graphics, Font font) {
		for(Cell c : tileMap.getCells())
		{
			if(c.isDestroyed())
			{
				graphics.drawImage(c.getDestroyAnimationCurrentFrame(),
						c.getPositionX() * tileMap.getTileWidthInPixels() + GAME_MARGIN_LEFT,
						c.getPositionY() * tileMap.getTileHeightInPixels() + GAME_MARGIN_TOP, this);
			}else{
				graphics.drawImage(c.getImage(), c.getPositionX() * tileMap.getTileWidthInPixels() + GAME_MARGIN_LEFT,
						c.getPositionY() * tileMap.getTileHeightInPixels() + GAME_MARGIN_TOP, this);
			}
			
		}
	}
	
	private void drawCurrentTetromino(Graphics2D graphics, Font font) {
		for(Cell c : currentTetromino.getCellState().getCells())
		{
			graphics.drawImage(c.getImage(), (currentTetromino.getPositionX() + c.getPositionX()) * tileMap.getTileWidthInPixels() + GAME_MARGIN_LEFT,
					(currentTetromino.getPositionY() + c.getPositionY()) * tileMap.getTileHeightInPixels() + GAME_MARGIN_TOP, this);
		}
	}
	
	private void drawShade(Graphics2D graphics, Font font) {
		int shadePositionOffset = 0;
		int minShadeOffest = 20;
		for(Cell c : currentTetromino.getCellState().getCells())
		{
			shadePositionOffset = 0;
			while(tileMap.getCellAt(currentTetromino.getPositionX() + c.getPositionX(),
					currentTetromino.getPositionY() + c.getPositionY() + shadePositionOffset + 1) == null &&
					currentTetromino.getPositionY() + c.getPositionY() + shadePositionOffset < 19)
			{
				shadePositionOffset++;
			}
			minShadeOffest = Math.min(shadePositionOffset, minShadeOffest);
		}
		for(Cell c : currentTetromino.getCellState().getCells())
		{
			BasicStroke s = new BasicStroke(2f);
			graphics.setStroke(s);
			graphics.setPaint(c.getShadeColor());
			graphics.drawRect((currentTetromino.getPositionX() + c.getPositionX()) * tileMap.getTileWidthInPixels() + GAME_MARGIN_LEFT,
						(currentTetromino.getPositionY() + c.getPositionY() + minShadeOffest) * tileMap.getTileHeightInPixels() + GAME_MARGIN_TOP,
						tileMap.getTileWidthInPixels(),
						tileMap.getTileHeightInPixels());
			Color translucentColor = new Color(c.getShadeColor().getRed(),
					c.getShadeColor().getGreen(),
					c.getShadeColor().getBlue(),
					125);
			graphics.setPaint(translucentColor);
			graphics.setStroke(new BasicStroke(1f));  //resetting the stroke
			graphics.fillRect((currentTetromino.getPositionX() + c.getPositionX()) * tileMap.getTileWidthInPixels() + GAME_MARGIN_LEFT,
						(currentTetromino.getPositionY() + c.getPositionY() + minShadeOffest) * tileMap.getTileHeightInPixels() + GAME_MARGIN_TOP,
						tileMap.getTileWidthInPixels(),
						tileMap.getTileHeightInPixels());
		}
	}
	
	private void drawNextTetromino(Graphics2D graphics, Font font) {
		graphics.setColor(Color.WHITE);
		if(font != null) graphics.setFont(font.deriveFont(24f));
		graphics.drawString("NEXT", this.getTileMap().getWidth() + NEXT_TETROMINO_POSITION_X,
				NEXT_TETROMINO_POSITION_Y);
		for(Cell c : game.getTetrominoFactory().peekNext().getCellState().getCells())
		{
			
			graphics.drawImage(c.getImage(), (c.getPositionX()) * tileMap.getTileWidthInPixels() + 
					this.getTileMap().getWidth() + NEXT_TETROMINO_POSITION_X,
				(c.getPositionY()) * tileMap.getTileHeightInPixels() +
					NEXT_TETROMINO_POSITION_Y, this);
		}
	}
	
	private void drawHeldTetromino(Graphics2D graphics, Font font) {
		graphics.setColor(Color.WHITE);
		if(font != null) graphics.setFont(font.deriveFont(24f));
		graphics.drawString("HOLD", this.getTileMap().getWidth() + HELD_TETROMINO_POSITION_X,
				HELD_TETROMINO_POSITION_Y);
		if(game.getHeldTetromino() != null)
		{
			for(Cell c : game.getHeldTetromino().getCellState().getCells())
			{
				
				graphics.drawImage(c.getImage(), (c.getPositionX()) * tileMap.getTileWidthInPixels() + 
						this.getTileMap().getWidth() + HELD_TETROMINO_POSITION_X,
					(c.getPositionY()) * tileMap.getTileHeightInPixels() +
						HELD_TETROMINO_POSITION_Y, this);
			}
		}
	}
	
	private void drawText(Graphics2D graphics, Font font) {
		if(font != null) graphics.setFont(font.deriveFont(SCORE_TEXT_SIZE));
		int scoreHeight = graphics.getFontMetrics().getHeight();
		int scoreAscent = graphics.getFontMetrics().getAscent();
		graphics.drawString("SCORE: " + game.getScore(), SCORE_POSITION_X,
				GAME_MARGIN_TOP + scoreAscent - scoreHeight + SCORE_POSITION_Y);
		
		//draw lines cleared
		if(font != null) graphics.setFont(font.deriveFont(LINES_TEXT_SIZE));
		int width = graphics.getFontMetrics().stringWidth("LINES");
		int height = graphics.getFontMetrics().getHeight();
		int ascent = graphics.getFontMetrics().getAscent();
		
		if(font != null) graphics.setFont(font.deriveFont(LINES_NUMBER_SIZE));
		int numberWidth = graphics.getFontMetrics().stringWidth(String.valueOf(game.getLevel()));
		int numberHeight = graphics.getFontMetrics().getHeight();
		int numberAscent = graphics.getFontMetrics().getAscent();
		if(font != null) graphics.setFont(font.deriveFont(LINES_TEXT_SIZE));
		
		graphics.drawString("LINES", 
				GAME_MARGIN_LEFT + tileMap.getWidth() + LINES_POSITION_X + width,
				GAME_MARGIN_TOP + tileMap.getHeight() - numberHeight - height + ascent + LINES_POSITION_Y);
		if(font != null) graphics.setFont(font.deriveFont(LINES_NUMBER_SIZE));
		graphics.drawString(String.valueOf(game.getLinesCleared()), 
				GAME_MARGIN_LEFT + tileMap.getWidth() + LINES_POSITION_X + width + numberWidth / 2,
				GAME_MARGIN_TOP + tileMap.getHeight() - numberHeight + numberAscent + LINES_POSITION_Y);
		
		
		//draw level
		if(font != null) graphics.setFont(font.deriveFont(LEVEL_TEXT_SIZE));
		int levelWidth = graphics.getFontMetrics().stringWidth("LEVEL");
		int levelHeight = graphics.getFontMetrics().getHeight();
		int levelAscent = graphics.getFontMetrics().getAscent();
		
		if(font != null) graphics.setFont(font.deriveFont(LEVEL_NUMBER_SIZE));
		int levelNumberWidth = graphics.getFontMetrics().stringWidth(String.valueOf(game.getLevel()));
		int levelNumberHeight = graphics.getFontMetrics().getHeight();
		int levelNumberAscent = graphics.getFontMetrics().getAscent();
		if(font != null) graphics.setFont(font.deriveFont(LEVEL_TEXT_SIZE));
		
		graphics.drawString("LEVEL", 
				GAME_MARGIN_LEFT + tileMap.getWidth() + LEVEL_POSITION_X + levelWidth,
				GAME_MARGIN_TOP + tileMap.getHeight() - levelNumberHeight - levelHeight + levelAscent + LEVEL_POSITION_Y);
		if(font != null) graphics.setFont(font.deriveFont(LEVEL_NUMBER_SIZE));
		graphics.drawString(String.valueOf(game.getLevel()), 
				GAME_MARGIN_LEFT + tileMap.getWidth() + LEVEL_POSITION_X + levelWidth + levelNumberWidth / 2,
				GAME_MARGIN_TOP + tileMap.getHeight() - levelNumberHeight + levelNumberAscent + LEVEL_POSITION_Y);
		
		if(game.getGameState() == Game.State.PAUSED)
		{
			graphics.setColor(new Color(0, 0, 0, 150));
			graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
			if(font != null) graphics.setFont(font.deriveFont(PAUSE_TEXT_SIZE));
			graphics.setColor(Color.WHITE);
			int pausewidth = graphics.getFontMetrics().stringWidth("PAUSED");
			graphics.drawString("PAUSED", this.getWidth() / 2 - pausewidth / 2, 
					this.getHeight() / 2);
			if(font != null) graphics.setFont(font.deriveFont(PAUSE_SMALL_TEXT_SIZE));
			int pausesmallwidth = graphics.getFontMetrics().stringWidth("PRESS 'OK' BUTTON TO EXIT");
			graphics.drawString("PRESS 'OK' BUTTON TO EXIT", this.getWidth() / 2 - pausesmallwidth / 2, 
					this.getHeight() / 2 + 40);
			
		}

		//draw win or lose
		if(game.getGameState() == Game.State.WIN)
		{
			if(font != null) graphics.setFont(font.deriveFont(WIN_LOSE_TEXT_SIZE));
			graphics.setColor(Color.GREEN);
			int winWidth = graphics.getFontMetrics().stringWidth("WIN");
			int winHeight = graphics.getFontMetrics().getHeight();
			int winAscent = graphics.getFontMetrics().getAscent();
			
			graphics.drawString("WIN", GAME_MARGIN_LEFT + tileMap.getWidth() / 2 - winWidth / 2,
					GAME_MARGIN_TOP + tileMap.getHeight() / 2 - (winHeight - winAscent));
		}else if(game.getGameState() == Game.State.LOST)
		{
			if(font != null) graphics.setFont(font.deriveFont(WIN_LOSE_TEXT_SIZE));
			graphics.setColor(Color.RED);
			int winWidth = graphics.getFontMetrics().stringWidth("LOSE");
			int winHeight = graphics.getFontMetrics().getHeight();
			int winAscent = graphics.getFontMetrics().getAscent();
			
			graphics.drawString("LOSE", GAME_MARGIN_LEFT + tileMap.getWidth() / 2 - winWidth / 2,
					GAME_MARGIN_TOP + tileMap.getHeight() / 2 - (winHeight - winAscent));
		}
		
		
	}
	public TileMap getTileMap() {
		return tileMap;
	}
	
	public OnDrawListener getOnDrawListener() {
		return onDrawListener;
	}

	public void setOnDrawListener(OnDrawListener onDrawFinishedListener) {
		this.onDrawListener = onDrawFinishedListener;
	}

	public Tetromino getCurrentTetromino() {
		return currentTetromino;
	}
	
	public void setCurrentTetromino(Tetromino currentTetrimino) {
		this.currentTetromino = currentTetrimino;
	}
	
	public boolean isLevelingUp() {
		return levelingUp;
	}

	public void setLevelingUp(boolean levelingUp) {
		this.levelingUp = levelingUp;
	}

	public interface OnDrawListener{
		public void onDrawCalled(Graphics g);
	}
}
