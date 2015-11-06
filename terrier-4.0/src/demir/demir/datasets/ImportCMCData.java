/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.tc.irbased.GenerateSets;


import demir.datasets.Generator.CollectionSetGenerator;
import demir.tc.irbased.hibernate.TtDocs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author nmeltem
 */
public class ImportCMCData {
    
    String sTrainFolder = "D:\\Datasets\\CMC\\train2\\";
    
    public static void main(String[] args) {
        ImportCMCData imp = new ImportCMCData();
        int iCollectionId = 7;
        int iSetId = 1;
        String sTrainFilePath = "D:\\Datasets\\CMC\\train_set\\";
        String sKeyFilePath = "D:\\Datasets\\CMC\\CMCKeyFile2.txt";
        String sFolderType = "TEST";
          
        try {
            //imp.ProcessCMCKeyFile(iCollectionId, iSetId, sKeyFilePath, sFolderType);
            imp.UpdateTrainFiles(iCollectionId, iSetId, sTrainFilePath);
        } catch (Exception ex) {
            Logger.getLogger(CollectionSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ProcessCMCKeyFile(int pCollectionId, int pSetId,
            String sKeyFilePath, String sFolderType) throws Exception {

        Session session = null;
        try {
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            session = sessionFactory.openSession();

            String sLine = "";
            FileReader fr = new FileReader(sKeyFilePath);
            BufferedReader br = new BufferedReader(fr);
            CollectionSetGenerator oImportStatements = new CollectionSetGenerator();
            while ((sLine = br.readLine()) != null) {
                String sLabels = sLine.substring(sLine.indexOf('\t')+1, sLine.length());
                String fileName = sLine.substring(0, sLine.indexOf('\t'));
                File fTemp = new File(sTrainFolder +  fileName + ".txt");
                if(fTemp.exists()) sFolderType = "TRAIN";
                String[] labelList = sLabels.split(",");
                for(int i = 0; i < labelList.length; i++)
                {
                      oImportStatements.ImportDocLabels(pCollectionId, fileName, fileName, pSetId, 
                              labelList[i], sFolderType, session);
                }             
            }
            br.close();
            fr.close();
        } 
        catch (HibernateException ex) {
            Logger.getLogger(demir.tc.irbased.GenerateSets.ImportCMCData.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(demir.tc.irbased.GenerateSets.ImportCMCData.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            session.close();
        }
    }
    
    /// Bu fonkisyon ile CMC verisinin TRAIN dosyaları veri tabanında güncellenmiştir.
    private void UpdateTrainFiles(int collectionId, int setId, String sPath)
    {
        Session session = null;
        Transaction tx = null;
        try {
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            File fFolder = new File(sPath);
            File[] files = fFolder.listFiles();
            for(int i = 0; i < files.length; i++)
            {
                String sFileName = files[i].getName();
                sFileName = sFileName.replace(".txt", "");
                UpdateTtDocs(collectionId, setId, sFileName, "TRAIN", session);
            }
            tx.commit();
        } 
        catch (HibernateException ex) {
            tx.rollback();
            Logger.getLogger(demir.tc.irbased.GenerateSets.ImportCMCData.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            session.close();
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
            ttDocs.setFlag("TRAIN");
            session.update(ttDocs);
        } catch (HibernateException e) {
            throw e;
        }
    }
}
