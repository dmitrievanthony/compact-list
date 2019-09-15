package com.dmitrievanthony.clist;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static com.sun.org.apache.bcel.internal.Constants.ALOAD_0;
import static com.sun.org.apache.bcel.internal.Constants.ALOAD_1;
import static com.sun.org.apache.bcel.internal.Constants.ILOAD_1;
import static com.sun.org.apache.bcel.internal.Constants.ILOAD_2;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * The generator that generates implementations of {@link CompactList} that are optimized to work with primitive types.
 */
class CompactListGenerator {
    /** The canonical class name with a dot separator. */
    private final String className;

    /** The type descriptor (I for int, J for long, etc). */
    private final String descriptor;

    /** The type of object representation (java/lang/Integer for int, java/lang/Long got long, etc). */
    private final String type;

    /** The name of the function that converts object representation into primitive representation. */
    private final String toPrimitive;

    /** The type operand. */
    private final int typeOperand;

    /** The opcode of the store into the array instruction. */
    private final int storeOpcode;

    /** The opcode of the load from the array instruction. */
    private final int loadOpcode;

    /**
     * Constructs the new instance of <tt>CompactListGenerator</tt>.
     *
     * @param className the canonical class name with a dot separator
     * @param descriptor the type descriptor (I for int, J for long, etc)
     * @param type the type of object representation (java/lang/Integer for int, java/lang/Long got long, etc)
     * @param toPrimitive the name of the function that converts object representation into primitive representation
     * @param typeOperand the type operand
     * @param storeOpcode the opcode of the store into the array instruction
     * @param loadOpcode the opcode of the load from the array instruction
     */
    CompactListGenerator(String className,
        String descriptor,
        String type,
        String toPrimitive,
        int typeOperand,
        int storeOpcode,
        int loadOpcode) {
        this.className = className;
        this.descriptor = descriptor;
        this.type = type;
        this.toPrimitive = toPrimitive;
        this.typeOperand = typeOperand;
        this.storeOpcode = storeOpcode;
        this.loadOpcode = loadOpcode;
    }

    /**
     * Generates the bytecode of the class that implements <tt>CompactList</tt>.
     *
     * @return the bytecode of the class that implements <tt>CompactList</tt>
     */
    byte[] generate() {
        ClassWriter writer = new ClassWriter(0);

        generateHeader(writer);
        generateFields(writer);
        generateConstructor(writer);
        generateSizeMethod(writer);
        generateAddMethod(writer);
        generateGetMethod(writer);
        generateCheckRangeMethod(writer);
        generateEnsureCapacityMethod(writer);

        return writer.toByteArray();
    }

    /**
     * Generates the class header.
     *
     * @param cv the class visitor
     */
    private void generateHeader(ClassVisitor cv) {
        cv.visit(
            V1_8,
            ACC_PUBLIC,
            className.replace('.', '/'),
            null,
            Object.class.getCanonicalName().replace('.', '/'),
            new String[] {CompactList.class.getCanonicalName().replace('.', '/')}
        );
    }

    /**
     * Generates the class fields.
     *
     * @param cv the class visitor
     */
    private void generateFields(ClassVisitor cv) {
        cv.visitField(ACC_PRIVATE, "data", "[" + descriptor, null, null).visitEnd();
        cv.visitField(ACC_PRIVATE, "size", "I", null, null).visitEnd();
    }

