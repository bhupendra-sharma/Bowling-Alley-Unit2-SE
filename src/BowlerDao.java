import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class BowlerDao implements Dao<Bowler>{

    private static final String BOWLER_FILE = "BOWLERS.DAT";

    @Override
    public Bowler getByParam(String nickName)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(BOWLER_FILE));
            String data;
            while ((data = br.readLine()) != null){
                String[] bowlerData = data.split("\t");
                if(nickName.equals(bowlerData[0])){
                    return new Bowler(bowlerData[0], bowlerData[1], bowlerData[2]);
                }
            }
            return null;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public Vector<Bowler> getAll() {
        Vector<Bowler> bowlerRecords = new Vector<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(BOWLER_FILE));
            String data;
            while((data = br.readLine()) != null){
                String[] bowlerData = data.split("\t");
                bowlerRecords.add(new Bowler(bowlerData[0], bowlerData[1], bowlerData[2]));
            }
            return bowlerRecords;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Bowler bowler) {
        try {
            RandomAccessFile fileAccessor = new RandomAccessFile(BOWLER_FILE, "rw");
            fileAccessor.skipBytes((int) fileAccessor.length());
            fileAccessor.writeBytes(bowler.toString());
            fileAccessor.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
