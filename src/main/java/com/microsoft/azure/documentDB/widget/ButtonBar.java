package com.microsoft.azure.documentDB.widget;

import java.awt.Color;

import com.l2fprod.common.swing.JButtonBar;

public class ButtonBar extends JButtonBar {
	private static final long serialVersionUID = 1L;

	public ButtonBar(int orientation) {
		super(orientation);
		setOpaque(false);

		setBackground(new Color(210, 225, 240));

	}

}
