/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.dbconnection;

import demir.tc.classification.ClassificationParameters;
import demir.tc.irbased.hibernate.RunRes;
import demir.tc.irbased.hibernate.RunResId;
import demir.tc.irbased.hibernate.Runs;
import demir.tc.irbased.hibernate.TestPrm;
import demir.tc.irbased.hibernate.TestPrmId;
import demir.tc.irbased.hibernate.UnclassifiedFiles;
import demir.tc.irbased.hibernate.UnclassifiedFilesId;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

/**
 *
 * @author meltem
 */
public class ImportToDB {
    
    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImportToDB.class);
            
    public static void ImportLine(Session session, int iRunId, String sLine) throws Exception
    {
        String sImageFileName = null;
        int iSepIndex = sLine.indexOf(" ");
        sImageFileName = sLine.substring(0, iSepIndex);
        
        if(sImageFileName.contains(".txt")) sImageFileName = sImageFileName.replace(".txt", "");
        sLine = sLine.substring(iSepIndex, sLine.length() - 1);
        session.beginTransaction();
        if(sLine.equals("") || sLine == null || sLine.trim().isEmpty())
        {
            InsertUnclassifiedClefFiles(
                    iRunId, sImageFileName, session);
        }
        else
        {
            sLine = sLine.substring(1, sLine.length() - 1);
            String [] sLabelList = sLine.split("\t");
            for(int i = 0; i < sLabelList.length; i= i+2)
            {
                String sLabel = sLabelList[i];
                int iOrder = i / 2;
                Double dVal = Double.parseDouble(sLabelList[i+1]);
                InsertClassificationResult(
                        iRunId, sImageFileName, sLabel, iOrder, dVal, session);
            }
        }
        session.getTransaction().commit();
    }
    
    public static void ImportKeysValues(Session session, int iRunId, 
            String QueryId,
            String[] keys,
            Double[] values) throws Exception
    {
        session.beginTransaction();
        if(keys == null || keys.length == 0)
        {
            InsertUnclassifiedClefFiles(
                    iRunId, QueryId, session);
        }
        else
        {
            for(int i = 0; i < keys.length; i= i+2)
            {
                String sLabel = keys[i];
                Double dVal = values[i];
                InsertClassificationResult(
                        iRunId, QueryId, sLabel, i, dVal, session);
            }
        }
        session.getTransaction().commit();
    }
    
    public static int InsertRun(Session session, ClassificationParameters clsPrm) throws Exception
    {
         try {
            
            session.beginTransaction();
            int runId = GetMaxRunId(session);
            Runs objRun= new Runs();
            objRun.setRunId(runId);
            objRun.setFirstNCnt(clsPrm.getFirstNLabelCnt());
            objRun.setRetDocCnt(clsPrm.getMaxRetDocSize());
            objRun.setRetLabelCnt(clsPrm.getMaxKLabelReached());
            objRun.setOtherPrm(clsPrm.getTermPipeline());
            objRun.setWghModel(clsPrm.getTermWeighting());
            objRun.setLabelWeighting(clsPrm.getLabelWeighting());
            objRun.setLabelFiltering(clsPrm.getLabelFiltering());
            objRun.setTrhValue(clsPrm.getTreshold());
            objRun.setCreateDate(Calendar.getInstance().getTime());
            objRun.setQueryPath(clsPrm.getTestFolderPath());
            objRun.setNormalization(clsPrm.getNormalization_technique());
            objRun.setQueryExpansion(clsPrm.getQeApply() + "-" + clsPrm.getQeMethod() + 
                    "DOCS:" + clsPrm.getQeDocs() + "TERMS:" + clsPrm.getQeTerms());
            
            objRun.setTerrierProps(clsPrm.getPropertiesStream());
            //objRun.setDemirProps(clsPrm.getPropertiesStream().replace("\n", "xx"));
            
            TestPrm tstPrm = new TestPrm(new TestPrmId(clsPrm.getCollectionId(), clsPrm.getSetId(), clsPrm.getTrainPrmId(), clsPrm.getTestPrmId()), null);
            objRun.setTestPrm(tstPrm);
            session.save(objRun); 
            session.getTransaction().commit();
            return runId;
        }   
        catch (HibernateException e) 
        { 
            session.getTransaction().rollback();
            logger.error(clsPrm.toString(), e);
            throw new Exception(e);
        }
    }
    
    private static void InsertClassificationResult(
            int iRunId, String sFileId, String sLabel, int iOrder, Double dVal,
            Session session) throws Exception
    {
        try {
            RunRes objRunRes = new RunRes();
            objRunRes.setId(new RunResId(iRunId, sFileId, sLabel));
            objRunRes.setResOrder(iOrder);
            objRunRes.setSimilarity(dVal);
            session.save(objRunRes); 
        }   
        catch (HibernateException e) 
        { 
            session.getTransaction().rollback();
            logger.error(sFileId, e);
            throw new Exception(e);
        }
    }
    
    private static void InsertUnclassifiedClefFiles(
            int iRunId, String sFileId, Session session) throws Exception
    {
         try {
            UnclassifiedFiles objUnCls = new UnclassifiedFiles();
            objUnCls.setId(new UnclassifiedFilesId(iRunId, sFileId));
            session.save(objUnCls); 
        }   
        catch (HibernateException e) 
        { 
            session.getTransaction().rollback();
            logger.error(sFileId, e);
            throw new Exception(e);
        }
    }
    
    private static int GetMaxRunId(Session session) {
        Criteria c = session.createCriteria(Runs.class, "Runs");
        c.setProjection(Projections.max("runId"));
        Number MaxValue = (Number) c.uniqueResult();
        if (MaxValue != null && MaxValue.intValue() > 0) {
            return MaxValue.intValue() + 1;
        }
        /// First Record
        return 1;
    }
}
