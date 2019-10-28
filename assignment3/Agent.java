/*******************************************************
 * COMP3411/9414 Artificial Intelligence, Term 1, 2019 *
 * 		 Project 3: Nine-Board Tic-Tac-Toe             *
 * *****************************************************
 *
 * Agent.java
 * Group: m01
 * Group member: 
 *		z5014567, Senlin Deng; 
 *		z5012085, Yinuo Li
 * 
 *
 * ***************************************************
 * *******      1. Briefly Introduction       ********
 * ***************************************************
 * 
 * This program is designed based on Game Playing agent. 
 * 
 * The planning part of the agent is based on:
 * 		1. Minmax Search (Depth-First-Search with Max_ Depth = 7)
 *	    2. Alpha-Beta Pruning
 * 		3. Heuristics evaluation
 *
 * The Agent has a local board (i.e. a 2D array int[][]) to store the current state
 * of the game. And it makes move by turn based on the 
 * value received from the server. And the desicison is generated
 * from the local algorith(e.g. minmax Search).
 * 
 *
 * The following table shows the testing result of our agent,
 * the performance is evaluated by the win ratio with different 
 * opponent agents and start turns. One certain condition is evaluated
 * with 100 samples.
 *
 * +----------------------------------------------------------------------------------------------------------------------------------+
 * |  (win ratio)  | randt | lookt -0 | lookt -1 |  lookt -3 |  lookt -5 |  lookt -7 |  lookt -9 | lookt -12 |	lookt -15 | lookt -17 |
 * |----------------------------------------------------------------------------------------------------------------------------------|
 * | Player = X    | 100%  |   100%   |   99%	 |   89%     |    85%    |	  80%    |    55%	 |    19%    |	  13%     |	   6%	  |				
 * |----------------------------------------------------------------------------------------------------------------------------------|
 * | Player = O    | 100%  |   100%   |   100%   |   76%     |	  75%    |    59%    |	  12%	 |    8%     |     5%     | uncertain |
 * +----------------------------------------------------------------------------------------------------------------------------------+
 * 
 *
 * ***************************************************
 * ****      2. Algoriths and Heuristics         *****
 * ***************************************************
 *
 * The main algorithm for this agent is based on Minmax algorithm with alpha-beta pruning
 * with a maximum depth of 7.
 * 
 *	*********************************** 
 *  ******* alpha-beta pruning ********
 *	***********************************
 *
 * The main method of this code is:
 * 
 * alphabeta(int[][] board, int depth, int alpha, int beta, int turn, int lastMove):
 *		- board: current traced node.
 *		- depth: the traced depth.
 * 		- alpha, beta value.
 * 		- turn: X or O
 *		- lastMove: next sub_board to choose
 * 
 *
 * For the player turn:
 * 		it returns the max_heuristic of the child_node
 *		if there are multiple moves with max_heuristic
 *		returns the one which will produce the sub_board
 *		which has more players than opponents
 *
 *
 * For the opponent turn:
 * 		it returns the min_heuristic of the child_node
 *		if there are multiple moves with min_heuristic
 *		returns the one which will produce the sub_board
 *		which has more opponents than players
 *
 *  *********************************** 
 *  *******    Heuristics      ********
 *	***********************************
 *
 * The idea of evaluating a heuristics is from tutorial 5:
 * 	
 *		Eval(s) = 3X2(s) + X1(s) - (3O2(s) + O1(s))
 * 
 * We take the sum of the linear evalustion functions of all 
 * nine sub_borads. However, instead of setting the weight as 
 * 3 and 1 for X2(s) and X1(s) we change the weight to 10 and 100.
 * Additionally, we add weight as 1000 for X3(s):
 *
 * 
 * New_Eval_SUB = 1000*X3(s) + 100*X2(s) + 10*X1(s) - 1000*O3(s) - 100*O2(s) - 10*O1(s)
 *
 * Heuristics = New_Eval_SUB_1 + New_Eval_SUB_2 + ... +  New_Eval_SUB_9.
 *
 *
 *
 *
 *
 * ***************************************************
 * ****     	 3. design decisions          	 *****
 * ***************************************************
 *
 * The search algorithm is based on Depth-first-Search. Since this is a game agent, we need to consider
 * opponents moves and make counter moves. Alpha-Beta pruning helps us optimize the algorithm. And the heuristic
 * is evaluated by linear_evalustion_functions from tutorial 5.
 *
 * This program has a bad performance when the depth of lookt_agent increased.
 *
 * Since the time is limited, we do not implement other advanced algorithms to optimize the game. However, we have
 * some ideas about how to do so.
 *
 * 1. get each possible 3x3 boards a certain weight. This weight will be evaluated with statistics. We choose next
 * 		move with a heigher weight as a player (opponent otherwise.).
 * 
 * 2. For the second_move() and third_move(), we pick the one with heigher possibilities to win a game. 
 * 		Again, this probability be evaluated with statistics.
*/



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.lang.Math;

