/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author meltem
 */
public class TestClassificationByTopicFile {

    public void ProcessQueries(String sTopicfileName, String sResFile)
            throws Exception {
        try {
            IRBasedTextClassification ibtc = new IRBasedTextClassification();
            String sLine = "";
            String sWholeText = "";
            try {
                FileReader fr = new FileReader(sTopicfileName);
                BufferedReader br = new BufferedReader(fr);

                while ((sLine = br.readLine()) != null) {
                    sWholeText += sLine + "\r\n";
                }
                br.close();
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(IRBasedTextClassification.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (sWholeText != null && !"".equals(sWholeText)) {
                int iT1Index = sWholeText.indexOf("<TOP>");
                int iT2Index = sWholeText.indexOf("</TOP>");
                if (iT1Index == -1 || iT2Index == -1) {
                    break;
                }
                String sQ1 = sWholeText.substring(iT1Index, iT2Index + 8);
                sWholeText = sWholeText.replace(sQ1, "");
                IRMedicalQuery irQuery = new IRMedicalQuery();
                {
                    int iN1Index = sQ1.indexOf("<NUM>");
                    int iN2Index = sQ1.indexOf("<NUM>", iN1Index + 5);
                    String sQueryId = sQ1.substring(iN1Index + 5, iN2Index);
                    irQuery.setsQueryId(sQueryId);
                }
                {
                    int iN1Index = sQ1.indexOf("<FID>");
                    int iN2Index = sQ1.indexOf("<FID>", iN1Index + 5);
                    String sDocNo = sQ1.substring(iN1Index + 5, iN2Index);
                    irQuery.setsDocNo(sDocNo);
                }
                irQuery.setsDocTxt(sQ1);
                irQuery.setsQDType("Q");
                ibtc.AddQuery(irQuery);
            }
            ibtc.ProcessQueries(sResFile);
//            CalculateClassificationPerformance ccp = new CalculateClassificationPerformance();
//            ccp.CalculatePerformance(ibtc.getQrySet());
//            ccp.CalculateQueryBasedPerformance(ibtc.getQrySet());
        } catch (Exception ex) {
            Logger.getLogger(TestClassificationByTopicFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
