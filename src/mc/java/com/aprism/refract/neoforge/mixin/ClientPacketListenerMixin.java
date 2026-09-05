package com.aprism.refract.neoforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover

/**
 * World-join dispatcher for the NeoForge shim bus (v26.9-Alpha.3).
 *
 * <p>Real NeoForge fires {@code ClientPlayerNetworkEvent.LoggingIn} on the
 * GAME bus when the client processes the login/respawn packets. Event-driven
 * client mods gate their in-world startup on exactly this event (JEI's
 * StartEventObserver starts JEI on LoggingIn once the lifecycle setup
 * sequence has completed). Aprism's phase system covers mod lifecycle only -
 * it has no world-join moment - so this mixin supplies the missing dispatch
 * by posting the shim event at the same points real NeoForge would.
 *
 * <p>Compiled in the {@code src/mc/java} source set: the class references
 * vanilla Minecraft client types, so it is only built when a local
 * unobfuscated 26.x client jar is available (compileOnly, never bundled as
 * a dependency; the mixin bytecode itself travels inside the extension jar).
 *
 * @author BlockConnect@StarsailsClover
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    /**
     * Fires LoggingIn after the login packet has been fully processed (the
     * player exists at this point, matching real NeoForge's post-handling
     * dispatch).
     */
    @Inject(method = "handleLogin", at = @At("RETURN"), require = 0)
    private void aprism$postLoggingIn(ClientboundLoginPacket packet, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingIn());
    }

    /**
     * Real NeoForge fires LoggingIn on respawn as well (a fresh networked
     * player session), so parity requires the same dispatch here.
     */
    @Inject(method = "handleRespawn", at = @At("RETURN"), require = 0)
    private void aprism$postLoggingInOnRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingIn());
    }
}
