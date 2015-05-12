package ie.corballis.treeway.clean;

import ie.corballis.treeway.AbstractHibernateMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

@Mojo(name = "clean-foreign-keys", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class ForeignKeyCleanerMojo extends AbstractHibernateMojo {

    @Override
    protected void doExecute(Configuration configuration) throws MojoExecutionException {
        StandardServiceRegistry serviceRegistry =
            new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        try {
            Iterator<Table> tableMappings = configuration.getTableMappings();
            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            ConnectionProvider connectionProvider = serviceRegistry.getService(ConnectionProvider.class);
            Connection connection = connectionProvider.getConnection();

            while (tableMappings.hasNext()) {
                Table table = tableMappings.next();
                Iterator foreignKeyIterator = table.getForeignKeyIterator();
                while (foreignKeyIterator.hasNext()) {
                    ForeignKey foreignKey = (ForeignKey) foreignKeyIterator.next();
                    String dropString = foreignKey.sqlDropString(jdbcServices.getDialect(), null, null);
                    dropForeignKey(connection, dropString);
                }
            }

            connection.commit();
            connectionProvider.closeConnection(connection);
        } catch (SQLException e) {
            getLog().error(e.getMessage(), e);
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            ((StandardServiceRegistryImpl) serviceRegistry).destroy();
        }
    }

    private void dropForeignKey(Connection connection, String dropString) throws SQLException {
        getLog().info("Execute: " + dropString);
        Statement statement = connection.createStatement();
        statement.execute(dropString);
        statement.close();
    }

}
