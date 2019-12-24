import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ThreadControlller implements Runnable{
	public Thread thread;
    private String threadName;
    private ArrayList<String> mapperLines;
    private int mode;
    private int mapperOffset;
    private boolean mapper;
    private ArrayList<Object> reducerValues;
    private String reducerKey;
    public String error = "";
    public ArrayList<tuple> mapperOutput;
    public Printout reducerOutput;

    /**
     * ThreadClass Constructor for mapper
     *
     * @param tName thread name
     * @param mLines    mapperlines
     * @param m mode (1,2,3)
     * @param mOffset   mapper offset - where the mapper starts from in the csv
     */
    public ThreadControlller(String tName,ArrayList<String> mLines,int m,int mOffset){
        this.threadName = tName;
        this.mapperLines = mLines;
        this.mode = m;
        this.mapper = true;
        this.mapperOffset = mOffset;
    }
    /**
     * ThreadClass Constructor for mapper
     *
     * @param tName thread name
     * @param mLines    mapperlines
     * @param m mode (1,2,3)
     * @param mOffset   mapper offset - where the mapper starts from in the csv
     */
    public ThreadControlller(String tName,ArrayList<Object> rValues,String rKey,int m){
        this.threadName = tName;
        this.reducerValues = rValues;
        this.reducerKey = rKey;
        this.mode = m;
    }
    public void run(){
        // For the relevant type run the relevant mode
        if(this.mapper){
            try {
                switch (this.mode) {
                    case 1:
                        this.mapperOutput = this.mapper1(this.mapperLines);
                        break;
                    case 2:
                        this.mapperOutput = this.mapper2(this.mapperLines);
                        break;
                    case 3:
                        this.mapperOutput = this.mapper2(this.mapperLines);
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else{
            switch (this.mode) {
                case 1:
                    this.reducerOutput = this.reducer1(this.reducerKey,this.reducerValues);
                    break;
                case 2:
                    this.reducerOutput = this.reducer2(this.reducerKey,this.reducerValues);
                    break;
                case 3:
                    this.reducerOutput = this.reducer3(this.reducerKey,this.reducerValues);
                    break;
            }
        }
    }
    public void start () {
        if (thread == null) {
            thread = new Thread (this, threadName);
            thread.start ();
        }
    }
    Flight checkForError(String[] input,int x){
        String passengerId = input[0];
        String flightId = input[1];
        String departureAirport = input[2];
        String arrivalAirport = input[3];
        String departureTime = input[4];
        int flightTime = Integer.parseInt(input[5]);
        // if the rows are empty print error
        if(passengerId.isEmpty() || departureAirport.isEmpty() || arrivalAirport.isEmpty() || flightTime==0) {
            this.error += "Error at "+(x+mapperOffset+1)+": Missing Values\r\n";
            System.err.println("Error at "+(x+mapperOffset+1)+": Missing Values");
            // if the starting airport is not in the airport has map print error
        } else if(!SupportClass.airportHashMap.containsKey(departureAirport)){
            this.error += "Error at "+(x+mapperOffset+1)+": Starting airport does not exist in airport list ("+departureAirport+")\r\n";
            System.err.println("Error at "+(x+mapperOffset+1)+": Starting airport does not exist in airport list ("+departureAirport+")");
            // if the destination airport is not in the airport hash mpa print error
        } else if(!SupportClass.airportHashMap.containsKey(arrivalAirport)){
            this.error += "Error at "+(x+mapperOffset+1)+": Destination airport does not exist in airport list ("+arrivalAirport+")\r\n";
            System.err.println("Error at "+(x+mapperOffset+1)+": Destination airport does not exist in airport list ("+arrivalAirport+")");
        } else{
            // If when constructing passenger error is created then print error or error correction else return passenger
            Flight passengerFlight = new Flight(passengerId,flightId,departureAirport,arrivalAirport,departureTime,flightTime);
            if(passengerFlight.error){
                this.error += "Error at "+(x+mapperOffset+1)+": "+passengerFlight.errorMessage+"\r\n";
            }
            return passengerFlight;
        }
           

        return null;
    }
    public ArrayList<tuple> mapper1(ArrayList<String> mapperLines){
        ArrayList<tuple> mapValue = new ArrayList<tuple>();
        for(int x=0;x<mapperLines.size();x++){
            String[] row = mapperLines.get(x).split(",");
            Flight passengerFlight = checkForError(row,x);
                if (passengerFlight != null && !passengerFlight.error){
                    tuple keyValue = new tuple(row[2],row[1]);
                    mapValue.add(keyValue);
                }
            }

        return mapValue;
    }
    public ArrayList<tuple> mapper2(ArrayList<String> mapperLines) throws ParseException {
        //this.getAirportHashMap();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        ArrayList<tuple> mapValue = new ArrayList<tuple>();
        for (int x=0;x<mapperLines.size();x++){
            String[] row = mapperLines.get(x).split(",");
            Flight passengerFlight = checkForError(row,x);
            if (passengerFlight != null && !passengerFlight.error){
                tuple keyValue = new tuple(row[1],passengerFlight);
                mapValue.add(keyValue);
            }
        }

        return mapValue;
    }
    public Printout reducer1(String key, ArrayList<Object> values){
        ArrayList<String> flights = new ArrayList<String>();
        for(int x=0;x<values.size();x++){
            String flightID = String.valueOf(values.get(x));
            if(!flights.contains(flightID)){
                flights.add(flightID);
            }
        }
        String airportName = SupportClass.airportHashMap.get(key).getName();
        SupportClass.Airports.add(key);
        String reducerString = "Airport:              "+airportName+"\r\n";
        reducerString += "Airport Code:         "+ key+"\r\n";
        reducerString += "Flights From Airport: "+ flights.size()+"\r\n";
        String[] options = {airportName,key,String.valueOf(flights.size())};
        String rCSV = SupportClass.makeCSVRow(options);
        return new Printout(reducerString,rCSV);
    }
    public Printout reducer2(String key, ArrayList<Object> values){
       Flight flight = (Flight) values.get(0);

        String arrivalTime = flight.getArrivalTime();
        String reducerString = "";
        reducerString += "Flight ID:            "+key+"\r\n";
        reducerString += "Flight Depature Time: "+flight.getDepartureTime()+"\r\n";
        reducerString += "Flight time:          "+flight.getFlightTime()+"minutes\r\n";
        reducerString += "Arrival Time:         "+flight.getArrivalTime()+"\r\n";
        reducerString += "Source Airport:       "+flight.getDepart()+"\r\n";
        reducerString += "Destination Airport:  "+flight.getArrive()+"\r\n";
        reducerString += "Passengers:           "+"\r\n";
        String passengerString = "";
        for(int x=0;x<values.size();x++){
            Flight passenger = (Flight) values.get(x);
            // Check if the passenger is in the hashmap if not add it to the list if it is then discard duplicates
            if(!SupportClass.passengerDetails.containsKey(passenger.getPassengerID())){
                SupportClass.passengerDetails.put(passenger.getPassengerID(),passenger.getPassengerID());
                reducerString += "                    "+passenger.getPassengerID()+"\r\n";
                passengerString +=passenger.getPassengerID()+";";
            }
        }

        String[] options = {key,String.valueOf(flight.getDepartureTime()),String.valueOf(flight.getFlightTime()),arrivalTime,
                flight.getDepart(),flight.getArrive(),passengerString};
        String rCSV = SupportClass.makeCSVRow(options);
        return new Printout(reducerString,rCSV);
}
    public Printout reducer3(String key, ArrayList<Object> values){

        String reducerString = "";

        int count = 0;
        for(int x=0;x<values.size();x++){
            Flight passenger = (Flight) values.get(x);
            // Check if the passenger is in the hashmap if not add it to the list if it is then discard duplicates
            if(!SupportClass.passengerDistance.containsKey(passenger.getPassengerID())){
                SupportClass.passengerDistance.put(passenger.getPassengerID(),passenger.getPassengerID());
                count++;
            }
        }
        reducerString += "Flight ID:            "+key+"\r\n";
        reducerString += "Passengers on Flight: "+count;
        String[] options = {key,String.valueOf(count)};
        String rCSV = SupportClass.makeCSVRow(options);
        return new Printout(reducerString,rCSV);

    }
    
}
