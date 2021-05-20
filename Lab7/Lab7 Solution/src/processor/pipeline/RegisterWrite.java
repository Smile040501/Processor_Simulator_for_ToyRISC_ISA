package processor.pipeline;

import generic.Simulator;
import processor.Processor;

// Required imports
import generic.Instruction;
import generic.Misc;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch,
			IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performRW() {
		// System.out.println("RW Stage"); // TEST

		if (MA_RW_Latch.isRW_enable()) {
			if (MA_RW_Latch.isValidInst()) { // If there is a valid instruction
				Instruction inst = MA_RW_Latch.getInstruction(); // Instruction as control signals

				// System.out.println("Instruction: \n" + inst); // TEST

				MA_RW_Latch.setValidInst(false);

				if (inst != null) { // If instruction is not a null (or nop) instruction


					int ldResult = MA_RW_Latch.getLdResult(); // Load result
					int aluResult = MA_RW_Latch.getAluResult(); // ALU result

					int excess = MA_RW_Latch.getExcess(); // excess bits
					containingProcessor.getRegisterFile().setValue(31, excess); // setting excess
																				// bits to x31
																				// register

					int rd = 0; // Destination register where we need to store result
					if (inst.getDestinationOperand() != null) { // If it is not an end instruction
						rd = inst.getDestinationOperand().getValue();
					}

					switch (inst.getOperationType()) {

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
							// R2I type
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
						case srai: {
							// Storing ALU result at destination register
							containingProcessor.getRegisterFile().setValue(rd, aluResult);
							break;
						}

						case load: {
							// Storing Load register at destination register
							containingProcessor.getRegisterFile().setValue(rd, ldResult);
							break;
						}

						case store:
						case beq:
						case bne:
						case blt:
						case bgt:
							// RI type :
						case jmp:
							break;

						case end: {
							// if instruction being processed is an end instruction
							Simulator.setSimulationComplete(true); // Setting simulation as complete
							break;
						}

						default:
							Misc.printErrorAndExit("Unknown Instruction!!");
					}
				} else {
					// System.out.println("NOP Received"); // TEST
				}

			} else {
				// System.out.println("Invalid Instruction"); // TEST
			}

			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		} else {
			// System.out.println("RW Not Enabled"); // TEST
		}
	}

}
