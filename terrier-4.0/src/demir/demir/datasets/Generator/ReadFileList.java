/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demir.datasets.Generator;

import demir.datasets.Generator.CollectionSetGenerator;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author nmeltem
 */
public class ReadFileList {
    
    public static void main(String[] args)
    {
        try {
            //ProcessFolder("D:\\Datasets\\ImageClef\\2013\\mc_TestSetGROUNDTRUTH\\");
            //int iCollectionId = 9;
            //int iSetId = 1;
            //AddFilesToTtDocs("D:\\Datasets\\turkish_trec_med\\Train6\\", "TRAIN");
            int iCollectionId = 12;
            int iSetId = 1;
            AddFilesToTtDocs(
                    iCollectionId,
                    iSetId,
                    "D:\\Datasets\\REUTERS\\reuters_cat7\\training\\",
                    "TRAIN");
            
        } catch (Exception ex) {
            Logger.getLogger(ReadFileList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void AddFilesToTtDocs(
            int iCollectionId,
            int iSetId,
            String sFolderPath, 
            String sFlag) throws Exception {

        File file = new File(sFolderPath);
        CollectionSetGenerator imp = new CollectionSetGenerator();
        Transaction tx = null;
        
        try {
            File[] files = file.listFiles();
            if(files != null)
            {
                tx = imp.getSession().beginTransaction();
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(sFolderPath, "");
                    System.out.println(sFileId + "\t" + sLabel + "\t" + sFileName);
                    
                    imp.AddDocToTtDocs(iCollectionId, iSetId, sFileId, sFlag, imp.session);
                }
                tx.commit();
        }
        } catch (HibernateException e) {
            Logger.getLogger(demir.datasets.Generator.CollectionSetGenerator.class.getName()).log(Level.SEVERE, null, e);
            if (tx != null) {
                tx.rollback();
            }
        }
        finally
        {
            imp.CloseSession();
        }
    }
    
    
    public static void ProcessFolder(
            String sFolderPath) throws Exception {

        File file = new File(sFolderPath);
        File[] folders = file.listFiles();
        
        try {
        for (int i = 0; i < folders.length; i++) {
            File[] files = folders[i].listFiles();
            if(files != null)
            {
                for (int j = 0; j < files.length; j++) {
                    String sFileName = files[j].getName();
                    String sFileId = sFileName;
                    if (sFileName.lastIndexOf('.') > -1) {
                        sFileId = sFileName.substring(0, sFileName.lastIndexOf('.'));
                    }
                    String sPath = files[j].getParent();
                    String sLabel = sPath.replace(sFolderPath, "");
                    System.out.println(sFileId + "\t" + sLabel + "\t" + sFileName);
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
}
