package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {

	boolean EX_enable;
	Instruction inst; // Instruction representing control signals
	// op1, op2, imm, branchTarget values
	int operand1 = 0, operand2 = 0, immediate = 0, branchTarget;
	// control signal, if value is immediate or not
	boolean isImmediate = false;

	public OF_EX_LatchType() {
		EX_enable = false;
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

}