public class Agent {

	static int MAX_DEPTH = 7;

	/*
	 * start X: player = -1, opponent = 1
	 * start O: player = 1, opponent = -1
	 * EMPTY = 2
	*/
	static int player;
	static int opponent;
	static int test;

	/*
	 * The game is won by getting three-in-a row either 
	 * horizontally, vertically or diagonally 
	 * in one of the nine boards.
	*/
	public static final int[][] winningCombo = {
		 {1, 2, 3},
		 {4, 5, 6},
		 {7, 8, 9},
		 {1, 4, 7},
		 {2, 5, 8},
		 {3, 6, 9},
		 {1, 5, 9},
		 {3, 5, 7}
	 };


	static int win_test;
    
    /*
	 * X = 0
	 * O = 1
	 * EMPTY = 2
	*/
    static int[][] boards = new int[10][10];


    static Random rand = new Random();
    static int prevMove = 0;
    
    public static void main(String args[]) throws IOException {

		if(args.length < 2) {
		    System.out.println("Usage: java Agent -p (port)");
		    return;
		}
			
		final String host = "localhost";
		final int portNumber = Integer.parseInt(args[1]);

		Socket socket = new Socket(host, portNumber);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		String line;

		while (true) {

		    line = br.readLine();

		    int move = parse(line);

		    if(move == -1) {
				socket.close();
				return;
		    }else if(move == 0){
			  	
		    }else {
				out.println(move);
		    }

		}
    }



