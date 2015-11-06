/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.dbconnection;

import java.util.ArrayList;

import demir.tc.irbased.hibernate.*;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

/**
 *
 * @author meltem
 */
public class DBFunctions {
    
    public static ArrayList SelectLabelofDoc(
            String sFileId, int CollectionId, Session session)
    {
        //session.beginTransaction();
        Criteria c = session.createCriteria(DocLabels.class, "DocLabels"); 
        c.setProjection(Projections.property("DocLabels.id.label"));
        c.add(Restrictions.eq("DocLabels.id.collectionId", CollectionId));
        c.add(Restrictions.eq("DocLabels.id.fileId", sFileId));
        /// Added 10.12.2013
        /// Image Clef Modality Claasification veri setinde aynı dokumanlar train ve test sette yer aldığı için eklenmiştir.
        //c.add(Restrictions.eq("DocLabels.tagType", "TR"));
        /// Added 10.12.2013
        List lsDocLabels = c.list();
        //session.getTransaction().commit();
        ArrayList lsaDocLabel = new ArrayList<String>();
        if(lsDocLabels != null)
        {
            for(int i = 0; i < lsDocLabels.size(); i++)
            {
                lsaDocLabel.add((String)lsDocLabels.get(i));
            }                
        }
        return lsaDocLabel;
    }
    
    public static void main(String [] args)
    {
        //SelectLabelofDoc("CC", 4);
    }
}
