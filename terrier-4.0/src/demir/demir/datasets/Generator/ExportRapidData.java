/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets.Generator;

import demir.terrier.matching.TaatWeightingPrinter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nmeltem
 */
public class ExportRapidData {

    public static void main(String[] args) {
        Connection con = demir.dbconnection.ConnectToServer.Connect();
        String sFilePath = "D:\\Datasets\\REUTERS\\rapid_data\\col14_repoid_3_4\\";
        int iTrainRepoId = 3;
        int iTestRepoId = 4;
        int iCollectionId = 14;
        try {
          GenerateDataFile(con, iCollectionId, (sFilePath + "train.txt"), iTrainRepoId, iTestRepoId, true);
          GenerateDataFile(con, iCollectionId, (sFilePath + "test.txt"), iTrainRepoId, iTestRepoId, false);
          GenerateExampleDataFile(con, (sFilePath + "example.txt"), iTrainRepoId, iTestRepoId);
        } catch (IOException ex) {
            Logger.getLogger(TaatWeightingPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static long GetTermCount(Connection con, int pTrainRepoId, int pTestRepoId) throws SQLException {
        Statement st;
        String sSql1 = "SELECT COUNT(DISTINCT TERM) as CNT FROM rapidminer_repo.REPO_TERMS "
                + "where "
                + "REPO_ID IN (" + pTrainRepoId + "," + pTestRepoId + ")";
        st = (Statement) con.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sSql1);
        if (rs.first()) {
            return (long) rs.getObject("CNT");
        }
        return 0;
    }

    public static void GenerateDataFile(Connection con, int iCollectionId,
            String sFilePath, int iTrainRepoId, int iTestRepoId, boolean isTrain) throws IOException {

        PrintWriter fw = new PrintWriter(new FileWriter(sFilePath));

        ArrayList<String> termList = new ArrayList();
        int iInitialRepoId = -1;

        if (isTrain) {
            iInitialRepoId = iTrainRepoId;
        } else {
            iInitialRepoId = iTestRepoId;
        }

        try {
            long iTermCnt = GetTermCount(con, iTrainRepoId, iTestRepoId);
            long iTermIndex = 0;
            String sSql1 = "select  demir_tc.doc_labels.file_id as file_id,"
                    + "  demir_tc.doc_labels.LABEL as label "
                    + " from rapidminer_repo.repo_docs, demir_tc.doc_labels where "
                    + " demir_tc.doc_labels.COLLECTION_ID = " + iCollectionId + " and "
                    // + " repo_id = " + iTrainRepoId
                    + " repo_id = " + iInitialRepoId
                    // + " and rapidminer_repo.repo_docs.FILE_ID > '0001800' "
                    // + " and rapidminer_repo.repo_docs.FILE_ID < '000100' "
                    + " and rapidminer_repo.repo_docs.FILE_ID = demir_tc.doc_labels.FILE_ID";

            Statement st;

            st = (Statement) con.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sSql1);

            sSql1 = "SELECT DISTINCT TERM FROM rapidminer_repo.REPO_TERMS "
                    + "where "
                    + "REPO_ID IN (" + iTrainRepoId + "," + iTestRepoId + ")";

            st = (Statement) con.createStatement();
            java.sql.ResultSet rsTerms = st.executeQuery(sSql1);
            fw.print("idx;labelx;");
            while (rsTerms.next()) {
                String sTerm = (String) rsTerms.getObject("TERM");
                iTermIndex++;
                //termList.add();
                if (iTermIndex != iTermCnt) 
                    fw.print(sTerm + ";");
                else
                    fw.print(sTerm);
            }
            int iRecCnt = 0;

            while (rs.next()) {

                //System.out.println();
                fw.print("\n");

                String sFileId = (String) rs.getString("file_id");
                String sLabel = (String) rs.getString("label");
                System.out.println(sFileId);
                fw.print(sFileId + ";");
                fw.print(sLabel + ";");

                sSql1 = " select a.TERM as term, IFNULL(repo_doc_term_score.W_SCORE,0) as wscore from "
                        + " (select distinct term from repo_terms where repo_id in ( "
                        + iTrainRepoId + "," + iTestRepoId + ")) a  "
                        + "  left  JOIN repo_doc_term_score "
                        + " ON   "
                        // + "  repo_doc_term_score.repo_id = "  + iTrainRepoId
                        + "  repo_doc_term_score.repo_id = " + iInitialRepoId
                        + " and  file_id = '" + sFileId + "'"
                        + " and a.term = repo_doc_term_score.term"
                        + " order by a.TERM";

                st = (Statement) con.createStatement();

                java.sql.ResultSet rsScore = st.executeQuery(sSql1);
                iTermIndex = 0;
                while (rsScore.next()) {
                    //String sTerm = (String)rsScore.getObject("term");
                    iTermIndex++;
                    double dScore = (double) rsScore.getObject("wscore");
                    //System.out.println(sTerm + " " + dScore);
                    if (dScore != 0) /// Score Yok ise missing value olarak eklenmiştir.
                    {
                        if (!isTrain) {
                            dScore = 1;
                        }
                        /// TEst dosyası için ağırlıklandırma kullanılmamıştır.
                        /// Ağırlık değeri 1 kabul edilmiştir.
                        if (iTermIndex != iTermCnt) {
                            fw.print(dScore + ";");
                        } else {
                            fw.print(dScore);
                        }
                    }
                    if (iTermIndex != iTermCnt) 
                        fw.print(";");
                }
                rsScore.close();
                iRecCnt++;
                if (iRecCnt > 100) {
                    fw.flush();
                    iRecCnt = 0;

                }
            }
            fw.close();
        } catch (SQLException ex) {
            Logger.getLogger(ExportRapidData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void GenerateExampleDataFile(Connection con, 
            String sFilePath,
            int iTrainRepoId, int iTestRepoId) throws IOException {

        PrintWriter fw = new PrintWriter(new FileWriter(sFilePath));

        ArrayList<String> termList = new ArrayList();

        long iTermIndex = 0;
        try {
            long iTermCnt = GetTermCount(con, iTrainRepoId, iTestRepoId);
            Statement st;

            String sSql1 = "SELECT DISTINCT TERM FROM rapidminer_repo.REPO_TERMS "
                    + "where "
                    + "REPO_ID IN (" + iTrainRepoId + "," + iTestRepoId + ")";

            st = (Statement) con.createStatement();
            java.sql.ResultSet rsTerms = st.executeQuery(sSql1);
            fw.print("idx;labelx;");
            /// TO DO MELTEM son termm için virgülsüz yaz
            while (rsTerms.next()) {
                iTermIndex++;
                String sTerm = (String) rsTerms.getObject("TERM");
                //termList.add();
                if (iTermIndex != iTermCnt) {
                    fw.print(sTerm + ";");
                } else {
                    fw.print(sTerm);
                }
            }
            fw.println();

            fw.print("1;AAA;");
            rsTerms.first();
            fw.print("1" + ";");
            iTermIndex = 1;
            while (rsTerms.next()) {
                iTermIndex++;
                //String sTerm = (String) rsTerms.getObject("TERM");
                if (iTermIndex != iTermCnt) {
                    fw.print("1.0" + ";");
                } else {
                    fw.print("1.0");
                }
            }
            fw.close();
        } catch (SQLException ex) {
            Logger.getLogger(ExportRapidData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//     public static void GenerateTestDataFile(Connection con, 
//             String sFilePath,
//             int iTrainRepoId, int iTestRepoId) throws IOException {
//
//        PrintWriter fw = new PrintWriter(new FileWriter(sFilePath));
//
//        ArrayList<String> termList = new ArrayList();
//
//        try {
//
////        String sSql1 = "select file_id from rapidminer_repo.repo_docs "
////                + "where "
////                + "repo_id = " + iTrainRepoId;
//            String sSql1 = "select  demir_tc.doc_labels.file_id as file_id,"
//                    + "  demir_tc.doc_labels.LABEL as label "
//                    + " from rapidminer_repo.repo_docs, demir_tc.doc_labels where "
//                    + " demir_tc.doc_labels.COLLECTION_ID = 12 and "
//                    + " repo_id = " + iTestRepoId
//                    // + " and rapidminer_repo.repo_docs.FILE_ID > '0001800' "
//                    + " and rapidminer_repo.repo_docs.FILE_ID = demir_tc.doc_labels.FILE_ID";
//
//            Statement st;
//
//            st = (Statement) con.createStatement();
//            java.sql.ResultSet rs = st.executeQuery(sSql1);
//
//            sSql1 = "SELECT DISTINCT TERM FROM rapidminer_repo.REPO_TERMS "
//                    + "where "
//                    + "REPO_ID IN (" + iTrainRepoId + "," + iTestRepoId + ")";
//
//            st = (Statement) con.createStatement();
//            java.sql.ResultSet rsTerms = st.executeQuery(sSql1);
//            fw.print("idx;labelx;");
//            while (rsTerms.next()) {
//                String sTerm = (String) rsTerms.getObject("TERM");
//                //termList.add();
//                fw.print(sTerm + ";");
//            }
//            int iRecCnt = 0;
//            
//            while (rs.next()) {
//                
//                //System.out.println();
//                fw.print("\n");
//
//                String sFileId = (String) rs.getString("file_id");
//                String sLabel = (String) rs.getString("label");
//                System.out.println(sFileId);
//                fw.print(sFileId + ";");
//                fw.print(sLabel + ";");
//
//                sSql1 = " select a.TERM as term, IFNULL(repo_doc_term_score.W_SCORE,0) as wscore from "
//                        + " (select distinct term from repo_terms where repo_id in ( " + 
//                        iTrainRepoId + "," + iTestRepoId + ")) a  "
//                        + "  left  JOIN repo_doc_term_score "
//                        + " ON   "
//                        + "  repo_doc_term_score.repo_id = "  + iTrainRepoId
//                        + " and  file_id = '" + sFileId + "'"
//                        + " and a.term = repo_doc_term_score.term"
//                        + " order by a.TERM";
//
//                st = (Statement) con.createStatement();
//
//                java.sql.ResultSet rsScore = st.executeQuery(sSql1);
//                
//                while (rsScore.next()) {
//                    //String sTerm = (String)rsScore.getObject("term");
//                    double dScore = (double)rsScore.getObject("wscore");
//                    //System.out.println(sTerm + " " + dScore);
//                    if(dScore != 0)
//                    {
//                        fw.print(1 + ";");
//                    }
//                    fw.print(";");
//                }
//                rsScore.close();
//                 iRecCnt++;
//                    if(iRecCnt > 100)
//                    {
//                        fw.flush();
//                        iRecCnt = 0;
//                      
//                    }
//            }
//            fw.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(ImportFileData.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
