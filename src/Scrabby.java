import java.util.ArrayList;
import java.util.HashMap;


public class Scrabby {
	//global
	GameInfo gi;
	WordFinder wf;
	char board[][];
	char emptyChar;
	
	public Scrabby(GameInfo _gi,WordFinder _wf){
		wf=_wf;
		setGameInfo(_gi);
		emptyChar=' ';
	}
	
	public void test(GameInfo _gi,String rack){
		//code for use in main
		//Scrabby sc=new Scrabby(points, wf);
		//sc.test(game,rack,points, wordlist);
		
//		wordlist=_wordlist;
		char[][] board=gameToBoard(gi.getGame());
		int[][] bonus=plainBonus(15,15);
		char[] rack2=rack.toCharArray();
		ArrayList<Move> res=brute();
		
		//print the generated moves/words
		for(int i=0;i<res.size();i++){
			res.get(i).toString();
		}
	}
	
	public GameInfo getGameInfo(){
		return gi;
	}
	
	public void setGameInfo(GameInfo _gi){
		gi=_gi;
		board=gameToBoard(gi.getGame());
	}
	
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
	public ArrayList<Move> brute(){
		ArrayList<Move> res=new ArrayList<Move>();
		String[] wordlist=wf.getWordlist();
		//for each word in wordlist
		for(int i=0;i<wordlist.length;i++){
			String word=wordlist[i];
			//for each position on board
			for(int x=0;x<board.length;x++){
				for(int y=0;y<board[x].length;y++){
					//calculate points for placing word on board at position and in direction
					int points=points(word,x,y,true);
					if(points>0){
						//it was a word so add to result
						res.add(new Move(this,word,x,y,true));
					}
					points=points(word,x,y,false);
					if(points>0){
						//it was a word so add to result
						res.add(new Move(this,word,x,y,false));
					}
				}
			}
		}
		return res;
	}
	
	public char[][] gameToBoard(String[][] game){
		return gameToBoard(game,null,emptyChar);
	}
	
