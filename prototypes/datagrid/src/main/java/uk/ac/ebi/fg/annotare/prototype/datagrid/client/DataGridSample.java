package uk.ac.ebi.fg.annotare.prototype.datagrid.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DataGridSample implements EntryPoint {
	private VerticalPanel mainPanel;
	private FlexTable stocksFlexTable;
	private HorizontalPanel addPanel;
	private TextBox newSymbolTextBox;
	private Button addButton;
	private ArrayList <String> stocks = new ArrayList<String>();
	private Label lblStockWatcher;
	
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		{
			mainPanel = new VerticalPanel();
			rootPanel.add(mainPanel, 5, 5);
			mainPanel.setSize("440px", "290px");
			{
				lblStockWatcher = new Label("DataGrid Sample");
				lblStockWatcher.setStyleName("gwt-Label-DataGridSample");
				
				mainPanel.add(lblStockWatcher);
			}
			{
				stocksFlexTable = new FlexTable();
				//Add these lines
				stocksFlexTable.setText(0, 0, "Symbol");
				stocksFlexTable.setText(0, 1, "Price");
				stocksFlexTable.setText(0, 2, "Change");
				stocksFlexTable.setText(0, 3, "Remove");
				
				// Add styles to elements in the stock list table.
				stocksFlexTable.setCellPadding(6);
			    stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
			    stocksFlexTable.addStyleName("watchList");
			    stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
			    stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
			    stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");
			    
				mainPanel.add(stocksFlexTable);
			}
			{
				addPanel = new HorizontalPanel();
				addPanel.addStyleName("addPanel");
				mainPanel.add(addPanel);
				{
					newSymbolTextBox = new TextBox();
					newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
						public void onKeyPress(KeyPressEvent event) {
							if (event.getCharCode() == KeyCodes.KEY_ENTER){
								addStock();
							}
						}
					});
					newSymbolTextBox.setFocus(true);
					addPanel.add(newSymbolTextBox);
				}
				{
					addButton = new Button("New button");
					addButton.setStyleName("gwt-Button-Add");
					addButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							addStock();
						}
					});
					addButton.setText("Add");
					addPanel.add(addButton);
				}
			}
		}
		

	}


	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
	    newSymbolTextBox.setFocus(true);

	    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
	    if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
	      Window.alert("'" + symbol + "' is not a valid symbol.");
	      newSymbolTextBox.selectAll();
	      return;
	    }

	    newSymbolTextBox.setText("");

	 // don't add the stock if it's already in the watch list
	    if (stocks.contains(symbol))
	        return;

	    // add the stock to the list
	    int row = stocksFlexTable.getRowCount();
	    stocks.add(symbol);
	    stocksFlexTable.setText(row, 0, symbol);
	    stocksFlexTable.setWidget(row, 2, new Label());
	    stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
	    stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
	    stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
	    
	    // add button to remove this stock from the list
	    Button removeStock = new Button("x");
	    removeStock.addStyleDependentName("remove");
	    removeStock.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {					
	        int removedIndex = stocks.indexOf(symbol);
	        stocks.remove(removedIndex);
	        stocksFlexTable.removeRow(removedIndex + 1);
	    }
	    });
	    stocksFlexTable.setWidget(row, 3, removeStock);	
		
	}
}
