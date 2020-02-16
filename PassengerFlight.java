import java.util.Date;

public class PassengerFlight {

	    /**
	     * Write the syntax out with the characters as described in the specification
	     * Passenger id:                           Format: 𝑋𝑋𝑋𝑛𝑛𝑛𝑛𝑋𝑋𝑛
	     * Flight id:                              Format: 𝑋𝑋𝑋𝑛𝑛𝑛𝑛𝑋
	     * From airport IATA/FAA code:             Format: 𝑋𝑋𝑋
	     * Destination airport IATA/FAA code:      Format: 𝑋𝑋𝑋
	     * Departure time (GMT):                   Format: 𝑛[10] (This is in Unix ‘epoch’ time)
	     * Total flight time (mins):               Format: 𝑛[1. .4]
	     **/
	    private Syntax[] passengerSyntax = {Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.NUMBER,
	                            Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.NUMBER};
	    private Syntax[] flightSyntax = {Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.NUMBER,
	            Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.CAPITAL_CASE};
	    private Syntax[] sourceAirportSyntax = {Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE};
	    private Syntax[] destinationAirportSyntax = {Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE,Syntax.CAPITAL_CASE};
	    private Syntax[] departureTimeSyntax = {Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,
	            Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER,Syntax.NUMBER};
	    private String passengerId;
	    private String flightId;
	    private String departAirport;
	    private String arriveAirport;
	    private Date departureTime;
	    private String departureTimeString;
	    private int flightTime;
	    private float flightTimeHours;
	    private Date arrivalTime;
	    protected Boolean error = false;
	    protected Boolean errorCorrection = false;
	    protected String errorMessage;
		


	    /**
	     * PassengerFlight Constructor
	     * @param all of the fields 
	     */
	    public PassengerFlight(String passengerID,String flightID, String dAirport, String aAirport, String dTime, int fTime){
	        this.passengerId = passengerID;
	        this.flightId = flightID;
	        this.departAirport = dAirport;
	        this.arriveAirport = aAirport;
	        this.departureTimeString = dTime;
	        this.departureTime = new Date(Long.valueOf(dTime)*1000);
	        this.flightTime = fTime;
	        validation();
	        this.flightTimeHours = (float) Math.floor(this.flightTime/60);
	        int minutes = (int) Math.floor(((this.flightTime - this.flightTimeHours)/60)/60);
	        long arrivalMili = this.departureTime.getTime() + ((this.flightTime * 60) *1000);
	        this.arrivalTime = new Date(arrivalMili);
	    }

	    /**
	     * Check if instance is valid
	     */
	    private void validation(){
	        char[] pId = this.passengerId.toCharArray();
	        char[] fId = this.flightId.toCharArray();
	        char[] dAirport = this.departAirport.toCharArray();
	        char[] aAirport = this.arriveAirport.toCharArray();
	        char[] dTime = this.departureTimeString.toCharArray();
	        String fTime = String.valueOf(this.flightTime);
	        String errorString = "";
	        // check validity of each variable according to their syntax.
	        // If true then invalid
	        Boolean passenger = checkInValid(passengerSyntax,pId);
	        Boolean flight = checkInValid(flightSyntax,fId);
	        Boolean source = checkInValid(sourceAirportSyntax,dAirport);
	        Boolean destination = checkInValid(destinationAirportSyntax,aAirport);
	        Boolean depatureTime = checkInValid(departureTimeSyntax,dTime);
	        Boolean flightTime = false;
	        if(fTime.length()<1 || fTime.length()>4){
	            flightTime = true;
	        }
	        if(passenger || flight || source || destination || depatureTime || flightTime){
	            String errorWith = "";
	            // if each varible is true add the column name to the error with to output what columns are errored
	            if(passenger){
	                int orginalLength = this.passengerId.length();
	                this.passengerId = this.passengerId.replaceAll("\\P{Print}","");
	                int replacementLength = this.passengerId.length();
	                // if length has changed it has found and unreadable character and replaced it
	                if(orginalLength != replacementLength && !checkInValid(passengerSyntax,this.passengerId.toCharArray())){
	                    this.errorCorrection = true;
	                    this.errorMessage = "Unreadable character detected and Corrected "+this.passengerId;
	                }
	                errorWith+=", PassengerID";
	            }
	            if(flight){
	                errorWith+=", FlightID";
	            }
	            if(source){
	                errorWith+=", Departure Airport";
	            }
	            if(destination){
	                errorWith+=", Arrival Airport";
	            }
	            if(depatureTime){
	                errorWith+=", Departure Time";
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

	    /**
	     * Check if the value is valid
	     *
	     * @param accepted  accepted values using Syntax enum
	     * @param values    value
	     * @return  if true then invalid
	     */
	    Boolean checkInValid(Syntax[] accepted ,char[] values){
	        for(int x=0;x<values.length;x++){
	            switch (accepted[x]){
	                case CAPITAL_CASE:
	                    if(!Character.isUpperCase(values[x])){
	                        return true;
	                    }
	                    break;
	                case LOWER_CASE:
	                    if(!Character.isLowerCase(values[x])){
	                        return true;
	                    }
	                    break;
	                case NUMBER:
	                    if(!Character.isDigit(values[x])){
	                        return true;
	                    }
	                    break;
	                case WHITESPACE:
	                    if(!Character.isWhitespace(values[x])){
	                        return true;
	                    }
	                    break;

	            }
	        }
	        return false;
	    }

	    /**
	     * Get Passenger ID
	     * @return  PassengerID
	     */
	    public String getPassengerId() {
	        return passengerId;
	    }

	    /**
	     * Get Source Airport
	     *
	     * @return  Source Airport
	     */
	    public String getDepartAirport() {
	        return departAirport;
	    }


	    /**
	     * Get Destination Airport
	     * @return  Destination Airport
	     */
	    public String getArriveAirport() {
	        return arriveAirport;
	    }


	    /**
	     * Get departure time
	     * @return departure time
	     */
	    public Date getDepartureTime() {
	        return departureTime;
	    }


	    /**
	     * Get Flight Time
	     * @return flight time
	     */
	    public int getFlightTime() {
	        return flightTime;
	    }


	    /**
	     * Get arrival date
	     * @return  arrival date
	     */
	    public Date getArrivalTime() {
			return arrivalTime;
	    }

}

