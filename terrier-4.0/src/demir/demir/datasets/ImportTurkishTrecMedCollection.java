/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets;

import demir.datasets.Generator.CollectionSetGenerator;
import demir.datasets.Generator.ImportFileText;
import demir.tc.classification.TestClassificationByQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.terrier.structures.DocumentIndexEntry;
import org.terrier.structures.Index;
import org.terrier.structures.IndexOnDisk;
import static org.terrier.structures.IndexUtil.close;
import org.terrier.structures.Lexicon;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.bit.DirectIndex;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author Meltem
 */
public class ImportTurkishTrecMedCollection {

    CollectionSetGenerator oGenerator = null;
    int iCollectionId = 22;
    int iSetId = 1;
    HashMap<String, String> docList = new HashMap();
    ArrayList<String> TrainDocList = new ArrayList();
    ArrayList<String> TestDocList = new ArrayList();

    public static void main(String[] args) {
        try {
            ImportTurkishTrecMedCollection objectTrMed = new ImportTurkishTrecMedCollection();
            //objectTrMed.ReadLabelFile();
            //objectTrMed.Generate();
            ReadFolder();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RCV1Converter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ImportTurkishTrecMedCollection() {
        oGenerator = new CollectionSetGenerator();
    }

    /// Türkçe veri seti için hazırlanmış label doc dosyasını okuyup Docs ve DocLabels oluşturdum.
    public void ReadLabelFile() {
        String LabelFile = "D:\\Datasets\\turkish_trec_med\\output_data\\ICDLabelDocs.txt";
        String sLine = null;
        try {
            File fileDir = new File(LabelFile);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "windows-1254"));
            while ((sLine = br.readLine()) != null) {
                String[] DocList = sLine.split(" ");
                String Label = DocList[0];
                Transaction t = oGenerator.getSession().beginTransaction();
                for (int i = 1; i < DocList.length; i++) {
                    String Doc = DocList[i];
                    oGenerator.AddDocToDocs(iCollectionId, Doc, Doc, oGenerator.getSession());
                    oGenerator.AddDocLabels(iCollectionId, Doc, Label, i, oGenerator.getSession());
                }
                t.commit();
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Generate() throws SQLException, Exception {
        String SourcePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\turkish_trec_med\\";
        String DestPath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50 cat\\1\\";
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        int[] QueryList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50
        };

        for (int iQueryId = 0; iQueryId < QueryList.length; iQueryId++) {
            /// Seti Küçültmek için her labelda kaç örnek varsa 10'a böldüm.
            /// Elde edilen setin %10'ununu test geri kalanının train için kullanıdm.
            long FileCnt = GetLabelFileCount(con, iCollectionId, QueryList[iQueryId]);
            FileCnt = FileCnt / 10;
            GetLabelFileList(con, iCollectionId, QueryList[iQueryId], FileCnt);
        }
        System.out.print("End");
        Collections.sort(TrainDocList);
        Collections.sort(TestDocList);

        System.out.println("TRAIN DOC CAT");
        Transaction t = oGenerator.getSession().beginTransaction();
        for (int i = 0; i < TrainDocList.size(); i++) {
            /*
              oGenerator.AddDocToTtDocs(iCollectionId, iSetId, TrainDocList.get(i), "TR", oGenerator.getSession());

             
            ResultSet rs = GetFileLabel(con, iCollectionId, TrainDocList.get(i));
            while (rs.next()) {
                int iQueryId = (int) rs.getObject("QUERY_ID");
                if (iQueryId < QueryList.length) {
                    System.out.println(i + " " + iQueryId);
                }
            }
            
            
            try {
                
                File source = new File(SourcePath  + "Report" + TrainDocList.get(i) + ".xml");
                File dest = new File(DestPath +  "Train\\" + "Report" + TrainDocList.get(i)+ ".xml");

                java.nio.file.Files.copy(source.toPath(), dest.toPath());
              

            } catch (java.nio.file.FileAlreadyExistsException ex) {
                System.out.println(TrainDocList.get(i));
            } catch (IOException ex) {
                Logger.getLogger(ImportTurkishTrecMedCollection.class.getName()).log(Level.SEVERE, null, ex);
            }
             */
        }
        System.out.println("TRAIN DOC CAT");

        System.out.println("TEST DOC CAT");
        for (int i = 0; i < TestDocList.size(); i++) {

            ResultSet rs = GetFileLabel(con, iCollectionId, TestDocList.get(i));
            while (rs.next()) {
                int iQueryId = (int) rs.getObject("QUERY_ID");
                if (iQueryId < QueryList.length) {
                    System.out.println(i + " " + iQueryId);
                }
            }

            try {
                File source = new File(SourcePath + "Report" + TestDocList.get(i) + ".xml");
                File dest = new File(DestPath + "Test\\" + "Report" + TestDocList.get(i) + ".xml");

                java.nio.file.Files.copy(source.toPath(), dest.toPath());

                //oGenerator.AddDocToTtDocs(iCollectionId, iSetId, TestDocList.get(i), "TE", oGenerator.getSession());

            } catch (java.nio.file.FileAlreadyExistsException ex) {
                System.out.println(TestDocList.get(i));
            } catch (IOException ex) {
                Logger.getLogger(ImportTurkishTrecMedCollection.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("TEST DOC CAT");

        t.commit();
    }

    /// Query'leri sıraya koydum örnek sayısına göre collection_labels'a kaydettin.
    public void GetLabelFileList(Connection con, int pCollectionId, int QueryId, Long FileCnt) throws SQLException, Exception {
        Statement st;
        long iTestFileCnt = FileCnt / 10;

        String sSql1 = "SELECT FILE_ID FROM demir_tc.doc_labels, collection_labels "
                + " WHERE doc_labels.COLLECTION_ID = " + pCollectionId + "  AND "
                + " doc_labels.COLLECTION_ID = collection_labels.COLLECTION_ID  "
                + " AND doc_labels.LABEL = collection_labels.LABEL "
                + " AND collection_labels.QUERY_ID = " + QueryId
                + " ORDER BY FILE_ID ";
        st = (Statement) con.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sSql1);
        int iFileOrder = 0;
        String sFlag = "TE";
        while (rs.next()) {
            String FileId = rs.getObject("FILE_ID").toString();
            if (iFileOrder > FileCnt) {
                break;
            }
            //if(iFileOrder == iTestFileCnt)System.out.print(QueryId + " " +  iFileOrder +  " ");
            if (iFileOrder > iTestFileCnt) {

                sFlag = "TR";
            }
            if (!docList.containsKey(FileId)) {
                /// Dosyayı klasöre Kopyala Train veya Test olmasına göre
                //System.out.println(QueryId + " " + FileId + "  " + sFlag);
                docList.put(FileId, sFlag);
                if (sFlag == "TR") {
                    TrainDocList.add(FileId);
                } else {
                    TestDocList.add(FileId);
                }
                iFileOrder++;
            }
        }
        if (sFlag != "TR") {
            throw new Exception(QueryId + "Hiç Train Dosyası yok");
        }
        //System.out.print(iFileOrder);
        //System.out.println();
    }

    public static long GetLabelFileCount(Connection con, int pCollectionId, int QueryId) throws SQLException {
        Statement st;
        String sSql1 = "SELECT COUNT(*) AS FILE_CNT FROM demir_tc.doc_labels, collection_labels "
                + " WHERE doc_labels.COLLECTION_ID = " + pCollectionId + "  AND "
                + " doc_labels.COLLECTION_ID = collection_labels.COLLECTION_ID  "
                + " AND doc_labels.LABEL = collection_labels.LABEL "
                + " AND collection_labels.QUERY_ID = " + QueryId
                + " ORDER BY FILE_ID ";
        st = (Statement) con.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sSql1);
        if (rs.first()) {
            return (long) rs.getObject("FILE_CNT");
        }
        return 0;
    }

    public static ResultSet GetFileLabel(Connection con, int pCollectionId, String pFıleId) throws SQLException {
        Statement st;
        String sSql1 = "SELECT doc_labels.LABEL, QUERY_ID FROM DOC_LABELS, COLLECTION_LABELS\n"
                + "WHERE doc_labels.COLLECTION_ID = " + pCollectionId + "\n"
                + "AND doc_labels.COLLECTION_ID = collection_labels.COLLECTION_ID\n"
                + "AND doc_labels.LABEL = collection_labels.LABEL\n"
                + "AND FILE_ID = '" + pFıleId + "'";
        st = (Statement) con.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sSql1);
        return rs;

    }
    
    /// Bu fonksiyon TurkishTrec Dosylarını düzeltmek için hazırlnamıştır.
    /// TAG sırası bozuktu.
    public static void ReadFolder() throws IOException
    {
        //String sFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\train_unordered\\";
        String sFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\test_unordered\\";
        File file = new File(sFilePath);
        File[] files = file.listFiles();
        //String DestFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\train\\";
        String DestFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\test\\";
        for(int i = 0; i  < files.length; i++)
        {
            String QueryId = files[i].getName().replace("Report", "").replace(".xml", "");
            String sWholeText = ReadFile(files[i].getAbsolutePath());
            String[] QueryTags = {"KlinikSeyir", "Anamnez"};
            //sWholeText = GenerateTRECFile(QueryId, sWholeText, QueryTags);
            sWholeText = GenerateQueryFile(QueryId, sWholeText, QueryTags);
            //System.out.println(sWholeText);
            PrintWriter fw = new PrintWriter(new FileWriter(DestFilePath + "Report" + QueryId + ".xml"));
            fw.append(sWholeText);
            fw.flush();
            fw.close();
        }
    }
    
    public static String ReadFile(String sFilePath) {

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
            Logger.getLogger(ImportTurkishTrecMedCollection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sWholeText;
    }
    
    
    public static String GenerateTRECFile(String queryId, String sWholeText, String[] QueryTags) {
        String sQueryText = "<REPORT>"
            + "<REPORT_ID>" + queryId + "</REPORT_ID>";
        for (int i = 0; i < QueryTags.length; i++) {
            int iStartIndex = sWholeText.indexOf("<" + QueryTags[i] + ">");
            int iEndIndex = sWholeText.indexOf("</" + QueryTags[i] + ">");
            if (iStartIndex > 0 && iEndIndex > 0) {
                String sQueryTag = sWholeText.substring(iStartIndex + ("<" + QueryTags[i] + ">").length(), iEndIndex);
                sQueryText += "<" + QueryTags[i] + ">" + sQueryTag.replace('İ', 'i') + "</" + QueryTags[i] + ">";
            }
        }
        sQueryText += "</REPORT>";
        return sQueryText;
    }
    
     public static String GenerateQueryFile(String queryId, String sWholeText, String[] QueryTags) {
        String sQueryText = "<TOP>"
            + "<NUM>" + queryId + "</NUM>";
        for (int i = 0; i < QueryTags.length; i++) {
            int iStartIndex = sWholeText.indexOf("<" + QueryTags[i] + ">");
            int iEndIndex = sWholeText.indexOf("</" + QueryTags[i] + ">");
            if (iStartIndex > 0 && iEndIndex > 0) {
                String sQueryTag = sWholeText.substring(iStartIndex + ("<" + QueryTags[i] + ">").length(), iEndIndex);
                if(sQueryTag.length() > 2)
                    sQueryText += "<" + QueryTags[i] + ">" + sQueryTag.replace('İ', 'i') + "</" + QueryTags[i] + ">";
                else
                {
                    sQueryText += "<" + QueryTags[i] + ">" + "ZXAA" + "</" + QueryTags[i] + ">";
                    System.out.println("Boş Dosya" + queryId);
                }
            }
            else
            {
                sQueryText += "<" + QueryTags[i] + ">" + "ZXAA" + "</" + QueryTags[i] + ">";
                System.out.println("Boş Dosya" + queryId);
            }
        }
        sQueryText += "</TOP>";
        return sQueryText;
    }
     
     
    


}
