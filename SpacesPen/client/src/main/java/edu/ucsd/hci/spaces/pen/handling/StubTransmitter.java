package edu.ucsd.hci.spaces.pen.handling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.ximtec.ipaper.activecomponent.event.IPaperEvent;
import org.ximtec.iserver.activecomponent.ActiveComponentEvent;

public class StubTransmitter implements PapurTransmitter {
	protected static final Logger LOGGER = Logger.getLogger(edu.ucsd.hci.spaces.pen.activecomponent.stub.SpacesBasicStub.class.getName());
	public static final int VOLUME_MAX = 100;
	public static final int VOLUME_MIN = 0;
	public static final int DEFAULT_VOLUME_INCR = 10;
	public static final int CHANNEL_MIN = 2;
	public static final int CHANNEL_MAX = 999;
	public static final int NUM_SERIES = 3;
	
	int volume = 50;
	int volumeIncr = 10;
	
	int channel;
	
	
	private Mode mode;
	private ActiveComponentEvent currentEvent;
	
	private String numberAccumulator = "";
	
	// BIG_A, BIG_B, and BIG_C
	List<ChannelSeries> series;
	private ChannelSeries currentSeries = null;
	private ChannelSeries channelHistory = new ChannelSeries(999, false);
	
	public StubTransmitter() {
		mode = Mode.OFF;
		
		series = new ArrayList<ChannelSeries>(NUM_SERIES);
		for(int i = 0; i < NUM_SERIES; i++)
		{
			series.add(new ChannelSeries(i));
		}
		
		// initial channel
		channelHistory.add(2);
		channel = channelHistory.next();
	}
	
	@Override
	public Button beforeHandle(Button which, IPaperEvent event) {
		LOGGER.info("beforeHandle("+which+"); current mode is " + mode);
		
		currentEvent = event;
		if(which == Button.POWER)
			return which;
		
		else if(mode.is(Mode.OFF))
		{
			currentEvent = null;
			return null;
		}
		
		return which;
	}
	
	@Override
	public Object afterHandle(Button buttonID, IPaperEvent event)
	{
		this.currentEvent = null;
		return null;
	}
	
	@Override
	public void togglePower() {
		mode = (mode == Mode.OFF ? Mode.IDLE : Mode.OFF);
		
		LOGGER.info("Power is " + (mode.is(Mode.OFF) ? "OFF" : "ON"));
	}

	@Override
	public void bigButton(Button which) {
		int whichSeries = -1;
		switch(which)
		{
		case BIG_A: whichSeries = 0; break;
		case BIG_B: whichSeries = 1; break;
		case BIG_C: whichSeries = 2; break;
		}
		
		if(whichSeries == -1)
		{
			LOGGER.info("UNKNOWN SERIES: " + which);
			return;
		}
		
		switch(mode)
		{
		case PROGRAM:
			mode = Mode.PROGRAM_SERIES_CHANNEL_ENTER;
			currentSeries = series.get(whichSeries);
			break;
			
		case RESET:
			mode = Mode.RESET_SERIES;
			currentSeries = series.get(whichSeries);
			break;
			
		case IDLE:
			currentSeries = series.get(whichSeries);
			int channel = currentSeries.next();
			if(channel > 0)
				this.setChannel(channel);
			break;
		}
	}

	@Override
	public void enter() {
		switch(mode)
		{
		case PROGRAM:
			mode = Mode.IDLE;
			break;
			
		case PROGRAM_VOLUME:
			Integer newVolIncr = consumeNumberAccumulator();
			if(newVolIncr != null)
			{
				volumeIncr = newVolIncr;
				mode = Mode.IDLE;
			}
			break;
			
		case PROGRAM_SERIES_CHANNEL_ENTER:
			if(currentSeries != null)
			{
				Integer channel = consumeNumberAccumulator();
				if(channel == null)
					break;
				
				if(channel < CHANNEL_MIN || channel > CHANNEL_MAX)
					break;
				
				if(currentSeries.last() != channel)
					currentSeries.add(channel);
			}
			break;
			
		case RESET_SERIES:
			if(currentSeries != null)
				currentSeries.clear();
			
			mode = Mode.IDLE;
			break;
			
		case RESET_VOLUME:
			volumeIncr = DEFAULT_VOLUME_INCR;
			mode = Mode.IDLE;
			break;
			
		case IDLE:
			Integer newChannel = consumeNumberAccumulator();
			if(newChannel != null)
			{
				setChannel(newChannel);
			}
			break;
		}
	}

	@Override
	public void lastChannel() {
		if(channelHistory.size() > 0)
		{
			int prevChannel = channelHistory.prev();
			
			if(prevChannel >= CHANNEL_MIN)
				this.channel = prevChannel;
		}
	}

	@Override
	public void nextChannel() {
		if(channelHistory.size() > 0)
		{
			int nextChannel = channelHistory.next();
			
			if(nextChannel <= CHANNEL_MAX)
				this.channel = nextChannel;
		}
	}
	
