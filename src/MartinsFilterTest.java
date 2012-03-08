import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
public class MartinsFilterTest {
	final String[] wordlist; //the wordlist
	final int[][] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	final byte[] lengths;
	final int limit;
	
	/**
	 * for testing performance
	 * or other stuff in this class.
	 * @param args
	 */
	public final  static void main(String[] args){
		String[] wordlist=new WordFinder().getWordlist();
		MartinsFilterTest ff3=new MartinsFilterTest(wordlist);
		
//		ff2.print();
		System.out.println(ff3.getAvarageLength());
		System.out.println(ff3.getAvarageWordLength());
		System.out.println(ff3.maxLength());
//		for(int i=2;i<=15;i++){
//			System.out.println(i+": "+Arrays.toString(FastFilter3.getCharCombinations("as dkfg gfw hrf", i)));
//		}
		String row="    hej d   low";
		boolean[] adjacent ={false,true,false,true,false,true,false,true, true,false,false,false,false,true,false};
//		row="               ";
		String[] wordsOnRow={"hej","d","low"};
		
		String rack="fi.hdf";
//		rack=".......";
//		for(int i=2;i<=15;i++){
//			ArrayList<String>[] tmp=ff3.filter(rack, "    hej d   low", i);
////		System.out.println(Arrays.toString(tmp));
//		}
		timingTest(rack,row, wordlist,10,adjacent);
//		timingTest(rack,wordsOnRow,wordlist,1000);
	}
	
	public static int maxDoubles(final String[] wordlist){
		int max=0;
		for(int i=0;i<wordlist.length;i++){
			int tmp=max(FastFilter.createFreq(wordlist[i]));
			if(tmp>max){
				max=tmp;
			}
		}
		return max;
	}
	
