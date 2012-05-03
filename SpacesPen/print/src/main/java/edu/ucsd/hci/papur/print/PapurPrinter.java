/*
 * $HeadURL		:	PapurPrinter.java $$
 *
 * Creator		:	Nadir Weibel, weibel@ucsd.edu
 *
 * Date			:	May 18, 2010	
 *
 * Purpose		:  
 *
 * -----------------------------------------------------------------------
 *
 * Revision Information:
 *
 * $LastChangedDate: 2010-11-24 16:05:34 -0800 (Wed, 24 Nov 2010) $$
 *	$Revision: 7 $$
 * $Author: ebayan $$
 *
 * -----------------------------------------------------------------------
 *
 * Copyright 2009-2010 Nadir Weibel. All Rights Reserved.
 *
 * This software is the proprietary information of Nadir Weibel.
 * Use is subject to license terms.
 * 
 */

package edu.ucsd.hci.papur.print;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import ch.ethz.globis.idoc.anoto.AnotoUtil.COLOR_CORRECTION;
import ch.ethz.globis.idoc.export.iserver.mappings.DocumentMappings;
import ch.ethz.globis.idoc.print.anoto.DocumentPrinter;
import ch.ethz.globis.idoc.print.anoto.license.AnotoPatternMapping;

/**
 * Comment
 * 
 * @version 1.0 May 18, 2010
 * @author Nadir Weibel, weibel@ucsd.edu
 */
public class PapurPrinter {

   /** Default Class logger */
   private static final Logger LOGGER = Logger.getLogger(PapurPrinter.class.getName());

   private static void generateRemoteControl() {
      String virtualPrinterXML = "print/src/main/data/anotoStreamingLicense.xml";

      String docID = "papur_document";
      int version = 1;
      String sourcePDF = "print/src/main/data/Papur.pdf";
      String mappingsFile = "print/src/main/data/Papur_mappings.xml";

      boolean verbose = true;
      boolean duplex = false;
      boolean printLogo = false;
      boolean printAnotoSpace = false;
      String infoSize = "2pt";
      COLOR_CORRECTION colorCorrection = COLOR_CORRECTION.K2CMY;
      double dotrad = 0.02;
      int dpi = 600;

      try {
         URI outputPS = new File("print/src/main/data/Papur.ps").toURI();

         DocumentPrinter printer = new DocumentPrinter(virtualPrinterXML);

         List<AnotoPatternMapping> mappings = printer.printDocument(docID, version,
               sourcePDF, outputPS, verbose, duplex, printLogo, printAnotoSpace, infoSize,
               colorCorrection, dotrad, dpi);

         DocumentMappings papurMapping = new DocumentMappings(mappings, docID);
         papurMapping.storeDocumentMappings(mappingsFile);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static void main(String[] args) {
      generateRemoteControl();
   }

}
