import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;

/**
 * This class handles the network connection for NetDot
 * It creates the server/client sockets, and waits for information
 * If it receives mouse click information from the foreign socket, it will call
 * the mouseClickHandler method in NetDot to update the board
 * This class extends thread, so it will be run in parallel with NetDot
 * @author JosephBoothby
 *
 */
public class NetThread extends Thread{
	
	//class variables
	NetDot parent; //The parent object that constructs the netThread

	//make all of the scanner, socket, printwriter global so that other method can access it
	Socket sock;
	Scanner sc;
	PrintWriter pw;
	ServerSocket ss;			

	/**
	 * This method is the constructor for the NetThread class. It doesn't do anything
	 */
	NetThread(){}

	/**
	 * This method is called by NetThread.start(), and implements the threading
	 * it accepts no parameters, and has no returns, but will loop indefinitely waiting for
	 * information to be read from the socket
	 * It is also responsible for trying to create the server or client socket depending on which player we are
	 */
	public void run() {
		//if this is the server side, create a server socket
		if( parent.whichPlayer.equals("server") ){
			//create serverSocket on port 1234
			try {
				ss = new ServerSocket(1234);
				sock = ss.accept();
				sc = new Scanner(sock.getInputStream());
				pw = new PrintWriter(sock.getOutputStream());
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Server Socket not able to be established on port 1234");
				return;
			}
			
			System.out.println("Input stream created, waiting for input from foreign socket");
		}

		//if this is the client side, create a normal socket
		else{
			//try to create a socket on 1234
			try {
				sock = new Socket(parent.hostnameField.getText(),1234);//create a socket on the IP specified by the player in the hostname box
				sc = new Scanner( sock.getInputStream() );
				pw = new PrintWriter(sock.getOutputStream());
			}catch(Exception e) {
				System.out.println("Could not create connection on localhost");
				return;
			}
		}	
		
		//send a single line with name to other side
		pw.println("N " + parent.myName);
		pw.flush();
		
		//read single line for name`
		//wait for input from client side
		String input = sc.nextLine();
		processInput(input);
		parent.displayInstruction.setText("Connection Established, It is " + parent.playerOneTextField.getText() + "'s turn");
		parent.beginGame = true;
		
		//loop and continue to read from the other side
		while( sc.hasNextLine() ) {
			input = sc.nextLine();
			System.out.println("Read " + input + " from the client side");
			processInput(input);
		}
		
		//close all of the scanners and sockets
		//This should already be handled by the quit button, but in case the player just exits the window
		try {
			sock.close();
			pw.close();
			ss.close();
			sc.close();
		}catch(Exception e) {
			System.out.println("Could not close the sockets/scanner");
		}
		
		System.out.println("All sockets and scanners successfully closed");
		parent.displayInstruction.setText("Connect Lost, Ending Gamplay");
		parent.beginGame = false;
		
	}
	
	/**
	 * This method saves the parent object so that netThead can access it's fields 
	 * @param p The parent object that created NetThread
	 */
	public void saveParent( NetDot p ) {
		parent = p;
	}
	
	/**
	 * This method processes the string that it reads from the foreign scanner
	 * Input should be in one of the following forms
	 * For a click: "C x y", where X and Y are x,y coordinates of a click
	 * For a name: "N name", where name is the name of the opponent
	 * For a quit: "Q", this will close the sockets/ scanner/ and printWriter
	 * @param input The string read from socket
	 */
	public void processInput( String input ){
		String opponent;//holds either "client" or "server". Whichever is the opponent
		if( parent.whichPlayer.equals("server") ) opponent = "client";
		else opponent = "server";
		
		//create a string array to hold the input split around spaces
		String[] split = input.split(" ");
		
		//input was mouseclick
		if( split[0].equals("C") ) {
			//If the line contains mouseClick information, then send it to handleMouseClick
			parent.handleMouseClick(Integer.parseInt(split[1]), Integer.parseInt(split[2]), opponent);
		}

		//input was name
		else if(split[0].equals("N")) {
			//if this window is the server, set the recieved name to the client
			if( parent.serverRadio.isSelected() ) {
				parent.playerTwoTextField.setText(split[1]);
			}
			//if this window is the client, set the recieved name to the server
			else {
				parent.playerOneTextField.setText(split[1]);
			}
		}
		//if a quit flag was sent
		else if(split[0].equals("Q")){
			try {
				sock.close();
				ss.close();
				pw.close();
				sc.close();
			}catch(Exception e) {
				System.out.println("Unable to close the connection!");
			}
		}

	}

	/**
	 * This method sends mouseClicks from the clickHandler to the sockets using the printWriter
	 * @param action The letter (C, N, or Q) that determines how the foreign socket handles the data
	 * @param x x-coordinate of the click
	 * @param y y-coordinate of the click
	 */
	public void sendData(char action, int x, int y) {
		if( action == 'C' ) {
			try {
				pw.println("C " + x + " " + y);
				pw.flush();
			}catch(Exception e) {
				System.out.println("Unable to send clicks between the sockets");
			}
		}
		//this action originates from the quit button
		if( action == 'Q' ) {
			//send quit flag to distant side, then close everything down
			try {
				pw.println("Q");
				pw.flush();
				//send quit flag to this as if coming from distant
				processInput("Q");
			}catch(Exception e) {
				System.out.println("Unable to send quit between the sockets");
			}
		}
	}
}
