package net.neoforged.neoforge.event;

import java.util.List;

/**
 * NeoForge API shim: fired when datapacks sync to clients so mods can resend
 * server-dependent data. Under Aprism the class exists so listener
 * registrations resolve; the event is not posted (no vanilla-style datapack
 * sync pipeline is modelled).
 *
 * @author BlockConnect@StarsailsClover
 */
public class OnDatapackSyncEvent {

    /**
     * Returns the player ids the sync targets (empty under Aprism).
     *
     * @return unmodifiable empty list
     */
    public List<java.util.UUID> getRelevantPlayers() {
        return List.of();
    }
}
