import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;



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
	final int[] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	final byte[][] charFreq;//list of the frequency of each letter in each word
	final byte[][] checkList;//only need to check chars in the word
	
	public Z_OLD_MartinsFilter(String[] _wordlist){
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
	
	public ArrayList<ArrayList<String>[]> slowFilter3(String rack, String row,
			final String[][] fastCrossers,final boolean[] impossible,final WordFinder wf,final GameInfo gi){
		

		int racklength=rack.length();

		rack=rack.toLowerCase();
		row=row.toLowerCase();

		//count wildcards
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		//count unknowns
		int unknowns=rack.length();
		rack=rack.replaceAll(",", "");
		unknowns=unknowns-rack.length();
		//count unknown wildcards
		int unknownWildCards=gi.getUnknownWildCards();
		byte[] unknown=gi.getUnknownFreq();

		final boolean[][] possible=Help.possible(fastCrossers, wf);

		int size=15-1;
		
		
		//data about row and position
		final String[][] comb=new String[size][];
		final Pattern[][] patterns=new Pattern[size][];
		final byte[][][] hasBytess=new byte[size][][];

		//Initialize
		ArrayList<ArrayList<String>[]> res=new ArrayList<ArrayList<String>[]>();

		//
		for(int length=2,index=0;length<=15;length++,index++){
			//Real, get the combinations for a length
			comb[index]=MartinsFilterTest.getCharCombinations4(racklength,row,length,fastCrossers,impossible);
			//Initialize
			hasBytess[index]=new byte[comb[index].length][];
			patterns[index]=new Pattern[comb[index].length];
			//get properties for each comb
			for(int i=0;i<comb[index].length;i++){
				if(comb[index][i]!=null){
					//Initialize
					final String boardLetters=comb[index][i].replaceAll(".","");
					final String sourceLetters=rack+boardLetters;
					//final int[] hasChars=getHasChars(Help.createFreq(sourceLetters));
					final byte[] freq=Help.createFreq(sourceLetters);
					//Real
					//					skipPatterns[index][i]=boardLetters.length()==0;
					//Real
					//hasCharss[index][i]=hasChars;
					//Real
					hasBytess[index][i]=freq;
					//Real
					patterns[index][i]=boardLetters.length()==0?null:Pattern.compile(comb[index][i]);
				}
			}
			//Initialize
			@SuppressWarnings("unchecked")
			ArrayList<String>[] tmp =new ArrayList[comb[index].length];
			for(int i=0;i<tmp.length;i++){
				tmp[i]=new ArrayList<String>();
			}
			res.add(tmp);
		}
		

		//do filtering
		//for each word
		for(int i=0;i<wordlist.length;i++){
			final String word=wordlist[i];
			final int index=word.length()-2;
			//			final ArrayList<String>[] res1=res.get(index);
			final String sarr[]=comb[index];
			//for each starting position on the row for the word
			for(int j=0;j<sarr.length;j++){
				if(sarr[j]!=null){
					//retrieve data about position
					//final int[] hasChars=hasCharss[index][j];
					final byte[] hasFreq=hasBytess[index][j];
					final Pattern p=patterns[index][j];
					//check, ha enough chars in rack
					if(Help.hasCharFreq3(checkList[i], charFreq[i], hasFreq, wildcards, unknownWildCards, unknown, unknowns)
							//hasNeededChars(i,hasChars,wildcards) 
							//hasNeededChars(neededChars[i],hasChars,wildcards) 
							&& (p==null || p.matcher(word).matches())
							//(p.matcher(wordlist[i]).matches())
							//&& Help.correctCrossing(wordlist[i],j,fastCrossers,wf)
							&& Help.correctCrossing(word, j, possible)
					){
						//						res1[j].add(word);
						res.get(index)[j].add(word);
					}

				}
			}
		}
		return res;
	}
	
	/**
	 * Made for the advanced bot.
	 * row is the whole row.
	 * length is the length of the words tested for.
	 * crossing is a string array with all words that cross the row with a space for
	 * the crossing point.
	 * impossible is points that it's impossible to create a word by inserting one letter.
	 * from and to is used in advanced bot to only allow new words that intersect the
	 * 
	 * null means impossible to create word there.
	 * empty string means it dosent intersect the new word
	 * @param row
	 * @param length
	 * @param haveAdjacent
	 * @param fastCrossers
	 * @return
	 */
	public static String[] getCharCombinations5(final int rackLength, final String row, final String newRow,final int length,
			final String[][] fastCrossers, final boolean[] impossible, final int from, final int to, final boolean touching){
		
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			//from and to must be inside the word
			//ERROR: don't have to be in word if just touching
			if(touching || from>=i && to<=i+length-1){
				//it must not be impossible
				if(!Help.impossibleInRange(i,length,impossible)){
					String tmp=row.substring(i,i+length);
					String tmp2=tmp.replaceAll(" ", "");
					//must have room to place pieces
					if(tmp.length()!=tmp2.length()){
						//must have enough letters
						if(length-tmp2.length()<=rackLength){
							//it must be empty before and after the word, or else it's not the given length
							if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
								if(tmp2.length()==0){
									//maybe should check if it's possible to complete each crossing word, and if not call the place forbidden
									//check if there are letters above or below
									for(int j=i;j<i+length;j++){
										//must be letters above or below, if it's empty
										if(fastCrossers[j]!=null){
											sarr[i]=tmp.replaceAll(" ", ".");
											break;
										}
									}
								} else {
									sarr[i]=tmp.replaceAll(" ", ".");
								}

							}
						}

					}
				}
			} else {
				sarr[i]="";
			}
		}
		return sarr;
	}
	
