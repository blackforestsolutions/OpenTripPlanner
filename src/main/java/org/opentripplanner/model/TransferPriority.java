package org.opentripplanner.model;


/**
 * Categorize how suitable a Station or Transfer is it for transfer. The values arrange
 * stops from transfer DISCOURAGED to PREFERRED. First of all this should encourage
 * transfers between two trips/routes to happen at the best possible location if there
 * are multiple stop to choose from. But, it will also apply to score a journey over
 * another one, if one of the journeys score better on the transfers, and they have the
 * same cost.
 */
public enum TransferPriority {
    /**
     * Block transfers from/to this stop. In OTP this is not a definitive block,
     * just a huge penalty is added to the cost function.
     * <p>
     */
    DISCOURAGED,

    /**
     * Allow transfers from/to this stop. This is the default.
     * <p>
     */
    ALLOWED,

    /**
     * Recommended stop place.
     * <p>
     */
    RECOMMENDED,

    /**
     * Preferred place to transfer, strongly recommended.
     * <p>
     */
    PREFERRED;
}
