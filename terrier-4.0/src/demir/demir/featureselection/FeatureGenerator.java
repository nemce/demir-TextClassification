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
        double ClassDocFrequency = 0.0;
        double ClassDocTermOccurence = 0.0;
        
        for (Iterator it2 = hmClass.values().iterator(); it2.hasNext();) {
            
            ClassInfo ci = (ClassInfo) it2.next();
            ClassDocCnt += ci.GetClassDocCnt();
            ClassDocFrequency += ci.GetClassDocFrequency();
            ClassDocTermOccurence += ci.GetClassTermDocOccurence();
        }
        
        //System.out.print("MI By Occurence");
        //System.out.print("ChiSquare");
        for (Iterator it = hmTerm.keySet().iterator(); it.hasNext();) {
            String keyTerm = (String) it.next();
            TermInfo valueTerm = (TermInfo) hmTerm.get(keyTerm);
            //System.out.println(keyTerm);
            double TermFreq = valueTerm.CalculateTermFrequency();
            double TermOccurence = valueTerm.CalculateTermOccurence();
            
            for (Iterator it2 = valueTerm.GetClasses().keySet().iterator(); it2.hasNext();) {
                String sClassLabel = (String) it2.next();

                // MI by Frequency
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
//                double N1N0 = TermFreq - N1N1;
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateMI2(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // MI by Frequency
                //double MI3 = hmTerm.get(keyTerm).CalculateMI3(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
//                 System.out.println(sClassLabel  + " " + MI
//                         + " " + N1N1 + " " + N1N0 + " " + N0N1 + " " +  ClassDocCnt);
            
                
                
//                // MI by Occurence
//                double N1N1 = (double) valueTerm.GetClassOccurences().get(sClassLabel);
//                double N1N0 = TermOccurence - N1N1;
//                /// TODO Meltem ClassDocFrequency ve ClassDocCnt yerine ne koyacağını bulmalısın.
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassTermDocOccurence() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateMI2(N1N1, N0N1, N1N0, ClassDocTermOccurence, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
//                // MI by Occurence
                
                 // Chi by Frequency
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
//                double N1N0 = TermFreq - N1N1;
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateChiSquare(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // Chi by Frequency
                
                // MI4 by Frequency 1 + log mantığına göre hesaplama yapıldı.
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
//                double N1N0 = TermFreq - N1N1;
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateMI4(N1N1, N0N1, N1N0, ClassDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // MI4 by Frequency
                
                
                // Inverse Class Frequency
                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
                double MI = hmTerm.get(keyTerm).CalculateInverse(N1N1,ClassDocCnt, sClassLabel);
                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // Inverse Class Frequency
                
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
            double dTotalDocCnt = 0.0;
            double dWeightedTotalMI = 0.0;
            double dClassCnt = hmClass.size();
            for (Iterator it2 = valueTerm.GetMI().keySet().iterator(); it2.hasNext();) {
                String sClassLabel = (String) it2.next();
                double dValCalc = (double) valueTerm.GetMI().get(sClassLabel);
                double dClassDocCnt =  hmClass.get(sClassLabel).GetClassDocCnt();
                dTotalMI += dValCalc;
                dWeightedTotalMI += dValCalc * dClassDocCnt;
                dTotalDocCnt += dClassDocCnt;
                //System.out.print(sClassLabel + "\t" + dValCalc + "\t");
                //System.out.print(sClassLabel + "\t" + FormatDouble(dValCalc) + "\t");
            }
            // System.out.print("Total\t" + dTotalMI + "\t");
            System.out.print("Totalf\t" + FormatDouble(dTotalMI));
            System.out.print("\tAverageTotalf\t" + FormatDouble(dTotalMI / dClassCnt));
            System.out.print("\tWeightedTotalf\t" + FormatDouble(dWeightedTotalMI));
            System.out.print("\tWeightedAverageTotalf\t" + FormatDouble(dWeightedTotalMI / dTotalDocCnt));
            System.out.println();
        }
    }

    public static String FormatDouble(double d) {
        DecimalFormat f = new DecimalFormat("##.0000000");
        return f.format(d);
    }
}
