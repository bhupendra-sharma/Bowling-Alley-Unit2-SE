/**
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class LaneStatusView implements EventObserver {

	private final JPanel jp;

	private final JLabel curBowler;
	private final JLabel pinsDown;
	private final JButton viewLane;
	private final JButton viewPinSetter;
	private final JButton maintenance;

	private final PinSetterView psv;
	private final LaneView lv;
	private final Lane lane;
	int laneNum;

	boolean laneShowing;
	boolean psShowing;

	public LaneStatusView(Lane lane, int laneNum ) {

		this.lane = lane;
		this.laneNum = laneNum;

		laneShowing=false;
		psShowing=false;

		psv = new PinSetterView( laneNum );
		Pinsetter ps = lane.setter;
		ps.subscribe(psv);

		lv = new LaneView( lane, laneNum );
		lane.subscribe(lv);


		jp = new JPanel();
		jp.setLayout(new FlowLayout());
		JLabel cLabel = new JLabel( "Now Bowling: " );
		curBowler = new JLabel( "(no one)" );
		JLabel pdLabel = new JLabel( "Pins Down: " );
		pinsDown = new JLabel( "0" );

		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		Insets buttonMargin = new Insets(4, 4, 4, 4);

		viewLane = new JButton("View Lane");
		JPanel viewLanePanel = new JPanel();
		viewLanePanel.setLayout(new FlowLayout());
		viewLane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if ( lane.isPartyAssigned ) {
					if (!laneShowing) {
						lv.show();
						laneShowing=true;
					} else {
						lv.hide();
						laneShowing=false;
					}
				}
			}
		});
		viewLanePanel.add(viewLane);

		viewPinSetter = new JButton("Pinsetter");
		JPanel viewPinSetterPanel = new JPanel();
		viewPinSetterPanel.setLayout(new FlowLayout());
		viewPinSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if ( lane.isPartyAssigned ) {
						if (!psShowing) {
							psv.show();
							psShowing=true;
						} else {
							psv.hide();
							psShowing=false;
						}

				}
			}
		});
		viewPinSetterPanel.add(viewPinSetter);

		maintenance = new JButton("     ");
		maintenance.setBackground( Color.GREEN );
		JPanel maintenancePanel = new JPanel();
		maintenancePanel.setLayout(new FlowLayout());
		maintenance.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if ( lane.isPartyAssigned ) {
					lane.unPauseGame();
					maintenance.setBackground( Color.GREEN );
				}
			}
		});
		maintenancePanel.add(maintenance);

		viewLane.setEnabled( false );
		viewPinSetter.setEnabled( false );


		buttonPanel.add(viewLanePanel);
		buttonPanel.add(viewPinSetterPanel);
		buttonPanel.add(maintenancePanel);

		jp.add( cLabel );
		jp.add( curBowler );
		jp.add( pdLabel );
		jp.add( pinsDown );
		
		jp.add(buttonPanel);

	}

	public JPanel showLane() {
		return jp;
	}


	public void receiveEvent(Object eventObject){
		if(eventObject instanceof Pinsetter){
			pinsDown.setText(Integer.toString(((Pinsetter) eventObject).totalPinsDown()));
		}
		else if(eventObject instanceof Lane){
			curBowler.setText( ( ((Lane)eventObject).getBowlerNickName()) );
			if ( ((Lane)eventObject).gameIsHalted ) {
				maintenance.setBackground( Color.RED );
			}
			if (!lane.isPartyAssigned) {
				viewLane.setEnabled( false );
				viewPinSetter.setEnabled( false );
			} else {
				viewLane.setEnabled( true );
				viewPinSetter.setEnabled( true );
			}
		}
	}

}
