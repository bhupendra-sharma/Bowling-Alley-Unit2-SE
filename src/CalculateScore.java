import java.util.HashMap;

public class CalculateScore {

    private final int ball;
    private final HashMap scores;
    private final int[][] cumulScores;
    private final int bowlIndex;

    public CalculateScore(int ball, HashMap scores, int[][] cumulScores, int bowlIndex) {
        this.ball = ball;
        this.scores = scores;
        this.cumulScores = cumulScores;
        this.bowlIndex = bowlIndex;
    }


}
