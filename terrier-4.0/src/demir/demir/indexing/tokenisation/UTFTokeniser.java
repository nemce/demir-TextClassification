/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.indexing.tokenisation;

import java.io.IOException;
import java.io.Reader;
import org.terrier.indexing.tokenisation.TokenStream;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author nmeltem
 * ETC DOSYASINA PARAMETRESİ EKLENEREK CAGIRILIR.
 * tokeniser=demir.indexing.tokenisation.UTFTokeniser 
 */
public class UTFTokeniser extends org.terrier.indexing.tokenisation.Tokeniser{
    
        /** The maximum number of digits that are allowed in valid terms. */
	protected final static int maxNumOfDigitsPerTerm = 0;
	/**
	 * The maximum number of consecutive same letters or digits that are
	 * allowed in valid terms.
	 */
	protected final static int maxNumOfSameConseqLettersPerTerm = 3;
	/**
	 * Whether tokens longer than MAX_TERM_LENGTH should be dropped.
	 */
	protected final static boolean DROP_LONG_TOKENS = true;
        
        /**
	 *  tokens shorter than MIN_TERM_LENGTH should be dropped.
	 */
	protected final static int MIN_TERM_LENGTH = 3;
	
	protected static final boolean LOWERCASE = Boolean.parseBoolean(ApplicationSetup.getProperty("lowercase", "true"));
	protected static final int MAX_TERM_LENGTH = ApplicationSetup.MAX_TERM_LENGTH;
        
        @Override
	public TokenStream tokenise(final Reader reader) {
		return new UTFTokenStream(reader);
	}

	/**
	 * Checks whether a term is shorter than the maximum allowed length,
	 * and whether a term does not have many numerical digits or many
	 * consecutive same digits or letters.
	 * @param s String the term to check if it is valid.
	 * @return String the term if it is valid, otherwise it returns null.
	 */
	static String check(String s) {
		//if the s is null
		//or if it is longer than a specified length
		s = s.trim();
		final int length = s.length();
		int counter = 0;
		int counterdigit = 0;
		int ch = -1;
		int chNew = -1;
                if (length < MIN_TERM_LENGTH) return "";
		for(int i=0;i<length;i++)
		{
			chNew = s.charAt(i);
			if (Character.isDigit(chNew))
				counterdigit++;
			if (ch == chNew)
				counter++;
			else
				counter = 1;
			ch = chNew;
			/* if it contains more than 4 consequtive same letters,
			   or more than 4 digits, then discard the term. */
			if (counter > maxNumOfSameConseqLettersPerTerm
				|| counterdigit > maxNumOfDigitsPerTerm)
				return "";
		}
                
		return LOWERCASE ? s.toLowerCase() : s;
	}

        
        static class UTFTokenStream extends TokenStream
	{
		int ch;
		boolean eos = false;
		int counter = 0;
		Reader br;

		public UTFTokenStream(Reader _br)
		{
			this.br = _br;
			if (this.br == null)
			{
				this.eos = true;
			}
		}
		
		@Override
		public boolean hasNext() {
			return ! eos;
		}
		
		@Override
		public String next() 
		{
			try{
				ch = this.br.read();
				while(ch != -1)
				{			
					/* skip non-alphanumeric charaters */
					while ( ch != -1 && ! (Character.isLetterOrDigit((char)ch) || Character.getType((char)ch) == Character.NON_SPACING_MARK || Character.getType((char)ch) == Character.COMBINING_SPACING_MARK)
						) 
						 /* removed by Craig: && ch != '<' && ch != '&' */
			
					{
						ch = br.read();
						counter++;
					}
					StringBuilder sw = new StringBuilder(MAX_TERM_LENGTH);
					//now accept all alphanumeric charaters
					while (ch != -1 && (Character.isLetterOrDigit((char)ch) || Character.getType((char)ch) == Character.NON_SPACING_MARK || Character.getType((char)ch) == Character.COMBINING_SPACING_MARK))
					{
						/* add character to word so far */
						sw.append((char)ch);
						ch = br.read();
						counter++;
					}
					if (sw.length() > MAX_TERM_LENGTH)
						if (DROP_LONG_TOKENS)
							return null;
						else
							sw.setLength(MAX_TERM_LENGTH);
					String s = check(sw.toString());
					if (s.length() > 0)
						return s;
				}
				eos = true;
				return null;
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
	}
}
