package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {

	boolean MA_enable;
	Instruction inst; // Instruction as control signals
	int aluResult, excess, op; // AluResult obtained, excess bits to store in x31 register, operand
								// value to store for a store operation

	public EX_MA_LatchType() {
		MA_enable = false;
		excess = 0;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

	// =================================
	// Getters and Setters for above values
	public Instruction getInstruction() {
		return inst;
	}

	public void setInstruction(Instruction newIns) {
		inst = newIns;
	}

	public int getAluResult() {
		return aluResult;
	}

	public void setAluResult(int result) {
		aluResult = result;
	}

	public int getExcess() {
		return excess;
	}

	public void setExcess(int exc) {
		excess = exc;
	}

	public int getOperand() {
		return op;
	}

	public void setOperand(int operand) {
		op = operand;
	}

}
