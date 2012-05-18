/*
 * @(#)$Id: PapurBasicStub.java 16 2010-11-30 08:18:25Z ebayan $
 *
 * Author       :   Nadir Weibel, weibel@ucsd.edu
 *
 * Purpose      :   General abstract capture class to be used by active
 *                  components dealing with writing capture.
 *
 * 
 */

package edu.ucsd.hci.spaces.pen.activecomponent.stub;

import java.awt.Shape;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sigtec.ink.Note;
import org.sigtec.ink.NoteTool;
import org.sigtec.ink.input.DetailedLocation;
import org.sigtec.input.BufferedInputDevice;
import org.sigtec.input.util.BufferedInputDeviceTool;
import org.ximtec.ipaper.activecomponent.event.IPaperEvent;
import org.ximtec.ipaper.activecomponent.stub.DefaultStub;
import org.ximtec.ipaper.anoto.r41.message.PenUpMessage;
import org.ximtec.ipaper.client.PenClient;
import org.ximtec.iserver.activecomponent.ActiveComponentConfiguration;
import org.ximtec.iserver.activecomponent.ActiveComponentEvent;

import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MotionCode;
import edu.ucsd.hci.spaces.pen.PenUpListener;
import edu.ucsd.hci.spaces.pen.SpacesPenClient;
import edu.ucsd.hci.spaces.pen.handling.ClickHandler;
import edu.ucsd.hci.spaces.pen.handling.GUITransmitter;

/**
 * General capture class to be used by active components dealing with writing
 * capture.
 */
public class SpacesBasicStub extends DefaultStub implements Observer{

	private static final Logger LOGGER = Logger.getLogger(SpacesBasicStub.class
			.getName());
	private final static String PARAMETER_BUTTONNR = "buttonNr";
	protected static Shape captureArea;

	private long startTime = 0;
	private long lastRequestTime = 0;
	private long currentTime = 0;

	private BufferedInputDevice currentReader = null;

	private Timer timer = null;

	// handler is static and not initialized in init() because init() is called
	// each click;
	// change the PapurTransmitter argument to ClickHandler constructor to
	// change
	// PapurTransmitter implementors
	private static ClickHandler handler = new ClickHandler(new GUITransmitter());

	/**
	 * The document on which the capture has been initiated.
	 */
	private String document;

	/**
	 * The page on which the capture has been initiated.
	 */
	private int page;

	@Override
	public void init(ActiveComponentConfiguration config,
			ActiveComponentEvent event) {
		super.init(config, event);
		this.startTime = PenClient.getInstance().getLastRequestTime();
		this.lastRequestTime = this.startTime;
		this.document = this.getInitLocation().getID();
		this.page = this.getInitLocation().getPage();
		this.captureArea = PenClient.getInstance().requestSender
				.getSelectorForStub(this, this.getServletPath(),
						this.getInitLocation());
		
		PenUpListener.getPenEventHandler().addObserver(this);
		

	} // init

	public Shape getCaptureArea() {
		return captureArea;
	} // getCaptureArea

	/**
	 * Handles a new pen event. If the location to be handled lies outside of
	 * the capture area, the capture process is terminated and the handleNote
	 * method is invoked a last time. If the user has specified a note timeout
	 * parameter, the handleNote method gets invoked each time there is a
	 * timeout greater than the note timeout between two locations.
	 * 
	 * @param event
	 *            the iPaperEvent to be handled.
	 */
	@Override
	public synchronized void processEvent(ActiveComponentEvent event) {

		if (SpacesPenClient.isPenUp())
			return;
		
		DetailedLocation location = (DetailedLocation) ((IPaperEvent) event)
				.getLocation();
		this.currentReader = ((IPaperEvent) event).getInputDevice();
		this.currentTime = location.getTimestamp();

		if (captureArea != null) {

			if ((!location.getID().equals(this.document)
					|| (location.getPage() != this.page) || (!captureArea
					.contains(location.getPosition())))) {
				// everything except the current location
				this.lastRequestTime = this.currentTime - 1;
				this.setDone();
				// delegate event
				PenClient.getInstance().processListenerEvent(
						(IPaperEvent) event);
			} else {
				//Sampled at ~100Hz.  A sample every ~10ms.67
				//LOGGER.info("Basic Stub");
				LOGGER.info("X=" + location.getPosition().getX() + ", Y="
						+ location.getPosition().getY() + ", T="
						+ location.getTimestamp() + ", F="
						+ location.getForce());
				
				LOGGER.info("Document=" + this.document + ", Page=" + this.page);
				
				SpacesPenClient.server.issueMotion(MotionCode.PenDown, 
						(float)location.getPosition().getX(), (float)location.getPosition().getY(),
						(int)location.getTimestamp(), location.getForce(),
						this.document, this.page);

				Note note = BufferedInputDeviceTool.getNote(this.currentReader,
						this.startTime, location.getTimestamp() - 1, 35);
				note.interpolate();
				
				if(note.getBounds2D().getWidth() > 10 || note.getBounds2D().getHeight() > 10)
					NoteTool.exportJPEG(note, "/tmp/notes/note.jpg");

			}
		} else {
			this.setDone();
		}

	}// processEvent

	@Override
	public void finish() {
		LOGGER.log(Level.INFO, "AC DONE");
	} // finish

	@Override
	public String getUserAdvice() {
		return "Run a default Active Component";
	} // getUserAdvice

	@Override
	public void update(Observable source, Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof PenUpMessage)
		{
			PenUpMessage penUp = (PenUpMessage)obj;
			LOGGER.info("PenUp");
			SpacesPenClient.server.issueMotion(MotionCode.PenUp, 
					(float)-1.0, (float)-1.0,
					0, 0,
					this.document, this.page);
		}
	}

}
