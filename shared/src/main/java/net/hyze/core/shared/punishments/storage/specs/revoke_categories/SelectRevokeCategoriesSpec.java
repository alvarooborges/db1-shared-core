package net.hyze.core.shared.punishments.storage.specs.revoke_categories;

import net.hyze.core.shared.punishments.storage.specs.categories.*;
import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.punishments.PunishmentLevel;
import net.hyze.core.shared.punishments.PunishmentRevokeCategory;
import net.hyze.core.shared.punishments.PunishmentType;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;

@RequiredArgsConstructor
public abstract class SelectRevokeCategoriesSpec extends SelectSqlSpec<Set<PunishmentRevokeCategory>> {

    @Override
    public ResultSetExtractor<Set<PunishmentRevokeCategory>> getResultSetExtractor() {

        return (ResultSet result) -> {

            Set<PunishmentRevokeCategory> output = Sets.newHashSet();

            while (result.next()) {

                Integer id = result.getInt("id");
                String name = result.getString("name");
                String displayName = result.getString("display_name");
                Group group = Enums.getIfPresent(Group.class, result.getString("group_id")).or(Group.GAME_MASTER);
                String[] description = parseDescription(result.getString("description"));
                boolean enabled = result.getBoolean("enabled");
                
                output.add(new PunishmentRevokeCategory(id, name, displayName, group, description, enabled));

            }

            return output;
        };

    }

    private String[] parseDescription(String rawDescription) {

        List<String> output = Lists.newArrayList();
        JsonElement descriptionJsonElement = new JsonParser().parse(rawDescription);

        if (descriptionJsonElement.isJsonArray()) {
            for (JsonElement jsonElement : descriptionJsonElement.getAsJsonArray()) {
                output.add(jsonElement.toString());
            }
        }

        return output.toArray(new String[]{});

    }

}
