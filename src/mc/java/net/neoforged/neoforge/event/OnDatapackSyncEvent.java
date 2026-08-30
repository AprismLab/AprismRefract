package net.neoforged.neoforge.event;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.util.List;

import net.minecraft.world.item.crafting.RecipeType;

import net.neoforged.bus.api.Event;

/**
 * NeoForge API shim: fired when datapacks sync to clients so mods can resend
 * server-dependent data (MC-typed variant compiled against a local client
 * jar). Under Aprism the event is not posted; the class and its members
 * exist so listener registrations and call sites link.
 *
 * @author BlockConnect@StarsailsClover
 */
public class OnDatapackSyncEvent extends Event {

    /**
     * Returns the player ids the sync targets (empty under Aprism).
     *
     * @return unmodifiable empty list
     */
    public List<java.util.UUID> getRelevantPlayers() {
        return List.of();
    }

    /**
     * No-op under Aprism: mirrors the real signature used by JEI's client
     * path to request recipe re-sends.
     *
     * @param recipeTypes the recipe types to re-send
     */
    public void sendRecipes(RecipeType<?>[] recipeTypes) {
        // no-op under Aprism
    }
}
