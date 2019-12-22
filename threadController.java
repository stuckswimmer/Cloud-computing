
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import likeDoop.mapper;
import likeDoop.reducer;
import likeDoop.tuple;

public class threadController {
	private HashMap<String, ArrayList<String>> mapResults = new HashMap<>();
	private HashMap<String, String> reduceResults = new HashMap<>();
	private Class<? extends mapper> mapperClass;
	private Class<? extends reducer> reducerClass;
	private BufferedReader input;
	private BufferedWriter output;
	private boolean verbose;
	
	public threadController() {
		this(false);
	}
	public threadController(boolean verbose) {
		this.verbose = verbose;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}

	public void setMapper(Class<? extends mapper> mapperClass) {
		this.mapperClass = mapperClass;
	}


	public void setReducer(Class<? extends reducer> reducerClass) {
		this.reducerClass = reducerClass;
	}

	/**
	 * Initiates the MapReduce algorithm with the user-supplied application code.
	 */
	public void run() {
		try {
			this.map();
			this.reduce();
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the Mappable's .map method, through a multi-threaded ExecutorService pool. Automatically uses all threads available on the machine.
	 */
	private void map() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException, ExecutionException, InterruptedException {
		String line;
		BufferedReader input = this.input;
		Constructor constructor = this.mapperClass.getConstructor();

		int threads = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		Set<Future<ArrayList<tuple>>> set = new HashSet<>();

		while ((line = input.readLine()) != null) {
			mapper mapped = (mapper) constructor.newInstance();
			mapped.setInput(line);
			mapped.setVerbose(this.verbose);
			Future f = pool.submit(mapped);
			set.add(f);
		}

		for (Future<ArrayList<tuple>> future : set) {
			this.appendMapResults(future.get());
		}

		pool.shutdown();
	}

	/**
	 * Runs the Reducible's .reduce method, through a multi-threaded ExecutorService pool. Automatically uses all threads available on the machine.
	 */
	private void reduce() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ExecutionException, InterruptedException {
		Constructor constructor = this.reducerClass.getConstructor();

		int cpuThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cpuThreads);
		Set<Future<tuple>> set = new HashSet<>();

		for (HashMap.Entry<String, ArrayList<String>> entry : this.mapResults.entrySet()) {
			reducer reduced = (reducer) constructor.newInstance();
			reduced.setData(entry.getKey(), entry.getValue());
			reduced.setVerbose(this.verbose);

			Future f = pool.submit(reduced);
			set.add(f);
		}

		for (Future<tuple> future : set) {
			this.appendReduceResult(future.get());
		}

		pool.shutdown();
	}

	/**
	 * Writes the stored results to the output buffer.
	 */
	private void finish() throws IOException {
		String contents = "";

		for (HashMap.Entry<String, String> result : this.reduceResults.entrySet()) {
			tuple kvp = new tuple(result.getKey(), result.getValue());
			contents += String.format("%s\n", kvp);
		}

		BufferedWriter output = this.output;
		output.write(contents);
		output.close();
	}

	/**
	 * Stores an output buffer to which results are written.
	 */
	public void setOutput(BufferedWriter output) {
		this.output = output;
	}

	/**
	 * Writes an ArrayList of KVPair map results into memory for later reduction. Results are split by key into arrays*/
	private void appendMapResults(ArrayList<tuple> results) {
		for (tuple result : results) {

			ArrayList<String> values = this.mapResults.get(result.getKey());
			if (values == null) {
				this.mapResults.put(result.getKey(), new ArrayList<>());
			}

			this.mapResults.get(result.getKey()).add(result.getValue());
		}
	}
	private void appendReduceResult(tuple result) {
		this.reduceResults.put(result.getKey(), result.getValue());
	}
}



