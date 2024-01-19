# HTML Dialog

Vaadin Flow integration of the html '<dialog>' element.

## Description

This addon provides a low-level api to work with an html dialog. 
It allows you to create a simple dialog, that is mostly handled by the browser. 


## Features
* dialog can be shown as normal or modal variant
* support of adding any type of Vaadin components
* Java api for "no close on escape" and "autofocus"
* Java events for "opened" / "closed" events
* Auto attachment to and detachment from the UI, when necessary (see Javadocs on the show methods)
* No additional stylings or contents. No shadow dom or similar.

# Exapmles
## Creating a simple dialog
```
Table table = new Table();

TableRow headerRow = table.addRow();
headerRow.addHeaderCell().setText("Hello");
headerRow.addHeaderCell().setText("World");

TableRow detailsRow = table.addRow();
detailsRow.addDataCell().setText("Hello");
detailsRow.addDataCell().setText("World");
```

## Applying colspan and rowspan to cells
```
Table table = new Table();

TableRow headerRow = table.addRow();
TableCell cell = headerRow.addHeaderCell();
cell.setText("Hello world, it's me.");
cell.setColSpan(3);
cell.getStyle().set("background-color", "#fdd");

TableRow detailsRow = table.addRow();
detailsRow.addDataCell().setText("Hello");
detailsRow.addDataCell().setText("World");

cell = detailsRow.addDataCell();
cell.setText("It's me.");
cell.setRowSpan(2);
cell.getStyle().set("background-color", "#dfd");

detailsRow = table.addRow();
cell = detailsRow.addDataCell();
cell.setText("Hello");
cell.setColSpan(2);
cell.getStyle().set("background-color", "#ddf");

table.getCaption().setText("Using col- and rowspan");

add(table);
```

## Using a caption element
```
Table table = new Table();

table.getCaption().setText("Some caption"); 

// caption also supports components
table.getCaption().add(new Image(...));
```

## Structuring the table
```
// Table takes care to order the elements on the client side as required by html specifications

Table table = new Table();

TableRow detailsRow = table.getBody().addRow();
detailsRow.addDataCell().setText("Hello");
detailsRow.addDataCell().setText("World");
add(table);

TableHead head = table.getHead(); // will be put before tbody in the client side dom
TableRow headerRow = head.addRow();
headerRow.addHeaderCell().setText("Hello");
headerRow.addHeaderCell().setText("World");

table.getCaption().setText("Using thead and tbody"); // will be put before thead in the client side dom

add(table);
```

## Using column groups
```
Table table = new Table();

TableHead head = table.getHead();
TableRow headerRow = head.addRow();
headerRow.addHeaderCell().setText("Hello with Style");
headerRow.addHeaderCell().setText("World with Style");
headerRow.addHeaderCell().setText("Hello");
headerRow.addHeaderCell().setText("World");

TableRow detailsRow = table.getBody().addRow();
detailsRow.addDataCell().setText("Hello with Style");
detailsRow.addDataCell().setText("World with Style");
detailsRow.addDataCell().setText("Hello");
detailsRow.addDataCell().setText("World");

TableColumnGroup columnGroup = table.getColumnGroup();
TableColumn column = columnGroup.addColumn();
column.addClassName("some");
column.setSpan(2);

headerRow.streamHeaderCells().forEach(c -> c.setScope(TableHeaderCell.SCOPE_COLUMN));

table.getCaption().setText("Using colgroups, thead and tbody");

add(table);
```

## Integrating Vaadin components
```
Table table = new Table();

TableRow headerRow = table.addRow();
headerRow.addHeaderCell().setText("Name");
headerRow.addHeaderCell().setText("Age");

for (int i = 0; i < 10; i++) {
    TextField textField = new TextField();
    textField.setValue("Some user " + i );

    NumberField numberField = new NumberField();
    numberField.setValue((double) (20 + i));

    TableRow detailsRow = table.addRow();
    detailsRow.addDataCell().add(textField);
    detailsRow.addDataCell().add(numberField);
}

add(table);
```

## Changing the content
```
// The table and its content is modifiable as any other Vaadin component

Table table = new Table();
table.setWidth("500px");

TableRow headerRow = table.addRow();
headerRow.addHeaderCell().setText("Hello");
headerRow.addHeaderCell().setText("World");

TableRow detailsRow = table.addRow();
detailsRow.addDataCell().setText("Hello");
detailsRow.addDataCell().setText("World");

add(table, new Button("Change cell content", event -> {
    table.getRow(1)
            .flatMap(row -> row.getCell(1))
            .ifPresent(cell -> cell.setText("You :)"));
}));
```

## Creating a Testbench testcase
```
// Needs the html-table-testbench-elements
// @see https://vaadin.com/directory/component/html-table-testbench-elements

public class SimpleTableViewIT extends TestBenchTestCase {

    // ... test setup and init

    @Test
    public void componentWorks() {
        TableElement table = getTable();

        Assert.assertFalse("caption" + " must not be present", table.getOptionalCaption().isPresent());
        Assert.assertFalse("colgroup" + " must not be present", table.getOptionalColumn().isPresent());
        Assert.assertFalse("thead" + " must not be present", table.getOptionalHead().isPresent());
        Assert.assertFalse("tbody" + " must not be present", table.getOptionalBody().isPresent());
        Assert.assertFalse("tfoot" + " must not be present", table.getOptionalFoot().isPresent());

        List<TableRowElement> rows = table.getRows();
        Assert.assertEquals(2, rows.size());

        TableRowElement row = rows.get(0);
        List<TableHeaderCellElement> headerCells = row.getHeaderCells();
        List<TableDataCellElement> dataCells = row.getDataCells();
        Assert.assertEquals(2, headerCells.size());
        Assert.assertEquals(0, dataCells.size());

        Assert.assertEquals("Hello", headerCells.get(0).getText());
        Assert.assertEquals("World", headerCells.get(1).getText());

        row = rows.get(1);
        headerCells = row.getHeaderCells();
        dataCells = row.getDataCells();
        Assert.assertEquals(0, headerCells.size());
        Assert.assertEquals(2, dataCells.size());

        Assert.assertEquals("Hello", dataCells.get(0).getText());
        Assert.assertEquals("World", dataCells.get(1).getText());
    }
}
```
