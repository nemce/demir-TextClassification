/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.featureselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.terrier.matching.models.WeightingModelLibrary;

/**
 *
 * @author nmeltem
 */
public class TermInfo {

    public static void main(String[] args) {
        TermInfo mi = new TermInfo(5);
        
        //mi.CalculateMI(49, 141, 27652, 774106, "AA");
        //mi.CalculateMI(0.0, 19.0, 369.0, 6010.0, "AA");
        //mi.CalculateChiSquare(49, 141, 27652, 801948, "AA");
        
        double k = mi.TwoBaseLog(24);
        double j = WeightingModelLibrary.log(24);
        
        k = mi.TwoBaseLog(2);
        j = WeightingModelLibrary.log(2);
        
    }

    int TermId;
    HashMap<String, Double> Classes;
    HashMap<String, Double> ClassOccurence;
    HashMap<String, Double> MI;
    double TermFrequency = 0.0;
    double TermOccurence = 0.0;
    // # of Classes term ti Occurs in
    double   c_ti = 0.0;

    public TermInfo(int TermId) {
        this.Classes = new HashMap<>();
        this.ClassOccurence = new HashMap<>();
        InitClasses();
        this.MI = new HashMap<>();
        this.TermId = TermId;
    }
    
    public TermInfo(int TermId, ArrayList  lsClass) {
        this.Classes = new HashMap<String, Double>();
        this.ClassOccurence = new HashMap<String, Double>();
        for(int i = 0; i < lsClass.size(); i++)
        {
            Classes.put((String) lsClass.get(i), 0.0);
            ClassOccurence.put((String) lsClass.get(i), 0.0);
        }
        this.MI = new HashMap<>();
        this.TermId = TermId;
    }

