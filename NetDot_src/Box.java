//This class defines a single box on the board for the dots game
//Each box is responsible for tracking if the adjacent lines have been drawn, and if it is complete
//it also holds the initial of whoever owns (completed) the box
public class Box {
	
	//define instance variables
	//each boolean variable indicates if the corresponding border line should be drawn
	//each variable is left public so that setters and getters are not necessary
	boolean top, bot, left, right = false;
	boolean complete = false;//turns true when all four sides are true
	char initial = '-'; //holds the initial for whoever completes the box, '-' is default flag

	/**This method is the sole constructor for the box class
	 * it accepts no arguments because each instance variable is already appropriately initialized
	 */
	Box(){}
	
	/**This method checks if the box is complete and sets the flag if it is
	 * @param player Initial of current player. Set to charFlag inside box box is complete
	 * @return 1 if the box is complete, 0 if the box is not complete
	 */
	public int checkComplete(char player) {
		if( left == true && right == true && top == true && bot == true) {
			complete = true;
			initial = player;
			return(1);
		}
		return(0);
	}


}
