package com.microsoft.azure.documentDB.ui;

/*
 * Twirl Editor - Petri-Net Editor
 * 
 * Copyright (C) 2009 Neil Brittliff
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class AnnotatedSliderUI extends BasicSliderUI implements MouseMotionListener, MouseListener {
	final JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem();
	JSlider slider;
	String scale;

	public AnnotatedSliderUI(JSlider slider) {

		this(slider, "");

	}

	public AnnotatedSliderUI(JSlider slider, String scale) {
		super(slider);

		this.slider = slider;
		this.scale = scale;

		slider.addMouseMotionListener(this);
		slider.addMouseListener(this);
		pop.add(item);

		pop.setDoubleBuffered(true);

	}

	public void showToolTip(MouseEvent me) {

		item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/increase-icon-24.png")));
		item.setText(slider.getValue() + scale);

		// limit the tooltip location relative to the slider

		pop.show(me.getComponent(), me.getComponent().getBounds().width, 3);

		item.setArmed(false);

	}

	public void mouseDragged(MouseEvent me) {
		showToolTip(me);
	}

	public void mouseMoved(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		showToolTip(me);
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mouseReleased(MouseEvent me) {
		pop.setVisible(false);
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

}