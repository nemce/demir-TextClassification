/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.evaulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import demir.evaulation.evalQuery;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Meltem
 */
public class demirEval {

    HashMap LabelsList = new HashMap<String, evalQuery>();
    HashMap DocList = new HashMap<String, Integer>();
    //ArrayList<ResultLines> resLines = new ArrayList<>();
    //dArrayList<QRelsLines> QRelsLines = new ArrayList<>();
    int iDocCnt = 0;

    public static void main(String[] args) {
        /*
        String QRelsFilePath = "D:\\Datasets\\TREC_EVAL_TEST\\SAMPLE QRELS.txt";
        String ResFilePath = "D:\\Datasets\\TREC_EVAL_TEST\\SAMPLE RES.txt";
        String OutputFilePath = "D:\\Datasets\\TREC_EVAL_TEST\\SAMPLE OUTPUT.txt";
        */
        
        /*
        String QRelsFilePath = "D:\\R PROGRAMMING\\Data\\RCV1\\sil\\17_1_doc_label_test0_qrels.txt";
        String ResFilePath = "D:\\R PROGRAMMING\\output\\RCV1\\17_1_three_tf_idf4.txt";
        String OutputFilePath = "D:\\R PROGRAMMING\\output\\RCV1\\17_1_three_tf_idf4.eval";
        */
        String QRelsFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\TEST CAT DOC.txt";
        String ResFilePath ="D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\output\\2\\output_22_1_f5_tf_idf_one_normalized_byclass.txt";
        String OutputFilePath = "D:\\Datasets\\turkish_trec_med\\TrecDocs\\Sets\\50_cat\\1\\output\\2\\output_22_1_f5_tf_idf_one_normalized_byclass.eval";
        
        demirEval oEval = new demirEval();
        oEval.ReadQrelsFile(QRelsFilePath);
        oEval.ReadResultFile(ResFilePath);
        oEval.CalculateMetricsAndWrite(OutputFilePath);
    }

    public void CalculateMetricsAndWrite(String OutputFilePath) {
        PrintWriter pwm = null;

        try {
            pwm = new PrintWriter(OutputFilePath);
            pwm.println("Query tp fp fn tn precision recall f1" );
           // pwm.println(sQueryText);
            Iterator it = LabelsList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                evalQuery eQuery = ((evalQuery) pair.getValue());
                eQuery.Calculate(iDocCnt);
                pwm.println( eQuery.QueryLabel + " " + eQuery.tp +  " " + eQuery.fp + " " + eQuery.fn + " " + eQuery.tn + " " +
                       eQuery.precision + " " + eQuery.recall  + " " + eQuery.f1  );
            }

        } catch (Exception ex) {
            Logger.getLogger(demirEval.class.getName()).log(Level.SEVERE, null, ex);
        }
        pwm.close();
    }

    /*This function reads Result file in trec format
        for predicitions
    query-id Q0 document-id rank score STANDARD
     */
    public void ReadResultFile(String FilePath) {
        String sLine = "";
        try {
            int iLineId = 0;
            File fileDir = new File(FilePath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "windows-1254"));
            while ((sLine = br.readLine()) != null) {
                ResultLines objLines = new ResultLines(iLineId);
                objLines.ParseLine(sLine);
                //resLines.add(objLines);
                int docid = -1;
                evalQuery eQuery = null;
                if (!DocList.containsKey(objLines.DocumentId)) {
                    docid = iDocCnt;
                    DocList.put(objLines.DocumentId, iDocCnt++);
                } else {
                    docid = (int) DocList.get(objLines.DocumentId);
                }
                if (!LabelsList.containsKey(objLines.QueryId)) {
                    throw new Exception(" Unknown Label");
                } else {
                    ((evalQuery) LabelsList.get(objLines.QueryId)).getPredictionArray().add(docid);
                    System.out.println(objLines.QueryId + " " + docid);
                }
                iLineId++;
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(demirEval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(demirEval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*This function reads qrels file in trec format
        for ground truth data (conditions)
    query-id 0 document-id relevance
     */
    public void ReadQrelsFile(String FilePath) {
        String sLine = "";

        try {
            int iLineId = 0;
            File fileDir = new File(FilePath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "windows-1254"));
            while ((sLine = br.readLine()) != null) {
                QRelsLines objLines = new QRelsLines(iLineId);
                objLines.ParseLine(sLine);
                int docid = -1;
                if (!DocList.containsKey(objLines.DocumentId)) {
                    docid = iDocCnt;
                    DocList.put(objLines.DocumentId, iDocCnt++);
                } else {
                    docid = (int) DocList.get(objLines.DocumentId);
                }
                if (objLines.Relevance == 1) {
                    evalQuery eQuery = null;
                    if (!LabelsList.containsKey(objLines.QueryId)) {
                        eQuery = new evalQuery(objLines.QueryId);
                        eQuery.getConditionsArray().add(docid);
                        LabelsList.put(objLines.QueryId, eQuery);
                        System.out.println(objLines.QueryId + " " + docid + " " + objLines.DocumentId);
                    } else {
                        ((evalQuery) LabelsList.get(objLines.QueryId)).getConditionsArray().add(docid);
                        System.out.println(objLines.QueryId + " " + docid + " " + objLines.DocumentId);
                    }
                }
                iLineId++;
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(demirEval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(demirEval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class QRelsLines {

        int LineId;
        String QueryId;
        String DocumentId;
        Byte Relevance;

        public QRelsLines(int LineId) {
            this.LineId = LineId;
        }

        public String getQueryId() {
            return QueryId;
        }

        public String getDocId() {
            return DocumentId;
        }

        public Byte getRelevance() {
            return Relevance;
        }

        public void AssignValues(String qid, String docid, Byte Relevance) {
            QueryId = qid;
            DocumentId = docid;
            this.Relevance = Relevance;
        }

        public void ParseLine(String sLine) {
            String[] Values = sLine.split(" ");
            //AssignValues(Values[0], Values[2], Byte.parseByte(Values[3]));
            AssignValues(Values[0], Values[1], (byte)1);
        }
    }

    /*query-id Q0 document-id rank score STANDARD
     */
    class ResultLines {

        int LineId;
        String QueryId;
        String DocumentId;
        int rank;
        double Score;

        public String getQueryId() {
            return QueryId;
        }

        public String getDocId() {
            return DocumentId;
        }

        public int getRank() {
            return rank;
        }

        public double getScore() {
            return Score;
        }

        public ResultLines(int LineId) {
            this.LineId = LineId;
        }

        public void AssignValues(String qid, String docid, int rank, double Score) {
            QueryId = qid;
            DocumentId = docid;
            this.rank = rank;
            this.Score = Score;
        }

        public void ParseLine(String sLine) {
            String[] Values = sLine.split(" ");
            AssignValues(Values[0], Values[2], Integer.parseInt(Values[3]), Double.parseDouble(Values[4]));
        }
    }
}
