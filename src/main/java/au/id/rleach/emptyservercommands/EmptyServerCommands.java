package au.id.rleach.emptyservercommands;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeParseException;

@Plugin(
        id = "emptyservercommands",
        name = "EmptyServerCommands",
        version = "1.0.0",
        description = "Run's a set of commands when the server has been inactive.",
        authors = {
                "ryantheleach"
        }
)
public class EmptyServerCommands {

    @Inject private PluginContainer container;
    @Inject private Logger logger;
    @Inject @ConfigDir(sharedRoot = true)
    private Path configDir;
    private EmptyServerCommandsConfig config;
    private PlayerListener playerListener;


    @Listener
    public void onServerStart(final GameStartedServerEvent event) {
        this.setup();
    }

    static {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Duration.class), new ESCDurationSerializer());
    }

    private void setup(){
        try {
            final CommentedConfigurationNode configNode = ConfigLoader.loadConfigUnchecked("emptyservercommands.conf", this.configDir, this.container);
            this.config = configNode.getValue(EmptyServerCommandsConfig.TYPE);
            this.playerListener = new PlayerListener(
                    this.config.playerCount,
                    this.config.duration,
                    this.config.commands,
                    this.config.waitForJoin,
                    Sponge.getScheduler().createSyncExecutor(this)
            );
            Sponge.getEventManager().registerListeners(this, this.playerListener);
        } catch (ObjectMappingException e) {
            this.logger.error(String.valueOf(e));
        }
    }


    private static class ESCDurationSerializer  implements TypeSerializer<Duration> {

        @Override public Duration deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
            try {
                return Duration.parse(value.getString());
            } catch (final DateTimeParseException e){
                throw new ObjectMappingException(e);
            }

        }

        @Override public void serialize(TypeToken<?> type, Duration obj, ConfigurationNode value) throws ObjectMappingException {
            value.setValue(obj.toString());
        }
    }
}