	private char[][] gameToBoard(String[][] game,String emptyLetter, char emptyChar){
		char[][] board=new char[game.length][game[0].length];
		for(int x=0;x<game.length;x++){
			for(int y=0;y<game[0].length;y++){
				String letter=game[x][y];
				if(letter==null || letter.equals(emptyLetter)){
					board[x][y]=emptyChar;
				} else {
					board[x][y]=letter.charAt(0);
				}
			}
		}
		return board;
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
	public int points(//char[][] board, int[][] bonus,char[] rack, 
			String word, int x,int y,boolean vertical){
		char[] rack=gi.getRack().toCharArray();
		int[][] bonus=gi.getBonus();
		
		final int xsize=15;
		final int ysize=15;
		//check length of word
		if(vertical){
			if(y+word.length()>ysize){
				return -1;
			}
		} else {
			if(x+word.length()>xsize){
				return -1;
			}
		}
		
		//Check chars before and after word
		if(vertical){
			//if char before word quit
			if(y>0 && board[x][y-1]!=emptyChar){return -1;}
			//if char after word quit
			if(y+word.length()<ysize-1 && board[x][y+word.length()]!=emptyChar){return -1;}
		} else {
			//if char after word quit
			if(x>0 && board[x-1][y]!=emptyChar){return -1;}
			//if char after word quit
			if(x+word.length()<xsize-1 && board[x+word.length()][y]!=emptyChar){return -1;}
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
		//it has to have added atleast one char and it must contain atleast one char from the board
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
				int tmp=crosspoints(x,y,!vertical);
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
	
//	/**
//	 * Returns the points of a move
//	 * @param move
//	 * @param bonus
//	 * @return
//	 */
//	public int simplePoints(Move move,int[][] bonus){//TODO: implement help methods
//		int points=0;
//		if(move.vertical){
//			for(int i=0;i<move.word.length();i++){
//				points+=pointsAtPoint(bonus,move.x,move.y+i,move.word.charAt(i));
//			}
//		} else {
//			for(int i=0;i<move.word.length();i++){
//				points+=pointsAtPoint(bonus,move.x+i,move.y,move.word.charAt(i));
//			}
//		}
//		return points;
//	}
	
	public int simplePoints(String word,int x,int y,boolean vertical){//TODO: implement help methods
		return simplePoints3(word,x,y,vertical,true);
	}
	
	/**
	 * Not fully tested.
	 * gives points to a word.
	 * 
	 * Has a HAX so that it works with flipped x and y value.
	 * @param word
	 * @param x
	 * @param y
	 * @param vertical
	 * @param recurse
	 * @return
	 */
	public int simplePoints2(String word,int x,int y,boolean vertical,boolean recurse){
		//TODO: remove HAX
		//HAX, swaps x and y, and inverts vertical
		int tmp=x;
		x=y;
		y=tmp;
		vertical=!vertical;
		
		
		//some variables
		char emptyChar=' ';
		int xsize=15;
		int ysize=15;
		
		
		//points accumulated from recursions
		int otherPoints=0;
		
		//the current points
		int currentPoints=0;
		//the total factor that currentPoints will be multiplied by
		int wordFactor=1;
		
		//chose the direction
		if(vertical){
			//for each char
			for(int i=0;i<word.length();i++){
				
				//points for current word
//				try{
				if(board[x][y+i]==emptyChar){//added a character
					
					//only do this for newly created words
					if(recurse){
						//check if there is a word in other direction
						if( (x-1>0 && board[x-1][y+i]!=emptyChar) || 
								(x+1<xsize && board[x+1][y+i]!=emptyChar)){//is there a word in !vertical direction?
							//find start of word
							int x2=x;
							while(x2>0 && board[x2-1][y+i]!=emptyChar){
								x2--;
							}
							//get points for word in other direction
							otherPoints+=simplePoints2(word,x2,y+i,!vertical,false);
						}
					}
					
					
					//updates points for current word, currentPoints+=valueOfChar*letterBonus
					currentPoints+=wf.valueOf(word.charAt(i))*bonusFactor(gi.getBonus()[x][y+i],false);
					//updates factor for current word, wordFactor*=wordBonus
					wordFactor*=bonusFactor(gi.getBonus()[x][y+i],true);
				} else {
					//no letter bonus because it already was on board
					currentPoints+=wf.valueOf(word.charAt(i));
				}
//				} catch(Exception e){
//					
//					//beror troligen på att spelplanen är i andra riktningen
//					//DEBUG
//					System.out.println("min "+word+" "+x+" "+y+" "+i+" "+(y+i)+" "+recurse);
//					//RE-THROW
//					if(board[x][y+i]==emptyChar){//added a character
//						//updates points for current word, currentPoints+=valueOfChar*letterBonus
//						currentPoints+=wf.valueOf(word.charAt(i))*bonusFactor(gi.getBonus()[x][y+i],false);
//						//updates factor for current word, wordFactor*=wordBonus
//						wordFactor*=bonusFactor(gi.getBonus()[x][y+i],true);
//					} else {
//						//no letter bonus because it already was on board
//						currentPoints+=wf.valueOf(word.charAt(i));
//					}
//				}
			}
		} else {
			//a mirror of the direction above
			
			//for each char
			for(int i=0;i<word.length();i++){
				
				
				//points for current word
				if(board[x+i][y]==emptyChar){//added a character
					
					//only do this for newly created words
					if(recurse){
						//check if there is a word in other direction
						if( (y-1>0 && board[x+i][y-1]!=emptyChar) || 
								(y+1<ysize && board[x][y+1]!=emptyChar)){//is there a word in !vertical direction?
							//find start of word
							int y2=y;
							while(y2>0 && board[x+i][y2-1]!=emptyChar){
								y2--;
							}
							//get points for word in other direction
							otherPoints+=simplePoints2(word,x+i,y2,!vertical,false);
						}
					}
					
					//updates points for current word, currentPoints+=valueOfChar*letterBonus
					currentPoints+=wf.valueOf(word.charAt(i))*bonusFactor(gi.getBonus()[x+i][y],false);
					//updates factor for current word, wordFactor*=wordBonus
					wordFactor*=bonusFactor(gi.getBonus()[x+i][y],true);
				} else {
					//no letter bonus because it already was on board
					currentPoints+=wf.valueOf(word.charAt(i));
				}
			}
		}
		//calculate and return total points
		int totalPoints=currentPoints*wordFactor+otherPoints;
		return totalPoints;
	}
	
	
	
	/**
	 * gets a bounsCode and returns the bonus factor or a 1 if the boolean is the opposite
	 * of the bonustype (dl or dw)
	 * @param bonusCode
	 * @param isWord
	 * @return
	 */
	public int bonusFactor(int bonusCode,boolean isWord){
		if(bonusCode<=2){//it's a letter bonus
			return isWord?1:bonusCode+1;
		} else { //it's a word bonus
			return isWord?bonusCode-1:1;
		}
	}
	
	
	/**
	 * Does the same as simplepoints 2 but is shorter
	 * @param word
	 * @param x
	 * @param y
	 * @param vertical
	 * @param recurse
	 * @return
	 */
	public int simplePoints3(String word,int a,int b,boolean vertical,boolean recurse){
		//TODO: remove HAX
		//HAX, swaps x and y, and inverts vertical
		int tmp=a;
		a=b;
		b=tmp;
		vertical=!vertical;
		
		
		//some variables
		char emptyChar=' ';
		boolean swap=vertical;
		int asize=arrSize(board,swap);

		//points accumulated from recursions
		int otherPoints=0;
		
		//the current points
		int currentPoints=0;
		//the total factor that currentPoints will be multiplied by
		int wordFactor=1;

		//for each char
		for(int i=0;i<word.length();i++){

			//points for current word
			if(arrGet(board,a,b,swap)==emptyChar){//added a character

				//only do this for newly created words
				if(recurse){
					//check if there is a word in other direction
					if( (a-1>0 && arrGet(board,a-1,b,swap)!=emptyChar) || 
							(a+1<asize && arrGet(board,a+1,b,swap)!=emptyChar)){//is there a word in !vertical direction?
						//find start of word
						int a2=a;
						while(a2>0 && arrGet(board,a2-1,b,swap)!=emptyChar){
							a2--;
						}
						//get points for word in other direction
						otherPoints+=simplePoints2(word,a2,b,!vertical,false);
					}
				}
				
				//gets the bonus code
				int bonusCode=arrGet(gi.getBonus(),a,b,swap);
				
				//updates points for current word, currentPoints+=valueOfChar*letterBonus
				currentPoints+=wf.valueOf(word.charAt(i))*bonusFactor(bonusCode,false);
				
				//updates factor for current word, wordFactor*=wordBonus
				wordFactor*=bonusFactor(bonusCode,true);
			} else {
				//no letter bonus because it already was on board
				currentPoints+=wf.valueOf(word.charAt(i));
			}
			b++;
		}
		
		//calculate and return total points
		int totalPoints=currentPoints*wordFactor+otherPoints;
		return totalPoints;
	}
	
	/**
	 * used so that it's easy to swap x and y
	 * the same as arr[x][y] but it's easier to swap x and y
	 * @return
	 */
	public static char arrGet(final char[][] arr,final int x,final int y,final boolean swap){
		if(swap){
			return arr[y][x];
		} else {
			return arr[x][y];
		}
	}
	/**
	 * used so that it's easy to swap x and y
	 * the same as arr[x][y] but it's easier to swap x and y
	 * @return
	 */
	public static int arrGet(final int[][] arr,final int x,final int y,final boolean swap){
		if(swap){
			return arr[y][x];
		} else {
			return arr[x][y];
		}
	}
	/**
	 * used so that it's easy to swap x and y
	 * the same as arr[x][y] but it's easier to swap x and y
	 * @return
	 */
	public static void arrSet(final char[][] arr,final int x,final int y,final boolean swap,final char value){
		if(swap){
			arr[y][x]=value;
		} else {
			arr[x][y]=value;
		}
	}
	/**
	 * used so that it's easy to swap x and y
	 * the same as arr[x][y] but it's easier to swap x and y
	 * @return
	 */
	public int arrSize(final char[][] arr,final boolean swap){
		if(swap){
			return arr[0].length;
		} else {
			return arr.length;
		}
	}
	
	
	
	
	
	
	public int pointsAtPoint(int[][] bonus,int x, int y, char ch){
		return wf.valueOf(ch)*bonus[x][y];
	}
	
	public int[][] plainBonus(int xl, int yl){
		int[][] arr=new int[xl][yl];
		for(int x=0;x<xl;x++){
			for(int y=0;y<yl;y++){
				arr[x][y]=1;
			}
		}
		return arr;
	}	
	
	
	/**
	 * does not work, use simplepoints2 as inspiration instead
	 * 
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
	public int crosspoints(//char[][] board,int[][] bonus,
			int x, int y, boolean vertical){
		
		final int xsize=15;
		final int ysize=15;
		
		StringBuilder sb=new StringBuilder();
		int points=0;
		if(vertical){
			while(y>0 && board[x][y-1]!=emptyChar){
				y--;
			} 
			while(y<ysize && board[x][y]!=emptyChar){
				sb.append(board[x][y]);
				points+=pointsAtPoint(gi.getBonus(),x,y,board[x][y]);
				y++;
			}
		} else {
			while(x>0 && board[x-1][y]!=emptyChar){
				x--;
			} 
			while(x<xsize && board[x][y]!=emptyChar){
				sb.append(board[x][y]);
				points+=pointsAtPoint(gi.getBonus(),x,y,board[x][y]);
				x--;
			}
		}
		if(sb.length()==1){
			return -1;
		}
		
		if(!wf.isWord(sb.toString())){
			return -1;
		}
		return points;
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

