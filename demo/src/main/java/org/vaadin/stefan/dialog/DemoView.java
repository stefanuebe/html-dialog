package org.vaadin.stefan.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoView extends VerticalLayout {

    public DemoView() {

        add(new Button("Show", event -> {
            Dialog dialog = createDialog();
            add(dialog);
            dialog.show();
        }));

        add(new Button("Show modal", event -> {
            Dialog dialog = createDialog();
            add(dialog);
            dialog.showModal();
        }));

        add(new Button("Show (ui attached)", event -> {
            Dialog dialog = createDialog();
            dialog.show();
        }));

        add(new Button("Show modal  (ui attached)", event -> {
            Dialog dialog = createDialog();
            dialog.showModal();
        }));
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog();

        dialog.addOpenedListener(e -> Notification.show("Opened (modal: %s)".formatted(e.isModal())));
        dialog.addClosedListener(e -> Notification.show("Closed (from client: %s, by esc: %s)".formatted(e.isFromClient(), e.isClosedByEscape())));

        Button button = new Button(VaadinIcon.CLOSE.create(), event -> dialog.close(event.isFromClient()));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
        dialog.add(new Span("Hello World"), button);

        return dialog;
    }
}