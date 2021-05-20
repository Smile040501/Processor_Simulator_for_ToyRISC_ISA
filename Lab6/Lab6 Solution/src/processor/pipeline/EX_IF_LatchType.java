package processor.pipeline;

public class EX_IF_LatchType {

	boolean isBranchTaken; // whether branch is taken or not
	int branchPC; // The branchPC taken


	public EX_IF_LatchType() {
		isBranchTaken = false;
		branchPC = 0;
	}

	// ==============================
	// Getters and Setters for above mentioned values
	public boolean getIsBranchTaken() {
		return isBranchTaken;
	}

	public void setIsBranchTaken(boolean isBranchTaken) {
		this.isBranchTaken = isBranchTaken;
	}

	public int getBranchPC() {
		return branchPC;
	}

	public void setBranchPC(int branchPC) {
		this.branchPC = branchPC;
	}

}
