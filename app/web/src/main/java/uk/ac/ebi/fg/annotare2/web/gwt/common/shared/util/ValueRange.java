package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public abstract class ValueRange<T> implements Serializable {

    ValueRange() {
    }

    public static <V> ValueRange<V> one(V v) {
        if (v == null) {
            throw new NullPointerException("Can't create single value range from null object");
        }
        return new SingletonValue<V>(v);
    }

    @SuppressWarnings("unchecked")
    public static <V> ValueRange<V> any() {
        return (ValueRange<V>) AnyValue.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <V> ValueRange<V> none() {
        return (ValueRange<V>) NoneValue.INSTANCE;
    }

    public abstract boolean isAny();

    public abstract boolean isNone();

    public abstract boolean isSingleton();

    public abstract T get();
}
