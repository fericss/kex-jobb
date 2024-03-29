import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;


public class Main extends JFrame{
	private String user;
	private String username;
	private String password;
	String cookie;
	HttpURLConnection connection = null;
	URL serverAddress = null;
	OutputStreamWriter wr = null;
	BufferedReader rd  = null;
	StringBuilder sb = null;
	String ruleset;
	
	
	
	public static boolean DEBUG = false;
	private boolean DUMB = true;
	
	 
	
	private String rack;
	private boolean end_game;
	private int current_player;
	private int player;
	private boolean[][] wildcards;
	private HashMap<String,String> smart_score;
	private HashMap<String,String> dumb_score;
	//    String line = null;
	private boolean once = true;
	greedyMoveFinder best;

	String[][] game;
	WordFinder find;
	private String score;

	public Main() throws Exception{
		user = "fredric.ericsson@gmail.com";
		username = "Lather";
		password = "icpmwq";
		//		//		
		///
		if(DUMB){
			user = "fericss@gmail.com";
			username = "lather2";
			password = "lather";
		}

		load_data();
		boolean running = true;
		cookie = getCookie();
//<<<<<<< .mine
		while(running){
			if(DUMB){
				handle_invites();
			}
			boolean active_game = false;
			running = !DEBUG;
			List<String> gamesIDList = getGames();
			for(String g : gamesIDList){
				wildcards = new boolean[15][15];
				game = new String[15][15];
				String _gameInfo = getGame(g);
				parseTiles(_gameInfo);
				if(!end_game){
					active_game = true;
				}
				else{
					if(DUMB){
						dumb_score.put(g, score);
					}
					else{
						smart_score.put(g, score);
					}
				}

				if(!end_game && current_player == player){
					int[][] bonus = getGameBoard(_gameInfo);



					find = new WordFinder();
					ArrayList<Point> buildLocations = new ArrayList<Point>();
					List<String> bla; 


					if(DEBUG){
						System.out.println("Rack: "+rack);
						printBoard(gameToBoard(game));
					}
					//		rack = "whehe.";
					GameInfo gi=new GameInfo(game,bonus ,wildcards,rack);

					long time = System.currentTimeMillis();
					best = new greedyMoveFinder(game,buildLocations,rack,this, bonus, find); 
					System.out.println(g+" "+(System.currentTimeMillis()-time)+" "+best.buildAbleWords.size());
					
					if(DEBUG){
						System.out.println("Time: "+(System.currentTimeMillis()-time)+" milisec");
					}
					if(best.getBestMove()==null){
						this.pass(Integer.parseInt(g));
					}
					else{
						if(DUMB){
							this.playMove(best.getRandomMove(), Integer.parseInt(g));
//							this.playMove(best.getBestMove(), Integer.parseInt(g));

						}
						else{
							this.playMove(best.getBestMove(), Integer.parseInt(g));
							if(best.getBestMove().word.length()>9){
								System.exit(0);
							}
						}
					}
				}
				if(!DUMB && !active_game){
//					if(once){
//						once = false;
						new_game("lather2");
//					}
				}

			}
			write_data();
			Thread.sleep(500);
		}
	}
	
	public void testSlowFilterAndStuff(GameInfo gi,SlowFilter sf){
//		//Test of the new points method
//		for(Move m:gmf.buildAbleWords){
//			int points=gi.testPoints(m);
//			if(points!=m.points){
//				System.out.println("aaaaaiiiiiii!!!!!");
//				System.out.println("Move: "+m);
//				System.out.println("points method: "+points);
//				System.out.println("******************************Error above***********************************");
//			}
//		}
		
//		//print the board after the move
//		for(Move m:gmf.buildAbleWords){
//			System.out.println(rack);
//			System.out.println(m);
//			gi.newGameInfo("", m);
//		}
		
		
		
		//is done on this players every turn
		System.out.println("test of slowfilter");
		long time = System.currentTimeMillis();
		sf.reset();
		for(int rowIndex=0;rowIndex<15;rowIndex++){
			sf.slowFilterUpdate(rowIndex, false, find, gi, null);
		}
		for(int rowIndex=0;rowIndex<15;rowIndex++){
			sf.slowFilterUpdate(rowIndex, true, find, gi, null);
		}
		Collections.sort(sf.moves);
		System.out.println("slowfilter time: "+(System.currentTimeMillis()-time)+" milisec");
		
		System.out.println("slowfilter moves: ");
//		for(Move m:sf.moves){
//			System.out.println(m);
//		}
		System.out.println(sf.moves.size());
		
		System.out.println("How many times at each filter: ");
		System.out.println("p1:"+sf.p1);
		System.out.println("p2:"+sf.p2);
		System.out.println("p3:"+sf.p3);
		System.out.println("p4:"+sf.p4);
	}
	
