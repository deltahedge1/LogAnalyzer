package com.microsoft.azure.documentDB.dialog;

import static com.microsoft.azure.documentDB.util.WidgetUtils.createImageIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

import org.apache.commons.csv.CSVFormat;
import org.jdesktop.swingx.JXButton;

import com.microsoft.azure.documentDB.container.CollectionContainer;
import com.microsoft.azure.documentDB.widget.StandardDialog;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentCollection;

@SuppressWarnings("serial")
public class ImportDialog extends StandardDialog {

	public interface ImportAction {

		void OnComplete(CollectionContainer container);

		void OnFailure();

	}

	JTree tree;

	public ImportDialog(final JFrame frame, String title, final DocumentClient documentClient, final Database database,
			final ImportAction importAction) {
		super(frame, "Upload Dialog", false);

		JPanel contentPanel = new JPanel(new GridBagLayout());

		contentPanel.setBackground(Color.white);

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel fileNameLabel = new JLabel("FileName:");
		final JTextField fileNameField = new JTextField();
		JButton fileNameSelect = new JButton(createImageIcon("/images/folder-icon.png"));
		fileNameSelect.setPreferredSize(new Dimension(32, 32));
		fileNameSelect.setMinimumSize(new Dimension(32, 32));

		fileNameSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fileChooser = new JFileChooser();

				fileChooser.setPreferredSize(new Dimension(800, 500));

				int returnVal = fileChooser.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					fileNameField.setText(file.getAbsolutePath());

				}

				frame.repaint();

			};

		});

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(fileNameLabel, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 3;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(fileNameField, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 5;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(fileNameSelect, constraints);

		JLabel delimiterLabel = new JLabel("Delimiter:");
		final JTextField delimiterField = new JTextField(",");
		addSelectorToLayout(contentPanel, delimiterLabel, delimiterField, 1);

		JLabel quoteLabel = new JLabel("Quote:");
		final JTextField quoteField = new JTextField();
		addSelectorToLayout(contentPanel, quoteLabel, quoteField, 2);

		JLabel escapeLabel = new JLabel("Escape:");
		final JTextField escapeField = new JTextField();
		addSelectorToLayout(contentPanel, escapeLabel, escapeField, 3);

		final JCheckBox headerRow = new JCheckBox("Header Row?");

		headerRow.setSelected(true);
		headerRow.setBackground(Color.white);

		constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 3;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(headerRow, constraints);

		getContentPane().add(BorderLayout.NORTH, contentPanel);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		JXButton importButton = new JXButton("Import", createImageIcon("/images/import-icon-16.png"));

		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(fileNameField.getText());

				if (!file.exists()) {

					JOptionPane.showInternalMessageDialog(frame,
							"File: '" + file.getAbsolutePath() + "' does not exist", "Import Error",
							JOptionPane.ERROR_MESSAGE);

					return;

				}

				CSVFormat format = CSVFormat
						.newFormat(delimiterField.getText().equals("tab") ? '\t' : delimiterField.getText().charAt(0));

				if (!quoteField.getText().trim().isEmpty()) {

					format = format.withQuote(quoteField.getText().charAt(0));

				}

				if (!escapeField.getText().trim().isEmpty()) {

					format = format.withEscape(escapeField.getText().trim().charAt(0));

				}

				new ImportTaskDialog(frame, file, format, headerRow.isSelected(), documentClient, database,
						importAction);

			}

		});

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(importButton, constraints);

		JXButton cancelButton = new JXButton("Cancel", createImageIcon("/images/close-icon.png"));

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				frame.repaint();

				dispose();

			}

		});

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(20, 20, 20, 20);

		buttonPanel.add(cancelButton, constraints);

		getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		getContentPane().setBackground(Color.white);

		buttonPanel.setPreferredSize(new Dimension(400, 50));
		setSize(600, 320);

		setLocationRelativeTo(frame);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		decorate(importButton);
		decorate(cancelButton);

		setResizable(false);

	}

	void addSelectorToLayout(JPanel contentPanel, JLabel label, JTextField field, int y) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = y;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(label, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.gridx = 1;
		constraints.gridy = y;
		constraints.ipadx = 24;
		constraints.insets = new Insets(5, 5, 5, 5);

		contentPanel.add(field, constraints);

	}

}
