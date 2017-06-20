package com.microsoft.azure.documentDB.widget;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.jdesktop.swingx.JXButton;

@SuppressWarnings("serial")
public class ImageButton extends JXButton {
	float alpha = 0.5f;

	public class MouseListener extends MouseAdapter {

		private void sleepQuietly(long sleep) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}

		}

		public void mouseExited(MouseEvent me) {

			new Thread(new Runnable() {
				public void run() {

					if (!isEnabled()) {
						return;
					}

					for (float i = 1f; i >= .5f; i -= .03f) {
						setAlpha(i);
						sleepQuietly(10);
					}

				}

			}).start();

		}

		public void mouseEntered(MouseEvent me) {
			new Thread(new Runnable() {
				public void run() {

					if (!isEnabled()) {
						return;
					}

					for (float i = .5f; i <= 1f; i += .03f) {
						setAlpha(i);
						sleepQuietly(10);
					}

				}
			}).start();
		}

		public void mousePressed(MouseEvent me) {
			new Thread(new Runnable() {
				public void run() {

					if (!isEnabled()) {
						return;
					}

					for (float i = 1f; i >= 0.6f; i -= .1f) {

						setAlpha(i);
						sleepQuietly(1);
					}

				}

			}).start();
		}

	}

	public ImageButton(Icon icon) {
		super(icon);

		setBorder(null);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setOpaque(false);
		setFocusPainted(false);

		addMouseListener(new MouseListener());

	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		repaint();
	}

	public void paintComponent(Graphics graphics) {
		Graphics2D graphics2 = (Graphics2D) graphics;

		graphics2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		super.paintComponent(graphics2);

	}

}
