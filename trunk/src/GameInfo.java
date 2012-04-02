import java.util.Arrays;


public class GameInfo {

	private int bonus[][];//never changes
	private String game[][];
	private boolean wildCards[][];
	private String rack;
	final int length=15;
	
	//TEST
	GameInfo oldGameInfo=null;
	String[][] fastCrossers=null;
	int[] changed=null;
	
	public GameInfo(String _game[][], int _bonus[][],boolean _wildCards[][],String _rack){
		setGame(_game);
		setBonus(_bonus);
		setRack(_rack);
		setWildCards(_wildCards);
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
		
	}
	public boolean[][] getWildCards(){
		return null;
	}
	
	/**
	 * Unimplemented! 
	 * returns a freq array with the number of each unknown chartype
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
	
	
	/**
	 * Once you have the row and crossers you don't have to care about coordinates
	 * so you only have to make one version of the methods...
	 * @param row
	 * @param vertical
	 * @return
	 */
	public String getRow(final int row,boolean vertical){
		vertical=!vertical;
		final int length=15;
		final String[][] game=getGame();
		final StringBuilder sb=new StringBuilder(length);
		if(vertical){
			for(int i=0;i<length;i++){
				sb.append(game[row][i]==null?" ":game[row][i]);
			}
		}else {
			for(int i=0;i<length;i++){
				sb.append(game[i][row]==null?" ":game[i][row]);
			}
		}
		return sb.toString().toLowerCase();
	}
	
	
	/**
	 * 
	 * @param row
	 * @param vertical
	 * @return
	 */
	public String[] getRowCrossers(final int row,boolean vertical){
		vertical=!vertical;
		final int length=15;
		final String[][] game=getGame();
		final String[] crossers=new String[length];
		
		StringBuilder sb;//=new StringBuilder(length);
		if(vertical){
			for(int i=0;i<length;i++){
				//only check new letters
				if(game[row][i]==null){
					//check if it's possible
					if( (row-1>=0 && game[row-1][i]!=null) || (row+1<length && game[row+1][i]!=null)){
						int row2=row;
						//go to start of word
						while(row2-1>=0 && game[row2-1][i]!=null){
							row2--;
						}
						//construct word
						sb=new StringBuilder();
						while( row2==row || (row2<length && game[row2][i]!=null) ){
							if(row2 == row){
								sb.append(" ");
							} else {
								sb.append(game[row2][i]);
							}
							row2++;
						}
						//save word
						crossers[i]=sb.toString().toLowerCase();
					}
				}
			}
		}else {
			for(int i=0;i<length;i++){
				if(game[i][row]==null){
					//only check new letters
					if(game[i][row]==null){
						//check if it's possible
						if( (row-1>=0 && game[i][row-1]!=null) || (row+1<length && game[i][row+1]!=null)){
							int row2=row;
							//go to start of word
							while(row2-1>=0 && game[i][row2-1]!=null){
								row2--;
							}
							//construct word
							sb=new StringBuilder();
							while( row2==row || (row2<length && game[i][row2]!=null) ){
								if(row2 == row){
									sb.append(" ");
								} else {
									sb.append(game[i][row2]);
								}
								row2++;
							}
							//save word
							crossers[i]=sb.toString().toLowerCase();
						}
					}
				}
			}
		}
		return crossers;
	}
	
	
	//TEST
	/**
	 * TODO
	 * update version here: almost finished
	 * update version of Help methods
	 * update version of filter methods
	 */
	
