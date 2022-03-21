import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Date;

public class Lane extends Thread implements EventObserver {
	private Vector<Bowler> party;
	private final Pinsetter setter;
	private final HashMap scores;
	private final Vector subscribers;

	private boolean gameIsHalted;

	private boolean partyAssigned;
	private boolean gameFinished;
	private Iterator bowlerIterator;
	private int ball;
	private int bowlIndex;
	private int frameNumber;
	private boolean tenthFrameStrike;

	private boolean simulate_game = false;
	private boolean perform_throw = true;

	private int[][] cumulScores;
	private boolean canThrowAgain;

	private int[][] finalScores;
	private int gameNumber;

	private Bowler currentThrower;			// = the thrower who just took a throw

	public boolean get_simulate_game(){ 	return simulate_game;	}
	public void set_simulate_game(boolean val){		simulate_game=val;	}

	public boolean get_perform_throw(){ 	return perform_throw;	}
	public void set_perform_throw(boolean val){		perform_throw=val;	}

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
		partyAssigned = false;

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
			if (partyAssigned && !gameFinished) {	// we have a party on this lane,
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
							perform_throw=false;
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
					resetBowlerIterator();
					bowlIndex = 0;
					if (frameNumber > 9) {
						gameFinished = true;
						gameNumber++;
					}
				}
			} else if (partyAssigned && gameFinished) {

				//Code for point 5


				EndGamePrompt egp = new EndGamePrompt( (party.get(0)).getNick() + "'s Party" );
				int result = egp.getResult();
				egp.destroy();

				System.out.println("result was: " + result);

				// TODO: send record of scores to control desk
				if (result == 1) {					// yes, want to play again
					resetScores();
					resetBowlerIterator();

				} else if (result == 2) {// no, dont want to play another game
					Vector printVector;
					EndGameReport egr = new EndGameReport( (party.get(0)).getNick() + "'s Party", party);
					printVector = egr.getResult();
					partyAssigned = false;
					Iterator scoreIt = party.iterator();
					party = null;
					partyAssigned = false;

					publish(this);

					int myIndex = 0;
					while (scoreIt.hasNext()){
						Bowler thisBowler = (Bowler)scoreIt.next();
						ScoreReport sr = new ScoreReport( thisBowler, finalScores[myIndex++], gameNumber );
						//sr.sendEmail(thisBowler.getEmail());
						Iterator printIt = printVector.iterator();
						while (printIt.hasNext()){
							if (thisBowler.getNick() == (String)printIt.next()){
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

	/** resetBowlerIterator()
	 *
	 * sets the current bower iterator back to the first bowler
	 *
	 * @pre the party as been assigned
	 * @post the iterator points to the first bowler in the party
	 */
	private void resetBowlerIterator() {
		bowlerIterator = party.iterator();
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
			int[] toPut = new int[30];
			for ( int i = 0; i != 30; i++){
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
		resetBowlerIterator();
		partyAssigned = true;

		int[] curScores = new int[party.size()];
		cumulScores = new int[party.size()][14];
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


		curScore[ index - 1] = score;
		scores.put(Cur, curScore);
		CalculateScore scr=new CalculateScore(ball,scores,cumulScores,bowlIndex);
		scr.getScore( Cur, frame );
		publish( this );
	}

	/** isPartyAssigned()
	 *
	 * checks if a party is assigned to this lane
	 *
	 * @return true if party assigned, false otherwise
	 */
	public boolean isPartyAssigned() {
		return partyAssigned;
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
	 * Accessor to get this Lane's pinsetter
	 *
	 * @return		A reference to this lane's pinsetter
	 */

	public Pinsetter getPinsetter() {
		return setter;
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

	public int getFrameNum() {
		return frameNumber+1;
	}

	public HashMap getScore( ) {
		return scores;
	}


	public int getIndex() {
		return bowlIndex;
	}

	public int getBall( ) {
		return ball;
	}

	public int[][] getCumulScore(){
		return cumulScores;
	}

	public Vector<Bowler> getParty() {
		return party;
	}

	public String getBowlerNickName() {
		return currentThrower.getNick();
	}

	public boolean isMechanicalProblem(){
		return gameIsHalted;
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


}
