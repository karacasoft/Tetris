package com.karacasoft.tetris.sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundManager {
	private static SoundManager instance;
	
	private HashMap<String, String> inStreamMap = new HashMap<String, String>();
	
	
	private SoundManager()
	{
		
	}
	
	public static SoundManager getInstance()
	{
		if(instance == null)
			instance = new SoundManager();
		return instance;
	}
	
	public void addAudio(String key, String fileName)
	{
		inStreamMap.put(key, fileName);
	}
	
	public synchronized void playAudio(final String key)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if(inStreamMap.containsKey(key))
				{
					try {
						Clip clip = AudioSystem.getClip();
						AudioInputStream inStream = AudioSystem.getAudioInputStream(new File("assets/sfx/" + inStreamMap.get(key)));
						clip.open(inStream);
						clip.addLineListener(new LineListener() {
							
							@Override
							public void update(LineEvent event) {
								if(event.getType() == LineEvent.Type.STOP)
								{
									clip.close();
								}
							}
						});
						clip.start();
						
					} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
						System.err.println("Sound could not be played.");
						e.printStackTrace();
					}
					
				}else{
					System.err.println("No sfx found for this key.");
				}
			}
		}).start();
		
	}
	
}
