/* AddPartyView.java
 *
 *  Version:
 * 		 $Id$
 *
 *  Revisions:
 * 		$Log: AddPartyView.java,v $
 * 		Revision 1.7  2003/02/20 02:05:53  ???
 * 		Fixed addPatron so that duplicates won't be created.
 *
 * 		Revision 1.6  2003/02/09 20:52:46  ???
 * 		Added comments.
 *
 * 		Revision 1.5  2003/02/02 17:42:09  ???
 * 		Made updates to migrate to observer model.
 *
 * 		Revision 1.4  2003/02/02 16:29:52  ???
 * 		Added ControlDeskEvent and ControlDeskObserver. Updated Queue to allow access to Vector so that contents could be viewed without destroying. Implemented observer model for most of ControlDesk.
 *
 *
 */

/**
 * Class for GUI components need to add a party
 *
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.util.List;

/**
 * Constructor for GUI used to Add Parties to the waiting party queue.
 *
 */

public class QueryView implements  ListSelectionListener {

    private final JFrame win;
    private final JList partyList;
    private final JList allBowlers;
    private Vector party;
    private Vector bowlerdb =  new Vector();

    private String selectedMember = null;


    public QueryView() {

        win = new JFrame("Query");
        win.getContentPane().setLayout(new BorderLayout());
        ((JPanel) win.getContentPane()).setOpaque(false);

        JPanel colPanel = new JPanel();
        colPanel.setLayout(new GridLayout(1, 3));

        // Party Panel
        JPanel partyPanel = new JPanel();
        partyPanel.setLayout(new FlowLayout());
        partyPanel.setBorder(new TitledBorder("Query Data"));

        party = new Vector();
        Vector empty = new Vector();
        empty.add("(Empty)");

        partyList = new JList(empty);
        partyList.setFixedCellWidth(120);
        partyList.setVisibleRowCount(5);
        JScrollPane partyPane = new JScrollPane(partyList);
        //        partyPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        partyPanel.add(partyPane);

        // Bowler Database
        JPanel bowlerPanel = new JPanel();
        bowlerPanel.setLayout(new FlowLayout());
        bowlerPanel.setBorder(new TitledBorder("Bowlers"));

        try {
            Dao<Bowler> bowlerDao = new BowlerDao();
            bowlerDao.getAll().forEach(bowler -> bowlerdb.add(bowler.getNick()));
        } catch (Exception e) {
            System.err.println("File Error");
            bowlerdb = new Vector();
        }
        allBowlers = new JList(bowlerdb);
        allBowlers.setVisibleRowCount(8);
        allBowlers.setFixedCellWidth(120);
        JScrollPane bowlerPane = new JScrollPane(allBowlers);
        bowlerPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        allBowlers.addListSelectionListener(this);
        bowlerPanel.add(bowlerPane);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));

        JButton topPlayer = new JButton("Highest Overall Score");
        JPanel topPlayerPanel = new JPanel();
        topPlayerPanel.setLayout(new FlowLayout());
        topPlayer.addActionListener(actionEvent -> {
            Dao<Score> scoreDao = new ScoreDao();
            Vector<Score> scoreRecords = scoreDao.getAll();
            Score maxScoreRecord = Collections.max(scoreRecords
                    , Comparator.comparingInt(o -> Integer.parseInt(o.getScore())));
            party = new Vector();
            party.add(maxScoreRecord.getNick() + ":" + maxScoreRecord.getScore());
            partyList.setListData(party);
        });
        topPlayerPanel.add(topPlayer);

        JButton topThreeScores = new JButton("Top Three Scores");
        JPanel topThreeScoresPanel = new JPanel();
        topThreeScoresPanel.setLayout(new FlowLayout());
        topThreeScores.addActionListener(actionEvent -> {
            Dao<Score> scoreDao = new ScoreDao();
            Vector<Score> scoreRecords = scoreDao.getAll();
            scoreRecords.sort(Comparator.comparingInt(o -> -1 * Integer.parseInt(o.getScore())));
            party = new Vector();
            for(int i = 0 ; i < 3; i++){
                if(scoreRecords.get(i) != null){
                    party.add(scoreRecords.get(i).getNick() + ":" + scoreRecords.get(i).getScore() + "\n");
                }
            }
            partyList.setListData(party);
        });
        topThreeScoresPanel.add(topThreeScores);

        JButton lowestOverallScore = new JButton("Lowest Overall Score");
        JPanel lowestOverallScorePanel = new JPanel();
        lowestOverallScorePanel.setLayout(new FlowLayout());
        lowestOverallScore.addActionListener(actionEvent -> {
            Dao<Score> scoreDao = new ScoreDao();
            Vector<Score> scoreRecords = scoreDao.getAll();
            Score maxScoreRecord = Collections.min(scoreRecords
                    , Comparator.comparingInt(o -> Integer.parseInt(o.getScore())));
            party = new Vector();
            party.add(maxScoreRecord.getNick() + ":" + maxScoreRecord.getScore());
            partyList.setListData(party);
        });
        lowestOverallScorePanel.add(lowestOverallScore);

        JButton lowestThreeScores = new JButton("Lowest Three Scores");
        JPanel lowestThreeScoresPanel = new JPanel();
        lowestThreeScoresPanel.setLayout(new FlowLayout());
        lowestThreeScores.addActionListener(actionEvent -> {
            Dao<Score> scoreDao = new ScoreDao();
            Vector<Score> scoreRecords = scoreDao.getAll();
            scoreRecords.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getScore())));
            party = new Vector();
            for(int i = 0 ; i < 3; i++){
                if(scoreRecords.get(i) != null){
                    party.add(scoreRecords.get(i).getNick() + ":" + scoreRecords.get(i).getScore() + "\n");
                }
            }
            partyList.setListData(party);
        });
        lowestThreeScoresPanel.add(lowestThreeScores);

        JButton selectedPlayersAllScores = new JButton("Scores of selected player");
        JPanel selectedPlayersAllScoresPanel = new JPanel();
        selectedPlayersAllScoresPanel.setLayout(new FlowLayout());
        selectedPlayersAllScores.addActionListener(actionEvent -> {
            if(selectedMember != null) {
                Dao<Score> scoreDao = new ScoreDao();
                Vector<Score> scoreRecords = scoreDao.getAll();
                List<Score> filteredRecords =
                        scoreRecords.stream().filter(s -> selectedMember.equals(s.getNick())).toList();
                party = new Vector();
                if(filteredRecords.size() > 0){
                    for (Score record : filteredRecords) {
                        party.add(record.getNick() + ":" + record.getScore() + "\n");
                    }
                }
                partyList.setListData(party);
            }
        });
        selectedPlayersAllScoresPanel.add(selectedPlayersAllScores);

        JButton selectedPlayerHighestScore = new JButton("Highest Score Of Selected Player");
        JPanel selectedPlayerHighestScorePanel= new JPanel();
        selectedPlayerHighestScorePanel.setLayout(new FlowLayout());
        selectedPlayerHighestScore.addActionListener(actionEvent -> {
            if(selectedMember != null) {
                Dao<Score> scoreDao = new ScoreDao();
                Vector<Score> scoreRecords = scoreDao.getAll();
                List<Score> filteredRecords =
                        scoreRecords.stream().filter(s -> selectedMember.equals(s.getNick())).toList();
                party = new Vector();
                if(filteredRecords.size() > 0) {
                    Score maxScoreRecord = Collections.max(filteredRecords
                            , Comparator.comparingInt(o -> Integer.parseInt(o.getScore())));
                    party.add(maxScoreRecord.getNick() + ":" + maxScoreRecord.getScore());
                }
                partyList.setListData(party);
            }
        });
        selectedPlayerHighestScorePanel.add(selectedPlayerHighestScore);


        buttonPanel.add(topPlayerPanel);
        buttonPanel.add(topThreeScoresPanel);
        buttonPanel.add(lowestOverallScorePanel);
        buttonPanel.add(lowestThreeScoresPanel);
        buttonPanel.add(selectedPlayersAllScoresPanel);
        buttonPanel.add(selectedPlayerHighestScorePanel);

        // Clean up main panel
        colPanel.add(partyPanel);
        colPanel.add(bowlerPanel);
        colPanel.add(buttonPanel);

        win.getContentPane().add("Center", colPanel);

        win.pack();

        // Center Window on Screen
        Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
        win.setLocation(
                ((screenSize.width) / 2) - ((win.getSize().width) / 2),
                ((screenSize.height) / 2) - ((win.getSize().height) / 2));
        win.show();

    }


    /**
     * Handler for List actions
     * @param e the ListActionEvent that triggered the handler
     */

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource().equals(allBowlers)) {
            selectedMember =
                    ((String) ((JList) e.getSource()).getSelectedValue());
        }
    }

}
