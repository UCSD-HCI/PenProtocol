package edu.ucsd.hci.interactivespace.annotopen;

import java.io.IOException;

import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MotionCode;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.StatusCode;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BroadcastServer server = new BroadcastServer("192.168.1.14", 65432);	//will run a new thread
			
			server.issueCustomString("Hello World!");
			server.issueStatus(StatusCode.Connected, "Pen Connected");
			server.issueMotion(MotionCode.PenMove, 32.4f, 63.5f, 1335505852, 255, "DocumentName", 1);
			
			System.out.println("All commands issued.");
			
			server.join();	//wait until server thread quits
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
