package com.thinkaurelius.titan.diskstorage.lmdb;

import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManagerAdapter;
import com.thinkaurelius.titan.diskstorage.lmdb.LMDBStoreManager;
import com.thinkaurelius.titan.diskstorage.log.KCVSLogTest;


public class LMDBLogTest extends KCVSLogTest {

    public KeyColumnValueStoreManager openStorageManager() throws BackendException {
        LMDBStoreManager sm = new LMDBStoreManager(LMDBStorageSetup.getLMDBConfiguration());
        return new OrderedKeyValueStoreManagerAdapter(sm);
    }

}
