/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.indexing;

import demir.featureselection.ClassInfo;
import demir.featureselection.FeatureGenerator;
import demir.featureselection.TermInfo;
import demir.tc.classification.ClassificationParameters;
import demir.tc.classification.IRMedicalQuery;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
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
public class DocumentIndexWriter {

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DocumentIndexWriter.class);

    public static void main(String[] args) {
        DocumentIndexWriter diw = new DocumentIndexWriter();
        Index index = diw.loadIndex();
        try {
            //diw.printDocumentIndex(index, "document");
            //diw.GetInvertedIndex(index, null);
            diw.printLexicon(index, "lexicon");
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

    // printDocumentIndex fonksiyonu index'de yer alan dokumanları dışarı akktarır.
    // SVD algoritmasını denemek için hazırlanmıştır.
    protected void printDocumentIndex(Index index, String structureName) throws IOException {

        Iterator<DocumentIndexEntry> iterator = 
            (Iterator<DocumentIndexEntry>) index.getIndexStructureInputStream(structureName);

        String metaIndexDocumentKey = 
            ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        final MetaIndex metaIndex = index.getMetaIndex();
        int docid = 0;
        DirectIndex di = new DirectIndex((IndexOnDisk) index, "direct");
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter("D:\\Datasets\\20NEWSGROUP\\20news-18828\\documentindex.txt");

        while (iterator.hasNext()) {
            DocumentIndexEntry die = iterator.next();
            //System.out.print(docid + ": " + die.toString() + " ");
            String docno = metaIndex.getItem(metaIndexDocumentKey, docid);
            int[][] docTerms = di.getTerms(docid);
//            System.out.println(docid + ": " + die.toString()
//                    + " Doc No : " + docno
//                    + " Doc Length : " + die.getDocumentLength()
//                    + " # of entries : " + die.getNumberOfEntries());

            String sLine = "";
            for (int i = 0; i < docTerms[0].length; i++) {
                String sTerm = lx.getLexiconEntry(docTerms[0][i]).getKey().toString();
                System.out.println(docno + "," + sTerm + "," + docTerms[1][i]);
//                for (int j = 0; j < docTerms[1][i]; j++) {
//                    // System.out.print(sTerm);
//                    sLine += sTerm;
//
//                }
            }
//          pwm.println(sLine);
            if (docid % 1000 == 0) {
                pwm.flush();
            }
            docid++;
        }

        pwm.close();
        close(iterator);
    }

    
    protected void printWeightedDocumentIndex(Index index, String structureName) throws IOException {

        Iterator<DocumentIndexEntry> iterator = 
            (Iterator<DocumentIndexEntry>) index.getIndexStructureInputStream(structureName);

        String metaIndexDocumentKey =
            ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        final MetaIndex metaIndex = index.getMetaIndex();
        int docid = 0;
        DirectIndex di = new DirectIndex((IndexOnDisk) index, "direct");
        Lexicon lx = index.getLexicon();

        PrintWriter pwm = new PrintWriter("D:\\Datasets\\20NEWSGROUP\\20news-18828\\documentindex.txt");

        while (iterator.hasNext()) {
            DocumentIndexEntry die = iterator.next();
            //System.out.print(docid + ": " + die.toString() + " ");
            String docno = metaIndex.getItem(metaIndexDocumentKey, docid);
            int[][] docTerms = di.getTerms(docid);
//            System.out.println(docid + ": " + die.toString()
//                    + " Doc No : " + docno
//                    + " Doc Length : " + die.getDocumentLength()
//                    + " # of entries : " + die.getNumberOfEntries());

            String sLine = "";
            
            double docLength = die.getDocumentLength();
            for (int i = 0; i < docTerms[0].length; i++) {
                LexiconEntry lxe = (LexiconEntry) lx.getLexiconEntry(docTerms[0][i]);
                String sTerm =  lx.getLexiconEntry(docTerms[0][i]).getKey().toString();
                TF_IDF wm = new TF_IDF();
                //wm = (WeightingModel) queryTerms.getTermWeightingModels(queryTerm)[j].clone();
		wm.setCollectionStatistics(index.getCollectionStatistics());
		wm.setEntryStatistics(lxe.getWritableEntryStatistics());
		//wm.setRequest(queryTerms.getRequest());
		wm.setKeyFrequency(1);
		IndexUtil.configure(index, wm);
		wm.prepare();
                double tf = docTerms[1][i];
                wm.score(tf, docLength);
                System.out.println(docno + "," + sTerm + "," + docTerms[1][i]);
//                for (int j = 0; j < docTerms[1][i]; j++) {
//                    // System.out.print(sTerm);
//                    sLine += sTerm;
//
//                }
            }
//          pwm.println(sLine);
            if (docid % 1000 == 0) {
                pwm.flush();
            }
            docid++;
        }

        pwm.close();
        close(iterator);
    }

    
    public static void printLexicon(Index index, String structureName) throws IOException
    {
                double numberOfDocs = index.getDocumentIndex().getNumberOfDocuments();
                Idf idf = new Idf(numberOfDocs);
		Iterator<Map.Entry<?,LexiconEntry>> lexiconStream = 
			(Iterator<Map.Entry<?,LexiconEntry>>)index.getIndexStructureInputStream(structureName);
		while (lexiconStream.hasNext())
		{
			Map.Entry<?, LexiconEntry> lee = lexiconStream.next();
                        double idfVal = idf.idf(lee.getValue().getDocumentFrequency());
                        System.out.println(lee.getKey().toString()+","+ idfVal +","+lee.getValue().toString());
		}
		IndexUtil.close(lexiconStream);
	}
    // GetInvertedIndex fonksiyonu her bir termin hangi dokumanlarD geçtiğini bulur.
    // Dokuman listesi üzerinden class bilgisine ulaşılır.
    // Feature Selection algoritmalarının denenmesi için hazırlanmıştır.
    protected void GetInvertedIndex(Index index, String structureName) throws IOException, Exception {
        Lexicon lx = index.getLexicon();
        String metaIndexDocumentKey = ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        final MetaIndex metaIndex = index.getMetaIndex();
        PostingIndex<Pointer> invertedIndex = (PostingIndex<Pointer>) index.getInvertedIndex();
        ClassificationParameters prm = new ClassificationParameters();
        prm.LABEL_SEARCH_TYPE_DB = "DB";
        Session session = session = demir.tc.irbased.hibernate.connection.ConnectToServer.Connect();
        HashMap<String, ClassInfo> hmClass = new HashMap<>();
        HashMap<String, TermInfo> hmTerm = new HashMap<>();

        for (int i = 0; i < lx.numberOfEntries(); i++) {
            String sTerm = lx.getIthLexiconEntry(i).getKey().toString();
            //System.out.print(sTerm + " ");
            IterablePosting it = invertedIndex.getPostings((Pointer) lx.getIthLexiconEntry(i).getValue());
            TermInfo fsTerm = new TermInfo(i);

            while (!it.endOfPostings()) {
                it.next();
                int docid = it.getId();
                int iTermOccurence = it.getFrequency();
                IRMedicalQuery mq = new IRMedicalQuery();
                String docno = metaIndex.getItem(metaIndexDocumentKey, docid);
                mq.setCollectionId(prm.getCollectionId());
                mq.setsDocNo(docno);
                mq.SelRecLabelbyDocId(prm, session);
                //System.out.print(docno + ';');
                for (int j = 0; j < mq.getListICD().size(); j++) {
                    String sLabel = mq.getListICD().get(j);
                    hmClass.putIfAbsent(sLabel, new ClassInfo(sLabel));
                    hmClass.get(sLabel).AddTerm(sTerm, docno, iTermOccurence);
                    fsTerm.AddClass(sLabel);
                    fsTerm.AddClassOccurence(sLabel, iTermOccurence);
                }
            }
            hmTerm.put(sTerm, fsTerm);
            //System.out.println();
            
           // if(i==100) break;
        }
        
        FeatureGenerator.FeatureGenerator(hmClass, hmTerm);
    }


}
