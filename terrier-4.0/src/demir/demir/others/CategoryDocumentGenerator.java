/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.others;

import java.sql.Connection;
import java.util.ArrayList;
import org.hibernate.annotations.SourceType;
import demir.dbconnection.ConnectToServer;
import demir.dbconnection.DBFunctions;
import demir.tc.classification.IRBasedTextClassification;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 *
 * @author nmeltem
 * 
 * Bu sınıf ir sistemini categıri tabanlı hale getirmek için hazırlanmıştır.
 * Bir sınıfa ait tüm dokumanların metinleri birleştirilerek o sınıf için tek bir 
 * dokuman oluşturulur. IR sistemi için indeksleme bunlar üzerinden yapılır.
 */
public class CategoryDocumentGenerator {
    
    public static void main(String[] args) {
        int collection_id = 18;
        Session session = session = demir.tc.irbased.hibernate.connection.ConnectToServer.Connect();
        GenerateDoc(collection_id, session);
        
    }
    
    private static void GenerateDoc(int collection_id, Session session) {
        String outputPath = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\category_train_terrier\\";
        String inputPath = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\train_terrier\\";
        ArrayList labels
            = DBFunctions.SelectCollectionTrainLabels(collection_id, session);
       IRBasedTextClassification ir = new IRBasedTextClassification();
       
        for (Object label1 : labels) {
            String label = label1.toString();
            
            PrintWriter pwm = null;
            String sQueryText = "";
            try {
                
                pwm = new PrintWriter(outputPath + label);
                 sQueryText = "<DOC>"
               + "<ID>" + label + "</ID><DESC>";
                pwm.println(sQueryText);
                Connection con = demir.dbconnection.ConnectToServer.Connect();
                ResultSet rs
                = demir.dbconnection.CategoryDocumentGeneratorDB.SelRecDocsOfLabel(collection_id,  "",label,
                    con);
                int j = 0;
                while(rs.next()){
                    j++;
                    String docName = rs.getString(1); // file_id
                    String docText = TopicFileCreator.ReadFile(inputPath + docName, docName);
                    String docDesc = ExtractTag(docText, ir.getClsPrm().getQueryTagList());
                    pwm.println(docDesc);
                    if (j % 100 == 0) {
                        pwm.flush();
                    }
                }
                System.out.println(label1);
                System.out.println(j);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CategoryDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SQLException ex2) {
                Logger.getLogger(CategoryDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex2);
            }
            sQueryText = "</DESC></DOC>";
            pwm.println(sQueryText);
            pwm.close();
        }
    }
    
    
     private static  String ExtractTag(
             String sWholeText, String [] QueryTags)
    {
         for(int i = 0; i  < QueryTags.length; i++)
         {
             int iStartIndex = sWholeText.indexOf("<"+QueryTags[i]+">");
             int iEndIndex = sWholeText.indexOf("</"+QueryTags[i]+">");
             if(iStartIndex > 0 && iEndIndex > 0)
             {
                String sQueryTag = sWholeText.substring(iStartIndex + ("<"+QueryTags[i]+">").length(), iEndIndex);
                if(QueryTags[i].equals("DESC"))
                    return sQueryTag;
             }
         }
         return "";
    }
}
