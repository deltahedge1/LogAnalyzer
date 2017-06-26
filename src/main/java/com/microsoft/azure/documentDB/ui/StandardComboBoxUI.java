package com.microsoft.azure.documentDB.ui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class StandardComboBoxUI extends BasicComboBoxUI {

	public static ComponentUI createUI(JComponent c) {
		return new StandardComboBoxUI();
	}

	protected JButton createArrowButton() {
		JButton button = new BasicArrowButton(BasicArrowButton.SOUTH);

		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBackground(Color.white);
		return button;

	}

}
