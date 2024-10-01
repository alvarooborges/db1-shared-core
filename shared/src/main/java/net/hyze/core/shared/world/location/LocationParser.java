package net.hyze.core.shared.world.location;

import java.util.function.Function;

public interface LocationParser<T> extends Function<SerializedLocation, T> {

}
