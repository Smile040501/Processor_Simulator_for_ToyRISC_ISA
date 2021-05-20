package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {

	boolean EX_enable;
	Instruction inst; // Instruction representing control signals
	// op1, op2, imm, branchTarget values
	int operand1, operand2, immediate, branchTarget;
	// control signal, if value is immediate or not
	boolean isImmediate;

	// isEXBusy -> if EX stage is busy due to its own event
	// isEXMABusy -> if EX stage is set to busy because MA was busy
	// isValidInst -> whether instruction is valid or not
	boolean isEXBusy, isEXMABusy, isValidInst;

	public OF_EX_LatchType() {
		EX_enable = false;
		inst = null;
		operand1 = operand2 = immediate = branchTarget = 0;
		isImmediate = false;
		isEXBusy = isEXMABusy = isValidInst = false;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	// =================================================================
	// Getters and Setters for above values
	public Instruction getInstruction() {
		return inst;
	}

	public void setInstruction(Instruction newInst) {
		inst = newInst;
	}

	public int getOperand1() {
		return operand1;
	}

	public void setOperand1(int op1) {
		operand1 = op1;
	}

	public int getOperand2() {
		return operand2;
	}

	public void setOperand2(int op2) {
		operand2 = op2;
	}

	public int getImmediate() {
		return immediate;
	}

	public void setImmediate(int imm) {
		immediate = imm;
	}

	public int getBranchTarget() {
		return branchTarget;
	}

	public void setBranchTarget(int target) {
		branchTarget = target;
	}

	public boolean getIsImmediate() {
		return isImmediate;
	}

	public void setIsImmediate(boolean isImm) {
		isImmediate = isImm;
	}

	public void setEXBusy(boolean isEXBusy) {
		this.isEXBusy = isEXBusy;
	}

	public boolean isEXBusy() {
		return isEXBusy;
	}

	public void setEXMABusy(boolean isEXMABusy) {
		this.isEXMABusy = isEXMABusy;
	}

	public boolean isEXMABusy() {
		return isEXMABusy;
	}

	public boolean isValidInst() {
		return isValidInst;
	}

	public void setValidInst(boolean isValidInst) {
		this.isValidInst = isValidInst;
	}

}
