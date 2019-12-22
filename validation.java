

public class validation {
	private static void failed(String input, String field) {
		MasterUtils.printBeautiful((String.format("'%s' failed validation for %s", input, field)));
	}
	public static boolean flight(String input) {
		if(input.matches("^[A-Z]{3}[0-9]{4}[A-Z]")) {
			return true;
		}
		else{
			validation.failed(input, "valid flight");
			return false;
		}
	}
	public static boolean passenger(String input) {
		if(input.matches("^[A-Z]{3}[0-9]{4}[A-Z]{2}[0-9]")) {
			return true;
		}
		else{
			validation.failed(input, "valid passenger");
			return false;
	}

	}
	public static boolean airport(String input) {
		if(input.matches("^[A-Z]{3}")) {
			return true;
		}
		else{
			validation.failed(input, "valid airport");
			return false;
	}

	}
	public static boolean faa(String input) {
		if(input.matches("^[A-Z]{3}")) {
			return true;
		}
		else{
			validation.failed(input, "valid faa");
			return false;
	}

	}
	public static boolean time(String input) {
		if(input.matches("^[0-9]{10}")) {
			return true;
		}
		else{
			validation.failed(input, "valid time");
			return false;
	}

	}
	public static boolean duration(String input) {
		if(input.matches("^[0-9]{1,4}")) {
			return true;
		}
		else{
			validation.failed(input, "duration");
			return false;
	}

	}
	public static boolean airportName(String input) {
		if(input.matches("^[A-Z]{3,20}")) {
			return true;
		}
		else{
			validation.failed(input, "valid airport name");
			return false;
	}

	}
	public static boolean latlong(String input) {
		if(input.matches("^-?\\\\d+\\\\.\\\\d+")) {
			return true;
		}
		else{
			validation.failed(input, "valid lat long");
			return false;
	}

	}
}

