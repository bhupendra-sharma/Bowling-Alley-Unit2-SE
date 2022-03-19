/* ControlDeskView.java
 *
 *  Version:
 *			$Id$
 * 
 *  Revisions:
 * 		$Log$
 * 
 */

/**
 * Class for representation of the control desk
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;

public class ControlDeskView implements  EventObserver{

	private final JFrame win;
	private final JList partyList;
	private final ControlDeskView cdv=this;

	private final ControlDesk controlDesk;

	/**
	 * Displays a GUI representation of the ControlDesk
	 *
	 */

	public ControlDeskView(ControlDesk controlDesk, int maxMembers) {

		this.controlDesk = controlDesk;
		/** The maximum  number of members in a party */
		int numLanes = controlDesk.getNumLanes();

		win = new JFrame("Control Desk");
		win.getContentPane().setLayout(new BorderLayout());
		((JPanel) win.getContentPane()).setOpaque(false);

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new BorderLayout());

		// Controls Panel
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new GridLayout(3, 1));
		controlsPanel.setBorder(new TitledBorder("Controls"));

		JButton addParty = new JButton("Add Party");
		JPanel addPartyPanel = new JPanel();
		addPartyPanel.setLayout(new FlowLayout());
		addParty.addActionListener(actionEvent -> {
			new AddPartyView(cdv, maxMembers);
		});
		addPartyPanel.add(addParty);

		controlsPanel.add(addPartyPanel);

		JButton queryButton = new JButton("Query");
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new FlowLayout());
		queryButton.addActionListener(actionEvent -> {
			new QueryView();
		});
		queryPanel.add(queryButton);


		controlsPanel.add(queryPanel);

		JButton finished = new JButton("Finished");
		JPanel finishedPanel = new JPanel();
		finishedPanel.setLayout(new FlowLayout());
		finished.addActionListener(actionEvent -> {
			win.hide();
			System.exit(0);
		});
		finishedPanel.add(finished);
		controlsPanel.add(finishedPanel);

		// Lane Status Panel
		JPanel laneStatusPanel = new JPanel();
		laneStatusPanel.setLayout(new GridLayout(numLanes, 1));
		laneStatusPanel.setBorder(new TitledBorder("Lane Status"));

		HashSet lanes=controlDesk.getLanes();
		Iterator it = lanes.iterator();
		int laneCount=0;
		while (it.hasNext()) {
			Lane curLane = (Lane) it.next();
			LaneStatusView laneStat = new LaneStatusView(curLane,(laneCount+1));
			curLane.subscribe(laneStat);
			(curLane.getPinsetter()).subscribe(laneStat);
			JPanel lanePanel = laneStat.showLane();
			lanePanel.setBorder(new TitledBorder("Lane" + ++laneCount ));
			laneStatusPanel.add(lanePanel);
		}

		// Party Queue Panel
		JPanel partyPanel = new JPanel();
		partyPanel.setLayout(new FlowLayout());
		partyPanel.setBorder(new TitledBorder("Party Queue"));

		Vector empty = new Vector();
		empty.add("(Empty)");

		partyList = new JList(empty);
		partyList.setFixedCellWidth(120);
		partyList.setVisibleRowCount(10);
		JScrollPane partyPane = new JScrollPane(partyList);
		partyPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		partyPanel.add(partyPane);
		//		partyPanel.add(partyList);

		// Clean up main panel
		colPanel.add(controlsPanel, "East");
		colPanel.add(laneStatusPanel, "Center");
		colPanel.add(partyPanel, "West");

		win.getContentPane().add("Center", colPanel);

		win.pack();

		/* Close program when this window closes */
		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Center Window on Screen
		Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
		win.setLocation(
			((screenSize.width) / 2) - ((win.getSize().width) / 2),
			((screenSize.height) / 2) - ((win.getSize().height) / 2));
		win.show();

	}

	/**
	 * Handler for actionEvents
	 *
	 * @param e	the ActionEvent that triggered the handler
	 *
	 */


	/**
	 * Receive a new party from andPartyView.
	 *
	 * @param party representing vector of members in a party
	 *
	 */

	public void updateAddParty(Vector party) {
		controlDesk.addPartyQueue(party);
	}

	public void receiveEvent(Object eventObject){
		if(eventObject instanceof Vector){
			partyList.setListData((Vector)eventObject);
		}
	}

}
