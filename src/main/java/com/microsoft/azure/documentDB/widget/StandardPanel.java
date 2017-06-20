package com.microsoft.azure.documentDB.widget;

import static com.microsoft.azure.documentDB.util.WidgetUtils.createImageIcon;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXButton;

interface LoadActions {

	void unload(File file);

	void upload(File file);

}

@SuppressWarnings("serial")
abstract public class StandardPanel extends JPanel implements LoadActions {
	protected JXButton unloadButton;
	protected JXButton uploadButton;

	protected StandardPanel(final JFrame frame) {

		this.unloadButton = new JXButton("Unload", createImageIcon("/images/export-icon.png"));

		decorate(unloadButton);
		
		unloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					try {
						final JFileChooser fileChooser = new JFileChooser();

						if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {

							unload(fileChooser.getSelectedFile());

						}

					} catch (Exception e) {

						JOptionPane.showMessageDialog(frame, e.toString());

					}

				} catch (Exception e) {

					JOptionPane.showMessageDialog(frame, e.toString());

				}

			}

		});

		this.uploadButton = new JXButton("Upload", createImageIcon("/images/import-icon-16.png"));

		decorate(uploadButton);

		uploadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					final JFileChooser fileChooser = new JFileChooser();

					if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

						upload(fileChooser.getSelectedFile());

					}

				} catch (Exception e) {

					JOptionPane.showMessageDialog(frame, e.toString());

				}

			}

		});

	}

	public void decorate(JXButton button) {

		button.setPreferredSize(new Dimension(120, 35));
		button.setMinimumSize(new Dimension(120, 35));
		
		
	}
	
}
