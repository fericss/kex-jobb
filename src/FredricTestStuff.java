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
//		System.out.println("testing "+word);
		String wordTest="";
		for(int i = (boardPos-wordPos); i<((boardPos-wordPos)+word.length());i++){
			int y = x;
			while(y>0 && game[y][i]!=null && !game[y][i].equals("_")){
				y--;
			}
			int y2 = x;
			while(y2<15 && game[y2][i]!=null && !game[y2][i].equals("_")){
				y2++;
			}
			y++;
			
//			System.out.println(y+" w "+y2+" i "+(i-(boardPos-wordPos)));
			for(int r = y; r<y2; r++){
				
				wordTest += r==x ? word.charAt((i-(boardPos-wordPos))) : game[r][i];
			}
//			System.out.println("\n"+wordTest);
			
		}
		if(wordTest.length()<2){
			return true;
		}
//		System.out.println("\ntest "+wordTest);
		return find.isWord(wordTest.toLowerCase());
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
