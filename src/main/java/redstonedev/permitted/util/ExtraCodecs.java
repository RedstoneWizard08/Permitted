package redstonedev.permitted.util;

import com.mojang.serialization.Codec;

import java.util.UUID;

public class ExtraCodecs {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
}
