package generic;

import processor.Clock;
import processor.Processor;

// Importing libraries for Input Output operations
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.IOException;

public class Simulator {

	static Processor processor;
	static boolean simulationComplete;

	static int numInst; // Number of instructions executed
	static int numDataHazards; // Number of times the OF stage needed to stall because of a data
								// hazard
	static int numNop; // Number of times an instruction on a wrong branch path entered the pipeline

	static EventQueue eventQueue;

	public static void setupSimulation(String assemblyProgramFile, Processor p) {
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);

		simulationComplete = false;

		numInst = numDataHazards = numNop = 0; // Initializing them to all 0's

		eventQueue = new EventQueue();
	}

	static void loadProgram(String assemblyProgramFile) {

		try {
			FileInputStream fis = new FileInputStream(assemblyProgramFile); // Input file provided
			DataInputStream dis = new DataInputStream(fis); // DIS object for reading binary numbers
			try {
				try {
					int pc = -1, address = 0; // Program counter and current address
					while (dis.available() > 0) { // While we can read from the file
						int num = dis.readInt(); // Reading 4 byte number
						if (pc == -1) { // The first integer is Header which is main function
										// address which is initial pc
							pc = num; // setting pc to num
							// Setting pc in processor
							Simulator.processor.getRegisterFile().setProgramCounter(pc);
						} else {
							// Rest of the integers will be stored in memory
							Simulator.processor.getMainMemory().setWord(address, num);
							++address; // Incrementing the address
						}
					}
				} catch (EOFException e) {
				}
				dis.close(); // Closing the dis file object
			} catch (IOException e) {
				Misc.printErrorAndExit(e.toString());
			}
		} catch (FileNotFoundException e) {
			Misc.printErrorAndExit(e.toString());
		}

		Simulator.processor.getRegisterFile().setValue(0, 0); // Setting x0 = 0
		Simulator.processor.getRegisterFile().setValue(1, 65535); // Setting x1 = 65535
		Simulator.processor.getRegisterFile().setValue(2, 65535); // Setting x2 = 65535
	}

	public static void simulate() {

		int count = 0;

		while (simulationComplete == false) { // while simulation doesn't complete
			if (count > 800) {
				// return;
			}
			++count;
			// System.out.println("====================== Clock: " + Clock.getCurrentTime()
			// + " ==================="); // TEST

			processor.getRWUnit().performRW(); // Register Write Stage

			// System.out.println("======================\n"); // TEST

			processor.getMAUnit().performMA(); // Memory Access Stage

			// System.out.println("======================\n"); // TEST

			processor.getEXUnit().performEX(); // Execution Stage

			// System.out.println("======================\n"); // TEST

			eventQueue.processEvents();

			// System.out.println("======================\n"); // TEST

			processor.getOFUnit().performOF(); // Operand Fetch Stage

			// System.out.println("======================\n"); // TEST

			processor.getIFUnit().performIF(); // Instruction Fetch Stage

			// System.out.println("##### Mem: " + processor.getMainMemory().isMainBusy()); // TEST
			// System.out.println("##### L1i: " + processor.getL1iCache().isCacheBusy()); // TEST
			// System.out.println("##### L1d: " + processor.getL1dCache().isCacheBusy()); // TEST

			Clock.incrementClock(); // Incrementing Clock

		}

		// set statistics
		// The current clock time will be the number of cycles occur
		Statistics.setNumberOfCycles((int) Clock.getCurrentTime());

		// Setting the number of instructions
		Statistics.setNumberOfInstructions(numInst);

		// Setting the number of times the OF stage needed to stall because of a data hazard
		Statistics.setNumberOfDataHazards(numDataHazards);

		// Setting the number of times an instruction on a wrong branch path entered the pipeline
		Statistics.setNumberOfNop(numNop);
	}

	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}

	// Function to increment the numInst by 1
	public static void incNumInst() {
		++numInst;
	}

	// Function to increment numDataHazards by 1
	public static void incNumDataHazards() {
		++numDataHazards;
	}

	// Function to increment numNop by 1
	public static void incNop() {
		++numNop;
	}

	public static EventQueue getEventQueue() {
		return eventQueue;
	}
}