	private void write_data() throws Exception {
		FileWriter fstream = new FileWriter("data_"+username);
		BufferedWriter out = new BufferedWriter(fstream);
		if(DUMB){
			boolean first = true;
			for(String s : dumb_score.keySet()){
				out.write((first ? "": "\n")+s+" "+dumb_score.get(s));
				first = false;
			}
		}
		else{
			boolean first = true;
			for(String s : smart_score.keySet()){
				out.write((first ? "": "\n")+s+" "+smart_score.get(s));
				first = false;
			}
		}
		//Close the output stream
		out.close();

	}
	private void load_data() throws Exception {
		// TODO Auto-generated method stub
		if(DUMB){
			dumb_score = new HashMap<String, String>();
			FileReader fr = new FileReader("data_"+username);
			BufferedReader in = new BufferedReader(fr);
			String line;
			while((line = in.readLine())!=null){
				String read[] = line.split(" ");
				dumb_score.put(read[0], read[1]);
			}
			in.close();
		}
		else{
			smart_score = new HashMap<String, String>();
			FileReader fr = new FileReader("data_"+username);
			BufferedReader in = new BufferedReader(fr);
			String line;
			while((line = in.readLine())!=null){
				String read[] = line.split(" ");
				smart_score.put(read[0], read[1]);
			}
		}

	}
	private void accept_invite(String id) throws Exception{
		System.out.println(id);
		serverAddress = new URL("http://game06.wordfeud.com/wf/invite/"+id+"/accept/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Accept-Encoding","text");
		connection.addRequestProperty("Connection","Keep-Alive");
		connection.addRequestProperty("Cookie", cookie);
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");

		PrintStream utdata = new PrintStream(connection.getOutputStream());
		utdata.println("[]");
		connection.connect();

		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;//rd.readLine();
		if(DEBUG){
			while ((line = rd.readLine()) != null)
				//		        {
				System.out.println(line);
		}
	}
	private void handle_invites() throws Exception{
		serverAddress = new URL("http://game06.wordfeud.com/wf/user/status/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Accept-Encoding","text");
		connection.addRequestProperty("Connection","Keep-Alive");
		connection.addRequestProperty("Cookie", cookie);
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");

		PrintStream utdata = new PrintStream(connection.getOutputStream());
		utdata.println("[]");
		connection.connect();

		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		line = rd.readLine();

		//		if(line.contains("invite_received")){
		//
		//		}
		int pos = 0;
		while(line.charAt(pos)!=']'){
			pos++;
			if(line.charAt(pos)=='"'
					&& line.charAt(pos-1)=='d'
					&& line.charAt(pos-2)=='i'
					&& line.charAt(pos-3)=='"'){
				pos +=3;
				int pos2 = pos;
				while(line.charAt(pos2)!='}'){
					pos2++;
				}
				accept_invite(line.substring(pos,pos2));
			}
		}

		//		while ((line = rd.readLine()) != null)
		if(DEBUG){

			//		        {
			System.out.println(line);
		}

	}
	private void new_game(String str) throws Exception{
		serverAddress = new URL("http://game06.wordfeud.com/wf/invite/new/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Accept-Encoding","text");
		connection.addRequestProperty("Connection","Keep-Alive");
		connection.addRequestProperty("Cookie", cookie);
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");
		//=======
		//		List<String> gamesIDList = getGames();
		//		String _gameInfo = getGame(gamesIDList.get(2));
		//		int[][] bonus = getGameBoard(_gameInfo);
		//		parseTiles(_gameInfo);
		//		
		//		
		//		WordFinder find = new WordFinder();
		//		ArrayList<Point> buildLocations = new ArrayList<Point>();
		//		List<String> bla; 
		//		
		//		
		//>>>>>>> .r135


		PrintStream utdata = new PrintStream(connection.getOutputStream());
		utdata.println("{\"board_type\":\"normal\",\"ruleset\" :0, \"invitee\":\""+str+"\"}");
		connection.connect();
		//=======
		//		System.out.println("Rack: "+rack);
		//		printBoard(gameToBoard(game));
		////		rack = "whehe.";
		//		GameInfo gi=new GameInfo(game,bonus ,wildcards,rack);
		//		
		//		long time = System.currentTimeMillis();
		//		greedyMoveFinder gmf=new greedyMoveFinder(game,buildLocations,rack,this, bonus, find); 
		//		System.out.println("Time: "+(System.currentTimeMillis()-time)+" milisec");
		//>>>>>>> .r135
//=======
//		List<String> gamesIDList = getGames();
//		String _gameInfo = getGame(gamesIDList.get(0));
//		int[][] bonus = getGameBoard(_gameInfo);
//		parseTiles(_gameInfo);
//		
//		
//		WordFinder find = new WordFinder();
//		ArrayList<Point> buildLocations = new ArrayList<Point>();
//		List<String> bla; 
//		
//		
//>>>>>>> .r137

//<<<<<<< .mine
		//		//Test of the new points method
		//		for(Move m:gmf.buildAbleWords){
		//			int points=gi.testPoints(m);
		//			if(points!=m.points){
		//				System.out.println("aaaaaiiiiiii!!!!!");
		//				System.out.println("Move: "+m);
		//				System.out.println("points method: "+points);
		//				System.out.println("******************************Error above***********************************");
		//			}
		//		}
		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;//rd.readLine();
		if(DEBUG){
			while ((line = rd.readLine()) != null)
				//		        {
				System.out.println(line);
		}

	}
	private void pass(int id) throws Exception {
		serverAddress = new URL("http://game06.wordfeud.com/wf/game/"+id+"/pass/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Accept-Encoding","text");
		connection.addRequestProperty("Connection","Keep-Alive");
		connection.addRequestProperty("Cookie", cookie);
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");

		PrintStream utdata = new PrintStream(connection.getOutputStream());
		utdata.println("[]");
		connection.connect();

		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;//rd.readLine();
		if(DEBUG){
			while ((line = rd.readLine()) != null)
				//		        {
				System.out.println(line);
		}
//=======
//		System.out.println("Rack: "+rack);
//		printBoard(gameToBoard(game));
////		rack = "whehe.";
//		rack=".......";
//		GameInfo gi=new GameInfo(game,bonus ,wildcards,rack);
//		
//		long time = System.currentTimeMillis();
//		greedyMoveFinder gmf=new greedyMoveFinder(game,buildLocations,rack,this, bonus, find); 
//		System.out.println("Greedy time: "+(System.currentTimeMillis()-time)+" milisec");
//		
//
//		
////		//Test of the new points method
////		for(Move m:gmf.buildAbleWords){
////			int points=gi.testPoints(m);
////			if(points!=m.points){
////				System.out.println("aaaaaiiiiiii!!!!!");
////				System.out.println("Move: "+m);
////				System.out.println("points method: "+points);
////				System.out.println("******************************Error above***********************************");
////			}
////		}
//>>>>>>> .r137
		
//		//print the board after the move
//		for(Move m:gmf.buildAbleWords){
//			System.out.println(rack);
//			System.out.println(m);
//			gi.newGameInfo("", m);
//		}
		
		
		
		
//		//should only be done once per wordlist
//		SlowFilter sf=new SlowFilter(find.getWordlist());
//		
//		//is done on this players every turn
//		System.out.println("test of slowfilter");
//		time = System.currentTimeMillis();
//		sf.reset();
//		for(int rowIndex=0;rowIndex<15;rowIndex++){
//			sf.slowFilterUpdate(rowIndex, false, find, gi, null);
//		}
//		for(int rowIndex=0;rowIndex<15;rowIndex++){
//			sf.slowFilterUpdate(rowIndex, true, find, gi, null);
//		}
//		Collections.sort(sf.moves);
//		System.out.println("slowfilter time: "+(System.currentTimeMillis()-time)+" milisec");
//		
//		System.out.println("slowfilter moves: ");
////		for(Move m:sf.moves){
////			System.out.println(m);
////		}
//		System.out.println(sf.moves.size());
//		
//		System.out.println("How many times at each filter: ");
//		System.out.println("p1:"+sf.p1);
//		System.out.println("p2:"+sf.p2);
//		System.out.println("p3:"+sf.p3);
//		System.out.println("p4:"+sf.p4);
	}
	
