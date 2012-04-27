import java.util.Arrays;
import java.util.Random;

/**
 * experimental
 * 
 * Used for testing faster methods.
 * 
 * uses a char[][] array to save the game board, and many of the methods use char arrays.
 * Instead of writing the code twice an internal transposed GameInfo2 object is created
 * so that the same code can be used in the vertical direction.
 * 
 * This transposed game info object can be returned and used so that there is no need
 * to check the direction when using the game info object, but instead just use the transposed
 * version and run for every row with that.
 * 
 * @author mbernt
 *
 */
public class GameInfo2 {
	int bonus[][];
	char board[][];
	private boolean blanks[][];
	private String rack;
	final static int length=15;
	
	
	
	
	
	//TEST
	//contains total counts of each tile type
	final static int[] bag={10,2,2,5,12,2,3,3,9,1,1,4,2,6,7,2,1,6,5,7,4,2,2,1,2,1,2};
	final static char[] alphabet={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','.'};
	int[] leftInBag=null;
	
	Move move=null;
	GameInfo2 oldGameInfo=null;
	//contains the game info for rows in other direction
	GameInfo2 twin;
	
	
	int[][] changedPositionsHorizontal=null;
	String[] horizontalRows=null;
//	String[][][] FastCrossers=null;
	String[][][] horizontalFastCrossers=null;
	int[][] horizontalPoints=null;
	int[][] horizontalCrossPoints=null;
	
	final boolean isTransposed;
	
//	public GameInfo2(char _board[][], int _bonus[][],boolean _blanks[][],String _rack){
//		rack=_rack;
//		isTransposed=false;
//		bonus=_bonus;
//		blanks=_blanks;
//		board=_board;
//		twin=new GameInfo2(board, bonus,blanks,rack,this);
//	}
	
	/**
	 * should be used with _twin=null, and then the transposed version is constructed automatically.
	 * 
	 * board must be in lowercase!
	 * @param _board
	 * @param _bonus
	 * @param _blanks
	 * @param _rack
	 * @param _twin
	 */
	public GameInfo2(char _board[][], int _bonus[][],boolean _blanks[][],String _rack, GameInfo2 _twin){
		rack=_rack;
		
		if(_twin!=null){
			twin=_twin;
			isTransposed=!twin.isTransposed;
			if(isTransposed){
				bonus=new int[length][length];
				blanks=new boolean[length][length];
				board=new char[length][length];
				//transpose
				for(int i=0;i<length;i++){
					for(int j=0;j<length;j++){
						bonus[i][j]=_bonus[j][i];
						blanks[i][j]=_blanks[j][i];
						board[i][j]=_board[j][i];
					}
				}
			} else {
				bonus=_bonus;
				blanks=_blanks;
				board=_board;
			}
		} else {
			isTransposed=false;
			bonus=_bonus;
			blanks=_blanks;
			board=_board;
			twin=new GameInfo2(board, bonus,blanks,rack,this);
		}
	}
	
	
	
	/**
	 * the two input strings have to be lowercase or .
	 * @param knownInOldRack
	 * @param knownInThisRack
	 * @return
	 */
	public int[] getLefInBagCopy(String knownInOldRack,String knownInThisRack){
		if(leftInBag==null){
			leftInBag=Arrays.copyOf(bag, bag.length);
			//remove from count in bag all letters that are left in the old rack
			for(int i=0;i<knownInOldRack.length();i++){
				leftInBag[letterToIndex(knownInOldRack.charAt(i))]--;
			}
			//remove from count in bag those letters that already are in this rack
			for(int i=0;i<knownInThisRack.length();i++){
				leftInBag[letterToIndex(knownInThisRack.charAt(i))]--;
			}
			//remove from count in bag
			char[][] board=getBoard();
			for(int i=0;i<length;i++){
				for(int j=0;j<length;j++){
					if(board[i][j]!=' '){
						if(blanks[i][j]){
							leftInBag[letterToIndex('.')]--;
						} else {
							leftInBag[letterToIndex(board[i][j])]--;
						}
					}
				}
			}
		}
		return Arrays.copyOf(leftInBag, leftInBag.length);
	}
	
	
	public void getRandomRack(String alredyInRack,String inOldRack,Random rand){
		//get a copy of tiles left in bag
		int[] tiles=getLefInBagCopy(inOldRack,alredyInRack);
		StringBuilder sb=new StringBuilder(7);
		sb.append(alredyInRack);
		int nrTiles=0;
		for(int i=0;i<tiles.length;i++){
			nrTiles+=tiles[i];
		}
		while(sb.length()<7){
			int index=getRandomTile(tiles,nrTiles,rand);
			sb.append(indexToLetter(index));
			tiles[index]--;
			nrTiles--;
		}
	}
	
	/**
	 * only returns index of random tile
	 * doesen't take the tile
	 * blank tile should be at the last index
	 * throws index out of bounds exception if nrTiles is to high
	 * @param source
	 * @param nrTiles The total number of tiles in bag
	 * @param rand
	 * @return
	 */
	public int getRandomTile(final int[] source,final int nrTiles,final Random rand){
		if(nrTiles<=0){ return -1; }
		int tmp=1+rand.nextInt(nrTiles);
		int index=0;
		while(true){
			tmp-=source[index];
			if(tmp<=0){
				return index;
			}
			index++;
		}
	}
	
	public int letterToIndex(final char letter){
		return letter=='.'?bag.length-1:letter-'a';
	}
	
	public char indexToLetter(final int index){
		return alphabet[index];
	}
	
	public GameInfo2 getTransposedGameInfo2(){
		return twin;
	}
	
	
	
	
	public void printBoard(){
		printBoard(this.getBoard());
	}
	
	public static void printBoard(final char[][] board){
		int x=0;
		System.out.println(" 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14");
		for(char[] b:board){
			System.out.println(Arrays.toString(b)+" "+(x));
			x++;
		}
	}
	
	public char[][] getBoard(){
		return board;
	}
	
	public void setBoard(final char[][] _board){
		board=_board;
	}
	
	/**
	 * can be used 
	 * @param old
	 */
	public void setOldGameInfo(GameInfo2 old){
		oldGameInfo=old;
	}
	
	public GameInfo2 getOldGameInfo(){
		return oldGameInfo;
	}
	

	public int[][] getBonus() {
		return bonus;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public String getRack() {
		return rack;
	}
	
	public void setBlanks(boolean[][] wildcards){
		this.blanks=wildcards;
	}
	public boolean[][] getBlanks(){
		return blanks;
	}
	
	/**
	 * Unimplemented! 
	 * returns a freq array with the number of each unknown chartype
	 * Depends on what's on the board, in the players rack and the total
	 * amount of each tile.
	 * except unknown wildcard
	 * @return
	 */
	public byte[] getUnknownFreq(){
		return new byte['z'-'a'+1];
	}
	
	/**
	 * Unimplemented! 
	 * returns the number of unknown wildcards
	 * @return
	 */
	public int getUnknownWildCards(){
		return 0;
	}
	
	
	
	protected char[] getRow(final int row){
		return board[row];
	}
	
	
	protected char[][] getHorizontalRowsHelp(){
		return board;
	}
	
	protected static int[][] getHasChangedHorizontalHelp(char[][] board,char[][] game2){
		final int length=15;
		int[][] hasChanged=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				if(board[i][j]!=game2[i][j]){//game[i][j]!=' '
					int x=i,y=j;
					
					//set changed on the change
					hasChanged[x][y]=2;
					
					//in each row and column direction
					//set changed on the first empty position
					
					for(x=i+1,y=j;x<length;x++){
						if(game2[x][y]==' '){
							hasChanged[x][y]=1;
							break;
						}
					}
					for(x=i-1,y=j;x>=0;x--){
						if(game2[x][y]==' '){
							hasChanged[x][y]=1;
							break;
						}
					}
					for(x=i,y=j+1;y<length;y++){
						if(game2[x][y]==' '){
							hasChanged[x][y]=1;
							break;
						}
					}
					for(x=i,y=j-1;y>=0;y--){
						if(game2[x][y]==' '){
							hasChanged[x][y]=1;
							break;
						}
					}
				}
			}
		}
		return hasChanged;
	}
	
