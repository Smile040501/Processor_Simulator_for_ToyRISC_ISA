	.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	addi %x0, 2, %x4
	div %x3, %x4, %x5
	bne %x0, %x31, odd
even:
	subi %x0, 1, %x10
	end
odd:
	addi %x0, 1, %x10
	end