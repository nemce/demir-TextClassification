/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.classification;

import demir.dbconnection.ConnectToServer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import static org.terrier.utility.ApplicationSetup.getProperty;

/**
 *
 * @author meltem
 */
public class ClassificationParameters {

    String normalization_technique = "None";


    ///RowIndex-DocSim
    String LabelWeighting = "DocSim";
    ///Trh-FirstN
    String LabelFiltering = "Trh";
    // Kaç Dokuman Okudum. Komşu Dokuman Sayısı
    int MaxRetDocSize = 10;
    /// Kaç ICD Okudum. Komşu Label Sayısı.
    int MaxKLabelReached = 10;
    /// Kaç Sınıf döndürdüm. Atanan Sınıf Sayısı
    int FirstNLabelCnt = 15;
    /// Treshold Value
    double Treshold = 100.0;

    String LabelSearchType = "DB";

    String WriteClassificationRes = "FILE";

    boolean UseResultFile = false;

    String KeyFileName = null;

    String TestFolderPath = null;

    String TopicFileName = null;
    
    String RetrievalResultFilePath = null;

    String ClassificationResFile = null;

    int CollectionId = -1;
    int SetId = -1;
    int TestPrmId = -1;
    int TrainPrmId = -1;
    int RunId = -1;

    String propertiesStream = null;
    String termWeighting = null;
    String termPipeline = null;
    String queryTags = null;
    String docTags = null;
    
    String qeMethod = null;
    String qeApply = null;
    String qeDocs = null;
    String qeTerms = null;

    public String getQeMethod() {
        return qeMethod;
    }

    public String getQeApply() {
        return qeApply;
    }

    public String getQeDocs() {
        return qeDocs;
    }

    public String getQeTerms() {
        return qeTerms;
    }
    
     public String getNormalization_technique() {
        return normalization_technique;
    }

    public String getClassificationResFile() {
        return ClassificationResFile;
    }
    
    public String getWriteClassificationRes() {
        return WriteClassificationRes;
    }

    String[] QueryTagList = null;

    public String[] getQueryTagList() {
        return QueryTagList;
    }

    public String getTestFolderPath() {
        return TestFolderPath;
    }

    public String getTopicFileName() {
        return TopicFileName;
    }
    
    public String getRetrievalResultFilePath() {
        return RetrievalResultFilePath;
    }

    public String LABEL_SEARCH_TYPE_DB = "DB";
    public String LABEL_SEARCH_TYPE_FILE = "FILE";

    public String WRITE_CLASSIFICATION_RES_DB = "DB";
    public String WRITE_CLASSIFICATION_RES_FILE = "FILE";

    public String LABEL_WEIGHTING_DOCSIM = "DOCSIM";
    public String LABEL_WEIGHTING_ROWINDEX = "ROWINDEX";
    public String LABEL_WEIGHTING_DOCSIM_M_ROWINDEX = "DMR";
    public String LABEL_WEIGHTING_LCNT = "L_CNT";
    public String LABEL_WEIGHTING_CATE_WEIGHT = "C_WGHT";
    public String LABEL_WEIGHTING_CATE_WEIGHTED_DOCSIM = "C_WGHT_DOCSIM";

    public String LABEL_FILTERING_TRH = "TRH";
    public String LABEL_FILTERING_FIRSTN = "FIRSTN";
    
    public String NORMALIZATION_MINMAX = "MinMax";
    public String NORMALIZATION_EUCLIDIAN = "Euclid";
    public String NORMALIZATION_None = "None";
    
    public String FeaturesFileName = "";
    
    public String matchingClass = "";

    public int getRunId() {
        return RunId;
    }

    public void setRunId(int RunId) {
        this.RunId = RunId;
    }

    public int getCollectionId() {
        return CollectionId;
    }

    public void setCollectionId(int CollectionId) {
        this.CollectionId = CollectionId;
    }

    public int getSetId() {
        return SetId;
    }

    public void setSetId(int SetId) {
        this.SetId = SetId;
    }

    public int getTestPrmId() {
        return TestPrmId;
    }

    public void setTestPrmId(int TestPrmId) {
        this.TestPrmId = TestPrmId;
    }

    public int getTrainPrmId() {
        return TrainPrmId;
    }

    public void setTrainPrmId(int TrainPrmId) {
        this.TrainPrmId = TrainPrmId;
    }

    public String getPropertiesStream() {
        return propertiesStream;
    }

    public String getTermWeighting() {
        return termWeighting;
    }

    public String getTermPipeline() {
        return termPipeline;
    }

    public String getQueryTags() {
        return queryTags;
    }

    public String getDocTags() {
        return docTags;
    }

