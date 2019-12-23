
import java.text.*;
import java.util.*;

public class Flight {
	String passengerID;
	String flightID;
	String depart;
	String arrive;
	String unixDeparture;
	String unixArrival;
	String departureTime;
	String arrivalTime;
	int flightTime;
	Boolean error = false;
	Boolean errorCorrection = false;
	String errorMessage;
	
	public Flight(String _passID, String _flightID, String _depart, String _arrive, String _unixDepart, String _flightTime) {
		this.passengerID=_passID;
		this.flightID=_flightID;
		this.depart=_depart;
		this.arrive=_arrive;
		this.unixDeparture=_unixDepart;
		this.departureTime=unixToHHMMSS(unixDeparture);
		this.flightTime=Integer.parseInt(_flightTime);
		this.unixArrival=this.departureTime + ((this.flightTime * 60) *1000);
		this.arrivalTime=unixToHHMMSS(unixArrival);
	}
	public Flight(String _passID, String _flightID, String _depart, String _arrive, String _unixDepart, int _flightTime) {
		this.passengerID=_passID;
		this.flightID=_flightID;
		this.depart=_depart;
		this.arrive=_arrive;
		this.unixDeparture=_unixDepart;
		this.departureTime=unixToHHMMSS(unixDeparture);
		this.flightTime=_flightTime;
	}
	
	private String unixToHHMMSS(String unixTime) {
		long unixSeconds=Long.parseLong(unixTime);
		Date date=new Date(unixSeconds*1000L);
		SimpleDateFormat dt=new SimpleDateFormat("HH:mm:ss");
		String formatDate=dt.format(date);
		return formatDate;
	}
	private void CheckValid() {
		char[] pId = this.passengerID.toCharArray();
        char[] fId = this.flightID.toCharArray();
        char[] sAirport = this.depart.toCharArray();
        char[] dAirport = this.arrive.toCharArray();
        char[] dTime = this.departureTime.toCharArray();
        String fTime = String.valueOf(this.flightTime);
        String errorString = "";
        
        Boolean passenger = validation.passenger(this.passengerID);
        Boolean flight = validation.flight(this.flightID);
        Boolean depart = validation.faa(this.depart);
        Boolean arrive = validation.faa(this.arrive);
        Boolean departureTime = validation.time(this.unixDeparture);
        Boolean flightTime = false;
        if(fTime.length()<1 || fTime.length()>4){
            flightTime = true;
        }
        if(passenger || flight || depart || arrive || departureTime || flightTime){
            String errorWith = "";
            // if each variable is true add the column name to the error with to output which columns that are errored
            if(passenger){
                int orginalLength = this.passengerID.length();
                this.passengerID = this.passengerID.replaceAll("\\P{Print}","");
                int replacementLength = this.passengerID.length();
                // if length has changed it has found and unreadable character and replaced it
                if(orginalLength != replacementLength){
                    this.errorCorrection = true;
                    this.errorMessage = "Unreadable character detected and Corrected "+this.passengerID;
                }
                errorWith+=", PassengerID";
            }
            if(flight){
                errorWith+=", FlightID";
            }
            if(depart){
                errorWith+=", Source Airport";
            }
            if(arrive){
                errorWith+=", destination Airport";
            }
            if(departureTime){
                errorWith+=", departure Time";
            }
            if(flightTime){
                errorWith+=", Flight Time";
            }
            if(!this.errorCorrection){
                System.err.println("Error: Syntax Error with Passenger Flight;"+errorWith);
                this.error = true;
                this.errorMessage = "Error: Syntax Error with Passenger Flight;"+errorWith;
            }

        }
	}
	public String getDepart() {
		return depart;
	}
	public String getArrive() {
		return arrive;
	}
	public String getPassengerID() {
		return passengerID;
	}
	public String getFlightID() {
		return flightID;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public String toString() {
		return passengerID+" left "+depart+" to "+arrive+" at "+departureTime+" taking "+flightTime+" min.";
	}
}
