/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.indexing;

import demir.datasets.Generator.ImportFileText;
import demir.dbconnection.ConnectToServer;
import demir.dbconnection.DBFunctions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.transformation.SortedList;
import org.hibernate.Session;
import org.hsqldb.persist.HsqlDatabaseProperties;
import org.terrier.matching.models.Idf;
import org.terrier.matching.models.TF_IDF;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.DocumentIndexEntry;
import org.terrier.structures.Index;
import org.terrier.structures.IndexOnDisk;
import org.terrier.structures.IndexUtil;
import static org.terrier.structures.IndexUtil.close;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.Pointer;
import org.terrier.structures.PostingIndex;
import org.terrier.structures.bit.DirectIndex;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class DocumentIndexWriterInArffFormat {

    protected static final org.apache.log4j.Logger logger
        = org.apache.log4j.Logger.getLogger(DocumentIndexWriterInArffFormat.class);

    public static void main(String[] args) {
        DocumentIndexWriterInArffFormat diw = new DocumentIndexWriterInArffFormat();
        Index index = diw.loadIndex();
        ArrayList labels = ReadLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\CATS.txt");
        HashMap doclabels = ReadDocLabels("D:\\R PROGRAMMING\\Data\\20NEWS\\DOC_CAT.TXT", labels);
        String outputFile = "D:\\R PROGRAMMING\\data\\20NEWS\\18_1_term_doc.arff";
        try {
             diw.printArff(index, "document", doclabels, outputFile);
        } catch (Exception ex) {
            Logger.getLogger(DocumentIndexWriter.class.getName()).log(Level.SEVERE, null, ex);
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

    protected void printArff(Index index, String structureName, HashMap docLabels, 
        String outputFile) throws IOException {

        Iterator<DocumentIndexEntry> iterator
            = (Iterator<DocumentIndexEntry>) index.getIndexStructureInputStream(structureName);

        String metaIndexDocumentKey
            = ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        final MetaIndex metaIndex = index.getMetaIndex();
        int docid = 0;
        DirectIndex di = new DirectIndex((IndexOnDisk) index, "direct");
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter(outputFile);
        pwm.println("@relation DTM");
        int numberOfEntries = printLexicon(index,  "lexicon", pwm);
        pwm.println("@data");
        while (iterator.hasNext()) {
            DocumentIndexEntry die = iterator.next();
            String docno = metaIndex.getItem(metaIndexDocumentKey, docid);
            String docLabel = String.valueOf(docLabels.get(docno));
            int[][] docTerms = di.getTerms(docid);

            String sLine = "{";

            double docLength = die.getDocumentLength();
            for (int i = 0; i < docTerms[0].length; i++) {

                String sTerm = lx.getLexiconEntry(docTerms[0][i]).getKey().toString();
                LexiconEntry lxe = (LexiconEntry) lx.getLexiconEntry(docTerms[0][i]).getValue();

                double score;
                TF_IDF wm = new TF_IDF();
                wm.setCollectionStatistics(index.getCollectionStatistics());
                wm.setEntryStatistics(lxe.getWritableEntryStatistics());
                wm.setKeyFrequency(1);
                IndexUtil.configure(index, wm);
                wm.prepare();
                double tf = docTerms[1][i];
                score = wm.score(tf, docLength);
                sLine = sLine + docTerms[0][i] + " " + score + ",";

            }
            sLine = sLine + numberOfEntries + " " +  docLabel + "}";
            pwm.println(sLine);
            if (docid % 1000 == 0) {
                pwm.flush();
            }
            docid++;
        }

        pwm.close();
        close(iterator);
    }

    public static int printLexicon(Index index, String structureName, PrintWriter pwm) throws IOException {
        double numberOfDocs = index.getDocumentIndex().getNumberOfDocuments();
        Idf idf = new Idf(numberOfDocs);
        Iterator<Map.Entry<?, LexiconEntry>> lexiconStream
            = (Iterator<Map.Entry<?, LexiconEntry>>) index.getIndexStructureInputStream(structureName);
        int i = 0;
        
        while (lexiconStream.hasNext()) {
            
            Map.Entry<?, LexiconEntry> lee = lexiconStream.next();
            //pwm.println("@attribute  t" + lee.getKey().toString() + " numeric");
            pwm.println("@attribute  t" + i  + " numeric");
            if (i % 100 == 0) { 
                pwm.flush();
            }
            i++;
        }
        pwm.println("@attribute class numeric");
        IndexUtil.close(lexiconStream);
        return index.getLexicon().numberOfEntries();
    }
    
    public void LoadDocLabels()
    {
    
    }
    
    public static ArrayList ReadLabels(String FilePath) {
        String sLine = "";
        ArrayList aLabels = new ArrayList();
        try {
           // FileReader fr = new FileReader(FilePath);
             File fileDir = new File(FilePath);
		BufferedReader br = new BufferedReader(
		   new InputStreamReader(
                      new FileInputStream(fileDir), "windows-1254"));  
            while ((sLine = br.readLine()) != null) {
                aLabels.add(sLine);
            }
            br.close();
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aLabels;
    }
    
    public static HashMap ReadDocLabels(String FilePath, ArrayList aLabels) {
        String sLine = "";
        HashMap<String, String> DocLabels = new HashMap();
        try {
           // FileReader fr = new FileReader(FilePath);
             File fileDir = new File(FilePath);
		BufferedReader br = new BufferedReader(
		   new InputStreamReader(
                      new FileInputStream(fileDir), "windows-1254"));  
            while ((sLine = br.readLine()) != null) {
                String [] values = sLine.split(",");
                DocLabels.put(values[0], String.valueOf(aLabels.indexOf(values[1]) + 1) );
            }
            br.close();
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DocLabels;
    }
}
