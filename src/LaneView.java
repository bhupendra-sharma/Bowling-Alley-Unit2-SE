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

	private JButton PerformThrow;


	private int scr;
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

		JPanel[][] balls = new JPanel[numBowlers][25];
		ballLabel = new JLabel[numBowlers][25];
		JPanel[][] scores = new JPanel[numBowlers][11];
		scoreLabel = new JLabel[numBowlers][11];
		JPanel[][] ballGrid = new JPanel[numBowlers][11];
		JPanel[] pins = new JPanel[numBowlers];

		for (int i = 0; i != numBowlers; i++) {
			for (int j = 0; j != 23; j++) {
				ballLabel[i][j] = new JLabel(" ");
				balls[i][j] = new JPanel();
				balls[i][j].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				balls[i][j].add(ballLabel[i][j]);
			}
			ballLabel[i][24] = new JLabel(" ",JLabel.CENTER);
			balls[i][24] = new JPanel();
			balls[i][24].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			balls[i][24].add(ballLabel[i][24]);
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
			j = 10;
			ballGrid[i][j] = new JPanel();
			ballGrid[i][j].setLayout(new GridLayout(0, 1));
			ballGrid[i][j].add(balls[i][24]);
		}

		for (int i = 0; i != numBowlers; i++) {
			pins[i] = new JPanel();
			pins[i].setBorder(
					BorderFactory.createTitledBorder(
							((Bowler) bowlers.get(i)).getNick()));
			pins[i].setLayout(new GridLayout(0, 11));
			for (int k = 0; k != 10; k++) {
				scores[i][k] = new JPanel();
				scoreLabel[i][k] = new JLabel("  ", SwingConstants.CENTER);
				scores[i][k].setBorder(
						BorderFactory.createLineBorder(Color.BLACK));
				scores[i][k].setLayout(new GridLayout(0, 1));
				scores[i][k].add(ballGrid[i][k], BorderLayout.EAST);
				scores[i][k].add(scoreLabel[i][k], BorderLayout.SOUTH);
				pins[i].add(scores[i][k], BorderLayout.EAST);
			}
			scores[i][10] = new JPanel();
			scoreLabel[i][10] = new JLabel("  ", SwingConstants.CENTER);
			scores[i][10].setBorder(
					BorderFactory.createLineBorder(Color.BLACK));
			scores[i][10].setLayout(new GridLayout(0, 1));
			scores[i][10].add(ballGrid[i][10], BorderLayout.CENTER);
			pins[i].add(scores[i][10], BorderLayout.EAST);
			panel.add(pins[i]);
		}

		initDone = true;
		return panel;
	}

	public void receiveEvent(Object eventObject) {
		if(eventObject instanceof Lane) {
			if (lane.isPartyAssigned()) {
				int numBowlers = ((Lane)eventObject).getParty().size();
				while (!initDone) {
					//System.out.println("chillin' here.");
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}

				if (((Lane)eventObject).getFrameNum() == 1
						&& ((Lane)eventObject).getBall() == 0
						&& ((Lane)eventObject).getIndex() == 0) {
					System.out.println("Making the frame.");
					cpanel.removeAll();
					cpanel.add(makeFrame(((Lane)eventObject).getParty()), "Center");

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
						lane.set_simulate_game(true);
						System.out.println("Simulate Pressed:" + lane.get_simulate_game());
					});
					simulatePanel.add(simulate);

					PerformThrow = new JButton("Perform Throw");
					JPanel PerformThrowPanel = new JPanel();
					PerformThrowPanel.setLayout(new FlowLayout());
					PerformThrow.addActionListener(actionEvent -> {
						lane.set_perform_throw(true);
						System.out.println("Perform Throw Pressed:"+lane.get_perform_throw());
					});
					PerformThrowPanel.add(PerformThrow);

					buttonPanel.add(maintenancePanel);
					buttonPanel.add(PerformThrowPanel);
					buttonPanel.add(simulatePanel);

					cpanel.add(buttonPanel, "South");

					frame.pack();

				}

				int[][] lescores = ((Lane)eventObject).getCumulScore();
				for (int k = 0; k < numBowlers; k++) {
					for (int i = 0; i <= ((Lane)eventObject).getFrameNum() - 1; i++) {
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
						if(scr >= 0 && scr <= 10)
							ballLabel[k][24].setIcon(iconLogo);
					}
					for (int i = 0; i < 21; i++) {
						if (((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i] != -1)
							if (((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i] == 10
									&& (i % 2 == 0 || i == 19))
								ballLabel[k][i].setText("X");
							else if (i > 0 && ((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i]
									+ ((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i - 1] == 10
									&& i % 2 == 1)
								ballLabel[k][i].setText("/");
							else if (((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i] == -2) {
								ballLabel[k][i].setText("F");
							} else
								ballLabel[k][i].setText((Integer.toString(
										((int[]) (((Lane)eventObject).getScore()).get(bowlers.get(k)))[i])));

					}
				}

			}
		}
	}


}
