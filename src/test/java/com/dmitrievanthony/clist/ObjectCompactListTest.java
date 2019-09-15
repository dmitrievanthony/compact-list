package com.dmitrievanthony.clist;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Tests for {@link ObjectCompactList}.
 */
public class ObjectCompactListTest extends CompactListAbstractTest<Object> {
    /** {@inheritDoc} */
    @Override
    Iterator<Object> getSequence() {
        return Stream.iterate(0L, e -> e + 1).map(Object.class::cast).iterator();
    }

    /** {@inheritDoc} */
    @Override
    CompactList<Object> getCompactList() {
        return new ObjectCompactList<>();
    }
}