    public ClassificationParameters() {

        propertiesStream = org.terrier.utility.ApplicationSetup.gettcPropertiesStream();
        
        normalization_technique = getProperty("tc.irbased.normalization", NORMALIZATION_None);
        LabelWeighting = getProperty("tc.irbased.LabelWeighting", LABEL_WEIGHTING_DOCSIM);
        LabelFiltering = getProperty("tc.irbased.LabelFiltering", LABEL_FILTERING_FIRSTN);
        MaxRetDocSize = Integer.parseInt(getProperty("tc.irbased.MaxRetDocSize", ""));
        MaxKLabelReached = Integer.parseInt(getProperty("tc.irbased.MaxKLabelReached", ""));
        FirstNLabelCnt = Integer.parseInt(getProperty("tc.irbased.FirstNLabelCnt", ""));
        Treshold = Double.parseDouble(getProperty("tc.irbased.TrhValue", ""));

        // TO DO MELTEM ClassificationParameters.java sınıfına taşıyorum.
        TestFolderPath = getProperty("tc.irbased.TestFolder", "");
        TopicFileName = getProperty("tc.irbased.TopicFile", "");

        LabelSearchType = getProperty("tc.irbased.LabelSearch", "DB");
        KeyFileName = getProperty("tc.irbased.KeyFileName", "");
        UseResultFile = Boolean.parseBoolean(getProperty("tc.irbased.UseResultFile", "false"));
        RetrievalResultFilePath = getProperty("tc.irbased.RetrievalResultFilePath", "");
        WriteClassificationRes = getProperty("tc.irbased.WriteClassificationRes", "FILE");
        ClassificationResFile = getProperty("tc.irbased.ClassResFilePath", "");

        if (WriteClassificationRes.equals("DB") || LabelSearchType.equals("DB")) {
            CollectionId = Integer.parseInt(getProperty("tc.irbased.CollectionId", ""));
            SetId = Integer.parseInt(getProperty("tc.irbased.SetId", ""));
            TrainPrmId = Integer.parseInt(getProperty("tc.irbased.TrainPrmId", ""));
            TestPrmId = Integer.parseInt(getProperty("tc.irbased.TestPrmId", ""));
        }
        
        FeaturesFileName = getProperty("demir.features.MI", null);
        matchingClass = getProperty("trec.matching", "terrier.matching.taat.Full");

        docTags = getProperty("TrecDocTags.process", "");
        queryTags = getProperty("TrecQueryTags.process", "");
        termPipeline = getProperty("termpipelines", "");
        termWeighting = getProperty("trec.model", "");
        
        qeMethod = getProperty("trec.qe.model", "");
        qeApply = getProperty("demir.qe", "false");
        qeDocs = getProperty("EXPANSION_DOCUMENTS", "");
        qeTerms = getProperty("EXPANSION_TERMS", "");
        
        if (queryTags != null) {
            QueryTagList = queryTags.split(",");
        }
    }
    
    public void ArrangePrmFromDB()
    {
        try {
            Connection con = ConnectToServer.Connect();
            ResultSet rs = SelRecParameters(CollectionId, SetId, TrainPrmId, TestPrmId, con);
            rs.next();
            RetrievalResultFilePath = rs.getString("QRELS_FILE_PATH");
            TopicFileName = rs.getString("TEST_FILE_PATH");
            java.util.logging.Logger.getLogger(
                    ClassificationParameters.class.getName()).log(java.util.logging.Level.INFO, 
                    " QRELS_FILE_PATH : " + RetrievalResultFilePath + "\n" +
                    " TEST_FILE_PATH : "  + TopicFileName);
            
            
            ConnectToServer.Disconnect(con);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ClassificationParameters.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            System.err.println(" Could not update parameters : ArrangePrmFromDB");
        }
    }
    
    /// TODO meltem hibernate'e taşı. 
    /// connection dan kurtar 04 07 2015
    private static ResultSet SelRecParameters(int iCollectionId, 
            int iSetId, 
            int iTrainId, int iTestId, 
            Connection con) throws SQLException
    {
            String sSql = "SELECT TEST_ID,\n" +
                            "TRAIN_ID, SET_ID, COLLECTION_ID, TEST_FILE_PATH, QRELS_FILE_PATH,\n" +
                            "CROSS_VALIDATION_SET, QUERY_CNT, RANKING_SET_NAME \n" +
                            "FROM demir_tc.test_prm \n" + 
                    " where TEST_ID = " + iTestId +  
                    " and  TRAIN_ID = " + iTrainId + 
                    " and SET_ID = " + iSetId + 
                    " and COLLECTION_ID = " + iCollectionId + ";";
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery(sSql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(ClassificationParameters.class.getName()).log(Level.FATAL, null, ex);
            throw ex;
        }
    }

    public String getLabelWeighting() {
        return LabelWeighting;
    }

    private void setLabelWeighting(String LabelWeighting) {
        this.LabelWeighting = LabelWeighting;
    }

    public String getLabelFiltering() {
        return LabelFiltering;
    }

    private void setLabelFiltering(String LabelFiltering) {
        this.LabelFiltering = LabelFiltering;
    }

    public int getMaxRetDocSize() {
        return MaxRetDocSize;
    }

    private void setMaxRetDocSize(int MaxRetDocSize) {
        this.MaxRetDocSize = MaxRetDocSize;
    }

    public int getFirstNLabelCnt() {
        return FirstNLabelCnt;
    }

    private void setFirstNLabelCnt(int FirstNLabelCnt) {
        this.FirstNLabelCnt = FirstNLabelCnt;
    }

    public int getMaxKLabelReached() {
        return MaxKLabelReached;
    }

    private void setMaxKLabelReached(int MaxKLabelReached) {
        this.MaxKLabelReached = MaxKLabelReached;
    }

    public double getTreshold() {
        return Treshold;
    }

    /// Her Bir Dokümana ait sınıfın nereden Database(DB) ya da (FILE)
    /// okunacağına karar verir.
    /// Bu parametre (FILE) olması durumunda "KeyFileName" property dolu olmalıdır.
    public String getLabelSearchType() {
        return LabelSearchType;
    }

    // Her Bir Dokümana ait sınıfın okunacağı dosya
    // LabelSearchType=FILE olması durumunda
    public String getKeyFileName() {
        return KeyFileName;
    }

    // Bu parametre terrier'den dönen sorgu sonucunun
    // res dosyası ya da java.util.ArrayList<TRECQuerying.OneQueryOutputFormat>
    // tipinde mi olacağını belirler.
    public boolean isUseResultFile() {
        return UseResultFile;
    }

}
