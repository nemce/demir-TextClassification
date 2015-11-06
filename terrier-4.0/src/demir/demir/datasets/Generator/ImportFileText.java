/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.datasets.Generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
 

/**
 *
 * @author nmeltem
 * Bu sınıf collection verilerinin rapid minerda 
 * kullanılabilmesi için 
 * text verisini veri tabanına atar.
 * 
 */
public class ImportFileText {
    
    public static void main(String args[])
    {
        try {
            ScanFiles(1, "D:\\Datasets\\REUTERS\\reuters_cat90_modified\\test_terrier");
           // ScanFiles(1, "D:\\Datasets\\REUTERS\\reuters_cat90_modified\\training_terrier");
        } catch (Exception ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void ScanFiles(
            int ipCollectionId,
            String sFolderPath) throws Exception {

        
        File file = new File(sFolderPath);
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        try {
            File[] files = file.listFiles();
            if(files != null)
            {
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    ReadFile(con, files[j].getAbsolutePath(), sFileId);
                    }
        }
        } catch (Exception e) {
            Logger.getLogger(demir.datasets.Generator.CollectionSetGenerator.class.getName()).log(Level.SEVERE, null, e);
        }
        finally
        {
            con.close();
        }
    }
    
    public static void ReadFile(Connection con, String sFile, String sFileId) {
        String sLine = "";
        String sWholeText = "";
        try {
            FileReader fr = new FileReader(sFile);
            BufferedReader br = new BufferedReader(fr);

            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\r\n";
            }
            br.close();
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }

        sWholeText = sWholeText.replace("'", "");
        String sTitle = FindRegex("<TITLE>","</TITLE>", sWholeText);
        String sBody = FindRegex("<BODY>","</BODY>", sWholeText);
        
        InsertFileData(con, 1, sFileId, sTitle, sBody);
    }
    
 
    public static String FindRegex(String REGEX1, String REGEX2, String INPUT) {
        int iStartIndex = -1;
        int iEndIndex = -1;
       {
       Pattern p = Pattern.compile(REGEX1);
       //  get a matcher object
       Matcher m = p.matcher(INPUT);
       if(m.find()) {
             iStartIndex = m.start();
       }
       }
       {
       Pattern p = Pattern.compile(REGEX2);
       //  get a matcher object
       Matcher m = p.matcher(INPUT);
       if(m.find()) {
             iEndIndex = m.start();
       }
       }
      return INPUT.substring(iStartIndex + REGEX1.length(), iEndIndex);
      
    }

    public static  void InsertFileData(Connection con, int pColId,
            String FileId, String title, String body)
    {
        String sSql = "INSERT INTO demir_tc.parent_col_docs(" +
                "file_id,`text`, pcol_id"  +
                ") VALUES (" + "'" +
                FileId + "','" +  title +  " " + body
                +  "'," + pColId  + ")";
        
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            st.executeUpdate(sSql);
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