	public static void printBoard(char[][] board){
		int x=0;
		System.out.println(" 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14");
		for(char[] b:board){
			System.out.println(Arrays.toString(b)+" "+(x));
			x++;
		}
	}
	public char[][] gameToBoard(final String[][] game){
		return gameToBoard(game," ");
	}

	/**
	 * 
	 * @param game
	 * @param emptyLetter
	 * @return
	 */
	private char[][] gameToBoard(final String[][] game,final String emptyLetter){
		char[][] board=new char[game.length][game[0].length];
		for(int x=0;x<game.length;x++){
			for(int y=0;y<game[0].length;y++){
				String letter=game[x][y];
				if(letter!=null){ letter=letter.trim(); }
				if(letter!=null && letter.length()>1){
					System.out.println("BLAAAAAA");
				}
				//				if(letter==null || letter.equals(emptyLetter)){
				if( letter==null || !letter.matches("[a-zA-Z]") ){
					board[x][y]=' ';
				} else {
					board[x][y]=letter.toLowerCase().charAt(0);
				}
			}
		}
		return board;
	}
	private int[][] getGameBoard(String gameInfo) throws Exception {


		boolean done = false;
		int pos = 0;
		while(!done){
			pos++;
			if(gameInfo.charAt(pos)=='"'
					&&gameInfo.charAt(pos-1)=='d'
					&&gameInfo.charAt(pos-2)=='r'
					&&gameInfo.charAt(pos-3)=='a'
					&&gameInfo.charAt(pos-4)=='o'
					&&gameInfo.charAt(pos-5)=='b'
					&&gameInfo.charAt(pos-6)=='"'){


				done = true;
				//				System.out.println("lol"+pos);
				break;

			}

		}
		done = false;
		int endpos = pos;
		while(!done){
			endpos++;
			if(gameInfo.charAt(endpos)==','){
				done=true;
				break;
			}
		}
		//		System.out.println(gameInfo);
		//		System.out.println(""+pos+" "+endpos);
		String boardID = gameInfo.substring(pos+3,endpos);
		//		
		//		System.out.println(boardID);
		ArrayList<String> gameID = new ArrayList<String>();
		serverAddress = new URL("http://game03.wordfeud.com/wf/board/"+boardID+"/");
		//        PrintStream utdata = new PrintStream(connection.getOutputStream());
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Cookie", cookie);
		connection.connect();
		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line= rd.readLine();
		//        while ((line = rd.readLine()) != null)
		//        {
		if(DEBUG){
			System.out.println(line);
		}


		done = false;
		pos = 0;
		while(!done){
			pos++;
			if(line.charAt(pos)=='"'
					&&line.charAt(pos-1)=='d'
					&&line.charAt(pos-2)=='r'
					&&line.charAt(pos-3)=='a'
					&&line.charAt(pos-4)=='o'
					&&line.charAt(pos-5)=='b'
					&&line.charAt(pos-6)=='"'){


				done = true;
				//				System.out.println("lol"+pos);
				break;

			}

		}
		pos += 5;
		int[][] retThis = new int [15][15];
		for(int i = 0; i<15;i++){
			for(int i2 = 0; i2<15;i2++){
				//				System.out.print(line.charAt(pos));
				retThis[i2][i] = Integer.parseInt(""+line.charAt(pos));
				pos += 3;
			}
			pos += 2;
		}

		return retThis;
	}
	//	private int calcPoints(String s){
	//		int word_points = 0;
	//		for(char c : s.toCharArray()){
	//			word_points+=points.get(""+((char)(c-32)));
	//		}
	//		return word_points;
	//	}
	//	List<String> sortByPoints(List<String> bla) {
	//		// this needs fixing, it really sucks and does not work
	//		List<String> returnList = new ArrayList<String>();
	//		for(String s : bla){
	//			returnList.add(""+calcPoints(s)+" "+s);
	//		}
	//		Collections.sort(returnList, new Comparator<String>(){
	//
	//			@Override
	//			public int compare(String arg0, String arg1) {
	//				int one =Integer.parseInt(arg0.split(" ")[0]);
	//				int two =Integer.parseInt(arg1.split(" ")[0]);
	//
	//				return one>two ? 0 : one==two ? 0 : 1;
	//			}
	//
	//		}); 
	//		//		String temp[] = (String[])returnList.toArray();
	//		//		Arrays.sort(temp);
	//		//		ArrayList.
	//		//		returnList.
	//		return returnList;
	//	}
	private void playMove(Move mv, int id) throws Exception{

		serverAddress = new URL("http://game06.wordfeud.com/wf/game/"+id+"/move/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Accept-Encoding","identity");
		connection.addRequestProperty("Connection","close");
		connection.addRequestProperty("Cookie", cookie);
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");

		PrintStream utdata = new PrintStream(connection.getOutputStream());


		String str = "{\"move\": [";

		if(mv.vertical){
			int length = mv.word.length();

			for(int i = mv.y; i < mv.y+length; i++){
				if(game[i][mv.x]==null){
					if(str.charAt(str.length()-1)!='['){
						if(str.charAt(str.length()-2)!=','){
							str += ", ";
						}
					}
					str += "["+mv.x+", "+i+", \""+(""+mv.word.charAt(i-mv.y)).toUpperCase()+"\", "+
							(rack.contains((""+mv.word.charAt(i-mv.y)).toUpperCase()) ? "false" : "true")
							+"]";
					if(rack.contains((""+mv.word.charAt(i-mv.y)).toUpperCase())){
						rack = WordFinder.Remove(rack.indexOf((""+mv.word.charAt(i-mv.y)).toUpperCase()), rack);
					}
					if(i==mv.y+length-1 || (game[i+1][mv.x]!=null)){

					}
					else{
						str += ", ";
					}
				}
			}

		}
		else{
			int length = mv.word.length();

			for(int i = mv.x; i < mv.x+length; i++){
				if(game[mv.y][i]==null){
					if(str.charAt(str.length()-1)!='['){
						if(str.charAt(str.length()-2)!=','){
							str += ", ";
						}
					}
					str += "["+i+", "+mv.y+", \""+(""+mv.word.charAt(i-mv.x)).toUpperCase()+"\", "+
							(rack.contains((""+mv.word.charAt(i-mv.x)).toUpperCase()) ? "false" : "true")
							+"]";
					if(rack.contains((""+mv.word.charAt(i-mv.x)).toUpperCase())){
						rack = WordFinder.Remove(rack.indexOf((""+mv.word.charAt(i-mv.x)).toUpperCase()), rack);
					}
					if(i==mv.x+length-1 || (game[mv.y][i+1]!=null)){

					}
					else{
						str += ", ";
					}
				}
			}

		}
		str += "]";
		str += ", \"ruleset\": "+ruleset+", \"words\": [\""+mv.word.toUpperCase()+ (mv.words.length!= 0 ? "\", " : "\"");

		int counter = 0;
		int totalWords = mv.words.length;
		for(String s : mv.words){
			counter++;
			str += "\""+s.toUpperCase()+"\"";
			if(counter != totalWords){
				str += ", ";
			}

		}
		str += "]}";
		if(DEBUG){
			System.out.println("Sending: \n"+str);
		}
		utdata.println(str);
		if(DEBUG){
			System.out.println(connection);
		}
		
		connection.connect();


		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;//rd.readLine();
//		if(DEBUG){
			while ((line = rd.readLine()) != null){
				if(DEBUG){
					System.out.println(line);
					
				}
				if(line.contains("illegal_word")){
					best.buildAbleWords.remove(0);  
					System.out.println("Illegal word: "+str);
					this.playMove(best.getBestMove(), id);
				}
			}
				//		        {
//				System.out.println(line);
//		}

		//		        for(String bla : connection.getHeaderFields().keySet()){
		//		            for(String s : bla){
		//		                System.out.println(bla + connection.geth);
		//		            }
		//		           
		//		        }
	}
	private void printTiles() {
		for(String[] s : game){
			for(String i : s){
				System.out.print(""+(i==null ? "\t" : "\t"+i));
			}
			System.out.println();
		}
		System.out.print("Rack: ");
		System.out.println(getRack());
	}

