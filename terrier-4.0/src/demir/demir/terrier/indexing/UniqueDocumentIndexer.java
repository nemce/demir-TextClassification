/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.indexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import org.terrier.indexing.FileDocument;
import org.terrier.realtime.incremental.IncrementalIndex;
import org.terrier.realtime.incremental.IncrementalMergeGeometric;
import org.terrier.realtime.incremental.IncrementalMergePolicy;
import org.terrier.realtime.incremental.IncrementalMergeSingle;
import org.terrier.realtime.memory.MemoryIndex;
import org.terrier.realtime.multi.MultiIndex;
import org.terrier.structures.Index;
import org.terrier.structures.IndexOnDisk;
import org.terrier.structures.IndexUtil;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.LexiconUtil;
import org.terrier.structures.PostingIndexInputStream;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class UniqueDocumentIndexer {

    public static void main(String[] args) {
        try {
            test_incremental();
        } catch (Exception ex) {
            Logger.getLogger(UniqueDocumentIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void test_incremental() throws Exception {
        IncrementalIndex ii = IncrementalIndex.get("D:\\terrier_home_v4\\var\\index\\", "data");
        System.out.println("Before");
        System.out.println("Number of Docs : " + ii.getDocumentIndex().getNumberOfDocuments());
        System.out.println("Number of Tokens : " + ii.getCollectionStatistics().getNumberOfTokens());
        System.out.println("Number of Terms : " + ii.getCollectionStatistics().getNumberOfUniqueTerms());

        Document doc = new FileDocument("11", new ByteArrayInputStream(
                "aaa jjj jjj kkk mmm".getBytes()),
                new org.terrier.indexing.tokenisation.UTFTokeniser());
        ii.memory.indexDocument(doc);

        System.out.println("After");
        System.out.println("Number of Docs : " + ii.getDocumentIndex().getNumberOfDocuments());
        System.out.println("Number of Tokens : " + ii.getCollectionStatistics().getNumberOfTokens());
        System.out.println("Number of Terms : " + ii.getCollectionStatistics().getNumberOfUniqueTerms());
        ii.flush();
    }

    public static void printLexicon(Lexicon<String> lex) throws IOException {
        Iterator<Map.Entry<?, LexiconEntry>> lexiconStream
                = (Iterator<Map.Entry<?, LexiconEntry>>) lex;
        while (lexiconStream.hasNext()) {
            Map.Entry<?, LexiconEntry> lee = lexiconStream.next();
            System.out.println(lee.getKey().toString() + "," + lee.getValue().toString());
        }
        IndexUtil.close(lexiconStream);
    }

}
