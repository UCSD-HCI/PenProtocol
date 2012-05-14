/*
 * $HeadURL		:	UbiSketchPenEventStorer.java $
 *
 * Creator		:	Nadir Weibel, weibel@ucsd.edu
 *
 * Date			:	Mar 15, 2010	
 *
 * Purpose		:  
 *
 * -----------------------------------------------------------------------
 *
 * Revision Information:
 *
 * $LastChangedDate: 2010-07-13 11:10:57 -0700 (Tue, 13 Jul 2010) $
 *	$Revision: 260 $
 * $Author: weibel $
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

import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.sigtec.input.AbstractInputDeviceEventListener;
import org.sigtec.input.InputDevice;
import org.sigtec.input.InputDeviceEvent;
import org.sigtec.input.InputDeviceEventListener;
import org.ximtec.ipaper.anoto.r41.message.AnotoMessage;
import org.ximtec.ipaper.anoto.r41.message.CoordinateMessage;
import org.ximtec.ipaper.anoto.r41.message.PenUpMessage;
import org.ximtec.ipaper.client.IPaperClient;
import org.ximtec.ipaper.io.MagicommPenEvent;
import org.ximtec.ipaper.transformer.AnotoTransformer;

/**
 * Comment
 * 
 * @version 1.0 Mar 15, 2010
 * @author Nadir Weibel, weibel@ucsd.edu
 */
public class PenUpListener extends AbstractInputDeviceEventListener {

   /** Default Class logger */
   private static final Logger LOGGER = Logger.getLogger(PenUpListener.class.getName());

   private AnotoTransformer transformer;
   private static boolean connecting = false;

   private static PenEventHandler penEventHandler;

   /**
    * Constructs a new MagicommPenEventListener.
    * 
    * @param transformer
    *           the Anoto transformer used to decode the Anoto coordinates.
    */
   public PenUpListener(AnotoTransformer transformer) {
      this.transformer = transformer;
      penEventHandler = new PenEventHandler();
   } // MagicommPenEventListener

   public static PenEventHandler getPenEventHandler() {
      return penEventHandler;
   }

   /**
    * Handles the MagicommPenEvent fired by the Magicomm G303 pen.
    * 
    * @param inputDevice
    *           the input device firing the MagicommPenEvent.
    * @param event
    *           the MagicommPenEvent fired by the Magicomm G303 pen.
    */
   public synchronized void inputDeviceEvent(InputDevice inputDevice, InputDeviceEvent event) {
      if (event instanceof MagicommPenEvent) {
         MagicommPenEvent penEvent = (MagicommPenEvent) event;

         switch (penEvent.getEventType()) {
            case MagicommPenEvent.NEW_MESSAGE:

               switch (penEvent.getMessage().getType()) {
                  case AnotoMessage.TYPE_PEN_UP:
                     PenUpMessage penUpMessage = (PenUpMessage) penEvent.getMessage();
                     penEventHandler.firePenEvent(penUpMessage);
                     break;
                  case AnotoMessage.TYPE_COORDINATE:
                     CoordinateMessage penDownMessage = (CoordinateMessage) penEvent
                           .getMessage();
                     penEventHandler.firePenEvent(penDownMessage);
                     break;
               }

               break;
         }
      }

   } // inputDeviceEvent

   public class PenEventHandler extends Observable {

      public void firePenEvent(AnotoMessage penMsg) {
         if (!SpacesPenClient.isConnected() && !connecting) {
           

            connecting = true;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

               @Override
               public void run() {
            	   SpacesPenClient.setConnected(true);
                  LOGGER.info("Connected, releasing stubs...");
               }
            }, 1000);

         }

         this.setChanged();
         this.notifyObservers(penMsg);
      }
   }

   public static InputDeviceEventListener createPenUpListener(IPaperClient client,
         String inputDevices) {

      String inputDevice = new StringTokenizer(inputDevices).nextToken();

      if (inputDevice.equals(IPaperClient.MAGICOMM_G303_SOCKET_PEN)) {
         return new PenUpListener(new AnotoTransformer(client.getAnotoMappings()));
      } else if (inputDevice.equals(IPaperClient.MAGICOMM_G303_PEN)) {
         return new PenUpListener(new AnotoTransformer(client.getAnotoMappings()));
      } else if (inputDevice.equals(IPaperClient.MAGICOMM_G303_BLUETOOTH_PEN)) {
         return new PenUpListener(new AnotoTransformer(client.getAnotoMappings()));
      } else {
         LOGGER.info("Device is not supported");
         return null;
      }

   } // createLogger

}
