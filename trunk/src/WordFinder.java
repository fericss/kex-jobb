import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WordFinder {
	private String[] wordList;
	private String[][] m_words;
	public WordFinder(){
		wordList = LoadWords();
	}
	
	public String[] getWordlist(){
		return wordList;
	}
	
	public List<String> Matches(String sourceLetters)
	{
		sourceLetters = sourceLetters.toUpperCase();
		List<String> matches = new ArrayList<String>();
		for(String word : wordList)
		{
			if(word!=null){
				if (WordCanBeBuiltFromSourceLetters(word.toUpperCase(), sourceLetters))
					matches.add(word);
			}
		}
		return matches;
	}

	private String[] LoadWords() {
		m_words = new String[16][];
		FileInputStream fstream;
		String[] words = new String[50000];
		int counter = 0;
		try {
			fstream = new FileInputStream("words.txt");

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));


			int numWords = 0;
			String line=null;
			while ((line =br.readLine()) != null){
				numWords++;
			}
			words = new String[numWords];
			//			System.out.println(""+numWords);
			br.close();
			in.close();
			fstream.close();

			fstream = new FileInputStream("words.txt");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				words[counter] = strLine;
				counter++;
			}
			br.close();
			in.close();
			fstream.close();

			/// Create some arrays with words of certain lengths, for example m_words[5] has all words of length 5
			for (int i = 2; i <= 15; i++) {
				fstream = new FileInputStream("words.txt");
				in = new DataInputStream(fstream);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
				// Count the number of lines in the file....
				numWords = 0;
				line=null;
				while ((line =bufferedReader.readLine()) != null){
					if(line.length()==i)
						numWords++;
				}

				br.close();
				in.close();
				fstream.close();
				m_words[i] = new String[numWords];
				fstream = new FileInputStream("words.txt");
				in = new DataInputStream(fstream);
				bufferedReader = new BufferedReader(new InputStreamReader(in));
				numWords = 0;
				line=null;
				while ((line =bufferedReader.readLine()) != null){
					if(line.length()==i){
						m_words[i][numWords] = line;
						numWords++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return words;
	}
	public boolean WordCanBeBuiltFromSourceLetters(String targetWord, String sourceLetters)
	{
		String builtWord = "";
		char[] letters = targetWord.toCharArray();
		for(char letter : letters)
		{

			//			char letter = targetWord.charAt(i);
			int pos = sourceLetters.indexOf(letter);
			//			System.out.println(""+letter+" "+pos);
			if (pos >= 0)
			{
				//				System.out.println("bla");
				builtWord += letter;
				sourceLetters = Remove(pos, sourceLetters);
				continue;
			}


			// check for wildcard
			pos = sourceLetters.indexOf(".");
			if (pos >= 0)
			{
				builtWord += letter;

				sourceLetters = Remove(pos, sourceLetters);
			}
		}
		return builtWord.equals(targetWord);
	}
	private String Remove(int pos, String sourceLetters) {
		// TODO Auto-generated method stub
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
	public static void main(String[] args){
		WordFinder wordList = new WordFinder();
		System.out.println(wordList.Matches("fhosedextrnaa"));

	}
	public boolean isWord(String s) {
		if (s.length() < 2 || s.length() > 15)
			return false;
		String[] w = m_words[s.length()];
		return Arrays.binarySearch(w, s) >= 0;
	}
}
