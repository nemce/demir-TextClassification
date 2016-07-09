/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.classification;

import demir.dbconnection.ImportToDB;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import demir.terrier.querying.IRTCDocNoOutputFormat;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nefise
 */
public class IRBasedTextClassification {

    //Connection con = null;
    HashMap<String, IRMedicalQuery> QrySet = new HashMap<String, IRMedicalQuery>();
    ClassificationParameters clsPrm = null;
    Session session = null;

    public HashMap<String, IRMedicalQuery> getQrySet() {
        return QrySet;
    }
    
    public ClassificationParameters getClsPrm() {
        return clsPrm;
    }

    public IRBasedTextClassification() {
        try {
            ClassificationParameters pclsPrm = new ClassificationParameters();
            this.clsPrm = pclsPrm;
            InitConnections();
            // TODO MELTEM 04 07 2015
            // ArrangePrmFromDB yapısını değiştir.
            // TODO MELTEM 15 11 2015
            // Sadece Reranking uygulanacağı durumda çalışmasını sağla
            // 
            //pclsPrm.ArrangePrmFromDB();
            // Sonuçların veri tabanına yazılması istenir ise 
            // Sistemde Run oluşturulur.
            if(clsPrm.WriteClassificationRes.equals("DB"))
            {
                clsPrm.RunId = ImportToDB.InsertRun(session, clsPrm);
            } else {
            }
        } catch (Exception ex) {
            Logger.getLogger(IRBasedTextClassification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void InitConnections() {
        if (clsPrm.getLabelSearchType().equals(clsPrm.LABEL_SEARCH_TYPE_DB)) {
            session = demir.tc.irbased.hibernate.connection.ConnectToServer.Connect();
        }
    }

    public void CloseConnections() {
         if (clsPrm.getLabelSearchType().equals(clsPrm.LABEL_SEARCH_TYPE_DB) & session != null) {
            demir.tc.irbased.hibernate.connection.ConnectToServer.Disconnect(session);
        }
    }

    /**
     *
     * @param sQueryId
     * @param sQueryText
     * @param sResFile
     * @param ls
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    public String ProcessQuery(String sQueryId, String sQueryText,
            String sResFile,
            java.util.ArrayList<demir.terrier.querying.IRTCDocNoOutputFormat.OneQueryOutputFormat> ls,
            String[] keys, Double[] values) throws Exception {
        try {
            IRMedicalQuery irQuery = new IRMedicalQuery();
            irQuery.setsQueryId(sQueryId);
            irQuery.setsDocTxt(sQueryText);
            irQuery.setsQDType("Q");
            AddQuery(irQuery);
            //QrySet.put(irQuery.getsQueryId(), irQuery);
            long startLoading = System.currentTimeMillis();
            if (clsPrm.isUseResultFile() == true) {
                ReadRetrievalResults(sResFile);
            } else {
                ReadRetrievalResults(sQueryId, ls);
            }
            long endLoading = System.currentTimeMillis();
            
            Map<String, Double> CategoryFeas = demir.terrier.utility.FeatureLoader.LoadFeaturesFromFile(
           ApplicationSetup.getProperty("demir.features.Category", ""));
             
//            System.out.println("time to intialise index : "
//            + ((endLoading - startLoading) / 1000.0D));
            AssignClassLabels(CategoryFeas);

            /// TODO MELTEM hem Key Hem Value dönmesi sağlanacak
            keys = new String[irQuery.getListICDAssignedKeys().size()];
            values = new Double[irQuery.getListICDAssignedKeys().size()];
            String[] keysDefinition = new String[irQuery.getListICDAssignedKeys().size()];

            for (int i = 0; i < irQuery.getListICDAssignedKeys().size(); i++) {
                keys[i] = irQuery.getListICDAssignedKeys().get(i);
                values[i] = irQuery.getListICDAssignedValues().get(i);

                //// TREC için değiştirdim
                //if(!bSearchBykeyFile)
                //    keysDefinition[i] =  demir.dbconnection.ConnectMySqlServer.SelectDefinitionOfICD(con, keys[i]);
            }


            String sResult = "";
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    //sResult += keys[i] + "\t" + values[i] + "\t" + keysDefinition[i] + "\r\n";
                    //// CLEF için değiştirdim
                    sResult += keys[i] + "\t" + values[i] + "\t";
                }
                return sResult;
            } else {
                return "Sonuç Bulunamadı " + sResFile;
            }
        } catch (Exception ex) {
            Logger.getLogger(IRBasedTextClassification.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public void ProcessQueries(String sResFile)
            throws Exception {
        ReadRetrievalResults(sResFile);
        AssignClassLabels(null);
    }

    public void AddQuery(IRMedicalQuery irQuery) throws Exception {
        irQuery.setCollectionId(clsPrm.CollectionId);
        //irQuery.SelRecLabelbyDocId(clsPrm, session);
        QrySet.put(irQuery.getsQueryId(), irQuery);
    }

    private void ReadRetrievalResults(String sResFile)
            throws FileNotFoundException, IOException, Exception {
        BufferedReader br = new BufferedReader(new FileReader(sResFile));
        String line = null;
        try {
            line = br.readLine();
            while (line != null) {
                String sTempLine = line;
                int iN1Index = sTempLine.indexOf(' ');
                String sQueryId = sTempLine.substring(0, iN1Index);
                if (QrySet.get(sQueryId).getListQRes().size() < clsPrm.MaxRetDocSize) {
                    sTempLine = sTempLine.substring(iN1Index + 4, sTempLine.length());
                    iN1Index = sTempLine.indexOf(' ');
                    String sDocId = sTempLine.substring(0, iN1Index);
                    sTempLine = sTempLine.substring(iN1Index + 1, sTempLine.length());
                    iN1Index = sTempLine.indexOf(' ');
                    sTempLine = sTempLine.substring(iN1Index + 1, sTempLine.length());
                    iN1Index = sTempLine.indexOf(' ');
                    //double dVal = Double.parseDouble(sTempLine.substring(0, iN1Index).replace('.', ','));
                    double dVal = Double.parseDouble(sTempLine.substring(0, iN1Index));
                    IRMedicalQuery irQuery = new IRMedicalQuery();
                    irQuery.setCollectionId(clsPrm.CollectionId);
                    irQuery.setsDocNo(sDocId);
                    irQuery.SelRecLabelbyDocId(clsPrm, session);
                    irQuery.setsRetrievalScore(dVal);
                    QrySet.get(sQueryId).getListQRes().add(irQuery);
                }
                line = br.readLine();
            }
        } catch (Exception eSys) {
            System.out.println(eSys.getMessage());
        } finally {
            br.close();
        }
    }
    
     private void ReadRetrievalResults(
            String QueryId,
            java.util.ArrayList<IRTCDocNoOutputFormat.OneQueryOutputFormat> ls)
            throws FileNotFoundException, IOException, Exception {
        ArrayList<String> listAllICD = new ArrayList<String>();

        try {
            
            ApplyNormalization(ls);
            
            int iIndex = 0;
            while (iIndex < ls.size()
                    & QrySet.get(QueryId).getListQRes().size() < clsPrm.getMaxRetDocSize() /// Maksimum dokuman sayısına erişince okumayı bırakır.
                   ) 
            {
                String sDocId = ls.get(iIndex).getID();
                double dVal = ls.get(iIndex).getValue();
                
                IRMedicalQuery irQuery = new IRMedicalQuery();
                irQuery.setCollectionId(clsPrm.CollectionId);
                irQuery.setsDocNo(sDocId);
                long startLoading = System.currentTimeMillis();
                irQuery.SelRecLabelbyDocId(clsPrm, session);
                long endLoading = System.currentTimeMillis();
//                System.out.println("time to intialise index : "
//                 + ((endLoading - startLoading) / 1000.0D));
                irQuery.setsRetrievalScore(dVal);
                QrySet.get(QueryId).getListQRes().add(irQuery);
                if(CheckIfLabelCountReached(listAllICD, irQuery, clsPrm.getMaxKLabelReached()))
                {
                    break; /// K sınıfa erişince dokuman okumayı bırakır.
                }
                iIndex++;
            }
        } catch (Exception eSys) {
            System.out.println(eSys.getMessage());
        } finally {
            //demir.dbconnection.ConnectMySqlServer.Disconnect(con);
        }
    }

    
    private void ApplyNormalization(java.util.ArrayList<IRTCDocNoOutputFormat.OneQueryOutputFormat> ls)
    {
        if(clsPrm.normalization_technique == clsPrm.NORMALIZATION_MINMAX)
        {
            ApplyMinMaxNormalization(ls);
        }
    }
    
    private void ApplyMinMaxNormalization(java.util.ArrayList<IRTCDocNoOutputFormat.OneQueryOutputFormat> ls)
    {
        double dMaxVal = ls.get(0).getValue();
        double dMinVal = ls.get(ls.size() - 1).getValue();
        
        for(int i = 0; i < ls.size(); i++)
        {
            double dVal = ls.get(i).getValue();
            ls.get(i).setValue(dVal / (dMaxVal - dMinVal));
        }
        
    }

   
    /// Dokuman sayısına göre bir sınırlama yapmak istemediğimizde
    /// geri döndürülecek k label sayısına ulaşıldığında dokumanları işlemeyi sonlandırır.
    private boolean CheckIfLabelCountReached(ArrayList<String> listAllICD, IRMedicalQuery irQuery, int iMaxKLabel) {
        /// k değeri 0'dan küçük ise ulaşılan maksimum k değeri dikkate alınmaz.
        if (iMaxKLabel <= 0) {
            return false;
        }

        for (int i = 0; i < irQuery.listICD.size(); i++) {
            if (!listAllICD.contains(irQuery.listICD.get(i))) {
                listAllICD.add(irQuery.listICD.get(i));
                if (listAllICD.size() >= iMaxKLabel) {
                    return true;
                }
            }
        }
        return false;
    }

    /// Her Sorgu için döndürülen dokuman listesine bağlı olarak parametre 
    /// olarak gelen ağırlıklandırma yöntemine göre algoritmayı çalıştırır.
    private void AssignClassLabels(Map<String,Double> classFeas)
            throws Exception {

        Iterator i = QrySet.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            IRMedicalQuery irQuery = (IRMedicalQuery) entry.getValue();
            HashMap<String, Double> listRes = new HashMap<String, Double>();
            for (int iQryDocIndex = 0; iQryDocIndex < irQuery.getListQRes().size(); iQryDocIndex++) {
                for (int iDocICDIndex = 0; iDocICDIndex < irQuery.getListQRes().get(iQryDocIndex).getListICD().size(); iDocICDIndex++) {
                    double dVal = (double) irQuery.getListQRes().get(iQryDocIndex).getsRetrievalScore();
                    String sICDCode = irQuery.getListQRes().get(iQryDocIndex).getListICD().get(iDocICDIndex);
                    if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_ROWINDEX)) {
                        if (listRes.containsKey(sICDCode)) {
                            Double d = (clsPrm.getMaxRetDocSize() + 1 - iDocICDIndex) + 
                                    (double) listRes.get(sICDCode);
                            listRes.put(sICDCode, d);
                        } else {
                            Double d = clsPrm.getMaxRetDocSize() + 1 - (double) iDocICDIndex;
                            listRes.put(sICDCode, d);
                        }
                    } else if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_DOCSIM)) {
                        if (listRes.containsKey(sICDCode)) {
                            Double d = dVal + listRes.get(sICDCode);
                            listRes.put(sICDCode, d);
                        } else {
                            listRes.put(sICDCode, dVal);
                        }
                    } else if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_DOCSIM_M_ROWINDEX)) {
                        double mixedval = (dVal *
                                    clsPrm.getMaxRetDocSize() + 1 - (double) iDocICDIndex);
                        if (listRes.containsKey(sICDCode)) {
                            Double d = mixedval + (double) listRes.get(sICDCode);
                            /// TO DO MELTEM
                            /// Normalizasyon Yapmak lazım.
                            listRes.put(sICDCode, d);
                        } else {
                            listRes.put(sICDCode, mixedval);
                        }
                    }
                    else if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_LCNT)) {
                        double constantval = 1;
                        if (listRes.containsKey(sICDCode)) {
                            Double d = constantval + (double) listRes.get(sICDCode);
                            /// TO DO MELTEM
                            /// Normalizasyon Yapmak lazım.
                            listRes.put(sICDCode, d);
                        } else {
                            listRes.put(sICDCode, constantval);
                        }
                    }
                    else if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_CATE_WEIGHTED_DOCSIM)) {
                        Double ClassWeight = classFeas.get(sICDCode);
                        if (listRes.containsKey(sICDCode)) {
                            Double d = dVal * ClassWeight + listRes.get(sICDCode);
                            /*to do meltem
                            Class weightler bulunup eklenecek.*/
                            listRes.put(sICDCode, d);
                        } else {
                            listRes.put(sICDCode, dVal * ClassWeight);
                        }
                    }
                    else if (clsPrm.getLabelWeighting().equals(clsPrm.LABEL_WEIGHTING_CATE_WEIGHT)) {
                        Double ClassWeight = classFeas.get(sICDCode);
                        if (listRes.containsKey(sICDCode)) {
                            Double d = ClassWeight + listRes.get(sICDCode);
                            /*to do meltem
                            Class weightler bulunup eklenecek.*/
                            listRes.put(sICDCode, d);
                        } else {
                            listRes.put(sICDCode, ClassWeight);
                        }
                    }
                    else {
                        throw new Exception("Invalid Label Weighting Parameter");
                    }
                }
            }
            irQuery.ClearListICDAssigned();
            if (clsPrm.getLabelFiltering().equals(clsPrm.LABEL_FILTERING_TRH)) {
                SortLabels(listRes, clsPrm.getTreshold(), irQuery.getListICDAssignedValues(), irQuery.getListICDAssignedKeys());
            } else if (clsPrm.getLabelFiltering().equals(clsPrm.LABEL_FILTERING_FIRSTN)) {
                FirstN(listRes, clsPrm.getFirstNLabelCnt(), irQuery.getListICDAssignedValues(), irQuery.getListICDAssignedKeys());
            } else {
                throw new Exception("Invalid Label Filtering Parameter");
            }
        }
    }

    private void SortLabels(HashMap<String, Double> listRes,
            ArrayList<Double> aListValues, ArrayList<String> aListKeys) {
        if (listRes.size() > 0) {
            Double[] listValues = listRes.values().toArray(new Double[listRes.size()]);
            String[] listKeys = listRes.keySet().toArray(new String[listRes.size()]);
            DemirHeapSort.descendingHeapSort(listValues, listKeys);
            for (int i = 0; i < listKeys.length; i++) {
                aListKeys.add(listKeys[i]);
                aListValues.add(listValues[i]);
            }
        }
    }

    private void SortLabels(HashMap<String, Double> listRes, double iTreshold,
            ArrayList<Double> aListValues, ArrayList<String> aListKeys) {
        if (listRes.size() > 0) {
            Double[] listValues = listRes.values().toArray(new Double[listRes.size()]);
            String[] listKeys = listRes.keySet().toArray(new String[listRes.size()]);

            DemirHeapSort.descendingHeapSort(listValues, listKeys);
            for (int i = 0; i < listKeys.length; i++) {
                if (listValues[i] >= iTreshold) {
                    aListKeys.add(listKeys[i]);
                    aListValues.add(listValues[i]);
                }
            }
        }
    }

    private void FirstN(HashMap<String, Double> dicRes, int iArrayLength,
            ArrayList<Double> aListValues, ArrayList<String> aListKeys) {
        SortLabels(dicRes, aListValues, aListKeys);
        for (int i = aListKeys.size() - 1; i > iArrayLength - 1; i--) {
            aListKeys.remove(i);
            aListValues.remove(i);
        }
    }
}
