/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.featureselection;

import static demir.featureselection.FeatureGenerator.FormatDouble;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Meltem
 */
public class C3MTermWeighting {
    
    public static double CalculateCij(double tfi, double collectionFrequencyOfi, double tfj, double lengthOfDock)
    {
      double val =  (tfi /collectionFrequencyOfi) * (tfj / lengthOfDock);
      return val;
    }
    
    
    public static void Print(
        HashMap<String, TermInfo> hmTerm) {
        
        for (Iterator it = hmTerm.keySet().iterator(); it.hasNext();) {
            String keyTerm = (String) it.next();
            System.out.print(keyTerm + "\t");
            TermInfo valueTerm = (TermInfo) hmTerm.get(keyTerm);

            //System.out.print("\t" + valueTerm.TermId);
            System.out.print("\t" + FormatDouble(valueTerm.getDecoupling() + 1.0 ));
            
            //System.out.print("\t" + FormatDouble(valueTerm.getTF()));
            //System.out.print("\t" + FormatDouble(valueTerm.getNt()));
            System.out.println();
        }
    }

}
