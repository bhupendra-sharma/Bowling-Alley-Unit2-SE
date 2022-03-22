import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Date;

public class Lane extends Thread implements EventObserver {
	private final Vector<EventObserver> subscribers;
	private boolean gameFinished;
	private Iterator bowlerIterator;
	private boolean tenthFrameStrike;
	private boolean canThrowAgain;
	private int[][] finalScores;
	private int gameNumber;
	private Bowler currentThrower;
	private int frameNumber;

	public Vector<Bowler> party;
	public final HashMap scores;
	public boolean gameIsHalted;
	public int ball;
	public int bowlIndex;
	public int[][] cumulScores;
	public boolean simulate_game = false;
	public boolean perform_throw = true;
	public final Pinsetter setter;
	public boolean isPartyAssigned;


	/** Lane()
	 *
	 * Constructs a new lane and starts its thread
	 *
	 * @pre none
	 * @post a new lane has been created and its thered is executing
	 */


	public Lane() {
		setter = new Pinsetter();
		scores = new HashMap();
		subscribers = new Vector();

		gameIsHalted = false;
		isPartyAssigned = false;

		gameNumber = 0;

		setter.subscribe( this );

		this.start();
	}

	/** run()
	 *
	 * entry point for execution of this lane
	 */
	public void run() {

		while (true) {
			if (isPartyAssigned && !gameFinished) {	// we have a party on this lane,
				// so next bower can take a throw

				while (gameIsHalted) {
					try {
						sleep(10);
					} catch (Exception e) {}
				}


				if (bowlerIterator.hasNext()) {
					currentThrower = (Bowler)bowlerIterator.next();

					canThrowAgain = true;
					tenthFrameStrike = false;
					ball = 0;

					while (canThrowAgain) {
						try {
							sleep(10);
						} catch (Exception e) {}
						if(simulate_game){
							setter.ballThrown();        // simulate the thrower's ball hiting
							ball++;
							continue;
						}
						if(perform_throw) {
							setter.ballThrown();        // simulate the thrower's ball hiting
							ball++;
							perform_throw = false;
						}
					}

					if (frameNumber == 9){
						finalScores[bowlIndex][gameNumber] = cumulScores[bowlIndex][9];
						try{
							Date date = new Date();
							Dao<Score> scoreDao = new ScoreDao();
							String dateString = "" + date.getHours() + ":" + date.getMinutes() + " " + date.getMonth() + "/" + date.getDay() + "/" + (date.getYear() + 1900);
							scoreDao.save(new Score(currentThrower.getNick(), dateString
									, Integer.toString(cumulScores[bowlIndex][9])));
						} catch (Exception e) {System.err.println("Exception in addScore. "+ e );}
					}


					setter.reset();
					bowlIndex++;

				} else {
					frameNumber++;
					bowlerIterator = party.iterator();
					bowlIndex = 0;
					if (frameNumber > 9) {
						gameFinished = true;
						gameNumber++;
					}
				}

			} else if (isPartyAssigned) {
				updateScoreOnGutterCondition();
				publish(null);
				EndGamePrompt egp = new EndGamePrompt( (party.get(0)).getNick() + "'s Party" );
				int result = egp.getResult();
				egp.destroy();

				System.out.println("result was: " + result);

				// TODO: send record of scores to control desk
				if (result == 1) {					// yes, want to play again
					resetScores();
					bowlerIterator = party.iterator();

				} else if (result == 2) {// no, dont want to play another game
					Vector printVector;
					EndGameReport egr = new EndGameReport( (party.get(0)).getNick() + "'s Party", party);
					printVector = egr.getResult();
					isPartyAssigned = false;
					Iterator scoreIt = party.iterator();
					party = null;
					isPartyAssigned = false;

					publish(this);

					int myIndex = 0;
					while (scoreIt.hasNext()){
						Bowler thisBowler = (Bowler)scoreIt.next();
						ScoreReport sr = new ScoreReport( thisBowler, finalScores[myIndex++], gameNumber );
						//sr.sendEmail(thisBowler.getEmail());
						Iterator printIt = printVector.iterator();
						while (printIt.hasNext()){
							if (thisBowler.getNick().equals(printIt.next())){
								System.out.println("Printing " + thisBowler.getNick());
								sr.sendPrintout();
							}
						}

					}
				}
			}


			try {
				sleep(10);
			} catch (Exception e) {}
		}
	}

