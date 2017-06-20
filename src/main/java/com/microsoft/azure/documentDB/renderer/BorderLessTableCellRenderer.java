package com.microsoft.azure.documentDB.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BorderLessTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int col) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		setBorder(noFocusBorder);

		return this;

	}

}
