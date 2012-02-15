import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;


public class FredricTestStuff {

	private String[][] game;
	ArrayList<Point> buildLocations;
	WordFinder find;
//	private String[][] m_words;
	public FredricTestStuff(String[][] _game, ArrayList<Point> _buildLocations){
//		wordList = _wordList;
//		m_words = _m_words;
		game = _game;
		find = new WordFinder();
		buildLocations = _buildLocations;
		ArrayList<String> wordThatCanBeBuildLol= new ArrayList<String>();
		for(Point p: buildLocations){
			wordThatCanBeBuildLol.addAll(getWordThatCanBeBuilt(p));
		}
		
	}
	private Collection<String> getWordThatCanBeBuilt(Point p) {
		int x = p.x;
		int y = p.y;
		
		return null;
	}
}
