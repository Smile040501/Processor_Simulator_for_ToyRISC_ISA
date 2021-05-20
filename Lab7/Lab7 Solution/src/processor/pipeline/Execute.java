package processor.pipeline;

import processor.Clock;
import processor.Processor;
import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;
// Required imports
import generic.Instruction;
import generic.Simulator;
import generic.Misc;

// Implementing Element interface for Execute class
public class Execute implements Element {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;

	// Variables need to store to pass to event for processing
	int aluResult, excess, op;
	boolean isBranchTaken;
	int branchPC;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch,
			EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}

	public void performEX() {
		// System.out.println("EX Stage"); // TEST

		if (OF_EX_Latch.isEX_enable()) {

			if (!OF_EX_Latch.isEXBusy()) { // If EX stage is not busy
				if (!EX_MA_Latch.isMABusy()) { // If MA stage is not busy
					OF_EX_Latch.setEXMABusy(false);

					if (OF_EX_Latch.isValidInst()) { // If instruction is valid
						Instruction inst = OF_EX_Latch.getInstruction(); // Getting the instruction

						if (inst != null) { // If instruction is not a null (or nop) instruction
							// System.out.println("Computing Results"); // TEST

							compute(inst); // executing the instruction
							scheduleEvent(inst); // Scheduling the event
							OF_EX_Latch.setValidInst(false);

						} else {
							// System.out.println("NOP Received"); // TEST

							EX_MA_Latch.setInstruction(inst); // Passing instruction as control
																// signals to pipeline
							EX_MA_Latch.setValidInst(true);

							EX_IF_Latch.setIsBranchTaken(false);

							OF_EX_Latch.setValidInst(false);
						}
					} else {
						// System.out.println("Invalid Instruction."); // TEST
					}
				} else {
					// System.out.println("MA Found Busy | Making EX Busy"); // TEST

					OF_EX_Latch.setEXMABusy(true); // Setting EX busy due to MA stage
				}
			} else {
				// System.out.println("EX Busy"); // TEST
			}

			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);
		} else {
			// System.out.println("EX Not Enabled"); // TEST
		}
	}

	// Function that acts like ALU and computes the aluResult and set excess value to x31
	// Set the flags for branching
	private void compute(Instruction inst) {

		// System.out.println("Instruction: " + inst); // TEST

		long op1 = OF_EX_Latch.getOperand1(); // operand1
		long op2 = OF_EX_Latch.getOperand2(); // operand2
		long imm = OF_EX_Latch.getImmediate(); // immediate
		long second = (OF_EX_Latch.getIsImmediate()) ? imm : op2; // second operand for ALU
																	// operations

		// Performing operation based on operation time
		switch (inst.getOperationType()) {
			case add:
			case addi: {
				// EX_MA_Latch.setAluResult(getResult(op1 + second)); // Adder
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 + second);
				this.isBranchTaken = false;
				break;
			}

			case sub:
			case subi: {
				// EX_MA_Latch.setAluResult(getResult(op1 - second)); // Subtraction
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 - second);
				this.isBranchTaken = false;
				break;
			}

			case mul:
			case muli: {
				// EX_MA_Latch.setAluResult(getResult(op1 * second)); // Multiplier
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 * second);
				this.isBranchTaken = false;
				break;
			}

			case div:
			case divi: {
				// EX_MA_Latch.setAluResult(getResult(op1 / second)); // Division
				// EX_MA_Latch.setExcess((int) (op1 % second)); // setting remainder as excess value
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 / second);
				this.excess = (int) (op1 % second);
				this.isBranchTaken = false;
				break;
			}

			case and:
			case andi: {
				// EX_MA_Latch.setAluResult(getResult(op1 & second)); // AND operation
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 & second);
				this.isBranchTaken = false;
				break;
			}

			case or:
			case ori: {
				// EX_MA_Latch.setAluResult(getResult(op1 | second)); // OR operation
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 | second);
				this.isBranchTaken = false;
				break;
			}

			case xor:
			case xori: {
				// EX_MA_Latch.setAluResult(getResult(op1 ^ second)); // XOR operation
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 ^ second);
				this.isBranchTaken = false;
				break;
			}

			case slt:
			case slti: {
				// EX_MA_Latch.setAluResult((op1 < second) ? 1 : 0);
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = (op1 < second) ? 1 : 0;
				this.isBranchTaken = false;
				break;
			}

			case sll:
			case slli: {
				// EX_MA_Latch.setAluResult(getResult(op1 << second)); // Left shift
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 << second);
				this.isBranchTaken = false;
				break;
			}

			case srl:
			case srli: {
				// EX_MA_Latch.setAluResult(getResult(op1 >>> second)); // Logical right shift
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 >>> second);
				this.isBranchTaken = false;
				break;
			}

			case sra:
			case srai: {
				// EX_MA_Latch.setAluResult(getResult(op1 >> second)); // Arithmetic right shift
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 >> second);
				this.isBranchTaken = false;
				break;
			}

			case load: {
				// EX_MA_Latch.setAluResult(getResult(op1 + imm)); // Address of load operation
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op1 + imm);
				this.isBranchTaken = false;
				break;
			}

			case store: {
				// EX_MA_Latch.setAluResult(getResult(op2 + imm)); // Address of store operation
				// EX_MA_Latch.setOperand((int) op1); // Setting operand to store at store address
				// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.aluResult = getResult(op2 + imm);
				this.op = (int) op1;
				this.isBranchTaken = false;
				break;
			}

			case beq: {
				if (op1 == op2) { // if true
					// EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					// EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {
					// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
					this.isBranchTaken = false;
				}
				break;
			}

			case bne: {
				if (op1 != op2) { // if true
					// EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					// EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {
					// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
					this.isBranchTaken = false;
				}
				break;
			}

			case blt: {
				if (op1 < op2) { // if true
					// EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					// EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {
					// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
					this.isBranchTaken = false;
				}
				break;
			}

			case bgt: {
				if (op1 > op2) { // if true
					// EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
					// Setting branchPC
					// EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget());
					this.isBranchTaken = true;
					this.branchPC = OF_EX_Latch.getBranchTarget();
				} else {
					// EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
					this.isBranchTaken = false;
				}
				break;
			}

			case jmp: {
				EX_IF_Latch.setIsBranchTaken(true); // Setting isBranchTaken signal as true
				EX_IF_Latch.setBranchPC(OF_EX_Latch.getBranchTarget()); // Setting branchPC
				this.isBranchTaken = true;
				this.branchPC = OF_EX_Latch.getBranchTarget();
				break;
			}


			case end: {
				EX_IF_Latch.setIsBranchTaken(false); // Setting isBranchTaken signal as false
				this.isBranchTaken = false;
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
			// EX_MA_Latch.setExcess(binaryToDecimal( // Setting excess
			// binaryString.substring(0, binaryString.length() - 32), (res < 0)));
			this.excess = binaryToDecimal( // Setting excess
					binaryString.substring(0, binaryString.length() - 32), (res < 0));

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

	// Function to schedule the ExecutionCompleteEvent with corresponding latency
	private void scheduleEvent(Instruction inst) {

		switch (inst.getOperationType()) {
			case mul:
			case muli: {
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.multiplier_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXBusy(true);
				break;
			}
			case div:
			case divi: {
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.divider_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXBusy(true);
				break;
			}

			case add:
			case sub:
			case and:
			case or:
			case xor:
			case slt:
			case sll:
			case srl:
			case sra:
			case addi:
			case subi:
			case andi:
			case ori:
			case xori:
			case slti:
			case slli:
			case srli:
			case srai:
			case load:
			case store:
			case beq:
			case bne:
			case blt:
			case bgt: {
				// System.out.println("Scheduling Event...\n"); // TEST

				Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
						Clock.getCurrentTime() + Configuration.ALU_latency, this, this, inst,
						this.aluResult, this.excess, this.op, this.isBranchTaken, this.branchPC));
				OF_EX_Latch.setEXBusy(true);
				break;
			}

			case jmp:
			case end: {
				// Performing Control-Interlock validation for branch instructions
				containingProcessor.getControlInterlockUnit().validate();

				OF_EX_Latch.setEXBusy(false);
				OF_EX_Latch.setValidInst(false);
				OF_EX_Latch.setEX_enable(false);

				EX_MA_Latch.setValidInst(true);
				EX_MA_Latch.setInstruction(inst);
				EX_MA_Latch.setMA_enable(true);
				break;
			}

			default:
				Misc.printErrorAndExit("Unknown Instruction!!");
		}
	}

	// Handle event function to handle ExecutionCompleteEvents
	@Override
	public void handleEvent(Event e) {
		// System.out.println("EX Event Detected: " + e); // TEST

		if (EX_MA_Latch.isMABusy()) { // If MA stage is busy
			// System.out.println("Event postponed | MA Busy"); // TEST

			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		} else {
			ExecutionCompleteEvent event = (ExecutionCompleteEvent) e;

			// System.out.println("Event Triggered in EX: \n" + event); // TEST

			OF_EX_Latch.setEXBusy(false);
			OF_EX_Latch.setValidInst(false);
			OF_EX_Latch.setEX_enable(false);

			EX_IF_Latch.setIsBranchTaken(event.isBranchTaken());
			EX_IF_Latch.setBranchPC(event.getBranchPC());

			EX_MA_Latch.setMA_enable(true);
			EX_MA_Latch.setValidInst(true);
			EX_MA_Latch.setInstruction(event.getInst());
			EX_MA_Latch.setAluResult(event.getAluResult());
			EX_MA_Latch.setExcess(event.getExcess());
			EX_MA_Latch.setOperand(event.getOp());

			// Performing Control-Interlock validation for branch instructions
			containingProcessor.getControlInterlockUnit().validate();
		}
	}

}