	private static int max(final byte[] arr){
		int max=0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]>max){
				max=arr[i];
			}
		}
		return max;
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
	
	public static void timingTest(String rack,String row, String[] wordlist,int filterRepeats,boolean[] adjacent){
		
		
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		MartinsFilterTest ff=new MartinsFilterTest(wordlist);
//		System.out.println(Arrays.toString(ff.filter(rack, row,2)));
		t2=System.currentTimeMillis();
		ArrayList<ArrayList<String>[]> list=new ArrayList<ArrayList<String>[]>();
		ArrayList<String>[] st=null;
		for(int i=0;i<filterRepeats;i++){
			list.clear();
			for(int j=2;j<=15;j++){
				st=ff.slowFilter(rack, row,j,adjacent);
				list.add(st);
			}
			
		}
		t3=System.currentTimeMillis();
		HashMap<String,String> map=new HashMap<String,String>();
		for(ArrayList<String>[] a:list){
			for(ArrayList<String> b:a){
				for(String s:b){
					map.put(s, s);
				}
			}
		}
		for(String s:map.values()){
			System.out.println(s);
		}
		for(int i=0;i<list.size();i++){
			System.out.println((i+2)+Arrays.toString(toLength(list.get(i))));
		}
		System.out.println(map.size());
		System.out.println(
				"rack: "+rack
				+"  wordsOnRow: "+row
				+"  wordListLength: "+wordlist.length
				+"  constructionTime(ms): "+(t2-t1)
				+"  filterTime per word(ms): "+(((double)(t3-t2))/((double)filterRepeats))
				+"  filterRepeats: "+filterRepeats
				);
	}
	
	private static int[] toLength(ArrayList<String>[] arr){
		int[] sizes=new int[arr.length];
		for(int i=0;i<arr.length;i++){
			sizes[i]=arr[i].size();
		}
		return sizes;
	}
	
	public static void timingTest(String rack,String[] wordsOnRow, String[] wordlist,int filterRepeats){
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		MartinsFilterTest ff=new MartinsFilterTest(wordlist);
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
		System.out.println("found: "+st.size());
		System.out.println(
				"rack: "+rack
				+"  wordsOnRow: "+Arrays.toString(wordsOnRow)
				+"  wordListLength: "+wordlist.length
				+"  constructionTime(ms): "+(t2-t1)
				+"  filterTime per row(ms): "+(((double)(t3-t2))/((double)filterRepeats))
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
	public MartinsFilterTest(String[] _wordlist){
		//initialize
		wordlist=_wordlist;
		limit=maxDoubles(wordlist);
		neededChars=new int[limit][wordlist.length];
		lengths=new byte[wordlist.length];
		System.out.println("limit: "+limit);
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			if(wordlist[i].matches("[a-z]+")){
				int[] tmp=getHasChars(wordlist[i],0,0);
				for(int j=0;j<tmp.length;j++){
					neededChars[j][i]=tmp[j];
				}
				lengths[i]=(byte) tmp.length;
			} else {
				System.out.println("StrangeWord: "+wordlist[i]);
			}
		}
	}
	private static int[] getHasChars(final String s,final int wildCards,final int unknown){
		return getHasChars(FastFilter.createFreq(s), wildCards);
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
		final byte[] hasFreq=FastFilter.createFreq(sourceLetters);
		//create hasChars from freq and wildcards
		final int[] hasChars=getHasChars(hasFreq,wildcards);
		
		ArrayList<String> res=new ArrayList<String>();
//		res.add("empty");
		for(int i=0;i<wordlist.length;i++){
			if(hasNeededChars(i,hasChars)){
				res.add(wordlist[i]);
			}
		}
		return res;
	}
	
	public ArrayList<String>[] slowFilter(String rack, String row,int length,boolean[] adjacent){
		int racklength=rack.length();
		
		rack=rack.toLowerCase();
		row=row.toLowerCase();
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		
		HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
		
		final String [] sarr=MartinsFilterTest.getCharCombinations3(row, length,adjacent);
//		System.out.println(length+Arrays.toString(sarr));
		@SuppressWarnings("unchecked")
		final ArrayList<String>[] res =new ArrayList[sarr.length];
		for(int j=0;j<res.length;j++){
			//initialize ArrayList
			res[j]=new ArrayList<String>();
			if(sarr[j]!=null){
				if(map.containsKey(sarr[j])){
					//reuse previous calculation
					res[j]=map.get(sarr[j]);
				} else {
					String boardLetters=sarr[j].replaceAll(".","");
					//create source letters string
					final String sourceLetters=rack+boardLetters;
					//check for matches if there enough sourceLetters
					if(boardLetters.length()+racklength>=length){
						//create freq array from the source
						final byte[] hasFreq=FastFilter.createFreq(sourceLetters);
						//create hasChars from freq and wildcards
						final int[] hasChars=getHasChars(hasFreq,wildcards);
//							res.add("empty");
						Pattern p=Pattern.compile(sarr[j]);

						for(int i=0;i<wordlist.length;i++){
							if(wordlist[i].length()==length && hasNeededChars(i,hasChars)
									&& p.matcher(wordlist[i]).matches()
									){
//									System.out.println(sarr[j]);
								res[j].add(wordlist[i]);
							}
						}
					}
					//save the calculation so that it can be reused if the same pattern is used
					map.put(sarr[j], res[j]);
				}
			}
		}
		
//		rack=rack.toLowerCase();
//		row=row.toLowerCase();
//		int wildcards=rack.length();
//		rack=rack.replaceAll("\\.", "");
//		wildcards=wildcards-rack.length();
//		
//		final String [] sarr=FastFilter3.getCharCombinations(row, length);
//		@SuppressWarnings("unchecked")
//		final ArrayList<String>[] res =new ArrayList[sarr.length];
//		for(int j=0;j<res.length;j++){
//			//initialize ArrayList
//			res[j]=new ArrayList<String>();
//			if(sarr[j]!=null){
//				//create source letters string
//				final String sourceLetters=rack+sarr[j];
//				//create freq array from the source
//				final byte[] hasFreq=FastFilter.createFreq(sourceLetters);
//				//create hasChars from freq and wildcards
//				final int[] hasChars=getHasChars(hasFreq,wildcards);
////				res.add("empty");
//				for(int i=0;i<wordlist.length;i++){
//					if(wordlist[i].length()==length && hasNeededChars(i,hasChars)){
//						res[j].add(wordlist[i]);
//					}
//				}
//			}
//		}
		return res;
	}
	
	public static String[] getCharCombinations(final String row,final int length){
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			String tmp=row.substring(i,i+length);
			String tmp2=tmp.replaceAll(" ", "");
			//must have room to place pieces
			if(tmp.length()!=tmp2.length()){
				//it must be empty before and after the word
				if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
					//must be letters above or below, if it's empty
					if(tmp2.length()==0){
						//TODO: insert check if there are letters above or below
						sarr[i]=tmp2;
					} else {
						sarr[i]=tmp2;
					}
					
				}
			}
		}
		return sarr;
	}
	
	public static String[] getCharCombinations2(final String row,final int length){
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			String tmp=row.substring(i,i+length);
			String tmp2=tmp.replaceAll(" ", "");
			//must have room to place pieces
			if(tmp.length()!=tmp2.length()){
				//it must be empty before and after the word
				if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
					//must be letters above or below, if it's empty
					if(tmp2.length()==0){
						//TODO: insert check if there are letters above or below
						sarr[i]=tmp.replaceAll(" ", ".");
					} else {
						sarr[i]=tmp.replaceAll(" ", ".");
					}
					
				}
			}
		}
		return sarr;
	}
	
	public static String[] getCharCombinations3(final String row,final int length,boolean[] haveAdjacent){
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			String tmp=row.substring(i,i+length);
			String tmp2=tmp.replaceAll(" ", "");
			//must have room to place pieces
			if(tmp.length()!=tmp2.length()){
				//it must be empty before and after the word
				if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
					//must be letters above or below, if it's empty
					if(tmp2.length()==0){
						if(length<=7){//max 7 letters in rack
							//check if there are letters above or below
							for(int j=i;j<i+length;j++){
								if(haveAdjacent[j]){
									sarr[i]=tmp.replaceAll(" ", ".");
									break;
								}
							}
						}
						
					} else {
						sarr[i]=tmp.replaceAll(" ", ".");
					}
					
				}
			}
		}
		return sarr;
	}
	
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
//		final byte[] hasFreq=FastFilter.createFreq(sourceLetters);
//		//create hasChars from freq and wildcards
//		final int[] hasChars=getHasChars(hasFreq,wildcards);
//		
//		ArrayList<String> res=new ArrayList<String>();
////		res.add("empty");
//		for(int i=0;i<wordlist.length;i++){
//			if(hasNeededChars(i,hasChars)){
//				res.add(wordlist[i]);
//			}
//		}
//		return res;
//	}
	
	private boolean hasNeededChars(final int i,final int hasChars[]){
		final int limit=lengths[i]; 
		if(limit>hasChars.length){ return false; }
//		for(int j=0;j<limit;j++){
////			if(((neededChars[j][i]&hasChars[j])!=neededChars[j][i])){
////				return false;
////			}
//			if(!hasNeededChars(neededChars[j][i],hasChars[j])){
//				return false;
//			}
//		}
		for(int j=limit-1;j>=0;j--){
//			if(((neededChars[j][i]&hasChars[j])!=neededChars[j][i])){
//				return false;
//			}
			if(!hasNeededChars(neededChars[j][i],hasChars[j])){
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
