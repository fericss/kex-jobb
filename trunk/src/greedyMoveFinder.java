import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class greedyMoveFinder {


	private String[][] game;
	ArrayList<Point> buildLocations;
	WordFinder find;
	String rack;
	ArrayList<Move> buildAbleWords;
	Main main;
	int[][] bonus;
	Scrabby scrab=null;

	public greedyMoveFinder(String[][] _game, ArrayList<Point> _buildLocations, String _rack, Main _main, int[][] _bonus, WordFinder _find){

		buildAbleWords = new ArrayList<Move>();
		game = _game;
		bonus = _bonus;
		main = _main;
		rack = _rack;
		find = _find;
		buildLocations = _buildLocations;

		for(int row = 0; row<15;row++){
			getWordThatCanBeBuiltOnCol(row);
			getWordThatCanBeBuiltOnRow(row);
		}
		Collections.sort(buildAbleWords);
		if(Main.DEBUG){
			for(Move p : buildAbleWords){
				System.out.println(p);
			}
			System.out.println(""+buildAbleWords.size());
		}
	}
	public Move getBestMove(){
		return buildAbleWords.size()>0 ? buildAbleWords.get(0) : null;
	}
	public Move getRandomMove(){
		return buildAbleWords.get((int)(Math.random()*buildAbleWords.size()));
	}
	public greedyMoveFinder(String[][] _game, ArrayList<Point> _buildLocations, String _rack, Main _main, int[][] _bonus, WordFinder _find,Scrabby _scrab){
		scrab=_scrab;

		buildAbleWords = new ArrayList<Move>();
		game = _game;
		bonus = _bonus;
		main = _main;
		rack = _rack;
		find = _find;
		buildLocations = _buildLocations;

		for(int row = 0; row<15;row++){
			getWordThatCanBeBuiltOnCol(row);
			getWordThatCanBeBuiltOnRow(row);
		}
		Collections.sort(buildAbleWords);
		for(Move p : buildAbleWords){
			System.out.println(p);
		}
		System.out.println(""+buildAbleWords.size());
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
		words.add("");
		List<String> test = find.getFastFilter().filter(rack.toLowerCase(), words.toArray(new String[words.size()]));
		for(String word : test){
			tryToMatchVertical(word,x);
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
		words.add("");
		List<String> test = find.getFastFilter().filter(rack.toLowerCase(), words.toArray(new String[words.size()]));
		for(String word : test){
			tryToMatchHorizontal(word,x);
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
			if(i-1>=0 && game[i-1][x]!=null){
				continue;
			}

			for(int c = 0; c<word.length();c++){
				int type = bonus[x][i+c];
				int tempMult = 1;

				if(x == 7 && (c+i)==7 && game[i+c][x]==null){
					possible = true;
				}

				if(x<14)
					if(game[i+c][x+1]!=null){
						possible=true;
					}
				if(x>0)
					if(game[i+c][x-1]!=null){
						possible=true;
					}	

				if(game[i+c][x]==null){
					//					word.charAt(c)
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
				ArrayList<String> createdWords = new ArrayList<String>();
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
							createdWords.add(tempWord.toLowerCase());
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
				String usedLetters="";
				for(int ic = 0; ic<word.length();ic++){
					if(game[ic+i][x]==null){
						usedLetters +=word.charAt(ic);
					}
				}
				if(usedLetters.length()==7){
					points += 40;
				}
				if(find.WordCanBeBuiltFromSourceLetters(usedLetters.toLowerCase(),rack.toLowerCase())){
					Move mov = new Move(points, word, x,i, true);
					mov.setWords(createdWords.toArray(new String[createdWords.size()]));
					if(!buildAbleWords.contains(mov)){
						buildAbleWords.add(mov);
					}
					if(scrab!=null){
						ArrayList<String> list=MartinTest.testCombinations(usedLetters, rack);
						for(String s:list){
							mov = new Move(scrab, s, x,i, true);
							mov.setWords(createdWords.toArray(new String[createdWords.size()]));
							System.out.println("jag:"+mov);
							if(!buildAbleWords.contains(mov)){
								buildAbleWords.add(mov);
							}
						}	
					}



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
			if(i-1>=0 && game[x][i-1]!=null){
				continue;
			}


			for(int c = 0; c<word.length();c++){
				int type = bonus[i+c][x];
				int tempMult = 1;

				if(x == 7 && (c+i)==7 && game[x][i+c]==null){
					possible = true;
				}

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
				ArrayList<String> createdWords = new ArrayList<String>();
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
							createdWords.add(tempWord.toLowerCase());
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
				String usedLetters="";
				for(int ic = 0; ic<word.length();ic++){
					if(game[x][ic+i]==null){
						usedLetters +=word.charAt(ic);
					}
				}
				if(usedLetters.length()==7){
					points += 40;
				}
				if(find.WordCanBeBuiltFromSourceLetters(usedLetters.toLowerCase(),rack.toLowerCase())){
					Move mov = new Move(points, word, i,x, false);
					mov.setWords(createdWords.toArray(new String[createdWords.size()]));
					if(!buildAbleWords.contains(mov)){
						buildAbleWords.add(mov);
					}
					if(scrab!=null){
						ArrayList<String> list=MartinTest.testCombinations(usedLetters, rack);
						for(String s:list){
							mov = new Move(scrab, s, i,x, true);
							mov.setWords(createdWords.toArray(new String[createdWords.size()]));
							System.out.println("jag:"+mov);
							if(!buildAbleWords.contains(mov)){
								buildAbleWords.add(mov);
							}
						}	
					}
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
