package org.vaadin.stefan.dialog;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

/**
 * Fired, when the {@link Dialog} has been opened.
 */
@DomEvent("opened")
public class OpenedEvent extends ComponentEvent<Dialog> {
    private final boolean modal;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public OpenedEvent(Dialog source, boolean fromClient, @EventData("event.detail.modal") boolean modal) {
        super(source, fromClient);
        this.modal = modal;
    }

    /**
     * If the dialog has been opened modal.
     * @return is the dialog modal
     */
    public boolean isModal() {
        return modal;
    }
}