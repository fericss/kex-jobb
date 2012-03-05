import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	
	
	public FredricTestStuff(String[][] _game, ArrayList<Point> _buildLocations, String _rack, Main _main, int[][] _bonus, WordFinder _find){

		buildAbleWords = new ArrayList<Move>();
		game = _game;
		bonus = _bonus;
		main = _main;
		rack = _rack;
		find = _find;
//		scrab = new Scrabby(new GameInfo(game, bonus,rack),find);
		buildLocations = _buildLocations;

		for(int row = 0; row<15;row++){
			getWordThatCanBeBuiltOnColD(row);
			getWordThatCanBeBuiltOnRowD(row);
		}
		System.out.println(""+buildAbleWords.size());
		Collections.sort(buildAbleWords);
//		for(Move p : buildAbleWords){
//			System.out.println(p);
//		}
		
		
//		ArrayList<Move> tmp=buildAbleWords;
//		buildAbleWords=new ArrayList<Move>();
//		for(int row = 0; row<15;row++){
//			getWordThatCanBeBuiltOnCol2(row);
//			getWordThatCanBeBuiltOnRow2(row);
//		}
//		Collections.sort(buildAbleWords);
//		System.out.println(buildAbleWords.size()+" "+tmp.size());
//		for(int i=0;i<buildAbleWords.size();i++){
//			if(!buildAbleWords.get(i).equals(s1.get(i))){
//				System.out.println(i+" "+buildAbleWords.get(i)+" "+s1.get(i));
//			}
//		}
		
//		


	}
	
	
	private Collection<String> getWordThatCanBeBuiltOnRow2(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){
			if(game[x][i]!=null){
				if(row==null){
					row = "";
				}
				row+=game[x][i];
				letters+=game[x][i];
			}
			if(row!=null && (game[x][i]==null || i==14) ){
				words.add(row);
				row = null;
			}
		}
		if(words.size()<1){
			return null;
		}
		System.out.println(letters);
		List<String> test = find.getFastFilter().filter(rack.toLowerCase(), words.toArray(new String[words.size()]));
//				find.Matches(rack+letters);

		for(String word : test){
			for(String s2 : words){
				if(word.contains(s2.toLowerCase())){
					if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase()))
						tryToMatchVertical(word,x);
				}
			}
		}
		return null;
	}
	private Collection<String> getWordThatCanBeBuiltOnCol2(int x) {
		List<String> words = new ArrayList<String>();
		//		words.add("");
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){
			if(game[i][x]!=null){
				if(row==null){
					row = "";
				}
				row+=game[i][x];
				letters+=game[i][x];
			}
			if(row!=null && (game[i][x]==null || i==14)){
				words.add(row);
				row = null;
			}
		}
		if(words.size()<1){
			return null;
		}
		System.out.println(letters);
		List<String> test = find.getFastFilter().filter(rack.toLowerCase(), words.toArray(new String[words.size()]));
