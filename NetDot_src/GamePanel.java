/**This is an extension of a JPanel. It allows the panel to be linked to the
 * JFrame
 */
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

public class GamePanel extends JPanel {
	
	Board board = new Board();
	int BOARD_WIDTH = 400;//save the board width
	int BOARD_HEIGHT = 400;//save the board height
	int BOXSIZE = BOARD_WIDTH/8;//size of the box
	NetDot parent;//save parent so that fields can be accessed
	String pOneName = null;
	String pTwoName = null;
	int pOneScore = 0;
	int pTwoScore = 0;

	
	public void paint(Graphics g) {
		//the main paint method for the game
		
		super.paintComponent(g);//clear the board before repainting
		if( parent == null ) return;//fix windowbuilder problem
		
		pOneName = parent.playerOneTextField.getText();
		pTwoName = parent.playerTwoTextField.getText();
		
		//fill the board with dots
		g.setColor(Color.BLACK);
		for(int x = 0; x <= BOARD_WIDTH; x = x + BOXSIZE) {//draw the dots by row
			for(int y = 0; y <= BOARD_HEIGHT; y = y + BOXSIZE) {//draw the dots by column
			g.fillRect(x,y,4,4);//draw squares that are a 4pixel square
			}
		}
		
		
		/*this loop parses through the array of boxes and outputs if a side should be drawn
		 */ 
		for( int row = 0; row < 8; row ++) {
			for( int col = 0; col < 8; col++) {
				/*IN EACH FILL RECT, THE +1 IS TO CENTER THE LINE ON THE 4PIXEL RECT THAT DENOTES A CORNER OF THE SQUARE*/
				//if the left side should be drawn
				if( board.boxes[row][col].left == true ) {
					g.fillRect(col*BOXSIZE+1, row*BOXSIZE, 2, BOXSIZE);//draw a rect from the upper left corner to bottom left
				}
				
				//if the right side should be drawn
				if( board.boxes[row][col].right == true ) {
					g.fillRect((col*BOXSIZE)+BOXSIZE+1,row*BOXSIZE, 2, BOXSIZE);//draw a rect from the upper right corner to bottom right
				}
				
				//if the top side should be drawn
				if( board.boxes[row][col].top == true ) {
					g.fillRect(col*BOXSIZE, row*BOXSIZE+1, BOXSIZE, 2);//draw a rect from the upper left corner to upper right
				}
				if( board.boxes[row][col].bot == true ) {
					g.fillRect(col*BOXSIZE, (row*BOXSIZE)+BOXSIZE+1, BOXSIZE, 2);//draw a rect from the bottom left corner to bottom right
				}
			    if( board.boxes[row][col].complete == true) {//if the box is complete
					//draw the inital of the current player in the middle of the current box
					g.drawString(Character.toString(board.boxes[row][col].initial), (col*BOXSIZE) + 20, (row*BOXSIZE) + 30);
			    }
			}
		}
	}

	/**Constructor for the game panel
	 */
	public GamePanel() {
		//should create a beveled border, but doesn't for some reason
		setBorder(new BevelBorder(BevelBorder.RAISED, Color.DARK_GRAY, Color.GRAY, null, null));
	}
	
	/**instance method that saves the parent object
	 * @param p The parent object of type GameWindow
	 */
	public void saveParent(NetDot p) {
		parent = p;
	}
	
	/**This method determines which box and which side the clicker intended
	 * it then attempts to set the side of that box and repaints the board
	 * it also determines if a turn should be over based on if the side was already set and if a box was completed
	 *@param x The x-coordinate of the click
	 *@param y The y-coordinate of the click
	 *@param player The initial of the current player
	 *@return 0 if the turn is not over(the side was already set or a new box is completed), returns 1 if the turn is over
	 */
	public int drawSide(int x, int y, char player) {
		
		int returnFlag = -1; //flag holds the proper return
		//round the click down if they click outside
		if( x >= BOARD_WIDTH) x = BOARD_WIDTH -1;
		if( y >= BOARD_HEIGHT) y = BOARD_HEIGHT -1;
		int mouseX = x; 
		int mouseY = y;

		//divide each by BOXSIZE as integer, so they automatically round down
		int row = mouseY/BOXSIZE;
		int col = mouseX/BOXSIZE;

		//decide where the top left of this box is
		int boxX = BOXSIZE*col; 
		int boxY = BOXSIZE*row;

		//decide which side distance is shortest
		int dLeft = mouseX - boxX;//distance to left side
		int dRight = (boxX + BOXSIZE) - mouseX;//distance to right side
		int dTop = mouseY - boxY; //distance to top side
		int dBot = (boxY + BOXSIZE) - mouseY; //distance to bottom side

		//make a decision on which is shortest
		if( dLeft < dRight && dLeft < dTop && dLeft < dBot) {//then left was the intended click
			returnFlag = board.setSide(row, col, "left", player); //returns 1 if side was not already set
		}
		else if( dRight < dLeft && dRight < dTop && dRight < dBot) {//then right was the intended click
			returnFlag =board.setSide(row, col, "right", player);//returns 1 if side was not already set
		}
		else if( dTop < dRight && dTop < dLeft && dTop < dBot) {//then top was the intended click
			returnFlag = board.setSide(row, col, "top", player);//returns 1 if side was not already set
		}
		else if( dBot < dRight && dBot < dTop && dBot < dLeft) {//then bot was the intended click
			returnFlag = board.setSide(row, col, "bot", player);//returns 1 if side was not already set
		}
		else {//the click was directly in the middle
			parent.displayInstruction.setText("Please Click closer\nto one side!");
		}
		
		this.repaint();//call the paint method to update the board
		
		//if setSide determined that the side was set properly and a box was completed
		if(returnFlag == 2) {
			return(0);//return 0 signaling the turn should not be over
		}
		//if setSide determined that the side was set properly but a box was not completed
		else if(returnFlag == 1) {
			return(1);//return 1 signaling that the turn should be over
		}
		//if setSide determined that the side was already set
		else if(returnFlag == 0) {
			//check who's turn it is, and report that they clicked on a side that is taken, and it is still their turn
			if( parent.whosTurn.equals("server")) {
				parent.displayInstruction.setText(parent.playerOneTextField.getText()+" clicked on a side that is taken! It is still " + parent.playerOneTextField.getText() + "'s turn");
			}else {
				parent.displayInstruction.setText(parent.playerTwoTextField.getText()+" clicked on a side that is taken! It is still " + parent.playerTwoTextField.getText() + "'s turn");
			}
			return(0); //return 0 signaling that the turn should not be over
		}
		else {
			return(-1);
		}
	}
	
	/**
	 * This method updates the score by iterating over each box and looking for compete ones
	 */
	public void updateScore() {
		//set scores back to zero before incrementing to avoid accumulation
		pOneScore = 0;
		pTwoScore = 0; 
		
		//iterate over boxes in board
		for( int row = 0; row < 8; row++) {
			for( int col = 0; col < 8; col++) {
				//if the character is the same as the first initial of player one, increment their score
				if( board.boxes[row][col].initial == parent.playerOneTextField.getText().charAt(0)) {
					pOneScore++;
				}
				//if the character is the same as the first initial of player two, increment their score
				else if( board.boxes[row][col].initial == parent.playerTwoTextField.getText().charAt(0)) {
					pTwoScore++;
				}
			}
		}
	}
}
