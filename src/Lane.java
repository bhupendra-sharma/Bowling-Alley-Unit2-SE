
/* $Id$
 *
 * Revisions:
 *   $Log: Lane.java,v $
 *   Revision 1.52  2003/02/20 20:27:45  ???
 *   Fouls disables.
 *
 *   Revision 1.51  2003/02/20 20:01:32  ???
 *   Added things.
 *
 *   Revision 1.50  2003/02/20 19:53:52  ???
 *   Added foul support.  Still need to update laneview and test this.
 *
 *   Revision 1.49  2003/02/20 11:18:22  ???
 *   Works beautifully.
 *
 *   Revision 1.48  2003/02/20 04:10:58  ???
 *   Score reporting code should be good.
 *
 *   Revision 1.47  2003/02/17 00:25:28  ???
 *   Added disbale controls for View objects.
 *
 *   Revision 1.46  2003/02/17 00:20:47  ???
 *   fix for event when game ends
 *
 *   Revision 1.43  2003/02/17 00:09:42  ???
 *   fix for event when game ends
 *
 *   Revision 1.42  2003/02/17 00:03:34  ???
 *   Bug fixed
 *
 *   Revision 1.41  2003/02/16 23:59:49  ???
 *   Reporting of sorts.
 *
 *   Revision 1.40  2003/02/16 23:44:33  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.39  2003/02/16 23:43:08  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.38  2003/02/16 23:41:05  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.37  2003/02/16 23:00:26  ???
 *   added mechnanical problem flag
 *
 *   Revision 1.36  2003/02/16 21:31:04  ???
 *   Score logging.
 *
 *   Revision 1.35  2003/02/09 21:38:00  ???
 *   Added lots of comments
 *
 *   Revision 1.34  2003/02/06 00:27:46  ???
 *   Fixed a race condition
 *
 *   Revision 1.33  2003/02/05 11:16:34  ???
 *   Boom-Shacka-Lacka!!!
 *
 *   Revision 1.32  2003/02/05 01:15:19  ???
 *   Real close now.  Honest.
 *
 *   Revision 1.31  2003/02/04 22:02:04  ???
 *   Still not quite working...
 *
 *   Revision 1.30  2003/02/04 13:33:04  ???
 *   Lane may very well work now.
 *
 *   Revision 1.29  2003/02/02 23:57:27  ???
 *   fix on pinsetter hack
 *
 *   Revision 1.28  2003/02/02 23:49:48  ???
 *   Pinsetter generates an event when all pins are reset
 *
 *   Revision 1.27  2003/02/02 23:26:32  ???
 *   ControlDesk now runs its own thread and polls for free lanes to assign queue members to
 *
 *   Revision 1.26  2003/02/02 23:11:42  ???
 *   parties can now play more than 1 game on a lane, and lanes are properly released after games
 *
 *   Revision 1.25  2003/02/02 22:52:19  ???
 *   Lane compiles
 *
 *   Revision 1.24  2003/02/02 22:50:10  ???
 *   Lane compiles
 *
 *   Revision 1.23  2003/02/02 22:47:34  ???
 *   More observering.
 *
 *   Revision 1.22  2003/02/02 22:15:40  ???
 *   Add accessor for pinsetter.
 *
 *   Revision 1.21  2003/02/02 21:59:20  ???
 *   added conditions for the party choosing to play another game
 *
 *   Revision 1.20  2003/02/02 21:51:54  ???
 *   LaneEvent may very well be observer method.
 *
 *   Revision 1.19  2003/02/02 20:28:59  ???
 *   fixed sleep thread bug in lane
 *
 *   Revision 1.18  2003/02/02 18:18:51  ???
 *   more changes. just need to fix scoring.
 *
 *   Revision 1.17  2003/02/02 17:47:02  ???
 *   Things are pretty close to working now...
 *
 *   Revision 1.16  2003/01/30 22:09:32  ???
 *   Worked on scoring.
 *
 *   Revision 1.15  2003/01/30 21:45:08  ???
 *   Fixed speling of received in Lane.
 *
 *   Revision 1.14  2003/01/30 21:29:30  ???
 *   Fixed some MVC stuff
 *
 *   Revision 1.13  2003/01/30 03:45:26  ???
 *   *** empty log message ***
 *
 *   Revision 1.12  2003/01/26 23:16:10  ???
 *   Improved thread handeling in lane/controldesk
 *
 *   Revision 1.11  2003/01/26 22:34:44  ???
 *   Total rewrite of lane and pinsetter for R2's observer model
 *   Added Lane/Pinsetter Observer
 *   Rewrite of scoring algorythm in lane
 *
 *   Revision 1.10  2003/01/26 20:44:05  ???
 *   small changes
 *
 * 
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

	private int[][] cumulScores;
	private boolean canThrowAgain;
	
	private int[][] finalScores;
	private int gameNumber;
	
	private Bowler currentThrower;			// = the thrower who just took a throw

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
						setter.ballThrown();		// simulate the thrower's ball hiting
						ball++;
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
				EndGamePrompt egp = new EndGamePrompt( (party.get(0)).getNick() + "'s Party" );
				int result = egp.getResult();
				egp.destroy();
				egp = null;
				
				
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
		resetBowlerIterator();
		partyAssigned = true;

		int[] curScores = new int[party.size()];
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
