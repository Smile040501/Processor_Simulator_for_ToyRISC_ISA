package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {

	boolean MA_enable;
	Instruction inst; // Instruction as control signals
	int aluResult, excess, op; // AluResult obtained, excess bits to store in x31 register, operand
								// value to store for a store operation

	// whether MA Stage is busy or not, whether MA stage has valid instruction or not
	boolean isMABusy, isValidInst;

	public EX_MA_LatchType() {
		MA_enable = false;
		inst = null;
		aluResult = op = excess = 0;
		isMABusy = isValidInst = false;
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

	public boolean isMABusy() {
		return isMABusy;
	}

	public void setMABusy(boolean isMABusy) {
		this.isMABusy = isMABusy;
	}

	public void setValidInst(boolean isValidInst) {
		this.isValidInst = isValidInst;
	}

	public boolean isValidInst() {
		return isValidInst;
	}

}
