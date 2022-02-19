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

    private int getStrikeBalls(int[] curScore,int i){
        int strikeballs=0;
        if (curScore[i+2] != -1) {
            strikeballs = 1;
            if(curScore[i+3] != -1) {
                //Still got em.
                strikeballs = 2;
            } else if(curScore[i+4] != -1) {
                //Ok, got it.
                strikeballs = 2;
            }
        }
        return strikeballs;
    }

    private void performNormalThrow(int[] curScore,int i)
    {
        if( i%2 == 0 && i < 18){
            if ( i/2 == 0 ) {
                //First frame, first ball.  Set his cumul score to the first ball
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += curScore[i];
                }
            } else if (i/2 != 9){
                //add his last frame's cumul to this ball, make it this frame's cumul.
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1] + curScore[i];
                } else {
                    cumulScores[bowlIndex][i/2] += cumulScores[bowlIndex][i/2 - 1];
                }
            }
        } else if (i < 18){
            if(curScore[i] != -1 && i > 2){
                if(curScore[i] != -2){
                    cumulScores[bowlIndex][i/2] += curScore[i];
                }
            }
        }
        if (i/2 == 9){
            if (i == 18){
                cumulScores[bowlIndex][9] += cumulScores[bowlIndex][8];
            }
            if(curScore[i] != -2){
                cumulScores[bowlIndex][9] += curScore[i];
            }
        } else if (i/2 == 10) {
            if(curScore[i] != -2){
                cumulScores[bowlIndex][9] += curScore[i];
            }
        }
    }
    public int getScore(Bowler Cur, int frame) {
        int[] curScore;
        int strikeballs = 0;
        int totalScore = 0;
        curScore = (int[]) scores.get(Cur);
        for (int i = 0; i != 10; i++){
            cumulScores[bowlIndex][i] = 0;
        }
        int current = 2*(frame - 1)+ball-1;
        //Iterate through each ball until the current one.
        for (int i = 0; i != current+2; i++){
            //Spare:
            if( i%2 == 1 && curScore[i - 1] + curScore[i] == 10 && i < current - 1 && i < 19){
                //This ball was a the second of a spare.
                //Also, we're not on the current ball.
                //Add the next ball to the ith one in cumul.
                cumulScores[bowlIndex][(i/2)] += curScore[i+1] + curScore[i];

            } else if( i < current && i%2 == 0 && curScore[i] == 10  && i < 18){
                strikeballs = getStrikeBalls(curScore,i);
                //This ball is the first ball, and was a strike.
                //If we can get 2 balls after it, good add them to cumul.

                if (strikeballs == 2){
                    //Add up the strike.
                    //Add the next two balls to the current cumulscore.
                    cumulScores[bowlIndex][i/2] += 10;

                    if ( i/2 > 0 ){
                        cumulScores[bowlIndex][i/2] += curScore[i+2] + cumulScores[bowlIndex][(i/2)-1];
                    } else {
                        cumulScores[bowlIndex][i/2] += curScore[i+2];
                    }
                    if (curScore[i+3] != -1){
                        if( curScore[i+3] != -2){
                            cumulScores[bowlIndex][(i/2)] += curScore[i+3];
                        }
                    } else {
                        cumulScores[bowlIndex][(i/2)] += curScore[i+4];
                    }

                } else {
                    break;
                }
            }else {
                //We're dealing with a normal throw, add it and be on our way.
                performNormalThrow(curScore,i);
            }
        }
        return totalScore;
    }
}
