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
movq $5, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmovg %rbx, %rax
push %rax
pop %rax
cmp $1, %rax
jne L1
L2:
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
movq (%rax), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq $-8, %rax
add %rbp, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push (%rax)
pop %rcx
pop %rbx
movq (%rbx), %rax
cmpq $5, %rax
jae array_out_of_bounds
imulq $8, %rax
subq %rax, %rcx
push %rcx
movq $-8, %rax
add %rbp, %rax
push %rax
movq $24, %rax
add %rbp, %rax
push (%rax)
pop %rcx
pop %rbx
movq (%rbx), %rax
cmpq $5, %rax
jae array_out_of_bounds
imulq $8, %rax
subq %rax, %rcx
push %rcx
pop %rax
pop %rbx
sub $0, %rax
sub $0, %rbx
movq %rax, %rdi
movq %rbx, %rsi
movl $8, %edx
call memcpy
movq $1, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
addq %rbx, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
movq $5, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmovle %rbx, %rax
push %rax
pop %rax
cmpq $1, %rax
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
movq $0, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmove %rbx, %rax
push %rax
pop %rax
cmp $1, %rax
jne L3
movq $0, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
jmp L4
L3:
movq $1, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmove %rbx, %rax
push %rax
pop %rax
cmp $1, %rax
jne L5
movq $1, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
jmp L6
L5:
movq $1, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
subq %rbx, %rax
push %rax
call fib
addq $8, %rsp
push %rax
movq $2, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
subq %rbx, %rax
push %rax
call fib
addq $8, %rsp
push %rax
pop %rax
pop %rbx
addq %rbx, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
L6:
L4:
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rbx
movq (%rbx), %rax
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
movq $-8, %rax
add _globals, %rax
push %rax
pop %rax
movq (%rax), %rsi
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
movq $16, %rax
add %rbp, %rax
push %rax
pop %rax
movq (%rax), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq $24, %rax
add %rbp, %rax
push %rax
pop %rax
movq (%rax), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
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
movq $5, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmovg %rbx, %rax
push %rax
pop %rax
cmp $1, %rax
jne L7
L8:
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
movq (%rax), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq $-8, %rax
add %rbp, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
movq $16, %rax
add %rbp, %rax
push (%rax)
pop %rcx
pop %rbx
movq (%rbx), %rax
cmpq $5, %rax
jae array_out_of_bounds
imulq $8, %rax
subq %rax, %rcx
push %rcx
pop %rax
pop %rbx
sub $0, %rax
sub $0, %rbx
movq %rax, %rdi
movq %rbx, %rsi
movl $8, %edx
call memcpy
movq $1, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
addq %rbx, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
movq $5, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rbx
cmpq %rax, %rbx
movq $0, %rax
movq $1, %rbx
cmovle %rbx, %rax
push %rax
pop %rax
cmpq $1, %rax
jne L8
L7:
movq %rbp, %rsp
pop %rbp
retq


main:

push %rbp
movq %rsp, %rbp
subq $112, %rsp
movl $112, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq %rbp, _globals
movq $-24, %rax
add _globals, %rax
push %rax
call set
addq $8, %rsp
movq $-72, %rax
add _globals, %rax
push %rax
movq $-24, %rax
add _globals, %rax
push %rax
call copy
addq $16, %rsp
movq $4, %rax
push %rax
movq $-72, %rax
add _globals, %rax
push %rax
pop %rcx
pop %rax
cmpq $5, %rax
jae array_out_of_bounds
imulq $8, %rax
subq %rax, %rcx
push %rcx
pop %rax
movq (%rax), %rsi
movl $printf_num, %edi
movl $0, %eax
call printf
movq $8, %rax
push %rax
movq $7, %rax
push %rax
call print
addq $16, %rsp
movq $167, %rax
push %rax
movq $-8, %rax
add _globals, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
call globalTest
addq $0, %rsp
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
