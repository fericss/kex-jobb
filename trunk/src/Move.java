public class Move implements Comparable{//TODO: make comparable
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
	public String toString(){
		return "{"+this.points+","+word+","+this.x+","+this.y+","+this.vertical+"}";
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return this.points-((Move)arg0).points;
	}
}