/**This class defines the game board. It is responsible for keeping track of
 * the 2D array of boxes and the methods for manipulating the boxes`
 */
public class Board {
	
	//define instance variables
	public int BOXWIDTH = 50; //create an immutable variable that holds the width of each box
									//this allows for easy changes of the size of this box
	public int BOXNUM = 8;	//allow for easy changing of box numbers
	public Box[][] boxes = new Box[BOXNUM][BOXNUM]; //the 8x8 array of boxes
	
	/**This method is the sole constructor for the Board class
	 *It constructs the box objects inside the array boxes"
	 */
	Board() {
		for( int i = 0; i < BOXNUM; i++) {
			for( int j = 0; j < BOXNUM; j++) {
				boxes[i][j] = new Box();
			}
		}
	}

	
	/**This class allows the setting of side flags for a box
	 * It accepts three parameters, the side, the row, and the column
	 * After checking and setting the flag for the specified box, it ensures that adjacent boxes
	 * are also set properly. returns 1 if side set properly, returns 0 if side already set, return -1 if error
	 * The player initial get's set as the initial flag inside of the box if the box is complete
	 * @param row The row of the specified box
	 * @param column The column of the specified box
	 * @param side The side to set a true flag for (must be "top", "bot", "right", or "left")
	 * @param player First initial of current player
	 * @return integer flag. 2 means setSide was successful and a box was completed 1 means setSide was successful but no box was complete,
	 * 						 0 means the side was already set, -1 means the parameters were incorrect
	 */
	public int setSide(int row, int column, String side, char player) {
		
		int completedAdj = 0; //flag that tracks if an adjacent box was completed by the setSide
		int completed = 0; //tracks if the current box was completed
		//handle edge case that should never happen. returns if parameter type is not correct
		if ( !side.equals("top") && !side.equals("bot") && !side.equals("right") && !side.equals("left") ) {
			return(-1);
		}
		//second edge case if row/column are out of range
		if ( (row < 0) || (row > BOXNUM - 1) || (column < 0) || (column > BOXNUM -1) ) {
			return(-1);
		}
		
		//do same operations for each side
		if (side.equals("top")) {
			//if the side is already set, return 0 signaling this
			if( boxes[row][column].top == true ) {
				return 0;
			}
			if( row > 0 ) {//if there are more boxes above this one, then make sure that side is set too
				boxes[row - 1][column].bot = true;
				completedAdj = boxes[row - 1][column].checkComplete(player);//check to see if a box was completed
			}
			//set the top side of the current box
			boxes[row][column].top = true;
			completed = boxes[row][column].checkComplete(player);//check to see if a box was completed
			if( completed == 1 || completedAdj == 1) {
				return(2); //return 2 signalling that the side was set and a box was completed
			}
			else {//if just the side was set but no box was completed
				return(1);
			}
			
		}

		//do same operations for each side
		if (side.equals("bot")) {
			//if the side is already set, return 0 signaling this
			if( boxes[row][column].bot == true ) {
				return 0;
			}
			if( row < BOXNUM - 1 ) {//if there are more boxes below this one, then make sure that side is set too
				boxes[row + 1][column].top = true;
				completedAdj = boxes[row + 1][column].checkComplete(player);
			}
			boxes[row][column].bot = true;
			completed = boxes[row][column].checkComplete(player);//check to see if a box was completed
			if( completed == 1 || completedAdj == 1) {
				return(2); //return 2 signalling that the side was set and a box was completed
			}
			else {//if just the side was set but no box was completed
				return(1);
			}
		}
		//do same operations for each side
		if (side.equals("right")) {
			//if the side is already set, return 0 signaling this
			if( boxes[row][column].right == true ) {
				return 0;
			}
			if( column < BOXNUM - 1 ) {//if there are more boxes to the right this one, then make sure that side is set too
				boxes[row][column + 1].left = true;
				completedAdj = boxes[row][column + 1].checkComplete(player);
			}
			boxes[row][column].right = true;
			completed = boxes[row][column].checkComplete(player);//check to see if a box was completed
			if( completed == 1 || completedAdj == 1) {
				return(2); //return 2 signalling that the side was set and a box was completed
			}
			else {//if just the side was set but no box was completed
				return(1);
			}
		}
		//do same operations for each side
		if (side.equals("left")) {
			//if the side is already set, return 0 signaling this
			if( boxes[row][column].left == true ) {
				return 0;
			}
			if( column > 0 ) {//if there are more boxes to the left of this one, then make sure that side is set too
				boxes[row][column - 1].right = true;
				completedAdj = boxes[row][column - 1].checkComplete(player);
			}
			boxes[row][column].left = true;
			completed = boxes[row][column].checkComplete(player);//check to see if a box was completed
			if( completed == 1 || completedAdj == 1) {
				return(2); //return 2 signalling that the side was set and a box was completed
			}
			else {//if just the side was set but no box was completed
				return(1);
			}
		}
		
		return(-1);//default return flag because compiler complains
		
	}
	
	/**This method allows all of the flags and and initials in the boxes
	 * contained within the board to be reset to false, effectively restarting the game
	 */
	public void reset() {
		//iterate over the boxes
		for( int i = 0; i < BOXNUM; i++) {
			for( int j = 0; j < BOXNUM; j++) {
				//reset all flags to default values
				boxes[i][j].left = false;
				boxes[i][j].right = false;
				boxes[i][j].top = false;
				boxes[i][j].bot = false;
				boxes[i][j].complete = false;
				boxes[i][j].initial = '-';
			}
		}
	}
}