//	/**
//	 * for testing performance
//	 * or other stuff in this class.
//	 * @param args
//	 */
//	public final  static void main(String[] args){
//		String[] wordlist=new WordFinder().getWordlist();
//		Z_OLD_MartinsFilter ff2=new Z_OLD_MartinsFilter(wordlist);
//		
////		ff2.print();
//		System.out.println(ff2.getAvarageLength());
//		System.out.println(ff2.getAvarageWordLength());
//		System.out.println(ff2.maxLength());
//		
//		String[] wordsOnRow={"hej","d","low"};
//		String rack="fi.hdf";
//		timingTest(rack,wordsOnRow,wordlist,1000);
//	}
//	
//	public int maxLength(){
//		int max=0;
//		for(int i=0;i<neededChars.length;i++){
//			if(neededChars[i].length>max){
//				max=neededChars[i].length;
//			}
//		}
//		return max;
//	}
//	
//	public double getAvarageLength(){
//		double avg=0;
//		double length=neededChars.length;
//		for(int i=0;i<neededChars.length;i++){
//			double tmp=neededChars[i].length;
//			avg+=tmp/length;
//		}
//		return avg;
//	}
//	
//	public double getAvarageWordLength(){
//		double avg=0;
//		double length=wordlist.length;
//		for(int i=0;i<wordlist.length;i++){
//			double tmp=wordlist[i].length();
//			avg+=tmp/length;
//		}
//		return avg;
//	}
//	
//	public static void timingTest(String rack,String[] wordsOnRow, String[] wordlist,int filterRepeats){
//		long t1,t2,t3;
//		t1=System.currentTimeMillis();
//		Z_OLD_MartinsFilter ff=new Z_OLD_MartinsFilter(wordlist);
//		t2=System.currentTimeMillis();
//		ArrayList<String> st=null;
//		for(int i=0;i<filterRepeats;i++){
//			st=ff.filter(rack, wordsOnRow);
////			String s=st.get(0);
//		}
//		t3=System.currentTimeMillis();
//		for(String s:st){
//			System.out.println(s);
//		}
//		System.out.println(st.size());
//		System.out.println(
//				"rack: "+rack
//				+"  wordsOnRow: "+Arrays.toString(wordsOnRow)
//				+"  wordListLength: "+wordlist.length
//				+"  constructionTime(ms): "+(t2-t1)
//				+"  filterTime per word(ms): "+(((double)(t3-t2))/((double)filterRepeats))
//				+"  filterRepeats: "+filterRepeats
//				);
//	}
//	
//	public void print(){
//		for(int i=0;i<wordlist.length;i++){
//			System.out.println(wordlist[i]+Arrays.toString(toBinaryStrings(neededChars[i])));
//		}
//	}
//	
//	private static String[] toBinaryStrings(int[] arr){
//		String[] res=new String[arr.length];
//		for(int i=0;i<arr.length;i++){
//			res[i]=Integer.toBinaryString(arr[i]);
//		}
//		return res;
//	}
//	
//	
//	
//	
//	/**
//	 * constructs a FastFilter.
//	 * @param _wordlist
//	 */
//	public Z_OLD_MartinsFilter(String[] _wordlist){
//		//initialize
//		wordlist=_wordlist;
//		neededChars=new int[wordlist.length][];
//		
//		//calculate properties for each word
//		for(int i=0;i<wordlist.length;i++){
//			if(wordlist[i].matches("[a-z]+")){
//				neededChars[i]=getHasChars(wordlist[i],0,0);
//			} else {
//				System.out.println("StrangeWord: "+wordlist[i]);
//				neededChars[i]=null;
//			}
//		}
//	}
//	private static int[] getHasChars(final String s,final int wildCards,final int unknown){
//		return getHasChars(createFreq(s), wildCards);
//	}
//	
//	private static int[] getHasChars(final byte[] charFreq, final int wildCards){
//		int max=0;
//		for(int i=0;i<charFreq.length;i++){
//			if(charFreq[i]>max){
//				max=charFreq[i];
//			}
//		}
//		int[] res=new int[max+wildCards];
//		for(int j=0;j<wildCards;j++){
//			res[j]=-1;
//		}
//		int tmp;
//		for(int j=0;j<max;j++){
//			tmp=0;
//			for(int i=0;i<charFreq.length;i++){
//				if(charFreq[i]>j){
//					tmp=tmp | (1<<i);
//				}
//			}
//			res[j+wildCards]=tmp;
//		}
//		return res;
//	}
//	
//	/**
//	 * Returns a filtered wordlist where some of the words that can't be solutions have been filtered out.
//	 * @param rack
//	 * @param wordsOnRow
//	 * @return
//	 */
//	public ArrayList<String> filter(String rack, String[] wordsOnRow){
//		//to lowe case
//		rack=rack.toLowerCase();
//		wordsOnRow=Arrays.copyOf(wordsOnRow, wordsOnRow.length);
//		for(int i=0;i<wordsOnRow.length;i++){
//			wordsOnRow[i]=wordsOnRow[i].toLowerCase();
//		}
//		//get and remove wildcards
//		int wildcards=rack.length();
//		rack=rack.replaceAll("\\.", "");
//		wildcards=wildcards-rack.length();
//		//create source letters string
//		String sourceLetters=rack+concatinate(wordsOnRow);
//		//create freq array from the source
//		final byte[] hasFreq=createFreq(sourceLetters);
//		//create hasChars from freq and wildcards
//		final int[] hasChars=getHasChars(hasFreq,wildcards);
//		
//		ArrayList<String> res=new ArrayList<String>();
////		res.add("empty");
//		for(int i=0;i<wordlist.length;i++){
//			if(hasNeededChars(neededChars[i],hasChars)){
//				res.add(wordlist[i]);
//			}
//		}
//		return res;
//	}
//	
//	private static boolean hasNeededChars(final int neededChars[],final int hasChars[]){
//		if(neededChars.length>hasChars.length){ return false; }
//		for(int i=0;i<neededChars.length;i++){
//			if((neededChars[i] & hasChars[i]) !=neededChars[i]){
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * Checks that all the "neededChars" is in "hasChars". 
//	 * @param neededChars
//	 * @param hasChars
//	 * @return
//	 */
//	private static boolean hasNeededChars(final int neededChars,final int hasChars){
//		return (neededChars & hasChars) == neededChars;
//	}
//	
////	/**
////	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
////	 * @param needFreq
////	 * @param hasFreq
////	 * @return
////	 */
////	private static boolean hasCharFreq(final byte[] needFreq, final byte[] hasFreq){
////		for(int i=0;i<needFreq.length;i++){
////			if(needFreq[i]>hasFreq[i]){
////				//if there are less letters available than needed...
////				return false;
////			}
////		}
////		return true;
////	}
//	
//	/**
//	 * Returns a list of the indexes that contain a number greater than zero.
//	 * @param charFreq
//	 * @return
//	 */
//	private static byte[] getCheckList(final byte[] charFreq){
//		byte size=0;
//		for(byte i=0;i<charFreq.length;i++){
//			if(charFreq[i]>0){
//				size++;
//			}
//		}
//		final byte[] res=new byte[size];
//		byte index=0;
//		for(byte i=0;i<charFreq.length;i++){
//			if(charFreq[i]>0){
//				res[index]=i;
//				index++;
//			}
//		}
//		return res;
//	}
//	
//	/**
//	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
//	 * Only checks the letters that is in the word.
//	 * @param checkList
//	 * @param needFreq
//	 * @param hasFreq
//	 * @return
//	 */
//	private static boolean hasCharFreq2(final byte[] checkList,final byte[] needFreq, final byte[] hasFreq,int wildCards){
//		int i;
//		for(int j=0;j<checkList.length;j++){
//			i=checkList[j];
//			if(needFreq[i]>hasFreq[i]){
//				//if there are less letters available than needed...
////				System.out.println(needFreq[i]+" "+hasFreq[i]+" "+wildCards);
//				wildCards=wildCards-(needFreq[i]-hasFreq[i]);
////				System.out.println(needFreq[i]+" gg "+hasFreq[i]+" "+wildCards);
//				if(wildCards<0){
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//	
//	
//	/**
//	 * Uses an int as a bit array to store which chars are in the word.
//	 * It's then very fast to check if one string contains all the letter types by
//	 * using: (a&b)==a.
//	 * If that returns true then b contains all letter types in a.
//	 * @param s
//	 * @return
//	 */
//	private static int getHasChars(String s){
////		if(s==null){ s=""; }
//		int res=0;
//		for(int i=0;i<s.length();i++){
//			res= res | (1<<(s.charAt(i)-'a'));
//		}
//		return res;
//	}
//	
//	/**
//	 * Counts the frequency of each letter and stores the count for each letter in a byte array, where 
//	 * index 0 contains the count for 'a'.
//	 * @param s
//	 * @return
//	 */
//	public static byte[] createFreq(String s){
////		System.out.println("freqOf: "+s);
////		if(s==null){ s=""; }
//		byte[] freq=new byte['z'-'a'+1];//size of alphabet
//		for(int i=0;i<s.length();i++){
//			freq[s.charAt(i)-'a']++;
//		}
//		return freq;
//	}
//	
//	/**
//	 * help method for concatinating all strings in a string array.
//	 * Should be moved to a help methods class.
//	 * @param sarr
//	 * @return
//	 */
//	private String concatinate(final String[] sarr){
//		final StringBuilder sb=new StringBuilder();
//		for(int i=0;i<sarr.length;i++){
//			sb.append(sarr[i]);
//		}
//		return sb.toString();
//	}
	
}
