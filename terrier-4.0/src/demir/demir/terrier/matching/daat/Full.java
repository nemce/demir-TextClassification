/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.matching.daat;

import demir.terrier.structures.FeaturedLexiconEntry;
import it.unimi.dsi.fastutil.longs.LongHeapPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.PostingListManager;
import org.terrier.matching.ResultSet;
import org.terrier.matching.daat.CandidateResult;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.BasicLexiconEntry;
import org.terrier.structures.EntryStatistics;
import org.terrier.structures.Index;
import org.terrier.structures.IndexUtil;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.collections.MapEntry;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem Weighting Modelde kullanılacak yeni özelliklerin matching
 * aşamasında term EntryStatics'e eklenmesi için türetilmiştir.
 * InitializePostings Fonksiyonunda termlere ait diğer featureların yüklenmesini
 * sağlar. Bu Sınıf ile beraber TF_IDF_MI weighting Modeli Geliştirilmiştir.
 */
public class Full extends org.terrier.matching.daat.Full {

    /** posting list manager opens and scores postings */
	PostingListManager plm;
        
    Map<String, Double> MIValues = new HashMap<>();

    public Full(Index index) throws Exception {
        super(index);
        LoadFeaturesFromFile(ApplicationSetup.getProperty("demir.features.MI", null));
    }

    protected void initialisePostings(MatchingQueryTerms queryTerms) {

        // We purge the query terms not present in the lexicon and retrieve the information from the lexicon
        String[] queryTermStrings = queryTerms.getTerms();
        queryTermsToMatchList = new ArrayList<Map.Entry<String, LexiconEntry>>(queryTermStrings.length);
        for (String queryTerm : queryTermStrings) {
            LexiconEntry t = lexicon.getLexiconEntry(queryTerm);

            if (t != null) {
                //check if the term IDF is very low.
                if (IGNORE_LOW_IDF_TERMS && collectionStatistics.getNumberOfDocuments() < t.getFrequency()) {
                    logger.warn("query term " + queryTerm + " has low idf - ignored from scoring.");
                    continue;
                }
                // check if the term has weighting models
                WeightingModel[] termWeightingModels = queryTerms.getTermWeightingModels(queryTerm);
                if (termWeightingModels.length == 0) {
                    logger.warn("No weighting models for term " + queryTerm + ", skipping scoring");
                    continue;
                } 
                /// Added By Meltem 27122015
                FeaturedLexiconEntry tfle = new FeaturedLexiconEntry((BasicLexiconEntry) t);
                if(MIValues.containsKey(queryTerm))
                    tfle.SetMiValue(MIValues.get(queryTerm));
                else
                    tfle.SetMiValue(0.0);
                /// Added By Meltem 27122015
                queryTermsToMatchList.add(new MapEntry<String, LexiconEntry>(queryTerm, tfle));

            } else {
                logger.debug("Term Not Found: " + queryTerm);
            }
        }

        //logger.warn("queryTermsToMatchList = " + queryTermsToMatchList.size());
        int queryLength = queryTermsToMatchList.size();

        wm = new WeightingModel[queryLength][];
        for (int i = 0; i < queryLength; i++) {
            Map.Entry<String, LexiconEntry> termEntry = queryTermsToMatchList.get(i);
            String queryTerm = termEntry.getKey();
            LexiconEntry lexiconEntry = termEntry.getValue();
            //get the entry statistics - perhaps this came from "far away"
            EntryStatistics entryStats = queryTerms.getStatistics(queryTerm);
            //if none were provided with the query we seek the entry statistics query term in the lexicon
            if (entryStats == null) {
                entryStats = lexiconEntry;
				//save them as they may be useful for query expansion. HOWEVER ONLY IF we didnt
                //get the statistics from MQT in the first place
                queryTerms.setTermProperty(queryTerm, lexiconEntry);
            }

            // Initialise the weighting models for this term
            int numWM = queryTerms.getTermWeightingModels(queryTerm).length;
            wm[i] = new WeightingModel[numWM];
            for (int j = 0; j < numWM; j++) {
                wm[i][j] = (WeightingModel) queryTerms.getTermWeightingModels(queryTerm)[j].clone();
                wm[i][j].setCollectionStatistics(collectionStatistics);
                wm[i][j].setEntryStatistics(entryStats);
                wm[i][j].setRequest(queryTerms.getRequest());
                wm[i][j].setKeyFrequency(queryTerms.getTermWeight(queryTerm));
                IndexUtil.configure(index, wm[i][j]);
                wm[i][j].prepare();
            }
        }
    }

