import java.util.Vector;

public class drive {

	public static void main(String[] args) {

		final int numLanes = 3;
		final int maxPatronsPerParty = 5;

		ControlDesk controlDesk = new ControlDesk( numLanes );
		ControlDeskView cdv = new ControlDeskView( controlDesk, maxPatronsPerParty);
		controlDesk.subscribe( cdv );

	}
}
