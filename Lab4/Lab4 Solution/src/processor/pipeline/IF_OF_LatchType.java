package processor.pipeline;

public class IF_OF_LatchType {

	boolean OF_enable;
	int instruction, currentPC; // Current pc to get value of current Program Counter

	public IF_OF_LatchType() {
		OF_enable = false;
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
	public int getCurrentPC() { // Getter to get current pc
		return currentPC;
	}

	public void setCurrentPC(int currPC) { // Setter to set current pc
		currentPC = currPC;
	}

}
