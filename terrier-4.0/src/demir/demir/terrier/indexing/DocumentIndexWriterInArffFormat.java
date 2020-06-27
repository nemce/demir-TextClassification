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
import org.terrier.matching.models.Tf;
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
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.collections4.multimap.AbstractListValuedMap;

/**
 *
 * @author nmeltem
 */
public class DocumentIndexWriterInArffFormat {

    protected static final org.apache.log4j.Logger logger
            = org.apache.log4j.Logger.getLogger(DocumentIndexWriterInArffFormat.class);

    public static void main(String[] args) {
        boolean addDocLabel = false; // false for  multilabel Bu durumda labellar ayrı dosyadan okunacak.

        /*
        //20News
        String categoryFile = "D:\\R PROGRAMMING\\Data\\20NEWS\\CATS.txt";
        String categoryDocFile = "D:\\R PROGRAMMING\\Data\\20NEWS\\DOC_CAT.TXT";
        String outputFile = "D:\\R PROGRAMMING\\data\\20NEWS\\18_1_term_doc_tf_idf2.arff";
        
        ArrayList labels = ReadLabels(categoryFile);
        HashMap doclabels = null;
        doclabels = ReadDocLabels(categoryDocFile, labels);
        //20News
         */
        //RCV1
        /*
        String categoryFile = "D:\\Datasets\\RCV1\\rcv1.topics.txt";
        String categoryDocFile = "D:\\Datasets\\RCV1\\rcv1-v2.topics.qrels";
        String outputFile = "D:\\R PROGRAMMING\\data\\RCV1\\17_1_term_doc_tf_idf.arff";
        String outputLabelFile = "D:\\R PROGRAMMING\\data\\RCV1\\17_1_doc_label.txt";
         */
        String categoryFile = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\CATS.txt";
        String outputFile = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\22_1_term_doc_tf_idf.arff";
        String outputLabelFile = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\22_1_doc_label.txt";
        ArrayList labels = ReadLabels(categoryFile);
        HashMap doclabels = null;
        ArrayListValuedHashMap<String, ArrayList<String>> mDocLabels = new ArrayListValuedHashMap<>();
        // doclabels = ReadDocLabelsInQRelsFormat(categoryDocFile, labels, mDocLabels);

        //RCV1
        DocumentIndexWriterInArffFormat diw = new DocumentIndexWriterInArffFormat();
        Index index = diw.loadIndex();

        try {
            diw.printArff(index, "document", doclabels, outputFile, outputLabelFile, mDocLabels);
            
            
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
            String outputFile, String outputLabelFile,
            ArrayListValuedHashMap mDocLabels) throws IOException {

        Iterator<DocumentIndexEntry> iterator
                = (Iterator<DocumentIndexEntry>) index.getIndexStructureInputStream(structureName);

        String metaIndexDocumentKey
                = ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        final MetaIndex metaIndex = index.getMetaIndex();
        int docid = 0;
        DirectIndex di = new DirectIndex((IndexOnDisk) index, "direct");
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter(outputFile);
        PrintWriter pwmLabel = new PrintWriter(outputLabelFile);

        pwm.println("@relation DTM");
        int numberOfEntries = printLexicon(index, "lexicon", pwm);
        pwm.println("@data");
        int iDocCnt = 0;
        while (iterator.hasNext()) {

            iDocCnt++;
            //if (iDocCnt == 2000) break;

            DocumentIndexEntry die = iterator.next();
            String docno = metaIndex.getItem(metaIndexDocumentKey, docid);
            String docLabel = null;
            Object[] mDocLabel = null;

            if (docLabels != null) {
                docLabel = (String) docLabels.get(docno);
                mDocLabel = mDocLabels.get(docno).toArray();
                //mDocLabel = (AbstractListValuedMap) mDocLabels.get(docno);
            }

            if (mDocLabel != null) {
                for (Object mDocLabel1 : mDocLabel) {
                    pwmLabel.println(iDocCnt + " " + mDocLabel1);
                }
            }

            int[][] docTerms = di.getTerms(docid);

            String sLine = "{";
            try {
                double docLength = die.getDocumentLength();
                if (docLength > 0) {
                    for (int i = 0; i < docTerms[0].length; i++) {

                        String sTerm = lx.getLexiconEntry(docTerms[0][i]).getKey().toString();
                        LexiconEntry lxe = (LexiconEntry) lx.getLexiconEntry(docTerms[0][i]).getValue();

                        double score;
                        TF_IDF wm = new TF_IDF();
                        //Tf wm = new Tf();
                        wm.setCollectionStatistics(index.getCollectionStatistics());
                        wm.setEntryStatistics(lxe.getWritableEntryStatistics());
                        wm.setKeyFrequency(1);
                        IndexUtil.configure(index, wm);
                        wm.prepare();
                        double tf = docTerms[1][i];
                        score = wm.score(tf, docLength);
                        sLine = sLine + docTerms[0][i] + " " + score + ",";

                    }
                }
                if(docLabel != null)
                    sLine = sLine + numberOfEntries + " " + String.valueOf(docLabel) + "}";
                else /// Multilabelda class bilgisini arff dosyasına eklemedim. Ayrı dosya yapıyorum label doc ilişkisi için.
                    sLine = sLine + numberOfEntries + " " + "1" + "}";
                pwm.println(sLine);
                if (docid % 1000 == 0) {
                    pwm.flush();
                    pwmLabel.flush();
                }
                docid++;
                
                //if(docid == 10) break;
            } catch (Exception ex) {
                System.err.println(docno + " " + ex.getMessage());
            }
        }

        pwm.close();
        pwmLabel.close();
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
            pwm.println("@attribute  t" + i + " numeric");
            if (i % 100 == 0) {
                pwm.flush();
            }
            i++;
        }
        pwm.println("@attribute class numeric");
        IndexUtil.close(lexiconStream);
        return index.getLexicon().numberOfEntries();
    }

    public void LoadDocLabels() {

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
                String[] values = sLine.split(",");
                DocLabels.put(values[0], String.valueOf(aLabels.indexOf(values[1]) + 1));
            }
            br.close();
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DocLabels;
    }

    /*Added at 02.08.2019 
    RCV1 veri seti qrels dosya fotmatına uygun olacak şekilde düzenlendi*/
    public static HashMap ReadDocLabelsInQRelsFormat(String FilePath, ArrayList aLabels, ArrayListValuedHashMap mDocLabels) {
        String sLine = "";
        HashMap<String, String> DocLabels = new HashMap();

        try {
            // FileReader fr = new FileReader(FilePath);
            int LineCount = 0;
            File fileDir = new File(FilePath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "windows-1254"));
            while ((sLine = br.readLine()) != null) {
                LineCount++;
                //if(LineCount == 1000) break;
                String[] values = sLine.split(" ");
                DocLabels.put(values[1], String.valueOf(aLabels.indexOf(values[0]) + 1));
                mDocLabels.put(values[1], String.valueOf(aLabels.indexOf(values[0]) + 1));
            }
            br.close();
            //testArrayListValuedHashMap(mDocLabels);
            //fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DocLabels;
    }

    public static void testArrayListValuedHashMap(ArrayListValuedHashMap mDocLabels) {
        try {
            Object[] mDocLabel = mDocLabels.get("2286").toArray();

        } catch (Exception ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