	protected int[][] getHasChangedHorizontalHelp(){
		if(this.oldGameInfo==null){return null;}
		if(changedPositionsHorizontal==null){//load value if
			changedPositionsHorizontal=getHasChangedHorizontalHelp(this.oldGameInfo.getBoard(),this.getBoard());
		}
		return changedPositionsHorizontal;
	}
	
	/**
	 * 0=no change, 1=affected by change, 2=on top of change
	 * @param rowIndex
	 * @param vertical
	 * @return
	 */
	public int[] getChanged(final int rowIndex){
		int[][] tmp=getHasChangedHorizontalHelp();
		return tmp==null?null:tmp[rowIndex];
	}
	
	
	//TEST
	/**
	 * TODO
	 * update version here: almost finished
	 * update version of Help methods
	 * update version of filter methods
	 */
	
	
	
	
	@Override
	public GameInfo2 clone(){
		//needs cloning
		/*private String game[][];
		private boolean wildCards[][];
		private String rack;*/
		
		char[][] boardCopy=new char[length][length];
		boolean[][] blanksCopy=new boolean[length][length];
		
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				boardCopy[i][j]=board[i][j];
				blanksCopy[i][j]=blanks[i][j];
			}
		}
		return new GameInfo2(boardCopy, bonus, blanksCopy, rack,this);
	}
	
	
	
	/**
	 * Clones the current object and then add the word to the cloned object
	 * and the newRack. Useful when you want to calculate counter moves.
	 * 
	 * Maybe should have new rack as input isntead if Move has the used letters.
	 * @param newRack
	 * @param m
	 * @return
	 */
	public GameInfo2 newGameInfo(final String newRack,final Move m){
		//print stuff
//		System.out.println("Contructing GameInfo from old GameInfo and a move...");
//		System.out.println("Move: "+m);
		
		//do stuff
		GameInfo2 gi=clone();
		gi.oldGameInfo=this;
		if(m!=null){
			boolean vertical=!m.vertical;
			gi.move=m;
			String word=m.word;
			int x=m.y;//Hax, swaps x and y
			int y=m.x;
			if(vertical){
				for(int i=0;i<word.length();i++,y++){
					gi.getBoard()[x][y]=word.charAt(i);
					gi.getBlanks()[x][y]=m.isWildCard(i);
				}
			}else{
				for(int i=0;i<word.length();i++,x++){
					gi.getBoard()[x][y]=word.charAt(i);
					gi.getBlanks()[x][y]=m.isWildCard(i);
				}
			}
		}
		
		gi.setRack(newRack);
		
		int[][] hasChanged=gi.getHasChangedHorizontalHelp();
		gi.changedPositionsHorizontal=hasChanged;
		
		//Print stuff
//		
		System.out.println("Move: "+m);
		System.out.println("The new board in GameInfo:");
		gi.printBoard();
		//return stuff
		return gi;
	}
	
	
	

	
	/**
	 * get crossers but in the wrong direction.
	 * getFastCrossers gives the fastCrossers in the correct
	 * direction.
	 * 
	 * @param row
	 * @return
	 */
	protected static String[][] getFastCrossersHelp(final char[] row){
		final String empty="";
		String[][] res=new String[length][];
		StringBuilder sb=new StringBuilder();
		boolean added=true;
		int beforeWord=-1;
		for(int i=0;i<length;i++){
			if(row[i]==' '){
				//if has not added word, add it
				if(!added){
					//get the string
					String tmp=sb.toString();
					//construct array it's not already there
					if(beforeWord>=0 && res[beforeWord]==null){
						res[beforeWord]=new String[2];
						//fill in default values
						res[beforeWord][0]=empty;
						res[beforeWord][1]=empty;
					}
					//construct array if it's not already there
					if(res[i]==null){
						res[i]=new String[2];
						//fill in default values
						res[i][0]=empty;
						res[i][1]=empty;
					}
					//replace default value if there was a space before the word
					if(beforeWord>=0){
						res[beforeWord][1]=tmp;
					}
					//replace the default after the word
					res[i][0]=tmp;
					//reset some variables
					sb.setLength(0);//reset string builder
					added=true;
				}
				//keep adjusting until finds word
				beforeWord=i;
			} else {
				//construct the found word
				sb.append(row[i]);
				added=false;
			}
		}
		//if there is a letter at the last position then the last crosser may not have been added
		if(row[row.length-1]!=' '){
			//construct array it's not already there
			if(beforeWord>=0 && res[beforeWord]==null){
				res[beforeWord]=new String[2];
				//fill in default values
				res[beforeWord][0]=empty;
				res[beforeWord][1]=empty;
			}
			//replace default value if there was a space before the word
			if(beforeWord>=0){
				res[beforeWord][1]=sb.toString();
			}
		}
		return res;
	}
	
	/**
	 * uses crossing rows to get crossers in other direction
	 * e.g if it's vertical rows then it gets all fastCrossers 
	 * for all horizontal rows.
	 * @param transposedBoard
	 * @return
	 */
	protected static String[][][] getFastCrossersHelp(final char[][] transposedBoard){
		String[][][] res=new String[length][][];
		for(int i=0;i<length;i++){
			res[i]=getFastCrossersHelp(transposedBoard[i]);
		}
		String[][][] res2=new String[length][length][];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				res2[i][j]=res[j][i];
			}
		}
		return res2;
	}
	
	protected String[][][] getHorizontalFastCrossersHelp(){
		if(horizontalFastCrossers==null){
			horizontalFastCrossers=getFastCrossersHelp(this.getTransposedGameInfo2().getBoard());
		}
		return horizontalFastCrossers;
	}
	
	public String[][] getFastCrossers(final int rowIndex){
		return getHorizontalFastCrossersHelp()[rowIndex];
	}
	
	/**
	 * these crossing words must be included when making a name
	 * @param m
	 * @return
	 */
	public String[] getCrossingWordsList(Move m){
		boolean vertical=m.vertical;
		final int rowIndex;
		final int start;
		if(vertical){
			rowIndex=m.x;
			start=m.y;
		} else {
			rowIndex=m.y;
			start=m.x;
		}
		String[][] fastCrossersOnRow=getFastCrossers(rowIndex);
		
		String word=m.word;
		int size=0;
		for(int i=0,pos=start;i<word.length();i++,pos++){
			if(fastCrossersOnRow[pos]!=null){
				size++;
			}
		}
		String[] res=new String[size];
		for(int i=0,pos=start,index=0;i<word.length();i++,pos++){
			if(fastCrossersOnRow[pos]!=null){
				res[index]=fastCrossersOnRow[pos][0]+word.charAt(i)+fastCrossersOnRow[pos][1];
				index++;
			}
		}
		return res;
	}
	
	public int[][] getPointsHorizontal(){
		if(horizontalPoints==null){
			horizontalPoints=new int[length][length];
			char[][] board=this.getBoard();
			boolean[][] blanks=this.getBlanks();
			for(int i=0;i<length;i++){
				for(int j=0;j<length;j++){
					char tile=board[i][j];
					if(tile!=' ' && !blanks[i][j]){
						horizontalPoints[i][j]=WordFinder.valueOf(tile);
					} //else {
					//	points[i][j]=0;
					//}
				}
			}
		}
		return horizontalPoints;
	}
	
	protected int[][] crossPointsHorizontalHelp(){
		if(horizontalCrossPoints==null){
			horizontalCrossPoints=crossPointsHelp(twin.getPointsHorizontal(),twin.getBoard());
		}
		return horizontalCrossPoints;
	}
	
	public int[] getCrossPoints(final int rowIndex){
		return crossPointsHorizontalHelp()[rowIndex];
	}
	
	/**
	 * example: if it's horizontal rows then it returns crosspoints for all vertical rows.
	 * or vice versa.
	 * or vice versa.
	 * @param points
	 * @param board
	 * @return
	 */
	protected static int[][] crossPointsHelp(int[][] points,char[][] board){
		int[][] res=new int[length][length];
		for(int i=0;i<length;i++){
			res[i]=crossPointsHelp(points[i],board[i]);
		}
		int[][] res2=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				res2[i][j]=res[j][i];
			}
		}
		return res2;
	}
	
	/**
	 * works in a similar way to protected static String[][] getFastCrossersHelp(final String row).
	 * Gives cross points in the wrong direction, so the results has to be transposed.
	 * @param rowPoints
	 * @param row
	 * @return
	 */
	protected static int[] crossPointsHelp(final int[] rowPoints,final char[] row){
		int[] res=new int[length];
		int points=0;
		boolean added=true;
		int before=-1;
		for(int i=0;i<length;i++){
			if(row[i]==' '){
				if(!added){//add points if not added
					if(before>=0){
						res[before]+=points;
					}
					res[i]+=points;
					//added points
					added=true;
					points=0;
				}
				//update before
				before=i;
			} else{
				//start counting points
				points+=rowPoints[i];
				added=false;
			}
		}
		if(row[length-1]!=' '){
			if(before>=0){
				res[before]+=points;
			}
		}
		return res;
	}
	
	/**
	 * untested
	 * @param rowPoints
	 * @param row
	 * @return
	 */
	protected static int[] crossPointsHelp2(final String row){
		final int[] rowPoints=new int[length];
		final boolean[] isEmpty=new boolean[row.length()];
		for(int i=0;i<length;i++){
			char tmp=row.charAt(i);
			rowPoints[i]=WordFinder.valueOf(tmp);
			isEmpty[i]= tmp==' ';
		}
		
		int[] res=new int[length];
		int points=0;
		for(int i=0;i<length;i++){
			if(isEmpty[i]){
				res[i]+=points;
				points=0;
			} else {
				points+=rowPoints[i];
			}
		}
		points=0;
		for(int i=length-1;i>=0;i--){
			if(isEmpty[i]){
				res[i]+=points;
				points=0;
			} else {
				points+=rowPoints[i];
			}
		}
		return res;
	}
	
	/**
	 * does not save result
	 * @param rowIndex
	 * @param vertical
	 * @return
	 */
	public int[] getBonus(final int rowIndex, boolean vertical){
		vertical=!vertical;//kanske är fel...
		if(vertical){
			int[][] tmp=new int[length][length];
			for(int i=0;i<length;i++){
				for(int j=0;j<length;j++){
					tmp[i][j]=getBonus()[j][i];
				}
			}
			return tmp[rowIndex];
		} else {
			return this.getBonus()[rowIndex];
		}
	}
	
	/**
	 * does not save result
	 * @param rowIndex
	 * @param vertical
	 * @return
	 */
	public boolean[] getWildCards(final int rowIndex, boolean vertical){
		vertical=!vertical;//kanske är fel...
		if(vertical){
			boolean[][] tmp=new boolean[length][length];
			for(int i=0;i<length;i++){
				for(int j=0;j<length;j++){
					tmp[i][j]=this.getBlanks()[j][i];
				}
			}
			return tmp[rowIndex];
		} else {
			return this.getBlanks()[rowIndex];
		}
	}
	
	public int points(String word,int x, int y,boolean vertical, boolean[] blanksInWord){
		int pos;
		int rowIndex;
		if(vertical){
			pos=y;
			rowIndex=x;
		} else {
			pos=x;
			rowIndex=y;
		}
		return points(word,blanksInWord,pos,rowIndex);
	}
	
	/**
	 * just for testing, doesen't take into account the blank tiles used from the rack.
	 * @param m
	 * @return
	 */
	public int testPoints(Move m){
		return points(m.word,m.x,m.y,m.vertical,new boolean[m.word.length()]);
		
	}
	
	public int points(String word, boolean[] isBlankInWord,int pos,int rowIndex){
		return points(word, isBlankInWord, pos, board[rowIndex] , getCrossPoints(rowIndex), 
				bonus[rowIndex], this, rack.length(), blanks[rowIndex], getFastCrossers(rowIndex));
	}
	
	/**
	 * Almost always the same as the standard points calculation. 
	 * Is more correct when there are blank tiles on the board.'
	 * Has not been tested with blank tiles in the rack.
	 * @param word
	 * @param isBlankInWord 
	 * @param pos
	 * @param row
	 * @param fastCrossPoints have pre-calculated the points given from all the letters from the row-crossing
	 * words, so that the only thing that needs calculating is bonuses and the tile to be added. There should be a negative
	 * number at all positions that don't have crossing words.
	 * @param bonus
	 * @param wf
	 * @param gi
	 * @param rackSize
	 * @param isBlankOnBoard wildcard and blank are synonyms in this case. true means that it's a blank tile at that position.
	 * @return
	 */
	protected static int points(String word,boolean[] isBlankInWord, int pos,char[] row,int[] fastCrossPoints,
			int[] bonus,GameInfo2 gi,int rackSize,boolean[] isBlankOnBoard,String[][] fastCrossers){
		int wordFactor=1;
		int wordPoints=0;
		int crossPoints=0;
		int usedLetters=0;
//		System.out.println();//DEBUG
//		System.out.println("letter: letterPoints * letterBonus ... * wordBonus");//DEBUG
		for(int i=0;i<word.length();i++,pos++){
			char letter=isBlankInWord[i]?'.':word.charAt(i);
			//letter points are zero if it's a blank tile on the board at this position
			int letterPoints=isBlankOnBoard[pos]?0:WordFinder.valueOf(letter);
			int wordBonus=1;
			int letterBonus=1;
			
			if(row[pos]==' '){
				usedLetters++;
				//get bonus
				switch(bonus[pos]){
				case 0: break;//none
				case 1: letterBonus=2; break;//dl
				case 2: letterBonus=3; break;//tl
				case 3: wordBonus=2; break;//dw
				case 4: wordBonus=3; break;//tw
				}
				//only get crossPoints if there are a crossing word
				if(fastCrossers[pos]!=null){
					crossPoints+=(fastCrossPoints[pos]+letterPoints*letterBonus)*wordBonus;
				}
			}
			//takes into account that it can be a blank tile at the position, only gives points if it's not a blank tile
			wordPoints+=letterPoints*letterBonus;
			wordFactor*=wordBonus;
//			System.out.println(letter+": "+letterPoints+" * "+letterBonus+" = "+(letterPoints*letterBonus)+" ... * "+wordBonus);//DEBUG
		}
		int usedAllLettersBonus=usedLetters==7?40:0;
		//returns the total points
//		System.out.println("wordPoints="+wordPoints+ ", wordFactor="+wordFactor+", crossPoints="+crossPoints+", allBonus="+usedAllLettersBonus);//DEBUG
//		System.out.println("result=(wordPoints*wordFactor+crossPoints+allBonus)="+(crossPoints+wordPoints*wordFactor+usedAllLettersBonus));//DEBUG
		//print fast crossers
		return wordPoints*wordFactor+crossPoints+usedAllLettersBonus;
	}
	
	public static String fastCrossersToString(String[][] fastCrossers){
		String[] sarr=new String[fastCrossers.length];
		for(int i=0;i<fastCrossers.length;i++){
			if(fastCrossers[i]!=null){
				sarr[i]=fastCrossers[i][0]+" "+fastCrossers[i][1];
			}
		}
		return Arrays.toString(sarr);
	}
}