	public void receiveEvent(Object eventObject){
		if(eventObject instanceof Pinsetter){
			if (((Pinsetter) eventObject).getPinsDownInThrow() >=  0) {			// this is a real throw
				markScore(currentThrower, frameNumber + 1, ((Pinsetter)eventObject).getThrowNumber(),
						((Pinsetter)eventObject).getPinsDownInThrow());

				// next logic handles the ?: what conditions dont allow them another throw?
				// handle the case of 10th frame first
				if (frameNumber == 9) {
					if (((Pinsetter)eventObject).totalPinsDown() == 10) {
						setter.resetPins();
						if(((Pinsetter)eventObject).getThrowNumber() == 1) {
							tenthFrameStrike = true;
						}
					}

					if ((((Pinsetter)eventObject).totalPinsDown() != 10) &&
							(((Pinsetter)eventObject).getThrowNumber() == 2 && !tenthFrameStrike)) {
						canThrowAgain = false;
						//publish( lanePublish() );
					}

					if (((Pinsetter)eventObject).getThrowNumber() == 3) {
						canThrowAgain = false;
						//publish( lanePublish() );
					}

				} else { // its not the 10th frame

					if (((Pinsetter)eventObject).getPinsDownInThrow() == 10) {		// threw a strike
						canThrowAgain = false;
						//publish( lanePublish() );
					} else if (((Pinsetter)eventObject).getThrowNumber() == 2) {
						canThrowAgain = false;
						//publish( lanePublish() );
					} else if (((Pinsetter)eventObject).getThrowNumber() == 3)
						System.out.println("I'm here...");
				}
			}
		}
	}

	public String getBowlerNickName(){
		return currentThrower.getNick();
	}

	public int getFrameNumber(){
		return frameNumber + 1;
	}

	/** resetScores()
	 *
	 * resets the scoring mechanism, must be called before scoring starts
	 *
	 * @pre the party has been assigned
	 * @post scoring system is initialized
	 */
	private void resetScores() {
		Iterator bowlIt = party.iterator();

		while ( bowlIt.hasNext() ) {
			int[] toPut = new int[25];
			for ( int i = 0; i != 25; i++){
				toPut[i] = -1;
			}
			scores.put( bowlIt.next(), toPut );
		}

		gameFinished = false;
		frameNumber = 0;
	}

	/** assignParty()
	 *
	 * assigns a party to this lane
	 *
	 * @pre none
	 * @post the party has been assigned to the lane
	 *
	 * @param theParty		Party to be assigned
	 */
	public void assignParty( Vector<Bowler> theParty ) {
		party = theParty;
		bowlerIterator = party.iterator();
		isPartyAssigned = true;

		cumulScores = new int[party.size()][10];
		finalScores = new int[party.size()][128]; //Hardcoding a max of 128 games, bite me.
		gameNumber = 0;

		resetScores();
	}

	/** markScore()
	 *
	 * Method that marks a bowlers score on the board.
	 *
	 * @param Cur		The current bowler
	 * @param frame	The frame that bowler is on
	 * @param ball		The ball the bowler is on
	 * @param score	The bowler's score
	 */
	private void markScore( Bowler Cur, int frame, int ball, int score ){
		int[] curScore;
		int index =  ( (frame - 1) * 2 + ball);

		curScore = (int[]) scores.get(Cur);

		curScore[index - 1] = score;
		scores.put(Cur, curScore);
		getScore( Cur, frame );

		publish( this );
	}

	/** subscribe
	 *
	 * Method that will add a subscriber
	 *
	 * @param adding	Observer that is to be added
	 */

	public void subscribe( EventObserver adding ) {
		subscribers.add( adding );
	}

	/**
	 * Pause the execution of this game
	 */
	public void pauseGame() {
		gameIsHalted = true;
		publish(this);
	}

	/**
	 * Resume the execution of this game
	 */
	public void unPauseGame() {
		gameIsHalted = false;
		publish(this);
	}

	/** publish
	 *
	 * Method that publishes an event to subscribers
	 *
	 * @param lane	Event that is to be published
	 */

	private void publish( Lane lane ) {
		if( subscribers.size() > 0 ) {
			Iterator eventIterator = subscribers.iterator();

			while ( eventIterator.hasNext() ) {
				( (EventObserver) eventIterator.next()).receiveEvent( lane );
			}
		}
	}

