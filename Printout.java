public class Printout {
	protected String reducerString;
    protected String reducerCSV;

    /**
     * Constructor for ReducerOutput
     *
     * @param rString   reducer String for text file
     * @param rCSV  reducer csv string for csv file
     */
    public Printout(String lString,String lCSV){
        this.reducerString = lString;
        this.reducerCSV = lCSV;
    }


}
