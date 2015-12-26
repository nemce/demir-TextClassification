/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.matching.daat;

import java.util.ArrayList;
import java.util.Map;
import org.terrier.matching.MatchingQueryTerms;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.EntryStatistics;
import org.terrier.structures.Index;
import org.terrier.structures.IndexUtil;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.collections.MapEntry;

/**
 *
 * @author nmeltem
 */
public class Full extends org.terrier.matching.daat.Full{

    public Full(Index index) {
        super(index);
    }
    
    protected void initialisePostings(MatchingQueryTerms queryTerms)
	{
		
		// We purge the query terms not present in the lexicon and retrieve the information from the lexicon
		String[] queryTermStrings = queryTerms.getTerms();
		queryTermsToMatchList = new ArrayList<Map.Entry<String,LexiconEntry>>(queryTermStrings.length);
		for (String queryTerm: queryTermStrings) {
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
					logger.warn("No weighting models for term " + queryTerm +", skipping scoring");
					continue;
				}
				queryTermsToMatchList.add(new MapEntry<String, LexiconEntry>(queryTerm, t));
			}
			else
				logger.debug("Term Not Found: " + queryTerm);			
		}

		//logger.warn("queryTermsToMatchList = " + queryTermsToMatchList.size());
		int queryLength = queryTermsToMatchList.size();
		
		wm = new WeightingModel[queryLength][];
		for (int i = 0; i < queryLength; i++) 
		{
			Map.Entry<String, LexiconEntry> termEntry    = queryTermsToMatchList.get(i);
			String 							queryTerm    = termEntry.getKey();
			LexiconEntry 					lexiconEntry = termEntry.getValue();
			//get the entry statistics - perhaps this came from "far away"
			EntryStatistics entryStats = queryTerms.getStatistics(queryTerm);
			//if none were provided with the query we seek the entry statistics query term in the lexicon
			if (entryStats == null)
			{
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
    
}