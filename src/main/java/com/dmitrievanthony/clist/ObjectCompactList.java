package com.dmitrievanthony.clist;

import java.util.Arrays;
import java.util.Objects;

/**
 * Resizable-array implementation of the <tt>CompactList</tt> interface.
 *
 * @param <T> the type of elements in this list
 */
public class ObjectCompactList<T> implements CompactList<T> {
    /** Default capacity of the underlying array {@link #data}. */
    private static final int DEFAULT_CAPACITY = 10;

    /** The underlying array into which the elements of the list are stored. */
    private Object[] data = new Object[DEFAULT_CAPACITY];

    /** The size of the list. */
    private int size;

    /** {@inheritDoc} */
    @Override public int size() {
        return size;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkRange(index);

        return (T) data[index];
    }

    /** {@inheritDoc} */
    @Override
    public void add(T element) {
        Objects.requireNonNull(element);
        ensureCapacity(size + 1);

        data[size++] = element;
    }

    /**
     * Checks that specified index is greater or equal to zero and is less than the size of the list.
     *
     * @param index the index of element
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    private void checkRange(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
    }

    /**
     * Ensures that the current capacity of the list is enough, otherwise doubles it.
     *
     * @param minCapacity minimum desired capacity
     */
    private void ensureCapacity(int minCapacity) {
        if (data.length < minCapacity) {
            int newCapacity = Math.max(data.length * 2, minCapacity);
            if (newCapacity < 0)
                throw new OutOfMemoryError();

            data = Arrays.copyOf(data, newCapacity);
        }
    }
}
