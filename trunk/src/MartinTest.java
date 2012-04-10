import java.util.ArrayList;



public class MartinTest {
	public final static void main(String[] args){
//		testCombinations("head","hea.dfg");
		String word;
		String under;
		String rack;
		
		word ="head";
		under="  a ";//What's on the board under the word
		rack ="hea.dfg";
		System.out.println();
		System.out.println("test");
		for(String s:getAllCombinationsWithWildCards(word,under,rack)){
			System.out.println(s);
		}
		
		//BUG: can build words even though it has't all the needed letters
		word ="hoyer";
		under="ho e ";//What's on the board under the word
		rack ="hea..rg";
		
		System.out.println();
		System.out.println(word+" ("+under+") "+rack);
		for(String s:getAllCombinationsWithWildCards(word,under,rack)){
			System.out.println(s);
		}
	}
	
	/**
	 * this is the method to use
	 * @param word
	 * @param lettersBenethWord
	 * @param rack
	 * @return
	 */
	public static ArrayList<String> getAllCombinationsWithWildCards(String word,String lettersBenethWord,String rack){
		StringBuilder neededLetters=new StringBuilder();
		for(int i=0;i<word.length();i++){
			if(lettersBenethWord.charAt(i)==' '){
				neededLetters.append(word.charAt(i));
			}
		}
		ArrayList<String> res=testCombinations(neededLetters.toString(),rack);
		
		for(int j=0;j<res.size();j++){
			String s=res.get(j);
			StringBuilder sb=new StringBuilder();
			int index=0;
			for(int i=0;i<lettersBenethWord.length();i++){
				if(lettersBenethWord.charAt(i)==' '){
					sb.append(s.charAt(index));
					index++;
				} else {
					sb.append(lettersBenethWord.charAt(i));
				}
			}
			res.set(j, sb.toString());
			sb.setLength(0);
		}
		return res;
	}
	
	/**
	 * Use getAllCombinationsWithWildCards instead.
	 * 
	 * needs the exact needed letters if the word should be placed at a specified point
	 * and the rack including wildcards.
	 * 
	 * 
	 * @param neededLetters
	 * @param rack
	 */
	public static ArrayList<String> testCombinations(String neededLetters,String rack/*,int[] indexes*/){
		int wildcards=rack.length();
		wildcards=wildcards-rack.replaceAll("\\.", "").length();
//		System.out.println(wildcards);
		ArrayList<String> res1=new ArrayList<String>();
		help(res1,new StringBuilder(neededLetters),wildcards,0);
		ArrayList<String> res2=new ArrayList<String>();
		for(String s:res1){
//			System.out.println("yoo1: "+s+" "+rack);
//			if(WordFinder.WordCanBeBuiltFromSourceLetters(s, rack)){
			if(WordFinder.WordCanBeBuiltFromSourceLetters(s.replaceAll("\\.", ""), rack.replaceAll("\\.", ""))){
//				System.out.println("yoo: "+s+" "+rack);
				res2.add(s);
			} else {
//				System.out.println(s);
			}
		}
//		System.out.println(res1.size());
//		System.out.println("rack:("+rack+")");
//		System.out.println(neededLetters);
//		for(String s:res2){
//			System.out.println(s);
//		}
//		System.out.println("need: "+nededLetters+" rack: "+rack+" wildCombinations "+res2.size());
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
}
