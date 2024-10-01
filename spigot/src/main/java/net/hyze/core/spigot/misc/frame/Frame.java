package net.hyze.core.spigot.misc.frame;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.hyze.core.shared.misc.utils.ImageUtils;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

@Getter
public class Frame {

    private UUID id;
    private BufferedImage image;
    private Integer lengthX;
    private Integer lengthY;
    private FrameImageFormat format;
    private Map<FrameRelativeLocation, MapView> mapCollection;
    private Location location;
    private BlockFace blockFace;
    private List<ItemFrame> mapFrames;

    private Consumer<Player> interactConsumer;

    public Frame(BufferedImage image, FrameImageFormat format) {
        init(image, format);
    }

    public Frame(BufferedImage image, String ext) throws IllegalArgumentException, IOException {

        FrameImageFormat format = FrameImageFormat.fromExtension(ext);

        if (ext == null) {
            throw new IllegalArgumentException("Invalid file extension. Only JPEG and PNG are supported.");
        }

        init(image, format);
    }

    public Frame(File file) throws IllegalArgumentException, IOException {

        BufferedImage image = ImageIO.read(file);
        String ext = Files.getFileExtension(file.getName());
        FrameImageFormat format = FrameImageFormat.fromExtension(ext);

        if (ext == null) {
            throw new IllegalArgumentException("Invalid file extension. Only JPEG and PNG are supported.");
        }

        init(image, format);
    }

    public Frame(URL url) throws IOException, IllegalArgumentException {

        BufferedImage image = ImageUtils.getImage(url);
        String extension = url.getFile().substring(url.getFile().length() - 3);
        FrameImageFormat format = FrameImageFormat.fromExtension(extension);

        if (extension == null) {
            throw new IllegalArgumentException("Invalid file extension. Extensions supported: " + Arrays.stream(
                    FrameImageFormat.values())
                    .map(f -> f.getExtension())
                    .collect(Collectors.joining("; "))
            );
        }

        init(image, format);
    }

    private void init(BufferedImage image, FrameImageFormat format) {
        Integer xPanes = FrameUtils.getPanes(image.getWidth());
        Integer yPanes = FrameUtils.getPanes(image.getHeight());

        this.id = UUID.randomUUID();
        this.image = FrameUtils.resize(image, xPanes * 128, yPanes * 128);
        this.lengthX = xPanes;
        this.lengthY = yPanes;
        this.format = format;
        this.mapCollection = Maps.newHashMap();

        loadFrame();
    }

    private void loadFrame() {
        for (int x = 0; x < getLengthX(); x++) {
            for (int y = 0; y < getLengthY(); y++) {
                initFrame(x, y, Bukkit.createMap(FrameUtils.getDefaultWorld()));
            }
        }
    }

    private void initFrame(int x, int y, MapView mapView) {
        BufferedImage subImage = image.getSubimage(x * 128, y * 128, 128, 128);

        mapView.getRenderers().forEach(renderer -> mapView.removeRenderer(renderer));
        mapView.addRenderer(new FrameRenderer(subImage));
        mapCollection.put(new FrameRelativeLocation(x, y), mapView);
    }

    public ItemStack getItem() {
        return getItems().stream().findAny().orElse(null);
    }

    public LinkedHashSet<ItemStack> getItems() {
        LinkedHashSet<ItemStack> items = Sets.newLinkedHashSet();

        for (int y = 0; y < getLengthY(); y++) {
            for (int x = 0; x < getLengthX(); x++) {
                for (FrameRelativeLocation loc : getMapCollection().keySet()) {

                    if (loc.getX() != x || loc.getY() != y) {
                        continue;
                    }

                    ItemBuilder itemCustom = new ItemBuilder(new ItemStack(Material.MAP, 1));

                    itemCustom.durability(FrameUtils.getMapID(getMapCollection().get(loc)));
                    itemCustom.name(" X: " + (x + 1) + " Y: " + (y + 1));
                    itemCustom.lore(UUID.randomUUID().toString());

                    items.add(itemCustom.make());
                }
            }
        }

        return items;
    }

