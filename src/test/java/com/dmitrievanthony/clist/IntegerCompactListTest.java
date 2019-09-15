package com.dmitrievanthony.clist;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Tests for <tt>IntegerCompactList</tt> generated by {@link CompactListGenerator} and {@link CompactListFactory}.
 */
public class IntegerCompactListTest extends CompactListAbstractTest<Integer> {
    /** {@inheritDoc} */
    @Override
    Iterator<Integer> getSequence() {
        return Stream.iterate(0, e -> e + 1).iterator();
    }

    /** {@inheritDoc} */
    @Override
    CompactList<Integer> getCompactList() {
        return new CompactListFactory().newCompactList(Integer.class);
    }
}