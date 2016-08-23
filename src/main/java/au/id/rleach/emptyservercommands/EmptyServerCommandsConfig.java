package au.id.rleach.emptyservercommands;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;
import java.util.List;

@SuppressWarnings({"InstanceVariableMayNotBeInitialized", "PackageVisibleField"})
@ConfigSerializable
public class EmptyServerCommandsConfig {

    public static final TypeToken<EmptyServerCommandsConfig> TYPE = TypeToken.of(EmptyServerCommandsConfig.class);

    @Setting("player-count")
    int playerCount;

    @Setting("duration")
    Duration duration;

    @Setting("commands")
    List<String> commands;

    @Setting("wait-for-join")
    boolean waitForJoin;
}