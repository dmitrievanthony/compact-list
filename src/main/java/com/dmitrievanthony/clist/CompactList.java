package com.dmitrievanthony.clist;

/**
 * An ordered collection with limited capabilities. The user of this interface can add elements, get elements by their
 * integer index and get the size of the list.
 *
 * @param <T> the type of elements in this list
 */
public interface CompactList<T> {
    /**
     * Returns the number of elements in the list.
     *
     * @return the number of elements in the list
     */
    int size();

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index of element to return
     * @return the element at the specified position in this list
     */
    T get(int index);

    /**
     * Adds the specified element into this list.
     *
     * @param element the element to be added
     */
    void add(T element);
}
