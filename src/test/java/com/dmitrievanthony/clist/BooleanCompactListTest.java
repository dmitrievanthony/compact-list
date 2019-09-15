package com.dmitrievanthony.clist;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Tests for <tt>BooleanCompactList</tt> generated by {@link CompactListGenerator} and {@link CompactListFactory}.
 */
public class BooleanCompactListTest extends CompactListAbstractTest<Boolean> {
    /** {@inheritDoc} */
    @Override
    Iterator<Boolean> getSequence() {
        return Stream.iterate(true, e -> !e).iterator();
    }

    /** {@inheritDoc} */
    @Override
    CompactList<Boolean> getCompactList() {
        return new CompactListFactory().newCompactList(Boolean.class);
    }
}