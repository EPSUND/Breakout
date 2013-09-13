package sound;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.Helpers;

/***
 * A class used for playing background music
 * @author Erik
 *
 */
public class Jukebox {
	private HashMap<String, Clip> songs;
	private Clip activeSong;
	private boolean muted;
	
	/***
	 * The constructor of Jukebox
	 */
	public Jukebox()
	{
		songs = new HashMap<String, Clip>();
		activeSong = null;
		muted = false;
	}
	
	/***
	 * Loads a song and associates it with a name
	 * @param fileName The path and file name of the song
	 * @param name The name by which the song should be known
	 */
	public void loadSong(String fileName, String name)
	{
		try
		{
			URL songURL = Helpers.getResourceURL(this, fileName);
			
			Clip song = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(songURL);
			song.open(inputStream);
			
			songs.put(name, song);
		}
		catch(UnsupportedAudioFileException e)
		{
			System.err.println("Breakout: Unsupported audio file");
			e.printStackTrace();
			return;
		}
		catch (IOException e) 
		{
			System.err.println("Breakout: Could not load song");
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
	
	/***
	 * Plays a song once
	 * @param song The song
	 */
	public void playSong(String song)
	{
		//Don't play a song if the jukebox has been muted
		if(muted)
		{
			return;
		}
		
		//If no song with that name exists
		if(!songs.containsKey(song))
		{
			System.err.println("Breakout: No song called " + song + " exists");
			return;
		}
		
		//Stops and rewinds the current song
		if(activeSong != null)
		{
			activeSong.loop(0);//Stop any looping
			activeSong.stop();
			activeSong.setFramePosition(0);
		}
		
		//Get the new song
		activeSong = songs.get(song);
		
		//Start playing the new song
		activeSong.start();
	}
	
	/***
	 * Loop a song
	 * @param song The song
	 */
	public void loopSong(String song)
	{
		//Don't play a song if the jukebox has been muted
		if(muted)
		{
			return;
		}
		
		//If no song with that name exists
		if(!songs.containsKey(song))
		{
			System.err.println("Breakout: No song called " + song + " exists");
			return;
		}
		
		//Stops and rewinds the current song
		if(activeSong != null)
		{
			activeSong.loop(0);//Stop any looping
			activeSong.stop();
			activeSong.setFramePosition(0);
		}
			
		//Get the new song
		activeSong = songs.get(song);
			
		//Loop the new song
		activeSong.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/***
	 * Dispose of all resources held by the jukebox
	 */
	public void disposeJukebox()
	{
		//Close all song clips
		for(Clip song : songs.values())
		{
			song.stop();
			song.close();
		}
		
		//Clear the song collection
		songs.clear();
	}
	
	/***
	 * Mute the jukebox
	 */
	public void mute()
	{
		//Stops and rewinds the current song
		if(activeSong != null)
		{
			activeSong.loop(0);//Stop any looping
			activeSong.stop();
			activeSong.setFramePosition(0);
		}
		
		muted = true;
	}
	
	/***
	 * Unmute the jukebox
	 */
	public void unmute()
	{
		if(activeSong != null)
		{
			//Begin playing the active song again
			activeSong.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		muted = false;
	}
	
	/***
	 * Get if the jukebox is muted
	 * @return If the jukebox is muted
	 */
	public boolean isMuted()
	{
		return muted;
	}
}
