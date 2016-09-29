package com.thinkaurelius.titan;

import com.thinkaurelius.titan.diskstorage.configuration.ModifiableConfiguration;
import com.thinkaurelius.titan.diskstorage.configuration.WriteConfiguration;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.*;

public class LMDBStorageSetup extends StorageSetup {

    public static ModifiableConfiguration getLMDBConfiguration(String dir) {
        return buildGraphConfiguration()
                .set(STORAGE_BACKEND,"lmdb")
                .set(STORAGE_DIRECTORY, dir);
    }

    public static ModifiableConfiguration getLMDBConfiguration() {
        return getLMDBConfiguration(getHomeDir("lmdb"));
    }

    public static WriteConfiguration getLMDBGraphConfiguration() {
        return getLMDBConfiguration().getConfiguration();
    }

    public static ModifiableConfiguration getLMDBPerformanceConfiguration() {
        return getLMDBConfiguration()
                .set(STORAGE_TRANSACTIONAL,false)
                .set(TX_CACHE_SIZE,1000);
    }
}
