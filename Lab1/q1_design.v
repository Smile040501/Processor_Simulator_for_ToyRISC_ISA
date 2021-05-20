// Mayank Singla

// A 1-bit Full Adder that add two bits and give their sum and carry
module fullAdder(a, b, cin, sum, cout);
  input a, b, cin;	// bits of a, b, and cin (carry_In)
  output sum, cout;	// bits of sum and cout (carry_Out)
  
  // Using assign and concatenation, adding the values of input and assigning them to the output
  assign {cout, sum} = a + b + cin;
  
endmodule

// A 4-bit Full Adder that uses 4 1-bit full adders to perform addition of 2 4-bit Unsigned integers
// The Device Under Test (DUT)
module adder(A, B, Sum, Cout);
  input [3:0] A, B;	// Input 4-bit unsigned integers
  output [3:0] Sum;	// Output 4-bit Sum of A and B
  output Cout;		// Output Carry of addition of A and B
  wire c0, c1, c2;	// Using intermediate wires to join 4 Full adders
  
  // Taking each bit of A and B and putting sum bit to Sum
  
  // For the first full adder cin = 0 and cout goes to c0
  fullAdder f0(.a(A[0]), .b(B[0]), .cin(0), .sum(Sum[0]), .cout(c0));
  // For the second full adder cin = c0 and cout goes to c1
  fullAdder f1(.a(A[1]), .b(B[1]), .cin(c0), .sum(Sum[1]), .cout(c1));
  // For the first full adder cin = c1 and cout goes to c2
  fullAdder f2(.a(A[2]), .b(B[2]), .cin(c1), .sum(Sum[2]), .cout(c2));
  // For the first full adder cin = 2 and cout goes to final carry out (Cout)
  fullAdder f3(.a(A[3]), .b(B[3]), .cin(c2), .sum(Sum[3]), .cout(Cout));
  
endmodule
