
public class GameInfo {

	private int bonus[][];
	private String game[][];
	private boolean wildCards[][];
	private String rack;
	
	public GameInfo(String _game[][], int _bonus[][],boolean _wildCards[][],String _rack){
		setGame(_game);
		setBonus(_bonus);
		setRack(_rack);
		setWildCards(_wildCards);
	}

	public void setBonus(int bonus[][]) {
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
	 * Once you have the row and crossers you don't have to care about coordinates
	 * so you only have to make one version of the methods...
	 * @param row
	 * @param vertical
	 * @return
	 */
	public String getRow(int row,boolean vertical){
		vertical=!vertical;
		int length=15;
		String[][] game=getGame();
		StringBuilder sb=new StringBuilder(length);
		String s;
		if(vertical){
			for(int i=0;i<length;i++){
				s=game[row][i]==null?" ":game[row][i];
				sb.append(s);
			}
		}else {
			for(int i=0;i<length;i++){
				s=game[i][row]==null?" ":game[i][row];
				sb.append(s);
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
		String[][] game=getGame();
		String[] crossers=new String[length];
		
		StringBuilder sb;//=new StringBuilder(length);
		String s;
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
	
}
