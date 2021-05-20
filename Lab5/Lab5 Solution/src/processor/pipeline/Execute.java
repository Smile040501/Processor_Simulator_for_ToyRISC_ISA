package processor.pipeline;

import processor.Processor;

// Required imports
import generic.Instruction;
import generic.Misc;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch,
			EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}

	public void performEX() {
		if (OF_EX_Latch.isEX_enable()) {
			Instruction inst = OF_EX_Latch.getInstruction(); // Getting the instruction
			EX_MA_Latch.setInstruction(inst); // Passing instruction as control signals to pipeline

			if (inst != null) { // If instruction is not a null (or nop) instruction
				compute(inst); // executing the instruction

				// Performing Control-Interlock validation for branch instructions
				containingProcessor.getControlInterlockUnit().validate();
			}

			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);
		}
	}

	// Function that acts like ALU and computes the aluResult and set excess value to x31
	// Set the flags for branching
	private void compute(Instruction inst) {

		long op1 = OF_EX_Latch.getOperand1(); // operand1
		long op2 = OF_EX_Latch.getOperand2(); // operand2
		long imm = OF_EX_Latch.getImmediate(); // immediate
		long second = (OF_EX_Latch.getIsImmediate()) ? imm : op2; // second operand for ALU
																	// operations

		// Performing operation based on operation time
		switch (inst.getOperationType()) {

			case add:
			case addi: {
				EX_MA_Latch.setAluResult(getResult(op1 + second)); // Adder
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case sub:
			case subi: {
				EX_MA_Latch.setAluResult(getResult(op1 - second)); // Subtraction
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case mul:
			case muli: {
				EX_MA_Latch.setAluResult(getResult(op1 * second)); // Multiplier
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case div:
			case divi: {
				EX_MA_Latch.setAluResult(getResult(op1 / second)); // Division
				EX_MA_Latch.setExcess((int) (op1 % second)); // setting remainder as excess value
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case and:
			case andi: {
				EX_MA_Latch.setAluResult(getResult(op1 & second)); // AND operation
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case or:
			case ori: {
				EX_MA_Latch.setAluResult(getResult(op1 | second)); // OR operation
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case xor:
			case xori: {
				EX_MA_Latch.setAluResult(getResult(op1 ^ second)); // XOR operation
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case slt:
			case slti: {
				EX_MA_Latch.setAluResult((op1 < second) ? 1 : 0);
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case sll:
			case slli: {
				EX_MA_Latch.setAluResult(getResult(op1 << second)); // Left shift
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case srl:
			case srli: {
				EX_MA_Latch.setAluResult(getResult(op1 >>> second)); // Logical right shift
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case sra:
			case srai: {
				EX_MA_Latch.setAluResult(getResult(op1 >> second)); // Arithmetic right shift
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case load: {
				EX_MA_Latch.setAluResult(getResult(op1 + imm)); // Address of load operation
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case store: {
				EX_MA_Latch.setAluResult(getResult(op2 + imm)); // Address of store operation
				EX_MA_Latch.setOperand((int) op1); // Setting operand to store at store address
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			case beq: {
				if (op1 == op2) { // if true
					EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				} else {
					EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				}
				break;
			}

			case bne: {
				if (op1 != op2) { // if true
					EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				} else {
					EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				}
				break;
			}

			case blt: {
				if (op1 < op2) { // if true
					EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				} else {
					EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				}
				break;
			}

			case bgt: {
				if (op1 > op2) { // if true
					EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				} else {
					EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				}
				break;
			}

			case jmp: {
				EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
				// Setting branchPC
				EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
				break;
			}


			case end: {
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				break;
			}

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}
	}

	// Function to return first 32 bits result, and passing the excess bits
	private int getResult(long res) {
		String binaryString = Long.toBinaryString(res);
		if (binaryString.length() <= 32) { // if number of bits are <= 32
			return (int) res;

		} else {
			EX_MA_Latch.setExcess(binaryToDecimal( // Setting excess
					binaryString.substring(0, binaryString.length() - 32), (res < 0)));

			return binaryToDecimal(binaryString.substring(binaryString.length() - 32), (res < 0));
		}
	}

	// Function to convert binary string to decimal, considering whether it is signed or not
	private int binaryToDecimal(String binaryString, boolean isSigned) {
		if (!isSigned) { // if not signed
			return Integer.parseInt(binaryString, 2);

		} else {
			String copyString = '0' + binaryString.substring(1); // Considering only first n-1 bits
			int ans = Integer.parseInt(copyString, 2); // The integer corresponding to first n-1
														// bits

			if (binaryString.length() == 32) { // if length is 32
				int power = (1 << 30); // 2^30 // We can't store 2^31 in 4 bytes
				if (binaryString.charAt(0) == '1') { // If the binary string represents negative
														// number
					// Subtracting 2^31 i.e 2*(2^30) out of it
					ans -= power;
					ans -= power;
				}
			} else {
				int power = (1 << (binaryString.length() - 1));
				if (binaryString.charAt(0) == '1') { // If the binary string represents negative
														// number
					ans -= power;
				}
			}

			return ans;
		}
	}

}
