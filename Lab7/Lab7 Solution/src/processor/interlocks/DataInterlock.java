package processor.interlocks;

import processor.pipeline.IF_EnableLatchType;
import processor.pipeline.IF_OF_LatchType;
import processor.pipeline.EX_MA_LatchType;
import processor.pipeline.MA_RW_LatchType;
import processor.Processor;

import generic.Instruction;
import generic.Simulator;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Misc;

// DataInterlock class to implement the functionality of Data-Interlock
public class DataInterlock {

    // Required variables
    Processor containingProcessor;
    IF_EnableLatchType IF_EnableLatch;
    IF_OF_LatchType IF_OF_Latch;
    EX_MA_LatchType EX_MA_Latch;
    MA_RW_LatchType MA_RW_Latch;

    // Constructor
    public DataInterlock(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
            IF_OF_LatchType iF_OF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch) {
        this.containingProcessor = containingProcessor;
        this.IF_EnableLatch = iF_EnableLatch;
        this.IF_OF_Latch = iF_OF_Latch;
        this.EX_MA_Latch = eX_MA_Latch;
        this.MA_RW_Latch = mA_RW_Latch;
    }

    // When an instruction reaches the OF stage, check if it has a conflict with any of the
    // instructions in the EX and MA
    // If there is no conflict nothing needs to be done, else stall the pipeline (IF & OF stages)
    public void checkConflict() {

        Instruction currInst = getInstruction(); // Current Instruction in OF stage
        Instruction eXInst = EX_MA_Latch.getInstruction(); // Instruction in EX stage
        Instruction mAInst = MA_RW_Latch.getInstruction(); // Instruction in MA stage

        // If there is a conflict set IF and OF stages on stall
        if ((EX_MA_Latch.isValidInst() && eXInst != null && hasConflict(currInst, eXInst))
                || (MA_RW_Latch.isValidInst() && mAInst != null && hasConflict(currInst, mAInst))) {

            // System.out.println("Data Hazard Occurred!!"); // TEST
            // System.out.println(currInst); // TEST
            // System.out.println(eXInst); // TEST
            // System.out.println(mAInst); // TEST
            // System.out.println(EX_MA_Latch.isValidInst()); // TEST
            // System.out.println(MA_RW_Latch.isValidInst()); // TEST

            IF_EnableLatch.setStall(true);
            IF_OF_Latch.setStall(true);

            Simulator.incNumDataHazards(); // Incrementing number of data hazards occurred

        } else { // Un-stall IF and OF stages if there is no conflict

            // System.out.println("No Data Hazard Occurred!!"); // TEST

            // Un-stall IF and OF stages as there is no conflict
            IF_EnableLatch.setStall(false);
            IF_OF_Latch.setStall(false);
        }
    }

    // Function to check if there is any conflict between two instructions
    // Basically checking for RAW (Read After Write) Hazard
    // A = Current Instruction
    private boolean hasConflict(Instruction A, Instruction B) {
        // if any of the instruction is null (or nop), there is no conflict
        if (A == null || B == null) {
            return false;
        }

        // If current instruction is either jmp or end, there is no conflict
        switch (A.getOperationType()) {
            case jmp:
            case end:
                return false;
            default:
        }

        int rs1A = A.getSourceOperand1().getValue(); // rs1 of A
        int rs2A = A.getSourceOperand2().getValue(); // rs2 of A
        int rdA = A.getDestinationOperand().getValue(); // rd of A
        boolean isSecondImm = // whether rs2 of A is immediate or not
                (A.getSourceOperand2().getOperandType() == OperandType.valueOf("Immediate"));
        int second = rs2A; // second operand

        if (A.getOperationType() == OperationType.valueOf("store")) {
            second = rdA; // For store it will be rd of A
            isSecondImm = false; // It is not immediate
        }

        // If any one value is 31, we need to wait for all the instructions to go till RW stage
        // For excess bits or case of shift and division operations
        // Hence marking it as a conflict
        if (rs1A == 31 || second == 31) {
            return true;
        }

        // If B is a jmp or end instruction, there is no conflict
        switch (B.getOperationType()) {
            case jmp:
            case end:
                return false;
            default:
        }

        int rs2B = B.getSourceOperand2().getValue(); // rs2 of B
        int rdB = B.getDestinationOperand().getValue(); // rd of B

        // If B is a branch instruction there is no conflict
        switch (B.getOperationType()) {
            case beq:
            case bne:
            case blt:
            case bgt:
                return false;
            default:
        }

        // If there is a load after store operation, checking whether the previous address and
        // current address are same or not, in case they are same there is a conflict
        if (A.getOperationType() == OperationType.valueOf("load")
                && B.getOperationType() == OperationType.valueOf("store")) {
            int addr1 = containingProcessor.getRegisterFile().getValue(rs1A) + rs2A;
            int addr2 = containingProcessor.getRegisterFile().getValue(rdB) + rs2B;
            if (addr1 == addr2) {
                return true;
            }
        }

        // Passing all above checks if B is a store operation, there is no conflict
        if (B.getOperationType() == OperationType.valueOf("store")) {
            return false;
        }

        // If Following condition holds true, then there is a conflict
        if (rs1A == rdB || (!isSecondImm && second == rdB)) {
            return true;
        }

        return false;
    }

