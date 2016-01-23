/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.querying;

import org.terrier.applications.batchquerying.QuerySource;
import org.terrier.applications.batchquerying.TRECQuerying;
import org.terrier.matching.models.InL2;
import org.terrier.matching.models.queryexpansion.Bo1;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class QueryProcessor extends TRECQuerying{

    public QueryProcessor() {
        
    }
    
    
        public QueryProcessor(String queryText, String queryId, Index index) {
            super(queryText, queryId, index);
	
		
	}

  
        
        

    /**
	 * According to the given parameters, it sets up the correct matching class
	 * and performs retrieval for the given query.
	 * 
	 * @param queryId
	 *            the identifier of the query to process.
	 * @param query
	 *            the query to process.
	 * @param cParameter
	 *            double the value of the parameter to use.
	 * @param c_set
	 *            boolean specifies whether the parameter c is set.
	 */
	public SearchRequest processQuery(String queryId, String query,
			double cParameter, boolean c_set) {

		if (removeQueryPeriods && query.indexOf(".") > -1) {
			logger.warn("Removed . from query");
			query = query.replaceAll("\\.", " ");
		}

		if (logger.isInfoEnabled())
			logger.info(queryId + " : " + query);
 		SearchRequest srq = queryingManager.newSearchRequest(queryId, query);
		initSearchRequestModification(queryId, srq);
		String c = null;
		if (c_set) {
			srq.setControl("c", Double.toString(cParameter));
		} else if ((c = ApplicationSetup.getProperty("trec.c", null)) != null) {
			srq.setControl("c", c);
		}
		c = null;
		if ((c = srq.getControl("c")).length() > 0) {
			c_set = true;
		}
		srq.setControl("c_set", "" + c_set);

		srq.addMatchingModel(mModel, wModel);
		
		if (queryexpansion) {
			//if (srq.getControl("qemodel").length() == 0)
			srq.setControl("qemodel", defaultQEModel);
			srq.setControl("qe", "on");
		}
		
		preQueryingSearchRequestModification(queryId, srq);
			
		if (logger.isInfoEnabled())
			logger.info("Processing query: " + queryId + ": '" + query + "'");
		matchingCount++;
		queryingManager.runPreProcessing(srq);
                System.out.println(srq.getOriginalQuery());
                System.out.println(srq.getQuery());
//		queryingManager.runMatching(srq);
//		queryingManager.runPostProcessing(srq);
//		queryingManager.runPostFilters(srq);
//		resultsCache.add(srq);
		return srq;
	}
        
    /**
	 * Performs the matching using the specified weighting model from the setup
	 * and possibly a combination of evidence mechanism. It parses the file with
	 * the queries creates the file of results, and for each query, gets the
	 * relevant documents, scores them, and outputs the results to the result
	 * file.
	 * <p>
	 * <b>Queries</b><br />
	 * Queries are parsed from file, specified by the <tt>trec.topics</tt> property
	 * (comma delimited)
	 * 
	 * @param c
	 *            the value of c.
	 * @param c_set
	 *            specifies whether a value for c has been specified.
	 * @return String the filename that the results have been written to
         * Added By Meltem
         * Bu fonksiyon tek sorgu için result file oluşturmadan sorgu sonucunu
         * belirtilen formatta döndürür.
	 */
	public SearchRequest processOneQuery(double c, boolean c_set) {
		matchingCount = 0;
		querySource.reset();
		this.startingBatchOfQueries();
		final long startTime = System.currentTimeMillis();
		boolean doneSomeMethods = false;
		boolean doneSomeTopics = false;
		
		wModel = ApplicationSetup.getProperty("trec.model", InL2.class.getName());		
		defaultQEModel = ApplicationSetup.getProperty("trec.qe.model", Bo1.class.getName());
                

		String query = querySource.next();
       
		String qid = querySource.getQueryId();
			// process the query
		long processingStart = System.currentTimeMillis();
		SearchRequest srq = processQuery(qid, query, c, c_set);
		long processingEnd = System.currentTimeMillis();
		if (logger.isInfoEnabled())
                    logger.info("Time to process query: "
			+ ((processingEnd - processingStart) / 1000.0D));
			doneSomeTopics = true;
		querySource.reset();
		// after finishing with a batch of queries, close the result
		// file
		doneSomeMethods = true;

                return srq;
	}

}