	/**
	 * Used by when making counter clone and is used when making counter moves.
	 * @param _game
	 * @param _bonus
	 * @param _wildCards
	 * @param _rack
	 * @param old
	 */
	private GameInfo(String _game[][], int _bonus[][],boolean _wildCards[][],String _rack,GameInfo old){
		setGame(_game);
		setBonus(_bonus);
		setRack(_rack);
		setWildCards(_wildCards);
		setOldGameInfo(old);
	}
	
	
	
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
		return new GameInfo(gameCopy, bonus, wildcardsCopy, rack, this);
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
		GameInfo gi=clone();
		boolean vertical=!m.vertical;
		String word=m.word;
		int x=m.x;
		int y=m.y;
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
		return gi;
	}
	
	/**
	 * -1=no change, 0=change adjacent, 1=on change.
	 * 
	 * returns null if oldGameInfo==null.
	 * @param row
	 * @param vertical
	 * @param old
	 * @return
	 */
	public int[] changed(final int row, boolean vertical){
		if(this.changed!=null){ return this.changed; }
		if(oldGameInfo==null){ return null; }
		final GameInfo old=oldGameInfo;
		int[] changed=new int[length];
		final String[][] game=getGame();
		final String[][] oldGame=old.getGame();
		if(vertical){
			for(int i=0;i<length;i++){
				changed[i]=changed(row,i,game,oldGame);
			}
		} else {
			for(int i=0;i<length;i++){
				changed[i]=changed(i,row,game,oldGame);
			}
		}
		return changed;
	}
	
	/**
	 * returns -1=no change, 0=change adjacent, 1=on change for a coordinate in game
	 * @param x
	 * @param y
	 * @param game
	 * @param oldGame
	 * @return
	 */
	private static int changed(final int x,final int y,final String[][] game,final String[][] oldGame){
		int x2=x;
		int y2=y;
		int ret=1;
		for(int i=0;i<=4;i++){
			ret=0;//return 0 if something adjacent changed
			//change parameter values
			switch(i){
			case 0: x2=y; y2=y; ret=1; break;//return a two if on change
			case 1: x2=x+1; y2=y; break;
			case 2: x2=x-1; y2=y; break;
			case 3: x2=x; y2=y+1; break;
			case 4: x2=x; y2=y-1; break;
			}
			//try makes it unnecessary to do range cheks
			try{
				//return if the value at position if not equal
				if( ! (game[x2][y2] == oldGame[x2][y2] || 
					(game[x2][y2] !=null && game[x2][y2].equals(oldGame[x2][y2])))){
					return ret;
				} 
			} catch(Exception e){}
		}
		//no change return -1
		return -1;
	}
	
	
	public String[] getRowCrossers2Update(final int row, boolean vertical, final String[] oldCrossers, final int[] change){
		vertical=!vertical;
		final String[][] game=getGame();
		final String[] crossers=Arrays.copyOf(oldCrossers,oldCrossers.length);
		
		StringBuilder sb;//=new StringBuilder(length);
		if(vertical){
			for(int i=0;i<length;i++){
				if(game[row][i]!=null){ //can be no crossers here
					crossers[i]=null;//overwrite
				} else{//game[row][i]==null
					//only update the value if it has been updated
					if(change[i]==0){ //null char and, update if changed else keep the old
						//check if it's possible
						if( (row-1>=0 && game[row-1][i]!=null) || (row+1<length && game[row+1][i]!=null)){
							int row2=row;
							//go to start of word
							while(row2-1>=0 && game[row2-1][i]!=null){
								row2--;
							}
							//construct word
							sb=new StringBuilder();
							while( row2==row || (row2<length && game[row2][i]!=null) ){
								if(row2 == row){
									sb.append(" ");
								} else {
									sb.append(game[row2][i]);
								}
								row2++;
							}
							//save word
							crossers[i]=sb.toString().toLowerCase();
						}
					}
				}

			}
		}else {
			for(int i=0;i<length;i++){
				if(game[i][row]!=null){ //can be no crossers here
					crossers[i]=null; //overwrite
				} else {//game[i][row]==null
					//only update the value if it has been updated
					if(change[i]==0){//null char and, update if changed else keep the old
						//check if it's possible
						if( (row-1>=0 && game[i][row-1]!=null) || (row+1<length && game[i][row+1]!=null)){
							int row2=row;
							//go to start of word
							while(row2-1>=0 && game[i][row2-1]!=null){
								row2--;
							}
							//construct word
							sb=new StringBuilder();
							while( row2==row || (row2<length && game[i][row2]!=null) ){
								if(row2 == row){
									sb.append(" ");
								} else {
									sb.append(game[i][row2]);
								}
								row2++;
							}
							//save word
							crossers[i]=sb.toString().toLowerCase();
						}
					}
				}

			}
		}
		return crossers;
	}
	
	
	
	/**
	 * Not tested!
	 * creates fast row crossers directly. 
	 * @param row
	 * @param vertical
	 * @param oldCrossers
	 * @param change
	 * @return
	 */
	public String[][] getFastRowCrossers2Update(final int row, boolean vertical){
		if(this.fastCrossers!=null){ return this.fastCrossers; }
		final int[] change1=this.changed(row, vertical);
		final String[][] fastCrossers;
		if(oldGameInfo!=null){
			fastCrossers=Arrays.copyOf(oldGameInfo.getFastRowCrossers2Update(row, vertical), length);
		} else {
			fastCrossers=new String[length][];
		}
		
		vertical=!vertical;
		final String[][] game=getGame();
		//copy if not null else create new array filled with zeroes (0 or larger means something was changed)
		final int[] change=change1!=null?change1:new int[length];
		
		StringBuilder sb;//=new StringBuilder(length);
		StringBuilder sb2;
		if(vertical){
			for(int i=0;i<length;i++){
				if(game[row][i]!=null){ //can be no crossers here
					fastCrossers[i]=null;//overwrite
				} else{//game[row][i]==null
					//only update the value if it has been updated
					if(change[i]==0){ //null char and, update if changed else keep the old
						//check if it's possible
						if( (row-1>=0 && game[row-1][i]!=null) || (row+1<length && game[row+1][i]!=null)){
							int row2=row;
							//go to start of word
							while(row2-1>=0 && game[row2-1][i]!=null){
								row2--;
							}
							//construct word
							sb=new StringBuilder("");
							sb2=new StringBuilder("");
							boolean before=true;
							while( row2==row || (row2<length && game[row2][i]!=null) ){
								if(row2 == row){
									before=false;
								} else {//before or after
									if(before){
										sb.append(game[row2][i]);
									} else {
										sb2.append(game[row2][i]);
									}
								}
								row2++;
							}
							//save word
							fastCrossers[i]=new String[] {sb.toString().toLowerCase(),sb2.toString().toLowerCase()};
						}
					}
				}

			}
		}else {
			for(int i=0;i<length;i++){
				if(game[i][row]!=null){ //can be no crossers here
					fastCrossers[i]=null; //overwrite
				} else {//game[i][row]==null
					//only update the value if it has been updated
					if(change[i]==0){//null char and, update if changed else keep the old
						//check if it's possible
						if( (row-1>=0 && game[i][row-1]!=null) || (row+1<length && game[i][row+1]!=null)){
							int row2=row;
							//go to start of word
							while(row2-1>=0 && game[i][row2-1]!=null){
								row2--;
							}
							//construct word
							sb=new StringBuilder();
							sb2=new StringBuilder();
							boolean before=true;
							while( row2==row || (row2<length && game[i][row2]!=null) ){
								if(row2 == row){
									before=false;
								} else {//before or after
									if(before){
										sb.append(game[i][row2]);
									} else {
										sb2.append(game[i][row2]);
									}
								}
								row2++;
							}
							//save word
							fastCrossers[i]=new String[] {sb.toString().toLowerCase(), sb2.toString().toLowerCase()};
						}
					}
				}

			}
		}
		return fastCrossers;
	}
	
}
