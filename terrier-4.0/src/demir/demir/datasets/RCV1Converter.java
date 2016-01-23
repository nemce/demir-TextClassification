/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import static org.junit.Assert.assertTrue;
import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import org.terrier.utility.Files;

/**
 *
 * @author nmeltem
 */
public class RCV1Converter {
    public static void main(String [] args)
    {
        try {
//          ReadFile2("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_test_pt0_p2.dat",
//                     "D:\\Datasets\\RCV1\\lyrl2004_tokens\\terrier_test_pt0_p2.dat");
//          ReadFile2("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_test_pt1_p2.dat",
//                     "D:\\Datasets\\RCV1\\lyrl2004_tokens\\terrier_test_pt1_p2.dat");
//          ReadFile2("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_test_pt2_p2.dat",
//                    "D:\\Datasets\\RCV1\\lyrl2004_tokens\\terrier_test_pt2_p2.dat");
//          ReadFile2("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_test_pt3_p2.dat",
//                   "D:\\Datasets\\RCV1\\lyrl2004_tokens\\terrier_test_pt3_p2.dat");
              ReadFile2("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_train.dat",
                      "D:\\Datasets\\RCV1\\lyrl2004_tokens\\terrier_train.dat");
          
            //terrierDocExtractor("D:\\Datasets\\RCV1\\lyrl2004_tokens\\lyrl2004_tokens_test_pt0.dat");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RCV1Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void ReadFile2(String InputFile, String outputFile) throws FileNotFoundException 
    {
        try {
            
              Scanner scan = new Scanner(new File(InputFile));  
        scan.useDelimiter(".I"); 
        String content = scan.next();
          String currentDocNo = null;
            String NewDocText = "";
            int DocCnt = 0;
            
            PrintWriter fw = new PrintWriter(new FileWriter(outputFile));
        while(content != null && !content.equals(""))
        {
                    currentDocNo = content.substring(1, content.indexOf(".W"));
                    DocCnt++;
                    if(DocCnt % 5000 == 0)
                    {   
                        fw.append(NewDocText);
                        fw.flush();
                        System.out.println(DocCnt);
                        NewDocText = "";
                    }    
                    NewDocText += "<DOC><ID>" +
                            currentDocNo +  
                            "</ID>" +
                            "<DESC>" + 
                            content.substring(content.indexOf(".W") + 2 )+ 
                            "</DESC></DOC>";
//                 NewDocText += "<TOP><NUM>" +
//                            currentDocNo +  
//                            "<NUM>" +
//                            "<DESC>" + 
//                            content.substring(content.indexOf(".W") + 2 )+ 
//                            "</DESC></TOP>";
                 if(scan.hasNext())
                    content = scan.next();
                 else
                     break;
        }
         fw.append(NewDocText);
         fw.flush();
         System.out.println(DocCnt);
         NewDocText = "";
         fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RCV1Converter.class.getName()).error(ex.getMessage());
        }

       
    }
    
  
    /// Çok Yavaş Çalıştığı için bıraktım.
    public static void ReadFile(String InputFile, String outputFile) {
        String sLine = "";
        try {
            FileReader fr = new FileReader(InputFile);
            
            BufferedReader br = new BufferedReader(fr);
            String currentDocNo = null;
            String NewDocText = "";
            int DocCnt = 0;
            
            PrintWriter fw = new PrintWriter(new FileWriter(outputFile));
           
            while ((sLine = br.readLine()) != null) {
                if(sLine.startsWith(".I"))
                {
                    currentDocNo = sLine.replace(".I ", "");
                    DocCnt++;
                    if(DocCnt % 10000 == 0)
                    {   
                        fw.append(NewDocText);
                        fw.flush();
                        System.out.println(DocCnt);
                        NewDocText = "";
                    }    
                    br.readLine();
                    NewDocText += "<DOC><ID>" + currentDocNo +  "</ID>";
                    
                }
                else if(sLine.equals("") || sLine.equals(null))
                {
                    NewDocText += "</DOC>";
                }
                else
                    NewDocText += sLine + "\r\n";
            }
            br.close();
            fr.close();
            fw.append(sLine);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RCV1Converter.class.getName()).error(ex.getMessage());
        }

       
    }
}
