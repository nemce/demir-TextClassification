/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.classification;

import demir.dbconnection.ImportToDB;
import demir.terrier.querying.IRTCDocNoOutputFormat;
import demir.terrier.querying.QueryProcessor;
import demir.terrier.utility.FeatureLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author meltem
 */
public class TestClassificationByQuery {

    public static void main(String[] args) {
        IRBasedTextClassification ibtc = new IRBasedTextClassification();

        String sTestFolder = ibtc.getClsPrm().getTestFolderPath();
        String sTopicFile = ibtc.getClsPrm().getTopicFileName();
        if (!sTestFolder.isEmpty()) {
            ReadFiles(ibtc, sTestFolder);
        } else {
            ReadTopicFile(ibtc, sTopicFile);
        }
        // ibtc.CloseConnections(); /// TODO Consider for parallel programing.
    }

    public static void ReadFiles(IRBasedTextClassification ibtc, String sTestFolder) {
        System.out.println(sTestFolder);
        File file = new File(sTestFolder);
        File[] files = file.listFiles();
        int iMaximum = files.length;
        int iSepIndex = 0;
        int iProcessCount = 1;

        if (iMaximum % iProcessCount == 0) {
            iSepIndex = iMaximum / iProcessCount;
        } else {
            iSepIndex = (iMaximum / iProcessCount) + 1;
        }

        for (int i = 0; i < iProcessCount; i++) {
            TestClassificationByQuery ic = new TestClassificationByQuery();
            ic.setFiles(files);
            ic.ir = ibtc;
            ic.QueryIdStartIndex = i * iSepIndex;
            try {
                //ic.start();
                ic.run();
            } catch (Exception ex) {
                Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void ReadTopicFile(IRBasedTextClassification ibtc, String sTopicFile) {
        String sLine = "";
        String sWholeText = "";
        try {
            FileReader fr = new FileReader(sTopicFile);
            BufferedReader br = new BufferedReader(fr);

            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\r\n";
            }
            br.close();
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map<String, String> queries = new HashMap<String, String>();
        //String sTopicStartTag = "<DOC>";
        //String sTopicEndTag = "</DOC>";
        String sTopicStartTag = "<TOP>";
        String sTopicEndTag = "</TOP>";
        String sFIDStartTag = "<FID>";
        String sFIDEndTag = "<FID>";
        int iQueryId = 0;
        while (sWholeText != null && !sWholeText.isEmpty()) {
            int iStartIndex = sWholeText.indexOf(sTopicStartTag);
            int iEndIndex = sWholeText.indexOf(sTopicEndTag);
            String QueryText = sWholeText.substring(iStartIndex, iEndIndex + sTopicEndTag.length());
             
            int iStartIndex1 = QueryText.indexOf(sFIDStartTag);
            int iEndIndex1 = QueryText.lastIndexOf(sFIDEndTag);
            String QueryName = QueryText.substring(iStartIndex1 + sFIDEndTag.length(), iEndIndex1);
            
            ++iQueryId;
            QueryText = TestClassificationByQuery.GenerateQueryFile(String.valueOf(iQueryId), QueryText, ibtc.clsPrm.getQueryTagList());
            queries.put(QueryName, QueryText);

            if (iEndIndex + sTopicEndTag.length() == sWholeText.length()) {
                break;
            }
            sWholeText = sWholeText.substring(iEndIndex + 7, sWholeText.length());
            sWholeText = sWholeText.trim();
        }

        TestClassificationByQuery ic = new TestClassificationByQuery();
        ic.setQueries(queries);
        ic.ir = ibtc;
        ic.QueryIdStartIndex = 0;
        try {
            //ic.start();
            ic.run();
        } catch (Exception ex) {
            Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    protected Index index = null;
    protected Map features = null;
    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TestClassificationByQuery.class);
    File[] files = null;
    Map<String, String> queries = null;

    IRBasedTextClassification ir = null;
    //// Her thread için Farklı Query id'ler oluşturmak için kullanılmıştır.
    int QueryIdStartIndex = 0;
    String topicFileName = "";

    PrintWriter ResultFileWriter = null;

    protected void loadIndex() {
        long startLoading = System.currentTimeMillis();
        index = Index.createIndex();
        if (index == null) {
            logger.fatal("Failed to load index. " + Index.getLastIndexLoadError());
            throw new IllegalArgumentException("Failed to load index: " + Index.getLastIndexLoadError());
        }
        long endLoading = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("time to intialise index : "
                + ((endLoading - startLoading) / 1000.0D));
        }
        if (ApplicationSetup.getProperty("trec.matching", "").equals("demir.terrier.matching.daat.Full")) {
            features = FeatureLoader.LoadFeaturesFromFile();
            if (logger.isInfoEnabled()) {
                if (features != null) {
                    logger.info("Features Loaded from File");
                } else {
                    logger.info("Features are not loaded");
                }
            }
        }
    }

    public void closeIndex() {
        if (index != null) {
            try {
                index.close();
            } catch (IOException e) {
            }
        }
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

    public void setIr(IRBasedTextClassification ir) {
        this.ir = ir;
    }

    public void setQueryIdStartIndex(int QueryIdStartIndex) {
        this.QueryIdStartIndex = QueryIdStartIndex;
    }

    public String getTopicFileName() {
        return topicFileName;
    }

    public void setTopicFileName(String topicFileName) {
        this.topicFileName = topicFileName;
    }

    public void run() throws Exception {
        long startLoading = System.currentTimeMillis();
        if (!ir.clsPrm.isUseResultFile()) {
            loadIndex();
        }
        int iCount = 0;
        Session session = ir.session;

        try {
            /// TODO MELTEM 
            /// Buraya farklı threadler için aynı dosynanın oluşmaması için kontrol eklenebilir.

            ResultFileWriter = new PrintWriter(new FileWriter(ir.clsPrm.getClassificationResFile() + startLoading + ".txt"));
        } catch (IOException ex) {
            String sMessage = ir.clsPrm.getClassificationResFile() + " could not be created";
            Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, sMessage, ex);
        }

        if (files != null) { /// path'den dosyaları oku
            for (int fileInList = 0; fileInList < files.length; fileInList++) {
                if (files[fileInList] != null) {
                    try {
                        if (ir.clsPrm.isUseResultFile() == true) {
                            IRMedicalQuery irQuery = new IRMedicalQuery();
                            irQuery.setsQueryId(files[fileInList].getName());
                            irQuery.setsDocNo(files[fileInList].getName());
                            irQuery.setsQDType("Q");
                            ir.AddQuery(irQuery);
                        } else {
                            String QueryId = String.valueOf(QueryIdStartIndex + fileInList);
                            ProcessFile(files[fileInList].getPath(), files[fileInList].getName(), QueryId, session);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    iCount++;
                }

                if (iCount % 100 == 0) {
                    System.out.println(iCount);
                }
            }
        } else { /// topic file dan oku
            Iterator entries = queries.entrySet().iterator();
            int fileInList = 0;
            while (entries.hasNext()) {
                try {
                    String QueryId = String.valueOf(++fileInList);
                    Entry thisEntry = (Entry) entries.next();
                    String sFileName = (String) thisEntry.getKey();
                    String sQueryValue = (String) thisEntry.getValue();

                    if (ir.clsPrm.isUseResultFile() == true) {
                        IRMedicalQuery irQuery = new IRMedicalQuery();
                        irQuery.setsQueryId(sFileName);
                        irQuery.setsDocNo(sFileName);
                        irQuery.setsQDType("Q");
                        ir.AddQuery(irQuery);
                    } else {
                        String sResult = ClassifyQuery(QueryId, sQueryValue, sFileName, session);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                iCount++;
            }
            if (iCount % 100 == 0) {
                System.out.println(iCount);
            }
        }

        if (ir.clsPrm.isUseResultFile() == true) {
            ir.ProcessQueries(ir.clsPrm.getRetrievalResultFilePath());

            /// Sonuçları veri tabanına yazdırır.
            Iterator iEntry = ir.QrySet.entrySet().iterator();
            while (iEntry.hasNext()) {
                Map.Entry entry = (Map.Entry) iEntry.next();
                IRMedicalQuery irQuery = (IRMedicalQuery) entry.getValue();
                String[] keys = new String[irQuery.getListICDAssignedKeys().size()];
                Double[] values = new Double[irQuery.getListICDAssignedKeys().size()];
                String sResult = (String) entry.getKey();
                for (int i = 0; i < irQuery.getListICDAssignedKeys().size(); i++) {
                    keys[i] = irQuery.getListICDAssignedKeys().get(i);
                    values[i] = irQuery.getListICDAssignedValues().get(i);
                    sResult += keys[i] + " " + values[i] + " ";
                }
                if (ir.clsPrm.WriteClassificationRes.equals(ir.clsPrm.WRITE_CLASSIFICATION_RES_DB)) {
                    ImportToDB.ImportKeysValues(session, ir.clsPrm.RunId,
                        irQuery.sQueryId, keys, values);
                } else {
                    ResultFileWriter.print(sResult);
                }
                /// Sonuçları veri tabanına yazdırır.
            }
        }

        if (!ir.clsPrm.isUseResultFile()) {
            closeIndex();
        }
        demir.tc.irbased.hibernate.connection.ConnectToServer.Disconnect(session);
        ResultFileWriter.close();
        long endLoading = System.currentTimeMillis();
        System.out.println("time to classifiy All Queries : "
            + ((endLoading - startLoading) / 1000.0D));
    }

    private void ProcessFile(String sFilePath, String sFileName, String queryId, Session session) throws Exception {
        try {
            long startLoading = System.currentTimeMillis();
            String sWholeText = ReadFile(sFilePath, sFileName);
            String sQueryText = GenerateQueryFile(queryId, sWholeText, ir.clsPrm.getQueryTagList());

            String sResult = ClassifyQuery(queryId, sQueryText, sFileName, session);
            long endLoading = System.currentTimeMillis();
            System.out.println("time to classifiy Query : "
                + ((endLoading - startLoading) / 1000.0D));
        } catch (Exception eSys) {
            logger.error("Could not process file " + sFileName, eSys);
        }
    }

    protected String ReadFile(String sFilePath, String sFileName) {

        String sLine = "";
        String sWholeText = "";
        try {
            FileReader fr = new FileReader(sFilePath);
            BufferedReader br = new BufferedReader(fr);

            while ((sLine = br.readLine()) != null) {
                sWholeText += sLine + "\r\n";
            }
            br.close();
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(TestClassificationByQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sWholeText;
    }

    public static String GenerateQueryFile(String queryId, String sWholeText, String[] QueryTags) {
        String sQueryText = "<TOP>"
            + "<NUM>" + queryId + "<NUM>";
        for (int i = 0; i < QueryTags.length; i++) {
            int iStartIndex = sWholeText.indexOf("<" + QueryTags[i] + ">");
            int iEndIndex = sWholeText.indexOf("</" + QueryTags[i] + ">");
            if (iStartIndex > 0 && iEndIndex > 0) {
                String sQueryTag = sWholeText.substring(iStartIndex + ("<" + QueryTags[i] + ">").length(), iEndIndex);
                sQueryText += "<" + QueryTags[i] + ">" + sQueryTag + "</" + QueryTags[i] + ">";
            }
        }
        sQueryText += "</TOP>";
        return sQueryText;
    }

    /**
     *
     * @param pQueryId
     * @param pQueryText
     * @param pDocNo
     * @param session
     */
    private String ClassifyQuery(String pQueryId, String pQueryText, String pFileId, Session session) throws Exception {

        String sRes = "";
        try {
            long startLoading = System.currentTimeMillis();
            double c = 1.0;
            Boolean isParameterValueSpecified = false;
            String sResult = null;

            //21.12.2015 'te kapatıldı.
            /*
             demir.terrier.querying.QueryProcessor qp = new QueryProcessor(pQueryText, pQueryId, index);
             SearchRequest srq = qp.processOneQuery(c, true);
             */
            // TODO MELTEM
 /**/
            org.terrier.applications.batchquerying.TRECQuerying querying
                = new org.terrier.applications.batchquerying.TRECQuerying(pQueryText, pQueryId, index, features);
            SearchRequest srq = querying.processOneQuery(c, isParameterValueSpecified);
            demir.terrier.querying.IRTCDocNoOutputFormat irof = new IRTCDocNoOutputFormat(index);
            irof.printResults(null, srq, sRes, sRes, ir.getClsPrm().getMaxRetDocSize());

            /// v3.5 içindi kapatıldı.
            //java.util.ArrayList<TRECQuerying.OneQueryOutputFormat> ls;
            //ls = TrecTerrier.RequestQueryReturnSearchResult(pQueryId, pQueryText, index);
            /// v3.5 içindi kapatıldı.
            long endLoading = System.currentTimeMillis();
//            System.out.println("time to retrieve Query : "
//	    				+ ((endLoading - startLoading) / 1000.0D));
            startLoading = System.currentTimeMillis();
            String[] keys = null;
            Double[] values = null;

            sRes = ir.ProcessQuery(pQueryId, pQueryText, null, irof.getLs(), keys, values);
            endLoading = System.currentTimeMillis();
//            System.out.println("time to process Query : "
//	    				+ ((endLoading - startLoading) / 1000.0D));
            pFileId = pFileId.replace("Report", "");
            pFileId = pFileId.replace(".xml", "");
            pFileId = pFileId.replace(".txt", "");

            sResult = pFileId + " " + sRes + "\r\n";
            if (ir.clsPrm.WriteClassificationRes.equals(ir.clsPrm.WRITE_CLASSIFICATION_RES_DB)) {
                ImportToDB.ImportLine(session, ir.clsPrm.getRunId(), sResult);
            } else {
                ResultFileWriter.print(sResult);
            }
            /**/
            // TODO MELTEM
            return sResult;

        } catch (Exception ex) {
            logger.error("Could Not Classify Doc : " + pFileId, ex);
            return (pFileId + " " + sRes + "\r\n");
        }
    }

 //// TODO MELTEM BU FONKSİYONLAR PARAMETRIK YAPILMALI.
//    private String GenerateQueryFile(String queryId, String sWholeText) {
//        String sDocNo = null;
//        String sQueryCaption = null;
//        String sQueryDesc = null;
//        String sQueryText = null;
//
//        int iStartIndex = sWholeText.indexOf("<ID>");
//        int iEndIndex = sWholeText.indexOf("</ID>");
//        sDocNo = sWholeText.substring(iStartIndex + 4, iEndIndex);
//
//        iStartIndex = sWholeText.indexOf("<TITLE>");
//        iEndIndex = sWholeText.indexOf("</TITLE>");
//        sQueryCaption = sWholeText.substring(iStartIndex + 7, iEndIndex);
//
//        iStartIndex = sWholeText.indexOf("<BODY>");
//        iEndIndex = sWholeText.indexOf("</BODY>");
//        sQueryDesc = sWholeText.substring(iStartIndex + 6, iEndIndex);
//
//        sQueryText = "<TOP>"
//                + "<NUM>" + queryId + "<NUM>"
//                + "<DOCNO>" + sDocNo + "<DOCNO>"
//                + "<CAPTION>" + sQueryCaption + "</CAPTION>"
//                + "<DESC>" + sQueryDesc + "</DESC>"
//                + "</TOP>";
//        return sQueryText;
//    }
//
//    private String GenerateQueryFileForClef(String queryId, String sWholeText) {
//        String sDocNo = null;
//        String sQueryCaption = null;
//        String sQueryText = null;
//
//        int iStartIndex = sWholeText.indexOf("<DOCNO>");
//        int iEndIndex = sWholeText.indexOf("</DOCNO>");
//        sDocNo = sWholeText.substring(iStartIndex + 7, iEndIndex);
//
//        iStartIndex = sWholeText.indexOf("<CAPTION>");
//        iEndIndex = sWholeText.indexOf("</CAPTION>");
//        sQueryCaption = sWholeText.substring(iStartIndex + 8, iEndIndex);
//
//        sQueryText = "<TOP>"
//                + "<NUM>" + queryId + "<NUM>"
//                + "<DOCNO>" + sDocNo + "<DOCNO>"
//                + "<CAPTION>" + sQueryCaption + "</CAPTION>"
//                + "</TOP>";
//        return sQueryText;
//    }
//    
//    private String GenerateQueryFileForCMC(String queryId, String sWholeText) {
//        //String sDocNo = null;
//        String sQueryCaption = null;
//        String sQueryDesc = null;
//        String sQueryText = null;
//
//        int iStartIndex = sWholeText.indexOf("<DOCNO>");
//        int iEndIndex = sWholeText.indexOf("</DOCNO>");
//        //sDocNo = sWholeText.substring(iStartIndex + 7, iEndIndex);
//
//        iStartIndex = sWholeText.indexOf("<CLINICAL_HIST>");
//        iEndIndex = sWholeText.indexOf("</CLINICAL_HIST>");
//        sQueryCaption = sWholeText.substring(iStartIndex + "<CLINICAL_HIST>".length(), iEndIndex);
//        
//        
//        iStartIndex = sWholeText.indexOf("<IMPRESSION>");
//        iEndIndex = sWholeText.indexOf("</IMPRESSION>");
//        sQueryDesc = sWholeText.substring(iStartIndex + "<IMPRESSION>".length(), iEndIndex);
//
//        sQueryText = "<TOP>"
//                + "<NUM>" + queryId + "<NUM>"
//               // + "<DOCNO>" + sDocNo + "<DOCNO>"
//                + "<CAPTION>" + sQueryCaption + "</CAPTION>"
//                + "<DESC>" + sQueryDesc + "</DESC>"
//                + "</TOP>";
//        return sQueryText;
//    }
//    
//    private String GenerateQueryFileForTurkishMed(String queryId, String sWholeText) {
//        //String sDocNo = null;
//        String sQueryCaption = null;
//        String sQueryDesc = null;
//        String sQueryText = null;
//        String sQueryNarr = null;
//
//        int iStartIndex = sWholeText.indexOf("<_ReportNo>");
//        int iEndIndex = sWholeText.indexOf("</_ReportNo>");
//        //sDocNo = sWholeText.substring(iStartIndex + 7, iEndIndex);
//
//        iStartIndex = sWholeText.indexOf("<Anamnez>");
//        iEndIndex = sWholeText.indexOf("</Anamnez>");
//        sQueryCaption = sWholeText.substring(iStartIndex + "<Anamnez>".length(), iEndIndex);
//        
//        
//        iStartIndex = sWholeText.indexOf("<FizikInceleme>");
//        iEndIndex = sWholeText.indexOf("</FizikInceleme>");
//        sQueryDesc = sWholeText.substring(iStartIndex + "<FizikInceleme>".length(), iEndIndex);
//        
//        iStartIndex = sWholeText.indexOf("<KlinikSeyir>");
//        iEndIndex = sWholeText.indexOf("</KlinikSeyir>");
//        sQueryNarr = sWholeText.substring(iStartIndex + "<KlinikSeyir>".length(), iEndIndex);
//
//        sQueryText = "<TOP>"
//                + "<NUM>" + queryId + "<NUM>"
//               // + "<DOCNO>" + sDocNo + "<DOCNO>"
//                + "<CAPTION>" + sQueryCaption + "</CAPTION>"
//                + "<DESC>" + sQueryDesc + "</DESC>"
//                + "<NARR>" + sQueryNarr + "</NARR>"
//                + "</TOP>";
//        return sQueryText;
//    }
}
