
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
}
