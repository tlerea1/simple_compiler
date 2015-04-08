.text
.globl main
main:

push %rbp
movq %rsp, %rbp
subq $16, %rsp
movl $16, %edx
movl $0, %esi
movq %rsp, %rdi
call memset
movq $1, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
movq $0, %rax
push %rax
movq $-16, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
movq $-16, %rax
add %rbp, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rcx
movq (%rcx), %rax
pop %rcx
movq (%rcx), %rbx
cmp $0, %rbx
je mod_by_zero
cltd
idivq %rbx
movq %rdx, %rax
push %rax
movq $-8, %rax
add %rbp, %rax
push %rax
pop %rax
pop %rbx
movq %rbx, (%rax)
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
