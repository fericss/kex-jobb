import java.awt.Point;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	private String password;
	String cookie;
	HttpURLConnection connection = null;
	URL serverAddress = null;
	OutputStreamWriter wr = null;
	BufferedReader rd  = null;
	StringBuilder sb = null;
	private String rack;
	private boolean[][] wildcards;

	//    String line = null;

	String[][] game;


	public Main() throws Exception{

		wildcards = new boolean[15][15];
		game = new String[15][15];
		cookie = getCookie();
		List<String> gamesIDList = getGames();
		String _gameInfo = getGame(gamesIDList.get(0));
		int[][] bonus = getGameBoard(_gameInfo);
		parseTiles(_gameInfo);
		
		
		WordFinder find = new WordFinder();
		ArrayList<Point> buildLocations = new ArrayList<Point>();
		List<String> bla; 
		
		

		System.out.println("Rack: "+rack);
		printBoard(gameToBoard(game));
//		rack = "whehe.";
		rack=".......";
		GameInfo gi=new GameInfo(game,bonus ,wildcards,rack);
		
		long time = System.currentTimeMillis();
		greedyMoveFinder gmf=new greedyMoveFinder(game,buildLocations,rack,this, bonus, find); 
		System.out.println("Greedy time: "+(System.currentTimeMillis()-time)+" milisec");
		

		
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
		
		
		
		
		//should only be done once per wordlist
		SlowFilter sf=new SlowFilter(find.getWordlist());
		
		//is done on this players every turn
		System.out.println("test of slowfilter");
		time = System.currentTimeMillis();
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
		System.out.println(line);


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
		if(gameInfo.charAt(pos+1)!='['){
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
		System.out.println(line);
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
		System.out.println(line);
		//        }
		return line;

		//        System.out.println(str);
		//        utdata.println(str);

	}
	public static void main(String args[]) throws Exception{
		new Main();
	}
	public String getCookie() throws Exception{
		user = "fredric.ericsson@gmail.com";
//		user = "lather"
		password = "icpmwq";
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
		System.out.println(str);
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

}