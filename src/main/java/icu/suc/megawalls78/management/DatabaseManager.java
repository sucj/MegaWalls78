package icu.suc.megawalls78.management;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private static final String IDENTITY_GET_QUERY = "SELECT identity FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity WHERE uuid = ?";
    private static final String IDENTITY_SET_QUERY = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity(uuid, identity) VALUES(?, ?) AS new ON DUPLICATE KEY UPDATE identity = new.identity;";

    private static final String IDENTITY_LABEL = "identity";

    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void init() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity (uuid CHAR(36) NOT NULL PRIMARY KEY, identity VARCHAR(255) DEFAULT NULL);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<String> getPlayerIdentity(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            String identity = null;
            try (PreparedStatement statement = connection.prepareStatement(IDENTITY_GET_QUERY)) {
                statement.setString(1, player.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        identity = resultSet.getString(IDENTITY_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return identity;
        });
    }

    public void setPlayerIdentity(UUID player, Identity identity) {
        CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(IDENTITY_SET_QUERY)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
