/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.applications.batchquerying;

import demir.datasets.Generator.ImportFileText;
import demir.datasets.ImportTurkishTrecMedCollection;
import static demir.terrier.indexing.DocumentIndexWriterInArffFormat.ReadDocLabelsInQRelsFormat;
import static demir.terrier.indexing.DocumentIndexWriterInArffFormat.ReadLabels;
import static demir.terrier.indexing.DocumentIndexWriterInArffFormat.printLexicon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.terrier.matching.models.TF_IDF;
import org.terrier.querying.Request;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.IndexUtil;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;

/**
 *
 * @author nmeltem
 */
public class TrecQuerying {

    protected static final org.apache.log4j.Logger logger
            = org.apache.log4j.Logger.getLogger(TrecQuerying.class);

    public static void main(String[] args) {

        /*// 20NEWS
        ArrayList labels = ReadLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\CATS.txt");
        HashMap doclabels = ReadDocLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\DOC_CAT.TXT", labels);
        String outputFile = "D:\\R PROGRAMMING\\data\\20NEWS\\18_1_term_doc_test.arff";
        String TestPath = "D:\\Datasets\\20NEWSGROUP\\20news-18828\\test_terrier";
        // 20NEWS
         */
        /*//RCV1
        ArrayList labels = ReadLabels("D:\\Datasets\\RCV1\\rcv1.topics.txt");
        ArrayListValuedHashMap<String, ArrayList<String>> mDocLabels = new ArrayListValuedHashMap<>();
        HashMap doclabels = ReadDocLabelsInQRelsFormat("D:\\Datasets\\RCV1\\rcv1-v2.topics.qrels", labels, mDocLabels);
        String outputFile = "D:\\R PROGRAMMING\\data\\RCV1\\17_1_term_doc_test";
        String outputLabelFile = "D:\\R PROGRAMMING\\data\\RCV1\\17_1_doc_label_test";
        String TestPath = "D:\\Datasets\\RCV1\\appendix 12\\test_terrier";
        //RCV1
        */
        
      
        String outputFile = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\22_1_term_doc_tf_idf_F5.arff_test";
        String outputLabelFile = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\22_1_doc_label_test.qrels";
        String TestPath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\test\\";
        
        TrecQuerying trecquerying = new TrecQuerying();

        try {
            //trecquerying.WriteQueryFilesInArffFormat(outputFile, outputLabelFile, TestPath, doclabels, mDocLabels);
            //trecquerying.WriteRCV1QueryFilesInArffFormat(outputFile, outputLabelFile, TestPath, doclabels, mDocLabels);
            trecquerying.WriteQueryFilesInArffFormatWithoutLabel(outputFile, outputLabelFile, TestPath);
           
        } catch (IOException ex) {
            Logger.getLogger(TrecQuerying.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void WriteQueryFilesInArffFormatWithoutLabel(String outputFile, String outputLabelFile, String TestPath
            ) throws FileNotFoundException, IOException {
        String pQueryText = "";
        String pQueryId = "";
        double c = 1.0;
        Boolean isParameterValueSpecified = false;
        Index index = loadIndex();
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter(outputFile);
        PrintWriter pwmLabel = new PrintWriter(outputLabelFile);
        pwm.println("@relation DTM");
        int numberOfEntries = printLexicon(index, "lexicon", pwm);
        pwm.println("@data");

        int docid = 0;
        File[] files = ListFiles(TestPath);
       // Connection con = demir.dbconnection.ConnectToServer.Connect();
        for (docid = 0; docid < files.length; docid++) {
            String sLine = "{";
            pQueryText = ReadFile(files[docid].getAbsoluteFile().getAbsolutePath());
            
            String docno = files[docid].getAbsoluteFile().getAbsolutePath().replace((TestPath + "Report"), "");
            docno = docno.replace(".xml", "");
            // Multilabel Query için default 1 yaptım. Labelları Qrels dosyasına yazıyorum.
            String docLabel = "1";
            System.out.println(docno);  
            org.terrier.applications.batchquerying.TRECQuerying querying
                    = new org.terrier.applications.batchquerying.TRECQuerying(pQueryText, docno, index, null);
            SearchRequest srq = querying.processOneQuery(c, isParameterValueSpecified);
            System.out.println(srq.getQueryID());
            Request rq = (Request) srq;
            rq.getMatchingQueryTerms();
            ArrayList termList = new ArrayList();
            for (String entry : rq.getMatchingQueryTerms().getTerms()) {
                int TermId = -1;
                //System.out.println(entry);
                if(lx.getLexiconEntry(entry) != null)
                    TermId = (int) lx.getLexiconEntry(entry).getWritableEntryStatistics().getTermId();
                /* bu kod çalışmıyor .
                if (rq.getMatchingQueryTerms().getStatistics(entry) != null) {
                    TermId = rq.getMatchingQueryTerms().getStatistics(entry).getTermId();
                }
                */
                
                //System.out.println(entry + " " + TermId);
                termList.add(TermId);
            }
            termList.sort(null);
            for (int i = 0; i < termList.size(); i++) {

                int iTermIndex = (int) termList.get(i);
                if (iTermIndex > -1) {
                    sLine = sLine + iTermIndex + " " + "1" + ",";
                }

            }
            sLine = sLine + numberOfEntries + " " + docLabel + "}";
            /*
            try {
                ResultSet rs = ImportTurkishTrecMedCollection.GetFileLabel(con, 22, docno);
            } catch (SQLException ex) {
                Logger.getLogger(TrecQuerying.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            pwm.println(sLine);
            if (docid % 1000 == 0) {
                pwm.flush();
                pwmLabel.flush();
            }
        }
        pwm.close();
        pwmLabel.close();
    }

    public void WriteQueryFilesInArffFormat(String outputFile, String outputLabelFile, String TestPath,
            HashMap docLabels, ArrayListValuedHashMap mDocLabels) throws FileNotFoundException, IOException {
        String pQueryText = "";
        String pQueryId = "";
        double c = 1.0;
        Boolean isParameterValueSpecified = false;
        Index index = loadIndex();
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter(outputFile);
        PrintWriter pwmLabel = new PrintWriter(outputLabelFile);
        pwm.println("@relation DTM");
        int numberOfEntries = printLexicon(index, "lexicon", pwm);
        pwm.println("@data");

        int docid = 0;
        File[] files = ListFiles(TestPath);
        for (docid = 0; docid < files.length; docid++) {
            String sLine = "{";
            pQueryText = ReadFile(files[docid].getAbsoluteFile().getAbsolutePath());

            String docno = files[docid].getAbsoluteFile().getAbsolutePath().replace((TestPath + "\\"), "");
            String docLabel = null;
            Object[] mDocLabel = null;

            if (docLabels != null) {
                docLabel = (String) docLabels.get(docno);
                mDocLabel = mDocLabels.get(docno).toArray();
                //mDocLabel = (AbstractListValuedMap) mDocLabels.get(docno);
            }

            if (mDocLabel != null) {
                for (Object mDocLabel1 : mDocLabel) {
                    pwmLabel.println(docid + 1 + " " + mDocLabel1);
                }
            }

            org.terrier.applications.batchquerying.TRECQuerying querying
                    = new org.terrier.applications.batchquerying.TRECQuerying(pQueryText, pQueryId, index, null);
            SearchRequest srq = querying.processOneQuery(c, isParameterValueSpecified);
            Request rq = (Request) srq;
            rq.getMatchingQueryTerms();
            ArrayList termList = new ArrayList();
            for (String entry : rq.getMatchingQueryTerms().getTerms()) {
                int TermId = -1;
                if (rq.getMatchingQueryTerms().getStatistics(entry) != null) {
                    TermId = rq.getMatchingQueryTerms().getStatistics(entry).getTermId();
                }
                System.out.println(entry + " " + TermId);
                termList.add(TermId);
            }
            termList.sort(null);
            for (int i = 0; i < termList.size(); i++) {

                int iTermIndex = (int) termList.get(i);
                if (iTermIndex > -1) {
                    sLine = sLine + iTermIndex + " " + "1" + ",";
                }

            }
            sLine = sLine + numberOfEntries + " " + docLabel + "}";
            pwm.println(sLine);
            if (docid % 1000 == 0) {
                pwm.flush();
                pwmLabel.flush();
            }
        }
        pwm.close();
        pwmLabel.close();
    }

    public void WriteRCV1QueryFilesInArffFormat(String outputFile, String outputLabelFile, String TestPath,
            HashMap docLabels, ArrayListValuedHashMap mDocLabels) throws FileNotFoundException, IOException {
        String pQueryText = "";
        String pQueryId = "1";
        double c = 1.0;
        Boolean isParameterValueSpecified = false;
        Index index = loadIndex();
        Lexicon lx = index.getLexicon();
        int docid = 0;
        int testDocId = 0;
        File[] files = ListFiles(TestPath);
        for (testDocId = 0; testDocId < files.length; testDocId++) {
            String [] testFiles = ReadRCV1TerrierTestFile(files[testDocId].getAbsolutePath());
            System.out.println(files[testDocId].getAbsolutePath());
            PrintWriter pwm = new PrintWriter(outputFile + testDocId + ".arff");
            PrintWriter pwmLabel = new PrintWriter(outputLabelFile + testDocId + ".txt");
            
            pwm.println("@relation DTM");
            int numberOfEntries = printLexicon(index, "lexicon", pwm);
            pwm.println("@data");
            
            for (docid = 1; docid < testFiles.length; docid++) {
                String sLine = "{";
                pQueryText = "<TOP><NUM>" + testFiles[docid];

                String docno =  testFiles[docid].substring(0, testFiles[docid].indexOf("NUM") - 1).trim();
                String docLabel = null;
                Object[] mDocLabel = null;

                if (docLabels != null) {
                    docLabel = (String) docLabels.get(docno);
                    mDocLabel = mDocLabels.get(docno).toArray();
                    //mDocLabel = (AbstractListValuedMap) mDocLabels.get(docno);
                }

                if (mDocLabel != null) {
                    for (Object mDocLabel1 : mDocLabel) {
                        pwmLabel.println(docid + " " + mDocLabel1);
                    }
                }

                org.terrier.applications.batchquerying.TRECQuerying querying
                        = new org.terrier.applications.batchquerying.TRECQuerying(pQueryText, pQueryId, index, null);
                SearchRequest srq = querying.processOneQuery(c, isParameterValueSpecified);
                Request rq = (Request) srq;
                rq.getMatchingQueryTerms();
                ArrayList termList = new ArrayList();
                for (String entry : rq.getMatchingQueryTerms().getTerms()) {
                    int TermId = -1;
                    if(index.getLexicon().getLexiconEntry(entry) != null)
                        TermId = index.getLexicon().getLexiconEntry(entry).getTermId();
                    /*
                    RCV1 veri setinde entryStatistics boş geldi.
                    if (rq.getMatchingQueryTerms().getStatistics(entry) != null) {
                        TermId = rq.getMatchingQueryTerms().getStatistics(entry).getTermId();
                    }*/
                    //System.out.println(entry + " " + TermId);
                    if(TermId > -1)
                        termList.add(TermId);
                }
                termList.sort(null);
                for (int i = 0; i < termList.size(); i++) {

                    int iTermIndex = (int) termList.get(i);
                    if (iTermIndex > -1) {
                        sLine = sLine + iTermIndex + " " + "1" + ",";
                    }

                }
                sLine = sLine + numberOfEntries + " " + docLabel + "}";
                pwm.println(sLine);
                if (docid % 1000 == 0) {
                    pwm.flush();
                    pwmLabel.flush();
                    System.out.println(docid);
                }
            }
            pwm.close();
            pwmLabel.close();
            //if(testDocId == 0) break;
        }
    }

    protected Index loadIndex() {
        long startLoading = System.currentTimeMillis();
        Index index = Index.createIndex();
        if (index == null) {
            logger.fatal("Failed to load index. " + Index.getLastIndexLoadError());
            throw new IllegalArgumentException("Failed to load index: " + Index.getLastIndexLoadError());
        }
        long endLoading = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("time to intialise index : "
                    + ((endLoading - startLoading) / 1000.0D));
        }
        return index;
    }

    public static String ReadFile(String FilePath) {
        String sLine = "";
        String sWholeText = "";
        ArrayList aLabels = new ArrayList();
        try {
            // FileReader fr = new FileReader(FilePath);
            File fileDir = new File(FilePath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            // new FileInputStream(fileDir), "windows-1254"));
                            new FileInputStream(fileDir), "UTF-8"));
            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\n";
            }
            br.close();
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sWholeText;
    }

    public static String[] ReadRCV1TerrierTestFile(String FilePath) {
        String sLine = "";
        String sWholeText = "";
        String[] queryFiles = null;
        int iLineCnt = 0;
        try {
            // FileReader fr = new FileReader(FilePath);
            File fileDir = new File(FilePath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "windows-1254"));
            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\n";
                iLineCnt++;
                //if(iLineCnt == 948) break;
            }
            br.close();
            queryFiles = sWholeText.split("<TOP><NUM>");
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queryFiles;
    }

    public static File[] ListFiles(String sFolder) {
        System.out.println(sFolder);
        File file = new File(sFolder);
        File[] files = file.listFiles();
        return files;
    }
}
