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
	public static ArrayList<String> testCombinations(String nededLetters,String rack){
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
		System.out.println(res2.size()+" wild combinations.");
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
