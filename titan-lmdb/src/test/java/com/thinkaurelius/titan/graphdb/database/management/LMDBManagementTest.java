package com.thinkaurelius.titan.graphdb.database.management;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;

public class LMDBManagementTest extends ManagementTest {
    @Override
    public WriteConfiguration getConfiguration() {
        return LMDBStorageSetup.getLMDBGraphConfiguration();
    }
}
