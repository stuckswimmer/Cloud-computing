package likeDoop;

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

public class ThreadController {
	private final HashMap<String, ArrayList<String>> mapResults = new HashMap<>();
	private final HashMap<String, String> reduceResults = new HashMap<>();
	private Class<? extends Mapper> mapperClass;
	private Class<? extends Reducer> reducerClass;
	private BufferedReader input;
	private BufferedWriter output;
	private boolean verbose;

	public ThreadController() {
		this(false);
	}

	public ThreadController(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the input stream for the job.
	 *
	 * @param input a stream from which input is read, line-by-line.
	 */
	public void setInput(BufferedReader input) {
		this.input = input;
	}

	/**
	 * Configures a Mappable class for multi-threaded instantiation.
	 *
	 * @param mapperClass A class which extends library Mappable class.
	 */
	public void setMapper(Class<? extends Mapper> mapperClass) {
		this.mapperClass = mapperClass;
	}

	/**
	 * Configures a Reducible class for multi-threaded instantiation.
	 *
	 * @param reducerClass A class which extends library Reducible class.
	 */
	public void setReducer(Class<? extends Reducer> reducerClass) {
		this.reducerClass = reducerClass;
	}

	/**
	 * Initiates the MapReduce algorithm with the user-supplied application code.
	 */
	public void run() {
		try {
			System.out.println("-----Start Mapper----");
			this.map();
			System.out.println("-----Start Reducer----");
			this.reduce();
			System.out.println("-----Writing to disk----");
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the Mappable's .map method, through a multi-threaded ExecutorService pool. Automatically uses all threads available on the machine.
	 *
	 * @throws NoSuchMethodException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void map() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException, InstantiationException, ExecutionException, InterruptedException {
		String line;
		BufferedReader input = this.input;
		Constructor constructor = this.mapperClass.getConstructor();

		int cpuThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cpuThreads);
		Set<Future<ArrayList<Tuple>>> set = new HashSet<>();

		while ((line = input.readLine()) != null) {
			Mapper mapper = (Mapper) constructor.newInstance();
			mapper.setInput(line);
			mapper.setVerbose(this.verbose);
			Future f = pool.submit(mapper);
			set.add(f);
		}
		for (Future<ArrayList<Tuple>> future : set) {
			this.shuffler(future.get());
		}

		pool.shutdown();
	}

	/**
	 * Runs the Reducible's .reduce method, through a multi-threaded ExecutorService pool. Automatically uses all threads available on the machine.
	 *
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void reduce() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ExecutionException, InterruptedException {
		Constructor constructor = this.reducerClass.getConstructor();

		int cpuThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cpuThreads);
		Set<Future<Tuple>> set = new HashSet<>();

		for (HashMap.Entry<String, ArrayList<String>> entry : this.mapResults.entrySet()) {
			Reducer reducer = (Reducer) constructor.newInstance();
			reducer.setData(entry.getKey(), entry.getValue());
			reducer.setVerbose(this.verbose);

			Future f = pool.submit(reducer);
			set.add(f);
		}
		

		for (Future<Tuple> future : set) {
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
			Tuple tuple = new Tuple(result.getKey(), result.getValue());
			contents += String.format("%s\n", tuple);
		}
		System.out.println(contents);

		BufferedWriter output = this.output;
		output.write(contents);
		output.close();
	}

	/**
	 * Stores an output buffer to which results are written.
	 *
	 * @param output The output buffer.
	 */
	public void setOutput(BufferedWriter output) {
		this.output = output;
	}

	/**
	 * Writes an ArrayList of KVPair map results into memory for later reduction. Results are split by key into arrays.
	 *
	 * @param results An ArrayList of zero or more KVPairs.
	 */
	private void shuffler(ArrayList<Tuple> results) {
		
		for (Tuple result : results) {

			ArrayList<String> values = this.mapResults.get(result.getKey());
			if (values == null) {
				this.mapResults.put(result.getKey(), new ArrayList<>());
			}

			this.mapResults.get(result.getKey()).add(result.getValue());
		}
		
	}

	/**
	 * Stores reduce results in memory for writing to disk.
	 *
	 * @param result A single KVPair representing a reduction result.
	 */
	private void appendReduceResult(Tuple result) {
		this.reduceResults.put(result.getKey(), result.getValue());
	}

}
