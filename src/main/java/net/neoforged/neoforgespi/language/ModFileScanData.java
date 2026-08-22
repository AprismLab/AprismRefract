package net.neoforged.neoforgespi.language;

import java.util.List;

/**
 * NeoForge SPI shim: per-mod annotation scan result. Under Aprism no scan
 * pass runs; instances only exist so signatures referencing the type resolve.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface ModFileScanData {

    /**
     * @return the annotation data (empty under Aprism)
     */
    List<AnnotationData> getAnnotations();

    /**
     * NeoForge SPI shim: one discovered annotation entry.
     */
    interface AnnotationData {

        /**
         * @return the annotation descriptor
         */
        String annotationType();
    }
}
