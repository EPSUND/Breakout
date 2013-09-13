package gui;
import java.awt.image.BufferedImage;

/**
 * An animated sprite using BufferedImage
 * @author Erik
 *
 */
public class AnimatedSprite {
	private BufferedImage[] sprites;
	private int frame, num_frames, tick;
	private static final int FRAME_DELAY = 10;

	/**
	 * Constructor for AnimatedSprite
	 * @param sprites The sprites of the animation
	 * @param num_frames The number of frames
	 */
	public AnimatedSprite(BufferedImage[] sprites, int num_frames) {
		this.sprites = sprites;
		this.num_frames = num_frames;
		frame = 0;
		tick = 0;
	}
	
	/**
	 * Gets the current image in the animation
	 * @return The current image in the animation
	 */
	public BufferedImage getCurrentImage() {
		tick++;
		if(tick > FRAME_DELAY) {
			if(frame < (num_frames - 1)) {
				frame++;
			}
			else {
				frame = 0;
			}
			tick = 0;
		}
		
		return sprites[frame];
	}
}
