import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;



/**
 * experimental
 * 
 * used for testing faster methods.
 * 
 * 
 * @author mbernt.mbernt
 *
 */
public class SlowFilter2 {
	final String[] wordlist; //the wordlist
	final int[] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	final byte[][] charFreq;//list of the frequency of each letter in each word
	final byte[][] checkList;//only need to check chars in the word
	final int[] wordLengths;
	
	final byte[][] charFreqDual;//TEST
	
	//these are set when called reset
	public int p0;
	public int p1;
	public int p2;
	public int p3;
	public int p4;
	ArrayList<Move> moves;
	
	public SlowFilter2(String[] _wordlist){
		//initialize
		wordlist=_wordlist;
		neededChars=new int[wordlist.length];
		charFreq=new byte[wordlist.length][];
		checkList=new byte[wordlist.length][];
		wordLengths=new int[wordlist.length];
		
		charFreqDual=new byte[wordlist.length][];//TEST
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			if(wordlist[i].matches("[a-z]+")){
				neededChars[i]=Help.getHasChars(wordlist[i]);
				charFreq[i]=Help.createFreq(wordlist[i]);
				checkList[i]=Help.getCheckList(charFreq[i]);
				wordLengths[i]=wordlist[i].length();
				
				charFreqDual[i]=Help.createFreqDual(wordlist[i]);//TEST
			} else {
				System.out.println("StrangeWord: "+wordlist[i]);
				neededChars[i]=0;
				charFreq[i]=null;
				checkList[i]=null;
				wordLengths[i]=0;
				charFreqDual[i]=null;//TEST
				System.out.println(neededChars[i]);
			}
				
		}
		reset();
	}
	
	public void reset(){
		p0=0;
		p1=0;
		p2=0;
		p3=0;
		p4=0;
		moves=new ArrayList<Move>();
	}
	
	
	/**
	 * if using Mionte Carlo then it's unimportant what the old rack was. 
	 * The only thing that is important is to make a fast greedy algorithm
	 * 
	 * Adds the resulting moves to res2
	 * @param rowIndex
	 * @param vertical
	 * @param wf
	 * @param gi
	 * @return
	 */
	public void slowFilterOptimized(final int rowIndex, final WordFinder wf,final GameInfo2 gi,
			ArrayList<Move> res2){
		//pre-calculate stuff...
		final String[][] fastCrossers=gi.getFastCrossers(rowIndex);
		final boolean[][] possible=Help.possibleLetters(fastCrossers, wf);
		final boolean[] impossible=Help.impossible(possible);
		
		final String row=String.valueOf(gi.getRow(rowIndex)).toLowerCase();
		
		//get rack
		String rack=gi.getRack().toLowerCase();
		final int racklength=rack.length();
		
		//count wildcards
		int wildcards=rack.length();
		rack=rack.replaceAll("\\.", "");
		wildcards=wildcards-rack.length();
		
		//
		final int size=15-1;
		
		//data about row and position
		final String[][] combinations=new String[size][];
		final int[][] checkList2=new int[size][];

		//calculate values for combinations and checklist2
		for(int length=2,index=0;length<=15;length++,index++){
			//TODO: use getCharCombinationsFaster
			combinations[index]=getCharCombinationsUpdate(racklength,row,length,fastCrossers,impossible,null);
//			getCharCombinationsFaster(rackLength, row, length,fastCrossers, impossible);
			int checkSize=0;
			for(int position=0;position<combinations[index].length;position++){
				if(combinations[index][position]!=null){
					checkSize++;
				}
			}
			checkList2[index]=new int[checkSize];
			int cindex=0;
			for(int position=0;position<combinations[index].length;position++){
				if(combinations[index][position]!=null){
					checkList2[index][cindex]=position;
					cindex++;
				}
			}
		}

		//check if there are no possible words on row, and in that case return
		boolean finished=true;
		for(int i=0;i<checkList2.length;i++){
			if(checkList2[i].length>0){
				finished=false;
				break;
			}
		}
		if(finished){
			return;
		}
		
		//data about what letters and letter types are available for a position and length
		final byte[][][] hasBytess=new byte[size][][];
		int[][] letterTypess=new int[size][];
		

		//Initialize hasBytess and letterTypess
		for(int length=2,lengthIndex=0;length<=15;length++,lengthIndex++){
			//Initialize hasBytess and patterns
			int combLen=combinations[lengthIndex].length;
			hasBytess[lengthIndex]=new byte[combLen][];
			letterTypess[lengthIndex]=new int[combLen];
			
			for(int j=0;j<checkList2[lengthIndex].length;j++){
				int position=checkList[lengthIndex][j];
				final String boardLetters=combinations[lengthIndex][position].replaceAll(" ","");
				final String sourceLetters=rack+boardLetters;
				
				hasBytess[lengthIndex][position]=Help.createFreq(sourceLetters);
				letterTypess[lengthIndex][position]=Help.getHasChars(sourceLetters);
			}
		}
		
		//initialize the res list 0 ms
		ArrayList<ArrayList<String>[]> res=new ArrayList<ArrayList<String>[]>();
		for(int length=2,index=0;length<=15;length++,index++){
			@SuppressWarnings("unchecked")
			ArrayList<String>[] tmp =new ArrayList[combinations[index].length];
			for(int position=0;position<tmp.length;position++){
				tmp[position]=new ArrayList<String>();
			}
			res.add(tmp);
		}
		
		
		
		//overhead when doing nothing but looping through word ? ms
		//do filtering
		//for each word
		for(int i=0;i<wordlist.length;i++){
			//get the index for finding information about words with the length
			final int lengthIndex=wordLengths[i]-2;
			
			//an int array is more cache friendly in java
			//this way you don't have to follow a reference to each word and then get the length
			
			//checkList2 must have a length greater than 0 or there will be no valid positions
			if(checkList2[lengthIndex].length>0){
				//TODO: maybe add row based check if checklist.length>1
				p0++;//Count the number of times this point is reached
				//for each starting position on the row for the word
				for(int j=0;j<checkList2[lengthIndex].length;j++){
					p1++;//Count the number of times this point is reached, from 100 to 125 ms with these
					//get position
					final int position=checkList2[lengthIndex][j];
					//the filter
					if(wildcards>0 || Help.hasNeededChars(neededChars[i], letterTypess[lengthIndex][position])){
						p2++;//Count the number of times this point is reached
						if(Help.hasCharFreq2(checkList[i], charFreq[i], hasBytess[lengthIndex][position], wildcards)){
							if(Help.correctCrossing(wordlist[i], position, possible)){
								p3++;//Count the number of times this point is reached
								if(Help.isFitting(combinations[lengthIndex][position],wordlist[i],' ')){
									p4++;//Count the number of times this point is reached
									//add if passed all filters
									res.get(lengthIndex)[position].add(wordlist[i]);
								}
							}
						}
					}
				}
			}

		}
//		if(res!=null){
			addResToMovesList(rowIndex,gi.isTransposed,res,gi);
//		}
	}
	
	/**
	 * if using Mionte Carlo then it's unimportant what the old rack was. 
	 * The only thing that is important is to make a fast greedy algorithm
	 * 
	 * Adds the resulting moves to res2
	 * @param rowIndex
	 * @param vertical
	 * @param wf
	 * @param gi
	 * @return
	 */
	public void slowFilterOptimized2(final int rowIndex, final WordFinder wf,final GameInfo2 gi, ArrayList<Move> res){
		//pre-calculate stuff...
		final String[][] fastCrossers=gi.getFastCrossers(rowIndex);
		final boolean[][] possible=Help.possibleLetters(fastCrossers, wf);
		final boolean[] impossible=Help.impossible(possible);
		
//		final String row=String.valueOf(gi.getRow(rowIndex)).toLowerCase();
//		final char[] row2=row.toCharArray();
		
		final char[] row2=String.valueOf(gi.getRow(rowIndex)).toLowerCase().toCharArray();
		
		//get rack
		String rack=gi.getRack().toLowerCase();
		final int racklength=rack.length();
		
		//count wildcards
		int blanks=rack.length();
		rack=rack.replaceAll("\\.", "");
		blanks=blanks-rack.length();
		
		//
		final int size=15-1;
		
		//data about row and position
//		final String[][] combinations=new String[size][];
		final int[][] combinations=new int[size][];
//		final char[][][] combinations=new char[size][][];
		final int[][] checkList2=new int[size][];

		//calculate values for combinations and checklist2
		for(int length=2,index=0;length<=15;length++,index++){
			//TODO: use getCharCombinationsFaster2
//			combinations[index]=getCharCombinationsUpdate(racklength,row,length,fastCrossers,impossible,null);
//			combinations[index]=getCharCombinationsFaster(racklength, row2, length,fastCrossers, impossible);
			combinations[index]=getCharCombinationsFaster2(racklength, row2, length,fastCrossers, impossible);
			int checkSize=0;
			for(int position=0;position<combinations[index].length;position++){
//				if(combinations[index][position]!=null){
				if(combinations[index][position]>0){
					checkSize++;
				}
			}
			checkList2[index]=new int[checkSize];
			int cindex=0;
			for(int position=0;position<combinations[index].length;position++){
//				if(combinations[index][position]!=null){
				if(combinations[index][position]>0){
					checkList2[index][cindex]=position;
					cindex++;
				}
			}
		}

		//check if there are no possible words on row, and in that case return
		boolean finished=true;
		for(int i=0;i<checkList2.length;i++){
			if(checkList2[i].length>0){
				finished=false;
				break;
			}
		}
		if(finished){
			return;
		}
		
		//data about what letters and letter types are available for a position and length
		final byte[][][] hasBytess=new byte[size][][];
		int[][] letterTypess=new int[size][];
		

		//Initialize hasBytess and letterTypess
		for(int length=2,lengthIndex=0;length<=15;length++,lengthIndex++){
			//Initialize hasBytess and patterns
			int combLen=combinations[lengthIndex].length;
			hasBytess[lengthIndex]=new byte[combLen][];
			letterTypess[lengthIndex]=new int[combLen];
			
			for(int j=0;j<checkList2[lengthIndex].length;j++){
				int position=checkList[lengthIndex][j];
//				final String boardLetters=combinations[lengthIndex][position].replaceAll(" ","");
				final String boardLetters=Help.getLetters(row2,position,length);
				final String sourceLetters=rack+boardLetters;
				
				hasBytess[lengthIndex][position]=Help.createFreq(sourceLetters);
				letterTypess[lengthIndex][position]=Help.getHasChars(sourceLetters);
			}
		}
		
		//initialize the res list 0 ms
		ArrayList<ArrayList<String>[]> res1=new ArrayList<ArrayList<String>[]>();
		for(int length=2,index=0;length<=15;length++,index++){
			@SuppressWarnings("unchecked")
			ArrayList<String>[] tmp =new ArrayList[combinations[index].length];
			for(int position=0;position<tmp.length;position++){
				tmp[position]=new ArrayList<String>();
			}
			res1.add(tmp);
		}
		
		
		
		//overhead when doing nothing but looping through word ? ms
		//do filtering
		//for each word
		for(int i=0;i<wordlist.length;i++){
			//get the index for finding information about words with the length
			final int lengthIndex=wordLengths[i]-2;
			
			//an int array is more cache friendly in java
			//this way you don't have to follow a reference to each word and then get the length
			
			//checkList2 must have a length greater than 0 or there will be no valid positions
			if(checkList2[lengthIndex].length>0){
				//TODO: maybe add row based check if checklist.length>1
				p0++;//Count the number of times this point is reached
				//for each starting position on the row for the word
				for(int j=0;j<checkList2[lengthIndex].length;j++){
					p1++;//Count the number of times this point is reached, from 100 to 125 ms with these
					//get position
					final int position=checkList2[lengthIndex][j];
					//the filter
					if(blanks>0 || Help.hasNeededChars(neededChars[i], letterTypess[lengthIndex][position])){
						p2++;//Count the number of times this point is reached
						//TODO: use hasFreqDual instead of hasCharFreq2
//						if(Help.hasCharFreq2(checkList[i], charFreq[i], hasBytess[lengthIndex][position], blanks)){
						if(Help.hasFreqDual(charFreqDual[i], hasBytess[lengthIndex][position], blanks)){ //TEST
							//TODO: skip if position and length has no crossers (combination==2)
							if(combinations[lengthIndex][position]==2 || 
									Help.correctCrossing(wordlist[i], position, possible)){
								p3++;//Count the number of times this point is reached
								//TODO: skip if position and length has no letters (combination==1)
//								if(Help.isFitting(combinations[lengthIndex][position],wordlist[i],' ')){
								if(combinations[lengthIndex][position]==1 || 
										Help.isFitting(wordlist[i], row2, position, ' ')){
									p4++;//Count the number of times this point is reached
									//add if passed all filters
									res1.get(lengthIndex)[position].add(wordlist[i]);
								}
							}
						}
					}
				}
			}

		}
//		if(res!=null){
			addResToMovesList(rowIndex,gi.isTransposed,res1,gi);
//		}
	}
	
	/**
	 * a static version.
	 * @param rowIndex
	 * @param vertical
	 * @param res
	 * @param gi
	 * @param res2
	 */
	public static void addResToMovesList(final int rowIndex,final boolean vertical,
			final ArrayList<ArrayList<String>[]> res,final GameInfo2 gi,ArrayList<Move> res2){
		for(int index=0;index<res.size();index++){
			final ArrayList<String>[] list=res.get(index);
			for(int position=0;position<list.length;position++){
				final ArrayList<String> words=list[position];
				for(int i=0;i<words.size();i++){
					final String word=words.get(i);
					final int x;
					final int y;
					if(vertical){
						y=position;
						x=rowIndex;
					} else {
						x=position;
						y=rowIndex;
					}
					final Move m=new Move(gi.points(word, x, y, vertical, new boolean[word.length()]), word, x, y, vertical);
					res2.add(m);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param rowIndex
	 * @param vertical
	 * @param res
	 * @param gi
	 */
	public void addResToMovesList(final int rowIndex,final boolean vertical,final ArrayList<ArrayList<String>[]> res,final GameInfo2 gi){
		addResToMovesList(rowIndex,vertical,res,gi,moves);
//		for(int index=0;index<res.size();index++){
//			final ArrayList<String>[] list=res.get(index);
//			for(int position=0;position<list.length;position++){
//				final ArrayList<String> words=list[position];
//				for(int i=0;i<words.size();i++){
//					final String word=words.get(i);
//					final int x;
//					final int y;
//					if(vertical){
//						y=position;
//						x=rowIndex;
//					} else {
//						x=position;
//						y=rowIndex;
//					}
//					final Move m=new Move(gi.points(word, x, y, vertical, new boolean[word.length()]), word, x, y, vertical);
//					moves.add(m);
//				}
//			}
//		}
	}
	
	public static String[] getPrintStringFromPossible(boolean[][] possible){
		StringBuilder sb=new StringBuilder();
		String[] sarr=new String[possible.length];
		for(int i=0;i<possible.length;i++){
			if(possible[i]!=null){
				for(int j=0;j<possible[i].length;j++){
					if(possible[i][j]){
						sb.append((char)(j+'a'));
					}
				}
				sarr[i]=sb.toString();
				sb.setLength(0);
			}
		}
		return sarr;
	}
	
	/**
	 * Debug the faster version, and use this as a reference implementation.
	 * 
	 * Made for the advanced bot.
	 * row is the whole row.
	 * length is the length of the words tested for.
	 * crossing is a string array with all words that cross the row with a space for
	 * the crossing point.
	 * impossible is points that it's impossible to create a word by inserting one letter.
	 * mayHaveChanged is the result from the method with the same name
	 * 
	 * null means impossible to create word there.
	 * empty string means it dosent intersect the new word
	 * @param row
	 * @param length
	 * @param haveAdjacent
	 * @param fastCrossers
	 * @param mayHaveChanged use the mayHaveChanged method
	 * @return
	 */
	public static String[] getCharCombinationsUpdate(final int rackLength, final String row, final int length,
			final String[][] fastCrossers, final boolean[] impossible, final boolean[] mayHaveChanged){
		final String[] sarr=new String[row.length()-length+1];
		for(int i=0;i<sarr.length;i++){
			//mayHaveChanged==null means it is from scratch
			//a 1 or higher change must be inside the word
			if(mayHaveChanged==null || mayHaveChanged[i]){
				//it must not be impossible
				if(!Help.impossibleInRange(i,length,impossible)){
					String tmp=row.substring(i,i+length);
					String tmp2=tmp.replaceAll(" ", "");//remove empty places
					//must have room to place pieces
					if(tmp.length()!=tmp2.length()){
						//must have enough letters
						if(length-tmp2.length()<=rackLength){
							//it must be empty before and after the word, or else it's not the given length
							if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
								if(tmp2.length()==0){
									for(int j=i;j<i+length;j++){
										//must be at least one adjacent letter
										//if the row is empty
										//looks for an adjacent letter
										if(fastCrossers[j]!=null){
											sarr[i]=tmp;
											break;
										}
									}
								} else {
									sarr[i]=tmp;
								}

							}
						}

					}
				}
			}
		}
		return sarr;
	}
	
	/**
	 * untested, should be faster
	 * @param rackLength
	 * @param row
	 * @param length
	 * @param fastCrossers
	 * @param impossible
	 * @return
	 */
	public static String[] getCharCombinationsFaster(final int rackLength, final char[] row, final int length,
			final String[][] fastCrossers, final boolean[] impossible){
//		String[] sarr=new char[row.length-length+1][];
		String[] sarr=new String[row.length-length+1];
		
		//Initialize values
		int spaces=0;
		int impossibles=0;
		int crossers=0;
		//count all the number of spaces and impossibles, except the next
		for(int i=0;i<length-1;i++){
			if(row[i]==' '){
				spaces++;
			}
			if(impossible[i]){
				impossibles++;
			}
			if(fastCrossers[i]!=null){
				crossers++;
				System.out.println(i);
			}
		}



		for(int i=0,end=i+length-1;i<sarr.length;i++,end++){
			//count next
			if(row[end]==' '){
				spaces++;
			}
			if(impossible[end]){
				impossibles++;
			}
			if(fastCrossers[end]!=null){
				crossers++;
				System.out.println(end);
			}
//			System.out.println("cross: "+crossers);
			
			
			boolean maybePossible=false;
			//it must not be impossible
			if(impossibles==0){
				//must have room to place pieces, must have enough letters in rack
				if((spaces>0 && spaces<=rackLength)){
					//it must be empty before and after the word, or else it's not the given length
					if( (i==0 || row[i-1]==' ') && ((i+length)==row.length || row[i+length]==' ')){
						if(spaces==length){
							//must be crossing words if there are no encountered letters
							if(crossers>0){
								maybePossible=true;
							}
						} else {
							maybePossible=true;
						}

					}


				}
			}
			
			if(maybePossible){
				sarr[i]=String.valueOf(row, i, length);
			}

			//uncount old
			if(row[i]==' '){
				spaces--;
			}
			if(impossible[i]){
				impossibles--;
			}
			if(fastCrossers[i]!=null){
				crossers--;
				System.out.println(i);
			}

		}
		return sarr;
	}
	
	/**
	 * untested, should be faster.
	 * 
	 * 0=impossible to place word at position.
	 * 1=possible, and can skip fit test.
	 * 2=possible, and can skip crosstest.
	 * 3=possible, can't skip.
	 * 
	 * @param rackLength
	 * @param row
	 * @param length
	 * @param fastCrossers
	 * @param impossible
	 * @return
	 */
	public static int[] getCharCombinationsFaster2(final int rackLength, final char[] row, final int length,
			final String[][] fastCrossers, final boolean[] impossible){
//		String[] sarr=new char[row.length-length+1][];
		int[] sarr=new int[row.length-length+1];
		
		//Initialize values
		int spaces=0;
		int impossibles=0;
		int crossers=0;
		//count all the number of spaces and impossibles, except the next
		for(int i=0;i<length-1;i++){
			if(row[i]==' '){
				spaces++;
			}
			if(impossible[i]){
				impossibles++;
			}
			if(fastCrossers[i]!=null){
				crossers++;
				System.out.println(i);
			}
		}



		for(int i=0,end=i+length-1;i<sarr.length;i++,end++){
			//count next
			if(row[end]==' '){
				spaces++;
			}
			if(impossible[end]){
				impossibles++;
			}
			if(fastCrossers[end]!=null){
				crossers++;
				System.out.println(end);
			}
//			System.out.println("cross: "+crossers);
			
			//it must not be impossible
			if(impossibles==0){
				//must have room to place pieces, must have enough letters in rack
				if((spaces>0 && spaces<=rackLength)){
					//it must be empty before and after the word, or else it's not the given length
					if( (i==0 || row[i-1]==' ') && ((i+length)==row.length || row[i+length]==' ')){
						if(spaces==length){
							//must be crossing words if there are no encountered letters
							if(crossers>0){
								sarr[i]=1;//crossers>0, spaces==length, can skip fit
							}
						} else {
							if(crossers==0){
								sarr[i]=2;//crossers==0, spaces<length, can skip crossers
							} else {
								sarr[i]=3;//crossers>0, spaces<length, can't skip
							}
						}
					}
				}
			}

			//uncount old
			if(row[i]==' '){
				spaces--;
			}
			if(impossible[i]){
				impossibles--;
			}
			if(fastCrossers[i]!=null){
				crossers--;
				System.out.println(i);
			}

		}
		return sarr;
	}
	
	public static void main(String args[]){
		char[] row="       hej f   fkkgf  fk".toCharArray();
		String[][] fastCrossers=new String[row.length][];
		String[] t={"hell",""};
		fastCrossers[0]=t;
		
		boolean[] impossible=Help.impossible(Help.possibleLetters(fastCrossers, new WordFinder()));
		
		String[] combinations=getCharCombinationsFaster(7,row, 3,
				fastCrossers, impossible);
		for(String s:combinations){
			System.out.println(":"+s);
		}
		
		System.out.println(Arrays.toString(impossible));
		System.out.println(Arrays.toString(fastCrossers));
	}
	
	
	
	public static boolean changed(final int start,final int length, final int changed[]){;
		for(int i=0,index=start;i<length;i++,index++){
			if(changed[index]>0){//contains a type one change or larger
				return true;
			}
		}
		return false;
	}
	
	public boolean[] mayHaveChanged(final int length,final int changed[]){
		final boolean[] mayHaveChanged=new boolean[15-length+1];
		for(int i=0;i<mayHaveChanged.length;i++){
			mayHaveChanged[i]=changed(i,length,changed);
		}
		return mayHaveChanged;
	}
	/**
	 * untested, should give the same result as changed.
	 * can have adjusted from and to ranges so that it's 
	 * slightly faster.
	 * @param length
	 * @param pos
	 * @param changed
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean changed2(final int length,final int pos, final int changed[],final int from,final int to){
		final int start=Math.max(pos, from);
		final int end=Math.min(pos+length-1, to);//index not length
		for(int i=start;i<=end;i++){
			if(changed[i]>0){//contains a type zero change or larger
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns the index of the first changed place
	 * if there is no change returns -1
	 * @param changed
	 * @return
	 */
	private int getFromChnaged(final int changed[]){
		for(int i=0;i<changed.length;i++){
			if(changed[i]>0){
				return i;
			}
		}
		return -1;
	}
	
	private int getToChnaged(final int changed[]){
		for(int i=changed.length-1;i>=0;i--){
			if(changed[i]>0){
				return i;
			}
		}
		return -1;
	}
}
