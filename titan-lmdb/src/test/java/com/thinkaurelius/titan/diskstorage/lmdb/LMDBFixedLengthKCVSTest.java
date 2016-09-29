package com.thinkaurelius.titan.diskstorage.lmdb;

import com.google.common.collect.ImmutableMap;
import com.thinkaurelius.titan.LMDBStorageSetup;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.KeyColumnValueStoreTest;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManagerAdapter;
import org.junit.Test;

import java.util.concurrent.ExecutionException;


public class LMDBFixedLengthKCVSTest extends KeyColumnValueStoreTest {

    public KeyColumnValueStoreManager openStorageManager() throws BackendException {
        LMDBStoreManager sm = new LMDBStoreManager(LMDBStorageSetup.getLMDBConfiguration());
        return new OrderedKeyValueStoreManagerAdapter(sm, ImmutableMap.of(storeName, 8));
    }

    @Test
    public void testGetKeysWithKeyRange() throws Exception {
        super.testGetKeysWithKeyRange();
    }

    @Test @Override
    public void testConcurrentGetSlice() throws ExecutionException, InterruptedException, BackendException {

    }

    @Test @Override
    public void testConcurrentGetSliceAndMutate() throws BackendException, ExecutionException, InterruptedException {

    }
}