	private void parseTiles(String gameInfo) {
		int pos = 0;
		while(gameInfo.charAt(pos)!='['){
			pos++;
		}
		boolean done = false;
		if(gameInfo.charAt(pos+1)==']'){
			done = true;
		}
		while(!done){


			while(gameInfo.charAt(pos)!=','){
				pos++;
			}
			int l1;
			if(gameInfo.charAt(pos-2)>'0' && gameInfo.charAt(pos-2)<'9'){
				l1 = Integer.parseInt((""+gameInfo.charAt(pos-2)+""+gameInfo.charAt(pos-1)));
			}else{
				l1 = gameInfo.charAt(pos-1)-48;
			}
			pos++;
			while(gameInfo.charAt(pos)!=','){
				pos++;
			}
			int l2;
			if(gameInfo.charAt(pos-2)>'0' && gameInfo.charAt(pos-2)<'9'){
				l2 = Integer.parseInt((""+gameInfo.charAt(pos-2)+""+gameInfo.charAt(pos-1)));
			}else{
				l2 = gameInfo.charAt(pos-1)-48;
			}
			pos++;
			while(gameInfo.charAt(pos)!=','){
				pos++;
			}
			String l3 = String.valueOf(gameInfo.charAt(pos-2));
			//						System.out.println(""+l1+" "+l2+" "+l3);
			game[l2][l1] = ""+l3;
			wildcards[l2][l1] = Boolean.parseBoolean(gameInfo.substring( pos+2, pos+6));
			if(gameInfo.charAt(pos+7)==']'&& gameInfo.charAt(pos+8)==']'){
				done = true;
				//                break;
			}
			if(gameInfo.charAt(pos+6)==']'&& gameInfo.charAt(pos+7)==']'){
				done = true;
				//                break;
			}
			pos = pos + 11;
		}

		done = false;
		while(!done){
			pos++;
			//            System.out.println(pos);
			if(gameInfo.charAt(pos)=='"' 
					&& gameInfo.charAt(pos-1)=='e'
					&& gameInfo.charAt(pos-2)=='m'
					&& gameInfo.charAt(pos-3)=='a'
					&& gameInfo.charAt(pos-4)=='g'
					&& gameInfo.charAt(pos-5)=='_'
					&& gameInfo.charAt(pos-6)=='d'
					&& gameInfo.charAt(pos-7)=='n'
					&& gameInfo.charAt(pos-8)=='e'
					&& gameInfo.charAt(pos-9)=='"'){
				done = true;
				break;
			}
		}
		pos+=3;
		end_game = gameInfo.charAt(pos)!='0';


		done = false;
		while(!done){
			pos++;
			//            System.out.println(pos);
			if(gameInfo.charAt(pos)=='"' 
					&& gameInfo.charAt(pos-1)=='r'
					&& gameInfo.charAt(pos-2)=='e'
					&& gameInfo.charAt(pos-3)=='y'
					&& gameInfo.charAt(pos-4)=='a'
					&& gameInfo.charAt(pos-5)=='l'
					&& gameInfo.charAt(pos-6)=='p'
					&& gameInfo.charAt(pos-7)=='_'
					&& gameInfo.charAt(pos-8)=='t'
					&& gameInfo.charAt(pos-9)=='n'
					&& gameInfo.charAt(pos-10)=='e'
					&& gameInfo.charAt(pos-11)=='r'
					&& gameInfo.charAt(pos-12)=='r'
					&& gameInfo.charAt(pos-13)=='u'
					&& gameInfo.charAt(pos-14)=='c'
					&& gameInfo.charAt(pos-15)=='"'){
				done = true;
				break;
			}
		}
		pos+=3;
		current_player = Integer.parseInt(""+gameInfo.charAt(pos));


		done = false;
		while(!done){
			pos++;
			for(int i = 0; i<username.length();i++){
				if(gameInfo.charAt(pos-i)!=username.charAt(username.length()-i-1)){
					break;
				}
				if(i == username.length()-1){
					done = true;
				}
			}
		}
		pos+=16;
		//		System.out.println(""+gameInfo.charAt(pos));
		player = Integer.parseInt(""+gameInfo.charAt(pos));

		pos += 12;

		int tpos = pos;
		while(gameInfo.charAt(tpos)!=','){
			tpos++;
		}
		score = ""+gameInfo.substring(pos,tpos);
		//        System.out.println("bla");
		//                while(gameInfo.charAt(pos)!='"' && gameInfo.charAt(pos-1)!='k'
		//                        && gameInfo.charAt(pos-2)!='c'
		//                            && gameInfo.charAt(pos-3)!='a'
		//                                && gameInfo.charAt(pos-4)!='r'
		//                                    && gameInfo.charAt(pos-5)!='"'){
		//                    pos++;
		//                    System.out.println(pos);
		//                }
		//                pos+=10;
		done = false;
		setRack("");

		while(!done){
			pos++;
			//            System.out.println(pos);
			if(gameInfo.charAt(pos)=='"' && gameInfo.charAt(pos-1)=='k'
					&& gameInfo.charAt(pos-2)=='c'
					&& gameInfo.charAt(pos-3)=='a'
					&& gameInfo.charAt(pos-4)=='r'
					&& gameInfo.charAt(pos-5)=='"'){
				done = true;
				break;
			}
		}
		done = false;
		while(!done){
			pos++;
			if(gameInfo.charAt(pos)>='A' && gameInfo.charAt(pos)<='Z' && gameInfo.charAt(pos-1)=='"' && gameInfo.charAt(pos+1)=='"'){
				//                System.out.println(""+gameInfo.charAt(pos));
				setRack(getRack()+gameInfo.charAt(pos));
			}
			if(gameInfo.charAt(pos)=='"'&&gameInfo.charAt(pos+1)=='"'){
				setRack(getRack()+".");
			}
			if(gameInfo.charAt(pos)==']'&& gameInfo.charAt(pos+1)=='}'){
				done = true;
				break;
			}
		}

		done = false;
		while(!done){
			pos++;
			//            System.out.println(pos);
			if(gameInfo.charAt(pos)=='"' && gameInfo.charAt(pos-1)=='t'
					&& gameInfo.charAt(pos-2)=='e'
					&& gameInfo.charAt(pos-3)=='s'
					&& gameInfo.charAt(pos-4)=='e'
					&& gameInfo.charAt(pos-5)=='l'
					&& gameInfo.charAt(pos-6)=='u'
					&& gameInfo.charAt(pos-7)=='r'
					&& gameInfo.charAt(pos-8)=='"'){
				pos += 3;
				ruleset = "" + gameInfo.charAt(pos);
				done = true;
				break;
			}
		}



	}
	private List<String> getGames() throws Exception {
		ArrayList<String> gameID = new ArrayList<String>();
		serverAddress = new URL("http://game03.wordfeud.com/wf/user/games/");
		//        PrintStream utdata = new PrintStream(connection.getOutputStream());
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Cookie", cookie);
		connection.connect();
		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line= rd.readLine();
		//        while ((line = rd.readLine()) != null)
		//        {
		if(DEBUG){
			System.out.println(line);
		}
		//        }
		boolean done = false;
		int pos = 0;
		while(!done){
			pos++;
			if(line.charAt(pos)=='}'&&line.charAt(pos-1)=='}'&&line.charAt(pos-2)==']'&&line.charAt(pos-3)=='}'){
				done = true;
				//				System.out.println("lol"+pos);
				break;

			}
			if(line.charAt(pos)=='"'&&line.charAt(pos-1)=='d'&&line.charAt(pos-2)=='i'&&line.charAt(pos-3)=='"'
					&&line.charAt(pos-10)=='e'&&line.charAt(pos-11)=='m'&&line.charAt(pos-12)=='a'&&line.charAt(pos-13)=='g'){

				//			while(!line.substring(pos,pos+4).equals("\"id\"")){
				//				pos++;
				//			}

				pos = pos+3;
				//        while(line.charAt(pos)<'0' &&  line.charAt(pos)>'9'){
				//            pos++;
				//        }
				int startPos = pos;
				while(line.charAt(pos)!=','){
					pos++;
				}
				//        System.out.println(line.substring(startPos,pos));
				gameID.add(line.substring(startPos,pos));
				//				System.out.println("lol "+line.substring(startPos,pos) );
			}
		}

		return gameID;

		//        System.out.println(str);
		//        utdata.println(str);

	}
	private String getGame(String s) throws Exception {
		serverAddress = new URL("http://game06.wordfeud.com/wf/game/"+s+"/");
		//        PrintStream utdata = new PrintStream(connection.getOutputStream());
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Cookie", cookie);
		connection.connect();
		rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = rd.readLine();
		//        while ((line = rd.readLine()) != null)
		//        {
		if(DEBUG){
			System.out.println(line);
		}
		//        }
		return line;

		//        System.out.println(str);
		//        utdata.println(str);

	}
	public static void main(String args[]) throws Exception{
		new Main();
	}
	public String getCookie() throws Exception{
		String salt = "JarJarBinks9";
		//        new Main();
		serverAddress = new URL("http://game03.wordfeud.com/wf/user/login/email/");
		//set up out communications stuff
		connection = null;

		//Set up the initial connection
		connection = (HttpURLConnection)serverAddress.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);
		connection.addRequestProperty("Content-type", "application/json");
		connection.addRequestProperty("Host","game03.wordfeud.com");
		connection.addRequestProperty("Connection","Keep-Alive");
		connection.addRequestProperty("User-Agent","WebFeudClient/1.2.8 (Android 2.2.3)");

