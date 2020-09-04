/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.svg_to_pdf;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;        
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import org.apache.pdfbox.pdmodel.PDDocument; 
import org.apache.pdfbox.pdmodel.PDPage; 
import org.apache.pdfbox.pdmodel.PDPageContentStream; 
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;  

/**
 *
 * @author rob
 */
public class NewClass {
    
    public static void main(String[] args)
    {
       
        String[] source_filenames = {   "files/bzr",
                                        "files/circles1",
                                        //"files/car",
        };
        
        svg_to_pdf(source_filenames);
        text_to_pdf(source_filenames);        
        merge_pdf(source_filenames);
        
    }
    
    public static void svg_to_pdf(String[] source_filenames)
    {
        try
        {
            // instantiate
            Transcoder transcoder = new PDFTranscoder();

            // set the scale for the SVG
            transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, (float)1122.52);
            transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH, (float)793.70);
            
            for (String source_filename : source_filenames)
            {
                // identify the source SVG file
                TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(new File(source_filename + ".svg")));
                // identify the target PDF file
                TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(new File(source_filename + "_PDF_TMP.pdf")));
                // Convert
                transcoder.transcode(transcoderInput, transcoderOutput);            
            }
        }
        catch (FileNotFoundException fnf)
        {
            
        }
        catch (TranscoderException te)
        {
            
        }
    }



    public static void text_to_pdf(String[] pdf_filenames)
    {

        try {
            
            for(String pdf_filename : pdf_filenames)
            {
                String full_file_name = pdf_filename + "_PDF_TMP.pdf";
                
                //Loading an existing document 
                PDDocument doc = PDDocument.load(new File(full_file_name)); 
                System.out.println(pdf_filename + "_PDF_TMP.pdf");

                //Creating a PDF Document      
                PDPage page = doc.getPage(0);
                page.setMediaBox(PDRectangle.A4);
                PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, false);


                //Begin the Content stream 
                contentStream.beginText(); 

                //Setting the font to the Content stream  
                contentStream.setFont( PDType1Font.TIMES_ROMAN, 16 ); 

//                System.out.println(page.getMediaBox().getHeight());
//                System.out.println(page.getMediaBox().getWidth());

                // set text position
                contentStream.newLineAtOffset(25, page.getMediaBox().getHeight());


                // Write title
                contentStream.newLineAtOffset(0, -25 );
                contentStream.showText(pdf_filename); 


                //Setting the position for the line 
                contentStream.newLineAtOffset(0, -400); 
                String text = "This is an example of adding text to a page in the pdf document. we can add as many lines as we want like this using the draw string method of the ContentStream class"; 

                //Adding text in the form of string 
                contentStream.showText(text); 

                //Ending the content stream 
                contentStream.endText(); 
                System.out.println("Content added");       


                //Closing the content stream 
                contentStream.close();      

                //Saving the document  
                doc.save(new File(full_file_name)); 

                //Closing the document  
                doc.close();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void merge_pdf(String[] pdf_filenames)
    {   
        try {
            PDFMergerUtility merge = new PDFMergerUtility();

            for (String pdf_filename : pdf_filenames)
            {
                merge.addSource(new File(pdf_filename + "_PDF_TMP.pdf"));
            }

            merge.setDestinationFileName("Final_Output.pdf");        
            merge.mergeDocuments();

            //MemoryUsageSetting memsetting = new MemoryUsageSetting();
            //ut.mergeDocuments(memsetting);
        
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }            

    }
}
