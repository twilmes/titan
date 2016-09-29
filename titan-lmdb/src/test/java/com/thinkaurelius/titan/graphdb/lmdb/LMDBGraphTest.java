package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigOption;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager;
import com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager.IsolationLevel;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigElement;
import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.graphdb.TitanGraphTest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class LMDBGraphTest extends TitanGraphTest {

    @Rule
    public TestName methodNameRule = new TestName();

    private static final Logger log =
            LoggerFactory.getLogger(LMDBGraphTest.class);

    @Override
    public WriteConfiguration getConfiguration() {
        ModifiableConfiguration mcfg = LMDBStorageSetup.getLMDBConfiguration();
        String methodName = methodNameRule.getMethodName();
        if (methodName.equals("testConsistencyEnforcement")) {
            IsolationLevel iso = IsolationLevel.SERIALIZABLE;
            log.debug("Forcing isolation level {} for test method {}", iso, methodName);
            mcfg.set(BerkeleyJEStoreManager.ISOLATION_LEVEL, iso.toString());
        } else {
            IsolationLevel iso = null;
            if (mcfg.has(BerkeleyJEStoreManager.ISOLATION_LEVEL)) {
                iso = ConfigOption.getEnumValue(mcfg.get(BerkeleyJEStoreManager.ISOLATION_LEVEL),IsolationLevel.class);
            }
            log.debug("Using isolation level {} (null means adapter default) for test method {}", iso, methodName);
        }
        return mcfg.getConfiguration();
    }

    @Override
    public void testConsistencyEnforcement() {
        // Check that getConfiguration() explicitly set serializable isolation
        // This could be enforced with a JUnit assertion instead of a Precondition,
        // but a failure here indicates a problem in the test itself rather than the
        // system-under-test, so a Precondition seems more appropriate
        IsolationLevel effective = ConfigOption.getEnumValue(config.get(ConfigElement.getPath(BerkeleyJEStoreManager.ISOLATION_LEVEL), String.class),IsolationLevel.class);
        Preconditions.checkState(IsolationLevel.SERIALIZABLE.equals(effective));
        super.testConsistencyEnforcement();
    }

    @Override
    protected boolean isLockingOptimistic() {
        return false;
    }

    @Override
    public void testConcurrentConsistencyEnforcement() {
        //Do nothing TODO: Figure out why this is failing in BerkeleyDB!!
    }

    @Test
    public void testIDBlockAllocationTimeout()
    {
        config.set("ids.authority.wait-time", Duration.of(0L, ChronoUnit.NANOS));
        config.set("ids.renew-timeout", Duration.of(1L, ChronoUnit.MILLIS));
        close();
        TitanCleanup.clear(graph);
        open(config);
        try {
            graph.addVertex();
            fail();
        } catch (TitanException e) {

        }

        assertTrue(graph.isOpen());

        close(); // must be able to close cleanly

        // Must be able to reopen
        open(config);

        assertEquals(0L, (long)graph.traversal().V().count().next());
    }
}
