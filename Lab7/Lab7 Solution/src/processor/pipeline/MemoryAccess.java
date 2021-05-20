package processor.pipeline;

import processor.Clock;
import processor.Processor;
import generic.CacheReadEvent;
import generic.CacheResponseEvent;
import generic.CacheWriteEvent;
import generic.Element;
// Required imports
import generic.Event;
import generic.Instruction;
import generic.Misc;
import generic.Simulator;

// Implementing Element interface for MemoryAccess class
public class MemoryAccess implements Element {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	// Variables to store while scheduling for event
	int excess;
	Instruction instruction;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch,
			MA_RW_LatchType mA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}

	public void performMA() {
		// System.out.println("MA Stage"); // TEST

		if (EX_MA_Latch.isMA_enable()) {

			if (!EX_MA_Latch.isMABusy()) { // If MA Stage is not busy

				if (EX_MA_Latch.isValidInst()) { // If there is a valid instruction
					Instruction inst = EX_MA_Latch.getInstruction(); // Instruction as control
																		// signal

					// System.out.println("Instruction: " + inst); // TEST
					this.instruction = inst;

					if (inst != null) { // If instruction is not a null (or nop) instruction
						int aluResult = EX_MA_Latch.getAluResult(); // Alu Result obtained
						int operand = EX_MA_Latch.getOperand(); // Operand to store in case of store
																// instruction
						this.excess = EX_MA_Latch.getExcess();

						// Performing on base of operation of instruction
						switch (inst.getOperationType()) {
							case load: {
								// System.out.println("Scheduling Event..."); // TEST

								// Alu Result will be address from which we have to load in case of
								// load instruction
								if (!containingProcessor.getL1dCache().isCacheBusy()) {
									Simulator.getEventQueue()
											.addEvent(new CacheReadEvent(Clock.getCurrentTime(),
													this, containingProcessor.getL1dCache(),
													aluResult));
								} else {
									// System.out.println("Data Cache Busy"); // TEST
								}
								EX_MA_Latch.setMABusy(true);
								break;
							}

							case store: {
								// System.out.println("Scheduling Event..."); // TEST

								// Alu Result will be address where we need to store in case of
								// store instruction
								if (!containingProcessor.getL1dCache().isCacheBusy()) {
									Simulator.getEventQueue()
											.addEvent(new CacheWriteEvent(Clock.getCurrentTime(),
													this, containingProcessor.getL1dCache(),
													aluResult, operand));
								} else {
									// System.out.println("Data Cache Busy"); // TEST
								}
								EX_MA_Latch.setMABusy(true);
								break;
							}

							case add:
							case sub:
							case mul:
							case div:
							case and:
							case or:
							case xor:
							case slt:
							case sll:
							case srl:
							case sra:
							case addi:
							case subi:
							case muli:
							case divi:
							case andi:
							case ori:
							case xori:
							case slti:
							case slli:
							case srli:
							case srai:
							case beq:
							case bne:
							case blt:
							case bgt:
							case jmp:
							case end: {
								EX_MA_Latch.setMABusy(false);
								EX_MA_Latch.setMA_enable(false);
								EX_MA_Latch.setValidInst(false);

								// Passing aluResult to Register Writeback stage to store it
								// register
								MA_RW_Latch.setInstruction(this.instruction); // Passing instruction
																				// forward in
																				// pipeline
								MA_RW_Latch.setExcess(this.excess); // Passing excess bits to
																	// Register Writeback stage
								MA_RW_Latch.setAluResult(aluResult);
								MA_RW_Latch.setRW_enable(true);
								MA_RW_Latch.setValidInst(true);
								break;
							}

							default:
								Misc.printErrorAndExit("Unknown Instruction!!");
						}
					} else {
						// System.out.println("NOP Received"); // TEST

						MA_RW_Latch.setInstruction(inst); // Passing instruction forward in pipeline
						MA_RW_Latch.setValidInst(true);
						EX_MA_Latch.setValidInst(false);
					}
				} else {
					// System.out.println("Invalid Instruction"); // TEST
				}
			} else {
				// System.out.println("MA Busy"); // TEST
			}

			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		} else {
			// System.out.println("MA Not Enabled"); // TEST
		}
	}

	// Handle Event Function to handle events to be processed by MemoryAccess stage
	@Override
	public void handleEvent(Event e) {
		// System.out.println("MA Event Detected: " + e); // TEST
		CacheResponseEvent event = (CacheResponseEvent) e;
		// System.out.println("Event Triggered in MA: \n" + event); // TEST
		containingProcessor.getL1dCache().setCacheBusy(false);

		EX_MA_Latch.setMABusy(false);
		EX_MA_Latch.setValidInst(false);

		MA_RW_Latch.setInstruction(this.instruction); // Pass instruction to RW stage
		MA_RW_Latch.setExcess(this.excess); // Pass excess value to RW stage
		MA_RW_Latch.setLdResult(event.getValue()); // Pass obtained load result to RW stage
		MA_RW_Latch.setValidInst(true);
	}

}