	private void publishInt(Integer flag){
		if( subscribers.size() > 0 ) {
			Iterator eventIterator = subscribers.iterator();

			while ( eventIterator.hasNext() ) {
				( (EventObserver) eventIterator.next()).receiveEvent(1);
			}
		}
	}

	private int getStrikeBalls(int[] curScore,int i){
		int strikeballs=0;
		if (curScore[i+2] != -1) {
			strikeballs = 1;
			if(curScore[i+3] != -1 || curScore[i+4] != -1) {
				strikeballs = 2;
			}
		}
		return strikeballs;
	}

	private void performNormalThrow(int[] curScore,int i)
	{
		if( i%2 == 0 && i < 18){
			if ( i/2 == 0 ) {
				//First frame, first ball.  Set his cumul score to the first ball
				if(curScore[i] != -2){
					cumulScores[bowlIndex][i/2] += curScore[i];
				}
			} else if (i/2 != 9){
				//add his last frame's cumul to this ball, make it this frame's cumul.
				if(curScore[i] != -2){
					cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1] + curScore[i];
				} else {
					cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1];
				}
			}
		} else if (i < 18){
			if(curScore[i] != -1 && i > 2){
				if(curScore[i] != -2){
					cumulScores[bowlIndex][i/2] += curScore[i];
				}
			}
		}
		if (i/2 == 9){
			if (i == 18){
				cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
			}
			if(curScore[i] != -2){
				cumulScores[bowlIndex][9] += curScore[i];
			}
		} else if (i/2 == 10) {
			if(curScore[i] != -2){
				cumulScores[bowlIndex][9] += curScore[i];
			}
		}
	}

	private void getScore(Bowler Cur, int frame) {
		int[] curScore;
		int strikeballs = 0;
		curScore = (int[]) scores.get(Cur);
		for (int i = 0; i != 10; i++){
			cumulScores[bowlIndex][i] = 0;
		}
		int current = 2*(frame - 1) + ball - 1;
		//Iterate through each ball until the current one.
		for (int i = 0; i != current+2; i++){
			//Spare:
			if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
				//This ball was a the second of a spare.
				//Also, we're not on the current ball.
				//Add the next ball to the ith one in cumul.
				cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];

			} else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
				strikeballs = getStrikeBalls(curScore,i);
				//This ball is the first ball, and was a strike.
				//If we can get 2 balls after it, good add them to cumul.

				if (strikeballs == 2){
					//Add up the strike.
					//Add the next two balls to the current cumulscore.
					cumulScores[bowlIndex][i/2] += 10;

					if ( i/2 > 0 ){
						cumulScores[bowlIndex][i/2] += curScore[i+2] + cumulScores[bowlIndex][(i/2)-1];
					} else {
						cumulScores[bowlIndex][i/2] += curScore[i+2];
					}
					if (curScore[i+3] != -1){
						if( curScore[i+3] != -2){
							cumulScores[bowlIndex][(i/2)] += curScore[i+3];
						}
					} else {
						cumulScores[bowlIndex][(i/2)] += curScore[i+4];
					}

				} else {
					break;
				}
			}else {
				//We're dealing with a normal throw, add it and be on our way.
				performNormalThrow(curScore,i);
			}
		}
	}

	private void updateScoreOnGutterCondition(){
		Iterator<Bowler> bowlerIterator = party.iterator();
		boolean singleFlag = false, doubleFlag = false;
		int maxScore = -1, bowlerIndex = 0;
		while (bowlerIterator.hasNext()){
			Bowler bowler = bowlerIterator.next();
			int[] bowlerScores = (int[]) scores.get(bowler);
			for(int i = 0; i < 20; i+=2){
				if(bowlerScores[i] == 0 && bowlerScores[i + 1] == 0){
					singleFlag = true;
					doubleFlag = true;
				}
				if(bowlerScores[i] > maxScore){
					maxScore = bowlerScores[i];
				}
			}


			if(bowlerScores[20] > maxScore){
				maxScore = bowlerScores[20];
			}

			if(singleFlag){
				//case of first 2 being gutters
				if(bowlerScores[0] == 0 && bowlerScores[1] == 0){
					for(int i = 0; i < bowlerScores.length; i++){
						if(bowlerScores[i] > 0){
							maxScore = bowlerScores[i];
							break;
						}
					}
				}
				cumulScores[bowlerIndex][9] -= maxScore/2;
			}
			singleFlag = false;
		}

		if(doubleFlag){
			publish(this);
		}
	}

}
