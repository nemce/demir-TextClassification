/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.nio.channels.FileChannel;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 *
 * @author nmeltem
 */
public class NewsGroupConverter {

    public static void main(String[] args) {
        try {
            //ProcessFolder("D:\\Datasets\\20NEWSGROUP\\20news-18828\\20news-18828\\");
            CopyFiles();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(NewsGroupConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ProcessFolder(
            String sFolderPath) throws Exception {

        File file = new File(sFolderPath);
        File[] folders = file.listFiles();
        String outputPath = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\terrier\\";
        int iCnt = 0;

        for (int i = 0; i < folders.length; i++) {
            File[] files = folders[i].listFiles();
            if (files != null) {
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(sFolderPath, "");
                    String sLabelTemp = sLabel;
                    String sLabel2 = sLabelTemp.replace('.', '_');
                    String newFileName = outputPath + sLabel2 + "_" + sFileName;
                    ConvertFile(files[j].getAbsolutePath(),
                            sLabel2 + "_" + sFileName,
                            sFileName,
                            iCnt++,
                            newFileName);
                }
            }
        }

    }
    
    
      public static void CopyFiles(
            ) throws Exception {

        
        String inputPath = "D:\\Datasets\\20NEWSGROUP\\20news-bydate\\20news-bydate-train\\";
        String FileDest = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\terrier\\";
        String outputPath = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\train_terrier\\";
        int iCnt = 0;
        
        File file = new File(inputPath);
        File[] folders = file.listFiles();
        for (int i = 0; i < folders.length; i++) {
            File[] files = folders[i].listFiles();
            if (files != null) {
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(inputPath, "");
                    String sLabelTemp = sLabel;
                    String sLabel2 = sLabelTemp.replace('.', '_');
                    String newFileName = outputPath + sLabel2 + "_" + sFileName;
                   //System.out.println(sLabel2 + "_" + sFileName);
                   
                   
                   try
                   {
                   //copyFileUsingFileChannels(new File(FileDest + sLabel2 + "_" + sFileName),
                   //        new File(outputPath + sLabel2 + "_" + sFileName));
                   if(new File(FileDest + sLabel2 + "_" + sFileName).exists())
                        System.out.println(sLabel2 + "_" + sFileName);
                   }
                   catch(Exception e)
                   {
                       // System.out.println("error " + sLabel2 + "_" + sFileName);
                   }
                }
            }
        }
        
      }
      
      private static void copyFileUsingFileChannels(File source, File dest)
		throws IOException {
	FileChannel inputChannel = null;
	FileChannel outputChannel = null;
	try {
		inputChannel = new FileInputStream(source).getChannel();
		outputChannel = new FileOutputStream(dest).getChannel();
		outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	} finally {
		inputChannel.close();
		outputChannel.close();
	}
}

    public static void ConvertFile(String InputFile,
            String FileName,
            String FileId,
            int QueryId,
            String outputFile) throws FileNotFoundException {
        try {

            FileReader fr = new FileReader(InputFile);
           
            BufferedReader br = new BufferedReader(fr);
            String from = br.readLine().replace("", "");
            String Subject = br.readLine().replace("Subject: ", "");
            String content = null;
            String sLine = "";
            
            while((sLine = br.readLine()) != null)
            {
                content += sLine + "\n";
            }

//            String from = content.substring("From: ".length(), content.indexOf("Subject: "));
//            content = content.substring("From: ".length() + 
//                    from.length()  + "Subject: ".length() , 
//                    content.length());
//            String subject = content.substring(0, content.indexOf("\n"));
//            content = content.substring(subject.length(), content.length());

            PrintWriter fw = new PrintWriter(new FileWriter(outputFile));
            String NewDocText = "<DOC>\n<ID>"
                    + FileName
                    + "</ID>\n<NUM>"
                    + QueryId + "</NUM>\n<TITLE>"
                    + Subject
                    + "</TITLE>\n<DESC>"
                    + content
                    + "</DESC></DOC>";
            fw.append(NewDocText);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RCV1Converter.class.getName()).error(ex.getMessage());
        }

    }

    private void WriteFileNames(
            String sFolderPath) throws Exception {
        File file = new File(sFolderPath);
        File[] folders = file.listFiles();

        for (int i = 0; i < folders.length; i++) {
            System.out.print(folders[i].getName() + "-");
            if (folders[i].getName().length() >= 20) {
                System.out.print(folders[i].getName().substring(0, 19));
            }
            System.out.println();
        }

        for (int i = 0; i < folders.length; i++) {
            File[] files = folders[i].listFiles();
            if (files != null) {
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(sFolderPath, "");
                    String sLabelTemp = sLabel;
                    String sLabel2 = sLabelTemp.replace('.', '_');
                    if (sLabel.length() >= 20) {
                        sLabel = sLabel.substring(0, 19);
                    }
                    System.out.println(sLabel2 + "_" + sFileId + "\t" + sLabel + "\t" + sLabelTemp + "\t" + sFileName);
                }
            }
        }
    }

}
