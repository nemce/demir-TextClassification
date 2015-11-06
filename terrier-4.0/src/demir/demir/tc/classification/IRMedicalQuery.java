/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.tc.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 *
 * @author nefise
 */
public class IRMedicalQuery {
    
    int iCollectionId = -1;
    String sQueryId = null;
    String sDocNo = null;
    Double sRetrievalScore = null;
    String sDocTxt = null;
    ArrayList<IRMedicalQuery> listQRes = new ArrayList<IRMedicalQuery>() {};
    ArrayList<String> listICD = new ArrayList<String>();
    String sQDType = null;
    ArrayList<Double> listICDAssignedValues = new ArrayList<Double>();
    ArrayList<String> listICDAssignedKeys = new ArrayList<String>();

    public ArrayList<String> getListICDAssignedKeys() {
        return listICDAssignedKeys;
    }

    public void setListICDAssignedKeys(ArrayList<String> listICDAssignedKeys) {
        this.listICDAssignedKeys = listICDAssignedKeys;
    }

    public ArrayList<Double> getListICDAssignedValues() {
        return listICDAssignedValues;
    }

    public void setListICDAssignedValues(ArrayList<Double> listICDAssignedValues) {
        this.listICDAssignedValues = listICDAssignedValues;
    }
    
    public void ClearListICDAssigned()
    {
        this.listICDAssignedKeys = new ArrayList<String>();
        this.listICDAssignedValues = new ArrayList<Double>();
    }

    public ArrayList<String> getListICD() {
        return listICD;
    }

    public void setListICD(ArrayList<String> listICD) {
        this.listICD = listICD;
    }
    
    public ArrayList<IRMedicalQuery> getListQRes() {
        return listQRes;
    }

    public void setListQRes(ArrayList<IRMedicalQuery> listQRes) {
        this.listQRes = listQRes;
    }

    public String getsDocNo() {
        return sDocNo;
    }

    public void setsDocNo(String sDocNo) {
        this.sDocNo = sDocNo;
    }

    public String getsDocTxt() {
        return sDocTxt;
    }

    public void setsDocTxt(String sDocTxt) {
        this.sDocTxt = sDocTxt;
    }

    public String getsQDType() {
        return sQDType;
    }

    public void setsQDType(String sQDType) {
        this.sQDType = sQDType;
    }

    public String getsQueryId() {
        return sQueryId;
    }

    public void setsQueryId(String sQueryId) {
        this.sQueryId = sQueryId;
    }

    public Double getsRetrievalScore() {
        return sRetrievalScore;
    }

    public void setsRetrievalScore(Double sRetrievalScore) {
        this.sRetrievalScore = sRetrievalScore;
    }
    
    public int getCollectionId() {
        return iCollectionId;
    }

    public void setCollectionId(int iCollectionId) {
        this.iCollectionId = iCollectionId;
    }
    
    public void SelRecLabelbyDocId(ClassificationParameters prm,  Session session) throws Exception
    {
        if(prm.getLabelSearchType().equals(prm.LABEL_SEARCH_TYPE_FILE)) {
            this.SelRecLabelbyDocId(prm.getKeyFileName());
        }
        else {
            if(this.getCollectionId() == -1)
            {
                throw new Exception(this.getsDocNo() + " Collection Id is not set.");
            }
            this.SelRecLabelbyDocId(session);
        }
    }
    
    private void SelRecLabelbyDocId(
      Session session)
    {
    try {
         //ArrayList listDocICD = demir.dbconnection.ConnectMSSqlServer.SelectICDofDoc(con, this.getsDocNo());
        
        
       ArrayList listDocICD = demir.dbconnection.DBFunctions.SelectLabelofDoc(this.getsDocNo(), this.getCollectionId(), session);
        
        for (int i = 0; i < listDocICD.size(); i++)
        {
            String sICD = (String) listDocICD.get(i);
            if (!this.getListICD().contains(sICD))
            {
                this.getListICD().add(sICD);
            }
            else
            {

            }
        }
    } catch (Exception ex) {
        Logger.getLogger(IRMedicalQuery.class.getName()).log(Level.SEVERE, null, ex);
    }

    }

    private void SelRecLabelbyDocId(
      String sKeyFileName)
    {
    try {

        String sLine = "";
        String [] sValues = null;

        try {
        FileReader fr = new FileReader(sKeyFileName);
        BufferedReader br = new BufferedReader(fr);

        while((sLine = br.readLine()) != null)
        {

            int iSepIndex = sLine.indexOf('\t');
            String sTempDocNo = sLine.substring(0, iSepIndex);
            //if(sLine.startsWith(this.sDocNo))
            if(sTempDocNo.equals(this.sDocNo))
            {
                sLine = sLine.replace(this.sDocNo, "");
                sLine = sLine.trim();
                sValues = sLine.split(","); 
                //System.out.print(sDocNo + "\tXXX\t");
//                    int iIndex = 0;
//                    while(sLine != null && sLine != "")
//                    {
//                        int iIndexOfSep = sLine.indexOf(',');
//                        if(iIndexOfSep > -1)
//                        {
//                            sValues[iIndex++] = sLine.substring(0, iIndexOfSep);
//                            sLine = sLine.substring(iIndexOfSep + 1, sLine.length() - (iIndexOfSep+1));
//                        }
//                        else
//                        {
//                            sValues[iIndex] = sLine;
//                            sLine = null; break;
//                        }
//                    }
                break;
            }
        }
        br.close();fr.close();
        } catch (IOException ex) {
        Logger.getLogger(IRMedicalQuery.class.getName()).log(Level.SEVERE, null, ex);
        } 

        for (int i = 0; i < sValues.length; i++)
        {
            String sICD = sValues[i];
            if (!this.getListICD().contains(sICD))
            {
                this.getListICD().add(sICD);
                //System.out.print(sICD + ",");
            }
            else
            {

            }
        }
        //System.out.println();
    } catch (Exception ex) {
        Logger.getLogger(IRMedicalQuery.class.getName()).log(Level.SEVERE, null, ex);
    }

    } 
    
}
