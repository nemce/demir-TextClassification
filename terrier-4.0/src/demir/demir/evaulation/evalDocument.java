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
public class evalDocument {
    
    String docId = null;
    
    ArrayList aPredicitions = null;
    ArrayList aConditions = null;
    
    public ArrayList getPredictionArray()
    {
        return aPredicitions;
    }
    
    public ArrayList getConditionsArray()
    {
        return aConditions;
    }
    
    public evalDocument(String docId)
    {
        this.docId = docId;
        aPredicitions = new ArrayList();
        aConditions = new ArrayList();
    }
}
