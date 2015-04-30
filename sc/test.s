.text
.globl main
copy:
push %rbp
movq %rsp, %rbp
subq $8, %rsp
movl $8, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovg %rbx, %r14
cmp $1, %r14
jne L1
L2:
movq -8(%rbp), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq 24(%rbp), %r14
movq -8(%rbp), %r8
sal $3, %r8
addq %r8, %r14
movq 16(%rbp), %r8
movq -8(%rbp), %r9
sal $3, %r9
addq %r9, %r8
movq (%r8), %r8
movq %r8, (%r14)
movq -8(%rbp), %r14
addq $1, %r14
movq %r14, -8(%rbp)
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovle %rbx, %r14
cmpq $1, %r14
jne L2
L1:
movq %rbp, %rsp
pop %rbp
retq


fib:
push %rbp
movq %rsp, %rbp
subq $8, %rsp
movl $8, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq $0, %r14
cmpq 16(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmove %rbx, %r14
cmp $1, %r14
jne L3
movq $0, -8(%rbp)
jmp L4
L3:
movq $1, %r14
cmpq 16(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmove %rbx, %r14
cmp $1, %r14
jne L5
movq $1, -8(%rbp)
jmp L6
L5:
movq 16(%rbp), %r14
subq $2, %r14
push %r14
call fib
addq $8, %rsp
movq %rax, %r14
push %r14
movq 16(%rbp), %r14
subq $1, %r14
push %r14
call fib
addq $8, %rsp
pop %r14
addq %rax, %r14
movq %r14, -8(%rbp)
L6:
L4:
movq -8(%rbp), %rax
movq %rbp, %rsp
pop %rbp
retq


globalTest:
push %rbp
movq %rsp, %rbp
subq $0, %rsp
movl $0, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq -8(%r15), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq %rbp, %rsp
pop %rbp
retq


print:
push %rbp
movq %rsp, %rbp
subq $0, %rsp
movl $0, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq 16(%rbp), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq 24(%rbp), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq %rbp, %rsp
pop %rbp
retq


printArray:
push %rbp
movq %rsp, %rbp
subq $8, %rsp
movl $8, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovg %rbx, %r14
cmp $1, %r14
jne L7
L8:
movq 16(%rbp), %r14
movq -8(%rbp), %r8
sal $3, %r8
addq %r8, %r14
movq (%r14), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq -8(%rbp), %r14
addq $1, %r14
movq %r14, -8(%rbp)
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovle %rbx, %r14
cmpq $1, %r14
jne L8
L7:
movq %rbp, %rsp
pop %rbp
retq


set:
push %rbp
movq %rsp, %rbp
subq $8, %rsp
movl $8, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovg %rbx, %r14
cmp $1, %r14
jne L9
L10:
movq -8(%rbp), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq 16(%rbp), %r14
movq -8(%rbp), %r8
sal $3, %r8
addq %r8, %r14
leaq -8(%rbp), %r8
movq (%r8), %r8
movq %r8, (%r14)
movq -8(%rbp), %r14
addq $1, %r14
movq %r14, -8(%rbp)
movq $5, %r14
cmpq -8(%rbp), %r14
movq $0, %r14
movq $1, %rbx
cmovle %rbx, %r14
cmpq $1, %r14
jne L10
L9:
movq %rbp, %rsp
pop %rbp
retq


main:

push %rbp
movq %rsp, %rbp
subq $136, %rsp
movl $136, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq %rbp, %r15
leaq -56(%r15), %r14
push %r14
call set
addq $8, %rsp
leaq -96(%r15), %r14
push %r14
leaq -56(%r15), %r14
push %r14
call copy
addq $16, %rsp
movq -64(%r15), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
push $8
push $7
call print
addq $16, %rsp
movq $167, -8(%r15)
call globalTest
addq $0, %rsp
leaq -96(%r15), %rsi
leaq -136(%r15), %rdi
movq $40, %rdx
call memcpy
leaq -136(%r15), %r14
push %r14
call printArray
addq $8, %rsp
movl $0, %eax
leave
retq



array_out_of_bounds:
movl $printf_arg, %edi
movl $array_str, %esi
movl $0, %eax
call printf
movl $1, %edi
call exit


div_by_zero:
movl $printf_arg, %edi
movl $div_zero_mes, %esi
movl $0, %eax
call printf
movl $1, %edi
call exit


mod_by_zero:
movl $printf_arg, %edi
movl $mod_zero_mes, %esi
movl $0, %eax
call printf
movl $1, %edi
call exit


.section .rodata
array_str:
.string "error: Array index out of bounds!\n"
printf_arg:
.string "%s"
printf_num:
.string "%d\n"
scanf_num:
.string "%d"
div_zero_mes:
.string "error: Division by Zero!\n"
mod_zero_mes:
.string "error: Mod by Zero!\n"
.section .data
_globals:
.long 0
