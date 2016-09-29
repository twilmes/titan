package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.graphdb.TitanGraphConcurrentTest;

public class LMDBGraphConcurrentTest extends TitanGraphConcurrentTest {

    @Override
    public WriteConfiguration getConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }

}
