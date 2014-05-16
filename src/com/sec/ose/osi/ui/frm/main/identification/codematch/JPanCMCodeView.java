/**
 * Copyright(C) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package com.sec.ose.osi.ui.frm.main.identification.codematch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import com.sec.ose.osi.ui.ConstantValue;

/**
 * JPanCMCodeView
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JPanCMCodeView extends JPanel {
	private static final long serialVersionUID = -3545279576280077737L;
	private static final String NO_SOURCE_CODE_AVAILABLE = "No source code available.";
	private static MutableAttributeSet DEFAULT_ATTR = new SimpleAttributeSet();
	
	JScrollPane jScrollPane;
	JTextPane jTextPaneLineNumber;
	JTextPane jTextPaneSourceView;
	StyledDocument styledDocument;
	int ComparedBaseline = 0;
	
	JPanCMCodeView siblingSourceView = null;
	
	private JPanel jPanelNavigator = null;
	
	public JPanCMCodeView(String direction) {
		if(direction.equals("LEFT")) {
			initLeft();
		} else if(direction.equals("RIGHT")) {
			initRight();
		}
	}
	
	private void initLeft() {
		this.setLayout(new BorderLayout());
		this.add(getJPanelNavigator(), BorderLayout.WEST);
		this.add(getJScrollPane(), BorderLayout.CENTER);
	}
	private void initRight() {
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane());
	}
	
	public JPanel getJPanelNavigator() {
		if (jPanelNavigator == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanelNavigator = new JPanel();
			jPanelNavigator.setLayout(null);
			jPanelNavigator.setPreferredSize(new Dimension(50, 10));
			jPanelNavigator.setBackground(Color.white);
			jPanelNavigator.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		}
		return jPanelNavigator;
	}

	public JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			JPanel LeftSourceCodePanel = new JPanel();
			LeftSourceCodePanel.setLayout(new BorderLayout());
			LeftSourceCodePanel.add(getJTextPaneLineNumber(), BorderLayout.WEST);
			LeftSourceCodePanel.add(getJTextPaneSourceView(), BorderLayout.CENTER);
			jScrollPane.setViewportView(LeftSourceCodePanel);

			jScrollPane.getVerticalScrollBar().setUnitIncrement(ConstantValue.CODE_SCROLL_SIZE);
			
			jScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						if(siblingSourceView != null) {
							siblingSourceView.changeScrollBar(changeLine());
						}						
					}
				}
			);

		}
		return jScrollPane;
	}

	public void setBaseline(int line) {
		ComparedBaseline = line * ConstantValue.CODE_SCROLL_SIZE;
	}
	
	public void changeScrollBar(int changeValue) {
		if(jScrollPane.getVerticalScrollBar().getValue() != (ComparedBaseline + changeValue)) {
			jScrollPane.getVerticalScrollBar().setValue(ComparedBaseline + changeValue);
		}
	}
	
	public int changeLine() {
		return jScrollPane.getVerticalScrollBar().getValue() - ComparedBaseline;
	}

	protected JTextPane getJTextPaneSourceView() {
		if (jTextPaneSourceView == null) {
			jTextPaneSourceView = new JTextPane();
			jTextPaneSourceView.setEditable(false);
			styledDocument = jTextPaneSourceView.getStyledDocument();
			
		}
		return jTextPaneSourceView;
	}

	protected StyledDocument getStyledDocumentForSourcePane() {
		return getJTextPaneSourceView().getStyledDocument();
	}

	protected JTextPane getJTextPaneLineNumber() {
		if (jTextPaneLineNumber == null) {
			jTextPaneLineNumber = new JTextPane();
			jTextPaneLineNumber.setEditable(false);
			jTextPaneLineNumber.setEnabled(false);
		}
		return jTextPaneLineNumber;
	}

	public void clear() {
		getJTextPaneLineNumber().setText("");
		getJTextPaneSourceView().setText(NO_SOURCE_CODE_AVAILABLE);
		clearStyle();
		
	}

	public void clearStyle() {
		getStyledDocumentForSourcePane()
		.setCharacterAttributes(0,
				getStyledDocumentForSourcePane().getLength(), 
				DEFAULT_ATTR, 
				true);
		
	}

	public void setText(String sourceCode) {
		this.getJTextPaneSourceView().setText(sourceCode);
		this.getJTextPaneSourceView().setCaretPosition(0);
		
	}

	public void setText(String sourceCode, String lineNumber) {
		this.getJTextPaneLineNumber().setText(lineNumber);
		this.setText(sourceCode);
		
	}

	public void addScrollAdjustmentObserver(JPanCMCodeView observer) {
		siblingSourceView = observer;
	}
	
	public JPanCMCodeView getSiblingSourceView() {
		return siblingSourceView;
	}
}
