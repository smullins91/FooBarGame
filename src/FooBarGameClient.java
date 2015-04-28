/** Sean Mullins
 *  CSC 460 - Operating Systems
 *  FooBarGameClient.java
 *  Version 1.0
 *  4/20/2014
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *	Simple client for players of the FooBar game. Text based output to the console.
 *  The client will recieve questions as Strings from the server. Users will enter an answer String
 *  which will be sent back to the server which will then calculate a score based on the answer.
 *  The server then sends back the score and the incorrect answer followed by the next question if the
 *  game has not yet ended. At the game's end, the server sends the final score to the client which displays it.
 */
public class FooBarGameClient {

    static BufferedReader in;
    static PrintWriter out;

    public static void main(String[] args) throws Exception {
        
    	run();
    	
    }
    
    
    /** 
     * Runs the client. Reads and writes messages to/from the server thread.
     */
    public static void run() throws Exception{
    	
    	boolean gameOver = false;
    	// Make connection and initialize streams
        Socket socket = new Socket("localhost", 8241);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        String input = "";
        
        String pointsThisQuestion, wrongAnswer, scoreSoFar, finalScore;
        
        int questionCounter = 0; 	// Determines if game is over yet.
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Reads user input from console (answers).
	    
        printWelcomeMessage();
        

        // Read and write messages from the server and write player's answers from the console to the server.
        while(!gameOver){
        	for ( int i = 0; i < 5; i++ ){
        		input = in.readLine(); 		// Read question strings from server.
        		System.out.println(input); 	// Output question strings to console.
        	}
        	
        	System.out.println("\n");
        	out.println(br.readLine()); 	// Send answer.
        	while (in.readLine().equals("true")){	// Answer is of invalid format.
        		System.out.println("WARNING: Answer must be between one and three characters in length\nand contain only numbers from 1-4 with no duplicate numbers.\n" +
        				"Please enter a valid answer :");
        		out.println(br.readLine());
        	}
        	out.flush();
        	pointsThisQuestion = in.readLine(); 	// Get points for current question from server.
        	wrongAnswer = in.readLine();			// Get wrong answer string from server.
        	scoreSoFar = in.readLine();				// Get cumulative points from server.
        	
        	System.out.printf("\n========================================\nPoints earned for this question: %s\nIncorrect Answer: %s\nCurrent Score: %s\n========================================", 
        			pointsThisQuestion, wrongAnswer, scoreSoFar);
        	
        	System.out.println("\n");
        	
        	
        	questionCounter++;
        	if (questionCounter == 5)	// End game if all questions have been completed.
        		gameOver = true;
    
        }
        finalScore = in.readLine(); 	// Get final score from server.
    	System.out.printf("=================================" +
    			"\n\tGAME OVER\n=================================\n\nFinal Score:" +
    			" %s\n", finalScore);
    	
    	socket.close(); 	// close the connection.
      
    }
    
    /** 
     * Prints welcome message to initialize the game.
     */
    private static void printWelcomeMessage(){
    	
    	System.out.println("\n================================================================================\nWelcome to FooBar! You will be presented with five trivia questions one-by-one.\n" +
        		"Each question is multiple choice, but THREE of the answers are correct options.\nProvide as many correct answers as possible for the most points," +
        		" but include the\nwrong answer and points will automatically be deducted from your score.\n\nEnter your answer to each question as a single number representing" +
        		" the choices\nyou think are correct.\nFor example, if you wish to choose options 1,2, and 3, simply enter \"123\".\n\nGood Luck!\n\n" +
        		"================================================================================\n\n");
    	
    }
 
}