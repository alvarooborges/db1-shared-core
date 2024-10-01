package net.hyze.core.spigot.misc.playerdata.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.utils.NBTTagCompoundUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.springframework.jdbc.core.PreparedStatementCreator;

@RequiredArgsConstructor
public class InsertOrUpdateUserDataSpec extends UpdateSqlSpec<Void> {

    private final User user;
    private final NBTTagCompound compound;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `nbt`) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE `nbt`= VALUES(`nbt`);",
                    CoreSpigotConstants.Databases.Mysql.Tables.USER_DATA_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, user.getId());
            statement.setString(2, NBTTagCompoundUtils.serialize(compound));

            return statement;
        };
    }
}
