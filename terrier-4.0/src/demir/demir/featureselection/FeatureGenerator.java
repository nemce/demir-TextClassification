/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.featureselection;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import joptsimple.util.KeyValuePair;

/**
 *
 * @author nmeltem
 */
public class FeatureGenerator {

    public static void FeatureGenerator(
            HashMap<String, ClassInfo> hmClass,
            HashMap<String, TermInfo> hmTerm) {
        double ClassDocCnt = 0.0;
        for (Iterator it2 = hmClass.values().iterator(); it2.hasNext();) {
            ClassDocCnt += ((ClassInfo) it2.next()).docCnt;
        }

        for (Iterator it = hmTerm.keySet().iterator(); it.hasNext();) {
            String keyTerm = (String) it.next();
            TermInfo valueTerm = (TermInfo) hmTerm.get(keyTerm);
            //System.out.println(keyTerm);
            double TermFreq = valueTerm.CalculateTermFrequency();
            for (Iterator it2 = valueTerm.GetClasses().keySet().iterator(); it2.hasNext();) {
                String sClassLabel = (String) it2.next();
                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
                double N1N0 = TermFreq - N1N1;
                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocFrequency() - N1N1;
                double MI = hmTerm.get(keyTerm).CalculateMI2(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
                
                //double MI3 = hmTerm.get(keyTerm).CalculateMI3(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
//                 System.out.println(sClassLabel  + " " + MI
//                         + " " + N1N1 + " " + N1N0 + " " + N0N1 + " " +  ClassDocCnt);
//               
            }
        }
        Print(hmClass, hmTerm);
    }

    public static void Print(
            HashMap<String, ClassInfo> hmClass,
            HashMap<String, TermInfo> hmTerm) {
        for (Iterator it = hmTerm.keySet().iterator(); it.hasNext();) {
            String keyTerm = (String) it.next();
            System.out.print(keyTerm + "\t");

            TermInfo valueTerm = (TermInfo) hmTerm.get(keyTerm);
            double dTotalMI = 0.0;
            for (Iterator it2 = valueTerm.GetMI().keySet().iterator(); it2.hasNext();) {
                String sClassLabel = (String) it2.next();
                double dValCalc = (double) valueTerm.GetMI().get(sClassLabel);
                dTotalMI += dValCalc;
                //System.out.print(sClassLabel + "\t" + dValCalc + "\t");
                System.out.print(sClassLabel + "\t" + FormatDouble(dValCalc) + "\t");
            }
            // System.out.print("Total\t" + dTotalMI + "\t");
            System.out.print("Totalf\t" + FormatDouble(dTotalMI));
            System.out.println();
        }
    }
    
    public static String FormatDouble(double d)
    {
        DecimalFormat f = new DecimalFormat("##.0000000");
        return f.format(d);
    }
}
