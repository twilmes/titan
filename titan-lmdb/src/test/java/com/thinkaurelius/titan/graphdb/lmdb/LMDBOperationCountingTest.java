package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.graphdb.TitanOperationCountingTest;

public class LMDBOperationCountingTest extends TitanOperationCountingTest {

    @Override
    public WriteConfiguration getBaseConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }

    @Override
    public boolean storeUsesConsistentKeyLocker() {
        return false;
    }

}
