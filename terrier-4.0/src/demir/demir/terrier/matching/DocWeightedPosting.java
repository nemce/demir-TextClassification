/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.terrier.matching;

import gnu.trove.TIntDoubleHashMap;

/**
 *
 * @author nmeltem
 */
public class DocWeightedPosting {
    
    int docId;
    String docName;
    
    TIntDoubleHashMap TermWeightedMap = null;
    
    public DocWeightedPosting(int pDocId, String pDocName)
    {
        docId = pDocId;
        docName = pDocName;
        TermWeightedMap = new TIntDoubleHashMap();
    }
    
    public void AddDocWeight(int pTermId, double pTermWeight)
    {
        TermWeightedMap.put(pTermId, pTermWeight);
    }

    public Object getTermWeightedMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