//				find.Matches(rack+letters);
		
		for(String word : test){
			for(String s2 : words){
				if(word.contains(s2.toLowerCase())){
					if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase())) //bug: could contain several s2 => overly restrictive
						tryToMatchHorizontal(word,x);
				}
			}
		}

		return null;
	}
	
	/**
	 * debug
	 * @param x
	 * @return
	 */
	private Collection<String> getWordThatCanBeBuiltOnRowD(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){
			//add letter to the word
			if(game[x][i]!=null){
				if(row==null){
					row = "";
				}
				row+=game[x][i];
				letters+=game[x][i];
			}
			//check for the end of a word
			if(row!=null && (game[x][i]==null || i==14)){
				words.add(row);
				row = null;
			}

		}
		//DEBUG, see if all words are found and letters are correct
		System.out.println(rack+" "+words+" "+letters);
		
		//try filtering and then placing word
		List<String> test = find.Matches(rack+letters);
		if(words.size()<1){
			for(String word : test){
				tryToMatchVertical(word,x);
			}
		}else {
			for(String word : test){
				for(String s2 : words){
					if(word.contains(s2.toLowerCase())){
						//buggy check?
						//					if(find.WordCanBeBuiltFromSourceLetters(word,(rack+letters).toLowerCase())){
						tryToMatchVertical(word,x);
						//					}
					}
				}
			}
		}
		return null;
	}
	/**
	 * debug
	 * @param x
	 * @return
	 */
	private Collection<String> getWordThatCanBeBuiltOnColD(int x) {
		List<String> words = new ArrayList<String>();
		//		words.add("");
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){
			//add letter to the word
			if(game[i][x]!=null){
				if(row==null){
					row = "";
				}
				row+=game[i][x];
				letters+=game[i][x];
			}
			//check for the end of a word
			if(row!=null && (game[i][x]==null || i==14)){
				words.add(row);
				row = null;
			}
		}
		//DEBUG, see if all words are found and letters are correct
		System.out.println(rack+" "+words+" "+letters);
			
		//try filtering and then placing word
		List<String> test = find.Matches(rack+letters);
		if(words.size()<1){
			for(String word : test){
				tryToMatchHorizontal(word,x);
			}
		} else {
			for(String word : test){
				for(String s2 : words){
					if(word.contains(s2.toLowerCase())){
						//					if(find.WordCanBeBuiltFromSourceLetters(word,(rack+letters).toLowerCase()))
						tryToMatchHorizontal(word,x);
						//					}
					}
				}
			}
		}
		return null;
	}
	
	private Collection<String> getWordThatCanBeBuiltOnRow(int x) {
		List<String> words = new ArrayList<String>();
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){

			if(row!=null && (game[x][i]==null)){
				words.add(row);
				row = null;
			}
			if(game[x][i]!=null){
				if(row==null){
					row = "";
				}
				row+=game[x][i];
				letters+=game[x][i];
			}
			else{
			}
		}
		List<String> test = find.Matches(rack+letters);
		if(words.size()<1){
			for(String word : test){
				tryToMatchVertical(word,x);
			}
		} else {
			for(String word : test){
				for(String s2 : words){
					if(word.contains(s2.toLowerCase())){
						if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase())){
							tryToMatchVertical(word,x);
						}
					}
				}
			}
		}
		return null;
	}
	private Collection<String> getWordThatCanBeBuiltOnCol(int x) {
		List<String> words = new ArrayList<String>();
		//		words.add("");
		String letters = "";
		String row = null;
		for(int i = 0; i<15; i++){

			if(row!=null && (game[i][x]==null)){
				words.add(row);
				row = null;
			}
			if(game[i][x]!=null){
				if(row==null){
					row = "";
				}
				row+=game[i][x];
				letters+=game[i][x];
			}
			else{
			}
		}
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
						if(find.WordCanBeBuiltFromSourceLetters(word,(rack+s2).toLowerCase()))
							tryToMatchHorizontal(word,x);
					}
				}
			}
		}
		return null;
	}
	
	private void tryToMatchHorizontal(String word, int x) {
		int points = 0;
		int mult = 0;
		for(int i = 0;i<(16-word.length());i++){ 
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
					if(game[i+c][x]!=null){
						continue;
					}
					String tempWord = ""+word.charAt(c);
					int y = x-1;
					while(y>=0 && game[i+c][y]!=null){
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
		for(int i = 0;i<(16-word.length());i++){ 
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
					if(game[x][i+c]!=null){
						continue;
					}
					
					String tempWord = ""+word.charAt(c);
					int y = x-1;
					while(y>=0 && game[y][i+c]!=null){
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
	private int calcPoints(String s){
		int word_points = 0;
		for(char c : s.toCharArray()){
			word_points += WordFinder.fastPoints[c-'a'];
		}
		return word_points;
	}
}
