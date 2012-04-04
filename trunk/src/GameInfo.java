import java.util.Arrays;

import org.junit.Test;


public class GameInfo {

	private int bonus[][];//never changes
	private String game[][];
	private boolean wildCards[][];
	private String rack;
	final int length=15;
	
	
	
	//TEST
	char board[][]=null;
	GameInfo oldGameInfo=null;
	int[][] changedPositionsHorizontal=null;
	int[][] changedPositionsVertical=null;
	String[] horizontalRows=null;
	String[] verticalRows=null;
	String[][][] horizontalFastCrossers=null;
	String[][][] verticalFastCrossers=null;
	
	public GameInfo(String _game[][], int _bonus[][],boolean _wildCards[][],String _rack){
//		System.out.println("contructing gameinfo...");
		setGame(_game);
		setBonus(_bonus);
		setRack(_rack);
		setWildCards(_wildCards);
		board=Scrabby.gameToBoard(_game);//TEST
//		System.out.println("The board in GameInfo:");
//		for(int i=0;i<board.length;i++){
//			System.out.println(Arrays.toString(board[i]));
//		}
//		System.out.println("contructed gameinfo.");
	}
	
	
	
	/**
	 * can be used 
	 * @param old
	 */
	public void setOldGameInfo(GameInfo old){
		oldGameInfo=old;
	}
	
	public GameInfo getOldGameInfo(){
		return oldGameInfo;
	}
	
	/**
	 * never use this except in constructor
	 * @param bonus
	 */
	private void setBonus(int bonus[][]) {
		this.bonus = bonus;
	}

	public int[][] getBonus() {
		return bonus;
	}

	public void setGame(String game[][]) {
		this.game = game;
	}

