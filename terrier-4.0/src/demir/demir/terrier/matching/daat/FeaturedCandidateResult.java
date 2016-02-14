/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.matching.daat;

import org.terrier.structures.postings.WritablePosting;

/**
 *
 * @author nmeltem
 */
public class FeaturedCandidateResult extends 
        org.terrier.matching.daat.CandidateResult {

	protected WritablePosting[] postings;
	
	public FeaturedCandidateResult(int id, int postingCount) {
		super(id);
		postings = new WritablePosting[postingCount];
	}
	
	public void setPosting(int term, WritablePosting p) {
		postings[term] = p;
	}

	public WritablePosting[] getPostings() {
		return postings;
	}
}
	
