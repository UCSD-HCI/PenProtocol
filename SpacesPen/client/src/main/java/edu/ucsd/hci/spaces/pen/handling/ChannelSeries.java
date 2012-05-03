package edu.ucsd.hci.spaces.pen.handling;

import java.util.ArrayList;
import java.util.List;

public class ChannelSeries extends ArrayList<Integer> {
	protected final int id;
	protected int ptr;
	protected boolean circular;
	
	public ChannelSeries(int id) {
		this(id, true);
	}
	
	public ChannelSeries(int id, boolean isCircular)
	{
		this.id = id;
		ptr = 0;
		circular = isCircular;
	}
	
	public boolean add(Integer i) {
		if(this.size() == 0 || this.get(this.size() - 1) != i)
		{
			super.add(i);
			return true;
		}
		
		return false;
	}
	/**
	 * Return the next channel in the series.
	 * @return
	 */
	public int next() {
		if(this.size() == 0)
			return -1;
		
		if(ptr >= this.size())
			ptr = circular ? 0 : this.size() - 1;
		if(ptr < 0)
			ptr = circular ? this.size() - 1 : 0;
			
		return this.get(ptr++);
	}
	
	/**
	 * Return the previous channel in this series.
	 * @return
	 */
	public int prev() {
		if(this.size() == 0)
			return -1;
		
		if(ptr < 0)
			ptr = circular ? this.size() - 1 : 0;
		if(ptr >= this.size())
			ptr = circular ? 0 : this.size() - 1;
		
		return this.get(ptr--);
	}
	
	
	/**
	 * Return the first channel in this series. Note that this
	 * does not move the internal pointer to point to the first
	 * channel, but simply returns the first channel.
	 * 
	 * @return
	 */
	public int first() {
		if(this.size() == 0)
			return -1;
		
		return this.get(0);
	}
	
	/**
	 * Return the last channel in this series. Note that this does
	 * not move the internal pointer to point to the last channel,
	 * but simply returns the last channel.
	 * 
	 * @return
	 */
	public int last() {
		if(this.size() == 0)
			return -1;
		
		return this.get(this.size() - 1);
	}
	
	/**
	 * Reset this series so that the internal pointer points to the first
	 * channel in this series, then return the first channel in the series.
	 * The next call to next() will also return the first channel.
	 * 
	 * @return
	 */
	public int toFirst() {
		ptr = this.size() == 0 ? -1 : 0;
		return this.size() == 0 ? -1 : this.get(0);
	}
	
	/**
	 * Set this series so that the internal pointer points to the last
	 * channel in this series, then return the last channel in the series.
	 * The next call to prev() will also return the last channel.
	 * 
	 * @return
	 */
	public int toLast() {
		ptr = this.size() == 0 ? -1 : this.size() - 1;
		return ptr >= 0 ? this.get(ptr) : -1;
	}
	
	/**
	 * Empty this channel series, and reset the internal pointer to zero.
	 */
	public void clear() {
		super.clear();
		ptr = 0;
	}
	
	/**
	 * Equality of Channel series is based solely on id.
	 */
	public boolean equals(Object other) { return ((ChannelSeries) other).id == id; }
	
	public String toString() {
		return super.toString() + ":" + ptr;
	}
}
