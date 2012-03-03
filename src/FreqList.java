import java.util.ArrayList;

/**
 * will use the frequency of each letter in each word
 * to filter out those words that is impossible to create
 * with the given letters.
 * Assumes that small characters are used.
 *
 * performance:
 * filter: fjriuhouihroij wordListLength: 38619 constructionTime: 10 filterTime: 4780 filterRepeats: 10000
 * @author mbernt.mbernt
 *
 */
public class FreqList {
	final ArrayList<Freq> list;
	
	/**
	 * for testing performance
	 * @param args
	 */
	public final  static void main(String[] args){
		String[] wordlist=new WordFinder().getWordlist();
		timingTest("fjriuhouihroij",wordlist,10000);
	}
	
	public static void timingTest(String filter,String[] wordlist,int filterRepeats){
		long t1,t2,t3;
		t1=System.currentTimeMillis();
		FreqList fl=new FreqList(wordlist);
		t2=System.currentTimeMillis();
		for(int i=0;i<filterRepeats;i++){
			ArrayList<String> st=fl.getFilteredList(filter);
			String s=st.get(0);
		}
		t3=System.currentTimeMillis();
		System.out.println("filter: "+filter+" wordListLength: "+wordlist.length+" constructionTime: "+(t2-t1)+" filterTime: "+(t3-t2)+" filterRepeats: "+filterRepeats);
	}

	/**
	 * construct with the wordlist as input
	 * @param wordlist
	 */
	public FreqList(String[] wordlist){
		list=arrToFreqList(wordlist);
	}
	
	/**
	 * gets a filtered list
	 * @param rowPlusRack
	 * @return
	 */
	public ArrayList<String> getFilteredList(String rowPlusRack){
		Freq filter=new Freq(rowPlusRack);
		return filter.filter(this.list);
	}
	
	
	
	
	
	/**
	 * use this on the wordlist to get a Freq list
	 * @param wordlist
	 * @return
	 */
	private ArrayList<Freq> arrToFreqList(String[] wordlist){
		ArrayList<Freq> res=new ArrayList<Freq>(wordlist.length);
		for(int i=0;i<wordlist.length;i++){
			res.add(new Freq(wordlist[i]));
		}
		return res;
	}
	
	/**
	 * experimental class used for checking if the word contains more characters
	 * of different types than is contained in the rack and the row.
	 * Assumes that small characters are used.
	 * @author mbernt
	 *
	 */
	public class Freq{
		
		
		final String s;
		final byte[] freq;
		public Freq(final String _s){
			s=_s;
			freq=createFreq(_s);
		}
		
		public String getString(){
			return s;
		}
		
		/**
		 * returns false if the word contains more characters of 
		 * a type than the row and rack put together
		 * @param word
		 * @return
		 */
		private boolean freqTest(final byte [] word){
			for(int i=0;i<freq.length;i++){
				if(freq[i]<word[i]){
					//if there are less letters available than needed...
					return false;
				}
			}
			return true;
		}
		
		/**
		 * call this first with the rack+row
		 * then use the resulting array with freqTest on each word
		 * @param s
		 * @return
		 */
		private byte[] createFreq(final String s){
			byte[] freq=new byte['z'-'a'+1];//size of alphabet
			for(int i=0;i<s.length();i++){
				freq[s.charAt(i)-'a']++;
			}
			return freq;
		}
		
		protected byte[] getFreq(){
			return freq;
		}
		
		
		
		/**
		 * create a Freq from  row+rack and call it from that object
		 * use freqWordList as input
		 * returns a new arraylist that are filtered
		 * @param freqWordList
		 * @return
		 */
		protected ArrayList<String> filter(ArrayList<Freq> freqWordList){
			ArrayList<String> res=new ArrayList<String>();
			for(int i=0;i<freqWordList.size();i++){
				if(freqTest(freqWordList.get(i).getFreq())){
					res.add(freqWordList.get(i).getString());
				}
			}
			return res;
		}
		
	}
}
