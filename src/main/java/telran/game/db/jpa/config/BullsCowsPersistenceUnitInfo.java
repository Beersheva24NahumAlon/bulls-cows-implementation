package telran.game.db.jpa.config;

import java.net.URL;
import java.util.*;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.*;
import jakarta.persistence.spi.*;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

public class BullsCowsPersistenceUnitInfo implements PersistenceUnitInfo {
    @Override
    public String getPersistenceUnitName() {
        return "bulls-cows-unit";
    }

    @Override
    public String getPersistenceProviderClassName() {
        return "org.hibernate.jpa.HibernatePersistenceProvider";
    }

    @Override
    public String getScopeAnnotationName() {
        return null;
    }

    @Override
    public List<String> getQualifierAnnotationNames() {
        return null;
    }

    @Override
    public DataSource getJtaDataSource() {
        return null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:postgresql://%s:%s/%s".formatted(
            System.getenv("POSTGRES_HOST"), 
            System.getenv("POSTGRES_PORT"),
            System.getenv("POSTGRES_DB")
        ));
		ds.setPassword(System.getenv("POSTGRES_PASSWORD"));
		ds.setUsername("postgres");
		ds.setDriverClassName("org.postgresql.Driver");
		return ds;
    }

    @Override
    public List<String> getMappingFileNames() {
        return null;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return null;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return List.of(
            "telran.queries.entities.Game",
            "telran.queries.entities.Gamer",
            "telran.queries.entities.GameGamer",
            "telran.queries.entities.Move"
        );
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return null;
    }

    @SuppressWarnings("removal")
    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return null;
    }
}