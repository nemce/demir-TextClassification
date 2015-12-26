/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.featureselection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nmeltem
 */
public class ClassInfo {
    String ClassName;
    int docCnt =0;
    ArrayList<String> listDocs = new ArrayList<String>();

    public ClassInfo(String Label) {
       this.ClassName = Label;
    }
    
    public void AddTerm(String sTerm, String sDoc)
    {
        if(!listDocs.contains(sDoc))
        {
            listDocs.add(sDoc);
            docCnt++;
        }
    }
    
    public double GetClassDocFrequency()
    {
        return (double)docCnt;
    }
}
