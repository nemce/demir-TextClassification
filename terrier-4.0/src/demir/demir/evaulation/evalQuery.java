/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.evaulation;

import java.util.ArrayList;

/**
 *
 * @author Meltem
 */
public class evalQuery {
    String QueryLabel;
    
    ArrayList aPredicitions = null;
    ArrayList aConditions = null;
    
    int tp = 0;
    int fn = 0;
    int fp = 0;
    int tn = 0;
    double precision = 0;
    double recall = 0;
    double f1 = 0;
    double accuracy = 0.0;
    
    
    public ArrayList getPredictionArray()
    {
        return aPredicitions;
    }
    
    public ArrayList getConditionsArray()
    {
        return aConditions;
    }
    
    public evalQuery(String name)
    {
        QueryLabel = name;
        aPredicitions = new ArrayList();
        aConditions = new ArrayList();
    }
    
    public void Calculate(int totalSampleSize)
    {
        for(int i = 0; i < aConditions.size(); i++)
        {
            if(aPredicitions.contains(aConditions.get(i)))
                tp++;
            else
                fn++;
        }
        
        for(int i = 0; i < aPredicitions.size(); i++)
        {
            if(aConditions.contains(aPredicitions.get(i)))
            {
            
            }
            else
            {
                fp++;
            }
        }
        
        
        tn = totalSampleSize - (tp+fp+fn);
        accuracy = (double)((tp + tn) / (double)totalSampleSize);
        if(tp+fp > 0) precision = (double)((double)tp / (tp+fp));
        if(tp+fn > 0) recall = (double)((double)tp / (tp+fn));
        f1 = 2 * (precision * recall) / (precision + recall);
    }
}
