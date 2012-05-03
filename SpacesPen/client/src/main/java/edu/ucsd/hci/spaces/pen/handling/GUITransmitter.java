package edu.ucsd.hci.spaces.pen.handling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ximtec.ipaper.activecomponent.event.IPaperEvent;
import org.ximtec.iserver.activecomponent.ActiveComponentEvent;

import edu.ucsd.hci.spaces.pen.GUI.TV;
import edu.ucsd.hci.spaces.pen.handling.StubTransmitter.Mode;

public class GUITransmitter extends StubTransmitter {
	TV tv;
	
	public GUITransmitter() {
		try
		{
			tv = new TV();
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("ERROR INITIALIZING TV");
		}
	}

	@Override
	public void togglePower() {
		super.togglePower();
		tv.togglePower();
	}
	

	@Override
	public void setChannel(int channel) {
		super.setChannel(channel);
		tv.setChannel(this.channel);
	}
	
	@Override
	public void setVolume(int volume) {
		super.setVolume(volume);
		tv.setVolume(this.volume);
	}

}
