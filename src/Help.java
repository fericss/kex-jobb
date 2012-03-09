
/**
 * contains some methods.
 * @author MJB1
 *
 */
public class Help {

	//ROW BASED HELP METHODS
	/**
	 * it's faster to insert a charcter with fast crossers
	 * you just do: crosser=fastCrossers[i][0]+word.charAt(i)+fastCrossers[i][1];.
	 * @param crossers
	 * @return
	 */
	public static String[][] fastCrossers(String[] crossers){
		String[][] fastCrossing=new String[crossers.length][];
		for(int i=0;i<crossers.length;i++){
			if(crossers[i]!=null){
				fastCrossing[i]=crossers[i].split(" ",5);
			}
		}
		return fastCrossing;
	}

	/**
	 * some crossers can't make any new words with a simple substitute so it
	 * can for example be impossible to insert a word there in the row.
	 * @param fastCrossers
	 * @return
	 */
	public static boolean[] impossible(String[][] fastCrossers,WordFinder wf){
		boolean[] impossible=new boolean[fastCrossers.length];
		for(int i=0;i<fastCrossers.length;i++){
			if(fastCrossers[i]!=null){
				impossible[i]=true;
				for(char j='a';j<='z';j++){
					if(wf.isWord(fastCrossers[i][0]+j+fastCrossers[i][1])){
						impossible[i]=false;
						break;
					}
				}
			}
		}
		return impossible;
	}
	
	public static boolean[] impossible(boolean[][] possible){
		boolean[] impossible=new boolean[possible.length];
		boolean impos;
		for(int i=0;i<impossible.length;i++){
			impos=true;
			for(int j=0;j<possible[i].length;j++){
				if(possible[i][j]){
					impos=false;
					break;
				}
			}
			impossible[i]=impos;
		}
		return impossible;
	}
	
	/**
	 * possible[charPos]==null means any character is possible, possible[charPos][char-'a']==true means that
	 * that char can be at that position.
	 * (it would be great if it could also)
	 * @param fastCrossers
	 * @return
	 */
	public static boolean[][] possible(String[][] fastCrossers,WordFinder wf){
		boolean[][] possible=new boolean[fastCrossers.length]['z'-'a'+1];
		for(int i=0;i<fastCrossers.length;i++){
			if(fastCrossers[i]!=null){
				for(char j='a';j<='z';j++){
					if(wf.isWord(fastCrossers[i][0]+j+fastCrossers[i][1])){
						possible[i][j-'a']=true;
					}
				}
			} else {
				possible[i]=null;
			}
		}
		return possible;
	}
	
	public static boolean correctCrossing(String word, int index,boolean[][] possible){
		for(int i=0;i<word.length();i++,index++){
			if(possible[index]!=null && 
					!possible[index][word.charAt(i)-'a']){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param word
	 * @param index
	 * @param fastCrossing
	 * @param wf
	 * @return
	 */
	public static boolean correctCrossing(String word, int index,String[][] fastCrossing,WordFinder wf){
		for(int i=0;i<word.length();i++,index++){
			if(fastCrossing[index]!=null){
				if(!wf.isWord( fastCrossing[index][0]+word.charAt(i)+fastCrossing[index][1]) ){
					return false;
				} 
			}
		}
		return true;
	}
	
	//OTHER HELP METHODS
	/**
	 * help method for concatinating all strings in a string array.
	 * Should be moved to a help methods class.
	 * @param sarr
	 * @return
	 */
	public static String concatinate(final String[] sarr){
		final StringBuilder sb=new StringBuilder();
		for(int i=0;i<sarr.length;i++){
			sb.append(sarr[i]);
		}
		return sb.toString();
	}

	//LETTER COUNTING METHODS
	/**
	 * Counts the frequency of each letter and stores the count for each letter in a byte array, where 
	 * index 0 contains the count for 'a'.
	 * @param s
	 * @return
	 */
	public static byte[] createFreq(final String s){
		final byte[] freq=new byte['z'-'a'+1];//size of alphabet
		for(int i=0;i<s.length();i++){
			freq[s.charAt(i)-'a']++;
		}
		return freq;
	}

	/**
	 * Uses an int as a bit array to store which chars are in the word.
	 * It's then very fast to check if one string contains all the letter types by
	 * using: (a&b)==a.
	 * If that returns true then b contains all letter types in a.
	 * @param s
	 * @return
	 */
	public static int getHasChars(String s){
		int res=0;
		for(int i=0;i<s.length();i++){
			res= res | (1<<(s.charAt(i)-'a'));
		}
		return res;
	}

	/**
	 * Checks that all the corresponding values in needFreq is less than or equal to hasFreq.
	 * Only checks the letters that is in the word.
	 * Uses the values in checklist to only check the indexes that need to be checked.
	 * Uses wildcards if it's needed.
	 * @param checkList
	 * @param needFreq
	 * @param hasFreq
	 * @return
	 */
	static boolean hasCharFreq2(final byte[] checkList,final byte[] needFreq, final byte[] hasFreq,int wildCards){
		int i;
		for(int j=0;j<checkList.length;j++){
			i=checkList[j];
			if(needFreq[i]>hasFreq[i]){
				wildCards=wildCards-(needFreq[i]-hasFreq[i]);
				if(wildCards<0){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns a list of the indexes that contain a number greater than zero.
	 * @param charFreq
	 * @return
	 */
	public static byte[] getCheckList(final byte[] charFreq){
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
	 * Checks that all the "neededChars" is in "hasChars". 
	 * @param neededChars
	 * @param hasChars
	 * @return
	 */
	public static boolean hasNeededChars(final int neededChars,final int hasChars){
		return (neededChars & hasChars) == neededChars;
	}

}
