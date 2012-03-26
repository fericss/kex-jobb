import java.util.ArrayList;



public class MartinTest {
	public final static void main(String[] args){
		testCombinations("head","hea.dfg");
	}
	
	/**
	 * needs the exact needed letters if the word should be placed at a specified point
	 * and the rack including wildcards.
	 * @param nededLetters
	 * @param rack
	 */
	public static ArrayList<String> testCombinations(String nededLetters,String rack/*,int[] indexes*/){
		int wildcards=rack.length();
		wildcards=wildcards-rack.replaceAll("\\.", "").length();
//		System.out.println(wildcards);
		ArrayList<String> res1=new ArrayList<String>();
		help(res1,new StringBuilder(nededLetters),wildcards,0);
		ArrayList<String> res2=new ArrayList<String>();
		for(String s:res1){
			
			if(WordFinder.WordCanBeBuiltFromSourceLetters(s, rack)){
//				System.out.println(s+"*");
				res2.add(s);
			} else {
//				System.out.println(s);
			}
		}
//		System.out.println(res1.size());
//		System.out.println("rack:("+rack+")");
		for(String s:res2){
//			System.out.println(s);
		}
		System.out.println("need: "+nededLetters+" rack: "+rack+" wildCombinations "+res2.size());
		return res2;
		
	}
	private static void help(ArrayList<String> res,StringBuilder neededLetters, int wildcards,int startIndex){
		char ch;
		for(int i=startIndex;i<neededLetters.length();i++){
			ch=neededLetters.charAt(i);
			neededLetters.setCharAt(i, '.');
			res.add(neededLetters.toString());
			if(wildcards>1){
				help(res,neededLetters, wildcards-1,i+1);
			} 
			neededLetters.setCharAt(i, ch);
		}
	}
	
	
	
	/**
	 * returns null if couldn't construct word else returns wildCount
	 * @param targetWord
	 * @param sourceLetters
	 * @return
	 */
	public static byte[] wildCount(String targetWord, String sourceLetters)
	{
		
		String builtWord = "";
		char[] letters = targetWord.toCharArray();
		byte[] wildCount=new byte['z'-'a'+1];
		for(char letter : letters){
			int pos = sourceLetters.indexOf(letter);
			if (pos >= 0){
				builtWord += letter;
				sourceLetters = Remove(pos, sourceLetters);
				continue;
			}
			// check for wildcard
			pos = sourceLetters.indexOf(".");
			if (pos >= 0){
				wildCount[letter-'a']++;
				builtWord += letter;
				sourceLetters = Remove(pos, sourceLetters);
			}
		}
		if(builtWord.equals(targetWord)){
			return wildCount;
		} else {
			return null;
		}
	}
	private static String Remove(int pos, String sourceLetters) {
		char[] wat = sourceLetters.toCharArray();
		for(int i = pos+1;i<wat.length;i++){
			wat[i-1] = wat[i];
			wat[i] = 0;
		}
		if(pos==wat.length-1){
			wat[pos]=0;
		}
		return (""+String.valueOf(wat));
	}
}
