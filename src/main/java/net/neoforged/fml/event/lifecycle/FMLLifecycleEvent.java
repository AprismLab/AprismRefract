package net.neoforged.fml.event.lifecycle;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * NeoForge API shim: base class for mod lifecycle events. Lifecycle events
 * are fired through the mod-scoped {@code IEventBus} during the Aprism
 * lifecycle phases.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class FMLLifecycleEvent extends Event implements IModBusEvent {
}
