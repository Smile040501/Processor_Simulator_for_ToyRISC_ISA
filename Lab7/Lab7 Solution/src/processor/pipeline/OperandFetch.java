package processor.pipeline;

import processor.Processor;

// Required imports
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Misc;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;

	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch,
			OF_EX_LatchType oF_EX_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

	public void performOF() {
		// System.out.println("OF Stage"); // TEST

		if (IF_OF_Latch.isOF_enable()) {

			// If EX Stage is not busy at all
			if (!OF_EX_Latch.isEXBusy() && !OF_EX_Latch.isEXMABusy()) {
				IF_OF_Latch.setOFBusy(false);

				if (IF_OF_Latch.getNop()) { // If the instruction is nop instruction
					// System.out.println("NOP Received"); // TEST

					// Passing null (nop) as instruction ahead in pipeline
					OF_EX_Latch.setInstruction(null);
					// NOP is also considered as a valid instruction
					OF_EX_Latch.setValidInst(true);
					IF_OF_Latch.setValidInst(false);

				} else {
					if (IF_OF_Latch.isValidInst()) {
						// System.out.println("Checking Conflict"); // TEST

						// Checking if current instruction is having some conflict with instructions
						// in EX and MA stages
						containingProcessor.getDataInterlockUnit().checkConflict();

						if (IF_OF_Latch.getStall()) { // If OF stage is set to stall
							// System.out.println("Conflict Detected"); // TEST

							OF_EX_Latch.setInstruction(null); // Passing null (nop) ahead in
																// pipeline
							OF_EX_Latch.setValidInst(true);

						} else {
							// System.out.println("Decoding Instruction"); // TEST

							decode(); // Decoding the instruction

							OF_EX_Latch.setValidInst(true);
							IF_OF_Latch.setValidInst(false);
						}
					} else {
						// System.out.println("Invalid Instruction"); // TEST
					}
				}
			} else {
				// System.out.println("EX Found Busy | Making OF Busy"); // TEST

				IF_OF_Latch.setOFBusy(true); // Setting OF stage as busy
			}

			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		} else {
			// System.out.println("OF Not Enabled"); // TEST
		}
	}

	// =================================================================
	// Function to decode the given instruction
	// Fetch register operands from register file
	// Computer the immediate and branchTarget
	// Generate control signals in form of an object of Instruction class
	private void decode() {
		// Getting the 32 bit binary string representation of given instruction
		String inst = padStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

		Instruction newIns = new Instruction(); // Making an object of Instruction class
		newIns.setProgramCounter(IF_OF_Latch.getCurrentPC()); // setting the current program counter

		// Setting operation type using opcode
		newIns.setOperationType(
				OperationType.values()[binaryToDecimal(inst.substring(0, 5), false)]);

		// Performing above mentioned operations based on the operation type of Instruction
		switch (newIns.getOperationType()) {
			// R3I type
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
			case sra: {
				// rs1
				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
				// rs2
				newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
				// rd
				newIns.setDestinationOperand(getRegisterOperand(inst.substring(15, 20)));

				// operand1
				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));
				// operand2
				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand2().getValue()));
				// isImmediate control signal
				OF_EX_Latch.setIsImmediate(false);
				break;
			}

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
			case srai:
			case load:
			case store: {
				// rs1
				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
				// rd
				newIns.setDestinationOperand(getRegisterOperand(inst.substring(10, 15)));
				// imm
				newIns.setSourceOperand2(getImmediateOperand(inst.substring(15, 32)));

				// operand1
				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));
				// operand2
				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getDestinationOperand().getValue()));
				// immediate
				OF_EX_Latch.setImmediate(newIns.getSourceOperand2().getValue());
				// isImmediate control signal
				OF_EX_Latch.setIsImmediate(true);
				break;
			}

			case beq:
			case bne:
			case blt:
			case bgt: {
				// rs1
				newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
				// rd
				newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
				// imm
				newIns.setDestinationOperand(getImmediateOperand(inst.substring(15, 32)));

				// operand1
				OF_EX_Latch.setOperand1(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand1().getValue()));
				// operand2
				OF_EX_Latch.setOperand2(containingProcessor.getRegisterFile()
						.getValue(newIns.getSourceOperand2().getValue()));
				// branchTarget
				OF_EX_Latch.setBranchTarget(
						IF_OF_Latch.getCurrentPC() + newIns.getDestinationOperand().getValue());
				break;
			}

			// RI type :
			case jmp: {
				if (binaryToDecimal(inst.substring(5, 10), false) != 0) { // if rd is used
					// rd
					newIns.setDestinationOperand(getRegisterOperand(inst.substring(5, 10)));
				} else { // else imm is used
					// imm
					newIns.setDestinationOperand(getImmediateOperand(inst.substring(10, 32)));
				}

				// branchTarget
				OF_EX_Latch.setBranchTarget(
						IF_OF_Latch.getCurrentPC() + newIns.getDestinationOperand().getValue());
				break;
			}

			case end:
				break;

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}

		// System.out.println("Instruction: " + newIns); // TEST

		// Passing instruction as control signals to pipeline
		OF_EX_Latch.setInstruction(newIns);
	}

	// Function to pad a given a string from starting by 0's and making it of given total length
	private String padStart(String str, int totalLength) {
		if (str.length() >= totalLength) { // If it is already greater or equal to total length
			return str;
		}
		int count = 0;
		String ans = "";
		while (count < totalLength - str.length()) { // Adding the required number of zeros
			ans += '0';
			++count;
		}
		ans += str; // Adding the given string
		return ans;
	}

	// Function to convert given binary string to decimal based on signed representation or not
	private int binaryToDecimal(String binaryString, boolean isSigned) {
		if (!isSigned) {
			return Integer.parseInt(binaryString, 2); // if unsigned
		} else {
			String copyString = '0' + binaryString.substring(1); // Considering only first n-1 bits
			int ans = Integer.parseInt(copyString, 2); // The integer corresponding to first n-1
														// bits

			// For length 32, we can't compute 2^31 in int data type
			if (binaryString.length() == 32) {
				if (binaryString.charAt(0) == '1') { // If the binary string represents negative
														// number

					int power = (1 << 30); // 2^30 // We can't store 2^31 in 4 bytes
					// Subtracting 2^31 i.e 2*(2^30) out of it
					ans -= power;
					ans -= power;
				}
			} else {
				if (binaryString.charAt(0) == '1') { // If the binary string represents negative
														// number
					int power = (1 << (binaryString.length() - 1));
					ans -= power;
				}
			}

			return ans;
		}
	}

	// Function to get the Register Operand whose value is decimal represented value of given binary
	// string
	private Operand getRegisterOperand(String val) {
		Operand operand = new Operand(); // Making a new operand
		operand.setOperandType(OperandType.Register); // setting operand type as Register
		operand.setValue(binaryToDecimal(val, false)); // setting its value
		return operand;
	}

	// Function to get the Immediate Operand whole value is decimal represented value of given
	// binary string
	private Operand getImmediateOperand(String val) {
		Operand operand = new Operand(); // Making a new operand
		operand.setOperandType(OperandType.Immediate); // setting operand type as Register
		operand.setValue(binaryToDecimal(val, true)); // setting its value
		return operand;
	}
}
