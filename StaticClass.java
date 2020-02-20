import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StaticClass {
	 public static HashMap<String,Airport> airportHashMap = new HashMap<String, Airport>();
	    public static ArrayList<String> objective1Airports = new ArrayList<String>();
	    public static HashMap<String,String> passengerObjective3Hash = new HashMap<String, String>();
	    public static HashMap<String,String> passengerObjective2Hash = new HashMap<String, String>();
	    public static HashMap<String,String> passengerObjective4Hash = new HashMap<String, String>();
	    public static String objective1CSVString = "";
	    public static String objective2CSVString = "";
	    public static String objective3CSVString = "";
	    public static String objective4CSVString = "";
	    public static String objective1TextString = "";
	    public static String objective2TextString = "";
	    public static String objective3TextString = "";
	    public static String objective4TextString = "";
	    
	    
	    
	    static ArrayList<String> getData(String pathToFile){
	        ArrayList<String> arrayOfLines = new ArrayList<String>();
	        // store each line in array object
	        String line = "";
	        try {
	            BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
	            while((line = reader.readLine()) != null ){
	              //  System.out.println(line);
	                arrayOfLines.add(line);
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return arrayOfLines;
	    }
	    /**
	     * Maps the airports in a hash map to enable them to be esaily looked up
	     *
	     * @param airportArrayLines lines of input csv
	     * **/
	    static void getAirportHashMap(ArrayList<String> airportArrayLines){
	        for(int x=0;x<airportArrayLines.size();x++){
	            String[] row = airportArrayLines.get(x).split(",");
	            if(row.length-1>2){
	                String airportName = row[0];
	                String airportCode = row[1];
	                Float lat = Float.valueOf(row[2]);
	                Float lng = Float.valueOf(row[3]);
	                Airport airport = new Airport(airportName,airportCode,lat,lng);
	                airportHashMap.put(airportCode,airport);
	            }

	        }
	    }

	    /**
	     * shuffle and sorts the mapper outputs
	     *
	     * @param mapperOutput  list of mapper outputs
	     * @param hashMap       existing hashmap to add values too
	     * @return
	     */
	    static HashMap<String,ArrayList<Object>> shuffleSort(ArrayList<KVPair> mapperOutput,HashMap<String, ArrayList<Object>> hashMap){

	        for(int x=0;x<mapperOutput.size();x++){
	            String key = mapperOutput.get(x).getKey();
	            Object value = mapperOutput.get(x).getValue();
	            if(hashMap.containsKey(key)){
	                hashMap.get(key).add(value);
	            } else{
	                ArrayList<Object> objectArrayList = new ArrayList<Object>();
	                objectArrayList.add(value);
	                hashMap.put(key,objectArrayList);
	            }
	        }
	        return hashMap;
	    }


	    /**
	     * Split the lines of data into smaller chunks
	     *
	     * @param bigList   list of all rows
	     * @param n         number of rows in each chunk
	     * @return
	     */
	    public static ArrayList<ArrayList<String>> partitionData(ArrayList<String> bigList, int n){
	        ArrayList<ArrayList<String>> chunks = new ArrayList<ArrayList<String>>();

	        for (int i = 0; i < bigList.size(); i += n) {
	            ArrayList<String> chunk = new ArrayList<String>(bigList.subList(i, Math.min(bigList.size(), i + n)));
	            chunks.add(chunk);
	        }

	        return chunks;
	    }

	    /**
	     * Calculate which airports are missing from objective 1
	     *
	     * @return airports missing
	     */
	    static Printout missingAirports(){
	        ArrayList<String> allAirports = new ArrayList<String>(StaticClass.airportHashMap.keySet());
	        for (int x=0;x<StaticClass.objective1Airports.size();x++){
	            allAirports.remove(StaticClass.objective1Airports.get(x));
	        }
	        String reducerString = "Missing Airports:\n";
	        String reducerCSV = "";
	        for(int x=0;x<allAirports.size();x++){
	            reducerString += "          "+allAirports.get(x)+","+
	                    StaticClass.airportHashMap.get(allAirports.get(x)).getAirportName()+"\r\n";
	            reducerCSV += allAirports.get(x) +","+StaticClass.airportHashMap.get((allAirports.get(x))).getAirportName()+"\n";
	        }
	        Printout reducerOuput = new Printout(reducerString,reducerCSV);
	        return reducerOuput;
	    }
	    /*Converts the depart latitude longitude and arrive latitude longitude
	     * into nautical miles
	     */
		static int latLongToMiles(float lat1, float lon1, float lat2, float lon2) {
			double R = 6378.137; // Radius of earth in KM
			double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
			double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
					Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
							Math.sin(dLon / 2) * Math.sin(dLon / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double dist = R * c;
			dist = dist * 1000;
			int nauticalMiles= (int) dist / 1852;
			
			return nauticalMiles;
		}
		
		static int distance(String a,String b) {
			 a = a.toLowerCase();
		        b = b.toLowerCase();
		        // i == 0
		        int [] costs = new int [b.length() + 1];
		        for (int j = 0; j < costs.length; j++)
		            costs[j] = j;
		        for (int i = 1; i <= a.length(); i++) {
		            // j == 0; nw = lev(i - 1, j)
		            costs[0] = i;
		            int nw = i - 1;
		            for (int j = 1; j <= b.length(); j++) {
		                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
		                nw = costs[j];
		                costs[j] = cj;
		            }
		        }
		        return costs[b.length()];
		}

	    /**
	     * Create a csv from the input
	     *
	     * @param name      name of file
	     * @param csvString csv string
	     */
	    static void createCSV(String name,String csvString){
	        FileWriter fileWriter = null;
	        try {
	            fileWriter = new FileWriter(name);

	            fileWriter.append(csvString);

	            System.out.println("CSV file was created successfully !!!");

	        } catch (Exception e) {
	            System.out.println("Error in CsvFileWriter !!!");
	            e.printStackTrace();
	        } finally {

	            try {
	                fileWriter.flush();
	                fileWriter.close();
	            } catch (IOException e) {
	                System.out.println("Error while flushing/closing fileWriter !!!");
	                e.printStackTrace();
	            }

	        }
	    }

	    /**
	     * Create a csvString from the inputs
	     *
	     * @param headings  headings for the CSV
	     * @param reducerOuputs outputs from the reducer
	     * @return
	     */
	    static String createCSVString(String[] headings, ArrayList<Printout> reducerOuputs){
	        String csvString = "";
	        csvString += StaticClass.makeCSVRow(headings);
	        for (int x=0;x<reducerOuputs.size();x++){
	            csvString += reducerOuputs.get(x).reducerCSV;
	        }
	        return csvString;
	    }

	    /**
	     * Create a csvString from the inputs with additional data
	     *
	     * @param headings  headings for the CSV
	     * @param reducerOuputs outputs from the reducer
	     * @param additionalHeadings    additional heading
	     * @param additionalData    additional data
	     * @return
	     */
	    static String createCSVString(String[] headings, ArrayList<Printout> reducerOuputs,String[] additionalHeadings, String additionalData){
	        String csvString = "";
	        csvString += StaticClass.makeCSVRow(headings);
	        for (int x=0;x<reducerOuputs.size();x++){
	            csvString += reducerOuputs.get(x).reducerCSV;
	        }

	        csvString+="\n\n";
	        csvString+=StaticClass.makeCSVRow(additionalHeadings);
	        csvString+=additionalData;
	        return csvString;
	    }

	    /**
	     * Make text file from inputs
	     *
	     * @param nameOfFile    name of file
	     * @param output    string to output to file
	     */
	    static void makeTxtFile(String nameOfFile,String output){
	        BufferedWriter fileWriter = null;
	        try {
	            fileWriter = new BufferedWriter(new FileWriter(nameOfFile));


	            fileWriter.append(output);

	            System.out.println("TXT file was created successfully !!!");

	        } catch (Exception e) {
	            System.out.println("Error in Txt file writer !!!");
	            e.printStackTrace();
	        } finally {

	            try {
	                fileWriter.flush();
	                fileWriter.close();
	            } catch (IOException e) {
	                System.out.println("Error while flushing/closing fileWriter !!!");
	                e.printStackTrace();
	            }

	        }
	    }

	    /**
	     * Make a csv row from the values given
	     *
	     * @param values    values to be made into csv row
	     * @return
	     */
	    static String makeCSVRow(String[] values){
	        String csvString = "";
	        for(int x=0;x<values.length;x++){
	            csvString += values[x] +",";
	        }
	        csvString += "\n";
	        return csvString;
	    }

}