    // Function to decode the given instruction
    // Fetch register operands from register file
    // Computer the immediate and branchTarget
    // Generate control signals in form of an object of Instruction class
    private Instruction getInstruction() {
        // Getting the 32 bit binary string representation of given instruction
        String inst = padStart(Integer.toBinaryString(IF_OF_Latch.getInstruction()), 32);

        Instruction newIns = new Instruction(); // Making an object of Instruction class

        // Setting operation type using opcode
        newIns.setOperationType(
                OperationType.values()[binaryToDecimal(inst.substring(0, 5), false)]);

        // Performing above mentioned operations based on the operation type of Instruction
        switch (newIns.getOperationType()) {
            // R3I type
            case add:
            case sub:
            case mul:
            case div:
            case and:
            case or:
            case xor:
            case slt:
            case sll:
            case srl:
            case sra: {
                // rs1
                newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rs2
                newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
                // rd
                newIns.setDestinationOperand(getRegisterOperand(inst.substring(15, 20)));
                break;
            }

            // R2I type
            case addi:
            case subi:
            case muli:
            case divi:
            case andi:
            case ori:
            case xori:
            case slti:
            case slli:
            case srli:
            case srai:
            case load:
            case store: {
                // rs1
                newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rd
                newIns.setDestinationOperand(getRegisterOperand(inst.substring(10, 15)));
                // imm
                newIns.setSourceOperand2(getImmediateOperand(inst.substring(15, 32)));
                break;
            }

            case beq:
            case bne:
            case blt:
            case bgt: {
                // rs1
                newIns.setSourceOperand1(getRegisterOperand(inst.substring(5, 10)));
                // rd
                newIns.setSourceOperand2(getRegisterOperand(inst.substring(10, 15)));
                // imm
                newIns.setDestinationOperand(getImmediateOperand(inst.substring(15, 32)));
                break;
            }

            // RI type :
            case jmp: {
                if (binaryToDecimal(inst.substring(5, 10), false) != 0) { // if rd is used
                    // rd
                    newIns.setDestinationOperand(getRegisterOperand(inst.substring(5, 10)));
                } else { // else imm is used
                    // imm
                    newIns.setDestinationOperand(getImmediateOperand(inst.substring(10, 32)));
                }
                break;
            }

            case end:
                break;

            default:
                Misc.printErrorAndExit("Unknown Instruction!!");
        }

        return newIns;
    }

    // Function to pad a given a string from starting by 0's and making it of given total length
    private String padStart(String str, int totalLength) {
        if (str.length() >= totalLength) { // If it is already greater or equal to total length
            return str;
        }
        int count = 0;
        String ans = "";
        while (count < totalLength - str.length()) { // Adding the required number of zeros
            ans += '0';
            ++count;
        }
        ans += str; // Adding the given string
        return ans;
    }

    // Function to convert given binary string to decimal based on signed representation or not
    private int binaryToDecimal(String binaryString, boolean isSigned) {
        if (!isSigned) {
            return Integer.parseInt(binaryString, 2); // if unsigned
        } else {
            String copyString = '0' + binaryString.substring(1); // Considering only first n-1 bits
            int ans = Integer.parseInt(copyString, 2); // The integer corresponding to first n-1
                                                       // bits

            // For length 32, we can't compute 2^31 in int data type
            if (binaryString.length() == 32) {
                if (binaryString.charAt(0) == '1') { // If the binary string represents negative
                                                     // number

                    int power = (1 << 30); // 2^30 // We can't store 2^31 in 4 bytes
                    // Subtracting 2^31 i.e 2*(2^30) out of it
                    ans -= power;
                    ans -= power;
                }
            } else {
                if (binaryString.charAt(0) == '1') { // If the binary string represents negative
                                                     // number
                    int power = (1 << (binaryString.length() - 1));
                    ans -= power;
                }
            }

            return ans;
        }
    }

    // Function to get the Register Operand whose value is decimal represented value of given binary
    // string
    private Operand getRegisterOperand(String val) {
        Operand operand = new Operand(); // Making a new operand
        operand.setOperandType(OperandType.Register); // setting operand type as Register
        operand.setValue(binaryToDecimal(val, false)); // setting its value
        return operand;
    }

    // Function to get the Immediate Operand whole value is decimal represented value of given
    // binary string
    private Operand getImmediateOperand(String val) {
        Operand operand = new Operand(); // Making a new operand
        operand.setOperandType(OperandType.Immediate); // setting operand type as Register
        operand.setValue(binaryToDecimal(val, true)); // setting its value
        return operand;
    }

}
