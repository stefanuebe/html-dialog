package org.vaadin.addons.stefan.dialog;

import com.vaadin.flow.component.*;
import com.vaadin.flow.shared.Registration;

/**
 * Flow integration of the native html {@code <dialog>} element.
 */
@Tag("dialog")
public class Dialog extends Component implements HasSize, HasStyle, HasOrderedComponents {

    private boolean opened;
    private boolean serverSideToBeModal;
    private boolean autoAttached;
    private boolean noCloseOnEsc;

    /**
     * Opens the dialog as a non-modal dialog.
     * <p/>
     * If this instance has not yet been attached to any parent, this method tries to attach it to the current
     * UI (if there is one).
     * <p/>
     * Noop, when the dialog is already open.
     */
    public void show() {
        if (!opened) {
            setup();

            getElement().callJsFunction("show");
            opened = true;
        }
    }

    /**
     * Opens the dialog as a modal dialog. When not deactivated, this method also activates the server side
     * modal state to protect any circumvention of the client side modal state.
     * <p/>
     * If this instance has not yet been attached to any parent, this method tries to attach it to the current
     * UI (if there is one).
     * <p/>
     * Noop, when the dialog is already open.
     */
    public void showModal() {
        if (!opened) {
            setup();

            if (serverSideToBeModal) {
                UI ui = UI.getCurrent();
                if (ui != null) {
                    ui.setChildComponentModal(this, serverSideToBeModal);
                }
            }

            getElement().callJsFunction("showModal");
            opened = true;
        }
    }

    private void setup() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            if (getParent().isEmpty()) {
                ui.add(this);
                autoAttached = true;
            }
        }

        getElement().addEventListener("cancel", event -> {
            onClose();
            this.fireEvent(new ClosedEvent(this, true, true));
        });
    }

    /**
     * Setups all necessary client side scripts. Subclasses need to call super.onAttach to assure expected
     * functionality.
     * @param attachEvent
     *            the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (noCloseOnEsc) {
            setupNoCloseOnEsc();
        }

        getElement().executeJs("""
                const show = this.show;
                const showModal = this.showModal;

                this.show = () => {
                    show.call(this);
                    this.dispatchEvent(new CustomEvent("opened", {detail: {modal: false}}));
                }

                this.showModal = () => {
                    showModal.call(this);
                    this.dispatchEvent(new CustomEvent("opened", {detail: {modal: true}}));
                }
                """);

    }

    private void setupNoCloseOnEsc() {
        getElement().executeJs("""
            if(!this.__preventEscClose) {
                this.__preventEscClose = e => {
                    e.preventDefault();
                    e.stopPropagation();
                    e.stopImmediatePropagation();
                };
                this.addEventListener("cancel", this.__preventEscClose);
            }
            """);
    }

    /**
     * Closes the dialog. Also deactivates the server side modal state. If this instance has been attached automatically
     * to the UI, this method will also detach it automatically.
     * <p/>
     * The closed event fired by this method will be marked as from server.
     */
    public void close() {
        close(false);
    }

    /**
     * Closes the dialog. Also deactivates the server side modal state. If this instance has been attached automatically
     * to the UI, this method will also detach it automatically.
     * <p/>
     * The boolean parameter allows the fired event to be marked as from client. It should only be used inside
     * other event handlers and delegate their {@link ComponentEvent#isFromClient()} value.
     * <p/>
     * This method is provided to circumvent the fact, that the browser cannot fire a proper "close" event in the case,
     * that the dialog element is removed from the dom (for instance in the case of auto attachment).
     *
     */
    public void close(boolean fromClient) {
        opened = false;
        getElement().callJsFunction("close");

        onClose();

        this.fireEvent(new ClosedEvent(this, fromClient, false));
    }

    private void onClose() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.setChildComponentModal(this, false);

            if (autoAttached && getParent().filter(ui::equals).isPresent()) {
                ui.remove(this);
                autoAttached = false;
            }
        }
    }

    /**
     * Activates autofocus for this instance. By default a new dialog has no autofocus activated.
     * @return this instance
     */
    public Dialog withAutofocus() {
        getElement().setAttribute("autofocus", true);
        return this;
    }


    /**
     * Deactivates autofocus for this instance. By default a new dialog has no autofocus activated.
     * @return this instance
     */
    public Dialog withoutAutofocus() {
        getElement().removeAttribute("autofocus");
        return this;
    }

    /**
     * By default opening a modal dialog will also activate Vaadin's server side modality to prevent the client
     * side from circumventing the modal state of the ui. By calling this method, that automatic server side modality
     * will NOT be set. Has to be called before calling {@link #showModal()}.
     *
     * @return this instance
     */
    public Dialog withoutModalServerSide() {
        serverSideToBeModal = false;
        return this;
    }

    /**
     * Reactivates the automatic server side modality when opening the dialog as a modal dialog.
     * See {@link #withoutModalServerSide()} for details. Needs to be called before {@link #showModal()}
     *
     * @return this instance
     */
    public Dialog withModalServerSide() {
        serverSideToBeModal = true;
        return this;
    }

    /**
     * By default a modal dialog can be closed via the Esc key. Calling this method will disable this feature.
     * @return this instance
     */
    public Dialog withoutCloseOnEscape() {
        if (!noCloseOnEsc && isAttached()) { // if not attached, that onAttach will do this
            setupNoCloseOnEsc();
        }
        noCloseOnEsc = true;

        return this;
    }

    /**
     * By default a modal dialog can be closed via the Esc key. If you have disabled this feature, this method will
     * re-enable it.
     * @return this instance
     */
    public Dialog withCloseOnEsc() {
        if (noCloseOnEsc && isAttached()) {
            getElement().executeJs("""
                if(this.__preventEscClose) {
                    this.removeEventListener("cancel", this.__preventEscClose);
                }
                """);
        }
        noCloseOnEsc = false;
        return this;
    }

    /**
     * Registers a listener to be notified, when this instance has been opened. This listener will always
     * be fired from the client side, when the browser opened the dialog.
     * @param listener listener
     * @return registration to remove the listener
     */
    public Registration addOpenedListener(ComponentEventListener<OpenedEvent> listener) {
        return addListener(OpenedEvent.class, listener);
    }

    /**
     * Registers a listener to be notified, when this instance has been closed.
     * @param listener listener
     * @return registration to remove the listener
     */
    public Registration addClosedListener(ComponentEventListener<ClosedEvent> listener) {
        return addListener(ClosedEvent.class, listener);
    }

}