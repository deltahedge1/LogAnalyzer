package com.microsoft.azure.documentDB.ui;

import static com.microsoft.azure.documentDB.util.WidgetUtils.createImageIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.jdesktop.swingx.JXButton;

@SuppressWarnings("serial")
class BorderlessButtonUI extends BasicButtonUI implements java.io.Serializable, MouseListener, KeyListener {

	private final static BorderlessButtonUI buttonUI = new BorderlessButtonUI();

	protected Border borderRaised = UIManager.getBorder("Button.border");
	protected Border borderLowered = UIManager.getBorder("Button.borderPressed");
	protected Color backgroundNormal = UIManager.getColor("Button.background");
	protected Color backgroundPressed = UIManager.getColor("Button.pressedBackground");
	protected Color foregroundNormal = UIManager.getColor("Button.foreground");
	protected Color foregroundActive = UIManager.getColor("Button.activeForeground");
	protected Color focusBorder = UIManager.getColor("Button.focusBorder");

	BorderlessButtonUI() {
	}

	public static ComponentUI createUI(JComponent c) {
		return buttonUI;
	}

	public void installUI(JComponent c) {
		super.installUI(c);

		c.addMouseListener(this);
		c.addKeyListener(this);
	}

	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeMouseListener(this);
		c.removeKeyListener(this);
	}

	public void paint(Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;
		Dimension d = b.getSize();

		g.setColor((Color) UIManager.get("ScrollBar.background"));
		g.fillRect(0, 0, b.getWidth(), b.getHeight());

		g.setFont(c.getFont());
		FontMetrics fm = g.getFontMetrics();

		g.setColor(b.getBackground());
		String caption = b.getText();
		int x = (d.width - fm.stringWidth(caption)) / 2;
		int y = (d.height + fm.getAscent()) / 2;
		g.drawString(caption, x, y);
		g.drawImage(((ImageIcon) b.getIcon()).getImage(), 2, 2, b.getIcon().getIconWidth(), b.getIcon().getIconHeight(),
				null);

	}

	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		if (borderRaised != null) {
			Insets ins = borderRaised.getBorderInsets(c);
			d.setSize(d.width + ins.left + ins.right, d.height + ins.top + ins.bottom);
		}
		return d;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setBorder(borderLowered);
		c.setBackground(backgroundPressed);
	}

	public void mouseReleased(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setBorder(borderRaised);
		c.setBackground(backgroundNormal);
	}

	public void mouseEntered(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setForeground(foregroundActive);
		c.repaint();
	}

	public void mouseExited(MouseEvent e) {
		JComponent c = (JComponent) e.getComponent();
		c.setForeground(foregroundNormal);
		c.repaint();
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
			JComponent c = (JComponent) e.getComponent();
			c.setBorder(borderLowered);
			c.setBackground(backgroundPressed);
		}
	}

	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
			JComponent c = (JComponent) e.getComponent();
			c.setBorder(borderRaised);
			c.setBackground(backgroundNormal);
		}
	}

}

public class StandardScrollBarUI extends BasicScrollBarUI {

	public static ComponentUI createUI(JComponent c) {
		return new StandardScrollBarUI();
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {

		return orientation == SwingConstants.NORTH ? createButton("/images/arrow-up-16.png", 22, 18)
				: createButton("/images/arrow-left-16.png", 20, 14);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {

		return orientation == SwingConstants.SOUTH ? createButton("/images/arrow-down-16.png", 22, 18)
				: createButton("/images/arrow-right-16.png", 18, 14);

	}

	private JButton createButton(String imageFile, int width, int height) {
		JXButton button = new JXButton();

		button.setPreferredSize(new Dimension(width, height));
		button.setMinimumSize(new Dimension(width, height));
		button.setMaximumSize(new Dimension(width, height));

		button.setIcon(createImageIcon(imageFile));

		button.setBorderPainted(false);

		button.setUI(new BorderlessButtonUI());

		return button;

	}

	@Override
	public Dimension getPreferredSize(JComponent c) {

		return new Dimension(20, 20);

	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

		g.setColor(SystemColor.scrollbar);
		g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
	}

}
