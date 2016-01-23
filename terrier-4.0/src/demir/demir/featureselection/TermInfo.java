/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.featureselection;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author nmeltem
 */
public class TermInfo {

    int TermId;
    HashMap<String, Double> Classes;
    HashMap<String, Double> MI;
    double TermFrequency = 0.0;

    public TermInfo(int TermId) {
        this.Classes = new HashMap<>();
        InitClasses();
        this.MI = new HashMap<>();
        this.TermId = TermId;
    }

//    private void InitClasses() {
//        Classes.put("acq", 0.0);
//        Classes.put("corn", 0.0);
//        Classes.put("crude", 0.0);
//        Classes.put("earn", 0.0);
//        Classes.put("interest", 0.0);
//        Classes.put("ship", 0.0);
//        Classes.put("trade", 0.0);
//    }
    
    private void InitClasses() {
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
    }

    public void AddClass(String sClassLabel) {
        if (Classes.containsKey(sClassLabel)) {
            Classes.put(sClassLabel, 1.0 + (double) Classes.get(sClassLabel));
        } else {
            Classes.put(sClassLabel, 1.0);
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

    public double GetTermFrequency() {
        return TermFrequency;
    }

    public HashMap GetClasses() {
        return Classes;
    }

    public HashMap GetMI() {
        return MI;
    }

    private TermInfo() {
        this.Classes = new HashMap<>();
        InitClasses();
        this.MI = new HashMap<>();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        TermInfo mi = new TermInfo(5);
        mi.CalculateMI(49, 141, 27652, 774106, "AA");
        mi.CalculateMI(0.0, 19.0, 369.0, 6010.0, "AA");
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

    public double CalculateMI2(double N11, double N01, double N10, double Total, String Classlabel) {
        Double dVal;
        if (N11 == 0 || N01 == 0 || N10 ==0) {
            dVal = 0.0;
        } else {
            double N00 = Total - (N11 + N01 + N10);
            dVal = (N11 / Total * TwoBaseLog(Total * N11 / ((N11 + N10) * (N11 + N01))))
                    + (N01 / Total * TwoBaseLog(Total * N01 / ((N01 + N00) * (N11 + N01))))
                    + (N10 / Total * TwoBaseLog(Total * N10 / ((N10 + N11) * (N10 + N00))))
                    + (N00 / Total * TwoBaseLog(Total * N00 / ((N01 + N00) * (N10 + N00))));
        }
        MI.put(Classlabel, dVal);
        return dVal;
    }

    protected double TwoBaseLog(double d) {
        return Math.log10(d) / Math.log10(2);
    }
    
    

}
