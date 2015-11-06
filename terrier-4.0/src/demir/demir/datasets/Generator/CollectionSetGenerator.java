/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.datasets.Generator;

import demir.tc.irbased.hibernate.*;
import java.io.File;
import java.nio.file.CopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
//import org.hibernate.classic.Session;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;


/**
 *
 * @author meltem
 */

/// Klasörlerde yer alan text dokumanlarından
/// Collection ve Set oluşturulmasını tüm dokumanların tek klasöre taşınmasını sağlar.

public class CollectionSetGenerator {

    public static void main(String[] args) {
        CollectionSetGenerator imp = new CollectionSetGenerator();
        
        
        /// REUTERS 115
//        String sFolderPath = "C:\\WORKS\\REUTERS\\reuters_cat115_modified\\training\\";
//        String sFolderType = "TRAIN";
        
//        String sFolderPath = "C:\\WORKS\\REUTERS\\reuters_cat115_modified\\test\\";
//        String sFolderType = "TEST";
        
        /// REUTERS 90
//        String sFolderPath = "C:\\WORKS\\REUTERS\\reuters_cat90_modified\\training\\";
//        String sFolderType = "TRAIN";
        
//        String sFolderPath = "C:\\WORKS\\REUTERS\\reuters_cat90_modified\\test\\";
//        String sFolderType = "TEST";
          
        /// ImageClef 2013 DataSet - 1
       //String sFolderPath = "D:\\Datasets\\ImageClef\\2013\\mc_TrainingSet\\";
       // String sFolderType = "TRAIN";

         //String sFolderPath = "D:\\Datasets\\ImageClef\\2013\\mc_TestSetGROUNDTRUTH\\";
         //String sFolderType = "TEST";
        
                /// REUTERS 7
       //int iCollectionId = 12;
       //int iSetId = 1;
      // String sFolderPath = "D:\\Datasets\\REUTERS\\reuters_cat7\\training\\";
      // String sFolderType = "TRAIN";
      // String sFolderPath = "D:\\Datasets\\REUTERS\\reuters_cat7\\test\\";
      // String sFolderType = "TEST";
       
//       String sFolderPath = "D:\\Datasets\\REUTERS\\reuters_cat10\\training\\";
//       String sFolderType = "TRAIN";
//       String sDestFolder = "D:\\Datasets\\REUTERS\\reuters_cat10\\training_terrier\\";
//       
//       int iCollectionId = 13;
//       int iSetId = 1;
//       String sFolderPath = "D:\\Datasets\\REUTERS\\reuters_cat10\\test\\";
//       String sFolderType = "TEST";
//       String sDestFolder = "D:\\Datasets\\REUTERS\\reuters_cat10\\test_terrier\\";
       
        
       /// REUTERS 90 WITHOUT UNKNOWN
       int iCollectionId = 15;
       int iSetId = 1;
       String sFolderPath = "D:\\Datasets\\REUTERS\\reuters_cat90_modified\\training\\";
       String sFolderType = "TRAIN";
       String sDestFolder = "D:\\Datasets\\REUTERS\\reuters_cat90_wo_unknown\\training_terrier\\";
       
       
       
        try {
            imp.ProcessFolder(iCollectionId, iSetId, sFolderPath, sFolderType, sDestFolder);
        } catch (Exception ex) {
            Logger.getLogger(CollectionSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Session session = null;

    public Session getSession() {
        return session;
    }
    
    public CollectionSetGenerator()
    {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
    }
    
    public void CloseSession()
    {
        session.close();
    }

    /// Folder Type points to TT_DOCS.FLAG -> TEST OR TRAIN
    /// Scan a folder and sub folders, sub-folders are class names
    public void ProcessFolder(int pCollectionId, int pSetId,
            String sFolderPath, String sFolderType,
            String sDestFolder) throws Exception {

        File file = new File(sFolderPath);
        File[] folders = file.listFiles();
        
        try {
            
            for (int i = 0; i < folders.length; i++) {
            File[] files = folders[i].listFiles();
            if(files != null)
            {
                for (int j = 0; j < files.length; j++) 
                {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(sFolderPath, "");
                    ImportDocLabels(pCollectionId, sFileId, sFileName, pSetId,
                            sLabel, sFolderType, session);
                    try
                    {
                        File dest = new File(sDestFolder + sFileName);
                        java.nio.file.Files.copy(files[j].toPath(), dest.toPath());
                    }
                    catch(java.nio.file.FileAlreadyExistsException ex)
                    {
                       System.out.println(sLabel + " " + files[j].getName());
                    }
                }
            }
        }
        } catch (HibernateException e) {
            Logger.getLogger(demir.datasets.Generator.CollectionSetGenerator.class.getName()).log(Level.SEVERE, null, e);
        }
        finally
        {
            
        }
    }

    public void ImportDocLabels(
            int pCollectionId, String pFileId, String pFileName, int pSetId,
            String label, String ttFlag, Session session) throws Exception {
        String labelExist = "T";
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (!AddDocToDocs(pCollectionId, pFileId, pFileName, session)) {
                AddDocToTtDocs(pCollectionId, pSetId, pFileId, ttFlag, session);
            }
            else
            {
                boolean checkTtDocExists = CheckTtDocExists(pCollectionId, pSetId, pFileId, ttFlag, session);
                System.out.println(pFileId + " exists");
                if (!checkTtDocExists) {
                    System.out.println(pFileName + " " + ttFlag + " exists");
                    UpdateTtDocs(pCollectionId, pSetId, pFileId, ttFlag, session);
                }
            }
            AddDocLabels(pCollectionId, pFileId, label, 0, session);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            String sMessage = pFileName + " " + label;
            Logger.getLogger(demir.datasets.Generator.CollectionSetGenerator.class.getName()).log(Level.SEVERE, sMessage, e);
            e.printStackTrace();
            throw e;
        }
    }

    private boolean AddDocToDocs(int pCollectionId, String pFileId, String pFileName, Session session) {
        boolean checkDocExists = CheckDocExists(pCollectionId, pFileId, session);
        if (!checkDocExists) {

            try {
                Docs objDoc = new Docs();
                objDoc.setId(new DocsId(pCollectionId, pFileId));
                objDoc.setFileName(pFileName);
                objDoc.setIri(pFileId);
                session.save(objDoc);
            } catch (HibernateException e) {
                throw e;
            }
        }
        return checkDocExists;
    }

    public void AddDocToTtDocs(int pCollectionId, int pSetId, String pFileId, String pTtFlag, Session session) {
        try {
            TtDocs objDoc = new TtDocs();
            objDoc.setId(new TtDocsId(pSetId, pCollectionId, pFileId));
            objDoc.setFlag(pTtFlag);
            session.save(objDoc);
        } catch (HibernateException e) {
            throw e;
        }
    }
    
    private void UpdateTtDocs(int pCollectionId, int pSetId, String pFileId, String pTtFlag, Session session) {
        try {
            /// hatalı
            Query q = session.createQuery("from TtDocs where id.collectionId = :pCollectionId and id.setId = :pSetId and id.fileId = :pFileId ");
            q.setParameter("pCollectionId", pCollectionId);
            q.setParameter("pSetId", pSetId);
            q.setParameter("pFileId", pFileId);
            TtDocs ttDocs = (TtDocs)q.list().get(0);
            ttDocs.setFlag(ttDocs.getFlag() + "-"+ pTtFlag);
            session.update(ttDocs);
        } catch (HibernateException e) {
            throw e;
        }
    }

    private void AddDocLabels(int pCollectionId, String pFileId, String pLabel, int pOrder, Session session) {
        try {
            DocLabels objDocLabel = new DocLabels();
            objDocLabel.setId(new DocLabelsId(pCollectionId, pFileId, pLabel));
            objDocLabel.setLabelOrder((short) pOrder);
            session.save(objDocLabel);
        } catch (HibernateException e) {
            throw e;
        }
    }

    private boolean CheckDocExists(int pCollectionId, String pFileId, Session session) {
        Criteria c = session.createCriteria(Docs.class, "Docs");
        c.createCriteria("Docs.collections", "collections");
        c.add(Restrictions.eq("collections.collectionId", pCollectionId));
        c.add(Restrictions.eq("Docs.id.fileId", pFileId));
        c.setProjection(Projections.rowCount());
        Number RowCnt = (Number) c.uniqueResult();
        if (RowCnt.intValue() > 0) {
            return true;
        }
        return false;
    }
    
    private boolean CheckTtDocExists(int pCollectionId, int pSetId, String pFileId, String pFlag, Session session) {
        Criteria c = session.createCriteria(TtDocs.class, "TtDocs");
        c.add(Restrictions.eq("TtDocs.id.collectionId", pCollectionId));
        c.add(Restrictions.eq("TtDocs.id.setId", pSetId));
        c.add(Restrictions.eq("TtDocs.id.fileId", pFileId));
        c.add(Restrictions.eq("TtDocs.flag", pFlag));
        c.setProjection(Projections.rowCount());
        Number RowCnt = (Number) c.uniqueResult();
        if (RowCnt.intValue() > 0) {
            return true;
        }
        return false;
    }
}
