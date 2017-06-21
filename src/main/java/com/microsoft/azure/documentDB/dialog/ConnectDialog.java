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
import java.io.Closeable;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXButton;

import com.ezware.dialog.task.TaskDialogs;
import com.microsoft.azure.documentDB.widget.StandardDialog;

@SuppressWarnings("serial")
public class ConnectDialog extends StandardDialog implements Closeable {

	public interface ConnectAction {

		boolean connect(Closeable closeable, String uri) throws Exception;

	}

	public ConnectDialog(final JFrame frame, String title, final ConnectAction connectAction) {
		super(frame, title, false);

		JPanel contentPanel = new JPanel(new GridBagLayout());

		contentPanel.setPreferredSize(new Dimension(350, 160));
		contentPanel.setBackground(Color.white);

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel inputLable = new JLabel("Enter Azure URI Connection:");

		inputLable.setForeground(Color.BLUE.darker().darker());

		Preferences preferences = Preferences.userRoot();

		final JComboBox<String> uri = new JComboBox<String>(StringUtils.split(preferences.get("azure-table-uri", ""), ","));
		uri.setEditable(true);

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

		contentPanel.add(new JLabel("URI:"), constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 3;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		uri.setPreferredSize(new Dimension(120,30));
		
		contentPanel.add(uri, constraints);

		getContentPane().add(BorderLayout.NORTH, contentPanel);
		getContentPane().setBackground(Color.WHITE);
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JXButton loginButton = new JXButton("Connect", createImageIcon("/images/ok-icon.png"));

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(loginButton, constraints);

		JXButton cancelButton = new JXButton("Cancel", createImageIcon("/images/close-icon.png"));

		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					if (connectAction.connect(ConnectDialog.this, uri.getEditor().getItem().toString())) {
					}

				} catch (Exception e) {

					TaskDialogs.showException(e);

				}

			}

		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();

			}

		});

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(cancelButton, constraints);
		buttonPanel.setPreferredSize(new Dimension(400, 50));

		decorate(loginButton);
		decorate(cancelButton);
		
		getContentPane().add(BorderLayout.SOUTH, buttonPanel);

		pack();

		setSize(620, 320);

		setLocationRelativeTo(frame);

	}

	@Override
	public void close() throws IOException {

		dispose();

	}

}