    private void LoadFeaturesFromFile(String FeaturesFileName) throws java.lang.Exception {
        if (FeaturesFileName.equals("")) {
            throw new UnsupportedOperationException("Invalid Features File");
        }
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(FeaturesFileName));
            while ((sCurrentLine = br.readLine()) != null) {
             String [] sTerm = sCurrentLine.split("\t");
             MIValues.put(sTerm[0], Double.parseDouble(sTerm[1]));
            }
            if (br != null) {
                    br.close();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    /** {@inheritDoc} */
	@SuppressWarnings("resource") //IterablePosting need not be closed
	@Override
	public ResultSet match(String queryNumber, MatchingQueryTerms queryTerms) throws IOException 
	{
		// The first step is to initialise the arrays of scores and document ids.
		initialise(queryTerms);
		plm = new PostingListManager(index, super.collectionStatistics, queryTerms);
		plm.prepare(true);
		
		// Check whether we need to match an empty query. If so, then return the existing result set.
		String[] queryTermStrings = queryTerms.getTerms();
		if (MATCH_EMPTY_QUERY && queryTermStrings.length == 0) {
			resultSet.setExactResultSize(collectionStatistics.getNumberOfDocuments());
			resultSet.setResultSize(collectionStatistics.getNumberOfDocuments());
			return resultSet;
		}
		
		//the number of documents with non-zero score.
		numberOfRetrievedDocuments = 0;
		
		// The posting list min heap for minimum selection
        LongPriorityQueue postingHeap = new LongHeapPriorityQueue();
		
		// The posting list iterator array (one per term) and initialization
		for (int i = 0; i < plm.size(); i++) {
			long docid = plm.getPosting(i).getId();
			assert(docid != IterablePosting.EOL);
			postingHeap.enqueue((docid << 32) + i);
		}
        boolean targetResultSetSizeReached = false;
        Queue<FeaturedCandidateResult> candidateResultList = new PriorityQueue<FeaturedCandidateResult>();
        int currentDocId = selectMinimumDocId(postingHeap);
        IterablePosting currentPosting = null;
        double threshold = 0.0d;
        //int scored = 0;
        
        while (currentDocId != -1)  {
            // We create a new candidate for the doc id considered
            System.out.println("Doc : " + currentDocId);
            FeaturedCandidateResult currentCandidate = makeCandidateResult(currentDocId, plm.size());
            
            int currentPostingListIndex = (int) (postingHeap.firstLong() & 0xFFFF);
                    int nextDocid;
            //System.err.println("currentDocid="+currentDocId+" currentPostingListIndex="+currentPostingListIndex);
            currentPosting = plm.getPosting(currentPostingListIndex); 
            //scored++;
            do {
            	double d = assignScore2(currentPostingListIndex, currentCandidate);
               // TODO MELTEM 
               // burayı değiştir
               // AssignScore FeaturedCandidateResultSet'e göre değiştir.
               //        cc.updateScore(score);
               //        cc.updateOccurrence((i < 16) ? (short)(1 << i) : 0);
               
//                System.out.println(currentPostingListIndex + "- " + 
//                        currentCandidate.getDocId() +  "- " +  
//                        currentCandidate.getScore() +  "- " + 
//                        currentCandidate.getOccurrence());
        
            	//assignScore(currentPostingListIndex, wm[currentPostingListIndex], currentCandidate, currentPosting);
            	long newDocid = currentPosting.next();
            	postingHeap.dequeueLong();
                if (newDocid != IterablePosting.EOL)
                    postingHeap.enqueue((newDocid << 32) + currentPostingListIndex);
                else if (postingHeap.isEmpty())
                    break;
                long elem = postingHeap.firstLong();
                currentPostingListIndex = (int) (elem & 0xFFFF);
                currentPosting = plm.getPosting(currentPostingListIndex);
                nextDocid = (int) (elem >>> 32);
            } while (nextDocid == currentDocId);
            
            
            
            if ((! targetResultSetSizeReached) || currentCandidate.getScore() > threshold) {
            	//System.err.println("New document " + currentCandidate.getDocId() + " with score " + currentCandidate.getScore() + " passes threshold of " + threshold);
        		candidateResultList.add(currentCandidate);
        		if (RETRIEVED_SET_SIZE != 0 && candidateResultList.size() == RETRIEVED_SET_SIZE + 1)
        		{
        			targetResultSetSizeReached = true;
        			candidateResultList.poll();
        			//System.err.println("Removing document with score " + candidateResultList.poll().getScore());
        		}
        		//System.err.println("Now have " + candidateResultList.size() + " retrieved docs");
        		threshold = candidateResultList.peek().getScore();
        	}
            currentDocId = selectMinimumDocId(postingHeap);
        }
        
        // System.err.println("Scored " + scored + " documents");
        plm.close();
        
        // Fifth, we build the result set
         //TODO Meltem 14022016
        // FeaturedResultSet'e göre düzeltilecek.
        // resultSet = makeResultSet(candidateResultList);
        
        numberOfRetrievedDocuments = resultSet.getScores().length;
        finalise(queryTerms);
		return resultSet;
	}
        
        /** assign the score for this posting to this candidate result.
	 * @param i which query term index this represents
	 * @param cc the candidate result object for this document
	 * @throws IOException
	 */
	protected void assignScore(final int i, CandidateResult cc) throws IOException
    {
        double score = plm.score(i);
        cc.updateScore(score);
        cc.updateOccurrence((i < 16) ? (short)(1 << i) : 0);
    }
        
        
        protected double assignScore2(final int i, CandidateResult cc) throws IOException
    {
        double score = plm.score(i);
        return score;
//        cc.updateScore(score);
//        cc.updateOccurrence((i < 16) ? (short)(1 << i) : 0);
    }
        
                
        protected FeaturedCandidateResult makeCandidateResult(int currentDocId, int postingCount) {
		return new FeaturedCandidateResult(currentDocId, postingCount);
	}

}
