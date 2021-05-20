package processor;

import processor.memorysystem.Cache;
import processor.memorysystem.MainMemory;
import processor.pipeline.EX_IF_LatchType;
import processor.pipeline.EX_MA_LatchType;
import processor.pipeline.Execute;
import processor.pipeline.IF_EnableLatchType;
import processor.pipeline.IF_OF_LatchType;
import processor.pipeline.InstructionFetch;
import processor.pipeline.MA_RW_LatchType;
import processor.pipeline.MemoryAccess;
import processor.pipeline.OF_EX_LatchType;
import processor.pipeline.OperandFetch;
import processor.pipeline.RegisterFile;
import processor.pipeline.RegisterWrite;

// Importing the Interlocks
import processor.interlocks.DataInterlock;
import processor.interlocks.ControlInterlock;

public class Processor {

	RegisterFile registerFile;
	MainMemory mainMemory;

	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	MA_RW_LatchType MA_RW_Latch;

	InstructionFetch IFUnit;
	OperandFetch OFUnit;
	Execute EXUnit;
	MemoryAccess MAUnit;
	RegisterWrite RWUnit;

	DataInterlock DataInterlockUnit; // The Data-Interlock unit of the processor
	ControlInterlock ControlInterlockUnit; // The Control-Interlock unit of the processor

	Cache l1iCache; // L1i Cache
	Cache l1dCache; // L1d Cache

	public Processor() {
		registerFile = new RegisterFile();
		mainMemory = new MainMemory();

		IF_EnableLatch = new IF_EnableLatchType();
		IF_OF_Latch = new IF_OF_LatchType();
		OF_EX_Latch = new OF_EX_LatchType();
		EX_MA_Latch = new EX_MA_LatchType();
		EX_IF_Latch = new EX_IF_LatchType();
		MA_RW_Latch = new MA_RW_LatchType();

		IFUnit = new InstructionFetch(this, IF_EnableLatch, IF_OF_Latch, EX_IF_Latch);
		OFUnit = new OperandFetch(this, IF_OF_Latch, OF_EX_Latch);
		EXUnit = new Execute(this, OF_EX_Latch, EX_MA_Latch, EX_IF_Latch);
		MAUnit = new MemoryAccess(this, EX_MA_Latch, MA_RW_Latch);
		RWUnit = new RegisterWrite(this, MA_RW_Latch, IF_EnableLatch);

		// Initializing Data-Interlock unit and Control-Interlock unit
		DataInterlockUnit =
				new DataInterlock(this, IF_EnableLatch, IF_OF_Latch, EX_MA_Latch, MA_RW_Latch);
		ControlInterlockUnit = new ControlInterlock(IF_OF_Latch, EX_IF_Latch);

		l1iCache = new Cache(this, 0);
		l1dCache = new Cache(this, 1);
	}

	public void printState(int memoryStartingAddress, int memoryEndingAddress) {
		System.out.println(registerFile.getContentsAsString());

		System.out.println(
				mainMemory.getContentsAsString(memoryStartingAddress, memoryEndingAddress));
	}

	public RegisterFile getRegisterFile() {
		return registerFile;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

	public MainMemory getMainMemory() {
		return mainMemory;
	}

	public void setMainMemory(MainMemory mainMemory) {
		this.mainMemory = mainMemory;
	}

	public InstructionFetch getIFUnit() {
		return IFUnit;
	}

	public OperandFetch getOFUnit() {
		return OFUnit;
	}

	public Execute getEXUnit() {
		return EXUnit;
	}

	public MemoryAccess getMAUnit() {
		return MAUnit;
	}

	public RegisterWrite getRWUnit() {
		return RWUnit;
	}

	// Getter and Setter Methods for Interlock units defined above
	public DataInterlock getDataInterlockUnit() {
		return DataInterlockUnit;
	}

	public void setDataInterlockUnit(DataInterlock dataInterlockUnit) {
		DataInterlockUnit = dataInterlockUnit;
	}

	public ControlInterlock getControlInterlockUnit() {
		return ControlInterlockUnit;
	}

	public void setControlInterlockUnit(ControlInterlock controlInterlockUnit) {
		ControlInterlockUnit = controlInterlockUnit;
	}

	public Cache getL1iCache() {
		return l1iCache;
	}

	public void setL1iCache(Cache l1iCache) {
		this.l1iCache = l1iCache;
	}

	public Cache getL1dCache() {
		return l1dCache;
	}

	public void setL1dCache(Cache l1dCache) {
		this.l1dCache = l1dCache;
	}

}
