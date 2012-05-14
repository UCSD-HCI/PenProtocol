/*
 * @(#)ChronoVizApp.java	1.0   Jun 16, 2011
 *
 * Author		:	Nadir Weibel, weibel@ucsd.edu
 *
 * Purpose		: 
 *
 * -----------------------------------------------------------------------
 *
 * Revision Information:
 *
 * Date				Who			Reason
 *
 * 					weibel		Initial Release
 *
 * -----------------------------------------------------------------------
 *
 * Copyright 2009-2011 Nadir Weibel. All Rights Reserved.
 *
 * This software is the proprietary information of Nadir Weibel.
 * Use is subject to license terms.
 * 
 */

package edu.ucsd.hci.spaces.pen;

import java.io.IOException;

import com.apple.eawt.Application;

/**
 *
 * @author  nad
 * @version 1.0
 * @since   ChronoVizBrowser
 */

/**
 * Comment
 * 
 * @version 1.0 Jun 16, 2011
 * @author Nadir Weibel, weibel@ucsd.edu
 */
public class SpacesPenClientApp extends Application {

   /**
    * @param args
    */
   public SpacesPenClientApp(String[] args) {
      try {
         SpacesPenClient spacesPenClient = new SpacesPenClient(args);
         

      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void main(String args[]) throws IOException {
      if (args.length == 0) {
         args = new String[1];
         args[0] = "spaces.properties";
      }

      new SpacesPenClientApp(args);

   }

}
