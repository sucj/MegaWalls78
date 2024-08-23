package icu.suc.megawalls78.management;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.Skin;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private static final String IDENTITY_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity (uuid CHAR(36) NOT NULL PRIMARY KEY, identity VARCHAR(255) DEFAULT NULL);";
    private static final String RANK_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_rank (uuid CHAR(36) NOT NULL PRIMARY KEY, identity VARCHAR(255) DEFAULT NULL);";
    private static final String ID_COLOR_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_id_color (uuid CHAR(36) NOT NULL, identity VARCHAR(255) NOT NULL, color VARCHAR(255) DEFAULT NULL, PRIMARY KEY (uuid, identity));";
    private static final String ID_SKIN_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_id_skin (uuid CHAR(36) NOT NULL, identity VARCHAR(255) NOT NULL, skin VARCHAR(255) DEFAULT NULL, PRIMARY KEY (uuid, identity));";
    private static final String PATTERN_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_pattern (uuid CHAR(36) NOT NULL, identity VARCHAR(255) NOT NULL, pattern VARCHAR(255) DEFAULT NULL, PRIMARY KEY (uuid, identity));";
    private static final String TRIM_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trim (uuid CHAR(36) NOT NULL, identity VARCHAR(255) NOT NULL, trim VARCHAR(255) DEFAULT NULL, PRIMARY KEY (uuid, identity));";
    private static final String TRIGGER_CREATE = "CREATE TABLE IF NOT EXISTS " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trigger (uuid CHAR(36) NOT NULL, `trigger` VARCHAR(255) NOT NULL, sneak TINYINT(255) DEFAULT NULL, PRIMARY KEY (uuid, `trigger`));";

    private static final String IDENTITY_GET = "SELECT identity FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity WHERE uuid = ?;";
    private static final String IDENTITY_SET = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_identity(uuid, identity) VALUES(?, ?) AS new ON DUPLICATE KEY UPDATE identity = new.identity;";
    private static final String RANK_GET = "SELECT identity FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_rank WHERE uuid = ?;";
    private static final String ID_COLOR_GET = "SELECT color FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_id_color WHERE uuid = ? AND identity = ?;";
    private static final String ID_SKIN_GET = "SELECT skin FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_id_skin WHERE uuid = ? AND identity = ?;";
    private static final String ID_SKIN_SET = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_id_skin(uuid, identity, skin) VALUES(?, ?, ?) AS new ON DUPLICATE KEY UPDATE skin = new.skin;";
    private static final String PATTERN_GET = "SELECT pattern FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_pattern WHERE uuid = ? AND identity = ?;";
    private static final String PATTERN_SET = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_pattern(uuid, identity, pattern) VALUES(?, ?, ?) AS new ON DUPLICATE KEY UPDATE pattern = new.pattern;";
    private static final String PATTERN_SET_NULL = "DELETE FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_pattern WHERE uuid = ? AND identity = ?;";
    private static final String TRIM_GET = "SELECT trim FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trim WHERE uuid = ? AND identity = ?;";
    private static final String TRIM_SET = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trim(uuid, identity, trim) VALUES(?, ?, ?) AS new ON DUPLICATE KEY UPDATE trim = new.trim;";
    private static final String TRIGGER_GET = "SELECT sneak FROM " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trigger WHERE uuid = ? AND `trigger` = ?;";
    private static final String TRIGGER_SET = "INSERT INTO " + MegaWalls78.getInstance().getConfigManager().database + ".mw78_trigger(uuid, `trigger`, sneak) VALUES(?, ?, ?) AS new ON DUPLICATE KEY UPDATE sneak = new.sneak;";

    private static final String IDENTITY_LABEL = "identity";
    private static final String COLOR_LABEL = "color";
    private static final String SKIN_LABEL = "skin";
    private static final String PATTERN_LABEL = "pattern";
    private static final String TRIM_LABEL = "trim";
    private static final String TRIGGER_LABEL = "sneak";

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

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void init() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(IDENTITY_CREATE);
            statement.executeUpdate(RANK_CREATE);
            statement.executeUpdate(ID_COLOR_CREATE);
            statement.executeUpdate(ID_SKIN_CREATE);
            statement.executeUpdate(PATTERN_CREATE);
            statement.executeUpdate(TRIM_CREATE);
            statement.executeUpdate(TRIGGER_CREATE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<String> getPlayerIdentity(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(IDENTITY_GET)) {
                statement.setString(1, player.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(IDENTITY_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public void setPlayerIdentity(UUID player, Identity identity) {
        CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(IDENTITY_SET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<String> getRankedIdentity(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(RANK_GET)) {
                statement.setString(1, player.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(IDENTITY_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<String> getIdentityColor(UUID player, Identity identity) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(ID_COLOR_GET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(COLOR_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<String> getIdentitySkin(UUID player, Identity identity) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(ID_SKIN_GET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(SKIN_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public void setIdentitySkin(UUID player, Identity identity, Skin skin) {
        CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(ID_SKIN_SET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                statement.setString(3, skin.id());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<String> getPlayerPattern(UUID player, Identity identity) {
        return CompletableFuture.supplyAsync(() -> {
            String pattern = null;
            try (PreparedStatement statement = connection.prepareStatement(PATTERN_GET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        pattern = resultSet.getString(PATTERN_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return pattern;
        });
    }

    public void setPlayerPattern(UUID player, Identity identity, Pattern pattern) {
        CompletableFuture.supplyAsync(() -> {
            String sql = PATTERN_SET;
            if (pattern == EquipmentManager.PATTERN_NONE) {
                sql = PATTERN_SET_NULL;
            }
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                if (pattern != null) {
                    statement.setString(3, pattern.getPattern().key().value());
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<String> getPlayerTrim(UUID player, Identity identity) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(TRIM_GET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(TRIM_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public void setPlayerTrim(UUID player, Identity identity, TrimPattern trim) {
        CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(TRIM_SET)) {
                statement.setString(1, player.toString());
                statement.setString(2, identity.getId());
                statement.setString(3, trim.key().value());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<Boolean> getTrigger(UUID player, Skill.Trigger trigger) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(TRIGGER_GET)) {
                statement.setString(1, player.toString());
                statement.setString(2, trigger.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(TRIGGER_LABEL);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return false;
        });
    }

    public void setTrigger(UUID player, Skill.Trigger trigger, boolean sneak) {
        CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = connection.prepareStatement(TRIGGER_SET)) {
                statement.setString(1, player.toString());
                statement.setString(2, trigger.getId());
                statement.setBoolean(3, sneak);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
