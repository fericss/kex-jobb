import java.util.ArrayList;
import java.util.Arrays;



/**
 * Uses stuff from FreqList and FredricTestStuff to make a very fast and effective filter of the wordlist.
 * You only need to construct FastFilter once (the constructor is only dependent on the wordlist) 
 * and then use the filter method with different inputs.
 * 
 * Performance:
 * rack: fihhdf  wordsOnRow: [hej, d]  wordListLength: 38619  
 * constructionTime(ms): 19  filterTime per word(ms): 0.1547  filterRepeats: 10000.
 * 
 * rack: fihhdf  wordsOnRow: [hej, d, low]  wordListLength: 38619  
 * constructionTime(ms): 19  filterTime per word(ms): 0.1104  filterRepeats: 10000.
 * 
 * It takes less than 0,2 milliseconds to filter the wordlist from one row.
 * @author mbernt.mbernt
 *
 */
public class FastFilter {
	final String[] wordlist; //the wordlist
	final int[] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	final byte[][] charFreq;//list of the frequency of each letter in each word
	final byte[][] checkList;//only need to check chars in the word
	
	
	/**
	 * for testing performance
	 * or other stuff in this class.
	 * @param args
	 */
	public final  static void main(String[] args){
		String[] wordlist=new WordFinder().getWordlist();
		String[] wordsOnRow={"hej","d","low"};
		String rack="fihhdf";
		timingTest(rack,wordsOnRow,wordlist,10000);
	}
	
	public static void timingTest(String rack,String[] wordsOnRow, String[] wordlist,int filterRepeats){
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		FastFilter ff=new FastFilter(wordlist);
		t2=System.currentTimeMillis();
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
		checkList=new byte[wordlist.length][];
		
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			neededChars[i]=getHasChars(wordlist[i]);
			charFreq[i]=createFreq(wordlist[i]);
			checkList[i]=getCheckList(charFreq[i]);
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
//				if(hasCharFreq(charFreq[i],hasFreq)){//check that the word has no more of a char type than in s
				if(hasCharFreq2(checkList[i],charFreq[i],hasFreq)){//check that the word has no more of a char type than in s
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
	
//	/**
//	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
//	 * @param needFreq
//	 * @param hasFreq
//	 * @return
//	 */
//	private static boolean hasCharFreq(final byte[] needFreq, final byte[] hasFreq){
//		for(int i=0;i<needFreq.length;i++){
//			if(needFreq[i]>hasFreq[i]){
//				//if there are less letters available than needed...
//				return false;
//			}
//		}
//		return true;
//	}
	
	/**
	 * Returns a list of the indexes that contain a number greater than zero.
	 * @param charFreq
	 * @return
	 */
	private static byte[] getCheckList(final byte[] charFreq){
		byte size=0;
		for(byte i=0;i<charFreq.length;i++){
			if(charFreq[i]>0){
				size++;
			}
		}
		final byte[] res=new byte[size];
		byte index=0;
		for(byte i=0;i<charFreq.length;i++){
			if(charFreq[i]>0){
				res[index]=i;
				index++;
			}
		}
		return res;
	}
	
	/**
	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
	 * Only checks the letters that is in the word.
	 * @param checkList
	 * @param needFreq
	 * @param hasFreq
	 * @return
	 */
	private static boolean hasCharFreq2(final byte[] checkList,final byte[] needFreq, final byte[] hasFreq){
		int i;
		for(int j=0;j<checkList.length;j++){
			i=checkList[j];
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
	public static byte[] createFreq(final String s){
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
