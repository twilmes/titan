package com.thinkaurelius.titan.diskstorage.lmdb;

import com.google.common.base.Preconditions;
import com.sleepycat.je.OperationStatus;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.PermanentBackendException;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.KVQuery;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.KeySelector;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.KeyValueEntry;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStore;
import com.thinkaurelius.titan.diskstorage.util.RecordIterator;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayBuffer;
import org.fusesource.lmdbjni.Cursor;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Entry;
import org.fusesource.lmdbjni.EntryIterator;
import org.fusesource.lmdbjni.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by twilmes on 9/26/16.
 */
public class LMDBKeyValueStore implements OrderedKeyValueStore {

    private static final Logger log = LoggerFactory.getLogger(LMDBKeyValueStore.class);

    private static final StaticBuffer.Factory<byte[]> ENTRY_FACTORY = new StaticBuffer.Factory<byte[]>() {
        @Override
        public byte[] get(byte[] array, int offset, int limit) {
            return Arrays.copyOfRange(array, offset, limit);
        }
    };


    private final Database db;
    private final String name;
    private final LMDBStoreManager manager;
    private boolean isOpen;

    public LMDBKeyValueStore(String n, Database data, LMDBStoreManager m) {
        db = data;
        name = n;
        manager = m;
        isOpen = true;
    }

//    public DatabaseConfig getConfiguration() throws BackendException {
//        try {
//            return db.getConfig();
//        } catch (DatabaseException e) {
//            throw new PermanentBackendException(e);
//        }
//    }

    @Override
    public String getName() {
        return name;
    }

    private static final Transaction getTransaction(StoreTransaction txh) {
        Preconditions.checkArgument(txh!=null);
        return ((LMDBTx) txh).getTransaction();
    }

    @Override
    public synchronized void close() throws BackendException {
        try {
            if(isOpen) db.close();
        } catch (Exception e) {
            throw new PermanentBackendException(e);
        }
        if (isOpen) manager.removeDatabase(this);
        isOpen = false;
    }

    @Override
    public StaticBuffer get(StaticBuffer key, StoreTransaction txh) throws BackendException {
        Transaction tx = getTransaction(txh);
        try {
//            DatabaseEntry dbkey = key.as(ENTRY_FACTORY);
//            DatabaseEntry data = new DatabaseEntry();

            log.trace("db={}, op=get, tx={}", name, txh);

            byte[] result = db.get(tx, key.asByteBuffer().array());

//            OperationStatus status = db.get(tx, dbkey, data, getLockMode(txh));

//            if (status == OperationStatus.SUCCESS) {
//                return getBuffer(data);
//            } else {
//                return null;
//            }
            if(result == null) {
                return null;
            }

            return new StaticArrayBuffer(result);
        } catch (Exception e) {
            throw new PermanentBackendException(e);
        }
    }

    @Override
    public boolean containsKey(StaticBuffer key, StoreTransaction txh) throws BackendException {
        return get(key,txh)!=null;
    }

    @Override
    public void acquireLock(StaticBuffer key, StaticBuffer expectedValue, StoreTransaction txh) throws BackendException {
        if (getTransaction(txh) == null) {
            log.warn("Attempt to acquire lock with transactions disabled");
        } //else we need no locking
    }

