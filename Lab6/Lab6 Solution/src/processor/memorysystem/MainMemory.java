package processor.memorysystem;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;

// Implementing the Element interface for MainMemory Class
public class MainMemory implements Element {
	int[] memory;

	public MainMemory() {
		memory = new int[65536];
	}

	public int getWord(int address) {
		return memory[address];
	}

	public void setWord(int address, int value) {
		memory[address] = value;
	}

	public String getContentsAsString(int startingAddress, int endingAddress) {
		if (startingAddress == endingAddress)
			return "";

		StringBuilder sb = new StringBuilder();
		sb.append("\nMain Memory Contents:\n\n");
		for (int i = startingAddress; i <= endingAddress; i++) {
			sb.append(i + "\t\t: " + memory[i] + "\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	// Handle Event Function to handle events requested/processed by Main Memory
	@Override
	public void handleEvent(Event e) {
		if (e.getEventType() == Event.EventType.MemoryRead) { // If Memory Read event
			MemoryReadEvent event = (MemoryReadEvent) e;

			// System.out.println("Event Triggered From Main Memory: \n" + event); // TEST

			// Adding response event with Main Memory Latency and required memory variable required
			Simulator.getEventQueue()
					.addEvent(new MemoryResponseEvent(
							Clock.getCurrentTime() + Configuration.mainMemoryLatency, this,
							event.getRequestingElement(), getWord(event.getAddressToReadFrom())));

		} else if (e.getEventType() == Event.EventType.MemoryWrite) { // If Memory Write Event
			MemoryWriteEvent event = (MemoryWriteEvent) e;

			// System.out.println("Event Triggered From Main Memory: \n" + event); // TEST

			// Setting the given value at the given address
			setWord(event.getAddressToWriteTo(), event.getValue());

			// Adding response event with Main Memory Latency
			Simulator.getEventQueue()
					.addEvent(new MemoryResponseEvent(
							Clock.getCurrentTime() + Configuration.mainMemoryLatency, this,
							event.getRequestingElement(), event.getAddressToWriteTo()));

		}
	}
}
