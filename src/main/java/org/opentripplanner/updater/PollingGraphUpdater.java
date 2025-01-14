package org.opentripplanner.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class implements logic that is shared between all polling updaters.
 * Usage example ('polling' name is an example and 'polling-updater' should be the type of a
 * concrete class derived from this abstract class):
 * 
 * <pre>
 * polling.type = polling-updater
 * polling.frequencySec = 60
 * </pre>
 * 
 * @see GraphUpdater
 */
public abstract class PollingGraphUpdater implements GraphUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(PollingGraphUpdater.class);

    /**
     * Mirrors GraphUpdater.run method. Only difference is that runPolling will be run multiple
     * times with pauses in between. The length of the pause is defined in the preference
     * frequencySec.
     */
    abstract protected void runPolling() throws Exception;

    /** How long to wait after polling to poll again. */
    protected Integer pollingPeriodSeconds;

    // TODO OTP2 eliminate this field for reasons in "primed" javadoc; also "initialized" is not a clear term.
    protected boolean blockReadinessUntilInitialized;

    /**
     * True when a full batch of realtime data has been fetched and applied to the graph.
     * There was previously a second boolean field that controlled whether this affected "readiness". If we are waiting
     * for any realtime data to be applied, we should wait for all of it to be applied, so I removed that.
     */
    protected boolean primed;

    private final String configRef;


    /** Shared configuration code for all polling graph updaters. */
    public PollingGraphUpdater(PollingGraphUpdaterParameters config) {
        this.pollingPeriodSeconds = config.getFrequencySec();
        this.configRef = config.getConfigRef();
    }

    @Override
    final public void run() {
        try {
            LOG.info("Polling updater started: {}", this);
            while (true) {
                try {
                    // Run concrete polling graph updater's implementation method.
                    runPolling();
                    if (pollingPeriodSeconds <= 0) {
                        // Non-positive polling period values mean to run the updater only once.
                        LOG.info("As requested in configuration, updater {} has run only once and will now stop.",
                                this.getClass().getSimpleName());
                        break;
                    }
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    LOG.error("Error while running polling updater of type {}", configRef, e);
                    // TODO Should we cancel the task? Or after n consecutive failures? cancel();
                } finally {
                    primed = true;
                }
                Thread.sleep(pollingPeriodSeconds * 1000);
            }
        } catch (InterruptedException e) {
            // When updater is interrupted
            LOG.error("Polling updater {} was interrupted and is stopping.", this.getClass().getName());
        }
    }

    public String getConfigRef() {
        return configRef;
    }

}
