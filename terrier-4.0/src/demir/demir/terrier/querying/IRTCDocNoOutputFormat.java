/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.terrier.querying;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.terrier.matching.ResultSet;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.outputformat.OutputFormat;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 */
public class IRTCDocNoOutputFormat implements OutputFormat {
    
     public static class OneQueryOutputFormat
        {
            String sID = null;
            Double dValue = null;

            public String getID() {
                return sID;
            }

            public void setID(String sID) {
                this.sID = sID;
            }

            public Double getValue() {
                return dValue;
            }

            public void setValue(Double dValue) {
                this.dValue = dValue;
            }

            public OneQueryOutputFormat(String sInput, Double dInput)
            {
                sID = sInput;
                dValue = dInput;
            }
        }
     
    java.util.ArrayList<OneQueryOutputFormat> ls;

    public ArrayList<OneQueryOutputFormat> getLs() {
        return ls;
    }
    
	Index index;
	/** The logger used */
	protected static final Logger logger = Logger.getLogger(IRTCDocNoOutputFormat.class);
	
	/** Create a TRECDocnoOutputFormat using the specified index for
	 * looking up the docnos */
	public IRTCDocNoOutputFormat(Index _index) {
		//System.err.println("new TRECDocnoOutputFormat created");
		this.index = _index;
                ls = new ArrayList<OneQueryOutputFormat>();
	}
	
	/** method which extracts the docnos for the prescribed resultset */
	protected String[] obtainDocnos(final String metaIndexDocumentKey
               , final SearchRequest q, final ResultSet set
                        ) throws IOException
	{
		String[] docnos;
		if (set.hasMetaItems(metaIndexDocumentKey)) {
			docnos = set.getMetaItems(metaIndexDocumentKey);
		} else {
			final MetaIndex metaIndex = index.getMetaIndex();
			docnos = metaIndex.getItems(metaIndexDocumentKey, set.getDocids());
		}
		return docnos;
	}

	/**
	 * Prints the results for the given search request, using the specified
	 * destination.
	 * 
	 * @param pw
	 *            PrintWriter the destination where to save the results.
	 * @param q
	 *            SearchRequest the object encapsulating the query and the
	 *            results.
	 */
	public void printResults(final PrintWriter pw, final SearchRequest q,
			String method, String iteration, int _RESULTS_LENGTH) throws IOException {
		final ResultSet set = q.getResultSet();
		final String metaIndexDocumentKey = ApplicationSetup.getProperty("trec.querying.outputformat.docno.meta.key", "docno");
		final double[] scores = set.getScores();
		if (set.getResultSize() == 0) {
			logger.warn("No results retrieved for query " + q.getQueryID());
			return;
		}
		String[] docnos = obtainDocnos(metaIndexDocumentKey, q, set);
		
		final int maximum = _RESULTS_LENGTH > set.getResultSize()
				|| _RESULTS_LENGTH == 0 ? set.getResultSize()
				: _RESULTS_LENGTH;
		logger.debug("Writing " + maximum + " results for query " + q.getQueryID());
				
		// if the minimum number of documents is more than the
		// number of documents in the results, aw.length, then
		// set minimum = aw.length

		// if (minimum > set.getResultSize())
		// minimum = set.getResultSize();
		//final String iteration = ITERATION + "0";
		
		// the results are ordered in desceding order
		// with respect to the score.
		for (int i = 0; i < maximum; i++) {
			if (scores[i] == Double.NEGATIVE_INFINITY)
				continue;
                        OneQueryOutputFormat output = new OneQueryOutputFormat(docnos[i], scores[i]);
                        ls.add(output);

		}
	}
}

