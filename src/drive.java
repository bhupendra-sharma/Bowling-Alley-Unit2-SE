
public class drive {

	public static void main(String[] args) {

		ControlDesk controlDesk = new ControlDesk( AppConstants.numLanes );
		ControlDeskView cdv = new ControlDeskView( controlDesk, AppConstants.maxPatronsPerParty);
		controlDesk.subscribe( cdv );

	}
}
