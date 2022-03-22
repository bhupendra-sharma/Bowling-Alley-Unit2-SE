import java.util.*;

class ControlDesk extends Thread {

	/** The collection of Lanes */
	private final HashSet<Lane> lanes;

	/** The party wait queue */
	private final Queue<Vector<Bowler>> partyQueue;

	/** The number of lanes represented */
	private final int numLanes;
	
	/** The collection of subscribers */
	private final Vector<EventObserver> subscribers;

    /**
     * Constructor for the ControlDesk class
     *
     * @param numLanes	the numbler of lanes to be represented
     *
     */

	public ControlDesk(int numLanes) {
		this.numLanes = numLanes;
		lanes = new HashSet<>(numLanes);
		partyQueue = new LinkedList<>();

		subscribers = new Vector<>();

		for (int i = 0; i < numLanes; i++) {
			lanes.add(new Lane());
		}
		
		this.start();

	}
	
	/**
	 * Main loop for ControlDesk's thread
	 * 
	 */
	public void run() {
		while (true) {
			
			assignLane();
			
			try {
				sleep(250);
			} catch (Exception e) {}
		}

	}

    /**
     * Iterate through the available lanes and assign the paties in the wait queue if lanes are available.
     *
     */

	public void assignLane() {
		Iterator<Lane> it = lanes.iterator();

		while (it.hasNext() && !partyQueue.isEmpty()) {
			Lane curLane = it.next();

			if (!curLane.isPartyAssigned) {
				System.out.println("ok... assigning this party");
				curLane.assignParty(partyQueue.poll());
			}
		}
		publish(getPartyQueue());
	}

    /**
     * Creates a party from a Vector of nickNAmes and adds them to the wait queue.
     *
     * @param partyNicks	A Vector of NickNames
     *
     */

	public void addPartyQueue(Vector<String> partyNicks) {
		Vector<Bowler> partyBowlers = new Vector<>();
		for (String partyNick : partyNicks) {
			Bowler newBowler = registerPatron(partyNick);
			partyBowlers.add(newBowler);
		}
		partyQueue.add(partyBowlers);
		publish(getPartyQueue());
	}

    /**
     * Accessor for the number of lanes represented by the ControlDesk
     * 
     * @return an int containing the number of lanes represented
     *
     */

	public int getNumLanes() {
		return numLanes;
	}

    /**
     * Allows objects to subscribe as observers
     * 
     * @param adding	the ControlDeskObserver that will be subscribed
     *
     */

	public void subscribe(EventObserver adding) {
		subscribers.add(adding);
	}

    /**
     * Broadcast an event to subscribing objects.
     * 
     * @param eventObject	the ControlDeskEvent to broadcast
     *
     */

	public void publish(Vector<String> eventObject) {
		for (EventObserver subscriber : subscribers) {
			subscriber.receiveEvent(eventObject);
		}
	}

    /**
     * Accessor method for lanes
     * 
     * @return a HashSet of Lanes
     *
     */

	public HashSet<Lane> getLanes() {
		return lanes;
	}

	/**
	 * Returns a Vector of party names to be displayed in the GUI representation of the wait queue.
	 *
	 * @return a Vecotr of Strings
	 *
	 */

	private Vector<String> getPartyQueue() {
		Vector<String> displayPartyQueue = new Vector<>();
		for (Vector<Bowler> party: partyQueue) {
			String nextParty =
					(party.get(0)).getNick() + "'s Party";
			displayPartyQueue.addElement(nextParty);
		}
		return displayPartyQueue;
	}



	/**
	 * Retrieves a matching Bowler from the bowler database.
	 *
	 * @param nickName	The NickName of the Bowler
	 *
	 * @return a Bowler object.
	 *
	 */

	private Bowler registerPatron(String nickName) {
		Bowler patron;
		Dao<Bowler> bowlerDao = new BowlerDao();
		patron = bowlerDao.getByParam(nickName);
		return patron;
	}


}