		PrintStream utdata = new PrintStream(connection.getOutputStream());


		String pwd = SHA1(password+salt);

		String str = "{\"password\": \""+pwd+"\""+","+
				"\"email\": \""+user+"\"}";
		if(DEBUG){
			System.out.println(str);
		}
		utdata.println(str);
		//        System.out.println(connection);
		connection.connect();

		//        for(String bla : connection.getHeaderFields().keySet()){
		////            for(String s : bla){
		////                System.out.println(bla);
		////            }
		//           
		//        }
		//        connection.
		String cookie = connection.getHeaderField("Set-Cookie");
		//        System.out.println(connection.getHeaderField("Set-Cookie"));
		//        rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		//        while ((line = rd.readLine()) != null)
		//        {
		//            System.out.println(line);
		//        }
		return cookie;
	}

	private static String convToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException,
	UnsupportedEncodingException  {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return convToHex(sha1hash);
	}
	public void setRack(String rack) {
		this.rack = rack;
	}
	public String getRack() {
		return rack;
	}
	
	public class GamesData{
		HashMap<String, GameData> games=new HashMap<String, GameData>();
		
		public void addData(String gameId,Data d){
			GameData gd=games.get(gameId);
			if(gd==null){
				gd=new GameData();
				games.put(gameId,gd);
			}
			gd.list.add(d);
		}
		
		public List<GameData> getGames(){
			return new ArrayList<GameData>(games.values());
		}
		
		/**
		 * res[turn][type]
		 * type=0 is time
		 * type=1 is moves
		 * type=2 is points
		 * @param games
		 * @return
		 */
		public double[][] getAvarages(List<GameData> games){
			int maxLength=0;
			for(int i=0;i<games.size();i++){
				if(games.get(i).list.size()>maxLength){
					maxLength=games.get(i).list.size();
				}
			}

			double[][] res=new double[maxLength][3];

			for(int turn=0;turn<maxLength;turn++){
				int nr=0;
				int total=0;
				for(int i=0;i<games.size();i++){
					if(turn<games.get(i).list.size()){
						nr++;
						//calculate total
						res[turn][0]=games.get(i).list.get(turn).time;
						res[turn][1]=games.get(i).list.get(turn).moves;
						res[turn][2]=games.get(i).list.get(turn).points;
					}
				}
				//calculate average
				res[turn][0]/=(double)nr;
				res[turn][1]/=(double)nr;
				res[turn][2]/=(double)nr;
			}
			return res;
		}
	}
	
	public class GameData{
		ArrayList<Data> list=new ArrayList<Data>();
	}
	
	public class Data{
		final int moveNr;
		final long time;
		final int moves;
		final int points;
		public Data(int _moveNr,long _time,int _moves,int _points){
			moveNr=_moveNr;
			time=_time;
			moves=_moves;
			points=_points;
		}
	}
	
	public void test(int gameId) throws Exception{
		user = "fredric.ericsson@gmail.com";
		username = "Lather";
		password = "icpmwq";
		load_data();
		cookie = getCookie();
		WordFinder wf=new WordFinder();
		GameInfo gi=getLetestGameInfo(gameId, wf);
		SlowFilter sf=new SlowFilter(wf.getWordlist());
		testSlowFilterAndStuff(gi,sf,wf);
	}
	
	public GameInfo getLetestGameInfo(int gameId,WordFinder find) throws Exception{
		List<String> gamesIDList = getGames();
		String g=gamesIDList.get(gameId);
		wildcards = new boolean[15][15];
		game = new String[15][15];
		String _gameInfo = getGame(g);
		parseTiles(_gameInfo);
		int[][] bonus = getGameBoard(_gameInfo);
		GameInfo gi=new GameInfo(game,bonus ,wildcards,rack);
		return gi;
	}
	
	public void testSlowFilterAndStuff(GameInfo gi,SlowFilter sf,WordFinder find){
//		//Test of the new points method
//		for(Move m:gmf.buildAbleWords){
//			int points=gi.testPoints(m);
//			if(points!=m.points){
//				System.out.println("aaaaaiiiiiii!!!!!");
//				System.out.println("Move: "+m);
//				System.out.println("points method: "+points);
//				System.out.println("******************************Error above***********************************");
//			}
//		}
		
//		//print the board after the move
//		for(Move m:gmf.buildAbleWords){
//			System.out.println(rack);
//			System.out.println(m);
//			gi.newGameInfo("", m);
//		}
		
		
		
		//is done on this players every turn
		System.out.println("test of slowfilter");
		long time = System.currentTimeMillis();
		sf.reset();
		for(int rowIndex=0;rowIndex<15;rowIndex++){
			sf.slowFilterUpdate(rowIndex, false, find, gi, null);
		}
		for(int rowIndex=0;rowIndex<15;rowIndex++){
			sf.slowFilterUpdate(rowIndex, true, find, gi, null);
		}
		Collections.sort(sf.moves);
		System.out.println("slowfilter time: "+(System.currentTimeMillis()-time)+" milisec");
		
		System.out.println("slowfilter moves: ");
//		for(Move m:sf.moves){
//			System.out.println(m);
//		}
		System.out.println(sf.moves.size());
		
		System.out.println("How many times at each filter: ");
		System.out.println("p1:"+sf.p1);
		System.out.println("p2:"+sf.p2);
		System.out.println("p3:"+sf.p3);
		System.out.println("p4:"+sf.p4);
	}

}