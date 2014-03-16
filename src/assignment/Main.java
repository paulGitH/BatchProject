package assignment;

import batch.*;

public class Main {

	/**
	 * @param args name of batch file that will be parsed and executed
	 */
	public static void main(String[] args) {
		
		BatchParser batchParser = new BatchParser(args[0]);
		
		batchParser.RunBatch();
	}

}
