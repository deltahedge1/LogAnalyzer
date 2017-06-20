package com.microsoft.azure.documentDB.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXButton;

import com.l2fprod.common.swing.JButtonBar;
import com.microsoft.azure.documentDB.layout.RiverLayout;

@SuppressWarnings("serial")
public class StandardDialog extends JDialog {
	public enum STATUS {

		CANCEL, OK

	}

	protected STATUS status;

	public STATUS getStatus() {
		return status;
	}

	/**
	 * Standard Dialog Constructor
	 * 
	 * @param frame
	 *            the owner
	 * @param title
	 *            the Dialog's title
	 * @param modal
	 *            'true' the dialog is model, 'false' otherwise
	 * @param resizable
	 *            'true' the dialog is resizable, 'false' otherwise
	 * 
	 */
	public StandardDialog(final Frame frame, String title, Boolean modal, Boolean resizable) {
		super(frame, title, modal);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setResizable(resizable);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				frame.repaint();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				frame.repaint();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}

		});

	}

	/**
	 * Add actions to a Button Bar
	 * 
	 * @param name
	 *            the Name of the Action
	 * @param iconLocation
	 *            the Icon Location
	 * @param selected
	 *            'true' selected, 'false' otherwise
	 * @param mainPanel
	 *            the Main Panel
	 * @param panel
	 *            the Panel to Add
	 * @param buttonBar
	 *            the Button Bar
	 * @param menuGroup
	 *            the associated Button Group
	 * 
	 */
	protected void addAction(final String name, String iconLocation, boolean selected, final JPanel mainPanel,
			final JPanel panel, JButtonBar buttonBar, ButtonGroup menuGroup) {

		final Action action = new AbstractAction(name, new ImageIcon(getClass().getResource(iconLocation))) {

			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				mainPanel.add(panel);
				mainPanel.updateUI();
			}

		};

		JToggleButton toggleButton = new JToggleButton(action);

		toggleButton.setSelected(selected);

		menuGroup.add(toggleButton);
		buttonBar.add(toggleButton);

	}

	/**
	 * Wrap a panel
	 * 
	 * @param view
	 *            the panel to wrap
	 * 
	 * @return the Wrapped Panel
	 * 
	 */
	protected JPanel wrapView(JPanel view) throws Exception {
		JPanel panel = new JPanel();
		panel.setLayout(new RiverLayout());
		panel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()));

		panel.add("br hfill vfill", view);

		return panel;

	}

	public void decorate(JXButton button) {

		button.setPreferredSize(new Dimension(120, 35));
		button.setMinimumSize(new Dimension(120, 35));

	}

	/**
	 * Constructor
	 * 
	 * @param frame
	 *            the Main Frame
	 * @param title
	 *            the Dialog's Title
	 * @param resizable
	 *            the Dialog's Resizable
	 * 
	 */
	public StandardDialog(final Frame frame, String title, Boolean resizable) {
		this(frame, title, true, resizable);

	}

}
