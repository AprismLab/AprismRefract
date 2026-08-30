package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: screen interaction events. Marker hierarchy only;
 * Aprism does not hook the screen stack.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class ScreenEvent extends Event {

    /** Fired when a screen opens. */
    public static final class Opening extends ScreenEvent {
    }

    /** Screen init phase events. */
    public static abstract class Init extends ScreenEvent {

        /** Pre-init. */
        public static final class Pre extends Init {
        }

        /** Post-init. */
        public static final class Post extends Init {
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Character typed events. */
    public static abstract class CharacterTyped extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends CharacterTyped {
        }

        /** Post-dispatch. */
        public static final class Post extends CharacterTyped {
        }
    }

    /** Key pressed events. */
    public static abstract class KeyPressed extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends KeyPressed {
        }

        /** Post-dispatch. */
        public static final class Post extends KeyPressed {
        }
    }

    /** Mouse button pressed events. */
    public static abstract class MouseButtonPressed extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends MouseButtonPressed {
        }

        /** Post-dispatch. */
        public static final class Post extends MouseButtonPressed {
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Mouse button released events. */
    public static abstract class MouseButtonReleased extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends MouseButtonReleased {
        }

        /** Post-dispatch. */
        public static final class Post extends MouseButtonReleased {
        }
    }

    /** Mouse dragged events. */
    public static abstract class MouseDragged extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends MouseDragged {
        }
    }

    /** Mouse scrolled events. */
    public static abstract class MouseScrolled extends ScreenEvent {

        /** Pre-dispatch. */
        public static final class Pre extends MouseScrolled {
        }
    }

    /** Screen render events. */
    public static abstract class Render extends ScreenEvent {

        /** Background render phase. */
        public static final class Background extends Render {
        }

        /** Foreground render phase. */
        public static final class Foreground extends Render {
        }
    }

    /** Inventory mob effects render event. */
    public static final class RenderInventoryMobEffects extends ScreenEvent {
    }
}
