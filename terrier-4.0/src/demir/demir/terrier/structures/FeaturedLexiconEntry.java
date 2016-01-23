/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.terrier.structures;

import org.terrier.structures.BasicLexiconEntry;
import org.terrier.structures.BitFilePosition;
import org.terrier.structures.LexiconEntry;

/**
 *
 * @author nmeltem
 */
public class FeaturedLexiconEntry extends BasicLexiconEntry{
    double MIValue = 0.0;

    public FeaturedLexiconEntry() {
    }

    public FeaturedLexiconEntry(int tid, int _n_t, int _TF) {
        super(tid, _n_t, _TF);
    }

    public FeaturedLexiconEntry(int tid, int _n_t, int _TF, byte fileId, long _startOffset, byte _startBitOffset) {
        super(tid, _n_t, _TF, fileId, _startOffset, _startBitOffset);
    }

    public FeaturedLexiconEntry(int tid, int _n_t, int _TF, byte fileId, BitFilePosition offset) {
        super(tid, _n_t, _TF, fileId, offset);
    }
    
    public FeaturedLexiconEntry(BasicLexiconEntry le)
    {
        super(le.termId, le.getDocumentFrequency(), le.getFrequency(), le.getFileNumber(), 
                le.startOffset, le.startBitOffset);
    }
    
    
    public double GetMiValue()
    {
        return MIValue;
    }
    public void SetMiValue(double d)
    {
        this.MIValue = d;
    }
}
