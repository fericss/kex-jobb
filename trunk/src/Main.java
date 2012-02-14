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
	String rack;
	//    String line = null;

	String[][] game;


	public Main() throws Exception{
		game = new String[15][15];
		cookie = getCookie();
		List<String> gamesIDList = getGames();
		String _gameInfo = getGame(gamesIDList.get(1));
		parseTiles(_gameInfo);
		printTiles();
		WordFinder find = new WordFinder();



		List<String> bla;
		/// TEST
		//		        List<String> build = new ArrayList<String>();
		//		        for(String[] s : game){
		//		            String _tmp ="";
		//		            for(String st : s){
		//		                _tmp += st==null? "" : st;
		//		            }
		//		            System.out.println(_tmp);
		//		            bla = find.Matches(rack+_tmp);
		//		            for(String st : bla){
		////		                System.out.print(st+", ");
		//		                if(!st.equals("") && st.contains(_tmp)){
		//		                    build.add(st);
		//		                }
		//		            }
		//		        }
		//		        for(String s : build){
		//		            System.out.println(s);
		//		        }

		//        /// END TEST
		System.out.print("Words built with rack: ");
		bla = find.Matches(rack);
		for(String s : bla){
			System.out.print(s+", ");
		}

	}
	private void printTiles() {
		for(String[] s : game){
			for(String i : s){
				System.out.print(""+(i==null ? "\t" : "\t"+i));
			}
			System.out.println();
		}
		System.out.print("Rack: ");
		System.out.println(rack);
	}
	private void parseTiles(String gameInfo) {
		int pos = 0;
		while(gameInfo.charAt(pos)!='['){
			pos++;
		}
		boolean done = false;
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
						System.out.println(""+l1+" "+l2+" "+l3);
			game[l2][l1] = ""+l3;
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
		rack = "";

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
				rack = rack+gameInfo.charAt(pos);
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
			System.out.println("lol "+line.substring(startPos,pos) );
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

}