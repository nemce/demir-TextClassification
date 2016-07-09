/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.matching.daat;

import java.util.Arrays;
import java.util.Collections;
import org.terrier.structures.postings.WritablePosting;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author nmeltem
 */
public class FeaturedCandidateResult extends
        org.terrier.matching.daat.CandidateResult {

    protected WritablePosting[] postings;
    protected double[] scores;
    protected double[] features;
    protected short[] occurences;

    public FeaturedCandidateResult(int id, int postingCount) {
        super(id);
        postings = new WritablePosting[postingCount];
        scores = new double[postingCount];
        occurences = new short[postingCount];
        features = new double[postingCount];;
    }

    public void setPosting(int term, WritablePosting p) {
        postings[term] = p;
    }

    public WritablePosting[] getPostings() {
        return postings;
    }

    /**
     * Increase the score by the specified amount.
     *
     * @param update Amount to increase document score by.
     */
    public void updateScore(int i, double update) {
        this.scores[i] += update;
    }
    
    public void UpdateScoreByDocWeight(double docWeight)
    {
        this.score = this.score * docWeight;
    }

    /**
     * Update the occurrence value of this result.
     *
     * @param update Mask to OR with current occurrence
     */
    public void updateOccurrence(int i, short update) {
        this.occurences[i] |= update;
    }

    public void updateFeature(int i, double update) {
        this.features[i] += update;
    }

    private void MinMaxNormalization() {

        double[] mm = GetMaxMin(scores);
        if (mm[1] - mm[0] != 0) {
            for (int i = 0; i < scores.length; i++) {
                scores[i] = (scores[i] - mm[0]) / (mm[1] - mm[0]);
            }
        }

        mm = GetMaxMin(features);
        if (mm[1] - mm[0] != 0) {
            for (int i = 0; i < features.length; i++) {
                features[i] = (features[i] - mm[0]) / (mm[1] - mm[0]);
            }
        }
    }

    private double GetVectorLength(double[] array) {
        double length = 0.0;
        for (int i = 0; i < array.length; i++) {
            length += array[i] * array[i];
        }
        return Math.sqrt(length);
    }

    private double[] GetMaxMin(double[] array) {
        DoubleSummaryStatistics stat = Arrays.stream(array).summaryStatistics();
        double[] MinMax = new double[2];
        MinMax[0] = stat.getMin();
        MinMax[1] = stat.getMax();

        return MinMax;
        /* ---------2----------------------------
         List b = Arrays.asList(ArrayUtils.toObject(array));
         System.out.println(Collections.min(b));
         System.out.println(Collections.max(b));
         */
    }

    public void CalculateScore1() {
        score = 0.0;
        occurrence = 0;
        MinMaxNormalization();
        for (int i = 0; i < scores.length; i++) {
            score += (scores[i] + Math.sqrt(features[i]));
            //score += scores[i];
            occurrence |= occurences[i];
        }
    }

    public void CalculateScore2() {
        score = 0.0;
        occurrence = 0;
        double scoreLength = GetVectorLength(scores);
        double featureLength = GetVectorLength(features);
        for (int i = 0; i < scores.length; i++) {
            score += (scores[i] / scoreLength);
            if (featureLength != 0.0) {
                score += (features[i] / featureLength);
            }
            //score += scores[i];
            occurrence |= occurences[i];
        }
    }
    
    /* WHM + SQRT(FEA) */
    
     public void CalculateScore3() {
        score = 0.0;
        occurrence = 0;
        for (int i = 0; i < scores.length; i++) 
        {
            score += (scores[i] + Math.sqrt(features[i]));
            //score += scores[i];
            occurrence |= occurences[i];
        }
    }
     
    // WHM * FEA 
     public void CalculateScore4() {
        score = 0.0;
        occurrence = 0;
        for (int i = 0; i < scores.length; i++) 
        {
            score += (scores[i] * features[i]);
            //score += scores[i];
            occurrence |= occurences[i];
        }
    }
     
     /// ICF için tasarlanmıştır
     /// ICFnin olumlu etkisini arttırmanın yöntemleri aranmıştır.
     // score * feature^2
      public void CalculateScore5() {
        score = 0.0;
        occurrence = 0;
        for (int i = 0; i < scores.length; i++) 
        {
            score += (scores[i] * features[i] * features[i]);
            //score += scores[i];
            occurrence |= occurences[i];
        }
    }
      
       public void CalculateScoreNormal() {
        score = 0.0;
        occurrence = 0;
        for (int i = 0; i < scores.length; i++) 
        {
            score += scores[i];
            occurrence |= occurences[i];
        }
    }

    /**
     * Returns the score of this result
     *
     * @return
     */
    @Override
    public double getScore() {
        return score;
    }

    /**
     * Returns the occurrence value of this result
     */
    public short getOccurrence() {
        return occurrence;
    }
}
