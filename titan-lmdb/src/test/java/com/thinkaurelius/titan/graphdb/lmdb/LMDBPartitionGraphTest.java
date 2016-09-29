package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.graphdb.TitanPartitionGraphTest;


public class LMDBPartitionGraphTest extends TitanPartitionGraphTest {

    @Override
    public WriteConfiguration getBaseConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }

    /**
     * TODO: debug berkeley dbs keyslice method
     */

}
