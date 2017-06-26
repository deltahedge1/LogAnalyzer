package com.microsoft.azure.documentDB.window;

import static com.microsoft.azure.documentDB.util.WidgetUtils.addAction;
import static com.microsoft.azure.documentDB.util.WidgetUtils.createImageIcon;
import static com.microsoft.azure.documentDB.util.WidgetUtils.wrap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.MultiSplitLayout;

import com.ezware.dialog.task.TaskDialogs;
import com.jgoodies.forms.builder.ListViewBuilder;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.microsoft.azure.documentDB.container.DatabaseContainer;
import com.microsoft.azure.documentDB.dialog.ConnectDialog;
import com.microsoft.azure.documentDB.dialog.ConnectDialog.ConnectAction;
import com.microsoft.azure.documentDB.dialog.ImportDialog;
import com.microsoft.azure.documentDB.factory.DocumentClientFactory;
import com.microsoft.azure.documentDB.model.JsonModel;
import com.microsoft.azure.documentDB.ui.StandardComboBoxUI;
import com.microsoft.azure.documentDB.ui.StandardScrollBarUI;
import com.microsoft.azure.documentDB.util.WidgetUtils.ButtonManager;
import com.microsoft.azure.documentDB.widget.FilteredTree;
import com.microsoft.azure.documentDB.widget.GlassPane;
import com.microsoft.azure.documentDB.widget.ImageButton;
import com.microsoft.azure.documentDB.widget.SearchTextField;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;

@SuppressWarnings("serial")
public class Main extends JPanel {

	final JFrame frame;

	private FilteredTree tableTree;
	private JXTreeTable dataTable;
	private JXMultiSplitPane mainSplitPane;
	private JPanel rowPanel;
	private JLabel rowLabel;
	private JEditorPane cellPane;
	private JLabel fileNameLabel;

	private JXButton headButton;
	private JXButton nextButton;

	JXButton searchButton;

	private SearchTextField searchField;
	private JXTextField globalSearchField;

	private JXLabel statusBar;

	final GlassPane glassPane;

	private final ButtonManager repositoryButtons;
	private final ButtonManager graphButtons;
	private final ButtonManager metadataButtons;

	private DocumentClient documentClient = null;

	final static public ResourceBundle TEXTS = PropertyResourceBundle.getBundle("locale.Texts");

