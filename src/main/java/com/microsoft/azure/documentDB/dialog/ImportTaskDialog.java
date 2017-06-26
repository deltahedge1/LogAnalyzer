package com.microsoft.azure.documentDB.dialog;

import static com.microsoft.azure.documentDB.util.WidgetUtils.setProgressBar;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.CountingInputStream;
import org.json.JSONObject;

import com.ezware.dialog.task.TaskDialogs;
import com.microsoft.azure.documentDB.dialog.ImportDialog.ImportAction;
import com.microsoft.azure.documentDB.widget.StandardDialog;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentCollection;

@SuppressWarnings("serial")
public class ImportTaskDialog extends StandardDialog {

	public ImportTaskDialog(final JFrame frame, final File file, final CSVFormat format, final boolean headerRow,
			final DocumentClient documentClient, ImportAction importAction) {
		super(frame, "Importing File '" + file.getName() + "'", false);

		JPanel contentPanel = new JPanel(new BorderLayout(5, 5));

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(BorderLayout.CENTER, contentPanel);

		final JProgressBar progressBar = new JProgressBar(0, 10000);
		progressBar.setStringPainted(true);
		progressBar.setString("0%");
		progressBar.setValue(0);

		contentPanel.add(BorderLayout.SOUTH, progressBar);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setSize(380, 120);

		setLocationRelativeTo(frame);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
			}

		});

		SwingWorker<Boolean, Boolean> importTask = new SwingWorker<Boolean, Boolean>() {

			@Override
			protected Boolean doInBackground() throws Exception {

				CountingInputStream cis = null;
				FileInputStream fis = null;

				CSVParser parser = null;

				try {
					long size = file.length();

					fis = new FileInputStream(file);
					cis = new CountingInputStream(fis);

					final Reader reader = new InputStreamReader(cis, "UTF-8");
					parser = new CSVParser(reader, format);

					boolean headerEnabled = headerRow;
					List<String> header = new LinkedList<String>();

					Database databaseDefinition = new Database();

					databaseDefinition.setId(file.getName());

					Database databaseCache = documentClient.createDatabase(databaseDefinition, null).getResource();
					
					DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId("Records");

                    DocumentCollection collectionCache = documentClient.createCollection(
                    		databaseCache.getSelfLink(),
                            collectionDefinition, null).getResource();
					for (CSVRecord csvRecord : parser) {
						JSONObject jsonElement = new JSONObject();

						int iHeader = 0;

						for (String value : csvRecord) {

							setProgressBar(progressBar, size, cis.getCount());
							
							if (headerEnabled) {
								header.add(value);
							} else {
								jsonElement.append(header.get(iHeader), value);
							
							}

							iHeader += 1;

						}
						
						if (!headerEnabled) {
						
							Document document = new Document(jsonElement);
						
							documentClient.createDocument(collectionCache.getSelfLink(), document, null, false);
						}
						
						headerEnabled = false;

					}
					
				} catch (Exception e) {

					TaskDialogs.showException(e);

				} finally {

					parser.close();

					cis.close();
					fis.close();

					dispose();

				}

				return true;

			}

		};

		importTask.execute();

		setVisible(true);

	}

}
