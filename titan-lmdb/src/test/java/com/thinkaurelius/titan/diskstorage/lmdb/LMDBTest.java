package com.thinkaurelius.titan.diskstorage.lmdb;

import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;
import org.junit.Test;

import static org.fusesource.lmdbjni.Constants.bytes;

/**
 * Created by twilmes on 9/28/16.
 */
public class LMDBTest {

    @Test
    public void testLMDBStuff() {
        Env env = new Env("/tmp/lmdbsnarf");
        Database db = env.openDatabase();
        Transaction tx = env.createReadTransaction();
        Transaction tx2 = env.createTransaction(tx, true);
        db.put(tx2, bytes("howdy"), bytes("there"));
        tx2.commit();
        tx.abort();
        System.out.println(db.get(bytes("howdy")));
    }
}
