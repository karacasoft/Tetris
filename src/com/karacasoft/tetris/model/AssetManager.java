package com.karacasoft.tetris.model;

import java.awt.Image;
import java.util.HashMap;
/**
 * A singleton to hold all the assets of the game.
 * 
 * @author Triforce
 *
 */
public class AssetManager {

	private static AssetManager instance = null;
	private HashMap<String, Image> images = new HashMap<String, Image>();
	
	private AssetManager()
	{}
	/**
	 * Returns the instance of {@link AssetManager}.
	 * @return {@link AssetManager} instance.
	 */
	public static AssetManager getInstance()
	{
		if(instance == null)
		{
			instance = new AssetManager();
		}
		return instance;
	}
	/**
	 * Adds an image to the AssetManager map.
	 * @param key String key
	 * @param img image
	 */
	public void addImage(String key, Image img) {
		images.put(key, img);
	}
	/**
	 * Get image with the String key.
	 * @param key String key 
	 * @return the image for given key.
	 */
	public Image getImage(String key)
	{
		return images.get(key);
	}
}
