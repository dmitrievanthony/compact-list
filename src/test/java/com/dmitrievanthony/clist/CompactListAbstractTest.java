package com.dmitrievanthony.clist;

import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Base abstract test class for {@link CompactList} implementations.
 *
 * @param <T> the type of elements in this list
 */
public abstract class CompactListAbstractTest<T> {
    /**
     * Constructs the fixed sequence of elements.
     *
     * @return the fixed sequence of elements
     */
    abstract Iterator<T> getSequence();

    /**
     * Constructs the new instance of compact list.
     *
     * @return the new instance of compact list
     */
    abstract CompactList<T> getCompactList();

    /**
     * Tests a simple {@link ObjectCompactList#add(Object)} followed by {@link ObjectCompactList#get(int)} of one
     * element.
     */
    @Test
    public void testAddGet() {
        T element = getSequence().next();

        CompactList<T> list = getCompactList();
        list.add(element);

        assertEquals(1, list.size());
        assertEquals(element, list.get(0));
    }

    /**
     * Tests a series of {@link ObjectCompactList#add(Object)} that triggers a resize of the list (assuming that default
     * capacity of the list is 10) followed by {@link ObjectCompactList#get(int)}.
     */
    @Test
    public void testAddGetWithResize() {
        Iterator<T> addSequence = getSequence();
        Iterator<T> testSequence = getSequence();

        CompactList<T> list = getCompactList();
        for (int i = 0; i < 11; i++)
            list.add(addSequence.next());

        assertEquals(11, list.size());
        for (int i = 0; i < 11; i++)
            assertEquals(testSequence.next(), list.get(i));
    }

    /**
     * Tests that list doesn't accept <tt>null</tt> values.
     */
    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        CompactList<T> list = getCompactList();
        list.add(null);
    }

    /**
     * Tests {@link ObjectCompactList#get(int)} with wrong index (index that is greater than the size of the list).
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetWithWrongIndex() {
        CompactList<T> list = getCompactList();
        list.get(0);
    }

    /**
     * Tests {@link ObjectCompactList#get(int)} with wrong index (negative index).
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetWithNegativeIndex() {
        CompactList<T> list = getCompactList();
        list.get(-1);
    }
}
