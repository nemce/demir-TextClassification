/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets.Generator;

import demir.others.TopicFileCreator;
import demir.tc.classification.IRBasedTextClassification;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nmeltem
 * Bu sınıf jforestlearning yöntemine uygulamak için train seti 
 * 3 parçaya ayırmak için oluşturulmuştur.
 */
public class LearningSetGenerator {
    public static void main(String[] args)
    {
        try {
           Run2(18,"TRAIN", 
               "D:\\Datasets\\20NEWSGROUP\\20news-18828\\train_terrier\\", 
               "D:\\Datasets\\20NEWSGROUP\\20news-18828\\cross_fold\\train_terrier\\");
//            Run2(18,"TEST", 
//                "D:\\Datasets\\20NEWSGROUP\\20news-18828\\test_terrier\\", 
//                "D:\\Datasets\\20NEWSGROUP\\20news-18828\\cross_fold\\test_terrier\\");
        } catch (IOException ex) {
            Logger.getLogger(LearningSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LearningSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /// LearningSet Generate Eder.
    private static void Run1() throws IOException
    {
        int iCollectionId = 14;
        int iSetId=1;
        String sFlag = "TEST";
        String sFilePath = "D:\\Datasets\\REUTERS\\reuters_cat7\\test_terrier\\";
        String sOutputFile1 = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\1.txt";
        String sOutputFile2 = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\2.txt";
        String sOutputFile3 = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\3.txt";
        String sOutputFile4 = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\4.txt";
        String sOutputFile5 = "D:\\Datasets\\REUTERS\\reuters_cat7\\RANKING5\\5.txt";
        
        PrintWriter [] fw = new PrintWriter[5];
        
        fw[0] = new PrintWriter(new FileWriter(sOutputFile1));
        fw[1] = new PrintWriter(new FileWriter(sOutputFile2));
        fw[2] = new PrintWriter(new FileWriter(sOutputFile3));
        fw[3] = new PrintWriter(new FileWriter(sOutputFile4));
        fw[4] = new PrintWriter(new FileWriter(sOutputFile5));
        
        IRBasedTextClassification ibtc = new IRBasedTextClassification(); 
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        
        try {
            java.sql.ResultSet lsa = SelRecLabelList(14, con);
            List<String> ProcessedFileList= new ArrayList<String>();
           
            while(lsa.next())
            {
               
               java.sql.ResultSet lsaFile = 
                       SelRecFileList(iCollectionId, sFlag, lsa.getString("LABEL"), con);
               int iQueryId = 1;
               int iIndex = 0;
               while(lsaFile.next())
            {
               String sFileId = lsaFile.getString("FILE_ID");
               if(!ProcessedFileList.contains(sFileId))
               {
                       String sQueryText = 
                               TopicFileCreator.ReadFile(sFilePath + sFileId, sFileId);
                       LearningSetGenerator.InsertRankingSetInfo(iCollectionId, iSetId, sFileId, iIndex % 5, sFlag, con);
                       fw[iIndex % 5].println(sQueryText);
                  
                   iIndex++;
                   ProcessedFileList.add(sFileId);
               }
                   }
               fw[0].flush();fw[1].flush();fw[2].flush();
               fw[3].flush();fw[4].flush();
            }
            fw[0].close();fw[1].close();fw[2].close();
            fw[3].close();fw[4].close();
        } catch (SQLException ex) {
            Logger.getLogger(LearningSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LearningSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private static void Run2(int iCollectionId, String sFlag, 
        String SourcePath,
        String DestPath
     ) throws IOException, SQLException
    {
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        ResultSet LabelList = SelRecLabelList(18, con);
         CopyOption[] options = new CopyOption[]{
      StandardCopyOption.REPLACE_EXISTING
    }; 
            while(LabelList.next())
            {
              String sLabel = LabelList.getString("LABEL");
              /// train
              ResultSet fileList = SelRecFileList(iCollectionId, sFlag, sLabel, con);
              int iCount = SelRecFileCount(iCollectionId,  sFlag, sLabel, con);
              int iMod = iCount / 10;
              for(int i = 0; i < 10; i++)
              {
                  int iSetId = i+2;
                //  InsertSet(iCollectionId, iSetId, con);
                for(int k = 0; k < iMod; k++)
                {
                    fileList.next();
                    System.out.println(iSetId + "-" + k + "-" + fileList.getString("FILE_ID"));
                    InsertTTDocs(iCollectionId, iSetId, fileList.getString("FILE_ID"), sFlag, con);
                    File source = new File(SourcePath + fileList.getString("FILE_ID"));
                    File dest = new File(DestPath + iSetId + "//"+fileList.getString("FILE_ID"));

                    /// Burda kaldım dosya var hata veriyor. Test ve train doğru oluşması.
                    java.nio.file.Files.copy(source.toPath(), dest.toPath(), options);
                    
                }
              }
            }
        
    }
    private static ResultSet SelRecFileList(int iCollectionId, String sFlag, String sLabel,
            Connection con) throws SQLException
    {
            String sSql = "select LABEL, tt_docs.FILE_ID as FILE_ID from tt_docs, doc_labels\n" +
       "where tt_docs.collection_id= " + iCollectionId +
       " and tt_docs.FLAG = '" + sFlag + "'" +
       " AND tt_docs.COLLECTION_ID = doc_labels.COLLECTION_ID\n" +
       " AND tt_docs.FILE_ID = doc_labels.FILE_ID" + 
       " AND doc_labels.LABEL = '" + sLabel + "'";
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery(sSql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private static int SelRecFileCount(int iCollectionId, String sFlag, String sLabel,
            Connection con) throws SQLException
    {
            String sSql = "select count(*) as cnt from tt_docs, doc_labels\n" +
       "where tt_docs.collection_id= " + iCollectionId +
       " and tt_docs.FLAG = '" + sFlag + "'" +
       " AND tt_docs.COLLECTION_ID = doc_labels.COLLECTION_ID\n" +
       " AND tt_docs.FILE_ID = doc_labels.FILE_ID" + 
       " AND doc_labels.LABEL = '" + sLabel + "'";
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery(sSql);
            rs.first();
            return rs.getInt("cnt");
            
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private static java.sql.ResultSet SelRecLabelList(int iCollectionId,
             Connection con) throws SQLException
    { 
           String sSql = "select label FROM collection_labels\n" +
"where collection_id = " + iCollectionId;
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery(sSql);
            
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
     private static boolean InsertRankingSetInfo(int iCollectionId,
             int iSetId,
             String sFileId, int iRankingSet,
             String sFlag,
             Connection con) throws SQLException
    { 
           String sSql = "INSERT INTO demir_tc.tt_ranking_set(\n" +
                        "   COLLECTION_ID\n" +
                        "  ,SET_ID\n" +
                        "  ,FILE_ID\n" +
                        "  ,FLAG\n" +
                        "  ,RANKING_SET_ID\n" +
                        ") VALUES (\n" +
                        iCollectionId +
                        ", " +   iSetId +
                        ",'" + sFileId + "'\n"  +
                        ",'" + sFlag + "'\n" +
                        "  ," + iRankingSet + "\n" +
                        ")";
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            boolean bRet = st.execute(sSql);
            return bRet;
            
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
     
      private static boolean InsertSet(int iCollectionId,
             int iSetId,
             Connection con) throws SQLException
    { 
          
            
        Statement st; 
        try {
             String sSql = "INSERT INTO demir_tc.train_test_set(\n" +
                            "   SET_ID\n" +
                            "  ,COLLECTION_ID\n" +
                            "  ,TEST_FILE_CNT\n" +
                            "  ,TRAIN_FILE_CNT\n" +
                            ") VALUES (\n" +
                        iSetId +
                        ", " +   iCollectionId +
                        ",'" + 0 + "'\n" +
                        "  ," + 0 + "\n" +
                        ")";
            st = (Statement) con.createStatement();
            boolean bRet;
            // boolean bRet = st.execute(sSql);
            
            
            sSql = "INSERT INTO demir_tc.train_prm(\n" +
                        "   TRAIN_ID\n" +
                        "  ,SET_ID\n" +
                        "  ,COLLECTION_ID\n" +
                        ") VALUES (\n" +
                        1 +
                        ", " +  iSetId +
                        ",'" + iCollectionId + "'\n" +
                        ")";
            bRet = st.execute(sSql);
            
            sSql = "INSERT INTO demir_tc.test_prm\n" +
"(TEST_ID, TRAIN_ID, SET_ID, COLLECTION_ID \n" +
                        ") VALUES (\n" +
                        1 +
                        ", " + 1  +
                        ", " + iSetId +
                        ",'" + iCollectionId + "'\n" +
                        ")";
            bRet = st.execute(sSql);
           
            return bRet;
            
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
      
       private static boolean InsertTTDocs(int iCollectionId,
             int iSetId,
             String sFileId, 
             String sFlag,
             Connection con) throws SQLException
    { 
           String sSql = "INSERT INTO demir_tc.tt_docs\n" +
                         "(FLAG, SET_ID, COLLECTION_ID, FILE_ID)  VALUES (\n" +
                        "'" + sFlag + "'" +
                        ", " +   iSetId +
                        ",'" + iCollectionId + "'\n" +
                        ",'" + sFileId + "'\n"  +
                        ")";
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            boolean bRet = st.execute(sSql);
            return bRet;
            
        } catch (SQLException ex) {
            //Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
return false;            
//throw ex;
        }
    }
}
