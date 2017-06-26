package com.microsoft.azure.documentDB.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.microsoft.azure.documentDB.renderer.BorderLessTableCellRenderer;

@SuppressWarnings("serial")
public class TableView extends JTable {
	private static final Color EVEN_ROW_COLOR = new Color(243, 247, 250);
	private static final Color ODD_ROW_COLOR = new Color(255, 255, 255);

	private static final Color TABLE_GRID_COLOR = new Color(0xd9d9d9);

	private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();

	final JScrollPane scrollPane;

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public TableView(TableModel model) {
		super(model);
		setDefaultRenderer(Object.class, new BorderLessTableCellRenderer());

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		setTableHeader(createTableHeader());

		getTableHeader().setReorderingAllowed(false);
		setOpaque(false);
		setGridColor(TABLE_GRID_COLOR);
		setIntercellSpacing(new Dimension(0, 0));
		setShowGrid(false);

		scrollPane = TableView.createStripedJScrollPane(this);

	}

	/**
	 * Creates a JTableHeader that paints the table header background to the
	 * right of the right-most column if neccesary
	 * 
	 */
	private JTableHeader createTableHeader() {

		return new JTableHeader(getColumnModel()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				JViewport viewport = (JViewport) table.getParent();

				if (viewport != null && table.getWidth() < viewport.getWidth()) {
					int x = table.getWidth();
					int width = viewport.getWidth() - table.getWidth();

					paintHeader(g, getTable(), x, width);

				}

			}

		};
	}

	/**
	 * Paints the given JTable's table default header background at given x for
	 * the given width.
	 * 
	 * @param g
	 *            the graphics context
	 * @param table
	 *            the Table's name
	 * @param x
	 *            the 'x' coordinate
	 * @param width
	 *            the Table's width
	 * 
	 */
	final static JTable model;
	final static Component component;
	final static TableCellRenderer renderer;

	static {
		model = new JTable();
		model.addColumn(new TableColumn());
		renderer = model.getTableHeader().getDefaultRenderer();
		component = renderer.getTableCellRendererComponent(model, "", false, false, -1, 0);

		model.addColumn(new TableColumn());

	}

	private static void paintHeader(Graphics graphics, JTable table, int x, int width) {

		component.setBounds(0, 0, width, table.getTableHeader().getHeight());

		((JComponent) component).setOpaque(false);

		CELL_RENDER_PANE.paintComponent(graphics, component, null, x, 0, width, table.getTableHeader().getHeight(),
				true);

	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component component = super.prepareRenderer(renderer, row, column);

		if (component instanceof JComponent) {
			((JComponent) component).setOpaque(getSelectionModel().isSelectedIndex(row));
		}

		return component;

	}

	public void showHeader(boolean show) {

		if (show) {

			getTableHeader().setVisible(true);
			getTableHeader().setPreferredSize(null);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		} else {

			getTableHeader().setVisible(false);
			getTableHeader().setPreferredSize(new Dimension(-1, 0));
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		}

	}

	/**
	 * Creates a JViewport that draws a striped backgroud corresponding to the
	 * row positions of the given JTable.
	 */
	private static class StripedViewport extends JViewport {

		private final JTable table;
		private final JScrollPane scrollPane;

		public StripedViewport(JTable table, JScrollPane scrollPane) {
			this.table = table;
			this.scrollPane = scrollPane;

			setOpaque(false);
			initListeners();

		}

		private void initListeners() {
			PropertyChangeListener listener = createTableColumnWidthListener();

			for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {

				table.getColumnModel().getColumn(i).addPropertyChangeListener(listener);

			}

			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					repaint();

				}

			});

		}

		private PropertyChangeListener createTableColumnWidthListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {

					repaint();

				}

			};
		}

		@Override
		protected void paintComponent(Graphics g) {

			paintStripedBackground(g);

			super.paintComponent(g);

		}

		private void paintStripedBackground(Graphics g) {
			int rowAtPoint = table.rowAtPoint(g.getClipBounds().getLocation());
			int topY = rowAtPoint < 0 ? g.getClipBounds().y : table.getCellRect(rowAtPoint, 0, true).y;

			int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;

			while (topY < g.getClipBounds().y + g.getClipBounds().height
					+ scrollPane.getVerticalScrollBar().getValue()) {
				int bottomY = topY + table.getRowHeight();
				g.setColor(getRowColor(currentRow));
				g.fillRect(g.getClipBounds().x, topY - scrollPane.getVerticalScrollBar().getValue(),
						g.getClipBounds().width, bottomY);

				topY = bottomY;

				currentRow++;

			}

			for (int iSelectedRow : table.getSelectedRows()) {
				Rectangle rect = getRowBounds(table, iSelectedRow);

				rect.width = rect.y + scrollPane.getHorizontalScrollBar().getValue();

				g.setColor(table.getSelectionBackground());
				g.fillRect(rect.x - 500, rect.y - scrollPane.getVerticalScrollBar().getValue(),
						g.getClipBounds().width + 500, rect.height);

			}

		}

		public Rectangle getRowBounds(JTable table, int row) {
			Rectangle result = table.getCellRect(row, -1, true);
			Insets i = table.getInsets();

			result.x = i.left;
			result.width = table.getWidth() - i.left - i.right;

			return result;
		}

		private Color getRowColor(int row) {

			return row % 2 == 0 ? EVEN_ROW_COLOR : ODD_ROW_COLOR;

		}

	}

	public static JScrollPane createStripedJScrollPane(JTable table) {
		JScrollPane scrollPane = new JScrollPane(table);

		scrollPane.setViewport(new StripedViewport(table, scrollPane));
		scrollPane.getViewport().setView(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		return scrollPane;

	}

}