import java.util.ArrayList;


public class Scrabby {
	public int[] charvalues;

	/**
	 * might be changed to take a filtered wordlist for each row and each column.
	 * @param board
	 * @param bonus
	 * @param charvalues
	 * @param rack
	 * @param wordlist
	 * @param emptyChar
	 * @return
	 */
	public ArrayList<Move> brute(char[][] board, int[][] bonus,int[] charvalues,char[] rack,String[] wordlist,char emptyChar){
		ArrayList<Move> res=new ArrayList<Move>();
		//for each word in wordlist
		for(int i=0;i<wordlist.length;i++){
			String word=wordlist[i];
			//for each position on board
			for(int x=0;x<board.length;x++){
				for(int y=0;y<board[x].length;y++){
					//calculate points for placing word on board at position and in direction
					int points=points(board,bonus,charvalues,rack,word,x,y,true, emptyChar);
					if(points>0){
						//it was a word so add to result
						res.add(new Move(wordlist,points,i,x,y,true));
					}
					points=points(board,bonus,charvalues,rack,word,x,y,false,emptyChar);
					if(points>0){
						//it was a word so add to result
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
	 * @param y
	 * @param x
	 * @param vertical
	 * @param emptyChar
	 * @return
	 */
	public int points(char[][] board, int[][] bonus,int[] charvalues,char[] rack, 
			String word, int x,int y,boolean vertical,final char emptyChar){
		
		//Check chars before and after word
		if(vertical){
			//if char before word quit
			if(y>0 && board[x][y-1]!=emptyChar){return -1;}
			//if char after word quit
			if(y+word.length()<board[x].length-1 && board[x][y+word.length()]!=emptyChar){return -1;}
		} else {
			//if char after word quit
			if(x>0 && board[x-1][y]!=emptyChar){return -1;}
			//if char after word quit
			if(y+word.length()<board[x].length-1 && board[x][y+word.length()]!=emptyChar){return -1;}
		}
		
		//the number of points
		int points=0;
		
		//check word in given direction (horizontal or not horizontal), and calculate points
		//check correct char at position and that the char is in the rack
		int usedChars=0;
		int setChars=0;
		char[] neededChars=new char[7]; //used chars, TODO: might be returned?
		boolean[] taken=new boolean[rack.length]; //wich of the char in the rack are taken
		int y2=y;
		int x2=x;
		for(int wi=0;wi<word.length();wi++){
			char boardCh=board[x2][y2];//retrieve board char
			char wordCh=word.charAt(wi);//retrieve word char
			if(boardCh!=emptyChar){
				//it's a nonempty position =>
				if(boardCh!=wordCh){
					return -1;
				} else {
					setChars++;
				}
			} else {
				boolean foundChar=false;
				for(int ri=0;ri<taken.length;ri++){
					if(!taken[ri]){
						if(rack[ri]==wordCh){
							//take char from rack
							foundChar=true;
							taken[ri]=true;
							neededChars[usedChars]=wordCh;
							usedChars++;
							break;
						}
					}
				}
				if(!foundChar){
					return -1;
				} 
			}
			//a char was "placed" calculate the points for it
			points+=pointsAtPoint(bonus,x2,y2,wordCh);
			
			if(vertical){
				y2++;//go to next letter in word
			} else {
				x2++;//go to next letter in word
			}
		}
		//it has to add atleast one char and it must contain atleast one char from the board
		if(setChars==0 || usedChars==0){
			return -1;
		}
		
		//check words in other direction
		for(int i=0;i<word.length();i++){
			boolean should=false;
			//check if letter above or below
			if(vertical){
				if(board[x+1][y]!=emptyChar || board[x-1][y]!=emptyChar){
					should=true;
				}
			} else {
				if(board[x][y+1]!=emptyChar || board[x][y-1]!=emptyChar){
					should=true;
				}
			}
			//if letter above or below check if word in that direction
			if(should){
				int tmp=crosspoints(board,bonus,charvalues,x,y,!vertical,emptyChar);
				if(tmp>0){
					points=points+tmp;
				} else {
					return -1;
				}
			}
			//increment position in direction
			if(vertical){
				//go to next letter in word
				y++;
			} else {
				//go to next letter in word
				x++;
			}
		}
		return points;
	}
	
	public int pointsAtPoint(int[][] bonus,int x, int y, char ch){
		return value(ch)*bonus[x][y];
	}
	
	/**
	 * 
	 * @param ch
	 * @return
	 */
	public int value(char ch){//TODO: implement
		return -1;
	}
	
	
	/**
	 * checks for a word in the indicated direction
	 * returns -1 if the resulting word has length -1
	 * TODO: must add a contains check
	 * @param board
	 * @param y
	 * @param x
	 * @param vertical
	 * @param emptyChar
	 * @return
	 */
	public int crosspoints(char[][] board,int[][] bonus,int[] charvalues,
			int x, int y, boolean vertical,final char emptyChar){
		
		StringBuilder sb=new StringBuilder();
		int points=0;
		if(vertical){
			while(y>0 && board[x][y-1]!=emptyChar){
				y--;
			} 
			while(y<board[x].length && board[x][y]!=emptyChar){
				sb.append(board[x][y]);
				points+=pointsAtPoint(bonus,x,y,board[x][y]);
				y++;
			}
		} else {
			while(x>0 && board[x-1][y]!=emptyChar){
				x--;
			} 
			while(x<board.length && board[x][y]!=emptyChar){
				sb.append(board[x][y]);
				points+=pointsAtPoint(bonus,x,y,board[x][y]);
				x--;
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
		
		
	}
	/**
	 * place holder, use fredric's method instead
	 * @param ch
	 * @return
	 */
	public static int indexOfChar(char ch){
		return -1;
	}
    
}

