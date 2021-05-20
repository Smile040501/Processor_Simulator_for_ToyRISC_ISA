	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	load %x0, $n, %x3
	subi %x0, 1, %x30
	addi %x0, 1, %x4
loop1:
	beq %x3, %x4, success1
	subi %x4, 1, %x5
	load %x4, $a, %x6
loop2:
	beq %x5, %30, success2
	load %x5, $a, %x7
	beq %x7, %x6, success2
	bgt %x7, %x6, success2
	addi %x5, 1, %x8
	store %x7, $a, %x8
	subi %x5, 1, %x5
	jmp loop2
success2:
	addi %x5, 1, %x5
	store %x6, $a, %x5
	addi %x4, 1, %x4
	jmp loop1
success1:
	end