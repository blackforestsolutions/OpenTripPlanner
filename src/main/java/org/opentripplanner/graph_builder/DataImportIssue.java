package org.opentripplanner.graph_builder;


/**
 * Represents noteworthy data import issues that occur during the graph building process. These
 * issues should be passed on the the {@link org.opentripplanner.graph_builder.DataImportIssueStore}
 * fwitch will be responsible for logging, summarizing and reporting the issue.
 *
 * Do NOT log the issue in the class where the issue is detected/created.
 *
 * @author andrewbyrd
 */
public interface DataImportIssue {

    /**
     * Provide a detailed message, including enough data to be able to fix the problem (in the
     * source system).
     */
    String getMessage();
}
