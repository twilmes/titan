package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.graphdb.TitanGraphPerformanceMemoryTest;

public class LMDBGraphPerformanceMemoryTest extends TitanGraphPerformanceMemoryTest {

    @Override
    public WriteConfiguration getConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }


}
