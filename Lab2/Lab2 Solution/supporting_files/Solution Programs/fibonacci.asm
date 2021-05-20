	.data
n:
	10
	.text
main:
	load %x0, $n, %x3
	addi %x0, 65535, %x4
	add %x0, %x0, %x5
	add %x0, %x0, %x6
	beq %x5, %x3, success
	store %x6, 0, %x4
	subi %x4, 1, %x4
	addi %x5, 1, %x5
	addi %x0, 1, %x7
	beq %x5, %x3, success
	store %x7, 0, %x4
loop:
	subi %x4, 1, %x4
	addi %x5, 1, %x5
	beq %x5, %x3, success
	add %x6, %x7, %x8
	store %x8, 0, %x4
	add %x0, %x7, %x6
	add %x0, %x8, %x7
	jmp loop
success:
	end