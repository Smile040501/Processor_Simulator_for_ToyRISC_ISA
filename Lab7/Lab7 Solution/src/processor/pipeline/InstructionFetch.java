package processor.pipeline;

import generic.CacheReadEvent;
import generic.CacheResponseEvent;
import generic.Element;
import generic.Event;
import generic.Simulator;
import processor.Clock;
import processor.Processor;

// Implementing Element interface for InstructionFetch class
public class InstructionFetch implements Element {

	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;

	int previousPC; // To keep track of PC of instruction requested from Main Memory

	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
			IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;

		previousPC = 0;
	}

	// Modified function to implement functionality of EX_IF_Latch
	public void performIF() {
		// System.out.println("IF Stage"); // TEST

		if (IF_EnableLatch.isIF_enable()) {

			if (!IF_EnableLatch.isIFBusy()) { // If IF stage is not busy

				if (!IF_EnableLatch.getStall()) { // If IF stage is not stall
					if (!containingProcessor.getL1iCache().isCacheBusy()) {
						if (EX_IF_Latch.getIsBranchTaken()) { // if isBranchTaken signal is True
							// System.out.println("IF Control Hazard Detected"); // TEST

							// Updating the pc of the processor
							containingProcessor.getRegisterFile()
									.setProgramCounter(EX_IF_Latch.getBranchPC());

							EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal to
																	// False
						}

						int currentPC = containingProcessor.getRegisterFile().getProgramCounter();

						// System.out.println("Requested for Instruction At: " + currentPC); // TEST

						// Adding MemoryReadEvent to the event queue to request the next instruction
						Simulator.getEventQueue()
								.addEvent(new CacheReadEvent(Clock.getCurrentTime(), this,
										containingProcessor.getL1iCache(), currentPC));
						IF_EnableLatch.setIFBusy(true); // Setting IF stage as busy

						previousPC = currentPC; // Updating previousPC

						containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

						Simulator.incNumInst(); // Incrementing the Number of instructions fetched
					} else {
						// System.out.println("Instruction Cache Busy"); // TEST
					}
				} else {
					// System.out.println("IF Stalled"); // TEST
				}
			} else {
				// System.out.println("IF Busy"); // TEST
			}
			// As IF stage will always be enabled to fetch instructions
			// IF_EnableLatch.setIF_enable(false);

			IF_OF_Latch.setOF_enable(true);
		} else {
			// System.out.println("IF Not Enabled"); // TEST
		}
	}

	// Handling Events processed by IF stage
	@Override
	public void handleEvent(Event e) {
		// System.out.println("IF Event Detected: " + e); // TEST

		if (IF_OF_Latch.isOFBusy()) { // If OF stage is busy
			// System.out.println("Event postponed | OF Busy"); // TEST

			// Postponing the event and adding it back to event queue
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);

		} else if (IF_OF_Latch.getNop()) { // If there occurs a Control Hazard
			// System.out.println(
			// "Event Discarded Due to Wrong Instruction Fetch due to Control Hazard!"); // TEST

			containingProcessor.getL1iCache().setCacheBusy(false);
			// Updating the pc of the processor
			containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC());

			IF_EnableLatch.setIFBusy(false); // Setting IF to not busy

			IF_OF_Latch.setValidInst(false); // Setting instruction as invalid in OF stage
			IF_OF_Latch.setNop(false); // Setting instruction as not NOP in IF-OF Latch
			IF_OF_Latch.setOF_enable(true); // Enabling OF Stage

			EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal to False
		} else {
			CacheResponseEvent event = (CacheResponseEvent) e;
			// System.out.println("Event Triggered in IF: \n" + event); // TEST
			containingProcessor.getL1iCache().setCacheBusy(false);

			IF_EnableLatch.setIFBusy(false); // Setting IF to not busy

			IF_OF_Latch.setInstruction(event.getValue());
			IF_OF_Latch.setValidInst(true); // Setting instruction as valid in OF stage
			IF_OF_Latch.setNop(false); // Setting new instruction as not nop
			IF_OF_Latch.setCurrentPC(previousPC); // passing the PC ahead in pipeline
			IF_OF_Latch.setOF_enable(true); // Enabling OF Stage
		}
	}

}
