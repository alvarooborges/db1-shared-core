package net.hyze.core.shared.dungeon;

import net.hyze.core.shared.group.Group;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@ToString
public class BasicDungeonMap {

    private final String id;

    private final String name;

    private final String[] description;

    private final String config;

    private final boolean enabled;

    public BasicDungeonMap(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getString("id");
        this.name = resultSet.getString("name");
        this.description = resultSet.getString("description").split("\r\n");
        this.config = resultSet.getString("config");
        this.enabled = resultSet.getBoolean("enabled");
    }

}
