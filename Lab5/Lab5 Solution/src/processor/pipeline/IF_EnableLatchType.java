package processor.pipeline;

public class IF_EnableLatchType {

	boolean IF_enable;

	boolean isStall; // whether IF stage is stall or not

	public IF_EnableLatchType() {
		IF_enable = true;
	}

	public boolean isIF_enable() {
		return IF_enable;
	}

	public void setIF_enable(boolean iF_enable) {
		IF_enable = iF_enable;
	}

	// Getter and setter methods for isStall variable
	public void setStall(boolean isStall) {
		this.isStall = isStall;
	}

	public boolean getStall() {
		return this.isStall;
	}

}
