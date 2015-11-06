/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.others;

import demir.tc.classification.IRBasedTextClassification;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author nmeltem
 */
public class TopicFileCreator {
    
    public static void main(String[] args) {
        IRBasedTextClassification ibtc = new IRBasedTextClassification();
        // String sTestFolder = ibtc.getClsPrm().getTestFolderPath();
        String sTestFolder =  "D:\\Datasets\\REUTERS\\reuters_cat7\\training_terrier\\";
        if (!sTestFolder.isEmpty()) {
            ReadFiles(ibtc, sTestFolder);
        } 
    }
    
    
    public static void ReadFiles(IRBasedTextClassification ibtc, String sTestFolder) {
        System.out.println(sTestFolder);
        File file = new File(sTestFolder);
        File[] files = file.listFiles();
        int iMaximum = files.length;
        int iSepIndex = 0;
        int iProcessCount = 1;

       for (int fileInList = 0; fileInList < files.length; fileInList++) {
                if (files[fileInList] != null) {
                    try {
                        String QueryId = String.valueOf(fileInList);
                        String sQueryText = ProcessFile(files[fileInList].getPath(), 
                                files[fileInList].getName(), QueryId, ibtc);
                        System.out.println(sQueryText);
                    } catch (Exception ex) {
                        Logger.getLogger(TopicFileCreator.class.getName()).log(Level.ERROR, null, ex);
                    }
                }

               
            }
    }
    
     public static String ProcessFile(String sFilePath, 
             String sFileName, 
             String queryId,
             IRBasedTextClassification ir) throws Exception {
        try {
            String sWholeText = ReadFile(sFilePath, sFileName);
            String sQueryText = GenerateQueryFile(queryId, 
                    sFileName, sWholeText, 
                    ir.getClsPrm().getQueryTagList());
            return sQueryText;
        } catch (Exception eSys) {
            System.out.println("Could not process file " + sFileName);
            Logger.getLogger(TopicFileCreator.class.getName()).log(Level.ERROR, null, eSys);
            return null;        
        }
    }
     
     public static  String GenerateQueryFile(String queryId, 
             String sFileName,
             String sWholeText, String [] QueryTags)
    {
        String sQueryText = "<TOP>"
               // + "<NUM>" + queryId + "<NUM>";
               + "<NUM>" + sFileName + "<NUM>";
         for(int i = 0; i  < QueryTags.length; i++)
         {
             int iStartIndex = sWholeText.indexOf("<"+QueryTags[i]+">");
             int iEndIndex = sWholeText.indexOf("</"+QueryTags[i]+">");
             if(iStartIndex > 0 && iEndIndex > 0)
             {
                String sQueryTag = sWholeText.substring(iStartIndex + ("<"+QueryTags[i]+">").length(), iEndIndex);
                sQueryText +="<"+QueryTags[i]+">" + sQueryTag + "</"+QueryTags[i]+">";
             }
         }
         sQueryText += "</TOP>";
         return sQueryText;
    }
  public static  String ReadFile(String sFilePath, String sFileName) {

        String sLine = "";
        String sWholeText = "";
        try {
            FileReader fr = new FileReader(sFilePath);
            BufferedReader br = new BufferedReader(fr);

            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\r\n";
            }
            br.close();
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(TopicFileCreator.class.getName()).log(Level.ERROR, null, ex);
        }

        return sWholeText;
    }

}
