/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.matching;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.Priority;
import static org.junit.Assert.assertNotNull;
import org.terrier.matching.AccumulatorResultSet;
import org.terrier.matching.CollectionResultSet;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.PostingListManager;
import org.terrier.matching.ResultSet;
import org.terrier.matching.models.InL2;
import org.terrier.matching.models.WeightingModelFactory;
import org.terrier.structures.Index;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class TaatWeightingPrinter extends org.terrier.matching.taat.Full {

    public static void main(String[] args) {
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        if (con == null) {
            //java.util.logging.Logger.getLogger("main").log(Level.SEVERE, null);
            return;
        }

        RunMatching(4, con);
    }

    public static void RunMatching(int iRepoId, Connection con) {
        Index index = null;
        index = Index.createIndex();
        Lexicon lexicon = index.getLexicon();
        final MetaIndex metaIndex = index.getMetaIndex();
        //Manager queryingManager = new Manager(index);
        String wModel = "PL2";
   
                /**
                 * The name of the matching model that is used for retrieval.
                 * Defaults to Matching
                 */
                String mModel = ApplicationSetup.getProperty("trec.matching",
                "Matching");
        wModel = ApplicationSetup.getProperty("trec.model", InL2.class.getName());
        demir.terrier.matching.TaatWeightingPrinter fl = new TaatWeightingPrinter(index);

        assertNotNull(fl);
        MatchingQueryTerms mqt = new MatchingQueryTerms();
        for (int i = 0; i < lexicon.numberOfEntries(); i++) {
            String sTerm = (String) lexicon.getIthLexiconEntry(i).getKey();
            LexiconEntry entry = (LexiconEntry) lexicon.getIthLexiconEntry(i).getValue();
            System.out.println(sTerm + " " + entry.getTermId());
            mqt.setTermProperty(sTerm, 1);
            mqt.setDefaultTermWeightingModel(WeightingModelFactory.newInstance(wModel, index));
            //mqt.setDefaultTermWeightingModel(new PureTF_IDF());
        }

        try {
            fl.match("query1", mqt);
            //fl.WriteToDB(con, iRepoId, metaIndex);
            fl.RearrangeDocList();
            fl.PrintDocList();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(TaatWeightingPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

//            SearchRequest srq = queryingManager.newSearchRequest("1", sTerm);
//            srq.addMatchingModel(mModel, wModel);
        //         org.terrier.matching.daat.Full fl = new Full(index);
//            Request rq = (Request)srq;
//           System.out.println(sTerm + " " + entry.getTermId());
//            queryingManager.runPreProcessing(srq);
//            queryingManager.runMatching(srq);
        //   org.terrier.matching.daat.Full matching = org.terrier.matching.daat.Full(index);
    }

    public String obtainDocno(MetaIndex metaIndex, int iDocId) throws IOException {

        String docno;
        final String metaIndexDocumentKey = ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
        docno = metaIndex.getItem(metaIndexDocumentKey, iDocId);
        return docno;
    }

    /**
     * Create a new Matching instance based on the specified index
     */
    public TaatWeightingPrinter(Index index) {
        super(index);
        //System.out.print(getInfo());
    }

    /**
     * posting list manager opens and scores postings
     */
    PostingListManager plm;
    List<TermWeightedPosting> listTerms = null;
    Map<String, DocWeightedPosting> listDocs = null;

    public List<TermWeightedPosting> getListTerms() {
        return listTerms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return "matching.TaatWeightingPrinter";
    }

    @Override
    protected void initialisePostings(MatchingQueryTerms queryTerms) {

    }

    public ResultSet match(String queryNumber, MatchingQueryTerms queryTerms) throws IOException {
        final long starttime = System.currentTimeMillis();
        initialise(queryTerms);

        plm = new PostingListManager(index, super.collectionStatistics, queryTerms);
        if (MATCH_EMPTY_QUERY && plm.size() == 0) {
            // Check whether we need to match an empty query. If so, then return the existing result set.
            resultSet = new CollectionResultSet(collectionStatistics.getNumberOfDocuments());
            resultSet.setExactResultSize(collectionStatistics.getNumberOfDocuments());
            resultSet.setResultSize(collectionStatistics.getNumberOfDocuments());
            return resultSet;
        }
        //DO NOT prepare the posting lists for TAAT retrieval
        plm.prepare(false);
        listTerms = new ArrayList<TermWeightedPosting>() {
        };

        for (int i = 0; i < plm.size(); i++) {
            TermWeightedPosting termWeightedPosting = new TermWeightedPosting(i, plm.getTerm(i));
            assignScores(i, plm.getTerm(i),
                    (AccumulatorResultSet) resultSet, plm.getPosting(i), termWeightedPosting);
            listTerms.add(termWeightedPosting);
        }

        resultSet.initialise();
        this.numberOfRetrievedDocuments = resultSet.getExactResultSize();
        finalise(queryTerms);
        if (logger.isDebugEnabled()) {
            logger.debug("Time to match " + numberOfRetrievedDocuments + " results: " + (System.currentTimeMillis() - starttime) + "ms");
        }
        return resultSet;
    }

    protected void assignScores(int i, String pTerm,
            AccumulatorResultSet rs, final IterablePosting postings,
            TermWeightedPosting termWeightedPosting) throws IOException {
        int docid;
        double score;

        short mask = 0;
        if (i < 16) {
            mask = (short) (1 << i);
        }

        while (postings.next() != IterablePosting.EOL) {
            score = plm.score(i);
            docid = postings.getId();
            /// TODO MELTEM burda kaldım
            termWeightedPosting.AddDocWeight(docid, score);
            // logger.info("Docid=" + docid + " score=" + score);
// 			if ((!rs.scoresMap.contains(docid)) && (score > 0.0d))
//				numberOfRetrievedDocuments++;
//			else if ((rs.scoresMap.contains(docid)) && (score < 0.0d))
//				numberOfRetrievedDocuments--;
//
//			rs.scoresMap.adjustOrPutValue(docid, score, score);
//			rs.occurrencesMap.put(docid, (short)(rs.occurrencesMap.get(docid) | mask));
        }
    }
    
    public void WriteToDB(Connection con, int iRepoId, MetaIndex metaIndex ) throws IOException
    {
        for (int i = 0; i < getListTerms().size(); i++) {
                String sTerm = getListTerms().get(i).Term;
                InsertTerm(con, iRepoId, sTerm, i);
                logger.log(Priority.INFO, sTerm);
                int[] docIds = getListTerms().get(i).getDocWeightedMap().keys();
                double[] WeightedDocScores = getListTerms().get(i).getDocWeightedMap().getValues();
                for (int j = 0; j < docIds.length; j++) {
                    String docName = obtainDocno(metaIndex, docIds[j]);
                    InsertDoc(con, iRepoId, docName, docIds[j]);
                    InsertScore(con, iRepoId, docName, i, WeightedDocScores[j], sTerm);
                    logger.log(Priority.INFO, docName + "\t" + sTerm + "\t" + WeightedDocScores[j]);
                    System.out.println(docName + "\t" + sTerm + "\t" + WeightedDocScores[j]);
                }
            }
    }

    public void RearrangeDocList() throws IOException {
        listDocs = new HashMap<String, DocWeightedPosting>();
        
        for (int i = 0; i < listTerms.size(); i++) {
            String sTerm = listTerms.get(i).getTerm();
            int[] docIds = getListTerms().get(i).getDocWeightedMap().keys();
            double[] WeightedDocScores = getListTerms().get(i).getDocWeightedMap().getValues();
            for (int j = 0; j < docIds.length; j++) {
                String docName = obtainDocno(index.getMetaIndex(), docIds[j]);
                if (!listDocs.containsKey(docName)) {
                    listDocs.put(docName, new DocWeightedPosting((docIds[j]), docName));
                }
                listDocs.get(docName).TermWeightedMap.adjustOrPutValue(i, WeightedDocScores[j], WeightedDocScores[j]);
                //System.out.println(docName + "\t" + sTerm + "\t" + WeightedDocScores[j]);
            }
        }
    }

    public void PrintDocList() {
        Object[] keys = listDocs.keySet().toArray();
        int iLexiconSize = index.getLexicon().numberOfEntries();

        for (int i = 0; i < keys.length; i++) {
            String sDocName = keys[i].toString();
            System.out.print(sDocName + ";");
            DocWeightedPosting doc = listDocs.get(sDocName);
            for (int j = 0; j < iLexiconSize; j++) {
                if (doc.TermWeightedMap.contains(j)) {
                    System.out.print(doc.TermWeightedMap.get(j) + ";");
                    }
            else
                System.out.print(";");
            }
            System.out.println();
        }

    }

    public static void InsertScore(Connection con, int pRepoId,
            String pFileId, int pTermIntId, double wScore, String pTerm) {
        String sSql = "INSERT INTO rapidminer_repo.repo_doc_term_score("
                + "repo_id, file_id, term_int_id, w_score, term"
                + ") VALUES (" + pRepoId + ",'"
                + pFileId + "'," + pTermIntId + "," + wScore + ",'" + pTerm + "')";

        Statement st;
        try {
            st = (Statement) con.createStatement();
            st.executeUpdate(sSql);
        } catch (SQLException ex) {
            Logger.getLogger(TaatWeightingPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void InsertDoc(Connection con, int pRepoId,
            String pFileId, int pIntFileId) {
        String sSql = "SELECT FILE_ID FROM rapidminer_repo.repo_docs WHERE "
                + "repo_id = " + pRepoId + " and  file_id = '" + pFileId + "'";

        Statement st;
        try {
            st = (Statement) con.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sSql);
            if (rs == null || rs.next() == false) {
                sSql = "INSERT INTO rapidminer_repo.repo_docs("
                        + "repo_id, file_id, file_int_id"
                        + ") VALUES (" + pRepoId + ",'"
                        + pFileId + "'," + pIntFileId + ")";
                st = (Statement) con.createStatement();
                st.executeUpdate(sSql);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaatWeightingPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void InsertTerm(Connection con, int pRepoId,
            String pTerm, int pIntTermId) {
        String sSql = "INSERT INTO rapidminer_repo.repo_terms("
                + "REPO_ID, TERM, TERM_INT_ID"
                + ") VALUES (" + pRepoId + ",'"
                + pTerm + "'," + pIntTermId + ")";

        Statement st;
        try {
            st = (Statement) con.createStatement();
            st.executeUpdate(sSql);
        } catch (SQLException ex) {
            Logger.getLogger(TaatWeightingPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