    /**
     * Generates the class constructor.
     *
     * @param cv the class visitor
     */
    private void generateConstructor(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

        mv.visitCode();

        /* Call parent constructor. */
        mv.visitInsn(ALOAD_0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        /* Pushes "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Pushes 10 onto the stack, calls "NEWARRAY" that pops 10 from the stack and pushes array reference back. */
        mv.visitVarInsn(BIPUSH, 10);
        mv.visitIntInsn(NEWARRAY, typeOperand);
        /* Calls "PUTFIELD" that pops array reference and "this" from the stack. */
        mv.visitFieldInsn(PUTFIELD, className.replace('.', '/'), "data", "[" + descriptor);
        /* Return void. */
        mv.visitInsn(RETURN);

        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    /**
     * Generates the <tt>size</tt> method.
     *
     * @param cv the class visitor
     */
    private void generateSizeMethod(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "size", "()I", null, null);

        mv.visitCode();

        /* Pushes "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes the field value (size) onto the stack. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "size", "I");
        /* Pops integer value from the stack and returns it. */
        mv.visitInsn(IRETURN);

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /**
     * Generates the <tt>add</tt> method.
     *
     * @param cv the class visitor
     */
    private void generateAddMethod(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "add", "(Ljava/lang/Object;)V", null, null);

        mv.visitCode();

        /* Pushes "this" onto the stack twice. */
        mv.visitInsn(ALOAD_0);
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes the field value (size) onto the stack. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "size", "I");
        /* Pushes "1" onto the stack. */
        mv.visitInsn(ICONST_1);
        /* Pops "1" and size from the stacks and pushes their sum back. */
        mv.visitInsn(IADD);
        /* Calls "INVOKESPECIAL" that pops integer value and "this" from the stack. */
        mv.visitMethodInsn(INVOKESPECIAL, className.replace('.', '/'), "ensureCapacity", "(I)V", false);

        /* Pushed "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes the reference to data array onto the stack. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "data", "[" + descriptor);
        /* Pushes "this" onto the stack twice. */
        mv.visitInsn(ALOAD_0);
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes the field value (size) back. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "size", "I");
        /* Copies the top element (size value) and insert it into stack with position top - 2. */
        mv.visitInsn(DUP_X1);
        /* Pushes "1" onto the stack. */
        mv.visitInsn(ICONST_1);
        /* Pops "1" and size from the stacks and pushes their sum back. */
        mv.visitInsn(IADD);
        /* Calls "PUTFIELD" that pops integer value and "this" from the stack and updates field. */
        mv.visitFieldInsn(PUTFIELD, className.replace('.', '/'), "size", "I");
        /* Pushes specified element onto the stack. */
        mv.visitInsn(ALOAD_1);
        /* Casts the specified element (from the stack) to object representation of the primitive and pushes it back. */
        mv.visitTypeInsn(CHECKCAST, type);
        /* Calls "INVOKEVIRTUAL" that pops element reference from the stack and pushes back the primitive. */
        mv.visitMethodInsn(INVOKEVIRTUAL, type, toPrimitive, "()" + descriptor, false);
        /* Stores primitive into the data array (pops primitive value and "data" from the stack). */
        mv.visitInsn(storeOpcode);
        /* Return void. */
        mv.visitInsn(RETURN);

