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
	
	public ArrayList<ArrayList<String>[]> testSlowFilter2(GameInfo gi,WordFinder find, final int rowIndex,boolean vertical){
		ArrayList<ArrayList<String>[]> list=new ArrayList<ArrayList<String>[]>();
		//get variables, first three, 3 ms
		final String rack=gi.getRack();
		final String row=gi.getRow(rowIndex, false);
		final String[][] fastCrossers=Help.fastCrossers(gi.getRowCrossers(rowIndex, vertical));
		final boolean[] impossible =Help.impossible(fastCrossers, find); //7 ms
		//get for all lengths
//		for(int length=2;length<=15;length++){
//			list.add(this.slowFilter2(rack, row, length, fastCrossers, impossible, find));
//		}
		list=slowFilter3(rack, row,fastCrossers, impossible, find);
		return list;
	}
	
	public static int maxDoubles(final String[] wordlist){
		int max=0;
		for(int i=0;i<wordlist.length;i++){
			int tmp=max(Help.createFreq(wordlist[i]));
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
		return getHasChars(Help.createFreq(s), wildCards);
	}
	
	
	private static int[] getHasChars(final byte[] charFreq){
		int max=0;
		for(int i=0;i<charFreq.length;i++){
			if(charFreq[i]>max){
				max=charFreq[i];
			}
		}
		int[] res=new int[max];
		int tmp;
		for(int j=0;j<max;j++){
			tmp=0;
			for(int i=0;i<charFreq.length;i++){
				if(charFreq[i]>j){
					tmp=tmp | (1<<i);
				}
			}
			res[j]=tmp;
		}
		return res;
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
		String sourceLetters=rack+Help.concatinate(wordsOnRow);
		//create freq array from the source
		final byte[] hasFreq=Help.createFreq(sourceLetters);
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
						final byte[] hasFreq=Help.createFreq(sourceLetters);
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
	
	/**
	 * all input must be lowercase.
	 * @param rack
	 * @param row
	 * @param length
	 * @param fastCrossers
	 * @param impossible
	 * @param wf
	 * @return
	 */
	public ArrayList<String>[] slowFilter2(String rack, String row,final int length,
			final String[][] fastCrossers,final boolean[] impossible,final WordFinder wf){
		int racklength=rack.length();
		rack=rack.toLowerCase();
		row=row.toLowerCase();
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		
		boolean[][] possible=Help.possible(fastCrossers, wf);
		
		HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
		
		final String [] sarr=MartinsFilterTest.getCharCombinations4(racklength,row,length,fastCrossers,impossible);
//		System.out.println(Arrays.toString(sarr));
//		System.out.println(length+Arrays.toString(sarr));
		@SuppressWarnings("unchecked")
		final ArrayList<String>[] res =new ArrayList[sarr.length];
		for(int j=0;j<res.length;j++){
			//initialize ArrayList
			res[j]=new ArrayList<String>();
			if(sarr[j]!=null){
//				if(map.containsKey(sarr[j])){
//					//reuse previous calculation
//					res[j]=map.get(sarr[j]);
//				} else {
					String boardLetters=sarr[j].replaceAll(".","");
					//create source letters string
					final String sourceLetters=rack+boardLetters;
					//check for matches if there enough sourceLetters
					if(boardLetters.length()+racklength>=length){
						//create freq array from the source
						final byte[] hasFreq=Help.createFreq(sourceLetters);
						//create hasChars from freq and wildcards
						final int[] hasChars=getHasChars(hasFreq);
//							res.add("empty");
						//if there is no letters on board there is no pattern
						boolean skipPattern=boardLetters.length()==0;
						Pattern p=Pattern.compile(sarr[j]);
						
						//do heavy filtering, everything else 60 ms, everything else in this method 50 ms
						for(int i=0;i<wordlist.length;i++){
							if(wordlist[i].length()==length 
									&& hasNeededChars(i,hasChars,wildcards) && (skipPattern || p.matcher(wordlist[i]).matches())
									//(p.matcher(wordlist[i]).matches())
									//&& Help.correctCrossing(wordlist[i],j,fastCrossers,wf)
									&& Help.correctCrossing(wordlist[i], j, possible)
									){
								res[j].add(wordlist[i]);
							}
						}
					}
					//save the calculation so that it can be reused if the same pattern is used
//					map.put(sarr[j], res[j]);
//				}
			}
		}
		return res;
	} 
	
	/**
	 * twice as fast as slowfilter2, and should not return words
	 * that can't fit to a position.
	 * The returned list is accessed in the following way:
	 * list.get(wordLength)[startPos].get(i);
	 * where wordLength is the length of the words and startPor is the
	 * position the word is on the board.
	 * iterate through i in the following way:
	 * for(int i=0;i<list.get(wordLength)[startPos].length;i++){
	 * 		String word=list.get(wordLength)[startPos].get(i);
	 * }
	 * @param rack
	 * @param row
	 * @param length
	 * @param fastCrossers
	 * @param impossible
	 * @param wf
	 * @return
	 */
	public ArrayList<ArrayList<String>[]> slowFilter3(String rack, String row,
			final String[][] fastCrossers,final boolean[] impossible,final WordFinder wf){
		
		
		int racklength=rack.length();
		rack=rack.toLowerCase();
		row=row.toLowerCase();
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		
		final boolean[][] possible=Help.possible(fastCrossers, wf);
		
		int size=15-1;
		//data about row and position
		final String[][] comb=new String[size][];
		final int[][][] hasCharss=new int[size][][];
//		final boolean[][] skipPatterns=new boolean[size][];
		final Pattern[][] patterns=new Pattern[size][];
		
		//Initialize
		HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
		ArrayList<ArrayList<String>[]> res=new ArrayList<ArrayList<String>[]>();
		
		//
		for(int length=2,index=0;length<=15;length++,index++){
			////Real, get the combinations for a length
			comb[index]=MartinsFilterTest.getCharCombinations4(racklength,row,length,fastCrossers,impossible);
			//Initialize
//			skipPatterns[index]=new boolean[comb[index].length];
			hasCharss[index]=new int[comb[index].length][];
			patterns[index]=new Pattern[comb[index].length];
			//get properties for each comb
			for(int i=0;i<comb[index].length;i++){
				if(comb[index][i]!=null){
					//Initialize
					final String boardLetters=comb[index][i].replaceAll(".","");
					final String sourceLetters=rack+boardLetters;
					final int[] hasChars=getHasChars(Help.createFreq(sourceLetters));
					//Real
//					skipPatterns[index][i]=boardLetters.length()==0;
					//Real
					hasCharss[index][i]=hasChars;
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
					final int[] hasChars=hasCharss[index][j];
					final Pattern p=patterns[index][j];
					//check, ha enough chars in rack
					if(hasNeededChars(i,hasChars,wildcards) && (p==null || p.matcher(word).matches())
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
		
		
		
		
		
		
		
		
		
////		System.out.println(Arrays.toString(sarr));
////		System.out.println(length+Arrays.toString(sarr));
//		@SuppressWarnings("unchecked")
//		final ArrayList<String>[] res =new ArrayList[sarr.length];
//		
//		for(int j=0;j<res.length;j++){
//			//initialize ArrayList
//			res[j]=new ArrayList<String>();
//			if(sarr[j]!=null){
////				if(map.containsKey(sarr[j])){
////					//reuse previous calculation
////					res[j]=map.get(sarr[j]);
////				} else {
//					String boardLetters=sarr[j].replaceAll(".","");
//					//create source letters string
//					final String sourceLetters=rack+boardLetters;
//					//check for matches if there enough sourceLetters
//					if(boardLetters.length()+racklength>=length){
//						//create freq array from the source
//						final byte[] hasFreq=Help.createFreq(sourceLetters);
//						//create hasChars from freq and wildcards
//						final int[] hasChars=getHasChars(hasFreq);
////							res.add("empty");
//						//if there is no letters on board there is no pattern
//						boolean skipPattern=boardLetters.length()==0;
//						Pattern p=Pattern.compile(sarr[j]);
//						
//						//do heavy filtering, everything else 60 ms, everything else in this method 50 ms
//						for(int i=0;i<wordlist.length;i++){
//							if(wordlist[i].length()==length 
//									&& hasNeededChars(i,hasChars,wildcards) && (skipPattern || p.matcher(wordlist[i]).matches())
//									//(p.matcher(wordlist[i]).matches())
//									//&& Help.correctCrossing(wordlist[i],j,fastCrossers,wf)
//									&& Help.correctCrossing(wordlist[i], j, possible)
//									){
//								res[j].add(wordlist[i]);
//							}
//						}
//					}
//					//save the calculation so that it can be reused if the same pattern is used
////					map.put(sarr[j], res[j]);
////				}
//			}
//		}
		return res;
	} 
	
	public boolean matches(final String word, final String row, int index){
		for(int i=0;i<word.length();i++,index++){
			if(word.charAt(i)!='.'){
				if(word.charAt(i)!=row.charAt(index)){
					return false;
				}
			}
		}
		return true;
	}
	
//	public ArrayList<String>[] slowFilter3(String rack, String row,final int length,
//			final String[][] fastCrossers,final boolean[] impossible,final WordFinder wf){
//		int racklength=rack.length();
//		rack=rack.toLowerCase();
//		row=row.toLowerCase();
//		int wildcards=rack.length();
//		rack=rack.replaceAll("\\.", "");
//		wildcards=wildcards-rack.length();
//		
//		HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
//		
//		final String [] sarr=MartinsFilterTest.getCharCombinations4(racklength,row,length,fastCrossers,impossible);
////		System.out.println(Arrays.toString(sarr));
////		System.out.println(length+Arrays.toString(sarr));
//		@SuppressWarnings("unchecked")
//		final ArrayList<String>[] res =new ArrayList[sarr.length];
//		for(int j=0;j<res.length;j++){
//			//initialize ArrayList
//			res[j]=new ArrayList<String>();
//			if(sarr[j]!=null){
//				if(map.containsKey(sarr[j])){
//					//reuse previous calculation
//					res[j]=map.get(sarr[j]);
//				} else {
//					String boardLetters=sarr[j].replaceAll(".","");
//					if(boardLetters.length()+racklength>=length){
//						Pattern p=Pattern.compile(sarr[j]);
//
//						for(int i=0;i<wordlist.length;i++){
//							if(wordlist[i].length()==length 
//									&& p.matcher(wordlist[i]).matches()
//									){
//								res[j].add(wordlist[i]);
//							}
//						}
//					}
//					//save the calculation so that it can be reused if the same pattern is used
//					map.put(sarr[j], res[j]);
//				}
//			}
//		}
//		return res;
//	} 
	

	
	
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	
	/**
	 * row is the whole row.
	 * length is the length of the words tested for.
	 * crossing is a string array with all words that cross the row with a space for
	 * the crossing point.
	 * impossible is points that it's impossible to create a word by inserting one letter.
	 * @param row
	 * @param length
	 * @param haveAdjacent
	 * @param fastCrossers
	 * @return
	 */
	public static String[] getCharCombinations4(final int rackLength, final String row,final int length,String[][] fastCrossers,boolean[] impossible){
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			//it must not be impossible
			if(!impossibleInRange(i,length,impossible)){
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
		}
		return sarr;
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
	public static String[] getCharCombinations5(final int rackLength, final String row,final int length,
			String[][] fastCrossers, boolean[] impossible, int from, int to){
		
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i+length-1<row.length();i++){
			//from and to must be inside the word
			if(from>=i && to<=i+length-1){
				//it must not be impossible
				if(!impossibleInRange(i,length,impossible)){
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
	
	private static boolean impossibleInRange(final int start,final int length,final boolean[] impossible){
		final int limit=start+length;
		for(int i=start;i<limit;i++){
			if(impossible[i]){
				return true;
			}
		}
		return false;
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
			if(!Help.hasNeededChars(neededChars[j][i],hasChars[j])){
				return false;
			}
		}
		return true;
	}
	
	private boolean hasNeededChars(final int i,final int hasChars[],final int wildcards){
		final int limit=lengths[i]; 
		if(limit>hasChars.length+wildcards){ return false; }
		for(int j=0,j2=j+wildcards;j2<limit;j++,j2++){
			if(!Help.hasNeededChars(neededChars[j2][i],hasChars[j])){
				return false;
			}
		}
		return true;
//		final int limit=lengths[i]; 
//		if(limit>hasChars.length+wildcards){ return false; }
//		for(int j=0,j2=j+wildcards;j2<limit;j++,j2++){
//			if(!Help.hasNeededChars(neededChars[j2][i],hasChars[j])){
//				return false;
//			}
//		}
//		return true;
	}

	
}
