package net.hyze.core.shared.world.materialdata;

import java.util.function.Function;

public interface MaterialDataParser<T> extends Function<SerializedMaterialData, T> {

}
