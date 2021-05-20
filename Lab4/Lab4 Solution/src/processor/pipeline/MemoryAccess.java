package processor.pipeline;

import processor.Processor;

// Required imports
import generic.Instruction;
import generic.Misc;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch,
			MA_RW_LatchType mA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_enable()) {
			Instruction inst = EX_MA_Latch.getInstruction(); // Instruction as control signal
			MA_RW_Latch.setInstruction(inst); // Passing instruction forward in pipeline
			int aluResult = EX_MA_Latch.getAluResult(); // Alu Result obtained
			int operand = EX_MA_Latch.getOperand(); // Operand to store in case of store instruction
			int excess = EX_MA_Latch.getExcess(); // Excess bits to store at x31 register
			MA_RW_Latch.setExcess(excess); // Passing excess bits to Register Writeback stage

			// Performing on base of operation of instruction
			switch (inst.getOperationType()) {
				case load: {
					// Alu Result will be address from which we have to load in case of load
					// instruction
					int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
					MA_RW_Latch.setLdResult(ldResult); // Passing the ldResult to store it to
														// register to Register Writeback stage
					break;
				}

				case store: {
					// Alu Result will be address where we need to store in case of store
					// instruction
					// Setting operand value at that address
					containingProcessor.getMainMemory().setWord(aluResult, operand);
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
					// Passing aluResult to Register Writeback stage to store it register
					MA_RW_Latch.setAluResult(aluResult);
					break;
				}

				default:
					Misc.printErrorAndExit("Unknown Instruction!!");
			}


			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
