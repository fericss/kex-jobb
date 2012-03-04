import java.util.ArrayList;
import java.util.Arrays;



/**
 * Uses stuff from FreqList and FredricTestStuff to make a very fast and effective filter of the wordlist.
 * 
 * Performance:
 * rack: fihhdf  wordsOnRow: [hej, d]  wordListLength: 38619  constructionTime(ms): 10  filterTime per word(ms): 0.1323  filterRepeats: 10000
 * rack: fihhdf  wordsOnRow: [hej, d, low]  wordListLength: 38619  constructionTime(ms): 10  filterTime per word(ms): 0.1825  filterRepeats: 10000
 * it takes 0,2 milliseconds to filter the wordlist from one row
 * @author MJB1
 *
 */
public class FastFilter {
	final String[] wordlist;
	final int[] neededChars;
	final byte[][] charFreq;
	
	
	/**
	 * for testing performance
	 * or other stuff in this class.
	 * @param args
	 */
	public final  static void main(String[] args){
		String[] wordlist=new WordFinder().getWordlist();
		timingTest(wordlist,10000);
	}
	
	public static void timingTest(String[] wordlist,int filterRepeats){
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		FastFilter ff=new FastFilter(wordlist);
		t2=System.currentTimeMillis();
		String[] wordsOnRow={"hej","d","low"};
		String rack="fihhdf";
		ArrayList<String> st=null;
		for(int i=0;i<filterRepeats;i++){
			st=ff.filter(rack, wordsOnRow);
			String s=st.get(0);
		}
		t3=System.currentTimeMillis();
		for(String s:st){
			System.out.println(s);
		}
		System.out.println(
				"rack: "+rack
				+"  wordsOnRow: "+Arrays.toString(wordsOnRow)
				+"  wordListLength: "+wordlist.length
				+"  constructionTime(ms): "+(t2-t1)
				+"  filterTime per word(ms): "+(((double)(t3-t2))/((double)filterRepeats))
				+"  filterRepeats: "+filterRepeats
				);
	}
	
	/**
	 * constructs a FastFilter.
	 * @param _wordlist
	 */
	public FastFilter(String[] _wordlist){
		//initialize
		wordlist=_wordlist;
		neededChars=new int[wordlist.length];
		charFreq=new byte[wordlist.length][];
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			neededChars[i]=getHasChars(wordlist[i]);
			charFreq[i]=createFreq(wordlist[i]);
		}
	}
	
	/**
	 * Returns a filtered wordlist where some of the words that can't be solutions have been filtered out.
	 * @param rack
	 * @param wordsOnRow
	 * @return
	 */
	public ArrayList<String> filter(String rack, String[] wordsOnRow){
		String s=rack+concatinate(wordsOnRow);
		final int hasChars=getHasChars(s);
		final byte[] hasFreq=createFreq(s);
		
		ArrayList<String> res=new ArrayList<String>();
		
		for(int i=0;i<wordlist.length;i++){
			if(hasNeededChars(neededChars[i], hasChars)){//check that the word contains no character that isn't in s
				if(hasCharFreq(charFreq[i],hasFreq)){//check that the word has no more of a char type than in s
					if(containsAtleastOne(wordlist[i],wordsOnRow)){//check that the word contains at least one of the "words" on the row
						//passed all the filters, so it's more likely to be a correct word
						res.add(wordlist[i]);
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * Checks that word contains at least one of the strings from wordsOnRow.
	 * @param word
	 * @param wordsOnRow
	 * @return
	 */
	private static boolean containsAtleastOne(final String word, final String[] wordsOnRow){
		for(int i=0;i<wordsOnRow.length;i++){
			if(word.length()>wordsOnRow[i].length() //the word must be longer
					&& word.contains(wordsOnRow[i])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks that all the "neededChars" is in "hasChars". 
	 * @param neededChars
	 * @param hasChars
	 * @return
	 */
	private static boolean hasNeededChars(final int neededChars,final int hasChars){
		return (neededChars & hasChars) == neededChars;
	}
	
	/**
	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
	 * @param needFreq
	 * @param hasFreq
	 * @return
	 */
	private static boolean hasCharFreq(final byte[] needFreq, final byte[] hasFreq){
		for(int i=0;i<needFreq.length;i++){
			if(needFreq[i]>hasFreq[i]){
				//if there are less letters available than needed...
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Uses an int as a bit array to store which chars are in the word.
	 * It's then very fast to check if one string contains all the letter types by
	 * using: (a&b)==a.
	 * If that returns true then b contains all letter types in a.
	 * @param s
	 * @return
	 */
	private static int getHasChars(String s){
		int res=0;
		for(int i=0;i<s.length();i++){
			res= res | (1<<(s.charAt(i)-'a'));
		}
		return res;
	}
	
	/**
	 * Counts the frequency of each letter and stores the count for each letter in a byte array, where 
	 * index 0 contains the count for 'a'.
	 * @param s
	 * @return
	 */
	private static byte[] createFreq(final String s){
		byte[] freq=new byte['z'-'a'+1];//size of alphabet
		for(int i=0;i<s.length();i++){
			freq[s.charAt(i)-'a']++;
		}
		return freq;
	}
	
	/**
	 * help method for concatinating all strings in a string array.
	 * Should be moved to a help methods class.
	 * @param sarr
	 * @return
	 */
	private String concatinate(final String[] sarr){
		final StringBuilder sb=new StringBuilder();
		for(int i=0;i<sarr.length;i++){
			sb.append(sarr[i]);
		}
		return sb.toString();
	}
	
}
