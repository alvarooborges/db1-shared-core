package net.hyze.core.spigot.misc.playerdata.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.utils.NBTTagCompoundUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public class SelectUserDataSpec extends SelectSqlSpec<NBTTagCompound> {

    private final User user;

    @Override
    public ResultSetExtractor<NBTTagCompound> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                if (result.getString("nbt") != null) {
                    return NBTTagCompoundUtils.deserialize(result.getString("nbt"));
                }
            }

            return null;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    CoreSpigotConstants.Databases.Mysql.Tables.USER_DATA_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, user.getId());

            return statement;
        };
    }
}
