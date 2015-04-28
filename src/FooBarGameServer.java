/** Sean Mullins
 *  CSC 460 - Operating Systems
 *  FooBarGameServer.java
 *  Version 1.0
 *  4/20/2014
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A multithreaded trivia game server.  The server reads game data
 * from a text file into a shared data structure (in this case, an
 * ArrayList). When a client connects, the server sends a welcome message 
 * followed by the first question. The client sends the user's answer as
 * a String to the server which calculates a score. The score is sent to 
 * the client along with the incorrect answer. The next question, if 
 * appropriate, is sent as well. This process is repeated for five questions
 * and a final score is presented to the user on completion.
 */

public class FooBarGameServer {

    
     
    private static final int PORT = 8241; 	// Port that the server listens on.

    
    
    private static ArrayList<ArrayList<String>> questions = new ArrayList<ArrayList<String>>();

    private static int points = 250;
    
    private static List<Character> validAnswers = Arrays.asList('1','2','3','4');
    
    
    /**
     * The application main method, which just listens on a port and
     * spawns handler threads. Main also calls getGameData method to initialize
     * an ArrayList that will contain the game's questions.
     */
    public static void main(String[] args) throws Exception {
    	
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        
        getGameData();	// Reads game data into ArrayList.
 
	    
        try {
            while (true) {
                new Comm(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
     
    }
    
    /** 
     * Method to read game data from text file into a shared data structure to be accessed by multiple threads.
     * 
     */
    public static void getGameData(){
    	
    	System.out.println("\nEnter the name of a game file to use:\n");
	 	BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s;
		try {
			s = bufferRead.readLine();
		
	    
    	    File file = new File(s);
    	    BufferedReader fr = new BufferedReader(new FileReader(file));
    	    String currentLine;
    	    
    	    ArrayList<String> gameData = new ArrayList<String>();
    	    
    	    
    	    while ((currentLine = fr.readLine()) != null) {
    	    	gameData.add(currentLine);
    			//System.out.println(currentLine);
    		}
    	    fr.close();
    	    
    	    for (int i = 0; i < 5; i++){
    	    	questions.add(new ArrayList<String>());		// Initialize questions array.
    	    }
    	    
    	    for (int i = 0; i < gameData.size(); i++) {		// Populate each question with its data.
    	        questions.get(i/6).add(gameData.get(i));
    	     }
	    
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }

    /**
     * A handler thread class.  Handlers are responsible for communication with 
     * each of the client connections that are spawned from the socket listener.
     */
    private static class Comm extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Constructs a handler thread.
         * Relevant work is done in the run() method.
         */
        public Comm(Socket socket) {
            this.socket = socket;
        }
        
        /** 
         * Responsible for validating only the format of a player's answer 
         * and returning a boolean indicating whether or not the format is valid.
         */
        private boolean validateAnswerFormat(String answer){
        
           
        	boolean isValid = false;
        	
        	if (answer == null) {
                return isValid;
            }
            else if (answer.length() <= 3){		// Length is OK
            	
            	for (int i = 0; i < answer.length(); i++){
            		if ( !(validAnswers.contains(answer.charAt(i)))) {	// Character is not valid
            			isValid = false;
            			break;
            		}
            		else
            			isValid = true;
            		
            		if (hasDuplicates(answer))
            			isValid = false;
            			
            	}
            
            }
            else
            	return isValid = false;
        	
        	
        	
        	return isValid;
        }
        
        /** 
         * Determines whether the user's answer contains duplicate numbers.
         */
        public static boolean hasDuplicates ( String s ) {
        	
            Set<Character> set = new HashSet<Character>();
            for ( int i = 0; i < s.length(); ++i ) {
                if ( set.contains( s.charAt(i))) {
                    return true;
                }
                else {
                    set.add(s.charAt(i));
                }
            }
            return false;
        }
        
        

        /**
         * Services this thread's client by sending questions, receiving answers, 
         * and calculating/sending scores.
         */
        public void run() {
        	
        	
        	
        	int totalPoints = 0; 		// Total accrued points.
            
            int pointsThisQuestion = 0;	// Points earned (or lost) for a single question.
            try {

                // Create streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                

                for (ArrayList<String> a : questions){ 	// Iterate through the list of questions.
        	    	for ( int i = 0; i < 5; i++){ 	// Question, and Answers 1 through 4.
        	    		
        	    		out.println(a.get(i)); 		// Send the Q&A's to the client.
        	    		out.flush();
        	    		
        	    	}
        	    	
        	    		String badAnswer = "true";
                        String input = in.readLine(); 	// Read the client's answer.
                        
                        while ( validateAnswerFormat(input) == false){ 	// Keep requesting valid answer until one is received.
                        	out.println(badAnswer);
                        	out.flush();
                        	input = in.readLine();
                        }            
                        out.println(badAnswer = "false"); 	// Tells client that its answer is OK.
                        out.flush();
                        if ( validateAnswerFormat(input) ){		// Answer was valid
                        	
                        	if ( input.contains(a.get(5))) { 	// Player entered wrong answer.
                        		pointsThisQuestion = -(points);
                        		totalPoints += pointsThisQuestion; 	// Deduct points.
                        	}
                        	else {
                        		pointsThisQuestion = (input.length() * points);  // Add points for each correct answer.
                        		totalPoints += pointsThisQuestion;	
                        	}
                        	
                        }
                        out.println(pointsThisQuestion);		// Send score for current question
                        out.flush();
                        out.println(a.get(Integer.parseInt(a.get(5))));		// Send string for incorrect answer.
                        out.flush();
                        out.println(totalPoints);		// Send total points thus far.
                        out.flush();
                        
                        
        	    		
                }
                out.println(totalPoints); 	// Send the final score to the client.
                out.flush();
                
             
                
            } catch (IOException e) {
                System.out.println(e);
            } finally {
             
                try {
                    socket.close(); 	// Close the socket.
                } catch (IOException e) {
                }
            }
        }
    }
}