package generic;

import java.io.PrintWriter;

public class Statistics {

	static int numberOfInstructions;
	static int numberOfCycles;

	static int numberOfDataHazards; // Number of times the OF stage needed to stall because of a
									// data hazard
	static int numberOfNop; // Number of times an instruction on a wrong branch path entered the
							// pipeline


	public static void printStatistics(String statFile) {
		try {
			PrintWriter writer = new PrintWriter(statFile);

			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println(
					"Number of times an instruction on a wrong branch path entered the pipeline = "
							+ numberOfNop);
			writer.println(
					"Number of times the OF stage needed to stall because of a data hazard = "
							+ numberOfDataHazards);
			writer.println("Throughput in terms of Number of instructions per cycle = "
					+ ((double) numberOfInstructions / (double) numberOfCycles));

			writer.close();
		} catch (Exception e) {
			Misc.printErrorAndExit(e.getMessage());
		}
	}

	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}

	// Setter methods for above two variables defined
	public static void setNumberOfDataHazards(int numberOfDataHazards) {
		Statistics.numberOfDataHazards = numberOfDataHazards;
	}

	public static void setNumberOfNop(int numberOfNop) {
		Statistics.numberOfNop = numberOfNop;
	}
}