	/**
	 * Controller
	 * 
	 * @param frame
	 *            the main frame
	 * 
	 * @throws Exception
	 *             thrown if the frame could not be created
	 * 
	 */
	public Main(final JFrame frame) throws Exception {

		this.frame = frame;

		List<Image> images = new LinkedList<Image>();

		this.repositoryButtons = new ButtonManager();
		this.graphButtons = new ButtonManager();
		this.metadataButtons = new ButtonManager();

		statusBar = new JXLabel("Ready");
		statusBar.setBorder(new EmptyBorder(5, 5, 10, 5));

		images.add(createImageIcon("/images/clouds-16.png").getImage());
		images.add(createImageIcon("/images/clouds-24.png").getImage());
		images.add(createImageIcon("/images/clouds-32.png").getImage());
		images.add(createImageIcon("/images/clouds-48.png").getImage());
		images.add(createImageIcon("/images/clouds-64.png").getImage());
		images.add(createImageIcon("/images/clouds-128.png").getImage());
		images.add(createImageIcon("/images/clouds-512.png").getImage());

		frame.setIconImages(images);

		glassPane = new GlassPane(frame);

		frame.getRootPane().setGlassPane(glassPane);

		tableTree = new FilteredTree(new DefaultMutableTreeNode());
		tableTree.getTree().setRootVisible(false);
		tableTree.getTree().setRowHeight(36);

		tableTree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		rowPanel = new JPanel(new BorderLayout());
		rowPanel.setBackground(new Color(243, 247, 250));

		rowPanel.setBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()));

		rowLabel = new JLabel("");
		rowLabel.setPreferredSize(new Dimension(400, 40));

		cellPane = new JEditorPane("text/html", null);
		cellPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(cellPane);

		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		rowPanel.add(rowLabel, BorderLayout.NORTH);
		rowPanel.add(scrollPane, BorderLayout.CENTER);

		tableTree.getTree().setShowsRootHandles(true);
		tableTree.getTree().setDragEnabled(true);
		tableTree.getTree().setDropMode(DropMode.ON_OR_INSERT);

		tableTree.getTree().setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row,
						focused);

				if (row == 0) {
					setIcon(createImageIcon("/images/clouds-32.png"));
				} else {
					setIcon(createImageIcon("/images/cosmosdb.png"));
				}

				return component;

			}

			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				super.paint(g);

			}
		});

		dataTable = new JXTreeTable(new JsonModel());

		tableTree.getTree().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableTree.getTree()
						.getLastSelectedPathComponent();

				if (node == null) {
				}

				final Object nodeInfo = node.getUserObject();

				if (node.isLeaf() && nodeInfo instanceof Database) {

					glassPane.activate("Please Wait");

					SwingUtilities.invokeLater(new Runnable() {

						public void run() {

							rowLabel.setText("");
							cellPane.setText("");
							cellPane.repaint();

							try {
								fileNameLabel.setText(nodeInfo.toString());
								fileNameLabel.setFont(fileNameLabel.getFont().deriveFont(Font.BOLD, 16));

								enableGraphButtons(true);
								enableMetadataButtons(true);
								enableRepositoryButtons(false);

								searchField.setEnabled(true);
								searchButton.setEnabled(!globalSearchField.getText().isEmpty());

							} catch (Exception e) {

								TaskDialogs.showException(e);

								glassPane.deactivate();

							}

							glassPane.deactivate();

						}

					});

				} else {

					resetDisplay();

					enableNavigation(false);
					enableGraphButtons(false);
					enableRepositoryButtons(true);
					enableMetadataButtons(node == tableTree.getTree().getModel().getRoot());
					searchField.setEnabled(false);
					searchButton.setEnabled(false);

				}

			}

		});

		tableTree.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {

					final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tableTree.getTree()
							.getLastSelectedPathComponent();

					if (node == null) {

						return;

					}

					final Object nodeInfo = node.getUserObject();

					JPopupMenu popupMenu = new JPopupMenu();

					popupMenu.show(e.getComponent(), e.getX(), e.getY());

				}

			}

		});

		headButton = new JXButton(createImageIcon("/images/begin-icon.png"));
		nextButton = new JXButton(createImageIcon("/images/next-icon.png"));

		headButton.setBorderPainted(false);
		nextButton.setBorderPainted(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}

		});

		headButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}

		});

		searchField = new SearchTextField(createImageIcon("/images/filter-icon-16.png"), "Filter");
		searchField.setEnabled(false);
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

		});

		searchField.setMaximumSize(new Dimension(130, 32));

		JToolBar navigationBar = new JToolBar();

		navigationBar.setFloatable(false);
		navigationBar.setRollover(true);
		navigationBar.setOpaque(false);

		navigationBar.add(headButton);

		navigationBar.addSeparator(new Dimension(3, 0));

		navigationBar.add(nextButton);

		JScrollPane sp = new JScrollPane(dataTable);

		sp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		this.fileNameLabel = new JLabel("");

		JComponent component = new ListViewBuilder().border(new EmptyBorder(5, 5, 5, 0)).labelView(fileNameLabel)
				.listView(sp).filterView(wrap(searchField, 2, 0, 0, 1)).listBarView(navigationBar).build();

		component.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		cellPane.setBackground(new Color(243, 247, 250));
		cellPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(
				"(ROW (LEAF name=left weight=0.2) (LEAF name=middle weight=0.6) (LEAF name=right weight=0.2))");

		mainSplitPane = new JXMultiSplitPane();

		mainSplitPane.getMultiSplitLayout().setModel(modelRoot);
		mainSplitPane.getMultiSplitLayout().setLayoutByWeight(true);

		mainSplitPane.add(tableTree, "left");
		mainSplitPane.add(component, "middle");
		mainSplitPane.add(rowPanel, "right");

		cellPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		mainSplitPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent event) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

		});

		frame.pack();
	}

	/**
	 * Get the 'Split' Pane
	 * 
	 * @return the split pane
	 * 
	 */
	public JComponent getSplitPane() {

		return mainSplitPane;

	}

	/**
	 * Create the Main Tool Bar
	 * 
	 * @return a new created Tool Bar
	 * 
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();

		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		addAction(toolBar, "Connect", "/images/connect-icon-32.png", "/images/connect-icon-16.png",
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
						ConnectDialog dialog = new ConnectDialog(frame, "Connect to Azure Table Storage",
								new ConnectAction() {

									@Override
									public boolean connect(final Closeable closeable, final String host,
											final String key) throws Exception {
										try {

											documentClient = (new DocumentClientFactory(host, key)).getDocumentClient();

											populateTableTree();

											closeable.close();

											return true;

										} catch (Exception e) {
											e.printStackTrace();
										}

										return false;

									}
								});

						dialog.setVisible(true);

					};

				});

		toolBar.addSeparator();

		addAction(toolBar, "Import", "/images/upload-icon-24.png", "/images/connect-icon-16.png", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				ImportDialog dialog = new ImportDialog(frame, "Import CSV File", documentClient,
						new ImportDialog.ImportAction() {

							@Override
							public void OnComplete() {
							}

							@Override
							public void OnFailure() {
							}

						});

				dialog.setVisible(true);

			};

		});

		toolBar.add(Box.createHorizontalGlue());

		JPanel searchPanel = new JPanel(new BorderLayout());

		searchPanel.setPreferredSize(new Dimension(200, 28));
		searchPanel.setMaximumSize(new Dimension(200, 28));
		searchPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));

		globalSearchField = new JXTextField("Search") {

			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				super.paintComponent(g);

			}
		};

		globalSearchField.setEnabled(true);

		globalSearchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}

		});

		globalSearchField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		searchPanel.add(globalSearchField, BorderLayout.CENTER);
		searchPanel.setBackground(globalSearchField.getBackground());

		searchButton = new ImageButton(createImageIcon("/images/search-icon.png"));

		searchButton.setEnabled(false);

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			}

		});

		searchPanel.add(searchButton, BorderLayout.EAST);

		toolBar.add(searchPanel);

		enableNavigation(false);
		enableRepositoryButtons(false);
		enableGraphButtons(false);
		enableMetadataButtons(false);

		return toolBar;

	}

	/**
	 * Reset the Display - clear the list
	 * 
	 */
	private void resetDisplay() {

		rowLabel.setText("");
		cellPane.setText("");
		cellPane.repaint();

		fileNameLabel.setText("");

	}

	private void populateTableTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tables");

		tableTree.setRoot(root);
		FeedOptions queryOptions = new FeedOptions();
		queryOptions.setPageSize(-1);

		FeedResponse<Database> databaseList = documentClient.queryDatabases("select * from root", queryOptions);

		for (Database database : databaseList.getQueryIterable()) {

			root.add(new DefaultMutableTreeNode(new DatabaseContainer(database)));

		}

		tableTree.getTree().setRootVisible(true);

	}

	/**
	 * Create and show the GUI
	 * 
	 * @throws Exception
	 *             thrown if the GUI cannot be created
	 * 
	 */
	private static void createAndShowGUI() throws Exception {

		System.setProperty("apple.laf.useScreenMenuBar", "true");

		UIManager.put("ScrollBarUI", StandardScrollBarUI.class.getName());
		UIManager.put("ComboBoxUI", StandardComboBoxUI.class.getName());

		UIManager.put("Menu.margin", new Insets(5, 5, 5, 5));
		UIManager.put("MenuItem.margin", new Insets(5, 5, 5, 5));
		UIManager.put("MenuBar.background", new Color(210, 225, 240));
		UIManager.put("ToolBar.shadow", new Color(210, 225, 240));
		UIManager.put("ToolBar.background", new Color(210, 225, 240));
		UIManager.put("Panel.background", new Color(210, 225, 240));
		UIManager.put("ScrollPane.background", new Color(210, 225, 240));
		UIManager.put("Button.foreground", new Color(51, 102, 153));
		UIManager.put("activeCaptionBorder", new Color(51, 102, 153));
		UIManager.put("inactiveCaptionBorder", new Color(51, 102, 153));
		UIManager.put("Menu.selectionBackground", new Color(51, 102, 153));
		UIManager.put("Menu.selectionForeground", new Color(255, 255, 255));
		UIManager.put("MenuItem.selectionBackground", new Color(51, 102, 153));
		UIManager.put("MenuItem.selectionForeground", new Color(255, 255, 255));
		UIManager.put("Button.background", new Color(210, 225, 240));
		UIManager.put("ComboBox.buttonBackground", new Color(210, 225, 240));
		UIManager.put("ComboBox.background", Color.white);
		UIManager.put("TextField.background", Color.white);
		UIManager.put("TextArea.background", Color.white);
		UIManager.put("CheckBox.background", new Color(210, 225, 240));
		UIManager.put("Slider.background", new Color(210, 225, 240));
		UIManager.put("MenuBar.border", new EmptyBorder(0, 0, 0, 0));
		UIManager.put("ToolBar.border", new EmptyBorder(5, 5, 5, 5));
		UIManager.put("OptionPane.background", new EmptyBorder(5, 5, 5, 5));

		UIManager.put("Button.border",
				new CompoundBorder(new LineBorder(new Color(51, 102, 153)), new EmptyBorder(5, 5, 5, 5)));
		UIManager.put("ToogleButton.border",
				new CompoundBorder(new LineBorder(new Color(51, 102, 153)), new EmptyBorder(5, 5, 5, 5)));

		UIManager.put("ComboBox.editorBorder",
				new CompoundBorder(new LineBorder(new Color(210, 225, 240)), new EmptyBorder(5, 5, 5, 5)));

		UIManager.put("ComboBox.editorBorder", new EmptyBorder(0, 0, 0, 0));
		UIManager.put("ComboBox.buttonBorder",
				new CompoundBorder(new LineBorder(new Color(51, 102, 153)), new EmptyBorder(5, 5, 5, 5)));
		UIManager.put("Table.background", new Color(210, 225, 240));
		UIManager.put("Menu.background", new Color(210, 225, 240));
		UIManager.put("MenuItem.background", new Color(210, 225, 240));
		UIManager.put("ToggleButton.background", new Color(210, 225, 240));

		UIManager.put("ScrollBar.background", new Color(210, 225, 240));
		UIManager.put("ScrollBar.track", new Color(210, 225, 240));
		UIManager.put("ScrollBar.trackHighlight", new Color(51, 102, 153));
		UIManager.put("ScrollBar.thumb", new Color(51, 102, 153));
		UIManager.put("ScrollBar.thumbHighlight", new Color(51, 102, 153));

		UIManager.put("TableHeader.background", UIManager.get("ScrollBar.background"));
		UIManager.put("TableHeader.foreground", new Color(51, 102, 153));

		UIManager.put("FileChooser.upFolderIcon", createImageIcon("/images/folder-up-icon-16.png"));
		UIManager.put("FileChooser.homeFolderIcon", createImageIcon("/images/home-icon-16.png"));
		UIManager.put("FileChooser.newFolderIcon", createImageIcon("/images/new-folder-icon-16.png"));
		UIManager.put("FileChooser.listViewIcon", createImageIcon("/images/icon-view-icon-16.png"));
		UIManager.put("FileChooser.detailsViewIcon", createImageIcon("/images/list-view-icon-16.png"));

		UIManager.put("FileView.hardDriveIcon", createImageIcon("/images/disk-icon-16.png"));
		UIManager.put("FileView.directoryIcon", createImageIcon("/images/folder-icon-16.png"));
		UIManager.put("FileView.fileIcon", createImageIcon("/images/file-16.png"));

		UIManager
				.put("TableHeader.cellBorder",
						BorderFactory
								.createCompoundBorder(
										BorderFactory
												.createEmptyBorder(2, 1, 2,
														1),
										(BorderFactory.createCompoundBorder(
												BorderFactory.createMatteBorder(2, 2, 2, 2,
														((Color) UIManager.get("Button.shadow")).darker()),
												BorderFactory.createEmptyBorder(4, 4, 4, 4)))));

		UIManager.put("Button.border",
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
						BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		UIManager.put("ToggleButton.border",
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
						BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		UIManager.put("PopupMenu.border",
				BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()));

		UIManager.put("TextField.border",
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
						BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		UIManager.put("ComboBox.border",
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
						BorderFactory.createMatteBorder(2, 2, 2, 2, Color.white)));

		UIManager.put("ScrollPane.border",
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(2, 2, 2, 2, ((Color) UIManager.get("Button.shadow")).darker()),
						BorderFactory.createEmptyBorder(2, 2, 2, 2)));

		UIManager.put("TableHeader.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.BOLD, 14));
		UIManager.put("Menu.font", ((Font) UIManager.get("Menu.font")).deriveFont(Font.BOLD, 14));
		UIManager.put("MenuItem.font", ((Font) UIManager.get("Menu.font")).deriveFont(Font.BOLD, 14));
		UIManager.put("Button.font", ((Font) UIManager.get("Button.font")).deriveFont(Font.BOLD, 14));
		UIManager.put("Label.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("TextField.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("CheckBox.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("ComboBox.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("Table.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("Tree.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));
		UIManager.put("List.font", ((Font) UIManager.get("Label.font")).deriveFont(Font.PLAIN, 14));

		UIManager.put("Button.select", new Color(114, 156, 214));

		UIManager.setLookAndFeel(new PlasticXPLookAndFeel());

		JFrame frame = new JFrame("Azure Table Navigator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getRootPane().setBorder(BorderFactory.createEtchedBorder());
		frame.getRootPane().setBackground(Color.WHITE);

		Main controller = new Main(frame);

		frame.getContentPane().add(controller.createToolBar(), BorderLayout.NORTH);
		frame.getContentPane().add(controller.getSplitPane(), BorderLayout.CENTER);
		frame.getContentPane().add(controller.statusBar, BorderLayout.SOUTH);

		frame.pack();
		frame.setSize(2400, 1600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	/**
	 * Main Method
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("sun.java2d.noddraw", "true");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * Enable Repository Buttons
	 * 
	 * @param enable
	 *            'true' enable repository buttons, 'false' disable repository
	 *            buttons
	 */

	private void enableRepositoryButtons(boolean enable) {

		repositoryButtons.setEnabled(enable);

	}

	/**
	 * Enable/Disable the navigation buttons
	 * 
	 * @param enabled
	 *            'true' enabled, 'false' otherwise
	 */
	public void enableNavigation(boolean enabled) {

		enableNavigation(false, false);

	}

	/**
	 * Enable Navigation Buttons
	 * 
	 * @param top
	 *            'true' enable top button
	 * @param next
	 *            'true' enable next button
	 * 
	 */
	private void enableNavigation(boolean top, boolean next) {

		headButton.setEnabled(top);
		nextButton.setEnabled(next);

	}

	/**
	 * Enable Graph Buttons
	 * 
	 * @param enable
	 *            'true' enable graph buttons, 'false' disable graph buttons
	 * 
	 */
	private void enableGraphButtons(boolean enable) {

		graphButtons.setEnabled(enable);

	}

	/**
	 * Enable Metadata Buttons
	 * 
	 * @param enable
	 *            'true' enable metadata buttons, 'false' disable graph buttons
	 * 
	 */
	private void enableMetadataButtons(boolean enable) {

		metadataButtons.setEnabled(enable);

	}

	public static String local(String key) {
		try {

			return TEXTS.getString(key);

		} catch (Exception e) {
		}

		return "[" + key + "]";
	}

}