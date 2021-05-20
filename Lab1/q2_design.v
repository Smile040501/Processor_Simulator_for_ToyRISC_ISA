// Mayank Singla

// A 1-bit Full Adder that add two bits and give their sum and carry
module fullAdder(a, b, cin, sum, cout);
  input a, b, cin;	// bits of a, b, and cin (carry_In)
  output sum, cout;	// bits of sum and cout (carry_Out)
  
  reg sum, cout;	// Making them of reg type
  
  always @ (a or b or cin) begin	// Always when either a or b or cin changes
    sum = a ^ b ^ cin;		// Calculating sum as per formula
    cout = ((a & b) | (cin & (a ^ b))); // Calculating cout as per formula
  end
  
endmodule

// A 4-bit Full Adder that uses 1 1-bit full adder and 2 4-bit registers
// To add 2 4-bit unsigned numbers A and B
// The Device Under Test (DUT)
module adder(Clock, Reset, A, B, Sum, Cout);
  input Clock, Reset;	// Clock and Reset for sequential circuit
  input [3:0] A, B;		// Input 4-bit unsigned numbers
  output [3:0] Sum;		// Output sum of A and B
  output Cout;			// Output carry of summation of A and B
  
  reg [3:0] Sum;	// Making it of reg type
  reg Cout;			// Making it of reg type
  
  reg [3:0] temp_A, temp_B;	// Registers R0 and R1 to store inputs
  wire cin, sum, cout;		// Wires to perform 1-bit addition
  
  // cin will always be equal to Cout
  // Hence, whenever Cout changes, cin also changes
  assign cin = Cout;
  
  // Using 1-bit full adder to perform addition
  // I will always perform addition of 0th bits of registers,
  // and then shift the bits of registers on every clock edge,
  // hence my other output values also changes whenever I shift the bits
  fullAdder f(.a(temp_A[0]), .b(temp_B[0]), .cin(cin), .sum(sum), .cout(cout));
  
  // On every positive edge of clock or negative edge or reset
  always @ (posedge Clock or negedge Reset) begin
    if (Reset == 1'b0) begin	// If it is negative edge of reset
      Sum <= 4'b0000;	// Resetting Sum to 0
      Cout <= 1'b0;		// Resetting Cout to 0
      temp_A <= A;		// Resetting temp_A to hold value of A again
      temp_B <= B;		// Resetting temp_B to hold value of B again
      
    end else begin		// else if it is positive edge of clock
      // Shifting my Sum bits to right by 1
      Sum[0] <= Sum[1];
      Sum[1] <= Sum[2];
      Sum[2] <= Sum[3];
      // Sum[3] will be the new sum calculated by full adder
      Sum[3] <= sum;
      // Cout will be the new cout calculated by full adder
      Cout <= cout;
      
      // Shifting the bits of my R0 register by 1, so in next addition, next bit of it will be taken
      temp_A[0] <= temp_A[1];
      temp_A[1] <= temp_A[2];
      temp_A[2] <= temp_A[3];
      temp_A[3] <= 1'b0;
      
      // Shifting the bits of my R1 register by 1, so in next addition, next bit of it will be taken
      temp_B[0] <= temp_B[1];
      temp_B[1] <= temp_B[2];
      temp_B[2] <= temp_B[3];
      temp_B[3] <= 1'b0;
    end
  end
  
endmodule
