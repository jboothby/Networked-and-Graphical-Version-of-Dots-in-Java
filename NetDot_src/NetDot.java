/*Programming assignment 4, CSE224, Joseph Boothby
 *This program plays a game of Dots using only
 *the graphical components from javax.swing, it does
 *not output anything to the command line interface
*/
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.SystemColor;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**An extension of JFrame. It implements the window that the game of Dots
  *is played in. There are several graphical compenents included and it makes
  *use of three other classes contained in the package. Namely: Box, Board, and GamePanel
  @author JosephBoothby
**/
public class NetDot extends JFrame {

	//declare all graphics componenets globally so they can be accessed from anywhere
	private JPanel windowPane;
	JTextField playerOneTextField;
	JTextField playerTwoTextField;
	JTextArea displayInstruction;
	JTextArea displayPlayerOneScore;
	JTextArea displayPlayerTwoScore;
	GamePanel gamePanel;
	JButton startButton;
	JPanel borderPanel;
	JRadioButton serverRadio;
	JRadioButton clientRadio;
	JTextArea txtrHostname;
	JLabel scoreTop;
	JLabel scorebot;
	
	//declare global variables
	boolean beginGame = false; //ensures that play game is pressed before the game begins
	private final ButtonGroup buttonGroup = new ButtonGroup();
	JTextField hostnameField;
	String whichPlayer = null;// changes to either "client" or "server" depending on which role this program is playing
	String whosTurn = "server";//flag changes from server to client depending on whos turn it is. always starts with server
	String myName = null;//saves the name of the current player
	NetThread net;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NetDot frame = new NetDot();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NetDot() {
		setForeground(UIManager.getColor("Button.background"));
		setBackground(UIManager.getColor("Button.background"));
		setTitle("Net Dots");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		windowPane = new JPanel();
		windowPane.setBorder(null);
		setContentPane(windowPane);
		windowPane.setLayout(null);
		
		//Create and start the netThread
		net = new NetThread();
		net.saveParent(this);
		
		//where playerOne's name is displayed and entered
		playerOneTextField = new JTextField();
		playerOneTextField.setToolTipText("");
		playerOneTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		playerOneTextField.setBounds(576, 56, 150, 30);
		playerOneTextField.setEditable(false);
		windowPane.add(playerOneTextField);
		playerOneTextField.setColumns(10);
		
		//where playerTwo's name is displayed and entered
		playerTwoTextField = new JTextField();
		playerTwoTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		playerTwoTextField.setEditable(false);
		playerTwoTextField.setBounds(576, 240, 150, 30);
		windowPane.add(playerTwoTextField);
		playerTwoTextField.setColumns(10);
		
		//Main text area that displays any instructions to the user
		displayInstruction = new JTextArea("Choose Server or Client, then Click Start or Connect to begin");
		displayInstruction.setBackground(UIManager.getColor("Button.background"));
		displayInstruction.setBounds(40, 0, 424, 30);
		displayInstruction.setFont(new Font("Tahoma", Font.PLAIN, 14));
		displayInstruction.setEditable(false);;		
		windowPane.add(displayInstruction);
	
		//area that displays playerOne/server score
		displayPlayerOneScore = new JTextArea();
		displayPlayerOneScore.setEditable(false);
		displayPlayerOneScore.setBackground(UIManager.getColor("Button.background"));
		displayPlayerOneScore.setFont(new Font("Monospaced", Font.BOLD, 16));
		displayPlayerOneScore.setText("0");
		displayPlayerOneScore.setBounds(564, 95, 20, 20);
		windowPane.add(displayPlayerOneScore);
		
		//area that displays playerTwo/client score
		displayPlayerTwoScore = new JTextArea();
		displayPlayerTwoScore.setText("0");
		displayPlayerTwoScore.setFont(new Font("Monospaced", Font.BOLD, 16));
		displayPlayerTwoScore.setBackground(SystemColor.menu);
		displayPlayerTwoScore.setBounds(564, 315, 20, 20);
		displayPlayerTwoScore.setEditable(false);
		windowPane.add(displayPlayerTwoScore);
		
		//This field is for entering the hostname to connect to. Defaults to localhost
		hostnameField = new JTextField();
		hostnameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		hostnameField.setText("localhost");
		hostnameField.setBounds(576, 280, 150, 25);
		hostnameField.setEditable(false);
		windowPane.add(hostnameField);
		hostnameField.setColumns(10);
		
		//Create the main game panel that overlays the JFrame
		gamePanel = new GamePanel();
		gamePanel.saveParent(this);//save this as the parent so that gamePanel can access it
		gamePanel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.DARK_GRAY, Color.GRAY, null, null));
		gamePanel.addMouseListener(new MouseAdapter() {
			@Override
			//This is the mouseClick actionListener that drives the program
			public void mouseClicked(MouseEvent e) {
				//call the handleMouseClick method with the x,y coords of the mouse click
				//also send whichPlayer so that the handler knows who is trying to click the mouse
				handleMouseClick( e.getX(), e.getY(), whichPlayer);
				
			}
		});
		gamePanel.setBounds(50, 40, 404, 404);
		windowPane.add(gamePanel);
		gamePanel.setLayout(null);

		//This is the object and action listener for the Start/connect button
		//It handles disabling the other buttons, setting the name, and initializing the netThread
		startButton = new JButton("Start");
		startButton.setEnabled(false);//disable the button until a radio button is clicked
		startButton.addMouseListener(new MouseAdapter() {
			//If this button is clicked, then run NetThread and accept create a ServerSocket
			public void mouseClicked(MouseEvent e) {

				//if the game has already started, then this is now a quit button, so disconnect the sockets
				//if this game has not already started, we will drop through to the lower block
				if( startButton.getText().equals("Quit")) {
					//send q quit flag over the 
					net.sendData('Q',0,0);
					System.out.println("Disconnect from the client");
					startButton.setEnabled(false);
					serverRadio.setEnabled(false);
					clientRadio.setEnabled(false);
					return;
				}
				
				//if the startButton was clicked and we are the server
				if(whichPlayer.equals("server")) {
					//disable the client radio button
					clientRadio.setEnabled(false);
					//set whichPlayer to server
					whichPlayer = "server";
					//set the name to server if not already chosen
					if ( playerOneTextField.getText().equals("") ) {
						System.out.println("Setting text to server");
						playerOneTextField.setText("Server");
					}
					//set my name
					myName = playerOneTextField.getText();
				}
				else {
					//disable to server radio button
					serverRadio.setEnabled(false);
					//set whichPlayer to client
					whichPlayer = "client";
					//set the name to client if not already chosen
					if ( playerTwoTextField.getText().equals("") ) {
						System.out.println("Setting text to server");
						playerTwoTextField.setText("Client");
					}
					myName = playerTwoTextField.getText();
				}

				//change the text to disconnect
				startButton.setText("Quit");

				//start the code contained in NetThread.run();
				net.start();

				displayInstruction.setText("Waiting for connection...");
			
			}
		});
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		startButton.setBounds(576, 161, 150, 30);
		windowPane.add(startButton);	
		
		//The radio button and actionlistener for server
		//This actionlistener should handle making the other textfields non-editable
		serverRadio = new JRadioButton("Server");
		serverRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				startButton.setEnabled(true);//enable the start button
				//if the button was deselected, return
				if( e.getStateChange() == 2 ) {
					return;
				}
				//if the server button was selected
				else {
					//enable server fields
					startButton.setText("Start");//change start button to "start"
					playerOneTextField.setEditable(true);//make server name editable
					whichPlayer = "server";//set player to server
					
					//disable client fields

					playerTwoTextField.setEditable(false);//gray out client side name box
					hostnameField.setEditable(false);//gray out the hostname box
				}
			}
		});
		serverRadio.setFont(new Font("Tahoma", Font.PLAIN, 10));
		buttonGroup.add(serverRadio);
		serverRadio.setBounds(491, 61, 79, 21);
		windowPane.add(serverRadio);
		
		//The radio button and action listener for client
		//Should handle making the server text fields non-editable
		clientRadio = new JRadioButton("Client");
		clientRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				startButton.setEnabled(true);//enable the start button
				//if the button was deslected, just return
				if( e.getStateChange() == 2) {
					return;
				}
				//if the button was selected...
				else {
					//enable client fields
					startButton.setText("Connect");//change start button to say connect
					playerTwoTextField.setEditable(true);//make player two name editable
					hostnameField.setEditable(true);//make hostname editable
					whichPlayer = "client";//change player to client
					
					//disable server fields
					playerOneTextField.setEditable(false);//disable player one name editing
				}
					
				
			}
		});
		clientRadio.setFont(new Font("Tahoma", Font.PLAIN, 10));
		buttonGroup.add(clientRadio);
		clientRadio.setBounds(491, 245, 79, 21);
		windowPane.add(clientRadio);

		//This JPanel has no functionality other than to provide a beveled border behind the GamePanel
		borderPanel= new JPanel();
		borderPanel.setBorder(new BevelBorder(BevelBorder.RAISED, UIManager.getColor("Button.light"), UIManager.getColor("Button.highlight"), UIManager.getColor("CheckBox.darkShadow"), UIManager.getColor("Button.shadow")));
		borderPanel.setBounds(40, 30, 424, 424);
		windowPane.add(borderPanel);

		//Just nonInteractive. Just labels the word Hostname next to the appropriate jtextfield
		txtrHostname = new JTextArea();
		txtrHostname.setBackground(UIManager.getColor("Button.background"));
		txtrHostname.setText("Hostname");
		txtrHostname.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txtrHostname.setBounds(491, 279, 71, 22);
		windowPane.add(txtrHostname);
		
		//nonInteractive. just lables the word score next to the Server score field
		scoreTop = new JLabel("SCORE");
		scoreTop.setFont(new Font("Monospaced", Font.PLAIN, 14));
		scoreTop.setBounds(502, 321, 44, 15);
		windowPane.add(scoreTop);
		
		//nonInteractive. just lables the word score next to the client score field
		scorebot = new JLabel("SCORE");
		scorebot.setFont(new Font("Monospaced", Font.PLAIN, 14));
		scorebot.setBounds(501, 102, 58, 15);
		windowPane.add(scorebot);
		
	}
	/**
	 * This method handles what would usually be done inside of the gamePanel actionListner for mouse clicks
	 * it has been moved into it's own method so that the netThread class can access the same functions
	 * by sending x,y coordinates without actually clicking inside of the gamePanel	 * 
	 * @param x The x-coordinate of a mouse click ( or simulated mouse click) within the borders of the game panel
	 * @param y The y-coordinate of a mouse click ( or simulated mouse click) within the borders of the game panel
	 * @param sender The string ("client" or "server") of who is trying to click the mouse
	 * @author JosephBoothby
	 */
	public void handleMouseClick(int x, int y, String sender) {
		//ensure that players have clicked the begin button before anything can execute
		//if beginGame is not true, then we fall return from this method
		if( beginGame != true) return;

		//execute this block if the click originated from this console
		if( whichPlayer.equals(sender) ) {
			//send the click to the other side
			net.sendData('C',x,y);
		}

		//handle edge cases to make sure it's the correct person's turn
		if( !whosTurn.equals(sender) ) {
			//publicly shame the person that tried to click out of turn
			if(sender.equals("server")) {
				displayInstruction.setText( playerOneTextField.getText()+" tried to click out of turn! It is still " + playerTwoTextField.getText() +"'s turn");
			}else {
				displayInstruction.setText( playerTwoTextField.getText()+" tried to click out of turn! It is still " + playerOneTextField.getText() +"'s turn");
			}
			return;
		}

		int turnComplete = 0; //flag becomes one if the turn was completed successfully
		
		//if the person who clicked was the server
		//at this point we know that it must be the server's turn because of edge cases above
		if( sender.equals("server")) {
			//pass the appropriate x,y coords of the click and player one's first initial
			turnComplete = gamePanel.drawSide(x, y, playerOneTextField.getText().charAt(0));//send initial of current player
			if(turnComplete == 1) {
				System.out.println("Determined that " + whosTurn + " turn is completed");
				displayInstruction.setText(playerTwoTextField.getText() + "'s turn"); //display which player goes next
				whosTurn = "client";//change whos turn it is
			}
		}

		//else it is the clients turn
		else {
			turnComplete = gamePanel.drawSide(x, y, playerTwoTextField.getText().charAt(0));//send initial of current player
			if(turnComplete == 1) {
				System.out.println("Determined that " + whosTurn + " turn is completed");
				displayInstruction.setText(playerOneTextField.getText() + "'s turn");//display which character goes next
				whosTurn = "server";
				}
		}

		//update the score
		gamePanel.updateScore();
		//display the new score
		displayPlayerOneScore.setText(Integer.toString(gamePanel.pOneScore));
		displayPlayerTwoScore.setText(Integer.toString(gamePanel.pTwoScore));	
		//if somebody has won
		if( (gamePanel.pOneScore + gamePanel.pTwoScore) == 64) {
			beginGame = false; //disable the gamePanel
			//if player1 one
			if( gamePanel.pOneScore > gamePanel.pTwoScore ) {
				displayInstruction.setText(playerOneTextField.getText() + " wins!");
			}
			//if player 2 one
			else if (gamePanel.pTwoScore > gamePanel.pOneScore ) {
				displayInstruction.setText(playerTwoTextField.getText() + " wins!");
			}
			//if it was a tie
			else {
				displayInstruction.setText("It was a tie!");
			}
		}
	}
}
