/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terrier.matching.models;

import demir.terrier.structures.FeaturedLexiconEntry;
import org.terrier.structures.postings.Posting;

/**
 *
 * @author nmeltem
 */
public class TF_IDF_MI  extends TF_IDF{
    
    double MI = 0.0;
    /**
	 * Returns score
	 * @param p
	 * @return score
	 */
	public double score(Posting p) {
		return this.score(p.getFrequency(), p.getDocumentLength());
	}
        
        /**
	 * Uses TF_IDF to compute a weight for a term in a document.
	 * @param tf The term frequency of the term in the document
	 * @param docLength the document's length
	 * @return the score assigned to a document with the given 
	 *		 tf and docLength, and other preset parameters
	 */
    @Override
	public double score(double tf, double docLength) {
		double Robertson_tf = k_1*tf/(tf+k_1*(1-b+b*docLength/averageDocumentLength));
		double idf = WeightingModelLibrary.log(numberOfDocuments/documentFrequency+1);
		//return (keyFrequency * Robertson_tf * idf) + (MI * 1.618);
                //return (keyFrequency * Robertson_tf * idf) + (MI * 2.0);
                //return MI;
                //return Math.sqrt(keyFrequency * Robertson_tf * idf) + Math.sqrt(MI * 1.618);
                return (keyFrequency * Robertson_tf * idf) + Math.sqrt(MI);
                //return Math.sqrt(keyFrequency * Robertson_tf * idf) + Math.sqrt(MI * 2.0);
                //return (keyFrequency * Robertson_tf * idf) * MI;	
}
        
        /**
	 * prepare
	 */
	public void prepare() {
		averageDocumentLength = cs.getAverageDocumentLength();
		numberOfDocuments = (double)cs.getNumberOfDocuments();
		i.setNumberOfDocuments(numberOfDocuments);
		numberOfTokens = (double)cs.getNumberOfTokens();
		numberOfUniqueTerms = (double)cs.getNumberOfUniqueTerms();
		numberOfPointers = (double)cs.getNumberOfPointers();
		documentFrequency = (double)getOverflowed(es.getDocumentFrequency());
		termFrequency = (double)getOverflowed(es.getFrequency());	
                MI = (double)(((FeaturedLexiconEntry)es).GetMiValue());
	}

}
