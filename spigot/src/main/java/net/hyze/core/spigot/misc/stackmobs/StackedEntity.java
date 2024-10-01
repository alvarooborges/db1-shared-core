package net.hyze.core.spigot.misc.stackmobs;

import net.hyze.core.spigot.CoreSpigotPlugin;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class StackedEntity {

    @NonNull
    @Getter
    private final Entity entity;

    public StackedEntity(Entity entity) {
        this.entity = entity;
        StackMobsAPI.setAi(entity);
    }

    public boolean hasStackSizeTag() {
        return entity.hasMetadata(StackMobsAPI.STACK_SIZE_TAG);
    }

    public int getSize() {
        if (!hasStackSizeTag()) {
            return 1;
        }

        return entity.getMetadata(StackMobsAPI.STACK_SIZE_TAG).get(0).asInt();
    }

    public void setSize(int newSize) {
        List<MetadataValue> values = entity.getMetadata(StackMobsAPI.STACK_SIZE_TAG);

        values.forEach(meta -> {
            entity.removeMetadata(StackMobsAPI.STACK_SIZE_TAG, meta.getOwningPlugin());
        });

        entity.setMetadata(StackMobsAPI.STACK_SIZE_TAG, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), newSize));
    }

    public boolean isStackingPrevented() {
        return entity.hasMetadata(StackMobsAPI.PREVENT_STACK_TAG)
                && entity.getMetadata(StackMobsAPI.PREVENT_STACK_TAG).get(0).asBoolean();
    }

    public void setStackingPrevented(boolean value) {
        entity.setMetadata(StackMobsAPI.PREVENT_STACK_TAG, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), value));
    }

    public void setSingleKill(boolean value) {
        entity.setMetadata(StackMobsAPI.SINGLE_KILL_TAG, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), value));
    }

    public boolean isSingleKill() {
        return entity.hasMetadata(StackMobsAPI.SINGLE_KILL_TAG)
                && entity.getMetadata(StackMobsAPI.SINGLE_KILL_TAG).get(0).asBoolean();
    }
}