 /*   public void update() {
        if (this.interactConsumer != null && isPlaced()) {
            Printer.INFO.coloredPrint("&b[Frame API] Update INTERACTABLE_FRAMES [" + getId() + "]");
            FrameManager.INTERACTABLE_FRAMES.put(getId(), this);
        }
        Entry<String, Frame> databaseFrame = FrameManager.DATABASE_FRAMES.entrySet().stream().filter(entry -> {
            return entry.getValue().getId() == getId();
        }).findFirst().orElse(null);
        if (databaseFrame != null) {
            Printer.INFO.coloredPrint("&b[Frame API] Update DATABASE_FRAMES [" + databaseFrame.getKey() + "]");
            FrameManager.DATABASE_FRAMES.put(databaseFrame.getKey(), this);
        }
    }
*/
    public void addInteractListener(Consumer<Player> consumer) {
        this.interactConsumer = consumer;
        if(isPlaced()) {
            FrameManager.INTERACTABLE_FRAMES.put(getId(), this);
        }
    }

    public boolean isPlaced() {
        return this.location != null && this.blockFace != null;
    }

    public void place(Location location, BlockFace blockFace) throws IllegalArgumentException {
        mapFrames = Lists.newArrayList();
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        HashMap<Location, MapView> mapsView = Maps.newHashMap();

        for (Map.Entry<FrameRelativeLocation, MapView> entry : mapCollection.entrySet()) {
            FrameRelativeLocation relLoc = entry.getKey();
            MapView mapView = entry.getValue();
            mapView.setWorld(location.getWorld());

            Location loc = null;

            switch (blockFace) {
                case SOUTH:
                    loc = new Location(world, x + relLoc.getX(), y - relLoc.getY(), z + 1);
                    break;
                case NORTH:
                    loc = new Location(world, x - relLoc.getX(), y - relLoc.getY(), z - 1);
                    break;
                case WEST:
                    loc = new Location(world, x - 1, y - relLoc.getY(), z + relLoc.getX());
                    break;
                case EAST:
                    loc = new Location(world, x + 1, y - relLoc.getY(), z - relLoc.getX());
                    break;
                default:
                    throw new IllegalArgumentException("BlockFace argument error. Use NORTH, SOUTH, EAST or WEST.");
            }

            if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != Material.ITEM_FRAME) {
                throw new IllegalArgumentException("The location is not empty. Location: " + loc.toString());
            }

            mapsView.put(loc, mapView);
        }
        this.location = location;
        this.blockFace = blockFace;
        mapsView.entrySet().stream().forEach(entry -> {
            Location loc = entry.getKey();
            MapView mapView = entry.getValue();
            mapView.setWorld(location.getWorld());

            final Location cloneLoc = loc.clone();

            cloneLoc.getWorld().getEntities().stream()
                    .filter(e -> e.getType() == EntityType.ITEM_FRAME)
                    .forEach(e -> {
                        if (e.getLocation().getBlockX() == cloneLoc.getBlockX()) {
                            if (e.getLocation().getBlockY() == cloneLoc.getBlockY()) {
                                if (e.getLocation().getBlockZ() == cloneLoc.getBlockZ()) {
                                    e.remove();
                                }
                            }
                        }
                    });

            Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {

                if (!loc.getChunk().isLoaded()) {
                    loc.getChunk().load();
                }

                ItemFrame itemFrame = (ItemFrame) world.spawnEntity(loc, EntityType.ITEM_FRAME);
                itemFrame.setFacingDirection(blockFace);

                ItemBuilder itemCustom = new ItemBuilder(Material.MAP);
                itemCustom.durability(mapView.getId());
                itemCustom.lore(UUID.randomUUID().toString());

                itemFrame.setItem(itemCustom.make());
                mapFrames.add(itemFrame);
            }, 20L);
        });
        
        if (this.interactConsumer != null) {
            FrameManager.INTERACTABLE_FRAMES.put(getId(), this);
        }
    }

}
