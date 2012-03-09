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
		timingTest(rack,wordsOnRow,wordlist,1000);
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
			String s;
			if(wordlist[i].matches("[a-z]+")){
				neededChars[i]=Help.getHasChars(wordlist[i]);
				charFreq[i]=Help.createFreq(wordlist[i]);
				checkList[i]=Help.getCheckList(charFreq[i]);
			} else {
				System.out.println("StrangeWord: "+wordlist[i]);
				neededChars[i]=0;
				charFreq[i]=null;
				checkList[i]=null;
				System.out.println(neededChars[i]);
			}
				
		}
	}
	
	
	
	/**
	 * Returns a filtered wordlist where some of the words that can't be solutions have been filtered out.
	 * @param rack
	 * @param wordsOnRow
	 * @return
	 */
	public ArrayList<String> filter(String rack, String[] wordsOnRow){
		rack=rack.toLowerCase();
		wordsOnRow=Arrays.copyOf(wordsOnRow, wordsOnRow.length);
		for(int i=0;i<wordsOnRow.length;i++){
			wordsOnRow[i]=wordsOnRow[i].toLowerCase();
		}
//		System.out.println(rack+"1"+Arrays.toString(wordsOnRow));
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
//		System.out.println(" wildCards: "+wildcards);
		
		String sourceLetters=rack+Help.concatinate(wordsOnRow);
//		System.out.println("here"+s);
		final int hasChars;
		if(wildcards>0){
			hasChars=-1;
		} else {
			hasChars=Help.getHasChars(sourceLetters);
		}
		final byte[] hasFreq=Help.createFreq(sourceLetters);
//		System.out.println("hasFreq: "+Arrays.toString(hasFreq));
		ArrayList<String> res=new ArrayList<String>();
//		System.out.println(rack+" "+Arrays.toString(wordsOnRow)+" "+sourceLetters);
		for(int i=0;i<wordlist.length;i++){
			if(neededChars[i]!=0){
//				if(wordlist[i].equals("coie.gf")){
//					System.out.println("WTF!");
//				}
				if(Help.hasNeededChars(neededChars[i], hasChars)){//check that the word contains no character that isn't in s
					//if(hasCharFreq(charFreq[i],hasFreq)){//check that the word has no more of a char type than in s
					if(Help.hasCharFreq2(checkList[i],charFreq[i],hasFreq,wildcards)){//check that the word has no more of a char type than in s
//						if(wordsOnRow.length==0 || containsAtleastOne(wordlist[i],wordsOnRow)){//check that the word contains at least one of the "words" on the row
							//passed all the filters, so it's more likely to be a correct word
							res.add(wordlist[i]);
//						}
					}
				}
			}
		}
		return res;
	}
	
//	/**
//	 * Checks that word contains at least one of the strings from wordsOnRow, but returns true if
//	 * wordsonrow has length 0.
//	 * @param word
//	 * @param wordsOnRow
//	 * @return
//	 */
//	private static boolean containsAtleastOne(String word, final String[] wordsOnRow){
////		if(word==null){ word=""; }
//		for(int i=0;i<wordsOnRow.length;i++){
//			if(word.length()>wordsOnRow[i].length() //the word must be longer
//					&& word.contains(wordsOnRow[i])){
//				return true;
//			}
//		}
//		return false;
//	}
	
}
