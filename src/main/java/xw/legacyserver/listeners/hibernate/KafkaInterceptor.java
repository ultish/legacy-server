package xw.legacyserver.listeners.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;

import java.io.Serializable;

public class KafkaInterceptor extends EmptyInterceptor {

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws
        CallbackException {
        super.onCollectionRecreate(collection, key);

        System.out.println("collection recreate");
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws
        CallbackException {
        super.onCollectionUpdate(collection, key);

        System.out.println("collection update");

    }
}
