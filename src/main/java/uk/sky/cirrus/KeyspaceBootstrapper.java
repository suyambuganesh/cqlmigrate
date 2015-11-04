package uk.sky.cirrus;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

class KeyspaceBootstrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyspaceBootstrapper.class);

    private final Session session;
    private final String keyspace;
    private final Paths paths;

    public KeyspaceBootstrapper(Session session, String keyspace, Paths paths) {
        this.session = session;
        this.keyspace = keyspace;
        this.paths = paths;
    }

    public void bootstrap() {
        KeyspaceMetadata keyspaceMetadata = session.getCluster().getMetadata().getKeyspace(keyspace);
        if (keyspaceMetadata == null) {
            paths.applyBootstrap(new Paths.Function() {
                @Override
                public void apply(String filename, Path path) {
                    LOGGER.info("Keyspace not found, applying {}", path);

                    FileLoader.loadCql(session, path);

                    LOGGER.info("Applied: bootstrap.cql");
                }
            });
        } else {
            LOGGER.info("Keyspace found, not applying bootstrap.cql");
        }
    }

}