import java.util.ArrayList;


public class Scrabby {
	
	public ArrayList<Move> brute(char[][] board, int[][] bonus,int[] charvalues,char[] rack,String[] wordlist,char emptyChar){
		ArrayList<Move> res=new ArrayList<Move>();
		for(int i=0;i<wordlist.length;i++){
			String word=wordlist[i];
			for(int y=0;y<board.length;y++){
				for(int x=0;x<board[y].length;x++){
					int points=points(board,bonus,charvalues,rack,word,x,y,true, emptyChar);
					if(points>0){
						//was a word so add to result
						res.add(new Move(wordlist,points,i,x,y,true));
					}
					points=points(board,bonus,charvalues,rack,word,x,y,false,emptyChar);
					if(points>0){
						//was a word so add to result
						res.add(new Move(wordlist,points,i,x,y,false));
					}
				}
			}
		}
		return res;
	}
	
	class Move{//TODO: make comparable
		int x;
		int y;
		String[] wordlist;
		int word;
		int points;
		boolean horizontal;
		public Move(String[] _wordlist,int _points,int _word,int _x, int _y,boolean _horizontal){
			wordlist=_wordlist;
			points=_points;
			word=_word;
			x=_x;
			y=_y;
			horizontal=_horizontal;
		}
		public String toString(){
			return "{"+this.points+","+this.wordlist[word]+","+this.x+","+this.y+","+this.horizontal+"}";
			
		}
		
	}
	
	/**
	 * calculates the number of points for the word
	 * -1 if the word can't be there
	 * 
	 * 
	 * maybe should return move instead, but that can be recalculated later
	 * @param board
	 * @param bonus
	 * @param charvalues
	 * @param rack
	 * @param word
	 * @param x
	 * @param y
	 * @param horizontal
	 * @param emptyChar
	 * @return
	 */
	public int points(char[][] board, int[][] bonus,int[] charvalues,char[] rack, String word, int x,int y,boolean horizontal,final char emptyChar){
		
		//Check chars before and after word
		if(horizontal){
			//if char before word quit
			if(x>0 && board[y][x-1]!=emptyChar){return -1;}
			//if char after word quit
			if(x+word.length()<board[y].length-1 && board[y][x+word.length()]!=emptyChar){return -1;}
		} else {
			//if char after word quit
			if(y+word.length()<board.length-1 && board[y+word.length()][x]!=emptyChar){return -1;}
			//if char after word quit
			if(x+word.length()<board[y].length-1 && board[y][x+word.length()]!=emptyChar){return -1;}
		}
		
		int points=0;
		
		//check word in indicated direction, and calculate points
		//quick check, take chars from rack, test if correct char at position, calculate points
		int emptyChars=0;
		int filledChars=0;
		char[] neededChars=new char[7]; //has to be in the rack, empty chars to know length
		boolean[] taken=new boolean[rack.length];
		int index=0;
		int x2=x;
		int y2=y;
		for(int i=0;i<word.length();i++){
			char tmp=board[y][x];
			if(tmp!=emptyChar){
				if(tmp==word.charAt(i)){
					points+=charvalues[board[y][x]]*bonus[y][x];
				} else {
					return -1;
				}
				filledChars++;
			} else {
				boolean foundChar=false;
				for(int j=0;j<taken.length;j++){
					if(!taken[j]){
						if(rack[j]==tmp){
							//take char from rack
							foundChar=true;
							taken[j]=true;
							break;
						}
					}
				}
				if(!foundChar){
					return -1;
				} 
				emptyChars++;
			}
			if(horizontal){
				x++;//go to next letter in word
			} else {
				y++;//go to next letter in word
			}
		}
		//it has to add atleast one char and it must contain atleast one char from the board
		if(filledChars==0 || emptyChars==0){
			return -1;
		}
		
		//check words in other direction
		for(int i=0;i<word.length();i++){
			boolean should=false;
			//check if letter above or below
			if(horizontal){
				if(board[y+1][x]!=emptyChar || board[y-1][x]!=emptyChar){
					should=true;
				}
			} else {
				if(board[y][x+1]!=emptyChar || board[y][x-1]!=emptyChar){
					should=true;
				}
			}
			//if letter above or below check if word in that direction
			if(should){
				int tmp=crosspoints(board,bonus,charvalues,x,y,!horizontal,emptyChar);
				if(tmp>0){
					points=points+tmp;
				} else {
					return -1;
				}
			}
			if(horizontal){
				//go to next letter in word
				x++;
			} else {
				//go to next letter in word
				y++;
			}
		}
		return points;
	}
	
	/**
	 * checks for a word in the indicated direction
	 * returns -1 if the resulting word has length -1
	 * TODO: must add a contains check
	 * @param board
	 * @param x
	 * @param y
	 * @param horisontal
	 * @param emptyChar
	 * @return
	 */
	public int crosspoints(char[][] board,int[][] bonus,int[] charvalues,int x, int y, boolean horisontal,final char emptyChar){
		StringBuilder sb=new StringBuilder();
		int points=0;
		if(horisontal){
			while(x>0 && board[y][x-1]!=emptyChar){
				x--;
			} 
			while(x<board[y].length && board[y][x]!=emptyChar){
				sb.append(board[y][x]);
				x++;
				//add the points
				points+=charvalues[board[y][x]]*bonus[y][x];
			}
		} else {
			while(y>0 && board[y-1][x]!=emptyChar){
				y--;
			} 
			while(y<board.length && board[y][x]!=emptyChar){
				sb.append(board[y][x]);
				y--;
				//add the points
				points+=charvalues[board[y][x]]*bonus[y][x];
			}
		}
		if(sb.length()==1){
			return -1;
		}
		// TODO: implement contains method
		//check contains
//		if(!wordlist.contains(sb.toString())){
//			return -1;
//		}
		return points;
	}
	
	
	/**
	 * experimental class used for checking if the word contains more characters
	 * of different types than is contained in the rack and the row.
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
		
		/**
		 * returns false if the word contains more characters of 
		 * a type than the row and rack put together
		 * @param rowAndRack
		 * @return
		 */
		public boolean freqTest(final byte [] rowAndRack){
			for(int i=0;i<freq.length;i++){
				if(freq[i]>rowAndRack[i]){
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
		public byte[] createFreq(final String s){
			byte[] freq=new byte[28];//size of alphabet
			for(int i=0;i<s.length();i++){
				freq[indexOfChar(s.charAt(i))]++;
			}
			return freq;
		}
		
		/**
		 * place holder, use fredric's method instead
		 * @param ch
		 * @return
		 */
		public int indexOfChar(char ch){
			return -1;
		}
	}
    
}

