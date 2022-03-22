import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class LaneView implements EventObserver {
	private boolean initDone = true;

	private final JFrame frame;
	private final Container cpanel;
	private Vector bowlers;

	private JLabel[][] ballLabel;
	private JLabel[][] scoreLabel;

	private final Lane lane;

	private int first_scorer = -1 , second_scorer = -1 ,first=-1,second=-1;
	private int[][] lescores;
	private int scr;

	public void computeLeader(){
		for(int i = 0;i < lane.party.size(); i++)
		{
			if(lescores[i][9]>first)
			{
				first=lescores[i][9];
				first_scorer=i;
			}
		}
		for(int i=0; i < lane.party.size(); i++)
		{
			if(lescores[i][9]>second && i!=first_scorer)
			{
				second=lescores[i][9];
				second_scorer=i;
			}
		}
		if(first_scorer!=-1)
			System.out.println("first_scorer"+bowlers.get(first_scorer)+"Score:"+first);
		if(second_scorer!=-1)
			System.out.println("second_scorer"+bowlers.get(second_scorer)+"Score:"+second);

	}
	public LaneView(Lane lane, int laneNum) {

		this.lane = lane;

		frame = new JFrame("Lane " + laneNum + ":");
		cpanel = frame.getContentPane();
		cpanel.setLayout(new BorderLayout());

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});

		cpanel.add(new JPanel());

	}

	public void show() {
		frame.setVisible(true);
	}

	public void hide() {
		frame.setVisible(false);
	}

	private JPanel makeFrame(Vector<Bowler> party) {

		initDone = false;
		bowlers = party;
		int numBowlers = bowlers.size();

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));

		JPanel[][] balls = new JPanel[numBowlers][32];
		ballLabel = new JLabel[numBowlers][32];
		JPanel[][] scores = new JPanel[numBowlers][16];
		scoreLabel = new JLabel[numBowlers][15];
		JPanel[][] ballGrid = new JPanel[numBowlers][15];
		JPanel[] pins = new JPanel[numBowlers];

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != 31; j++) {
				ballLabel[i][j] = new JLabel(" ");
				balls[i][j] = new JPanel();
				balls[i][j].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				balls[i][j].add(ballLabel[i][j]);
			}

			int j=31;
			ballLabel[i][j] = new JLabel(" ",JLabel.CENTER);
			balls[i][j] = new JPanel();
			balls[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			balls[i][j].add(ballLabel[i][j]);
		}

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != 9; j++) {
				ballGrid[i][j] = new JPanel();
				ballGrid[i][j].setLayout(new GridLayout(0, 3));
				ballGrid[i][j].add(new JLabel("  "), BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j], BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j + 1], BorderLayout.EAST);
			}
			int j = 9;
			ballGrid[i][j] = new JPanel();
			ballGrid[i][j].setLayout(new GridLayout(0, 3));
			ballGrid[i][j].add(balls[i][2 * j]);
			ballGrid[i][j].add(balls[i][2 * j + 1]);
			ballGrid[i][j].add(balls[i][2 * j + 2]);

			//Hidden 11,12,13 th frame for computing leader
			for (j = 10; j <= 13; j++) {
				ballGrid[i][j] = new JPanel();
				ballGrid[i][j].setLayout(new GridLayout(0, 3));
				ballGrid[i][j].add(new JLabel("  "), BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j + 1], BorderLayout.EAST);
				ballGrid[i][j].add(balls[i][2 * j + 2], BorderLayout.EAST);
			}


			//Emoticon box
			j = 14;
			ballGrid[i][j] = new JPanel();
			ballGrid[i][j].setLayout(new GridLayout(0, 1));
			ballGrid[i][j].add(balls[i][29]);
		}

		for (int i = 0; i != numBowlers; i++) {
			pins[i] = new JPanel();
			pins[i].setBorder(
					BorderFactory.createTitledBorder(
							((Bowler) bowlers.get(i)).getNick()));
			pins[i].setLayout(new GridLayout(0, 15));

			for (int k = 0; k != 14; k++) {
				scores[i][k] = new JPanel();
				scoreLabel[i][k] = new JLabel("  ", SwingConstants.CENTER);
				scores[i][k].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				scores[i][k].setLayout(new GridLayout(0, 1));
				scores[i][k].add(ballGrid[i][k], BorderLayout.EAST);
				scores[i][k].add(scoreLabel[i][k], BorderLayout.SOUTH);
				//if(k>=10)
				//	scores[i][k].setVisible(false);
				pins[i].add(scores[i][k], BorderLayout.EAST);
			}

			int j=14;
			scores[i][j] = new JPanel();
			scoreLabel[i][j] = new JLabel("  ", SwingConstants.CENTER);
			scores[i][j].setBorder(
					BorderFactory.createLineBorder(Color.BLACK));
			scores[i][j].setLayout(new GridLayout(0, 1));
			scores[i][j].add(ballGrid[i][j], BorderLayout.CENTER);
			pins[i].add(scores[i][j], BorderLayout.EAST);
			panel.add(pins[i]);
		}

		initDone = true;
		return panel;
	}

	public void receiveEvent(Object eventObject) {
		if(eventObject instanceof Lane) {
			if (lane.isPartyAssigned) {
				int numBowlers = ((Lane)eventObject).party.size();
				while (!initDone) {
					//System.out.println("chillin' here.");
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}

				if (((Lane)eventObject).getFrameNumber() == 1
						&& ((Lane)eventObject).ball == 0
						&& ((Lane)eventObject).bowlIndex == 0) {
					System.out.println("Making the frame.");
					cpanel.removeAll();
					cpanel.add(makeFrame(((Lane)eventObject).party), "Center");

					// Button Panel
					JPanel buttonPanel = new JPanel();
					buttonPanel.setLayout(new FlowLayout());

					JButton maintenance = new JButton("Maintenance Call");
					JPanel maintenancePanel = new JPanel();
					maintenancePanel.setLayout(new FlowLayout());
					maintenance.addActionListener(actionEvent -> lane.pauseGame());
					maintenancePanel.add(maintenance);

					JButton simulate = new JButton("Simulate");
					JPanel simulatePanel = new JPanel();
					simulatePanel.setLayout(new FlowLayout());
					simulate.addActionListener(actionEvent -> {
						lane.simulate_game = true;
						System.out.println("Simulate Pressed:" + lane.simulate_game);
					});
					simulatePanel.add(simulate);

					JButton performThrow = new JButton("Perform Throw");
					JPanel PerformThrowPanel = new JPanel();
					PerformThrowPanel.setLayout(new FlowLayout());
					performThrow.addActionListener(actionEvent -> {
						lane.perform_throw = true;
						System.out.println("Perform Throw Pressed:" + lane.perform_throw);
					});
					PerformThrowPanel.add(performThrow);

					buttonPanel.add(maintenancePanel);
					buttonPanel.add(PerformThrowPanel);
					buttonPanel.add(simulatePanel);

					cpanel.add(buttonPanel, "South");

					frame.pack();

				}

				lescores = ((Lane)eventObject).cumulScores;
				for (int k = 0; k < numBowlers; k++) {
					for (int i = 0; i <= ((Lane)eventObject).getFrameNumber() - 1; i++) {
						if (lescores[k][i] != 0)
							scoreLabel[k][i].setText(
									(Integer.toString(lescores[k][i])));
						int scr;

						if(i==0)
							scr=lescores[k][i];
						else
							scr=lescores[k][i]-lescores[k][i-1];

						ImageIcon iconLogo;
						if(scr==10)
							iconLogo = new ImageIcon(new ImageIcon("img/10.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						if(scr==9)
							iconLogo = new ImageIcon(new ImageIcon("img/9.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						if(scr==8)
							iconLogo = new ImageIcon(new ImageIcon("img/8.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						if(scr==7)
							iconLogo = new ImageIcon(new ImageIcon("img/7.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						else if(scr<=6 && scr>=5)
							iconLogo = new ImageIcon(new ImageIcon("img/6-5.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						else if(scr<=4 && scr>=3)
							iconLogo = new ImageIcon(new ImageIcon("img/4-3.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						else if(scr<=2 && scr>=1)
							iconLogo = new ImageIcon(new ImageIcon("img/2-1.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						else if(scr==0)
							iconLogo = new ImageIcon(new ImageIcon("img/0.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						else
							iconLogo = new ImageIcon(new ImageIcon("img/grey.png").getImage().getScaledInstance(50, 45, Image.SCALE_DEFAULT));
						if(scr>=0&&scr<=10)
							ballLabel[k][29].setIcon(iconLogo);
					}
					for (int i = 0; i < 21; i++) {
						if (((int[]) (((Lane)eventObject).scores)
								.get(bowlers.get(k)))[i]
								!= -1)
							if (((int[]) (((Lane)eventObject).scores)
									.get(bowlers.get(k)))[i]
									== 10
									&& (i % 2 == 0 || i == 19))
								ballLabel[k][i].setText("X");
							else if (
									i > 0
											&& ((int[]) (((Lane)eventObject).scores)
											.get(bowlers.get(k)))[i]
											+ ((int[]) (((Lane)eventObject).scores)
											.get(bowlers.get(k)))[i
											- 1]
											== 10
											&& i % 2 == 1)
								ballLabel[k][i].setText("/");
							else if (((int[]) (((Lane)eventObject).scores).get(bowlers.get(k)))[i] == -2) {

								ballLabel[k][i].setText("F");
							} else
								ballLabel[k][i].setText(
										(Integer.toString(((int[]) (((Lane)eventObject).scores)
												.get(bowlers.get(k)))[i])));

					}
				}
			}
		}
		else{
			handleExtendedDisplay();
		}
	}

	private void handleExtendedDisplay(){
		computeLeader();
		Random randomGenerator = new Random();
		int firstHighScore = lane.cumulScores[first_scorer][9];
		int secondHighScore = lane.cumulScores[second_scorer][9];
		int pinsDown;
		pinsDown = randomGenerator.nextInt(8) + 1;
		secondHighScore += pinsDown;
		scoreLabel[first_scorer][10].setText(Integer.toString(firstHighScore));
		ballLabel[second_scorer][21].setText(Integer.toString(pinsDown));
		scoreLabel[second_scorer][10].setText(Integer.toString(secondHighScore));
		if(secondHighScore > firstHighScore) {
			for (int i = 2; i < 8; i++) {
				pinsDown = randomGenerator.nextInt(3) + 1;
				ballLabel[first_scorer][21 + i].setText(Integer.toString(pinsDown));
				firstHighScore += pinsDown;
				scoreLabel[first_scorer][10 + (i / 2)].setText(Integer.toString(firstHighScore));
				pinsDown = randomGenerator.nextInt(3) + 1;
				secondHighScore += pinsDown;
				ballLabel[second_scorer][21 + i].setText(Integer.toString(pinsDown));
				scoreLabel[second_scorer][10 + (i / 2)].setText(Integer.toString(secondHighScore));
			}
		}
		String winner = firstHighScore > secondHighScore ? ((Bowler) bowlers.get(first_scorer)).getNick() :
				((Bowler) bowlers.get(second_scorer)).getNick();
		JOptionPane.showMessageDialog(frame,"Winner is " + winner);
	}

}