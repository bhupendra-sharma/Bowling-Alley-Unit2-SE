import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Vector;

public class ComputeWinner {
    private JFrame win1;
    private JButton ThreeThrow;

    JFrame frame;
    Container cpanel;

    JPanel[][] balls;
    JLabel[][] ballLabel;
    JPanel[][] scores;
    JLabel[][] scoreLabel;
    JPanel[][] ballGrid;
    JPanel[] pins;



    public ComputeWinner(int first, int first_scorer, int second, int second_scorer, Vector<Bowler> bowlers){
        String first_nick = ((Bowler) bowlers.get(first_scorer)).getNick();
        String second_nick = ((Bowler) bowlers.get(second_scorer)).getNick();

        Random rd = new Random();
        int low = 0;
        int high = 20;
        int result = rd.nextInt(high-low) + low;
//        System.out.printf("Second ThrowScore:%d First:%d Second:%d\n",result,first,second);
        System.out.printf("Second Throw Score:%d \n",result);
        if(100+second>=first){
            String msg="In Runner Up Throw "+second_nick+" has scored:"+result+".\nSince Runner Up has crossed Winner ,3 more Frame Play will be executed";
            int[][] ar=new int[2][6];
            for(int r=0;r<2;r++){
                System.out.printf("%s:",r==0?first_nick:second_nick);
                for(int c=0;c<3;c++){

                    ar[r][2*c] = rd.nextInt(5);
                    if(ar[r][2*c]==5)
                        ar[r][2*c+1]=rd.nextInt(4);
                    else
                        ar[r][2*c+1]=rd.nextInt(5);
                    System.out.printf("%d %d |",ar[r][2*c],ar[r][2*c+1]);
                    if(r==0) first+=ar[r][c*2]+ar[r][c*2+1];
                    else second+=ar[r][c*2]+ar[r][c*2+1];
                }
                System.out.println();
            }
            System.out.printf("%s's Score:%d  %s's Score:%d\n",first_nick,first,second_nick,second);
            System.out.printf("Winner %s\n",first>=second?first_nick:second_nick);
            win1 = new JFrame("Extended Play Contd.");
            win1.getContentPane().setLayout(new BorderLayout());
            ((JPanel) win1.getContentPane()).setOpaque(false);

            JPanel colPanel = new JPanel();
            colPanel.setLayout(new GridLayout(2, 1));

            // Label Panel
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new FlowLayout());



            JLabel message = new JLabel(msg);

            labelPanel.add(message);

            // Button Panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(1, 2));

            Insets buttonMargin = new Insets(4, 4, 4, 4);

            ThreeThrow = new JButton("Perform RunnerUp Throw");
            JPanel temp = new JPanel();
            temp.setLayout(new FlowLayout());
            temp.add(ThreeThrow);
            buttonPanel.add(temp);
            ThreeThrow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    win1.hide();

                    //
                    frame = new JFrame("Extended Play(3 Throws)");
                    cpanel = frame.getContentPane();
                    cpanel.setLayout(new BorderLayout());

                    frame.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            frame.setVisible(false);
                        }
                    });

                    cpanel.add(new JPanel());
                    //

                    JPanel panel = new JPanel();

                    panel.setLayout(new GridLayout(0, 1));
                    int numBowlers=2;
                    balls = new JPanel[numBowlers][6];
                    ballLabel = new JLabel[numBowlers][6];
                    scores = new JPanel[numBowlers][3];
                    scoreLabel = new JLabel[numBowlers][3];
                    ballGrid = new JPanel[numBowlers][3];
                    pins = new JPanel[numBowlers];

                    for (int i = 0; i != numBowlers; i++) {
                        for (int j = 0; j != 6; j++) {
                            ballLabel[i][j] = new JLabel(" ");
                            balls[i][j] = new JPanel();
                            balls[i][j].setBorder(
                                    BorderFactory.createLineBorder(Color.BLACK));
                            balls[i][j].add(ballLabel[i][j]);
                        }
                    }

                    for (int i = 0; i != numBowlers; i++) {
                        for (int j = 0; j != 3; j++) {
                            ballGrid[i][j] = new JPanel();
                            ballGrid[i][j].setLayout(new GridLayout(0, 3));
                            ballGrid[i][j].add(new JLabel("  "), BorderLayout.EAST);
                            ballGrid[i][j].add(balls[i][2 * j], BorderLayout.EAST);
                            ballGrid[i][j].add(balls[i][2 * j + 1], BorderLayout.EAST);
                        }
                    }

                    for (int i = 0; i != numBowlers; i++) {
                        pins[i] = new JPanel();
                        pins[i].setBorder(
                                BorderFactory.createTitledBorder(
                                        ((Bowler) bowlers.get(i)).getNick()));
                        pins[i].setLayout(new GridLayout(0, 3));
                        for (int k = 0; k != 3; k++) {
                            scores[i][k] = new JPanel();
                            scoreLabel[i][k] = new JLabel("  ", SwingConstants.CENTER);
                            scores[i][k].setBorder(
                                    BorderFactory.createLineBorder(Color.BLACK));
                            scores[i][k].setLayout(new GridLayout(0, 1));
                            scores[i][k].add(ballGrid[i][k], BorderLayout.EAST);
                            scores[i][k].add(scoreLabel[i][k], BorderLayout.SOUTH);
                            pins[i].add(scores[i][k], BorderLayout.EAST);
                        }
                        panel.add(pins[i]);
                    }
                }
            });

            // Clean up main panel
            colPanel.add(labelPanel);
            colPanel.add(buttonPanel);

            win1.getContentPane().add("Center", colPanel);

            win1.pack();

            // Center Window on Screen
            Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
            win1.setLocation(
                    ((screenSize.width) / 2) - ((win1.getSize().width) / 2),
                    ((screenSize.height) / 2) - ((win1.getSize().height) / 2));
            win1.setVisible(true);
        }
        else{
            String msg="In Runner Up Throw "+second_nick+" has scored:"+result+".\nSince Runner Up has not been able to cross Winner,Play will be Stopped.\nWinner "+first_nick;
            win1 = new JFrame("No Extended Play");
            win1.getContentPane().setLayout(new BorderLayout());
            ((JPanel) win1.getContentPane()).setOpaque(false);

            JPanel colPanel = new JPanel();
            colPanel.setLayout(new GridLayout(2, 1));

            // Label Panel
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new FlowLayout());



            JLabel message = new JLabel(msg);

            labelPanel.add(message);

            // Button Panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(1, 2));

            Insets buttonMargin = new Insets(4, 4, 4, 4);

            ThreeThrow = new JButton("Close");
            JPanel temp = new JPanel();
            temp.setLayout(new FlowLayout());
            temp.add(ThreeThrow);
            buttonPanel.add(temp);
            ThreeThrow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int numBowlers=bowlers.size();
                    for(int i=0;i<numBowlers;i++){
                        win1.hide();
                    }
                }
            });

            // Clean up main panel
            colPanel.add(labelPanel);
            colPanel.add(buttonPanel);

            win1.getContentPane().add("Center", colPanel);

            win1.pack();

            // Center Window on Screen
            Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
            win1.setLocation(
                    ((screenSize.width) / 2) - ((win1.getSize().width) / 2),
                    ((screenSize.height) / 2) - ((win1.getSize().height) / 2));
            win1.setVisible(true);
        }

    }
}
