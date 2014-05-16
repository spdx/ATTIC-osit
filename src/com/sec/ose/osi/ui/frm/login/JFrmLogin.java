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
package com.sec.ose.osi.ui.frm.login;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.sec.ose.osi.Version;
import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * JFrmLogin
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JFrmLogin extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final String TITLE = "Login - "+Version.getApplicationVersionInfo() ;
	private JPanLogin jPanLogin = null; 
		
	public JFrmLogin() {
		init();
	}

	private void init() {

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		JPanLogin loginPanel = getJPanLogin();
		cp.add(loginPanel);
		
		this.setSize(507, 220);
		this.setTitle(TITLE);
		this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
		this.setResizable(false);

		
		WindowUtil.locateCenter(this);
		
	}

	private JPanLogin getJPanLogin() {
		if(jPanLogin == null) {
			jPanLogin = new JPanLogin(this);
		}
		return jPanLogin;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible)	getJPanLogin().setFocus();
	}

	public void setPasswordText(String content) {
		jPanLogin.setJPasswordField(content);
	}

}
