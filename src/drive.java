import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class drive {

	public static void main(String[] args) {
		JFrame window = new JFrame("Configure Window");
		window.getContentPane().setLayout(new BorderLayout());
		((JPanel) window.getContentPane()).setOpaque(false);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height * 2 / 3;
		int width = screenSize.width * 2 / 3;

		window.setPreferredSize(new Dimension((width/3),(height/3)));

		JPanel colPanel = new JPanel();
		colPanel.setLayout(new BorderLayout());

		JPanel settingPanel = new JPanel();
		settingPanel.setLayout(new GridLayout(3, 1));
		settingPanel.setBorder(new TitledBorder("Controls"));


		String[] maxNumberOfPlayers = {"Maximum Number Of Players", "1", "2", "3", "4", "5", "6"};
		JPanel maxNumberOfPlayersPanel = new JPanel();
		JComboBox<String> numberOfPlayersList = new JComboBox<>(maxNumberOfPlayers);
		maxNumberOfPlayersPanel.add(numberOfPlayersList);

		String[] lanes = {"Number Of Lanes","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		JPanel numberOfLanesPanel = new JPanel();
		JComboBox<String> numberOfLanesList = new JComboBox<>(lanes);
		numberOfLanesPanel.add(numberOfLanesList);

		JButton startButton = new JButton("Start");
		JPanel startButtonPanel = new JPanel();
		startButtonPanel.setLayout(new FlowLayout());
		startButton.addActionListener(e -> {
			int maxPlayers = numberOfPlayersList.getSelectedIndex();
			int numOfLanes = numberOfLanesList.getSelectedIndex();
			if(maxPlayers != 0 && numOfLanes != 0){
				startButton.setText("Initializing, please wait..");
				startButton.setEnabled(false);
				ControlDesk controlDesk = new ControlDesk(numOfLanes);
				ControlDeskView cdv = new ControlDeskView(controlDesk, maxPlayers);
				controlDesk.subscribe(cdv);
				window.hide();
			}
		});

		startButtonPanel.add(startButton);

		settingPanel.add(maxNumberOfPlayersPanel);
		settingPanel.add(numberOfLanesPanel);
		settingPanel.add(startButtonPanel);

		colPanel.add(settingPanel, "Center");

		window.getContentPane().add("Center", colPanel);

		window.pack();

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		window.setLocation(
				((screenSize.width) / 2) - ((window.getSize().width) / 2),
				((screenSize.height) / 2) - ((window.getSize().height) / 2));
		window.show();



//		ControlDesk controlDesk = new ControlDesk( numLanes );0
//		ControlDeskView cdv = new ControlDeskView( controlDesk, maxPatronsPerParty);
//		controlDesk.subscribe( cdv );

	}
}
