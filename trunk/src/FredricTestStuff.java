import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class FredricTestStuff {

	private String[][] game;
	ArrayList<Point> buildLocations;
	WordFinder find;
	String rack;
	Main main;
//	private String[][] m_words;
	public FredricTestStuff(String[][] _game, ArrayList<Point> _buildLocations, String _rack, Main _main){
//		wordList = _wordList;
//		m_words = _m_words;
		game = _game;
		main = _main;
		rack = _rack;
		find = new WordFinder();
		buildLocations = _buildLocations;
//		ArrayList<String> wordThatCanBeBuildLol= new ArrayList<String>();
//		for(Point p: buildLocations){
//			if(p!=null)
//			getWordThatCanBeBuilt(p);
//		}
		
		for(int row = 0; row<15;row++){
//			getWordThatCanBeBuiltOnCol(row);
			getWordThatCanBeBuiltOnRow(row);
		}
		
	}
	private Collection<String> getWordThatCanBeBuiltOnRow(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String line="";
		String row = null;
		for(int i = 0; i<15; i++){
			
			if(row!=null && (game[x][i]==null || game[x][i].equals("_"))){
				words.add(row);
				row = null;
			}
			if(game[x][i]!=null && !game[x][i].equals("_")){
				if(row==null){
					row = "";
				}
				line+=game[x][i];
				row+=game[x][i];
				letters+=game[x][i];
			}
			else{
				line+=" ";
			}
		}
//		for(String s : words){
//			System.out.print("\n"+s+", ");
//		}
		List<String> wordsThatCanBeBuilt = new ArrayList<String>();
		System.out.println("\n");
		List<String> test = find.Matches(rack+letters);
		for(String word : test){
			for(String s2 : words){
				if(word.contains(s2.toLowerCase())){
//					System.out.print(word+", ");
					int board_pos = line.indexOf(s2.toUpperCase());
					int word_pos = word.indexOf(s2.toLowerCase());
					if((word.length()-word_pos+board_pos<=15) && ((board_pos-word_pos)>=0)){
						if(canBePlacedCol(word,x,board_pos,word_pos))
						System.out.print(""+board_pos+word+", ");
					}
				}
			}
		}
		return null;
	}

	private boolean canBePlacedCol(String word, int x, int boardPos, int wordPos) {
		int startPos = boardPos-wordPos;
		for(int i = 0; i<word.length();i++){
			
		}
		return false;
	}
	private Collection<String> getWordThatCanBeBuiltOnCol(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String line="";
		String row = null;
		for(int i = 0; i<15; i++){
			
			if(row!=null && (game[i][x]==null || game[i][x].equals("_"))){
				words.add(row);
				row = null;
			}
			if(game[i][x]!=null && !game[i][x].equals("_")){
				if(row==null){
					row = "";
				}
				line+=game[i][x];
				row+=game[i][x];
				letters+=game[i][x];
			}
			else{
				line+=" ";
			}
		}
//		for(String s : words){
//			System.out.print("\n"+s+", ");
//		}
		List<String> wordsThatCanBeBuilt = new ArrayList<String>();
		System.out.println("\n");
		List<String> test = find.Matches(rack+letters);
		for(String word : test){
			for(String s2 : words){
				if(word.contains(s2.toLowerCase())){
//					System.out.print(word+", ");
					int board_pos = line.indexOf(s2.toUpperCase());
					int word_pos = word.indexOf(s2.toLowerCase());
					if(((word.length()-word_pos)+board_pos<=15) && ((board_pos-word_pos)>=0)){
						System.out.print(word+", ");
					}
				}
			}
		}
		return null;
	}
	private Collection<String> getWordThatCanBeBuilt(Point p) {
		int x = p.x;
		int y = p.y;
		String row = "";
		for(int i = 0; i<15; i++){
			if(game[x][i]!=null && !game[x][i].equals("_"))
			row+=game[x][i];
		}
		System.out.println("");
		List<String> test = find.Matches(rack+row);
		for(String s : test){
			System.out.print(s+", ");
		}
		return null;
	}
}
