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
package com.sec.ose.osi.ui.dialog.about;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.Version;

/**
 * Panel for OSI License Display
 * 
 * @author suhyun47.kim, sjh.yoo
 *
 */
public class JPanAbout extends JPanel {
	private static Log log = LogFactory.getLog(JPanAbout.class);
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPaneAbout = null;
	private JTextArea jTextAreaAbout = null;

	public JPanAbout() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getJScrollPaneAbout(), BorderLayout.CENTER);
	}

	private JScrollPane getJScrollPaneAbout() {
		if (jScrollPaneAbout == null) {
			jScrollPaneAbout = new JScrollPane();
			jScrollPaneAbout.setViewportView(getJTextAreaAbout());
			
		}
		return jScrollPaneAbout;
	}

	public JTextArea getJTextAreaAbout() {
		if (jTextAreaAbout == null) {
			jTextAreaAbout = new JTextArea();
			jTextAreaAbout.setEditable(false);
			jTextAreaAbout.setLineWrap(true);
			jTextAreaAbout.setMargin(new Insets(15,15,15,15));
			jTextAreaAbout.setText(getLicenseText());
		}
		return jTextAreaAbout;
	}

	private static final String LICENSE_FILE = "LICENSE";
	public static String getLicenseText(){
		StringBuffer opensourceAnnouncement = new StringBuffer(Version.getApplicationVersionInfo() + "\n");
		FileReader fReader = null;
		try {
			fReader = new FileReader(new File(LICENSE_FILE));
			BufferedReader bReader = new BufferedReader(fReader);

			String tempOpensourceAnnouncement = null;
			while((tempOpensourceAnnouncement=bReader.readLine())!= null){
				 opensourceAnnouncement.append(tempOpensourceAnnouncement + "\n");
			}
		} catch (FileNotFoundException e) {
			log.warn(e);
		} catch (IOException e) {
			log.warn(e);
		} finally {
			if(fReader != null) {
				try {
					fReader.close();
				} catch (IOException e) {
					log.warn(e);
				} finally {
					fReader = null;
				}
			}
		}
			
		return opensourceAnnouncement.toString();
	}
}
