package sound;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

import utils.Helpers;

import java.util.HashMap;

/**
 * A class used for playing sound effects
 * @author Erik
 *
 */
public class SoundPlayer {
	/*Table of sound clips*/
	private HashMap<String, Clip> clips;
	private boolean muted;
	
	/**
	 * The constructor of SoundPlayer
	 */
	public SoundPlayer()
	{
		clips = new HashMap<String, Clip>();
		muted = false;
	}
	
	/**
	 * Loads a sound clip and associates the clip with a name
	 * @param fileName The name and path of the clip
	 * @param name The name to associate with the clip
	 */
	public void loadClip(String fileName, String name)
	{
		try
		{
			URL soundURL = Helpers.getResourceURL(this, fileName);
			
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundURL);
			clip.open(inputStream);
			
			clips.put(name, clip);
		}
		catch(UnsupportedAudioFileException e)
		{
			System.err.println("Breakout: Unsupported audio file");
			e.printStackTrace();
			return;
		}
		catch (IOException e) 
		{
			System.err.println("Breakout: Could not load audioclip");
			e.printStackTrace();
			return;
		} 
		catch (LineUnavailableException e) 
		{
			System.err.println("Breakout: Line unavailable");
			e.printStackTrace();
			return;
		}
		catch(Exception e)
		{
			System.err.println("Breakout: An exception occured");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Plays a sound clip
	 * @param clipName The clip to play
	 */
	public void playClip(String clipName)
	{
		Clip clip;
		
		//Don't play the clip if the sound has been muted
		if(muted)
		{
			return;
		}
		
		//Check if the clip exists
		if(!clips.containsKey(clipName))
		{
			System.err.println("Breakout: No clip called " + clipName + " exists");
			return;
		}
		
		try {
			clip = clips.get(clipName);
			
			//Stop the clip if it is already running
			if(clip.isRunning())
			{
				clip.stop();
			}
			
			//Rewind the clip and play it
			clip.setFramePosition(0);
			clip.start();
		} catch (Exception e) {
			System.err.println("Breakout: Could not play clip " + clipName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Dispose of the resources held by the sound player
	 */
	public void disposePlayer()
	{
		//Close all sound clips
		for(Clip clip : clips.values())
		{
			clip.stop();
			clip.close();
		}
		//Clear the clip collection
		clips.clear();
	}
	
	/**
	 * Get if the sound player is muted
	 * @return If the sound player is muted
	 */
	public boolean isMuted()
	{
		return muted;
	}
	
	/**
	 * Mute the sound player
	 */
	public void mute()
	{
		muted = true;
	}
	
	/**
	 * Unmute the sound player
	 */
	public void unmute()
	{
		muted = false;
	}
}
