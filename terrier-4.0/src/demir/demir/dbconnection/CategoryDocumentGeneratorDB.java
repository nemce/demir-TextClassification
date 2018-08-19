/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.dbconnection;

import demir.datasets.Generator.ImportFileText;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nmeltem
 */
public class CategoryDocumentGeneratorDB {
    public static ResultSet SelRecDocsOfLabel(int iCollectionId, String sFlag, String sLabel,
            Connection con) throws SQLException
    {
            String sSql = "select tt_docs.file_id  from tt_docs, doc_labels\n" +
                "where tt_docs.collection_id = " + iCollectionId + "\n" +
                "and set_id = 1\n" +
                "and tt_docs.COLLECTION_ID = doc_labels.COLLECTION_ID\n" +
                "and tt_docs.FILE_ID = doc_labels.FILE_ID\n" +
                "and doc_labels.LABEL = '" + sLabel + "'\n" +
                "and tt_docs.FLAG = 'TRAIN'";
            
        Statement st; 
        try {
            st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery(sSql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(ImportFileText.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
}
