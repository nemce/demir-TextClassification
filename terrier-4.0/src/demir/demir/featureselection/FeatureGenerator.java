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
import org.terrier.matching.models.WeightingModelLibrary;

/**
 *
 * @author nmeltem
 */
public class FeatureGenerator {

    public static void FeatureGenerator(
        HashMap<String, ClassInfo> hmClass,
        HashMap<String, TermInfo> hmTerm) {
        double CorpusDocCnt = 0.0;
        double ClassDocFrequency = 0.0;
        double ClassDocTermOccurence = 0.0;
        
        for (Iterator it2 = hmClass.values().iterator(); it2.hasNext();) {
            
            ClassInfo ci = (ClassInfo) it2.next();
            CorpusDocCnt += ci.GetClassDocCnt();
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
                        
            // double CS_delta_ti  = 0.0; //CS&(ti)
            //double sum_rf = 0.0;
            //double weighted_sum_rf = 0.0;
            
            ///23.04.2016
            double sum_rf_icfbased = 0.0;
            double weighted_sum_rf_icfbased = 0.0;
            
            double sum_rf_recall = 0.0;
            double weighted_sum_rf_recall = 0.0;
            double sum_rf_precision = 0.0;
            double weighted_sum_rf_precision = 0.0;
            
            
            double icf = valueTerm.CalculateInverseClassFrequency();
            for (Iterator it2 = valueTerm.GetClasses().keySet().iterator(); it2.hasNext();) {
               String sClassLabel = (String) it2.next();

                // MI by Frequency
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
//                double N1N0 = TermFreq - N1N1;
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateMI2(N1N1, N0N1, N1N0, CorpusDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // MI by Frequency
                //double MI3 = hmTerm.get(keyTerm).CalculateMI3(N1N1, N0N1, N1N0, CorpusDocCnt, sClassLabel);
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
//                double MI = hmTerm.get(keyTerm).CalculateChiSquare(N1N1, N0N1, N1N0, CorpusDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // Chi by Frequency
                
                // MI4 by Frequency 1 + log mantığına göre hesaplama yapıldı.
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
//                double N1N0 = TermFreq - N1N1;
//                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1;
//                double MI = hmTerm.get(keyTerm).CalculateMI4(N1N1, N0N1, N1N0, CorpusDocCnt, sClassLabel);
//                hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // MI4 by Frequency
                
                
                // Inverse Class Frequency - Density Functions
                // nCk(ti) : # of frequency of term ti in Class Ck - N1N1
                // NCk : total # of docs in Ck
                // N : total # of doc in the corpus - CorpusDocCnt
                // C : total # of Classes
                
                // Function F1
                // Not : Bu değer inverse Class Density'e karşılık geliyor olmalı.
                // Ren & Sohrab (2013) makalede önce Ck&(ti)=nCk(ti) / NCk değerlerini hesaplamış 
                // ve sonra bulduğu değerleri toplayarak, CS&t(i) = Sum(Ck&(ti))
                // log(C/CS&t(i)) ile bir term için değer bulmuş.
                // Benim uyguladığım formülde ise sınıf bazında hesaplanmış  log(N/nCk(ti)) var.
                // double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel);
                // double MI = hmTerm.get(keyTerm).CalculateInverse(N1N1,CorpusDocCnt);
                // hmTerm.get(keyTerm).putMI(sClassLabel, MI);
                // Function F1
                
                // Function F2
                double nCk_ti = (double) valueTerm.GetClasses().get(sClassLabel);
               double NCk = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt();
//                double c_delta_ti = nCk_ti / NCk;
//                CS_delta_ti += c_delta_ti;
                // Function F2
                // Inverse Class Frequency - Density Functions
                
               // Function F3 OLMALIYDI
//                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel); // a
//                double N1N0 = TermFreq - N1N1;                                  // c
//                if(N1N0 < 1) N1N0 = 1;
//                double rf =  WeightingModelLibrary.log(2+N1N1/N1N0);
//                sum_rf += rf;
//                weighted_sum_rf += rf * NCk;
                // Function F3 OLMALIYDI
                
                
                double N1N1 = (double) valueTerm.GetClasses().get(sClassLabel); // a
                double N1N0 = TermFreq - N1N1;                                  // c
                double N0N1 = ((ClassInfo) hmClass.get(sClassLabel)).GetClassDocCnt() - N1N1; //b
                if(N1N0 < 1) N1N0 = 1;
                
                // Function F4 
                // Bu değer yanlış olmuş
                // icf zaten logu alınmış bir değer C/Ct(i) olmalıydı.
                double rf =  WeightingModelLibrary.log(2+(N1N1/N1N0) * icf);
                sum_rf_icfbased += rf;
                weighted_sum_rf_icfbased += rf * NCk;
                // Function F4 
                
                 // Function F5
                double rf_recall =  WeightingModelLibrary.log(2+(N1N1/(N1N1+N1N0)));
                sum_rf_recall += rf_recall;
                weighted_sum_rf_recall += rf_recall * NCk;
                // Function F5
                
                 // Function F5
                double rf_precision =  WeightingModelLibrary.log(2+(N1N1/(N1N1+N0N1)));
                sum_rf_precision += rf_precision;
                weighted_sum_rf_precision += rf_precision * NCk;
                // Function F5
            }
            
            //double Inverse_CS_delta_ti = WeightingModelLibrary.log(hmClass.size()/CS_delta_ti + 1);
            double term_rf_icfbased = sum_rf_icfbased / hmClass.size();
            double term_w_rf_icfbased = weighted_sum_rf_icfbased / CorpusDocCnt;
            
            double term_rf_recall = sum_rf_recall / hmClass.size();
            double term_w_rf_recall = weighted_sum_rf_recall / CorpusDocCnt;
            
            double term_rf_precision = sum_rf_precision / hmClass.size();
            double term_w_rf_precision = weighted_sum_rf_precision / CorpusDocCnt;
            
            
            System.out.println(keyTerm + "\t" + icf + 
               // "\t" + Inverse_CS_delta_ti +
                "\t" + term_rf_icfbased +
                "\t" + term_w_rf_icfbased +
                "\t" + term_rf_recall +
                "\t" + term_w_rf_recall +
                "\t" + term_rf_precision +
                "\t" + term_w_rf_precision
                );
        }
        //Print(hmClass, hmTerm);
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
