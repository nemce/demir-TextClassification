/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terrier.matching.models;


/*
27.12.2018
Bu sınıf Decoupling değeri 1'den küçük olduğu için 
tf değerini dokuman uzunluğu ile normalize etmek için eklenmiştir.
*/
/**
 *
 * @author Meltem
 */
public class DL_Norm_TF extends WeightingModel{

    protected static final long serialVersionUID = 1L;

	/** model name */
	protected static final String name = "DL_Normalized_TF";
        
        public DL_Norm_TF() {
		super();
	}

	public DL_Norm_TF(double b) {
		this();
	}
        
        public final String getInfo() {
		return name;
	}

	public final double score(double tf, double docLength) {
		return tf / docLength;
	}

	public final double score(
		double tf,
		double docLength,
		double documentFrequency,
		double termFrequency,
		double keyFrequency) 
	{
		return tf / docLength;
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
