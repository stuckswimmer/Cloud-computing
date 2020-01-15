package likeDoop;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class Reducer implements Callable{
	private String key;
	private ArrayList<String> values;
	private boolean verbose;

	/**
	 * Converts a line of a file into zero or more KVPair objects.
	 *
	 * @param key    The key from which the values are derived.
	 * @param values An ArrayList of zero or more String values, as a result from mapping.
	 * @return An ArrayList of zero or more KVPairs.
	 */
	protected abstract Tuple reduce(String key, ArrayList<String> values);

	/**
	 * Internal method, sets the operated key and value list.
	 *
	 * @param key    The key from which the values are derived.
	 * @param values The list of values which are reduced.
	 */
	void setData(String key, ArrayList<String> values) {
		this.key = key;
		this.values = values;
	}

	/**
	 * Implements the Callable interface for multi-threading by the controller.
	 *
	 * @return The return value of the user-implemented this.reduce
	 */
	@Override
	public Tuple call() {
		if (this.verbose) {
			System.out.println("Reduce { " + this.key + " , " + values.size() + " values } :: " + Thread.currentThread().getName());
		}

		return this.reduce(this.key, this.values);
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
