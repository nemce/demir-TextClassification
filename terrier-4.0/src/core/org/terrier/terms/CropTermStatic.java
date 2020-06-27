/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terrier.terms;

import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author Meltem
 */
public final class CropTermStatic implements TermPipeline {

	/** Maximum length a term can be */ 
	protected static final int maxLen = 5;
	/** The next object in the term pipeline */	
	protected final TermPipeline next;

	
	/** Creates a new CropTerm pipeline object, which can be used in the 
	  * term pipeline 
	  * @param _next The next termpipeline object to pass the term onto.
	  */
	public CropTermStatic(TermPipeline _next)
	{
		this.next = _next;
	}
	
	/**
	 * Reduces the term to the maximum allowed size for this indexing run
	 * @param t String the term to check the length of.
	 */
	public void processTerm(String t)
	{
		if (t == null)
			return;
		if(t.length() > maxLen)
			t = t.substring(0,maxLen);	
		next.processTerm(t);
	}
	
	/**
	 * Implements the  default operation for all TermPipeline subclasses;
	 * By default do nothing.
	 * This method should be overrided by any TermPipeline that want to implements doc/query
	 * oriented lifecycle.
	 * @return return how the reset has gone
	 */
	public boolean reset() {
		return next!=null ? next.reset() : true;
	}
}
