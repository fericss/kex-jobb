import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class FredricTestStuff {

	private String[][] game;
	ArrayList<Point> buildLocations;
	WordFinder find;
	String rack;
	ArrayList<Move> buildAbleWords;
	Scrabby scrab;
	Main main;
	int[][] bonus;
	//	private String[][] m_words;
	public FredricTestStuff(String[][] _game, ArrayList<Point> _buildLocations, String _rack, Main _main, int[][] _bonus){
		//		wordList = _wordList;
		//		m_words = _m_words;

		buildAbleWords = new ArrayList<Move>();
		game = _game;
		bonus = _bonus;
		main = _main;
		rack = _rack;
		find = new WordFinder();
		scrab = new Scrabby(new GameInfo(game, bonus,rack),find);
		buildLocations = _buildLocations;
		//		ArrayList<String> wordThatCanBeBuildLol= new ArrayList<String>();
		//		for(Point p: buildLocations){
		//			if(p!=null)
		//			getWordThatCanBeBuilt(p);
		//		}

		for(int row = 0; row<15;row++){
			getWordThatCanBeBuiltOnCol(row);
			getWordThatCanBeBuiltOnRow(row);
		}
		System.out.println(""+buildAbleWords.size());
		//		for(String s : buildAbleWords.values())
		//		{
		//			System.out.println(s);
		//		}
		Collections.sort(buildAbleWords);
		for(Move p : buildAbleWords){
			System.out.println(p);
			//			System.out.println(p.x+":"+p.y+" "+p.word);
		}


	}
	private Collection<String> getWordThatCanBeBuiltOnRow(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String line="";
		String row = null;
		for(int i = 0; i<15; i++){

			if(row!=null && (game[x][i]==null || game[x][i].equals("_"))){
				words.add(row);
				row = null;
			}
			if(game[x][i]!=null && !game[x][i].equals("_")){
				if(row==null){
					row = "";
				}
				line+=game[x][i];
				row+=game[x][i];
				letters+=game[x][i];
			}
			else{
				line+=" ";
			}
		}
		//		for(String s : words){
		//			System.out.print("\n"+s+", ");
		//		}
		//		List<String> wordsThatCanBeBuilt = new ArrayList<String>();
		//		System.out.println("\n");
		List<String> test = find.Matches(rack+letters);
		for(String word : test){
			for(String s2 : words){
				if(word.contains(s2.toLowerCase())){
					if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase())){
						tryToMatchVertical(word,x);
					}
				}
			}
		}
		return null;
	}

	private boolean canBePlaced_ColCheck(String word, int row, int startPos) {

		int counter = 0;
		for(int i = startPos; i<startPos+word.length();i++){
			String tempWord = ""+word.charAt(counter);
			int y = row-1;
			while(y>0 && game[y][i]!=null && !game[y][i].equals("_")){
				tempWord = game[y][i]+tempWord;
				y--;
			}
			y = row+1;
			while(y<15 && game[y][i]!=null && !game[y][i].equals("_")){
				tempWord = tempWord+game[y][i];
				y++;
			}
			//			if(tempWord.length()>1)
			//			System.out.println(tempWord.toLowerCase());

			if(tempWord.length()>1 && !find.isWord(tempWord.toLowerCase())){
				return false;
			}

			counter++;
		}	
		return true;
	}
	private boolean canBePlaced_RowCheck(String word, int row, int startPos) {
		if(word==null){
			return false;
		}

		int counter = 0;
		//		if(startPos>0){
		//			if(game[startPos-1][row]==null || game[startPos-1][row].equals("_")){
		//				
		//			}
		//			else{
		//				return false;
		//			}
		//		}
		//		if(startPos+word.length()<14){
		//			if(game[startPos+word.length()][row]==null || game[startPos+word.length()][row].equals("_")){
		//				
		//			}
		//			else{
		//				return false;
		//			}
		//		}

		for(int i = startPos; i<startPos+word.length();i++){

			//			System.out.print(word);
			//			if(game[i][row]==null || game[i][row].equals("_") || game[i][row].toLowerCase().equals((""+word.charAt(counter)))){
			//				
			//			}
			//			else{
			////				System.out.println("The word is "+word+" and "+word.charAt(counter)+" does not match "+game[i][row]);
			//				return false;
			//			}

			String tempWord = ""+word.charAt(counter);
			int y = row-1;
			while(y>0 && game[i][y]!=null && !game[i][y].equals("_")){
				tempWord = game[i][y]+tempWord;
				y--;
			}
			y = row+1;
			while(y<15 && game[i][y]!=null && !game[i][y].equals("_")){
				tempWord = tempWord+game[i][y];
				y++;
			}
			//			if(tempWord.length()>1)
			//			System.out.println(tempWord.toLowerCase());

			if(word.toLowerCase().equals("arena")){
				System.out.println(tempWord.toLowerCase());
			}

			if(tempWord.length()>1 && !find.isWord(tempWord.toLowerCase())){
				return false;
			}

			counter++;
		}	
		return true;
	}
	private Collection<String> getWordThatCanBeBuiltOnCol(int x) {
		List<String> words = new ArrayList<String>();
		//		words.add("");
		String letters = "";
		String line="";
		String row = null;
		for(int i = 0; i<15; i++){

			if(row!=null && (game[i][x]==null )){
				words.add(row);
				row = null;
			}
			if(game[i][x]!=null){
				if(row==null){
					row = "";
				}
				line+=game[i][x];
				row+=game[i][x];
				letters+=game[i][x];
			}
			else{
				line+=" ";
			}
		}
		//		for(String s : words){
		//			System.out.print("\n"+s+", ");
		//		}
		//		List<String> wordsThatCanBeBuilt = new ArrayList<String>();
		//		System.out.println("\n");
		List<String> test = find.Matches(rack+letters);

		if(words.size()<1){
			for(String word : test){
				tryToMatchHorizontal(word,x);
			}
		}
		else{

			for(String word : test){
				for(String s2 : words){
					if(word.contains(s2.toLowerCase())){
						//					if(word.equals("rated")){
						//						System.out.println("WHEEEEEEE:" + rack+s2);
						//					}

						if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase()))
							tryToMatchHorizontal(word,x);
						//					System.out.print(word+", ");
						//					int board_pos = line.indexOf(s2.toUpperCase());
						//					int word_pos = word.indexOf(s2.toLowerCase());
						//					if(((word.length()-word_pos)+board_pos<=15) && ((board_pos-word_pos)>=0)){
						//						if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase()))
						//						if(canBePlaced_RowCheck(word,x,(board_pos-word_pos)))
						//						System.out.print(word+", ");
						//						tryToMatch(word,)
						//					}
					}
				}
			}
		}
		return null;
	}
	private void tryToMatchHorizontal(String word, int x) {
		int points = 0;
		int mult = 0;
		for(int i = 0;i<(15-word.length());i++){ 
			boolean possible = false;
			points = 0;
			mult = 1;
			if(i+word.length()!=15){
				if(game[i+word.length()][x]!=null)
					continue;
			}
			if(i-1>1 && game[i-1][x]!=null){
				continue;
			}

			for(int c = 0; c<word.length();c++){
				int type = bonus[x][i+c];
				int tempMult = 1;
				
				if(x<14)
				if(game[i+c][x+1]!=null){
					possible=true;
				}
				if(x>0)
				if(game[i+c][x-1]!=null){
					possible=true;
				}	
				
				if(game[i+c][x]==null){
					tempMult = type == 1 ? 2 : type==2 ? 3 : 1;
					mult = mult * (type == 3 ? 2 : type==4 ? 3 : 1);
				}
				points += tempMult*WordFinder.fastPoints[word.charAt(c)-'a'];

				if(game[i+c][x]==null){
					continue;
				}
				else{
					if(game[i+c][x].toLowerCase().equals(""+word.charAt(c))){
						possible=true;
						continue;
					}
					else{
						possible = false;
						break;
					}
				}
			}
			points = points*mult;
			if(possible){
				boolean check = false;
				for(int c = 0; c<word.length();c++){
					check = game[i+c][x] == null ? true : check;
				}
				if(!check){
					continue;
				}


				boolean cont = false;
				for(int c = 0; c<word.length();c++){
					//					if(bonus[x][i+c]!=0){
					//						System.out.println(word+" "+bonus[x][i+c]+" "+word.charAt(c));
					//					}
					String tempWord = ""+word.charAt(c);
					int y = x-1;
					while(y>0 && game[i+c][y]!=null){
						tempWord = game[i+c][y]+tempWord;
						y--;
					}
					y = x+1;
					while(y<15 && game[i+c][y]!=null){
						tempWord = tempWord+game[i+c][y];
						y++;
					}
					if(tempWord.length()>1){
						if(find.isWord(tempWord.toLowerCase())){
							if(game[i+c][x]==null){
								int type = bonus[x][i+c];
								if(type==0){
									points += calcPoints(tempWord.toLowerCase());
								} 
								else if(type==1){
									points += calcPoints(tempWord.toLowerCase()) + WordFinder.fastPoints[word.charAt(c)-'a'];
								}
								else if(type==2){
									points += calcPoints(tempWord.toLowerCase()) + WordFinder.fastPoints[word.charAt(c)-'a']*2;
								}
								else if(type==3){
									points += calcPoints(tempWord.toLowerCase())*2;
								}
								else if(type==4){
									points += calcPoints(tempWord.toLowerCase())*3;
								}
							}
						}	
						else{
							cont = true;
							break;
						}
					}

				}
				if(cont){
					continue;
				}

				String gameLetters = "";
				for(int ic = 0; ic<word.length();ic++){
					if(game[ic+i][x]!=null){
						gameLetters +=game[ic+i][x];
					}
				}
				gameLetters += rack;
				if(find.WordCanBeBuiltFromSourceLetters(word, gameLetters.toLowerCase())){
					//					System.out.println(x+":"+i+" "+word+" "+points);
					buildAbleWords.add(new Move(points, word, x,i, true));
				}
			}
		}
	}
	private void tryToMatchVertical(String word, int x) {
		int points = 0;
		int mult = 0;
		for(int i = 0;i<(15-word.length());i++){ 
			boolean possible = false;
			points = 0;
			mult = 1;
			if(i+word.length()!=15){
				if(game[x][i+word.length()]!=null)
					continue;
			}
			if(i-1>1 && game[x][i-1]!=null){
				continue;
			}

			for(int c = 0; c<word.length();c++){
				int type = bonus[i+c][x];
				int tempMult = 1;
				
				if(x<14)
				if(game[x+1][i+c]!=null){
					possible=true;
				}
				if(x>0)
				if(game[x-1][i+c]!=null){
					possible=true;
				}	
				
				
				if(game[x][i+c]==null){
					tempMult = type == 1 ? 2 : type==2 ? 3 : 1;
					mult = mult * (type == 3 ? 2 : type==4 ? 3 : 1);
				}
				points += tempMult*WordFinder.fastPoints[word.charAt(c)-'a'];

				if(game[x][i+c]==null){
					continue;
				}
				else{
					if(game[x][i+c].toLowerCase().equals(""+word.charAt(c))){
						possible=true;
						continue;
					}
					else{
						possible = false;
						break;
					}
				}
			}
			points = points*mult;
			if(possible){

				boolean check = false;
				for(int c = 0; c<word.length();c++){
					check = game[x][i+c] == null ? true : check;
				}
				if(!check){
					continue;
				}

				boolean cont = false;
				for(int c = 0; c<word.length();c++){
					String tempWord = ""+word.charAt(c);
					int y = x-1;
					while(y>0 && game[y][i+c]!=null){
						tempWord = game[y][i+c]+tempWord;
						y--;
					}
					y = x+1;
					while(y<15 && game[y][i+c]!=null){
						tempWord = tempWord+game[y][i+c];
						y++;
					}
					if(tempWord.length()>1){
						if(find.isWord(tempWord.toLowerCase())){
							if(game[x][i+c]==null){
								int type = bonus[i+c][x];
								if(type==0){
									points += calcPoints(tempWord.toLowerCase());
								} 
								else if(type==1){
									points += calcPoints(tempWord.toLowerCase()) + WordFinder.fastPoints[word.charAt(c)-'a'];
								}
								else if(type==2){
									points += calcPoints(tempWord.toLowerCase()) + WordFinder.fastPoints[word.charAt(c)-'a']*2;
								}
								else if(type==3){
									points += calcPoints(tempWord.toLowerCase())*2;
								}
								else if(type==4){
									points += calcPoints(tempWord.toLowerCase())*3;
								}
							}
						}	
						else{
							cont = true;
							break;
						}
					}
				}
				if(cont){
					continue;
				}
				String gameLetters = "";
				for(int ic = 0; ic<word.length();ic++){
					if(game[x][ic+i]!=null){
						gameLetters +=game[x][ic+i];
					}
				}
				gameLetters += rack;
				if(find.WordCanBeBuiltFromSourceLetters(word, gameLetters.toLowerCase())){
					buildAbleWords.add(new Move(points, word, i,x, false));
				}
			}
		}
	}
	private Collection<String> getWordThatCanBeBuilt(Point p) {
		int x = p.x;
		int y = p.y;
		String row = "";
		for(int i = 0; i<15; i++){
			if(game[x][i]!=null && !game[x][i].equals("_"))
				row+=game[x][i];
		}
		System.out.println("");
		List<String> test = find.Matches(rack+row);
		for(String s : test){
			System.out.print(s+", ");
		}
		return null;
	}
	private int calcPoints(String s){
		int word_points = 0;
		for(char c : s.toCharArray()){
			word_points += WordFinder.fastPoints[c-'a'];
		}
		return word_points;
	}
}
