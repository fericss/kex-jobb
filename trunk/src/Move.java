public class Move implements Comparable<Move>{//TODO: make comparable
	int x;
	int y;
//	String[] wordlist;
	String word;
	int points;
	boolean vertical;
	public Move(Scrabby scrab, String _word,int _x, int _y,boolean _vertical){
//		wordlist=_wordlist;
		
		word=_word;
		x=_x;
		y=_y;
		vertical=_vertical;
		points=scrab.simplePoints(word, x, y, vertical);
	}
	public Move(int _points, String _word,int _x, int _y,boolean _vertical){
//		wordlist=_wordlist;
		
		word=_word;
		x=_x;
		y=_y;
		vertical=_vertical;
		points=_points;
	}
	
	public String toString(){
		return "{"+this.points+","+word+","+this.x+","+this.y+","+this.vertical+"}";
	}
	
	@Override
	public int compareTo(Move arg0) {
		int points=arg0.points;
		if(this.points==points){
			return 0;
		}
		if(this.points>points){
			return -1;
		} else {
			return 1;
		}
	}
}