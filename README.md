# Networked and Graphical Version of Dots in Java

This project is the game Dots, which is an 9x9 grid of boxes. Each player takes turns
connecting two dots. When a player complets all 4 sides of a box, they get a point and get to take another turn.
Who controls each box is shown by putting the first inital of the player who completed the box inside of it.
The game is complete when all possible dots have been connected, and the winner is the person who completed
the most boxes.

This program is written in Java and makes use of the Swing libarary for the graphical interface. The program
uses a server/client model where two people both start up the game, and one chooses to be the server, and the other
chooses to be the client. The two players then enter the IP address of the person they are playing with.
This IP defaults to LocalHost which allows playing with two windows on the same machine. 

Under the hood, the program works by creating Sockets on both machines on a predefined port, and then starting
a multi-threaded operation where both instances of the program wait for input from the other and then move in lockstep.
