package org.opentripplanner.transit.raptor.api.debug;


import org.opentripplanner.model.base.ToStringBuilder;

/**
 * Debug events hold information about an internal event in the Raptor Algorithm. The element
 * may be a stop arrivals, a destination arrival or path.
 *
 * @param <E> the element type.
 */
public class DebugEvent<E> {
    private final Action action;
    private final int iterationStartTime;
    private final E element;
    private final E rejectedDroppedByElement;
    private final String reason;


    /**
     * Private constructor; use static factroy methods to create events.
     */
    private DebugEvent(
            Action action,
            int iterationStartTime,
            E element,
            E rejectedDroppedByElement,
            String reason
    ) {
        this.action = action;
        this.iterationStartTime = iterationStartTime;
        this.element = element;
        this.rejectedDroppedByElement = rejectedDroppedByElement;
        this.reason = reason;
    }

    public static <E> DebugEvent<E> accept(int iterationStartTime, E element) {
        return new DebugEvent<>(Action.ACCEPT, iterationStartTime, element, null, null);
    }

    public static <E> DebugEvent<E> reject(int iterationStartTime, E element, E rejectedByElement, String reason) {
        return new DebugEvent<>(Action.REJECT, iterationStartTime, element, rejectedByElement, reason);
    }

    public static <E> DebugEvent<E> drop(int iterationStartTime, E element, E droppedByElement, String reason) {
        return new DebugEvent<>(Action.DROP, iterationStartTime, element, droppedByElement, reason);
    }

    @Override
    public String toString() {
        return ToStringBuilder.of(DebugEvent.class)
            .addEnum("action", action)
            .addServiceTime("iterationStartTime", iterationStartTime, -9_999)
            .addObj("element", element)
            .addObj("rejectedDroppedByElement", rejectedDroppedByElement)
            .addStr("reason", reason)
            .toString();
    }

    /** The event action type */
    public enum Action {
        /** Element is accepted */
        ACCEPT("Accept"),
        /** Element is rejected */
        REJECT("Reject"),
        /**
         * Element is dropped from the algorithm state. Since Range Raptor works in rounds and iterations, an element
         * dropped in a later round/iteration might still make it to the optimal solution. This only means that the
         * element is no longer part of the state.
         */
        DROP("Drop");
        private final String description;

        Action(String description) { this.description = description; }
        @Override
        public String toString() { return description; }
    }
}