    public static int parse(String line) {



		if(line.contains("init")) {
		    //initialize a game 
		    init();
		    

		}else if(line.contains("start")) {

		    int argsStart = line.indexOf("(");
			char playerChar = line.charAt(argsStart + 1);
			
			int tmp_player = (playerChar=='x') ? -1 : 1;
			
			start(tmp_player);
			test = 0;
			System.out.println("Started with " + tmp_player);

		}else if(line.contains("second_move")) {

		    int argsStart = line.indexOf("(");
		    int argsEnd = line.indexOf(")");

		    String list = line.substring(argsStart+1, argsEnd);
		    String[] numbers = list.split(",");

		    System.out.println("the information i got is ("  + Integer.parseInt(numbers[0])+ Integer.parseInt(numbers[1]) + ")");

		    prevMove = Integer.parseInt(numbers[1]);

		    place(Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]), opponent);


		    
		    return makeMove();

		}else if(line.contains("third_move")) {

		    int argsStart = line.indexOf("(");
		    int argsEnd = line.indexOf(")");

		    String list = line.substring(argsStart+1, argsEnd);	
		    String[] numbers = list.split(",");

		    

		    place(Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]), player);
		    place(Integer.parseInt(numbers[1]),Integer.parseInt(numbers[2]), opponent);

		    prevMove = Integer.parseInt(numbers[2]);
		     

		    return  makeMove();

		}else if(line.contains("next_move")) {
				
		    int argsStart = line.indexOf("(");
		    int argsEnd = line.indexOf(")");

		    String list = line.substring(argsStart+1, argsEnd);	

		    place(prevMove, Integer.parseInt(list), opponent);

		    prevMove = Integer.parseInt(list);


		    
		    return makeMove();

		}else if(line.contains("last_move")) {

		    //TODO
		}else if(line.contains("win")) {
			
			win_test++;
			System.out.println("win win: " + win_test);
			//return -1;
			
		    //TODO
		}else if(line.contains("loss")) {
			
			System.out.println("win win: " + win_test);
			//return -1
		}else if(line.contains("end")) {

		    return -1;
		}
		return 0;
    }

    /*********************************************************//*
   		Called at the beginning of a series of games
	*/
 	public static void init() {
		win_test = 0;
    }


	/*********************************************************//*
		Called at the beginning of each game
	*/
	public static void start(int this_player) {
		
		player = this_player;
		opponent = -this_player;
		initBoard();
	}
	

	/*********************************************************//*
		initialize the board, set all elements of the board to empty
		Empty = 0;
	*/
    public static void initBoard() {
		for(int b = 1; b <= 9; b++ ) {
		    for(int c = 1; c <= 9; c++ ) {
		    	boards[b][c] = 0; 
		    }
  		}
    }

   /*********************************************************//*
		place the player for current move
	*/
    public static void place(int board, int num, int p) {
		boards[board][num] = p;
    }

    


    /*********************************************************//*
		horizentally, vertically, dioganolly.
	*/
	public static int checkWin(int[][] b){
		int winner = 0;
		//there 7 ways to win a game in any 9 sub-boards
		for(int i = 1; i <= 9; i++){
			//check each combination
			for (int j = 0; j < 8; j++){
				 
				int one = winningCombo[j][0];
				int two = winningCombo[j][1];
				int three = winningCombo[j][2];
				
				if ((b[i][one] == b[i][two]) && (b[i][two] == b[i][three]) && (b[i][one] != 0)){
			    	winner = b[i][one];
			    	return winner;
			    }

			}

		}
		return winner;
	}


	/*********************************************************//*
		New_Eval_SUB = 1000*X3(s) + 100*X2(s) + 10*X1(s) - 1000*O3(s) - 100*O2(s) - 10*O1(s)
		
		Heuristics = New_Eval_SUB_1 + New_Eval_SUB_2 + ... +  New_Eval_SUB_9.
	*/
    public static int getHeuristic(int [][] b) {
		
		int heuristic = 0;

		//for each sub_board
		for(int i = 1; i <=9 ; i++){
			//for each combination
			int comboH = 0;
			for (int j = 0; j < 8; j++){
				int one = winningCombo[j][0]; //1, 2, 3 
				int two = winningCombo[j][1];
				int three = winningCombo[j][2];
				
				if ((b[i][one] == b[i][two]) && (b[i][two] == b[i][three]) && (b[i][one] != 0)){
			    	return ( b[i][one] == player) ? 1000 : -1000;
			    }

				int sum = b[i][one] + b[i][two] + b[i][three];
				int product = b[i][one]*b[i][two]*b[i][three];
				 
				if(sum == player && product==0){
					comboH += 10;
				}

				if(sum == 2*player){
					comboH += 100;
				}

				if(sum == opponent && product==0){
					comboH -= 10;

				}
				if(sum == 2*opponent){
					comboH -= 100;
				}

			}
		 
			heuristic += comboH;
		}
  		return heuristic;
    }



    /*********************************************************//*
		
		psudo-code from lecture activities
		
	*/
    public static int alphabeta(int[][] b, int depth, int alpha, int beta, int turn, int lastMove){
 			
    	if(checkWin(b) !=0 || depth == 0){
    		return getHeuristic(b);
    	}

		//our turn
		if(turn == player){			
			for(int i = 1 ; i <= 9 ; i ++){				
				int[][] child = boardClone(b);
				if(child[lastMove][i] == 0){					 
					child[lastMove][i] = player;
					alpha = Math.max(alpha, alphabeta( child, depth-1, alpha, beta, opponent,i));
					if(alpha >= beta) return alpha;
				}
			}
			return alpha;
		}
    	
    	// opponent turn:
    	if(turn == opponent){    		
    		for(int i = 1 ; i <= 9 ; i ++){    			
    			int[][] child = boardClone(b);
    			if(child[lastMove][i] == 0){   				 
    				child[lastMove][i] = opponent;	    			
	    			beta = Math.min(beta, alphabeta( child, depth-1, alpha, beta, player,i));
	    			if(alpha >= beta) return beta;
    			}
    		}
    		return beta;
    	}
    	
		return 0;
    }


	/*********************************************************//*
		
		pick the move with good heuristics.
		
	*/
    public static int makeMove() {
		
		int []  choice = new int[10];
		for(int i = 0 ; i <=9 ; i++ ){
			choice[i] = Integer.MIN_VALUE;
		}
    	int heuristic = Integer.MIN_VALUE;
    	int [][] tmp_board = boardClone(boards);
    	int m = 0;
    	for(int i = 1; i <= 9; i++){
    		if(tmp_board[prevMove][i] == 0){
    			int [][] child =  boardClone(tmp_board);
    			child[prevMove][i] = player;	
    			
    			if(getHeuristic(child) == Integer.MAX_VALUE){
    				return i;
    			}

    			int tmpH  =  alphabeta(child,MAX_DEPTH,Integer.MIN_VALUE,Integer.MAX_VALUE,opponent,i);

    		 	if(heuristic <= tmpH){
    		 		choice[i] = tmpH;
					heuristic = tmpH;
					m = i;
				}

    		}
    	}
    	int diff = Integer.MIN_VALUE;
    	
    	//get a good choice
    	for(int i = 1; i <=9; i++){
    		if(choice[i] == heuristic){
    			if(getNumPlayer(boards,i,player) - getNumPlayer(boards,i,opponent)>=diff){
	    			diff = getNumPlayer(boards,i,player) - getNumPlayer(boards,i,opponent);
	    			m = i;
    			}
    		}
    	}


		place(prevMove, m, player);
		prevMove = m;
    	return m;	
    }


    /* get a copy of a board*/
    public static int getNumPlayer(int[][] b, int sb, int turn) {
    	int numP = 0;
  
    	for(int i = 1 ;i<=9;i++)
    		if(b[sb][i] == turn)
    			numP++;
    		
    	return  numP; 
    }

    /* get a copy of a board*/
    public static int[][] boardClone(int[][] b) {

    	int [][] newB =  new int[10][10];
    	
    	for(int i = 1; i <= 9; i++ ) {
		    for(int j = 1; j <= 9; j++ ) {
		    	newB[i][j] = b[i][j];
		    }
  		}
  		return newB;
    }

}