package com.thinkaurelius.titan.graphdb.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;
import com.thinkaurelius.titan.olap.OLAPTest;

public class LMDBOLAPTest extends OLAPTest {

    @Override
    public WriteConfiguration getConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }

}
