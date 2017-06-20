package com.microsoft.azure.documentDB.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.swingx.JXButton;

import com.microsoft.azure.documentDB.renderer.BorderLessTableCellRenderer;
import com.microsoft.azure.documentDB.widget.TableView;

public class WidgetUtils {

	public static class ButtonManager {
		final List<AbstractButton> buttons;

		public List<AbstractButton> getButtons() {
			return buttons;
		}

		public ButtonManager() {

			this.buttons = new ArrayList<AbstractButton>();

		}

		public ButtonManager(AbstractButton... buttons) {

			this.buttons = new ArrayList<AbstractButton>(Arrays.asList(buttons));

		}

		public ButtonManager(ButtonManager... buttonManagers) {

			this.buttons = new ArrayList<AbstractButton>();

			addButtons(buttonManagers);

		}

		public void setEnabled(boolean enabled) {

			for (AbstractButton button : buttons) {

				button.setEnabled(enabled);

			}

		}

		public void addButtons(AbstractButton... buttons) {

			this.buttons.addAll(Arrays.asList(buttons));

		}

		public void addButtons(ButtonManager... buttonManagers) {

			for (ButtonManager buttonManager : buttonManagers) {

				this.buttons.addAll(buttonManager.getButtons());

			}

		}

	}

	/**
	 * Get an Image Icon
	 * 
	 * @param path
	 *            the Path to the Icon
	 * 
	 * @return the Image Icon
	 */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = WidgetUtils.class.getResource(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}

	}

	/**
	 * Remove a node - 'graph' from the graph tree model
	 * 
	 * @param tree
	 *            the tree model
	 * @param node
	 *            the node to remove
	 * 
	 */
	public static void removeNodePath(JTree tree, DefaultMutableTreeNode node) {
		try {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

			((DefaultTreeModel) (tree.getModel())).removeNodeFromParent(node);

			while (parent.getChildCount() == 0 && parent != tree.getModel().getRoot()) {

				node = parent;

				parent = (DefaultMutableTreeNode) parent.getParent();

				((DefaultTreeModel) (tree.getModel())).removeNodeFromParent(node);

			}

			((DefaultTreeModel) (tree.getModel())).reload();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Set the Table Filter
	 * 
	 * @param table
	 *            the table to own the filter
	 * @param filter
	 *            the text to filter the rows
	 */
	public static void setFilter(JTable table, String filter) {
		RowFilter<TableModel, Object> rowFilter = null;
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		try {
			List<Integer> columns = new LinkedList<Integer>();

			for (int iColumn = 0; iColumn < table.getModel().getColumnCount() - 1; iColumn++) {
				columns.add(iColumn);
			}

			rowFilter = RowFilter.regexFilter(filter, ArrayUtils.toPrimitive(columns.toArray(new Integer[0])));

		} catch (java.util.regex.PatternSyntaxException e) {

			return;

		}

		sorter.setRowFilter(rowFilter);

	}

	/**
	 * Set the Column Sizes the last size is the default column size from there
	 * on
	 * 
	 * @param table
	 *            the table to own the filter
	 * @param sizes
	 *            the column sizes
	 */
	public static void setColumns(JTable table, int... sizes) {
		DefaultTableCellRenderer rightRenderer = new BorderLessTableCellRenderer();

		rightRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		table.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);

		for (int iColumn = 0; iColumn < table.getColumnModel().getColumnCount(); iColumn++) {

			if (iColumn < sizes.length) {

				table.getColumnModel().getColumn(iColumn).setPreferredWidth(sizes[iColumn]);

			} else {

				table.getColumnModel().getColumn(iColumn).setPreferredWidth(sizes[sizes.length - 1]);

			}

		}

	}

	/**
	 * Add a menu item
	 * 
	 * @param menu
	 *            The menu 'container
	 * @param title
	 *            The menu item's title
	 * @param icon
	 *            the menu's icon
	 * @param listener
	 *            the menu item's action listener
	 * 
	 */
	public static void addMenuItem(JPopupMenu menu, String title, String icon, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(title, createImageIcon(icon));

		menu.add(menuItem);

		menuItem.addActionListener(listener);

	}

	/**
	 * Convert the Tree Path to an formal Path (do not include root)
	 * 
	 * @param node
	 *            the tree node
	 * 
	 * @return the path
	 * 
	 */
	public static String toPath(DefaultMutableTreeNode node) {
		StringBuilder builder = new StringBuilder();

		for (int iNode = 0; iNode < node.getPath().length; iNode++) {

			if (iNode > 0) {

				builder.append("/");
				builder.append(node.getPath()[iNode]);
			}

		}

		return builder.toString();

	}

	/**
	 * Add a Button to a Tool Bar
	 * 
	 * @param toolBar
	 *            the Tool Bar
	 * @param menu
	 *            the Menu Item
	 * @param tooltip
	 *            the Button's tooltip (can be 'null')
	 * @param buttonImage
	 *            the Toolbar Button's image (cannot be 'null')
	 * @param itemImage
	 *            the Menu Item's image (cannot be 'null')
	 * 
	 * @return a Button Manager
	 * 
	 */
	public static ButtonManager addAction(JToolBar toolBar, String tooltip, String buttonImage,
			String itemImage, ActionListener listener) {
		JXButton button = new JXButton(createImageIcon(buttonImage));
		JMenuItem menuItem = new JMenuItem(tooltip, createImageIcon(itemImage));

		button.setToolTipText(tooltip);

		button.setFocusable(false);
		button.setBorderPainted(false);

		toolBar.add(button);
		toolBar.addSeparator(new Dimension(5, 0));
		button.addActionListener(listener);
		menuItem.addActionListener(listener);

		return new ButtonManager(button, menuItem);

	}

	/**
	 * Add a Button to a Tool Bar
	 * 
	 * @param menu
	 *            the Menu Item
	 * @param tooltip
	 *            the Button's tooltip (can be 'null')
	 * @param image
	 *            the Button's image (cannot be 'null')
	 * @param listener
	 *            the Tool Bar button's listener
	 * 
	 * @return a Toolbar button
	 * 
	 */
	public static ButtonManager addAction(JMenu menu, String tooltip, String image, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(tooltip, createImageIcon(image));

		menuItem.addActionListener(listener);

		menu.add(menuItem);

		return new ButtonManager(menuItem);

	}

	/**
	 * Add a Button to a Tool Bar
	 * 
	 * @param toolBar
	 *            the Tool Bar
	 * @param tooltip
	 *            the Button's tooltip (can be 'null')
	 * @param image
	 *            the Button's image (cannot be 'null')
	 * @param listener
	 *            the Tool Bar button's listener
	 * 
	 * @return a ButtonManager
	 * 
	 */
	public static ButtonManager addAction(JToolBar toolBar, String tooltip, String image, ActionListener listener) {
		JXButton button = new JXButton(createImageIcon(image));

		button.setToolTipText(tooltip);

		button.setFocusable(false);

		toolBar.add(button);
		toolBar.addSeparator(new Dimension(5, 0));
		button.addActionListener(listener);

		return new ButtonManager(button);

	}

	/**
	 * Enable Repository Buttons
	 * 
	 * @param buttonManager
	 *            the button manager 'group'
	 * @param enable
	 *            'true' enable repository buttons, 'false' disable repository
	 *            buttons
	 */

	public static void enableButtonGroup(List<ButtonManager> buttonManager, boolean enable) {

		for (ButtonManager buttonManger : buttonManager) {

			buttonManger.setEnabled(enable);

		}

	}

	/**
	 * Set the Progress Bar
	 * 
	 * @param progressBar
	 *            the Progress Bar
	 * @param progress
	 *            the new Progress Value
	 * 
	 */
	public static void setProgressBar(JProgressBar progressBar, int progress) {
		float percentage = Math.round(progress * 10000.0 / progressBar.getMaximum());

		if (progressBar.getValue() != progress) {
			progressBar.setValue(progress);

			progressBar.setString(String.format("%3.2f", percentage / 100) + "%");

		}

	}

	/**
	 * Set the Progress Bar (0 .. 100) with a size
	 * 
	 * @param progressBar
	 *            the Progress Bar
	 * @param size
	 *            the size (maximum
	 * @param progress
	 *            the new Progress Value
	 */
	public static void setProgressBar(JProgressBar progressBar, long size, long progress) {
		if (progressBar.getValue() != progress) {
			float percentage = (float) Math.round(progress * 10000.0 / size);

			int value = (int) ((size == 0) ? 10000 : percentage > 10000 ? 10000 : percentage);

			if (progressBar.getValue() != value) {
				progressBar.setString(String.format("%3.2f", (float) (percentage / 100)) + "%");
				progressBar.setValue((int) ((size == 0) ? 10000 : percentage > 10000 ? 10000 : percentage));
			}

		}

	}

	/**
	 * Wrap the Panel
	 * 
	 * @param component
	 *            the Component to Wrap
	 * 
	 * @return the wrapped panel
	 * 
	 */
	public static JPanel wrap(JComponent component, int top, int left, int bottom, int right) {
		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(top, left, bottom, right),
				BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
				BorderFactory.createEmptyBorder(1, 1, 1, 1))));
		
		panel.add(component, BorderLayout.CENTER);

		return panel;

	}

	/**
	 * Populate the Domain Combobox
	 * 
	 * @param tableView
	 *            The Table View
	 * @param entries
	 *            The Combobox contents
	 * 
	 * @return a populated Combobox
	 * 
	 */
	public static JComboBox<String> createCombobox(final TableView tableView,
			List<AbstractMap.SimpleEntry<String, String>> entries) {
		JComboBox<String> comboBox = new JComboBox<String>();

		comboBox.addItem("");
		comboBox.setOpaque(true);

		for (AbstractMap.SimpleEntry<String, String> entry : entries) {

			comboBox.addItem(entry.getKey());

		}

		comboBox.setUI(new BasicComboBoxUI());

		for (int iComponent = 0; iComponent < comboBox.getComponentCount(); iComponent++) {

			if (comboBox.getComponent(iComponent) instanceof JComponent) {
				((JComponent) comboBox.getComponent(iComponent)).setBorder(new EmptyBorder(0, 0, 0, 0));
			}

			if (comboBox.getComponent(iComponent) instanceof AbstractButton) {
				((AbstractButton) comboBox.getComponent(iComponent)).setBorderPainted(false);
				((AbstractButton) comboBox.getComponent(iComponent)).setOpaque(false);
				((AbstractButton) comboBox.getComponent(iComponent)).setContentAreaFilled(false);
				((AbstractButton) comboBox.getComponent(iComponent)).setBackground(null);

			}

		}

		comboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

				tableView.repaint();

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

				tableView.repaint();

			}

		});

		comboBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

				tableView.repaint();

			}

			@Override
			public void focusGained(FocusEvent e) {
			}

		});

		return comboBox;

	}

}
