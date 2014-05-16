/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * DialogTemplate
 * @author suhyun47.kim
 *
 */
public class DialogTemplate extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;
	
	private String mTitle;
	private int mWidth;
	private int mHeight;
	private JPanel mMainPanel;
	
	public DialogTemplate(String pTitle, JPanel pPanel) {
		this (pTitle, pPanel, WIDTH, HEIGHT);
	}

	public DialogTemplate(String pTitle, JPanel pPanel, int pWidth, int pHeight) {
		this.mTitle = pTitle;
		this.mHeight = pHeight;
		this.mWidth = pWidth;
		this.mMainPanel = pPanel;

		init();
	}
	
	protected JPanel getMainPanel() {
		return mMainPanel;
	}
	
	private void init() {
	
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mMainPanel);
		
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setTitle(mTitle);
		this.setSize(mWidth, mHeight);
		this.setResizable(false);
		this.setModal(true);
		
		WindowUtil.locateCenter(this);

	}
}
