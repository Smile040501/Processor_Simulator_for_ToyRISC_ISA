	.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	add %x0, %x0, %x4
	add %x0, %x3, %x5
loop:
	beq %x0, %x5, finish
	divi %x5, 10, %x5
	muli %x4, 10, %x4
	add %x4, %x31, %x4
	jmp loop
finish:
	bne %x3, %x4, notPalindrome
isPalindrome:
	addi %x0, 1, %x10
	end
notPalindrome:
	subi %x0, 1, %x10
	end