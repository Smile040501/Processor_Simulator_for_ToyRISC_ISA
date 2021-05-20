package processor.pipeline;

public class IF_OF_LatchType {

	boolean OF_enable;
	int instruction, currentPC; // Current pc to get value of current Program Counter
	boolean isNop, isStall; // Whether instruction is nop or not, whether OF stage is stall or not

	public IF_OF_LatchType() {
		OF_enable = false;
		isNop = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	// ====================
	// Getters and setter methods for above defined variables
	public int getCurrentPC() {
		return currentPC;
	}

	public void setCurrentPC(int currPC) {
		currentPC = currPC;
	}

	public void setNop(boolean isNop) {
		this.isNop = isNop;
	}

	public boolean getNop() {
		return this.isNop;
	}

	public void setStall(boolean isStall) {
		this.isStall = isStall;
	}

	public boolean getStall() {
		return this.isStall;
	}

}
