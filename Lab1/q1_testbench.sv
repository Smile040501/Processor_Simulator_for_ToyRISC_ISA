// Mayank Singla

// Test Bench for 4-bit Full Adder
module tb(); 
  reg [3:0] a, b;	// Registers a and b
  wire [3:0] sum;	// Wire for sum
  wire cout; 		// Wire for cout
  integer i, j;		// integers i and j to run the loop
  
  // Instantiating DUT
  adder add(.A(a), .B(b), .Sum(sum), .Cout(cout));
  
  // To generate wave forms when using Synopsis VCS Simulator
  initial begin
  	$dumpfile("dump.vcd");
  	$dumpvars();
  end
           
  // Applying stimulus
  initial begin 
    // Generating all the 4-bit binary numbers i.e from [0, 15]
    // and testing for every possible addition
    for(i = 0; i <= 15; i = i + 1) begin
      #10 a = i;
      for(j = 0; j <= 15; j = j + 1) begin
        #10 b = j;
      end
    end
    // As I am checking for many inputs, waveform build may be 
    // cluttered. Zoom in and scroll sideways to see clearly.
  end
  
  // Stimulus for waveform in the snapshot in pdf
//   initial begin
//     a = 4'b0001;
//     b = 4'b0010;
    
//     #5
//     a = 4'b0010;
//     b = 4'b0110;
    
//     #5
//     a = 4'b1111;
//     b = 4'b1111;
//   end
  
  // Displaying Outputs
  always @ (sum or cout) begin
    $monitor ("a=0x%0h b=0x%0h sum=0x%0h cout=0x%0h", a, b, sum, cout);
  end
              
endmodule 
