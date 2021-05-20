	.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	beq %x0, %x3, notPrime
	addi %x0, 1, %x4
	beq %x4, %x3, notPrime
	addi %x0, 2, %x5
loop:
	mul %x5, %x5, %x6
	bgt %x6, %x3, prime
	div %x3, %x5, %x7
	beq %x0, %x31, notPrime
	addi %x5, 1, %x5
	jmp loop
prime:
	addi %x0, 1, %x10
	end
notPrime:
	subi %x0, 1, %x10
	end