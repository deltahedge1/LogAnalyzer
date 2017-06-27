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
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXButton;

import com.ezware.dialog.task.TaskDialogs;
import com.microsoft.azure.documentDB.widget.GlassPane;
import com.microsoft.azure.documentDB.widget.StandardDialog;

@SuppressWarnings("serial")
public class ConnectDialog extends StandardDialog implements Closeable {

	public interface ConnectAction {

		boolean connect(Closeable closeable, String host, String key) throws Exception;

	}

	public ConnectDialog(final JFrame frame, String title, final ConnectAction connectAction) {
		super(frame, title, false);

		JPanel contentPanel = new JPanel(new GridBagLayout());

		contentPanel.setPreferredSize(new Dimension(300, 160));
		contentPanel.setBackground(Color.white);

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel inputLable = new JLabel("Enter Azure URI Connection:");

		inputLable.setForeground(Color.BLUE.darker().darker());

		final Preferences preferences = Preferences.userRoot();

		final JComboBox<String> host = new JComboBox<String>(
				StringUtils.split(preferences.get("azure-cosmosdb-host", ""), ","));
		host.setEditable(true);

		final JComboBox<String> key = new JComboBox<String>(
				StringUtils.split(preferences.get("azure-cosmosdb-key", ""), ","));
		key.setEditable(true);

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

		contentPanel.add(new JLabel("Host:"), constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 5;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		host.setPreferredSize(new Dimension(220, 30));

		contentPanel.add(host, constraints);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;

		constraints.insets = new Insets(5, 5, 5, 5);
		contentPanel.add(new JLabel("Key:"), constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 5;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(5, 5, 5, 5);

		key.setPreferredSize(new Dimension(220, 30));

		contentPanel.add(key, constraints);
		getContentPane().add(BorderLayout.NORTH, contentPanel);
		getContentPane().setBackground(Color.WHITE);

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		final JXButton loginButton = new JXButton("Connect", createImageIcon("/images/ok-icon.png"));

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);

		buttonPanel.add(loginButton, constraints);

		final JXButton cancelButton = new JXButton("Cancel", createImageIcon("/images/close-icon.png"));

		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				final GlassPane glassPane = new GlassPane(ConnectDialog.this);

				ConnectDialog.this.setEnabled(false);
				loginButton.setEnabled(false);
				cancelButton.setEnabled(false);

				glassPane.activate("Connecting...");

				ConnectDialog.this.revalidate();

				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						try {

							if (connectAction.connect(ConnectDialog.this, host.getEditor().getItem().toString(),
									key.getEditor().getItem().toString())) {

								preferences.put("azure-cosmosdb-host", host.getEditor().getItem().toString());
								preferences.put("azure-cosmosdb-key", key.getEditor().getItem().toString());

							}

						} catch (Exception e) {

							TaskDialogs.showException(e);

						}
						
						loginButton.setEnabled(true);
						cancelButton.setEnabled(true);

						ConnectDialog.this.setEnabled(true);
						glassPane.deactivate();

						ConnectDialog.this.revalidate();
						return null;

					}

				};

				worker.execute();
			}

		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					close();
				} catch (IOException e1) {
				}

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
