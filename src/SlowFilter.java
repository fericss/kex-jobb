import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;



/**
 * Uses stuff from FastFilter and greedy bot to make a complete filtering with only valid words for each position.
 * 
 * Might be used in advanced bot because it can replace just those places that need to be recalculated (this has not been tested yet
 * and it has not been fully implemented).
 * 
 * It has almost the same speed as greedy bot when there are no wildcards, but is almost two times faster when there are wildcards.
 * It still might have bugs...
 * 
 * i.e. it has a higher overhead but then it's faster, but that isn't noticed until there are many possible combinations.
 * 
 * Instructions:
 * use slowFilterUpdate
 * the result now is in moves
 * 
 * 
 * @author mbernt.mbernt
 *
 */
public class SlowFilter {
	final String[] wordlist; //the wordlist
	final int[] neededChars; //the ints functions as bit array, with a one for each letter type in the word
	final byte[][] charFreq;//list of the frequency of each letter in each word
	final byte[][] checkList;//only need to check chars in the word
	final int[] wordLengths;
	
	//these are set when called reset
	public int p0;
	public int p1;
	public int p2;
	public int p3;
	public int p4;
	ArrayList<Move> moves;
	
	public SlowFilter(String[] _wordlist){
		//initialize
		wordlist=_wordlist;
		neededChars=new int[wordlist.length];
		charFreq=new byte[wordlist.length][];
		checkList=new byte[wordlist.length][];
		wordLengths=new int[wordlist.length];
		
		//calculate properties for each word
		for(int i=0;i<wordlist.length;i++){
			String s;
			if(wordlist[i].matches("[a-z]+")){
				neededChars[i]=Help.getHasChars(wordlist[i]);
				charFreq[i]=Help.createFreq(wordlist[i]);
				checkList[i]=Help.getCheckList(charFreq[i]);
				wordLengths[i]=wordlist[i].length();
			} else {
				System.out.println("StrangeWord: "+wordlist[i]);
				neededChars[i]=0;
				charFreq[i]=null;
				checkList[i]=null;
				wordLengths[i]=0;
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
	
//	public ArrayList<ArrayList<String>[]> slowFilter3(String rack, String row,
//			final String[][] fastCrossers,final boolean[] impossible,final WordFinder wf,final GameInfo gi){
//		
//
//		int racklength=rack.length();
//
//		rack=rack.toLowerCase();
//		row=row.toLowerCase();
//
//		//count wildcards
//		int wildcards=rack.length();
//		rack=rack.replaceAll("\\.", "");
//		wildcards=wildcards-rack.length();
//		//count unknowns
//		int unknowns=rack.length();
//		rack=rack.replaceAll(",", "");
//		unknowns=unknowns-rack.length();
//		//count unknown wildcards
//		int unknownWildCards=gi.getUnknownWildCards();
//		byte[] unknown=gi.getUnknownFreq();
//
//		final boolean[][] possible=Help.possibleLetters(fastCrossers, wf);
//
//		int size=15-1;
//		
//		
//		//data about row and position
//		final String[][] comb=new String[size][];
//		final Pattern[][] patterns=new Pattern[size][];
//		final byte[][][] hasBytess=new byte[size][][];
//
//		//Initialize
//		ArrayList<ArrayList<String>[]> res=new ArrayList<ArrayList<String>[]>();
//
//		//
//		for(int length=2,index=0;length<=15;length++,index++){
//			//Real, get the combinations for a length
//			comb[index]=MartinsFilterTest.getCharCombinations4(racklength,row,length,fastCrossers,impossible);
//			//Initialize
//			hasBytess[index]=new byte[comb[index].length][];
//			patterns[index]=new Pattern[comb[index].length];
//			//get properties for each comb
//			for(int i=0;i<comb[index].length;i++){
//				if(comb[index][i]!=null){
//					//Initialize
//					final String boardLetters=comb[index][i].replaceAll(".","");
//					final String sourceLetters=rack+boardLetters;
//					final byte[] freq=Help.createFreq(sourceLetters);
//					//Real
//					hasBytess[index][i]=freq;
//					//Real
//					patterns[index][i]=boardLetters.length()==0?null:Pattern.compile(comb[index][i]);
//				}
//			}
//			//Initialize
//			@SuppressWarnings("unchecked")
//			ArrayList<String>[] tmp =new ArrayList[comb[index].length];
//			for(int i=0;i<tmp.length;i++){
//				tmp[i]=new ArrayList<String>();
//			}
//			res.add(tmp);
//		}
//		
//
//		//do filtering
//		//for each word
//		for(int i=0;i<wordlist.length;i++){
//			final String word=wordlist[i];
//			final int index=word.length()-2;
//			//			final ArrayList<String>[] res1=res.get(index);
//			final String sarr[]=comb[index];
//			//for each starting position on the row for the word
//			for(int j=0;j<sarr.length;j++){
//				if(sarr[j]!=null){
//					//retrieve data about position
//					final byte[] hasFreq=hasBytess[index][j];
//					final Pattern p=patterns[index][j];
//					//check, ha enough chars in rack
//					if(Help.hasCharFreq3(checkList[i], charFreq[i], hasFreq, wildcards, unknownWildCards, unknown, unknowns)
//							&& (p==null || p.matcher(word).matches())
//							&& Help.correctCrossing(word, j, possible)
//					){
//						res.get(index)[j].add(word);
//					}
//
//				}
//			}
//		}
//		return res;
//	}
	

	/**
	 * copies all the way to arrays.copy 
	 * as long as the lists in the array lists at
	 * each index in the array isn't modified and instead 
	 * replaced then the old res won't be changed.
	 * @param res
	 * @return
	 */
	public ArrayList<ArrayList<String>[]> cloneResList(ArrayList<ArrayList<String>[]> res){
		ArrayList<ArrayList<String>[]> newRes=new ArrayList<ArrayList<String>[]>();
		for(int length=2,index=0;length<=15;length++,index++){
			newRes.add(Arrays.copyOf(res.get(index), res.get(index).length));
		}
		return newRes;
	}
	
	
	
	/**
	 * if res==null it means that it has to calculate everything from scratch.
	 * 
	 * @param rowIndex
	 * @param vertical
	 * @param wf
	 * @param gi
	 * @param res
	 * @return
	 */
	public ArrayList<ArrayList<String>[]> slowFilterUpdate(final int rowIndex, boolean vertical, 
			final WordFinder wf,final GameInfo gi,
			ArrayList<ArrayList<String>[]> res){
		//maybe change so that res is saved in gameinfo?
		
		//pre-calculate stuff...
		String rack=gi.getRack().toLowerCase();
		final String row=gi.getRow(rowIndex, vertical).toLowerCase();
		final String[][] fastCrossers=gi.getFastCrossers(rowIndex, vertical);
		final boolean[][] possible=Help.possibleLetters(fastCrossers, wf);//TODO: want update version, maybe add it to gameinfo
		final boolean[] impossible=Help.impossible(possible);
		final int[] changed=gi.getChanged(rowIndex, vertical);
		
		
		//clone old res so that old data isn't destroyed
		//after this don't add or remove to the lowest level lists without
		//replacing them first
		if(changed!=null){
			res=cloneResList(res);
		}
		
		
		final int racklength=rack.length();

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
		//get freq of unknown chars
		byte[] unknown=gi.getUnknownFreq();

		

		int size=15-1;
		
		//
//		final byte[] rowFreq=Help.createFreq(rack+row.replaceAll(" ", ""));
		
		//data about row and position
		final String[][] combinations=new String[size][];
		final int[][] checkList2=new int[size][];
		final byte[][][] hasBytess=new byte[size][][];
		final boolean[][] mayHaveChanged=new boolean[size][];//is also used to clear old data from oldRes
		int[][] letterTypess=new int[size][];
		
		//Initialize data about row an position
		for(int length=2,index=0;length<=15;length++,index++){
			//Real, get the combinations for a length
			if(changed==null){
				combinations[index]=getCharCombinationsUpdate(racklength,row,length,fastCrossers,impossible,null);//TEST
			} else {
				mayHaveChanged[index]=mayHaveChanged(length,changed);
				combinations[index]=getCharCombinationsUpdate(racklength,row,length,fastCrossers,impossible,mayHaveChanged[index]);//TEST
			}
			int checkSize=0;
			for(int position=0;position<combinations[index].length;position++){
				if(combinations[index][position]!=null){
					checkSize++;
				}
			}
			checkList2[index]=new int[checkSize];
			int cindex=0;
			
			//Initialize hasBytess and patterns
			int combLen=combinations[index].length;
			hasBytess[index]=new byte[combLen][];
			letterTypess[index]=new int[combLen];
			//patterns[index]=new Pattern[combLen];
			//get properties for each comb
			for(int position=0;position<combinations[index].length;position++){
				if(combinations[index][position]!=null){
					//Initialize
					final String boardLetters=combinations[index][position].replaceAll("\\.","");
					final String sourceLetters=rack+boardLetters;
					
					final byte[] freq=Help.createFreq(sourceLetters);
					hasBytess[index][position]=freq;
					
					final int letterTypes=Help.getHasChars(sourceLetters);
					letterTypess[index][position]=letterTypes;
					
					checkList2[index][cindex]=position;
					cindex++;
				}
			}
		}
		
		
		
		//initialize the res list 0 ms
		if(changed==null) {
			res=new ArrayList<ArrayList<String>[]>();
			for(int length=2,index=0;length<=15;length++,index++){
				@SuppressWarnings("unchecked")
				ArrayList<String>[] tmp =new ArrayList[combinations[index].length];
				for(int position=0;position<tmp.length;position++){
					tmp[position]=new ArrayList<String>();
				}
				res.add(tmp);
			}
		} else {
			//clean all places that might may have changed
			//BUG: have to clean those that have become impossible because not all letters are available
			for(int length=2,index=0;length<=15;length++,index++){
				for(int position=0;position<res.get(index).length;position++){
					if(mayHaveChanged[index][position]){
						res.get(index)[position]=new ArrayList<String>();
					}
				}
			}
		}
		
		//check if there are no possible words on row
		boolean finished=true;
		for(int i=0;i<checkList2.length;i++){
			if(checkList2[i].length>0){
				finished=false;
				break;
			}
		}
		if(finished){
//			System.out.println("nothing on row!");
			return res;
		}
		
		//overhead when doing nothing but looping through word ? ms
		//do filtering
		//for each word
		for(int i=0;i<wordlist.length;i++){
			//an int array is more cache friendly in java
			//this way you don't have to follow a reference to each word and then get the length
			final int index=wordLengths[i]-2;
			if(checkList2[index].length>0){
				p0++;
				final String word=wordlist[i];
				final String sarr[]=combinations[index];
				//for each starting position on the row for the word
				for(int j=0;j<checkList2[index].length;j++){
					p1++;//from 100 to 125 ms with these
					final int position=checkList2[index][j];
					//if(sarr[position]!=null){//this check is already done indirectly by checkList2
					//retrieve data about position
					//filter
					if(wildcards>0 || Help.hasNeededChars(neededChars[i], letterTypess[index][position])){
						p2++;
						if(Help.hasCharFreq2(checkList[i], charFreq[i], hasBytess[index][position], wildcards)){
							if(Help.correctCrossing(word, position, possible)){
								p3++;
								if(Help.isFitting(sarr[position],word,'.')){
									p4++;
									res.get(index)[position].add(word);
								}
							}
						}
					}
				}
			}

		}
		if(res!=null){
			addResToMovesList(rowIndex,vertical,res,gi);
		}
		return res;
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
	public void slowFilterOptimized(final int rowIndex, boolean vertical, final WordFinder wf,final GameInfo gi,
			ArrayList<Move> res2){
		//pre-calculate stuff...
		final String row=gi.getRow(rowIndex, vertical).toLowerCase();
		final String[][] fastCrossers=gi.getFastCrossers(rowIndex, vertical);
		final boolean[][] possible=Help.possibleLetters(fastCrossers, wf);
		final boolean[] impossible=Help.impossible(possible);
		
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
				final String boardLetters=combinations[lengthIndex][position].replaceAll("\\.","");
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
								if(Help.isFitting(combinations[lengthIndex][position],wordlist[i],'.')){
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
			addResToMovesList(rowIndex,vertical,res,gi);
//		}
	}
	
	public void addResToMovesList(final int rowIndex,final boolean vertical,final ArrayList<ArrayList<String>[]> res,final GameInfo gi,ArrayList<Move> res2){
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
	
//	public void addResToMovesList(final int rowIndex,final boolean vertical,final String word,final int position,final GameInfo gi,ArrayList<Move> res2){
//		final int x;
//		final int y;
//		if(vertical){
//			y=position;
//			x=rowIndex;
//		} else {
//			x=position;
//			y=rowIndex;
//		}
//		final Move m=new Move(gi.points(word, x, y, vertical, new boolean[word.length()]), word, x, y, vertical);
//		res2.add(m);
//	}
	
	public void addResToMovesList(final int rowIndex,final boolean vertical,final ArrayList<ArrayList<String>[]> res,final GameInfo gi){
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
					moves.add(m);
				}
			}
		}
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
	public static String[] getCharCombinationsFaster(final int rackLength, final String row, final int length,
			final String[][] fastCrossers, final boolean[] impossible){
		String[] sarr=new String[row.length()-length+1];
		
		//Initialize values
		int spaces=0;
		int impossibles=0;
		//count all the number of spaces and impossibles, except the next
		for(int i=0;i<length-1;i++){
			if(row.charAt(i)==' '){
				spaces++;
			}
			if(impossible[i]){
				impossibles++;
			}
		}



		for(int i=0,end=i+length-1;i<sarr.length;i++,end++){
			//count next
			if(row.charAt(end)==' '){
				spaces++;
			}
			if(impossible[end]){
				impossibles++;
			}

			//it must not be impossible
			if(impossibles==0){
				//must have room to place pieces, must have enough letters in rack
				if(spaces>0 && spaces<=rackLength){
					//it must be empty before and after the word, or else it's not the given length
					if( (i==0 || row.charAt(i-1)==' ') && ((i+length)==row.length() || row.charAt(i+length)==' ')){
						if(spaces==length){
							for(int j=i;j<i+length;j++){
								//must be at least one adjacent letter
								//if the row is empty
								//looks for an adjacent letter
								if(fastCrossers[j]!=null){
									sarr[i]=row.substring(i,i+length).replaceAll(" ", ".");
									break;
								}
							}
						} else {
							sarr[i]=row.substring(i,i+length).replaceAll(" ", ".");
						}

					}


				}
			}

			//uncount old
			if(row.charAt(i)==' '){
				spaces--;
			}
			if(impossible[i]){
				impossibles--;
			}

		}
		return sarr;
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
