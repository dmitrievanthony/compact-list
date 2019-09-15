package com.dmitrievanthony.clist;

/**
 * Dynamic class loader that allows to load classes using their bytecode loaded as a byte array.
 */
class DynamicClassLoader extends ClassLoader {
    /**
     * Converts an array of bytes into an instance of class <tt>Class</tt>.
     *
     * @param className the name of the class
     * @param byteCode the bytes that make up the class data
     * @return the <tt>Class</tt> object that was created from the specified class data
     */
    Class<?> defineClass(String className, byte[] byteCode) {
        return defineClass(className, byteCode, 0, byteCode.length);
    }
}
