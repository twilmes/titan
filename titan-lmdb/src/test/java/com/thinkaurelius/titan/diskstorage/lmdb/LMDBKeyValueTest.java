package com.thinkaurelius.titan.diskstorage.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.KeyValueStoreTest;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManager;
import com.thinkaurelius.titan.diskstorage.lmdb.LMDBStoreManager;


public class LMDBKeyValueTest extends KeyValueStoreTest {

    @Override
    public OrderedKeyValueStoreManager openStorageManager() throws BackendException {
        return new LMDBStoreManager(LMDBStorageSetup.getLMDBConfiguration());
    }

}
