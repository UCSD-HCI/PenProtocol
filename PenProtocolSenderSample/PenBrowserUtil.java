/*
 * @(#)PenBrowserUtil.java	1.0   Oct 18, 2010
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
 * Copyright 2009-2010 Nadir Weibel. All Rights Reserved.
 *
 * This software is the proprietary information of Nadir Weibel.
 * Use is subject to license terms.
 * 
 */

package edu.ucsd.hci.chronoviz.penbrowser;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;

import org.apache.commons.httpclient.util.URIUtil;
import org.ximtec.ipaper.client.PenClient;

/**
 *
 * @author  nad
 * @version 1.0
 * @since   DataPrismBrowser
 */

/**
 * Comment
 * 
 * @version 1.0 Oct 18, 2010
 * @author Nadir Weibel, weibel@ucsd.edu
 */
public class PenBrowserUtil {

   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(PenBrowserUtil.class.getName());

   public static String LINKBASE = PenClient.getInstance().getProperties()
         .getString(Constants.PROPERTY_LINKBASE);

   public static void openChronoVizLink(String chronovizLink) {

      LOGGER.info("----------> " + chronovizLink);

      if (areLinksEnabled()) {
         URI uri;
         try {
            chronovizLink = URIUtil.encodeQuery(chronovizLink);
            uri = new URI(chronovizLink);
            Desktop.getDesktop().browse(uri);

         } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   public static void sendErrorMessage(String msg) {
      String link = LINKBASE + "penbrowser?status=error&message=" + msg;
      openChronoVizLink(link);
   }

   public static boolean areLinksEnabled() {
      return PenClient.getInstance().getProperties()
            .getBoolean(Constants.PROPERTY_LINKS_ENABLED);
   }

   public static String getChronoVizNoteLinkUrl(String pageNr, double x, double y) {
      return LINKBASE + "note?page=" + pageNr + "&x=" + x + "&y=" + y;
   }

   public static String getChronoVizStartPenBrowser() {
      return LINKBASE + "penbrowser?status=starting";
   }

   public static String getChronoVizPenBrowserReady() {
      return LINKBASE + "penbrowser?status=ready";
   }

   public static String getChronoVizPenBrowserConnected() {
      return LINKBASE + "penbrowser?status=connected";
   }

   public static String getDataPrismSkipFramesLink(int nofFrames) {
      String frames = "";
      if (nofFrames > 0) {
         frames = "+";
      } else {
         frames = "-";
      }

      frames = frames + Math.abs(nofFrames);
      return LINKBASE + "time?frame=" + frames;
   }

   public static String getDataPrismSkipSecondsLink(double seconds) {
      return LINKBASE + "time?seconds=" + seconds;
   }

   public static String getDataPrismPlayRateLink(double rate) {
      return LINKBASE + "time?rate=" + rate;
   }

   public static String getDataPrismStopLink() {
      return LINKBASE + "time?rate=stop";
   }

   public static String getDataPrismPlayLink() {
      return LINKBASE + "time?rate=play";
   }

   public static String getDataPrismFFLink() {
      return LINKBASE + "time?rate=ff";
   }

   public static String getDataPrismRWLink() {
      return LINKBASE + "time?rate=rw";
   }

   public static String getChronoVizStartPageUrl(String pageNr) {
      return LINKBASE + "start_page?page=" + pageNr;

   }

   public static String getChronoVizStartAnnotationUrl(String pageNr, double x, double y) {
      return LINKBASE + "start_annotation?page=" + pageNr + "&x=" + x + "&y=" + y;

   }

   public static String getChronoVizEndAnnotationUrl(String xml) {

      xml = xml.replace("&", "%26");
      return LINKBASE + "end_annotation?file=" + xml;

   }

}
