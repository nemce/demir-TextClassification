/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.applications.batchquerying;

import demir.datasets.Generator.ImportFileText;
import static demir.terrier.indexing.DocumentIndexWriterInArffFormat.ReadDocLabels;
import static demir.terrier.indexing.DocumentIndexWriterInArffFormat.ReadLabels;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.terrier.matching.models.TF_IDF;
import org.terrier.querying.Request;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.IndexUtil;
import org.terrier.structures.LexiconEntry;

/**
 *
 * @author nmeltem
 */
public class TrecQuerying {

    protected static final org.apache.log4j.Logger logger
        = org.apache.log4j.Logger.getLogger(TrecQuerying.class);

    public static void main(String[] args) {

        ArrayList labels = ReadLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\CATS.txt");
        HashMap doclabels = ReadDocLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\DOC_CAT.TXT", labels);
        String outputFile = "D:\\R PROGRAMMING\\data\\20NEWS\\18_1_term_doc.arff";
      
        TrecQuerying trecquerying = new TrecQuerying();
        trecquerying.WriteQueryFilesInArffFormat(null);
        
        
        /// Burda kaldım
        /// Test file oku tümünü
        /// arf formatına getir 
        /// trin dosysı için att isimleri hatalı düzelt.
    }
    
    public void  WriteQueryFilesInArffFormat(String outputFile)
    {
        String pQueryText = "";
        String pQueryId = "";
        double c = 1.0;
        Boolean isParameterValueSpecified = false;
        Index index = loadIndex();
        pQueryText = ReadFile("D:\\Datasets\\20NEWSGROUP\\20news-18828\\test_terrier\\rec_sport_hockey_54067");
        org.terrier.applications.batchquerying.TRECQuerying querying
            = new org.terrier.applications.batchquerying.TRECQuerying(pQueryText, pQueryId, index, null);
        SearchRequest srq = querying.processOneQuery(c, isParameterValueSpecified);
        Request rq = (Request) srq;
        rq.getMatchingQueryTerms();
        ArrayList termList = new ArrayList();
        for (String entry : rq.getMatchingQueryTerms().getTerms())
        {
            int TermId =  -1;
            if(rq.getMatchingQueryTerms().getStatistics(entry) != null)
             TermId = rq.getMatchingQueryTerms().getStatistics(entry).getTermId();
            System.out.println(entry + " " + TermId);
            termList.add(TermId);
        }
        termList.sort(null);
        
        for(int i = 0; i < termList.size();i++)
        {
          
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
                      new FileInputStream(fileDir), "windows-1254"));  
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
}
