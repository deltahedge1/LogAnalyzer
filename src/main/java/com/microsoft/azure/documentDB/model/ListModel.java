package com.microsoft.azure.documentDB.model;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


@SuppressWarnings("serial")
public class ListModel extends AbstractTableModel {
	int columnCount;
	long listLength;

	List<String> headers;

	boolean active;
	public ListModel() throws Exception {


		this.listLength = 0;

		this.headers = new LinkedList<String>();

		active = true;

	}

	@Override
	public int getColumnCount() {

		return columnCount + 1;

	}

	@Override
	public int getRowCount() {

		return 0;

	}

	@Override
	public Object getValueAt(int row, int column) {

		return "";

	}

	@Override
	public String getColumnName(int col) {

		return col == 0 ? "#" : (!headers.isEmpty()) ? headers.get(col - 1) : "[" + (col) + "]";

	}

	public void first() {
	}

	@Override
	public Class<?> getColumnClass(int column) {

		return column == 0 ? Long.class : String.class;

	}

	public void last() {

	}

	public void next() {
	}

	public void back() {
	}

	public void clear() {

		headers.clear();
		columnCount = -1;

	}

}
