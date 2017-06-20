package com.microsoft.azure.documentDB.widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/*
 *  Simple implementation of a Glass Pane that will capture and ignore all
 *  events as well paint the glass pane to give the frame a "disabled" look.
 *
 *  The background color of the glass pane should use a color with an
 *  alpha value to create the disabled look.
 */
@SuppressWarnings("serial")
public class GlassPane extends JComponent implements KeyListener {
	private final static Border MESSAGE_BORDER = new EmptyBorder(10, 10, 10, 10);
	private JLabel message = new JLabel();

	JFrame frame;

	public GlassPane(JFrame frame) {
		// Set glass pane properties

		this.frame = frame;

		setOpaque(false);
 		Color background = new Color(250, 250, 250, 128);
	 	setBackground(background);
		setLayout(new GridBagLayout());

		// Add a message label to the glass pane

		add(message, new GridBagConstraints());
		message.setOpaque(false);
		message.setBorder(MESSAGE_BORDER);
		message.setIcon(createImageIcon("/images/hourglass-icon.png"));

		// Disable Mouse, Key and Focus events for the glass pane

		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseMotionAdapter() {
		});

		addKeyListener(this);

		setFocusTraversalKeysEnabled(false);

	}

	/*
	 * The component is transparent but we want to paint the background to give
	 * it the disabled look.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getSize().width, getSize().height);
	}

	/*
	 * The background color of the message label will be the same as the
	 * background of the glass pane without the alpha value
	 * 
	 * @param background The Background Color
	 */
	@Override
	public void setBackground(Color background) {
		super.setBackground(background);

		Color messageBackground = new Color(background.getRGB());
		message.setBackground(messageBackground);

	}

	//
	// Implement the KeyListener to consume events
	//
	public void keyPressed(KeyEvent e) {
		e.consume();
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		e.consume();
	}

	/*
	 * Make the glass pane visible and change the cursor to the wait cursor
	 * 
	 * A message can be displayed and it will be centered on the frame.
	 */
	public void activate(String text) {

		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		message.setVisible(true);
		message.setText(text);
		message.setForeground(getForeground());
		message.invalidate();

		this.setVisible(true);
		this.repaint();

		requestFocusInWindow();

	}

	/*
	 * Hide the glass pane and restore the cursor
	 */
	public void deactivate() {

		frame.setCursor(null);
		setVisible(false);

	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GlassPane.class.getResource(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}