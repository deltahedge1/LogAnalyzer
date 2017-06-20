package com.microsoft.azure.documentDB.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.l2fprod.common.swing.JButtonBar;

@SuppressWarnings("serial")
public class DetailDialog extends StandardDialog {

	public interface AbstractDetailPanel {

		JPanel getPanel();

		String getLabel();

		String getIcon();

	}

	private ButtonBar buttonBar = new ButtonBar(JButtonBar.VERTICAL);
	private ButtonGroup menuGroup = new ButtonGroup();
	private JButton closeButton = new JButton("Close", new ImageIcon(getClass().getResource("/images/close-icon.png")));

	private JPanel mainPanel = new JPanel();

	public DetailDialog(final JFrame frame, String title, final AbstractDetailPanel... panels) throws Exception {
		super(frame, title, true, true);

		buttonBar.setBorder(new EmptyBorder(10, 10, 10, 10));

		setLayout(new GridBagLayout());

		JScrollPane leftBarScrollPane = new JScrollPane(buttonBar);

		leftBarScrollPane.setPreferredSize(new Dimension(100, 300));
		leftBarScrollPane.setMinimumSize(new Dimension(100, 300));
		setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);

		getContentPane().add(leftBarScrollPane, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;

		getContentPane().add(mainPanel, gridBagConstraints);

		for (int iPanel = 0; iPanel < panels.length; iPanel++) {
			JPanel panel = wrapView(panels[iPanel].getPanel());

			if (iPanel == 0) {

				mainPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(panel);

			}

			addAction(panels[iPanel].getLabel(), panels[iPanel].getIcon(), true, mainPanel, panel, buttonBar,
					menuGroup);

		}

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.anchor = GridBagConstraints.EAST;

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new GridLayout(1, 3, 5, 0));

		closeButton.setPreferredSize(new Dimension(120, 35));
		closeButton.setMinimumSize(new Dimension(120, 35));

		buttonPanel.add(closeButton);

		buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
				BorderFactory.createEmptyBorder(0, 5, 0, 0)));
		getContentPane().add(buttonPanel, gridBagConstraints);

		pack();

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}

		});

		setPreferredSize(new Dimension(1200, 800));
		
		setLocationRelativeTo(frame);

	}

}
