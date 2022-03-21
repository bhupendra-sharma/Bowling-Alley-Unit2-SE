import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class ScoreDao implements  Dao<Score>{

    private static final String SCOREHISTORY_FILE = "SCOREHISTORY.DAT";

    @Override
    public Score getByParam(String nickName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SCOREHISTORY_FILE));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                String[] scoreData = data.split("\t");
                if(nickName.equals(scoreData[0])) {
                    return new Score(scoreData[0], scoreData[1], scoreData[2]);
                }
            }
            return null;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Vector<Score> getAll() {
        Vector<Score> scoreRecords = new Vector<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SCOREHISTORY_FILE));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                String[] scoreData = data.split("\t");
                scoreRecords.add(new Score(scoreData[0], scoreData[1], scoreData[2]));
            }
            return scoreRecords;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Score score) {
        try {
            RandomAccessFile fileAccessor = new RandomAccessFile(SCOREHISTORY_FILE, "rw");
            fileAccessor.skipBytes((int) fileAccessor.length());
            fileAccessor.writeBytes(score.toString());
            fileAccessor.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
