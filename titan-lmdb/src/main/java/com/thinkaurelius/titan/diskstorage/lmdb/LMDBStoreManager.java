package com.thinkaurelius.titan.diskstorage.lmdb;

import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.BaseTransactionConfig;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.common.LocalStoreManager;
import com.thinkaurelius.titan.diskstorage.configuration.ConfigNamespace;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.configuration.MergedConfiguration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyRange;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StandardStoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreFeatures;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.KVMutation;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.KeyValueEntry;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManager;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.thinkaurelius.titan.util.system.IOUtils;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.thinkaurelius.titan.diskstorage.configuration.ConfigOption.disallowEmpty;

/**
 * Created by twilmes on 9/26/16.
 */
public class LMDBStoreManager extends LocalStoreManager implements OrderedKeyValueStoreManager {

    private static final Logger log = LoggerFactory.getLogger(LMDBStoreManager.class);

    public static final ConfigNamespace LMDB_NS =
            new ConfigNamespace(GraphDatabaseConfiguration.STORAGE_NS, "lmdb", "LMDB configuration options");

    private final Map<String, LMDBKeyValueStore> stores;

    protected Env environment;
    protected final StoreFeatures features;

    public LMDBStoreManager(Configuration configuration) throws BackendException {
        super(configuration);
        stores = new HashMap();

        initialize();

        features = new StandardStoreFeatures.Builder()
                .orderedScan(true)
                .transactional(transactional)
                .keyConsistent(GraphDatabaseConfiguration.buildGraphConfiguration())
                .locking(true)
                .keyOrdered(true)
                .scanTxConfig(GraphDatabaseConfiguration.buildGraphConfiguration())
                .supportsInterruption(false)
                .build();

//        features = new StoreFeatures();
//        features.supportsOrderedScan = true;
//        features.supportsUnorderedScan = false;
//        features.supportsBatchMutation = false;
//        features.supportsTxIsolation = transactional;
//        features.supportsConsistentKeyOperations = true;
//        features.supportsLocking = true;
//        features.isKeyOrdered = true;
//        features.isDistributed = false;
//        features.hasLocalKeyPartition = false;
//        features.supportsMultiQuery = false;
    }

    private void initialize() throws BackendException {
//            environment = new Environment(directory, envConfig);
        environment = new Env();
        environment.setMaxDbs(10L);
        environment.setMapSize(1L * 1024L * 1024L * 1024L);
        environment.open(directory.getAbsolutePath());

//
//        if(db == null) {
//            throw new PermanentBackendException("Error during LMDB initialization: ");
//        }
    }

    @Override
    public StoreFeatures getFeatures() {
        return features;
    }

    @Override
    public List<KeyRange> getLocalKeyPartition() throws BackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LMDBTx beginTransaction(final BaseTransactionConfig txCfg) throws BackendException {
//        try {
            Transaction tx = null;

            Configuration effectiveCfg =
                    new MergedConfiguration(txCfg.getCustomOptions(), getStorageConfig());

            if (transactional) {
//                TransactionConfig txnConfig = new TransactionConfig();
//                ConfigOption.getEnumValue(effectiveCfg.get(ISOLATION_LEVEL),BerkeleyJEStoreManager.IsolationLevel.class).configure(txnConfig);
//                tx = environment.beginTransaction(null, txnConfig);
//                tx = environment.createWriteTransaction();
                tx = environment.createReadTransaction();
                System.out.println(Thread.currentThread() + " Created " + tx);
//                tx = environment.createReadTransaction();

            }
            LMDBTx lmdbTx = new LMDBTx(tx, txCfg);

            if (log.isTraceEnabled()) {
                log.trace("LMDB tx created", new LMDBStoreManager.TransactionBegin(lmdbTx.toString()));
            }

            return lmdbTx;
//        } catch (DatabaseException e) {
//            throw new PermanentBackendException("Could not start BerkeleyJE transaction", e);
//        }
    }

    @Override
    public LMDBKeyValueStore openDatabase(String name) throws BackendException {
        Preconditions.checkNotNull(name);
        if (stores.containsKey(name)) {
            LMDBKeyValueStore store = stores.get(name);
            return store;
        }
//        try {
//            DatabaseConfig dbConfig = new DatabaseConfig();
//            dbConfig.setReadOnly(false);
//            dbConfig.setAllowCreate(true);
//            dbConfig.setTransactional(transactional);
//
//            dbConfig.setKeyPrefixing(true);
//
//            if (batchLoading) {
//                dbConfig.setDeferredWrite(true);
//            }
            Database db = environment.openDatabase(name);

            log.debug("Opened database {}", name, new Throwable());

            LMDBKeyValueStore store = new LMDBKeyValueStore(name, db, this);
            stores.put(name, store);
            return store;
//        } catch (DatabaseException e) {
//            throw new PermanentBackendException("Could not open BerkeleyJE data store", e);
//        }
    }

    @Override
    public void mutateMany(Map<String, KVMutation> mutations, StoreTransaction txh) throws BackendException {
        for (Map.Entry<String,KVMutation> muts : mutations.entrySet()) {
            LMDBKeyValueStore store = openDatabase(muts.getKey());
            KVMutation mut = muts.getValue();

            if (!mut.hasAdditions() && !mut.hasDeletions()) {
                log.debug("Empty mutation set for {}, doing nothing", muts.getKey());
            } else {
                log.debug("Mutating {}", muts.getKey());
            }

            if (mut.hasAdditions()) {
                for (KeyValueEntry entry : mut.getAdditions()) {
                    store.insert(entry.getKey(),entry.getValue(),txh);
                    log.trace("Insertion on {}: {}", muts.getKey(), entry);
                }
            }
            if (mut.hasDeletions()) {
                for (StaticBuffer del : mut.getDeletions()) {
                    store.delete(del,txh);
                    log.trace("Deletion on {}: {}", muts.getKey(), del);
                }
            }
        }
    }

    void removeDatabase(LMDBKeyValueStore db) {
        if (!stores.containsKey(db.getName())) {
            throw new IllegalArgumentException("Tried to remove an unkown database from the storage manager");
        }
        String name = db.getName();
        stores.remove(name);
        log.debug("Removed database {}", name);
    }


    @Override
    public void close() throws BackendException {
        if (environment != null) {
            if (!stores.isEmpty())
                throw new IllegalStateException("Cannot shutdown manager since some databases are still open");
            try {
                // TODO this looks like a race condition
                //Wait just a little bit before closing so that independent transaction threads can clean up.
                Thread.sleep(30);
            } catch (InterruptedException e) {
                //Ignore
            }
//            try {
                environment.close();
//            } catch (DatabaseException e) {
//                throw new PermanentBackendException("Could not close BerkeleyJE database", e);
//            }
        }

    }

    @Override
    public void clearStorage() throws BackendException {
        if (!stores.isEmpty())
            throw new IllegalStateException("Cannot delete store, since database is open: " + stores.keySet().toString());

//        for(LMDBKeyValueStore store : stores.values()) {
//
//        }
//        Transaction tx = null;
//        for (String db : environment) {
//            environment.removeDatabase(tx, db);
//            log.debug("Removed database {} (clearStorage)", db);
//        }
        environment.close();
        close();
        IOUtils.deleteFromDirectory(directory);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + ":" + directory.toString();
    }

    private static class TransactionBegin extends Exception {
        private static final long serialVersionUID = 1L;

        private TransactionBegin(String msg) {
            super(msg);
        }
    }
}
