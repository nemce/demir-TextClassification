/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terrier.matching.models;

/**
 *
 * @author nmeltem
 */
public class Dummy extends WeightingModel {
	private static final long serialVersionUID = 1L;
	/** model name */
	private static final String name = "Dummy";

	public Dummy() {
		super();
	}

	public Dummy(double b) {
		this();
	}

	public final String getInfo() {
		return name;
	}

	public final double score(double tf, double docLength) {
		return 1;
	}

	public final double score(
		double tf,
		double docLength,
		double documentFrequency,
		double termFrequency,
		double keyFrequency) 
	{
		return 1;
	}

	/**
	 * Sets the b parameter to ranking formula
	 * @param b the b parameter value to use.
	 */
	public void setParameter(double b) {
	}


	/**
	 * Returns the b parameter to the ranking formula as set by setParameter()
	 */
	public double getParameter() {
		return 0;
	}
}
