package edu.ucsd.hci.spaces.pen.handling;

import org.ximtec.ipaper.activecomponent.event.IPaperEvent;
import org.ximtec.iserver.activecomponent.ActiveComponentEvent;

public interface PapurTransmitter {
	/**
	 * ClickHandler.execute will call this method before
	 * calling a handler method. If this method returns
	 * null, the handler method will not be called. The
	 * returned Button determines which handler is called.
	 * 
	 * @param which
	 * @return
	 */
	Button beforeHandle(Button which, IPaperEvent event);
	Object afterHandle(Button which, IPaperEvent event);
	
	void togglePower();
	
	void bigButton(Button which);
	
	void enter();
	void lastChannel();
	void nextChannel();
	void setChannel(int channel);

	void num(Button digit);
	
	void volumeUp();
	void volumeDown();
	
	void channelUp();
	void channelDown();
	
	void function(Button which);
	
	void other(Button which);
	
	String checkStatus();
}
