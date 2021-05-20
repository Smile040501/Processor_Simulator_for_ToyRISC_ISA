// Mayank Singla

// Test Bench for 4-bit Full Adder
module tb(); 
  reg clock, reset;	// reg type variables clock and rest
  reg [3:0] a, b;	// reg type variables a and b
  wire cout;		// Wire for cout
  wire [3:0] sum;	// wire for sum
  integer i, j;		// integers i and j to run the loop
  
  // Instantiating DUT
  adder add(.Clock(clock), .Reset(reset), .A(a), .B(b), .Sum(sum), .Cout(cout));
  
  // To generate wave forms when using Synopsis VCS Simulator
  initial begin
  	$dumpfile("dump.vcd");
  	$dumpvars();
  end
  
  // Generating clock
  initial begin
    clock = 1'b0;
    forever #10 clock = ~clock;
  end
      
  // Applying stimulus
  initial begin 
    // Generating all the 4-bit binary numbers i.e from [0, 15]
    // and testing for every possible addition
    for(i = 0; i <= 15; i = i + 1) begin
      for(j = 0; j <= 15; j = j + 1) begin
        #10 
        reset = 1'b0;	// Providing inputs
        a = i;
        b = j;
        
        #10
        // Changing it's value so that on next clock cycle, addition can start taking place
        reset = 1'b1;
        
        #90
        reset = 1'b0;
      end
    end
    // As I am checking for many inputs, waveform build may be 
    // cluttered. Zoom in and scroll sideways to see clearly.
    $finish;
  end
  
  // Stimulus for waveform in the snapshot in pdf
//   initial begin
//     reset = 1'b0;
//     a = 4'b1111;
//     b = 4'b1111;
    
//     #5 reset = 1'b1;
    
//     #80
//     reset = 1'b0;
//     $finish;
//   end
              
endmodule
