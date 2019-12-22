

public class Airport {
	String name;
	String faaCode;
	double lat;
	double lon;
	private Integer numOfFlightsFrom;

	
	Airport(String _name, String _faaCode, String _lat, String _lon){
		this.name=_name;
		this.faaCode=_faaCode;
		this.numOfFlightsFrom = 0;
		
		try {
		this.lat=Double.parseDouble(_lat);
		this.lon=Double.parseDouble(_lon);
		}
		catch (Exception e) {
            this.lat = 0.0;
            this.lon = 0.0;
		}
	}
	public String getName() {
		return this.name;
	}
	public String getFAA() {
		return this.faaCode;
	}
	public double getLat() {
		return this.lat;
	}
	public double getLon() {
		return this.lon;
	}
	public String toString() {
		return name+" ("+faaCode+") is at "+lat+" by "+lon;
	}
	public String toString(String name) {
		return name+" ("+faaCode+") is at "+lat+" by "+lon;
	}

}