	@Override
	public void setChannel(int channel) {
		if(channel < CHANNEL_MIN)
			channel = CHANNEL_MIN;
		else if(channel > CHANNEL_MAX)
			channel = CHANNEL_MAX;
		
		channelHistory.add(channel);
		this.channel = channelHistory.toLast();
	}

	@Override
	public void num(Button digit) {
		LOGGER.info("NUMBERPAD (" + digit + ")");
		
		int value = digit.getNum();
		
		if(value == -1)
			return;
		
		switch(mode)
		{
		case PROGRAM_VOLUME:
			numberAccumulator += value;
			break;
		case PROGRAM_SERIES_CHANNEL_ENTER:
			numberAccumulator += value;
			break;
		case IDLE:
			numberAccumulator += value;
			break;
		}
	}

	@Override
	public void volumeUp() {
		switch(mode)
		{
		case PROGRAM:
			mode = Mode.PROGRAM_VOLUME;
			break;
			
		case RESET:
			mode = Mode.RESET_VOLUME;
			break;
			
		default:
			setVolume(volume + volumeIncr);
			break;
		}
	}

	@Override
	public void volumeDown() {
		switch(mode)
		{
		case PROGRAM:
			mode = Mode.PROGRAM_VOLUME;
			break;
			
		case RESET:
			mode = Mode.RESET_VOLUME;
			break;
			
		default:
			setVolume(volume - volumeIncr);
			break;
		}
	}
	
	protected void setVolume(int vol) {
		volume = vol;
		if(volume < VOLUME_MIN)
			volume = VOLUME_MIN;
		else if(volume > VOLUME_MAX)
			volume = VOLUME_MAX;
	}
	
	@Override
	public void channelUp() {
		setChannel(++channel);
	}

	@Override
	public void channelDown() {
		setChannel(--channel);
	}

	@Override
	public void function(Button which) {
		switch(which)
		{
		case FN_1:
			fn_1();
			break;
			
		case FN_2:
			fn_2();
			break;
			
		case FN_3:
			fn_3();
			break;
			
		case FN_4:
			fn_4();
			break;
			
		default:
			LOGGER.info("UNHANDLED FUNCTION BUTTON: " + which);
			break;
		}
	}
	
	// Program
	private void fn_1() {
		if(mode.is(Mode.PROGRAM, Mode.PROGRAM_SERIES_CHANNEL_ENTER, Mode.PROGRAM_VOLUME))
		{
			endProgramMode();
		}
		else
		{
			beginProgramMode();
		}
	}
	
	private void beginProgramMode() {
		LOGGER.info("Beginning program mode.");
		mode = Mode.PROGRAM;
		consumeNumberAccumulator();
	}
	
	private void endProgramMode() {
		LOGGER.info("Ending program mode.");
		mode = Mode.IDLE;
		consumeNumberAccumulator();
	}
	
	// Presets 1
	private void fn_2() {
		
	}
	
	// Presets 2, or other function
	private void fn_3() {
		
	}
	
	// Reset
	private void fn_4() {
		mode = Mode.RESET;
		consumeNumberAccumulator();
	}

	
	@Override
	public void other(Button which) {
		LOGGER.info("UNHANDLED BUTTON: " + which);
	}
	
	private Integer consumeNumberAccumulator() {
		if(!"".equals(numberAccumulator))
		{
			int value = Integer.parseInt(numberAccumulator);
			numberAccumulator = "";
			return new Integer(value);
		}
		
		return null;
	}
	static enum Mode{
		OFF,
		IDLE,
		PROGRAM,
		PROGRAM_SERIES_CHANNEL_ENTER,
		PROGRAM_VOLUME,
		RESET,
		RESET_SERIES,
		RESET_VOLUME;
		
		
		public boolean is(Mode ... modes) {
			if(modes == null || modes.length == 0)
				return false;
			
			for(int i = 0; i < modes.length; i++)
				if(modes[i] == this)
					return true;
			
			return false;
		}
	}
	
	@Override
	public String checkStatus() {
		StringBuffer status = new StringBuffer(528);
		String hr = "======================================================\n";
		String nl = "\n";
		status.append(hr);
		status.append("STATUS: ").append(mode).append(nl);
		status.append("  current channel = ").append(channel).append(nl);
		status.append("    channel history = ").append(channelHistory).append(nl);
		status.append("  volume = ").append(volume).append(nl);
		status.append("    volume increment = ").append(volumeIncr).append(nl);
		status.append("  series\n");
		status.append("    series A = ").append(series.get(0)).append(nl);
		status.append("    series B = ").append(series.get(1)).append(nl);
		status.append("    series C = ").append(series.get(2)).append(nl);
		status.append("  numberAccumulator = ").append(numberAccumulator).append(nl);
		status.append(hr);
		
		return status.toString();
	}
	
	
}
