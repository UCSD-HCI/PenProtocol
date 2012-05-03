/*
 * $HeadURL		:	RefreshMyDesktop.java $$
 *
 * Creator		:	Nadir Weibel, weibel@ucsd.edu
 *
 * Date			:	May 19, 2010	
 *
 * Purpose		:  
 *
 * -----------------------------------------------------------------------
 *
 * Revision Information:
 *
 * $LastChangedDate: 2010-06-03 15:59:46 -0700 (Thu, 03 Jun 2010) $$
 *	$Revision: 17 $$
 * $Author: weibel $$
 *
 * -----------------------------------------------------------------------
 *
 * Copyright 2009-2010 Nadir Weibel. All Rights Reserved.
 *
 * This software is the proprietary information of Nadir Weibel.
 * Use is subject to license terms.
 * 
 */
package edu.ucsd.hci.spaces.pen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import com.apple.eawt.AppEvent.OpenURIEvent;
import com.apple.eawt.AppEventListener;
import com.apple.eawt.OpenURIHandler;

import org.apache.commons.configuration.Configuration;
import org.sigtec.input.InputDevice;
import org.sigtec.input.InputDeviceEventListener;
import org.sigtec.input.InputHandler;
import org.ximtec.ipaper.anoto.r41.message.CoordinateMessage;
import org.ximtec.ipaper.anoto.r41.message.PenUpMessage;
import org.ximtec.ipaper.client.IPaperClient;
import org.ximtec.ipaper.client.PenClient;
import org.ximtec.ipaper.client.app.DefaultClient;
import org.ximtec.ipaper.client.app.PenTool;
import org.ximtec.ipaper.core.Document;
import org.ximtec.ipaper.core.IPaper;
import org.ximtec.ipaper.core.Page;
import org.ximtec.ipaper.input.ExtendedInputHandler;
import org.ximtec.iserver.core.IServer;
import org.ximtec.iserver.core.Individual;

import edu.ucsd.hci.interactivespace.annotopen.BroadcastServer;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.StatusCode;


/**
 * The Class RefreshMyDesktop.
 */
public class SpacesPenClient extends DefaultClient implements Observer{

	private static final Logger LOGGER = Logger.getLogger(PenClient.class.getName());
	static public BroadcastServer server;
	
   private static boolean penUp = true;

   private static boolean connected = false;

   /**
    * Instantiates a new refresh my desktop.
    * 
    * @param args
    *           the args
    * @throws IOException
    */
   public SpacesPenClient(String args[]) throws IOException {
      super(args, false);
      
      this.startApplication(this.client);
      PenUpListener.getPenEventHandler().addObserver(this);

      LOGGER.info("Setup OK. Ready to go!");
      
   }

   /*
    * (non-Javadoc)
    * @see
    * org.ximtec.ipaper.client.app.DefaultClient#startApplication(org.ximtec.ipaper.client
    * .IPaperClient)
    */
   @Override
   public void startApplication(IPaperClient client) {
      this.getClient().loadDB();

      Configuration configuration = PenClient.getInstance().getProperties();

      // init input devices
      InputHandler handler = new ExtendedInputHandler();
      String inputDevices = this.getClient().getInputDevice();
      Vector<List< ? extends InputDevice>> addedPens = new Vector<List< ? extends InputDevice>>();
      for (StringTokenizer tokens = new StringTokenizer(inputDevices); tokens
            .hasMoreTokens();) {

         String inputDevice = tokens.nextToken();

         List< ? extends InputDevice> pens = PenTool.createPens(this.getClient(),
               inputDevice);
         addedPens.add(pens);
         InputDeviceEventListener listener = PenTool.createListener(this.getClient(),
               inputDevice);
         listener.addInputHandler(handler);

         this.getClient().addListenerToPens(pens, listener);
      }

      for (List< ? extends InputDevice> pens : addedPens) {
         this.getClient().addListenerToPens(pens,
               PenUpListener.createPenUpListener(client, inputDevices));
      }
      
		try {
			server = new BroadcastServer("192.168.1.2", 65432);
			server.issueStatus(StatusCode.Connected, "Pen Connected");
		} catch (IOException e) {
			e.printStackTrace();
		}
   } // startApplication

   @Override
   public void update(Observable source, Object obj) {
      // LOGGER.info("-------> PENUP");
      if (obj instanceof PenUpMessage) {
         penUp = true;
      } else if (obj instanceof CoordinateMessage) {
         penUp = false;
      }
   }

   public static boolean isPenUp() {
      return penUp;
   }

   public static boolean isPenDown() {
      return !penUp;
   }

   /**
    * @return Returns the connected.
    */
   public static boolean isConnected() {
      return connected;
   }

   /**
    * @param connected
    *           The connected to set.
    */
   public static void setConnected(boolean connected) {
	   SpacesPenClient.connected = connected;
   }

   /**
    * The main method.
    * 
    * @param args
    *           the arguments
    * @throws IOException
    */
}
