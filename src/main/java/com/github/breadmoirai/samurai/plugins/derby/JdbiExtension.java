package com.github.breadmoirai.samurai.plugins.derby;

import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.extension.ExtensionCallback;
import org.jdbi.v3.core.extension.ExtensionConsumer;
import org.jdbi.v3.core.extension.NoSuchExtensionException;

import java.sql.SQLException;
import java.util.Optional;

public abstract class JdbiExtension {

    protected final Jdbi jdbi;

    public JdbiExtension(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    protected final <R, X extends Exception> R withHandle(HandleCallback<R, X> callback) throws X {
        return jdbi.withHandle(callback);
    }

    protected final <X extends Exception> void useHandle(HandleConsumer<X> callback) throws X {
        jdbi.useHandle(callback);
    }

    protected final <R, E, X extends Exception> R withExtension(Class<E> extensionType, ExtensionCallback<R, E, X> callback) throws NoSuchExtensionException, X {
        return jdbi.withExtension(extensionType, callback);
    }

    protected final <E, X extends Exception> void useExtension(Class<E> extensionType, ExtensionConsumer<E, X> callback) throws NoSuchExtensionException, X {
        jdbi.useExtension(extensionType, callback);
    }

    protected final <R> Optional<R> selectOnly(Class<R> resultType, String sql, Object... args) {
        return withHandle(handle -> handle.select(sql, args).mapTo(resultType).findFirst());
    }

    protected final void execute(String sql, Object... args) {
        useHandle(handle -> handle.execute(sql, args));
    }

    protected final boolean tableAbsent(String tableName) {
        try {
            return !jdbi.withHandle(handle -> handle.getConnection().getMetaData().getTables(null, "APP", tableName.toUpperCase(), null).next());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
