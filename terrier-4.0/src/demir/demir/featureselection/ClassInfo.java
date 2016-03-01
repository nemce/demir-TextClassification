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
    private String ClassName;
    
    // Sınıftaki doküman sayisi
    private int docCnt =0;
    // toplam(her doküman bir sınıfta kaç kez geçiyor)
    // toplam(bir dokuman bir sınıfta kaç kelime ile temsil ediliyor.)
    private int docFreq = 0;
    // toplam(bir dokuman bir sınıfta bir kelimenin kaç kez tekrar edilmesi ile temsil ediliyor.)
    private int docTermOccurence = 0;
    
    ArrayList<String> listDocs = new ArrayList<String>();

    public ClassInfo(String Label) {
       this.ClassName = Label;
    }
    
    public void AddTerm(String sTerm, String sDoc, int occurence)
    {
        if(!listDocs.contains(sDoc))
        {
            listDocs.add(sDoc);
            docCnt++;
        }
        docFreq++;
        docTermOccurence += occurence;
    }
    
    public double GetClassDocCnt()
    {
        return (double)docCnt;
    }
    
    public double GetClassDocFrequency()
    {
        return (double)docFreq;
    }
    
    public double GetClassTermDocOccurence()
    {
        return (double)docTermOccurence;
    }
}