    private TermInfo() {
        this.Classes = new HashMap<>();
        this.ClassOccurence = new HashMap<>();
        InitClasses();
        this.MI = new HashMap<>();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double GetTermFrequency() {
        return TermFrequency;
    }

    public HashMap GetClasses() {
        return Classes;
    }
    
    public HashMap GetClassOccurences() {
        return ClassOccurence;
    }

    public HashMap GetMI() {
        return MI;
    }
    
    public void putMI(String classLabel, double val)
    {
        MI.put(classLabel, val);
    }

    private void InitClassesDummy1() {
        Classes.put("acq", 0.0);
        Classes.put("corn", 0.0);
        Classes.put("crude", 0.0);
        Classes.put("earn", 0.0);
        Classes.put("interest", 0.0);
    }
    
    //Reuters Cat7
    private void InitClasses() {
        Classes.put("acq", 0.0);
        Classes.put("corn", 0.0);
        Classes.put("crude", 0.0);
        Classes.put("earn", 0.0);
        Classes.put("interest", 0.0);
        Classes.put("ship", 0.0);
        Classes.put("trade", 0.0);
        
        ClassOccurence.put("acq", 0.0);
        ClassOccurence.put("corn", 0.0);
        ClassOccurence.put("crude", 0.0);
        ClassOccurence.put("earn", 0.0);
        ClassOccurence.put("interest", 0.0);
        ClassOccurence.put("ship", 0.0);
        ClassOccurence.put("trade", 0.0);
    }
    
    
    private void InitClasses20NewsGroup() {
        Classes.put("alt.atheism", 0.0);
        Classes.put("comp.graphics", 0.0);
        Classes.put("comp.os.ms-windows.", 0.0);
        Classes.put("comp.sys.ibm.pc.har", 0.0);
        Classes.put("comp.sys.mac.hardwa", 0.0);
        Classes.put("comp.windows.x", 0.0);
        Classes.put("misc.forsale", 0.0);
        Classes.put("rec.autos", 0.0);
        Classes.put("rec.motorcycles", 0.0);
        Classes.put("rec.sport.baseball", 0.0);
        Classes.put("rec.sport.hockey", 0.0);
        Classes.put("sci.crypt", 0.0);
        Classes.put("sci.electronics", 0.0);
        Classes.put("sci.med", 0.0);
        Classes.put("sci.space", 0.0);
        Classes.put("soc.religion.christ", 0.0);
        Classes.put("talk.politics.guns", 0.0);
        Classes.put("talk.politics.midea", 0.0);
        Classes.put("talk.politics.misc", 0.0);
        Classes.put("talk.religion.misc", 0.0);
        
        
        ClassOccurence.put("alt.atheism", 0.0);
        ClassOccurence.put("comp.graphics", 0.0);
        ClassOccurence.put("comp.os.ms-windows.", 0.0);
        ClassOccurence.put("comp.sys.ibm.pc.har", 0.0);
        ClassOccurence.put("comp.sys.mac.hardwa", 0.0);
        ClassOccurence.put("comp.windows.x", 0.0);
        ClassOccurence.put("misc.forsale", 0.0);
        ClassOccurence.put("rec.autos", 0.0);
        ClassOccurence.put("rec.motorcycles", 0.0);
        ClassOccurence.put("rec.sport.baseball", 0.0);
        ClassOccurence.put("rec.sport.hockey", 0.0);
        ClassOccurence.put("sci.crypt", 0.0);
        ClassOccurence.put("sci.electronics", 0.0);
        ClassOccurence.put("sci.med", 0.0);
        ClassOccurence.put("sci.space", 0.0);
        ClassOccurence.put("soc.religion.christ", 0.0);
        ClassOccurence.put("talk.politics.guns", 0.0);
        ClassOccurence.put("talk.politics.midea", 0.0);
        ClassOccurence.put("talk.politics.misc", 0.0);
        ClassOccurence.put("talk.religion.misc", 0.0);

    }

    public void AddClass(String sClassLabel) {
        if (Classes.containsKey(sClassLabel)) {
            Classes.put(sClassLabel, 1.0 + (double) Classes.get(sClassLabel));
        } else {
            Classes.put(sClassLabel, 1.0);
        }
    }

    public void AddClassOccurence(String sClassLabel, double termOccurence) {
        if (ClassOccurence.containsKey(sClassLabel)) {
            ClassOccurence.put(sClassLabel, termOccurence + (double) ClassOccurence.get(sClassLabel));
        } else {
            ClassOccurence.put(sClassLabel, termOccurence);
        }
    }

    public double CalculateTermFrequency() {

        TermFrequency = 0.0;
        try {
            for (Iterator it = Classes.values().iterator(); it.hasNext();) {
                TermFrequency += (double) it.next();
            }
        } catch (Exception eSys) {
            System.out.println("Error : " + TermId);
        }
        return TermFrequency;
    }

    public double CalculateTermOccurence() {

        TermOccurence = 0.0;
        try {
            for (Iterator it = ClassOccurence.values().iterator(); it.hasNext();) {
                TermOccurence += (double) it.next();
            }
        } catch (Exception eSys) {
            System.out.println("Error : " + TermId);
        }
        return TermOccurence;
    }
    
    /// #of Classes ti occurs
    public double CalculateC_ti()
    {
        c_ti = 0.0;
        try {
            for (Iterator it = ClassOccurence.values().iterator(); it.hasNext();) {
                if((double) it.next() > 0.0)
                {
                    c_ti+=1;
                }
            }
        } catch (Exception eSys) {
            System.out.println("Error : " + TermId);
        }
        return c_ti;
    }
    
    public double CalculateInverseClassFrequency()
    {
        CalculateC_ti();
        return CalculateInverse(c_ti, Classes.size());
    }


    public double CalculateMI(double N11, double N01, double N10, double N00, String Classlabel) {
        if (N11 == 0) {
            return 0;
        }
        double iCnt = N11 + N01 + N10 + N00;
        Double dVal = (N11 / iCnt * TwoBaseLog(iCnt * N11 / ((N11 + N10) * (N11 + N01))))
            + (N01 / iCnt * TwoBaseLog(iCnt * N01 / ((N01 + N00) * (N11 + N01))))
            + (N10 / iCnt * TwoBaseLog(iCnt * N10 / ((N10 + N11) * (N10 + N00))))
            + (N00 / iCnt * TwoBaseLog(iCnt * N00 / ((N01 + N00) * (N10 + N00))));

        return dVal;
    }

    // N11 A -  N10 B - N01 C - N00 D
    public double CalculateMI2(double N11, double N01, double N10, double Total, String Classlabel) {
        Double dVal;
        if (N11 == 0 || N01 == 0 || N10 ==0) {
            dVal = 0.0;
        } else {
            double N00 = Total - (N11 + N01 + N10);
            dVal =    (N11 / Total * TwoBaseLog(Total * N11 / ((N11 + N10) * (N11 + N01))))
                + (N01 / Total * TwoBaseLog(Total * N01 / ((N01 + N00) * (N11 + N01))))
                + (N10 / Total * TwoBaseLog(Total * N10 / ((N10 + N11) * (N10 + N00))))
                + (N00 / Total * TwoBaseLog(Total * N00 / ((N01 + N00) * (N10 + N00))));
        }
        //MI.put(Classlabel, dVal);
        return dVal;
    }

    /// Bu fonksiyon Class İndexing Based term weighting for automatic text Classification
    /// Makalesindeki sayfa 115 Table 2'de yer alan MI formülüne göre hesaplama yapar.
    /// Mutula information = log(A*N / (A+B)*(A+C))
    /// N11 A -  N10 B - N01 C - N00 D
    public double CalculateMI3(double N11, double N01, double N10, double Total, String Classlabel) {
        Double dVal;
        // 16.06.2018 BURASI HATALI OLABİLİR HEPSİ SIFIR İSE SIFIT OLMALI
        if (N11 == 0 || N01 == 0 || N10 ==0) {
            dVal = 0.0;
        } else {
            double N00 = Total - (N11 + N01 + N10);
            dVal = TwoBaseLog(Total * N11 / ((N11 + N10) * (N11 + N01)));
        }
        //MI.put(Classlabel, dVal);
        return dVal;
    }
    
    /// Added By Meltem idf formulündeki 1 + log değeri baz alınarak değiştirildi.
    /// Bu fonksiyon Class İndexing Based term weighting for automatic text Classification
    /// Makalesindeki sayfa 115 Table 2'de yer alan MI formülüne göre hesaplama yapar.
    /// Mutula information = log(A*N / (A+B)*(A+C))
    /// N11 A -  N10 B - N01 C - N00 D
    public double CalculateMI4(double N11, double N01, double N10, double Total, String Classlabel) {
        Double dVal;
        if (N11 == 0 || N01 == 0 || N10 ==0) {
            dVal = 0.0;
        } else {
            //dVal = TwoBaseLog(Total * N11 / ((N11 + N10) * (N11 + N01)));
            dVal = CalculateMI3(N11, N01, N10, Total, Classlabel);
        }
        return (1 + dVal);
    }
    
    public double CalculateChiSquare(double N11, double N01, double N10, double Total, String Classlabel)
    {
        double dVal;
        if (N11 == 0 || N01 == 0 || N10 ==0) {
            dVal = 0.0;
        } else {
            double N00 = Total - (N11 + N01 + N10);
            double E11 = (N11 + N10) * (N11 + N01) / Total;
            double E01 = (N01 + N00) * (N01 + N11) / Total;
            double E10 = (N10 + N11) * (N10 + N00) / Total;
            double E00 = (N00 + N01) * (N00 + N10) / Total;
            
            dVal =   ((N11 - E11) * (N11 - E11) / E11 ) + 
                ((N10 - E10) * (N10 - E10) / E10 ) +
                ((N01 - E01) * (N01 - E01) / E01 ) +
                ((N00 - E00) * (N00 - E00) / E00 );
        }
        //MI.put(Classlabel, dVal);
        return dVal;
    }
    
    public double CalculateInverse(double dVal, double Total)
    {
        if (dVal == 0) 
            return 0.0;
        //return TwoBaseLog(Total  / N11 + 1);
        return WeightingModelLibrary.log(Total/dVal + 1);
    }
    
    
    protected double TwoBaseLog(double d) {
        return Math.log10(d) / Math.log10(2);
    }




}
