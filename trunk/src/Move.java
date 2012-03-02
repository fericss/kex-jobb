public class Move{//TODO: make comparable
	int x;
	int y;
//	String[] wordlist;
	String word;
	int points;
	boolean vertical;
	public Move(int _points,String _word,int _x, int _y,boolean _vertical){
//		wordlist=_wordlist;
		points=_points;
		word=_word;
		x=_x;
		y=_y;
		vertical=_vertical;
	}
	public String toString(){
		return "{"+this.points+","+word+","+this.x+","+this.y+","+this.vertical+"}";
	}
}