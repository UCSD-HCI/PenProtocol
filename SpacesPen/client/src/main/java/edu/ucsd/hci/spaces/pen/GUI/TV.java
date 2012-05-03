package edu.ucsd.hci.spaces.pen.GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * A representation of a TV featuring a 2D representation of the TV.
 * 
 * @author Ellison
 *
 */
public class TV extends Component {
	public static final int CHANNEL_MAX = 4; // 1+highest channel
	public static final int CHANNEL_MIN = 2; // lowest channel
	private static final int width = 400, height = 400;	// window dimensions
	
	// these are the main variables needed by the paint() method
	boolean isOn;
	int channel;
	int volume;
	
	// array of still images representing each channel
	BufferedImage[] channelBGs = new BufferedImage[CHANNEL_MAX];
	
	// general font for channel and volume
	private Font font = new Font("", Font.PLAIN, 30);
	
	public TV() throws IOException {
		isOn = false;
		channel = 2;
		volume = 50;
		
		initChannelBGs();
	}
	
	/**
	 * Read in all the channel backgrounds.
	 * @throws IOException
	 */
	private void initChannelBGs() throws IOException {
		String fileBase = "face";
		String extension = ".jpg";
		for(int i = CHANNEL_MIN; i < CHANNEL_MAX; i++) {
			channelBGs[i] = scaleImage(ImageIO.read(new File(fileBase + i + extension)), width, height);
		}
	}
	
	/*
	 * All setters should repaint when done.
	 */
	
	public void setChannel(int channel) {
		this.channel = channel;
		repaint();
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
		repaint();
	}
	
	public void togglePower() {
		isOn = !isOn;
		repaint();
	}
	
	public void paint(Graphics g) {
		if(isOn)
		{
			drawBG(g);
			g.setFont(font);
			drawChannel(g);
			drawVolume(g);
		}
		else
		{
			g.setColor(Color.black);
			g.fillRect(0, 0, width, height);
		}
	}
	
	/**
	 * Draw background to show what the current channel is pictorially.
	 * @param g
	 */
	private void drawBG(Graphics g) {
		if(channel >= 0 || channel < channelBGs.length)
		{
			BufferedImage bg = channelBGs[channel];
			
			g.drawImage(bg, 0, 0, bg.getWidth(), bg.getHeight(), 0, 0, width, height, null);
		}
	}
	
	/**
	 * Draw channel number.
	 * @param g
	 */
	private void drawChannel(Graphics g) {
		int x = 10, y = 36;	// upper left corner
		String str = (channel < 10 ? "0" : "") + channel;
		
		Color mainColor = Color.green, outlineColor = Color.black;

		drawOutlinedString(g, str, x, y, mainColor, outlineColor);
	}
	
	private void drawVolume(Graphics g) {
		int x = width-60, y = height-50;	// lower right corner
		String str = volume+"";
		Color mainColor = Color.red, outlineColor = Color.white;
		
		drawOutlinedString(g, str, x, y, mainColor, outlineColor);
	}
	
	/**
	 * Draw a string with a mediocre outline.
	 * @param g
	 * @param str
	 * @param x
	 * @param y
	 * @param mainColor
	 * @param outlineColor
	 */
	private void drawOutlinedString(Graphics g, String str, int x, int y, Color mainColor, Color outlineColor) {

		int outlineWidth = 3;
		
		g.setColor(outlineColor);
		
		for(int i = 1; i <= outlineWidth; i++)
		{
			g.drawString(str, ShiftWest(x, i), ShiftNorth(y, i));
			g.drawString(str, ShiftWest(x, i), ShiftSouth(y, i));
			g.drawString(str, ShiftEast(x, i), ShiftNorth(y, i));
			g.drawString(str, ShiftEast(x, i), ShiftSouth(y, i));
		}
		
		g.setColor(mainColor);
		g.drawString(str, x, y);
	}
	
	// Test driver
	public static void main(String[] args) throws IOException {
		JFrame f = new JFrame("TV");
        
        f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        TV tv = new TV();
        tv.togglePower();
        tv.setChannel(3);
        tv.setVolume(40);
        f.add(tv);
        f.setMinimumSize(new Dimension(width, height));
        f.setVisible(true);
	}

	
	/*
	 * Utility methods
	 */
	
	int ShiftNorth(int p, int distance) {
		return (p - distance);
	}
	int ShiftSouth(int p, int distance) {
		return (p + distance);
	}
	int ShiftEast(int p, int distance) {
		return (p + distance);
	}
	int ShiftWest(int p, int distance) {
		return (p - distance);
	}

	/**
	 * Scale an image to the given width and height.
	 * 
	 * @param img
	 * @param width
	 * @param height
	 * @return
	 */
	private BufferedImage scaleImage(BufferedImage img, int width, int height) {
		// Create new (blank) image of required (scaled) size
		BufferedImage scaledImage = new BufferedImage(
		   width, height, BufferedImage.TYPE_INT_ARGB);

		// Paint scaled version of image to new image
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(img, 0, 0, width, height, null);

		// clean up
		graphics2D.dispose();

		return scaledImage;
	}
}
