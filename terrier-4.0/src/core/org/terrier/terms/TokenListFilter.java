/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terrier.terms;

import gnu.trove.THashSet;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import static org.terrier.terms.Stopwords.INTERN_STOPWORDS;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.Files;

/**
 *
 * @author nmeltem
 * 
 * Bu sınıf index oluşurken sadece belirli bir kelime kümesinin 
 * indexe eklenebilmesi için oluşturulmuştur.
 * TTCP veri setinde yer alan 7508 term üzerinden index oluşturlmak istenmiştir.
 */
public class TokenListFilter implements TermPipeline{
    /** The logger used */
	private static Logger logger = Logger.getLogger(TokenListFilter.class);
	
	/** The next component in the term pipeline. */
	protected final TermPipeline next;

	/** The hashset that contains all the TOKENS words.*/
	protected final THashSet<String> whiteList = new THashSet<String>();
	/** 
	 * Makes a new token filter termpipeline object. The whiteToken 
	 * file is loaded from the application setup file, 
	 * under the property <tt>whitelist.filename</tt>.
	 * @param _next TermPipeline the next component in the term pipeline.
	 */
	public TokenListFilter(final TermPipeline _next)
	{
		this(_next, ApplicationSetup.getProperty("whitetokens.filename", "whitetokens-list.txt"));
	}
        
        /** Makes a new token filter term pipeline object. The whiteToken file(s)
	  * are loaded from the filename parameter. If the filename is not absolute, it is assumed
	  * to be in TERRIER_SHARE. StopwordsFile is split on \s*,\s* if a comma is found in 
	  * StopwordsFile parameter.
	  * @param _next TermPipeline the next component in the term pipeline
          * @param whiteTokensFile  The filename(s) of the file to use as the whiteToken list. Split on comma,
	  * and passed to the (TermPipeline,String[]) constructor.
	  */	
	public TokenListFilter(final TermPipeline _next, final String whiteTokensFile)
	{
		this.next = _next;
			loadList(whiteTokensFile);
	}
        
        /** Loads the specified  file. Used internally by Stopwords(TermPipeline, String[]).
        * @param whiteTokenFilename
	*/
	public void loadList(String whiteTokenFilename)
	{
		//get the absolute filename
		whiteTokenFilename = ApplicationSetup.makeAbsolute(whiteTokenFilename, ApplicationSetup.TERRIER_SHARE);
		//determine encoding to use when reading the whiteToken files
		String whiteTokenEncoding = 
			ApplicationSetup.getProperty("whiteToken.encoding", 
				ApplicationSetup.getProperty("trec.encoding", null));
		try {
			//use sys default encoding if none specified
			BufferedReader br = whiteTokenEncoding != null
				? Files.openFileReader(whiteTokenFilename, whiteTokenEncoding)
				: Files.openFileReader(whiteTokenFilename);
			String word;
			while ((word = br.readLine()) != null)
			{
				word = word.trim();
				if (word.length() > 0)
				{
					if (INTERN_STOPWORDS)
						word = word.intern();
					whiteList.add(word);
				}
			}
			br.close();
		} catch (IOException ioe) {
			logger.error("Errror: Input/Output Exception while reading whiteToken list ("+whiteTokenFilename+") :  Stack trace follows.",ioe);
			
		}
		if (whiteList.size() == 0)
            logger.error("Error: Empty whiteToken file was used ("+whiteTokenFilename+")");
	}


	/** Clear all whiteToken from this whiteToken list object. 
	  * @since 1.1.0 */
	public void clear()
	{
		whiteList.clear();	
	}

	/** Returns true is term t is a whiteToken */
	public boolean isWhiteToken(final String t)
	{
		return whiteList.contains(t);
	}

	
	/** 
	 * Checks to see if term t is a whiteToken. If so,the term is passed on to 
         * the next TermPipeline object. Otherwise, the TermPipeline
	 * is exited. This is the TermPipeline implementation part of this object.
	 * @param t The term to be checked.
	 */
	public void processTerm(final String t)
	{
		if (whiteList.contains(t))
                    next.processTerm(t);
                return;
	}
	
	/** {@inheritDoc} */
	public boolean reset() {
		return next.reset();
	}

	
}
