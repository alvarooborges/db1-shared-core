package net.hyze.core.spigot.misc.utils;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum HeadTexture {

    // entities
    ENTITY_BUNNY_KILLER("1f59851de93f4c6547f809ca3aed189e94bbf4f888f1f75208e94c3733852a1"),
    ENTITY_BLAZE("f9341abe33be9a7ecd225e1b24c391a644a4afb45acb981a7021c1242ed4b474"),
    ENTITY_CAVE_SPIDER("298aa4445e9daafd2de9e674d987bc44b4526463b8416e869d9a31822ad383"),
    ENTITY_CHICKEN("55f4a7518da40d0abce39b8ac5f9371c5cd3ffe02873c577b47c397d68ba"),
    ENTITY_COW("b7d776eef2a6f5ff279f2718d984cac5ed3f33d43b25a7c33cc1d13263f184"),
    ENTITY_CREEPER("dc3fe3c55bcb735e63a7666516124f928d8f4323c33675d8498ecd71b51b685"),
    ENTITY_ENDERMAN("fd14f7aaad73aa31d5515a8fb9a286ca1a4fd1bed2688cd7267bf1d8db3a0d"),
    ENTITY_ENDERMITE("1730127e3ac7677122422df0028d9e7368bd157738c8c3cddecc502e896be01c"),
    ENTITY_GHAST("173860d10c674bc55bf3947b51f670b677b368833832c15c30dc51c05584ec"),
    ENTITY_HORSE("42eb967ab94fdd41a6325f1277d6dc019226e5cf34977eee69597fafcf5e"),
    ENTITY_HORSE_ZOMBIE("d22950f2d3efddb18de86f8f55ac518dce73f12a6e0f8636d551d8eb480ceec"),
    ENTITY_HORSE_SKELETON("47effce35132c86ff72bcae77dfbb1d22587e94df3cbc2570ed17cf8973a"),
    ENTITY_IRON_GOLEM("117a42d678a9ba51e24cf6b8ce1e54abc4e1c85fa827bfac5ab603eb93139a6"),
    ENTITY_MAGMA_CUBE("e5b5a86f9b1c87af54c0287788613a8e56ef2b9a2d71d243711751d2566a5134"),
    ENTITY_PIG("1f5d81c8a46239202ed3b5ba171876c15a5c69e228ce2e98184c0de6e592"),
    ENTITY_PIG_ZOMBIE("561af1f4a8a49f124bbe64c32bc8ed4a80854c821235b3a1eb817a526010c0ca"),
    ENTITY_SHEEP("5c67b9bdfd87664f3b422414bf4dd2de4407778cee22c35bbaf77fa985deb"),
    ENTITY_SKELETON("a450d21581210801197336912922d23bb2e9ac3aa13e28ab10bc1786dc448b"),
    ENTITY_SILVERFISH("da91dab8391af5fda54acd2c0b18fbd819b865e1a8f1d623813fa761e924540"),
    ENTITY_SLIME("5aa6f5ef6ff14ac25c3d9f486ffbb54f119d2ff14eca4ca71060ac363d50f7"),
    ENTITY_SPIDER("118c8b1488f3d8c7e6ef396cd3e1bcae308591828ae97f9c147f757998f6054"),
    ENTITY_MOOSHROOM_COW("d0bc61b9757a7b83e03cd2507a2157913c2cf016e7c096a4d6cf1fe1b8db"),
    ENTITY_WITCH("20e13d18474fc94ed55aeb7069566e4687d773dac16f4c3f8722fc95bf9f2dfa"),
    ENTITY_WITHER("31ccfdd845226d409eddc84b120bcf1243eff2476a7e114ae4ba5cdf93"),
    ENTITY_WITHER_SKELETON("ba96e9d76bed30090ce6e2d8425996594eec6d68ac88cf07356e9814834243ec"),
    ENTITY_ZOMBIE("6c57db2646b12222be556ef36dc8bdaa888922f1c2957611ba0103584b4f55c"),
    //alphabet
    QUESTION_MARK_STONE("d23eaefbd581159384274cdbbd576ced82eb72423f2ea887124f9ed33a6872c"),
    // arrows
    ARROW_WHITE_UP("1ad6c81f899a785ecf26be1dc48eae2bcfe777a862390f5785e95bd83bd14d"),
    ARROW_WHITE_DOWN("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"),
    // customs
    FURY_ENCHANTMENT("cdf74e323ed41436965f5c57ddf2815d5332fe999e68fbb9d6cf5c8bd4139f"),
    // colors
    GRAY("6ba0c4a0fdce923a9048328d664147c5b924491f4ee5fea71f3e9ec314"),
    LIME("4b599c618e914c25a37d69f541a22bebbf751615263756f2561fab4cfa39e"),
    HOURGLASS("6fe7d46322477d61d41c18788f5c1afd24ed526eb3ed84127f212e2515b1883"),
    ;

    public static final String TEXTURE_API_URL = "https://textures.minecraft.net/texture/";

    private static final Map<String, ItemStack> TEMP_CACHE = Maps.newHashMap();

    private final String textureKey;
    private final String textureUrl;
    private final ItemStack head;

    HeadTexture(String textureKey) {
        this.textureKey = textureKey;
        this.textureUrl = TEXTURE_API_URL + textureKey;
        this.head = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .skullUrl(this.textureKey)
                .make();
    }

    public ItemStack getHead() {
        return head.clone();
    }

    public static ItemStack getTempHead(String key) {
        if (TEMP_CACHE.containsKey(key)) {
            return TEMP_CACHE.get(key).clone();
        }

        ItemStack head = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .skullUrl(key)
                .make();

        TEMP_CACHE.put(key, head.clone());

        return head.clone();
    }

    public static ItemStack getPlayerHead(String nick) {
        if (TEMP_CACHE.containsKey(nick.toLowerCase())) {
            return TEMP_CACHE.get(nick.toLowerCase()).clone();
        }

        ItemStack head = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .skullOwner(nick)
                .make();

        TEMP_CACHE.put(nick.toLowerCase(), head.clone());

        return head.clone();
    }
}
