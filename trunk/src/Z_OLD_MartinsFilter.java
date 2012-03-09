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
@Deprecated
public class Z_OLD_MartinsFilter {
	final String[] wordlist; //the wordlist
	final int[][] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	
	
	/**
	 * for testing performance
	 * or other stuff in this class.
	 * @param args
	 */
	public final  static void main(String[] args){
		String[] wordlist=new WordFinder().getWordlist();
		Z_OLD_MartinsFilter ff2=new Z_OLD_MartinsFilter(wordlist);
		
//		ff2.print();
		System.out.println(ff2.getAvarageLength());
		System.out.println(ff2.getAvarageWordLength());
		System.out.println(ff2.maxLength());
		
		String[] wordsOnRow={"hej","d","low"};
		String rack="fi.hdf";
		timingTest(rack,wordsOnRow,wordlist,1000);
	}
	
	public int maxLength(){
		int max=0;
		for(int i=0;i<neededChars.length;i++){
			if(neededChars[i].length>max){
				max=neededChars[i].length;
			}
		}
		return max;
	}
	
	public double getAvarageLength(){
		double avg=0;
		double length=neededChars.length;
		for(int i=0;i<neededChars.length;i++){
			double tmp=neededChars[i].length;
			avg+=tmp/length;
		}
		return avg;
	}
	
	public double getAvarageWordLength(){
		double avg=0;
		double length=wordlist.length;
		for(int i=0;i<wordlist.length;i++){
			double tmp=wordlist[i].length();
			avg+=tmp/length;
		}
		return avg;
	}
	
	public static void timingTest(String rack,String[] wordsOnRow, String[] wordlist,int filterRepeats){
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		Z_OLD_MartinsFilter ff=new Z_OLD_MartinsFilter(wordlist);
		t2=System.currentTimeMillis();
		ArrayList<String> st=null;
		for(int i=0;i<filterRepeats;i++){
			st=ff.filter(rack, wordsOnRow);
//			String s=st.get(0);
		}
		t3=System.currentTimeMillis();
		for(String s:st){
			System.out.println(s);
		}
		System.out.println(st.size());
		System.out.println(
				"rack: "+rack
				+"  wordsOnRow: "+Arrays.toString(wordsOnRow)
				+"  wordListLength: "+wordlist.length
				+"  constructionTime(ms): "+(t2-t1)
				+"  filterTime per word(ms): "+(((double)(t3-t2))/((double)filterRepeats))
				+"  filterRepeats: "+filterRepeats
				);
	}
	
	public void print(){
		for(int i=0;i<wordlist.length;i++){
			System.out.println(wordlist[i]+Arrays.toString(toBinaryStrings(neededChars[i])));
		}
	}
	
	private static String[] toBinaryStrings(int[] arr){
		String[] res=new String[arr.length];
		for(int i=0;i<arr.length;i++){
			res[i]=Integer.toBinaryString(arr[i]);
		}
		return res;
	}
	
	
	
	
	/**
	 * constructs a FastFilter.
	 * @param _wordlist
	 */
	public Z_OLD_MartinsFilter(String[] _wordlist){
		//initialize
		wordlist=_wordlist;
		neededChars=new int[wordlist.length][];
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			if(wordlist[i].matches("[a-z]+")){
				neededChars[i]=getHasChars(wordlist[i],0,0);
			} else {
				System.out.println("StrangeWord: "+wordlist[i]);
				neededChars[i]=null;
			}
		}
	}
	private static int[] getHasChars(final String s,final int wildCards,final int unknown){
		return getHasChars(createFreq(s), wildCards);
	}
	
	private static int[] getHasChars(final byte[] charFreq, final int wildCards){
		int max=0;
		for(int i=0;i<charFreq.length;i++){
			if(charFreq[i]>max){
				max=charFreq[i];
			}
		}
		int[] res=new int[max+wildCards];
		for(int j=0;j<wildCards;j++){
			res[j]=-1;
		}
		int tmp;
		for(int j=0;j<max;j++){
			tmp=0;
			for(int i=0;i<charFreq.length;i++){
				if(charFreq[i]>j){
					tmp=tmp | (1<<i);
				}
			}
			res[j+wildCards]=tmp;
		}
		return res;
	}
	
	/**
	 * Returns a filtered wordlist where some of the words that can't be solutions have been filtered out.
	 * @param rack
	 * @param wordsOnRow
	 * @return
	 */
	public ArrayList<String> filter(String rack, String[] wordsOnRow){
		//to lowe case
		rack=rack.toLowerCase();
		wordsOnRow=Arrays.copyOf(wordsOnRow, wordsOnRow.length);
		for(int i=0;i<wordsOnRow.length;i++){
			wordsOnRow[i]=wordsOnRow[i].toLowerCase();
		}
		//get and remove wildcards
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		//create source letters string
		String sourceLetters=rack+concatinate(wordsOnRow);
		//create freq array from the source
		final byte[] hasFreq=createFreq(sourceLetters);
		//create hasChars from freq and wildcards
		final int[] hasChars=getHasChars(hasFreq,wildcards);
		
		ArrayList<String> res=new ArrayList<String>();
//		res.add("empty");
		for(int i=0;i<wordlist.length;i++){
			if(hasNeededChars(neededChars[i],hasChars)){
				res.add(wordlist[i]);
			}
		}
		return res;
	}
	
	private static boolean hasNeededChars(final int neededChars[],final int hasChars[]){
		if(neededChars.length>hasChars.length){ return false; }
		for(int i=0;i<neededChars.length;i++){
			if((neededChars[i] & hasChars[i]) !=neededChars[i]){
				return false;
			}
		}
		return true;
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
	private static boolean hasCharFreq2(final byte[] checkList,final byte[] needFreq, final byte[] hasFreq,int wildCards){
		int i;
		for(int j=0;j<checkList.length;j++){
			i=checkList[j];
			if(needFreq[i]>hasFreq[i]){
				//if there are less letters available than needed...
//				System.out.println(needFreq[i]+" "+hasFreq[i]+" "+wildCards);
				wildCards=wildCards-(needFreq[i]-hasFreq[i]);
//				System.out.println(needFreq[i]+" gg "+hasFreq[i]+" "+wildCards);
				if(wildCards<0){
					return false;
				}
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
//		if(s==null){ s=""; }
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
	public static byte[] createFreq(String s){
//		System.out.println("freqOf: "+s);
//		if(s==null){ s=""; }
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
