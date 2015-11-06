/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.terrier.matching;

import gnu.trove.TIntDoubleHashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author nmeltem
 */
public class TermWeightedPosting {
    int TermId;
    String Term = null;
    
    TIntDoubleHashMap docWeightedMap = null;

    public int getTermId() {
        return TermId;
    }

    public String getTerm() {
        return Term;
    }

    public TIntDoubleHashMap getDocWeightedMap() {
        return docWeightedMap;
    }
    
    public TermWeightedPosting(int pTermId, String pTerm)
    {
        TermId = pTermId;
        Term = pTerm;
        docWeightedMap = new TIntDoubleHashMap();
    }
    
    public void AddDocWeight(int pDocId, double pDocWeight)
    {
        docWeightedMap.put(pDocId, pDocWeight);
    }
}
