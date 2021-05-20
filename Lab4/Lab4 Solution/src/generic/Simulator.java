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

	public static void setupSimulation(String assemblyProgramFile, Processor p) {
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);

		simulationComplete = false;
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

		int numberOfInstructions = 0; // Number of instructions executed

		while (simulationComplete == false) { // while simulation doesn't complete
			processor.getIFUnit().performIF();
			Clock.incrementClock();

			processor.getOFUnit().performOF();
			Clock.incrementClock();

			processor.getEXUnit().performEX();
			Clock.incrementClock();

			processor.getMAUnit().performMA();
			Clock.incrementClock();

			processor.getRWUnit().performRW();
			Clock.incrementClock();

			++numberOfInstructions; // Incrementing the number of instructions by 1
		}

		// set statistics
		// The current clock time will be the number of cycles occur
		Statistics.setNumberOfCycles((int) Clock.getCurrentTime());
		// Setting the number of instructions
		Statistics.setNumberOfInstructions(numberOfInstructions);
	}

	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}
}
