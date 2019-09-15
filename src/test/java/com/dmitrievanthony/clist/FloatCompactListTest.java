package com.dmitrievanthony.clist;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Tests for <tt>FloatCompactList</tt> generated by {@link CompactListGenerator} and {@link CompactListFactory}.
 */
public class FloatCompactListTest extends CompactListAbstractTest<Float> {
    /** {@inheritDoc} */
    @Override
    Iterator<Float> getSequence() {
        return Stream.iterate(0f, e -> e + 1).iterator();
    }

    /** {@inheritDoc} */
    @Override
    CompactList<Float> getCompactList() {
        return new CompactListFactory().newCompactList(Float.class);
    }
}