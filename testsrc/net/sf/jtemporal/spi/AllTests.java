package net.sf.jtemporal.spi;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.sf.jtemporal.spi");
        //$JUnit-BEGIN$
        suite.addTestSuite(LazyTemporalAttributeStorageTest.class);
        suite.addTestSuite(TreeTemporalAttributeStorageTest.class);
        suite.addTestSuite(PeriodStorageTest.class);
        suite.addTestSuite(ORMTemporalAttributeStorageTest.class);
        suite.addTestSuite(CompactTemporalSetStorageTest.class);
        suite.addTestSuite(LazyTemporalAttibuteStorageIFTest.class);
        //$JUnit-END$
        return suite;
    }

}