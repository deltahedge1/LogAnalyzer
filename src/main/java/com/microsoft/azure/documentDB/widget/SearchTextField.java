package com.microsoft.azure.documentDB.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXTextField;

@SuppressWarnings("serial")
class IconTextField extends JXTextField {

	private Icon icon;

	private int iconWidth = 0;
	private int iconHeight = 0;

	public IconTextField(Icon icon) {
		super();
		this.icon = icon;

		iconWidth = icon.getIconWidth();
		iconHeight = icon.getIconHeight();

		setBorder(new EmptyBorder(7, 7 + iconWidth, 10, 10));

	}

	public Icon getIcon() {
		return this.icon;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int x = 5; // this is our icon's x
		int y = (this.getHeight() - iconHeight) / 2;
		icon.paintIcon(this, g, x, y);

	}

}

@SuppressWarnings("serial")
public class SearchTextField extends IconTextField implements FocusListener {

	private String textWhenNotFocused;

	/**
	 * Constructor for the Search specifying both the text and icon
	 * 
	 * @param text
	 *            the text to display when empty and not in focus
	 * @param icon
	 *            the icon
	 * 
	 */
	public SearchTextField(Icon icon, String text) {
		super(icon);

		this.textWhenNotFocused = text;
		this.addFocusListener(this);

	}

	/**
	 * Constructor for the Search specifying the icon only
	 * 
	 * @param icon
	 *            the icon
	 * 
	 */
	public SearchTextField(Icon icon) {
		this(icon, "Search...");

	}

	/**
	 * Return the text when focus is lost
	 * 
	 * @return the focus 'lost' text
	 * 
	 */
	public String getTextWhenNotFocused() {
		return this.textWhenNotFocused;
	}

	/**
	 * Set the the focus is lost
	 * 
	 * @param text
	 *            The new text
	 * 
	 */
	public void setTextWhenNotFocused(String text) {
		this.textWhenNotFocused = text;
	}

	/**
	 * Paint the Button
	 * 
	 * @param graphics
	 *            the Graphics Context
	 * 
	 */
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		if (!this.hasFocus() && this.getText().equals("")) {
			int height = this.getHeight();
			Font prev = graphics.getFont();
			Font italic = prev.deriveFont(Font.ITALIC);
			Color prevColor = graphics.getColor();

			graphics.setFont(italic);
			graphics.setColor(UIManager.getColor("textInactiveText"));
			int h = graphics.getFontMetrics().getHeight();
			int textBottom = (height - h) / 2 + h - 4;
			int x = this.getInsets().left;
			Graphics2D g2d = (Graphics2D) graphics;
			RenderingHints hints = g2d.getRenderingHints();
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.drawString(textWhenNotFocused, x, textBottom);
			g2d.setRenderingHints(hints);
			graphics.setFont(prev);
			graphics.setColor(prevColor);

		}

	}

	/**
	 * Focus Listener (Gained)
	 * 
	 * @param e
	 *            the Focus Event
	 * 
	 */
	public void focusGained(FocusEvent e) {
		this.repaint();
	}

	/**
	 * Focus Listener (Lost)
	 * 
	 * @param e
	 *            the Focus Event
	 * 
	 */
	public void focusLost(FocusEvent e) {
		this.repaint();
	}

}