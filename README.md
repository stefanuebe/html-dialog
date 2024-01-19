# HTML Dialog

Vaadin Flow integration of the html `<dialog>` element.


## Description
This addon provides a Java api to work with the native html dialog.
It allows you to create a simple dialog, that is mostly handled by the browser.

Also see https://developer.mozilla.org/en-US/docs/Web/API/HTMLDialogElement for additional information.

## Features
* dialog can be shown as normal or modal variant
* support of adding any type of Vaadin components
* Java api for "no close on escape" and "autofocus"
* Java events for "opened" / "closed" events
* Auto attachment to and detachment from the UI, when necessary (see Javadocs on the show methods)
* No additional stylings or contents. No shadow dom or similar.

# Examples
## Creating a simple dialog

```
Dialog dialog = new Dialog();
dialog.add(...); // add some context, e.g. span, buttons, or similar.
```

## Open the dialog in the UI
In this scenario, the dialog is automatically attached to the current UI, when one of the `show` methods are called.
When closed, the dialog is also automatically detached.

```
Dialog dialog = new Dialog();
// add content

dialog.show(); // shows the dialog as a non modal dialog, auto attaches it to the current UI.

// or call instead
dialog.showModal(); // shows the dialog as a modal dialog, auto attaches it to the current UI.

someButton.addClickListener(event -> dialog.close()); // closes the dialog and detaches it from the UI
```

## Open the dialog "inside" another component
In this scenario, the dialog is manually attached to a container (e.g. the current view). It is not detached
automatically - this has to be done by you. Please be advised, that the dialog is only detached automatically,
when it's containing element is detached.

```

Dialog dialog = new Dialog();
// add content

container.add(dialog);

dialog.show(); // shows the dialog as a non modal dialog, attached to the "container".

// or call instead
dialog.showModal(); // shows the dialog as a modal dialog, attached to the "container".

someButton.addClickListener(event -> dialog.close()); // just closes the dialog, does NOT automatically detach it
```

## No close on escape
By default a modal dialog is closed, when the user presses the "Escape" key. This can be prevented by using the
`withoutCloseOnEsc` method. Non modal dialogs never close on escape, so in that case, calling that method is not
necessary.

```
dialog.withoutCloseOnEscape().showModal(); // needs to be closed using some interactive element, e.g. a button
```

## Autofocus on open
To have a better UX, you may autofocus the dialog, when shown.

```
dialog.withAutofocus().show(); // or showModal();
```

## Server side modality
Vaadin provides a mechanism to prevent the client from circumventing the modality by blocking any requests for
components, that are not a child of the current modal container. This mechanism is also used for the dialog, when
shown modal.

If you need or want to disable this mechanism, you can do that by easily call `withoutModalServerSide` before
showing the dialog. It still will be shown modal in the client, but Vaadin will not block any events for
components outside the modal container.

```
dialog.withoutModalServerSide().showModal();
```

## Event handling
The component provides two event types to register for.

```
dialog.addOpenedListener(event ->
        Notification.show("Dialog has been opened " + (event.isModal() ? "modal" : "modeless")));

        dialog.addClosedListener(event ->
        Notification.show("Dialog has been closed " + (event.isClosedByEscape() ? "by the Escape key" : "somehow")));
```


Technical side note: These events are not the native browser events, since the
dialog element does not provide an "open" event and the "close" event will not be always fired correctly (for
instance when the component is detached from the dom).

Also, the opened event will always be triggered by the client side. The close event itself may be triggered
by the client or server side. Due to the manual handling of the close event, there is an additional `close` method,
that allows to simulate a client side close for the sake of the event source. However, that method should
only be called by other event handlers.

```
// calling Dialog#close(boolean) allows setting the isFromClient flag for the ClosedEvent
new Button("Close", event -> dialog.close(event.isFromClient()));
```