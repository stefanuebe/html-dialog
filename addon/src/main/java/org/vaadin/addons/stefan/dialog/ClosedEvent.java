package org.vaadin.addons.stefan.dialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

/**
 * Fired, when the {@link Dialog} has been closed.
 */
@DomEvent("closed")
public class ClosedEvent extends ComponentEvent<Dialog> {
    private final boolean closedByEscape;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public ClosedEvent(Dialog source, boolean fromClient, @EventData("event.detail.esc") boolean closedByEscape) {
        super(source, fromClient);
        this.closedByEscape = closedByEscape;
    }

    /**
     * Indicates, if the dialog has been closed using the Escape key.
     * @return closed by escape
     */
    public boolean isClosedByEscape() {
        return closedByEscape;
    }
}