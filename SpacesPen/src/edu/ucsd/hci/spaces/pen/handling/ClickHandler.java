package edu.ucsd.hci.spaces.pen.handling;

import java.awt.Point;
import java.util.logging.Logger;

import org.ximtec.ipaper.activecomponent.event.IPaperEvent;
import org.ximtec.iserver.activecomponent.ActiveComponentEvent;

public class ClickHandler {
	public static final Logger LOGGER = Logger.getLogger(edu.ucsd.hci.spaces.pen.activecomponent.stub.SpacesBasicStub.class.getName());
	
	private PapurTransmitter transmitter;
	
	public ClickHandler(PapurTransmitter trans)
	{
		transmitter = trans;
	}

	private int lastButton = -1;				// ID of last button pressed
	private Point lastLocation = null;			// Point of last click
	private long lastClickTime = -1;			// time of last click
	public static double DIST_THRESH_MM = 3;	// minimum distance between clicks to count them as separate clicks (if on same button)
	public static long TIME_THRESH_MS = 300;		// minimum time between clicks to count them as separate clicks (if on same button)
	
	private double dist(Point a, Point b) {
		if(a == null || b == null)
			return -1;
		
		int x = a.x - b.x;
		int y = a.y - b.y;
		return Math.sqrt(x*x+y*y);
	}
	
	private boolean canExecute(int buttonID, IPaperEvent event) {
		Point location = event.getLocation().getPosition();
		long now = System.currentTimeMillis();
		boolean canExecute = false;
		
		// If last location doesn't exist, or different button, or distance to last location is greater
		// than the distance threshhold, then this is a valid click
		if(lastLocation == null || buttonID != lastButton || dist(location, lastLocation) > DIST_THRESH_MM
			|| now - lastClickTime > TIME_THRESH_MS)
		{
			canExecute = true;
		}
						
		lastButton = buttonID;
		lastLocation = location;
		lastClickTime = now;
		return canExecute;
	}
	
	public synchronized void execute(int buttonID, IPaperEvent event)
	{
		LOGGER.info("--------EVENT: " + event + "; " + event.getClass().getName());
		if(canExecute(buttonID, event))
		{
		
			LOGGER.info("======== CAN EXECUTE ========");
			Button button = Button.get(buttonID-1);
			button = transmitter.beforeHandle(button, event);
			
			if(button == null)
				return;
			
			int value = 0;
			switch(button)
			{
			case POWER:
				transmitter.togglePower();
				break;
			case BIG_A:
			case BIG_B:
			case BIG_C:
				transmitter.bigButton(button);
				break;
			case ENTER:
				transmitter.enter();
				break;
			case LAST:
				transmitter.lastChannel();
				break;
			case NEXT:
				transmitter.nextChannel();
				break;
			case NUM_0: value = 0;
			case NUM_1:
			case NUM_2:	value++;
			case NUM_3:
			case NUM_4:
			case NUM_5:
			case NUM_6:
			case NUM_7:
			case NUM_8:
			case NUM_9:
				transmitter.num(button);
				break;
			case VOL_UP:
				transmitter.volumeUp();
				break;
			case VOL_DOWN:
				transmitter.volumeDown();
				break;
			case CHAN_UP:
				transmitter.channelUp();
				break;
			case CHAN_DOWN:
				transmitter.channelDown();
				break;
			case FN_1:
			case FN_2:
			case FN_3:
			case FN_4:
				transmitter.function(button);
				break;
			default:
				transmitter.other(button);
				break;
			}
		}
		
		LOGGER.info(transmitter.checkStatus());
	}
}