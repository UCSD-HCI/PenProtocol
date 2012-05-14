package edu.ucsd.hci.spaces.pen.handling;

public enum Button {
	POWER,
	BIG_A,
	BIG_B,
	BIG_C,
	ENTER,
	LAST,
	NEXT,
	NUM_1,
	NUM_2,
	NUM_3,
	NUM_4,
	NUM_5,
	NUM_6,
	NUM_7,
	NUM_8,
	NUM_9,
	NUM_0,
	VOL_UP,
	VOL_DOWN,
	CHAN_UP,
	CHAN_DOWN,
	FN_1,
	FN_2,
	FN_3,
	FN_4;
	
	public static Button get(int i) {
		return Button.values()[i];
	}
	
	public boolean isNum() {
		return getNum() >= 0;
	}
	
	public int getNum() {
		int value = -1;
		switch(this)
		{
		case NUM_0: value = 0; break;
		case NUM_1: value = 1; break;
		case NUM_2: value = 2; break;
		case NUM_3: value = 3; break;
		case NUM_4: value = 4; break;
		case NUM_5: value = 5; break;
		case NUM_6: value = 6; break;
		case NUM_7: value = 7; break;
		case NUM_8: value = 8; break;
		case NUM_9: value = 9; break;
		default: value = -1; break;
		}
		
		return value;
	}
}
