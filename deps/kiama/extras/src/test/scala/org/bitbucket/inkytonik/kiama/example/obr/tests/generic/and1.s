    ! Prologue
    .seg "data"
ifmt:
    .asciz "%d"
ofmt:
    .asciz "%d\n"
    .align 4
mem:
    .skip 4
    .seg "text"
    .globl main
main:
    save %sp, -112, %sp
    set mem, %l0
    ! Read(Local(0))
    set ifmt, %o0
    add %l0, 0, %o1
    call scanf
    nop
    ! Beq(Cond(IntDatum(0),CmpeqW(DivW(IntDatum(1),IntDatum(0)),IntDatum(1)),IntDatum(0)),L1)
    mov 0, %l1
    tst %l1
    be L3
    nop
    mov 1, %l2
    mov 0, %l3
    sdiv %l2, %l3, %l3
    mov 1, %l4
    cmp %l3, %l4
    mov 1, %l4
    be L5
    nop
    mov 0, %l4
L5:
    mov %l4, %l1
    ba L4
    nop
L3:
    mov 0, %l5
    mov %l5, %l1
L4:
    tst %l1
    be L1
    nop
    ! Write(IntDatum(0))
    mov 0, %l1
    set ofmt, %o0
    mov %l1, %o1
    call printf
    nop
    ! Ret
    ba go
    nop
    ! Jmp(L2)
    ba L2
    nop
    ! LabelDef(L1)
L1:
    ! Write(IntDatum(1))
    mov 1, %l1
    set ofmt, %o0
    mov %l1, %o1
    call printf
    nop
    ! Ret
    ba go
    nop
    ! LabelDef(L2)
L2:
    ! Epilogue
go:
    ret
    restore
