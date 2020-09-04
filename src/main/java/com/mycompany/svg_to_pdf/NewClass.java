/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.svg_to_pdf;

import java.io.File;
import java.io.FileFilter;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.fop.svg.PDFTranscoder;        
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import org.apache.pdfbox.pdmodel.PDDocument; 
import org.apache.pdfbox.pdmodel.PDPage; 
import org.apache.pdfbox.pdmodel.PDPageContentStream; 
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;  
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

/**
 *
 * @author rob
 */
public class NewClass {
    
    public static void main(String[] args)
    {
        String path = "files/";

        pdf_first_page("first_page.pdf"); 
        String[] source_filenames = fetch_all_files(path);
        svg_to_pdf(source_filenames);
        text_to_pdf(source_filenames);        
        merge_pdf(source_filenames);    
    }
    
    public static String[] fetch_all_files(String folder_path)
    {
        File folder = new File(folder_path);
        FileFilter fileFilter = new WildcardFileFilter("*.SVG", IOCase.INSENSITIVE);
        File[] listOfFiles = folder.listFiles(fileFilter);
        String[] file_name_list = new String[listOfFiles.length];
            
        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                String tmp = listOfFiles[i].getName();
                tmp = FilenameUtils.removeExtension(tmp);
                file_name_list[i] = folder_path + tmp;
            }
        }       
        return file_name_list;
    }    
    
    public static void pdf_first_page(String filename)
    {
        try
        {
            //Loading an existing document 
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page); 
            page.setMediaBox(PDRectangle.A4);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false);

            //Begin the Content stream 
            contentStream.beginText(); 
            contentStream.setFont( PDType1Font.TIMES_ROMAN, 16 ); 
            contentStream.newLineAtOffset(25, page.getMediaBox().getHeight());
            contentStream.newLineAtOffset(0, -25 );
            contentStream.showText("TITLE"); 
            contentStream.endText(); 

            //Closing
            contentStream.close();      
            doc.save(new File(filename)); 
            doc.close();
            
        }
        catch (IOException ex)
        {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public static void pdf_table_of_contens(String source_filename)
    {   
        try
        {        
            // there are other types of destinations, choose what is appropriate
            PDPageXYZDestination dest = new PDPageXYZDestination();
            // the indexing is odd here.  if you are doing this on the first page of the pdf
            // that page is -1, the next is 0, the next is 1 and so on.  odd.
            dest.setPageNumber(3);
            dest.setLeft(0);
            dest.setTop(0); // link to top of page, this is the XYZ part

            PDActionGoTo action = new PDActionGoTo();
            action.setDestination(dest);

            PDAnnotationLink link = new PDAnnotationLink();
            link.setAction(action);
            link.setDestination(dest);

            PDRectangle rect = new PDRectangle();
            // just making these x,y coords up for sample
            rect.setLowerLeftX(72);
            rect.setLowerLeftY(600);
            rect.setUpperRightX(144);
            rect.setUpperRightY(620);

            PDDocument doc = PDDocument.load(new File(source_filename)); 
            PDPage page = doc.getPage(0);// however you are getting your table of contents page, eg new PDPage() or doc.getDocumentCatalog().getAllPages().get(0)

            page.getAnnotations().add(link);

            PDPageContentStream stream = new PDPageContentStream(doc, page, true, true);
            stream.beginText();
            stream.setTextTranslation(85, 600); // made these up, have to test to see if padding is correct
            stream.drawString("Page 1");
            stream.endText();
            stream.close();
            
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }        
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
            //transcoder.addTranscodingHint(PDFTranscoder.KEY_USER_STYLESHEET_URI, (float)1122);
            
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
        try
        {
            PDFMergerUtility merge = new PDFMergerUtility();

            merge.addSource("first_page.pdf");    
            for (String pdf_filename : pdf_filenames)
            {
                merge.addSource(new File(pdf_filename + "_PDF_TMP.pdf"));
            }

            merge.setDestinationFileName("Final_Output.pdf");        
            merge.mergeDocuments();

            //MemoryUsageSetting memsetting = new MemoryUsageSetting();
            //ut.mergeDocuments(memsetting);
        
        }
        catch (IOException ex)
        {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }
}
