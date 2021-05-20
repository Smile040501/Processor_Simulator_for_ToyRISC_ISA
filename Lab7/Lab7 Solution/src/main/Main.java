package main;

import configuration.Configuration;
import generic.Misc;
import generic.Statistics;
import processor.Processor;
import generic.Simulator;

public class Main {

	public static void main(String[] args) {
		if (args.length != 3) {
			Misc.printErrorAndExit(
					"usage: java -jar <path-to-jar-file> <path-to-config-file> <path-to-stat-file> <path-to-object-file>\n");
		}

		Configuration.parseConfigurationFile(args[0]);

		Processor processor = new Processor();

		Simulator.setupSimulation(args[2], processor);
		Simulator.simulate();

		processor.printState(0, 31); // ((0, 0) refers to the range of main memory addresses we
										// wish to print. this is an empty set.

		Statistics.printStatistics(args[1]);
	}

}