        mv.visitMaxs(5, 2);
        mv.visitEnd();
    }

    /**
     * Generates the <tt>get</tt> method.
     *
     * @param cv the class visitor
     */
    private void generateGetMethod(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "get", "(I)Ljava/lang/Object;", null, null);

        mv.visitCode();

        /* Pushes "this" and index onto the stack. */
        mv.visitInsn(ALOAD_0);
        mv.visitInsn(ILOAD_1);
        /* Calls "INVOKESPECIAL" that pops index and "this" from the stack. */
        mv.visitMethodInsn(INVOKESPECIAL, className.replace('.', '/'), "checkRange", "(I)V", false);

        /* Pushes "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Pops "this" from the stack and pushes back reference to data. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "data", "[" + descriptor);

        /* Pushes index onto the stack, */
        mv.visitInsn(ILOAD_1);
        /* Pops index and reference to data from the stack and pushes back value from the array with the index. */
        mv.visitInsn(loadOpcode);
        /* Calls "INVOKESTATIC" that pops reference to the value and pushes back object representation. */
        mv.visitMethodInsn(INVOKESTATIC, type, "valueOf", "(" + descriptor + ")L" + type + ";", false);
        /* Returns reference to the object representation of the value. */
        mv.visitInsn(ARETURN);

        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * Generates the <tt>checkRange</tt> method.
     *
     * @param cv the class visitor
     */
    private void generateCheckRangeMethod(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE, "checkRange", "(I)V", null, null);

        mv.visitCode();

        /* Define two labels, they will be inserted later. */
        Label l1 = new Label();
        Label l2 = new Label();

        /* Loads index onto the stack. */
        mv.visitInsn(ILOAD_1);
        /* If value on top of the stack (index) is less than 0 goto l1. */
        mv.visitJumpInsn(IFLT, l1);
        /* Load index and "this" onto the stack. */
        mv.visitInsn(ILOAD_1);
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes the field value (size) back. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "size", "I");
        /* If index is less than size goto l2. */
        mv.visitJumpInsn(IF_ICMPLT, l2);

        /* Section that throws the exception. */
        mv.visitLabel(l1);
        /* Should be called after jump target. */
        mv.visitFrame(F_SAME, 0, null, 0, null);
        /* Creates a new object and pushes it onto the stack. */
        mv.visitTypeInsn(NEW, "java/lang/IndexOutOfBoundsException");
        /* Duplicates the value on top of the stack. */
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IndexOutOfBoundsException", "<init>", "()V", false);
        /* Throws the exception. */
        mv.visitInsn(ATHROW);

        /* Section for correct return. */
        mv.visitLabel(l2);
        /* Should be called after jump target. */
        mv.visitFrame(F_SAME, 0, null, 0, null);
        /* Returns void. */
        mv.visitInsn(RETURN);

        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * Generates the <tt>ensureCapacity</tt> method.
     *
     * @param cv the class visitor
     */
    private void generateEnsureCapacityMethod(ClassVisitor cv) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE, "ensureCapacity", "(I)V", null, null);

        mv.visitCode();

        /* Define the label, it will be inserted later. */
        Label l1 = new Label();

        /* Loads "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops "this" from the stack and pushes reference to "data" back. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "data", "[" + descriptor);
        /* Calls "ARRAYLENGTH" that pops reference to "data" from the stack and pushes the array length back. */
        mv.visitInsn(ARRAYLENGTH);
        /* Pushes "minCapacity" onto the stack. */
        mv.visitInsn(ILOAD_1);
        /* If array length is greater or equal to "minCapacity" goto l1. */
        mv.visitJumpInsn(IF_ICMPGE, l1);
        /* Pushes "this" onto the stack. */
        mv.visitInsn(ALOAD_0);
        /* Calls "GETFIELD" that pops this from the stack and pushes reference to "data" back. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "data", "[" + descriptor);
        /* Pops reference to "data" from the stack and pushes array length back. */
        mv.visitInsn(ARRAYLENGTH);
        /* Pushes 2 onto the stack. */
        mv.visitInsn(ICONST_2);
        /* Pops array length and 2 from the stack and pushes the product back. */
        mv.visitInsn(IMUL);
        /* Pushes "minCapacity" onto the stack. */
        mv.visitInsn(ILOAD_1);
        /* Pops "minCapacity" and produce of array length and 2 from the stack, pushes max value back. */
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "max", "(II)I", false);
        /* Store the produce into variable 2. */
        mv.visitVarInsn(ISTORE, 2);
        // TODO: Check integer overflow.
        /* Load "this" onto the stack twice. */
        mv.visitInsn(ALOAD_0);
        mv.visitInsn(ALOAD_0);
        /* Pops "this" from the stack and pushes reference to the "data" back. */
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), "data", "[" + descriptor);
        /* Pushes variable 2 onto the stack. */
        mv.visitInsn(ILOAD_2);
        /* Allocates new array and copies old data into it. */
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf", "(" + "[" + descriptor + "I)" + "[" + descriptor, false);
        /* Update "data" field. */
        mv.visitFieldInsn(PUTFIELD, className.replace('.', '/'), "data", "[" + descriptor);

        /* Section for correct return. */
        mv.visitLabel(l1);
        /* Should be called after jump target. */
        mv.visitFrame(F_SAME, 0, null, 0, null);
        /* Returns void. */
        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }
}
