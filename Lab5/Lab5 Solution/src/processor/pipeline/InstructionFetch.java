package processor.pipeline;

import generic.Simulator;
import processor.Processor;

public class InstructionFetch {

	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;

	boolean firstInstruction = true;

	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
			IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}

	// Modified function to implement functionality of EX_IF_Latch
	public void performIF() {
		if (IF_EnableLatch.isIF_enable()) {

			if (!IF_EnableLatch.getStall()) { // If IF stage is not stall
				if (EX_IF_Latch.getIsBranchTaken()) { // if isBranchTaken signal is True
					// Updating the pc of the processor
					containingProcessor.getRegisterFile()
							.setProgramCounter(EX_IF_Latch.getBranchPC());

					EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal to False
				}

				int currentPC = containingProcessor.getRegisterFile().getProgramCounter();

				int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);
				IF_OF_Latch.setInstruction(newInstruction);
				IF_OF_Latch.setNop(false); // Setting new instruction as not nop

				IF_OF_Latch.setCurrentPC(currentPC); // passing the current pc ahead in pipeline

				containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);

				Simulator.incNumInst(); // Incrementing the Number of instructions fetched
			}
			// As IF stage will always be enabled to fetch instructions
			// IF_EnableLatch.setIF_enable(false);

			IF_OF_Latch.setOF_enable(true);
		}
	}

}
