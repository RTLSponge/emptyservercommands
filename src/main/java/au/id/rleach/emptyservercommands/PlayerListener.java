package au.id.rleach.emptyservercommands;

import com.google.common.collect.Lists;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerListener {
    //configuration
    private final List<String> commands;
    private final Duration duration;
    private final int triggerAmount;
    private final boolean waitForJoin;
    private final SpongeExecutorService executor;
    //mutable fields
    private boolean started;
    private SpongeExecutorService.SpongeFuture<?> future;
    private final Runnable runCommands;

    public PlayerListener(final int triggerAmount, final Duration duration, final List<String> commands, final boolean waitForJoin,
            final SpongeExecutorService syncExecutor) {
        this.duration = duration;
        this.commands = Lists.newArrayList(commands);
        this.triggerAmount = triggerAmount;
        this.waitForJoin = waitForJoin;
        this.started = waitForJoin;
        this.executor = syncExecutor;

        this.runCommands = ()->{
            final ConsoleSource source = Sponge.getServer().getConsole();
            for(final String command:this.commands){
                Sponge.getCommandManager().process(source, command);
            }
        };

        if(!waitForJoin)
            this.startCountdown();
    }

    @Listener public void onClientConnection(final ClientConnectionEvent.Join event) {
        //already in
        final int playerCount = Sponge.getServer().getOnlinePlayers().size();
        if(this.triggerAmount < playerCount) this.cancelCountdown();
    }

    @Listener public void onClientConnection(final ClientConnectionEvent.Disconnect event) {
        //not yet gone.
        final int futureCount = Sponge.getServer().getOnlinePlayers().size() - 1;
        if(this.triggerAmount >=futureCount){
            this.startCountdown();
        }
    }

    private void startCountdown() {
        if(!this.started){
            this.future = this.executor.schedule(this.runCommands, this.duration.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
            this.started = true;
        }
    }

    private void cancelCountdown() {
        if(this.started) {
            this.future.cancel(false);
            this.started = false;
        }
    }
}
