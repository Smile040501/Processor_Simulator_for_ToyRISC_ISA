package processor.interlocks;

import processor.pipeline.IF_OF_LatchType;
import generic.Simulator;
import processor.pipeline.EX_IF_LatchType;

// Control-Interlock Class to implement the functionality of Control-Interlock
public class ControlInterlock {

    // Required Latches
    IF_OF_LatchType IF_OF_Latch;
    EX_IF_LatchType EX_IF_Latch;

    // Constructor
    public ControlInterlock(IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch) {
        this.IF_OF_Latch = iF_OF_Latch;
        this.EX_IF_Latch = eX_IF_Latch;
    }

    // If the branch instruction in the EX stage is taken, then invalidate the instructions in the
    // OF stage, Else Don't take any special action
    public void validate() {
        if (EX_IF_Latch.getIsBranchTaken()) {
            IF_OF_Latch.setNop(true); // Setting Nop to True indicating an invalid instruction

            // System.out.println("Control Hazard Occurred!!"); // TEST
            Simulator.incNop(); // Incrementing number of nop instructions
        }
    }

}
