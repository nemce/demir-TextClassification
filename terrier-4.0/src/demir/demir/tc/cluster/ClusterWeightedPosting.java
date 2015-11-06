/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.cluster;

import demir.terrier.matching.DocWeightedPosting;

/**
 *
 * @author nmeltem
 */
public class ClusterWeightedPosting extends DocWeightedPosting{

    public ClusterWeightedPosting(int pDocId, String pDocName) {
        super(pDocId, pDocName);
    }
    
}
