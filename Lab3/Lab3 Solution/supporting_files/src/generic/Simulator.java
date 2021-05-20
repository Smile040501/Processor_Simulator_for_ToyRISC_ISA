package generic;

import java.io.FileInputStream;
import generic.Operand.OperandType;

// Importing Classes for Input/Output
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import generic.Instruction.OperationType;

public class Simulator {

	static FileInputStream inputcodeStream = null;

	private static String destinationFile; // To store the output file path

	public static void setupSimulation(String assemblyProgramFile, String objectProgramFile) {
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();

		destinationFile = objectProgramFile; // Storing output file path
	}

	public static void assemble() {

		FileOutputStream outputStream = null; // FileOutputStream object
		DataOutputStream dos = null; // DataOutputStream object to write integers to binary file
		// Trying to open output file
		try {
			outputStream = new FileOutputStream(destinationFile);
			dos = new DataOutputStream(outputStream);
			try {
				// Getting the main function address
				int mainFunctionAddress = ParsedProgram.symtab.get("main");
				// Writing HEADER
				dos.writeInt(mainFunctionAddress);

				// Writing DATA
				for (Integer i : ParsedProgram.data) {
					dos.writeInt(i);
				}

				// Writing TEXT
				// Looping all the instructions
				for (int pc = mainFunctionAddress; pc < mainFunctionAddress
						+ ParsedProgram.code.size(); ++pc) {
					// The current instruction
					Instruction currIns = ParsedProgram.getInstructionAt(pc);
					// Integer corresponding to the instruction
					int assembledInstruction = binaryToDecimal(getInstructionString(currIns), true);
					dos.writeInt(assembledInstruction);
				}

				dos.flush();
				dos.close();
			} catch (IOException e) { // Handling Errors if they occur
				Misc.printErrorAndExit(e.toString());
			}
		} catch (FileNotFoundException e) { // Handling Errors if they occur
			Misc.printErrorAndExit(e.toString());
		}
	}



	// Function to get the binary encoded string representation of the given instruction
	private static String getInstructionString(Instruction i) {

		String ans = ""; // The final answer to return

		// Adding 5 bit opcode to the string corresponding to the operation of the instruction
		ans += padStart(Integer.toBinaryString(
				OperationType.valueOf(i.getOperationType().name()).ordinal()), 5, false);

		// Adding remaining bits to the string
		switch (i.getOperationType()) {
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
				// 5 bits for rs1
				ans += padStart(Integer.toBinaryString(i.getSourceOperand1().getValue()), 5, false);
				// 5 bits for rs2
				ans += padStart(Integer.toBinaryString(i.getSourceOperand2().getValue()), 5, false);
				// 5 bits for rd
				ans += padStart(Integer.toBinaryString(i.getDestinationOperand().getValue()), 5,
						false);

				ans = padEnd(ans, 32); // Padding it at end to add remaining 12 unused bits
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
				// 5 bits for rs1
				ans += padStart(Integer.toBinaryString(i.getSourceOperand1().getValue()), 5, false);
				// 5 bits for rd
				ans += padStart(Integer.toBinaryString(i.getDestinationOperand().getValue()), 5,
						false);

				String immStr = ""; // The immediate string
				int immVal = 0;
				if (i.getSourceOperand2().getOperandType() == OperandType.valueOf("Immediate")) {
					// if it is an immediate type
					immVal = i.getSourceOperand2().getValue();
					immStr = Integer.toBinaryString(immVal);
				} else {
					// if it is a Label type
					String label = i.getSourceOperand2().getLabelValue(); // Label value of operand
					// Storing the address of the label
					immVal = ParsedProgram.symtab.get(label);
					immStr = Integer.toBinaryString(immVal);
				}
				immStr = padStart(immStr, 17, immVal < 0); // Making it a total length of 17 bits

				ans += immStr;
				break;
			}

			case beq:
			case bne:
			case blt:
			case bgt: {
				// 5 bits for rs1
				ans += padStart(Integer.toBinaryString(i.getSourceOperand1().getValue()), 5, false);
				// 5 bits for rd
				ans += padStart(Integer.toBinaryString(i.getSourceOperand2().getValue()), 5, false);

				int branchTarget = 0;
				String immStr = ""; // The immediate string
				if (i.getDestinationOperand().getOperandType() == OperandType
						.valueOf("Immediate")) {
					// if it is an immediate type
					branchTarget = i.getDestinationOperand().getValue();
				} else {
					// if it is a Label type
					String label = i.getDestinationOperand().getLabelValue(); // Label value of
																				// operand
					// Storing the address of the label
					branchTarget = ParsedProgram.symtab.get(label);
				}
				int pc = i.getProgramCounter();
				immStr = Integer.toBinaryString(branchTarget - pc);
				if (immStr.length() > 17) {
					immStr = immStr.substring(immStr.length() - 17);
				}
				immStr = padStart(immStr, 17, (branchTarget - pc) < 0); // Making it a total length
																		// of 17 bits

				ans += immStr;
				break;
			}

			// RI type :
			case jmp: {
				ans = padEnd(ans, 10); // Assuming rd to be unused, adding 5bits of it at end

				String immStr = ""; // The immediate string
				int branchTarget = 0;
				if (i.getDestinationOperand().getOperandType() == OperandType.valueOf("Immediate")
						|| i.getDestinationOperand().getOperandType() == OperandType
								.valueOf("Register")) {
					// if it is an immediate or register type
					branchTarget = i.getDestinationOperand().getValue();
					immStr = Integer.toBinaryString(i.getDestinationOperand().getValue());
				} else {
					String label = i.getDestinationOperand().getLabelValue(); // Label value of
																				// operand
					// Storing the address of the label
					branchTarget = ParsedProgram.symtab.get(label);
				}
				int pc = i.getProgramCounter();
				immStr = Integer.toBinaryString(branchTarget - pc);
				if (immStr.length() > 22) {
					immStr = immStr.substring(immStr.length() - 22);
				}
				immStr = padStart(immStr, 22, (branchTarget - pc) < 0); // Making it a total length
																		// of 22 bits

				ans += immStr;
				break;
			}

			case end:
				ans = padEnd(ans, 32); // Both rd and imm are unused
				break;

			default:
				Misc.printErrorAndExit("unknown instruction!!");
		}

		return ans; // Returning final answer
	}

	// Function to pad a given a string from starting by 0's and making it of given total length
	private static String padStart(String str, int totalLength, boolean isSigned) {
		if (str.length() >= totalLength) { // If it is already greater or equal to total length
			return str;
		}
		int count = 0;
		String ans = "";
		char ch = (isSigned) ? '1' : '0';
		while (count < totalLength - str.length()) { // Adding the required number of bits
			ans += ch;
			++count;
		}
		ans += str; // Adding the given string
		return ans;
	}

	// Function to pad a given a string from end by 0's and making it of given total length
	private static String padEnd(String str, int totalLength) {
		if (str.length() >= totalLength) { // If it is already greater or equal to total length
			return str;
		}
		int count = 0;
		String ans = str; // Adding the given string
		while (count < totalLength - str.length()) { // Adding the required number of zeros
			ans += '0';
			++count;
		}
		return ans;
	}

	private static int binaryToDecimal(String binaryString, boolean isSigned) {

		if (!isSigned) {
			return Integer.parseInt(binaryString, 2);
		} else {
			String copyString = '0' + binaryString.substring(1); // Considering only first n-1 bits
			int ans = Integer.parseInt(copyString, 2); // The integer corresponding to first n-1
														// bits

			if (binaryString.length() == 32) {
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