    @Override
    public RecordIterator<KeyValueEntry> getSlice(KVQuery query, StoreTransaction txh) throws BackendException {
        log.trace("beginning db={}, op=getSlice, tx={}", name, txh);
        Transaction tx = getTransaction(txh);
        Cursor cursor = null;
        final StaticBuffer keyStart = query.getStart();
        final StaticBuffer keyEnd = query.getEnd();
        final KeySelector selector = query.getKeySelector();
        final List<KeyValueEntry> result = new ArrayList<KeyValueEntry>();
        try {
//            DatabaseEntry foundKey = keyStart.as(ENTRY_FACTORY);
//            DatabaseEntry foundData = new DatabaseEntry();

            byte[] foundKey = keyStart.asByteBuffer().array();
//            byte[] foundKey = keyStart.as(ENTRY_FACTORY);

//            foundKey = keyStart.

            //cursor = db.openCursor(tx);
//            OperationStatus status = cursor.getSearchKeyRange(foundKey, foundData, getLockMode(txh));
            EntryIterator valueIterator = db.seek(tx, foundKey);

            //Iterate until given condition is satisfied or end of records
            while(valueIterator.hasNext()) {
                Entry entry = valueIterator.next();
                StaticBuffer key = getBuffer(entry.getKey());
                byte[] value = entry.getValue();

                if (key.compareTo(keyEnd) >= 0)
                    break;

                if (selector.include(key))
                    result.add(new KeyValueEntry(key, getBuffer(value)));


                if (selector.reachedLimit())
                    break;
            }


            //Iterate until given condition is satisfied or end of records
//            while (status == OperationStatus.SUCCESS) {
//                StaticBuffer key = getBuffer(foundKey);
//
//                if (key.compareTo(keyEnd) >= 0)
//                    break;
//
//                if (selector.include(key)) {
//                    result.add(new KeyValueEntry(key, getBuffer(foundData)));
//                }
//
//                if (selector.reachedLimit())
//                    break;
//
//                status = cursor.getNext(foundKey, foundData, getLockMode(txh));
//            }
            log.trace("db={}, op=getSlice, tx={}, resultcount={}", name, txh, result.size());
//            log.trace("db={}, op=getSlice, tx={}, resultcount={}", name, txh, result.size(), new Throwable("getSlice trace"));

            return new RecordIterator<KeyValueEntry>() {
                private final Iterator<KeyValueEntry> entries = result.iterator();

                @Override
                public boolean hasNext() {
                    return entries.hasNext();
                }

                @Override
                public KeyValueEntry next() {
                    return entries.next();
                }

                @Override
                public void close() {
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } catch (Exception e) {
            throw new PermanentBackendException(e);
        } finally {
            try {
                if (cursor != null) cursor.close();
            } catch (Exception e) {
                throw new PermanentBackendException(e);
            }
        }
    }

    @Override
    public Map<KVQuery,RecordIterator<KeyValueEntry>> getSlices(List<KVQuery> queries, StoreTransaction txh) throws BackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(StaticBuffer key, StaticBuffer value, StoreTransaction txh) throws BackendException {
        insert(key, value, txh, true);
    }

    public void insert(StaticBuffer key, StaticBuffer value, StoreTransaction txh, boolean allowOverwrite) throws BackendException {
        Transaction tx = getTransaction(txh);

        try {
            byte[] status;

            log.trace("db={}, op=insert, tx={}", name, txh);

            if (allowOverwrite)
                status = db.put(tx, key.as(ENTRY_FACTORY), value.as(ENTRY_FACTORY));
            else
                status = db.put(tx, key.as(ENTRY_FACTORY), value.as(ENTRY_FACTORY));
            // todo overwrite? hmmm
//                status = db.putputNoOverwrite(tx, key.as(ENTRY_FACTORY), value.as(ENTRY_FACTORY));

            if (status != null) {
//                if (status == OperationStatus.KEYEXIST) {
//                    throw new PermanentBackendException("Key already exists on no-overwrite.");
//                } else {
                    throw new PermanentBackendException("Could not write entity, return status: " + status);
//                }
            }
        } catch (Exception e) {
            throw new PermanentBackendException(e);
        }
    }


    @Override
    public void delete(StaticBuffer key, StoreTransaction txh) throws BackendException {
        log.trace("Deletion");
        Transaction tx = getTransaction(txh);
        try {
            log.trace("db={}, op=delete, tx={}", name, txh);
            boolean status = db.delete(tx, key.asByteBuffer().array());
            if (status != true) {
                throw new PermanentBackendException("Could not remove: " + status);
            }
        } catch (Exception e) {
            throw new PermanentBackendException(e);
        }
    }

    private static StaticBuffer getBuffer(byte[] value) {
        return new StaticArrayBuffer(value);
    }

//    private static LockMode getLockMode(StoreTransaction txh) {
//        return ((BerkeleyJETx)txh).getLockMode();
//    }
}