	public String[][] getGame() {
		return game;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public String getRack() {
		return rack;
	}
	
	public void setWildCards(boolean[][] wildcards){
		this.wildCards=wildcards;
	}
	public boolean[][] getWildCards(){
		return wildCards;
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
	
	
//	/**
//	 * Once you have the row and crossers you don't have to care about coordinates
//	 * so you only have to make one version of the methods...
//	 * @param row
//	 * @param vertical
//	 * @return
//	 */
//	public String getRow(final int row,boolean vertical){
//		vertical=!vertical;
//		final int length=15;
//		final String[][] game=getGame();
//		final StringBuilder sb=new StringBuilder(length);
//		if(vertical){
//			for(int i=0;i<length;i++){
//				sb.append(game[row][i]==null?" ":game[row][i]);
//			}
//		}else {
//			for(int i=0;i<length;i++){
//				sb.append(game[i][row]==null?" ":game[i][row]);
//			}
//		}
//		return sb.toString().toLowerCase();
//	}
	
	protected String getRowHelp(final int row,boolean vertical){
		vertical=!vertical;
		final StringBuilder sb=new StringBuilder(length);
		if(vertical){
			for(int i=0;i<length;i++){
				sb.append(board[row][i]);
			}
		}else {
			for(int i=0;i<length;i++){
				sb.append(board[i][row]);
			}
		}
		return sb.toString().toLowerCase();
	}
	
	
	protected String[] getHorizontalRowsHelp(){
		if(horizontalRows!=null){//load if
			return horizontalRows;
		}
		String[] rows=new String[length];
		for(int i=0;i<length;i++){
			rows[i]=getRowHelp(i,false);
		}
		horizontalRows=rows;
		return rows;
	}
	
	protected String[] getVerticalRowsHelp(){
		if(verticalRows!=null){//load if
			return verticalRows;
		}
		String[] rows=new String[length];
		for(int i=0;i<length;i++){
			rows[i]=getRowHelp(i,true);
		}
		verticalRows=rows;
		return rows;
	}
	
	/**
	 * Appears to work
	 * maybe faster than the old get row.
	 * @param rowIndex
	 * @param vertical
	 * @return
	 */
	public String getRow(final int rowIndex,final boolean vertical){
		if(vertical){
			return getVerticalRowsHelp()[rowIndex];
		} else {
			return getHorizontalRowsHelp()[rowIndex];
		}
	}
	
	protected static int[][] getHasChangedHorizontalHelp(char[][] game,char[][] game2){
		final int length=15;
		int[][] hasChanged=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				if(game[i][j]!=game2[i][j]){//game[i][j]!=' '
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
	
	/**
	 * Uses the horizontal calculation but with the result transposed.
	 * @return
	 */
	protected int[][] getHasChangedVerticalHelp(){
		if(this.oldGameInfo==null){return null;}
		if(changedPositionsVertical!=null){
			return changedPositionsVertical;
		}
		int[][] tmp=getHasChangedHorizontalHelp();
		int[][] res=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				res[i][j]=tmp[j][i];
			}
		}
		changedPositionsVertical=res;
		return res;
	}
	
	protected int[][] getHasChangedHorizontalHelp(){
		if(this.oldGameInfo==null){return null;}
		if(changedPositionsHorizontal!=null){//load value if
			return changedPositionsHorizontal;
		}
		changedPositionsHorizontal=getHasChangedHorizontalHelp(this.oldGameInfo.board,this.board);
		return changedPositionsHorizontal;
	}
	
	
	//TEST
	/**
	 * TODO
	 * update version here: almost finished
	 * update version of Help methods
	 * update version of filter methods
	 */
	
	
	
	
	@Override
	public GameInfo clone(){
		//needs cloning
		/*private String game[][];
		private boolean wildCards[][];
		private String rack;*/
		
		String[][] gameCopy=new String[length][];
		boolean[][] wildcardsCopy=new boolean[length][];
		
		for(int i=0;i<15;i++){
			gameCopy[i]=Arrays.copyOf(getGame()[i], length);
			wildcardsCopy[i]=Arrays.copyOf(getWildCards()[i], length);
		}
		return new GameInfo(gameCopy, bonus, wildcardsCopy, rack);
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
	public GameInfo newGameInfo(final String newRack,final Move m){
		//print stuff
		System.out.println("Contructing GameInfo from old GameInfo and a move...");
		System.out.println("Move: "+m);
		
		//do stuff
		GameInfo gi=clone();
		gi.oldGameInfo=this;
		boolean vertical=!m.vertical;
		String word=m.word;
		int x=m.y;//Hax, swaps x and y
		int y=m.x;
		if(vertical){
			for(int i=0;i<word.length();i++,y++){
				gi.getGame()[x][y]=String.valueOf(word.charAt(i));
				gi.getWildCards()[x][y]=m.isWildCard(i);
			}
		}else{
			for(int i=0;i<word.length();i++,x++){
				gi.getGame()[x][y]=String.valueOf(word.charAt(i));
				gi.getWildCards()[x][y]=m.isWildCard(i);
			}
		}
		gi.setRack(newRack);
		
		gi.board=Scrabby.gameToBoard(gi.getGame());
		char[][] board2=gi.board;
		int[][] hasChanged=gi.getHasChangedHorizontalHelp();
		this.changedPositionsHorizontal=hasChanged;
		
		//Print stuff
		System.out.println("The new board in GameInfo:");
		for(int i=0;i<board2.length;i++){
			System.out.println(Arrays.toString(board2[i]));
		}
//		System.out.println("changed1:");
//		for(int i=0;i<hasChanged.length;i++){
//			System.out.println(Arrays.toString(hasChanged[i]));
//		}
		System.out.println("changed2:");
		for(int i=0;i<length;i++){
			System.out.println(Arrays.toString(gi.getChanged(i, false)));
		}
//		System.out.println("changed3 (in vertical direction):");
//		for(int i=0;i<length;i++){
//			System.out.println(Arrays.toString(gi.changed(i, true)));
//		}
		System.out.println("get fastCrossers");
		for(int i=0;i<length;i++){
			String[][] tmp=gi.getFastCrossers(i, false);
			for(int j=0;j<length;j++){
				System.out.print(Arrays.toString(tmp[j])+" ");
			}
			System.out.println();
		}
		System.out.println("get rows");
		for(int i=0;i<length;i++){
			System.out.println(gi.getRow(i, false));
		}
		System.out.println("Contructed new GameInfo.");
		
		//return stuff
		return gi;
	}
	
	/**
	 * 0=no change, 1=affected by change, 2=on top of change
	 * @param rowIndex
	 * @param vertical
	 * @return
	 */
	public int[] getChanged(final int rowIndex, final boolean vertical){
		if(vertical){
			return getHasChangedVerticalHelp()[rowIndex];
		} else {
			return getHasChangedHorizontalHelp()[rowIndex];
		}
	}
	

	
	/**
	 * get crossers but in the wrong direction.
	 * getFastCrossers gives the fastCrossers in the correct
	 * direction.
	 * 
	 * @param row
	 * @return
	 */
	protected static String[][] getFastCrossersHelp(final String row){
		final int length=15;
		String[][] res=new String[length][];
		StringBuilder sb=new StringBuilder();
		boolean added=true;
		for(int i=0, beforeWord=-1;i<length;i++){
			if(row.charAt(i)==' '){
				//if has not added word, add it
				if(!added){
					//get the string
					String tmp=sb.toString();
					//construct array it's not already there
					if(beforeWord>=0 && res[beforeWord]==null){
						res[beforeWord]=new String[2];
						//fill in default values
						res[beforeWord][0]="";
						res[beforeWord][1]="";
					}
					//construct array if it's not already there
					if(res[i]==null){
						res[i]=new String[2];
						//fill in default values
						res[i][0]="";
						res[i][1]="";
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
				sb.append(row.charAt(i));
				added=false;
			}
		}
		return res;
	}
	
	/**
	 * uses crossing rows to get crossers in other direction
	 * e.g if it's vertical rows then it gets all fastCrossers 
	 * for all horizontal rows.
	 * @param rows
	 * @return
	 */
	protected String[][][] getFastCrossersHelp(final String[] rows){
		final int length=15;
		String[][][] res=new String[length][][];
		for(int i=0;i<length;i++){
			res[i]=getFastCrossersHelp(rows[i]);
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
			horizontalFastCrossers=getFastCrossersHelp(getVerticalRowsHelp());
		}
		return horizontalFastCrossers;
	}
	
	protected String[][][] getVerticalFastCrossersHelp(){
		if(verticalFastCrossers==null){
			verticalFastCrossers=getFastCrossersHelp(getHorizontalRowsHelp());
		}
		return verticalFastCrossers;
	}
	
	public String[][] getFastCrossers(final int rowIndex,final boolean vertical){
		if(vertical){
			return getVerticalFastCrossersHelp()[rowIndex];
		} else {
			return getHorizontalFastCrossersHelp()[rowIndex];
		}
	}
	
}
