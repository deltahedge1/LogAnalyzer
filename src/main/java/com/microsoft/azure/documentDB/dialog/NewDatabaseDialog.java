package com.microsoft.azure.documentDB.dialog;

/**
 * Connection Dialog - obtains the connection 'uri' which identifies the repository
 * 
 * @author Neil Brittliff
 * 
 *         (c) 2013 - Neil Brittliff
 * 
 */

import static com.microsoft.azure.documentDB.util.WidgetUtils.createImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXButton;

import com.ezware.dialog.task.TaskDialogs;
import com.microsoft.azure.documentDB.widget.StandardDialog;

@SuppressWarnings("serial")
public class NewDatabaseDialog extends StandardDialog {

	public interface NewDatabaseAction {

		boolean select(String path) throws Exception;

	}

	public NewDatabaseDialog(final JFrame frame, final String title, final NewDatabaseAction action) {
		super(frame, title, false);

		JPanel contentPanel = new JPanel(new GridBagLayout());

		contentPanel.setPreferredSize(new Dimension(350, 80));
		contentPanel.setBackground(Color.white);

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel inputLable = new JLabel("Enter Database Name :");

		inputLable.setForeground(Color.BLUE.darker().darker());

		JLabel uriNameLabel = new JLabel("Database:");

		final JTextField databaseName = new JTextField();

		databaseName.setEditable(true);

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 6;
		constraints.insets = new Insets(5, 5, 5, 10);

		contentPanel.add(inputLable, constraints);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(uriNameLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 3;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(databaseName, constraints);

		getContentPane().add(BorderLayout.NORTH, contentPanel);

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		final JXButton okButton = new JXButton("OK", createImageIcon("/images/ok-icon.png"));

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(okButton, constraints);

		final JXButton cancelButton = new JXButton("Cancel", createImageIcon("/images/close-icon.png"));

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				try {

					action.select(databaseName.getText());
					dispose();

				} catch (Exception e) {

					TaskDialogs.showException(e);

				}

			}

		});

		databaseName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {

				okButton.setEnabled(!databaseName.getText().isEmpty());

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

				okButton.setEnabled(!databaseName.getText().isEmpty());

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				okButton.setEnabled(!databaseName.getText().isEmpty());

			}

		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();

			}

		});

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(cancelButton, constraints);
		buttonPanel.setPreferredSize(new Dimension(400, 40));
		buttonPanel.setMaximumSize(new Dimension(400, 40));

		decorate(okButton);
		decorate(cancelButton);

		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		pack();

		setSize(500, 200);

		setLocationRelativeTo(frame);

	}

}
