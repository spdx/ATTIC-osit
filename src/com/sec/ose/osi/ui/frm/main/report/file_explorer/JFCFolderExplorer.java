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
package com.sec.ose.osi.ui.frm.main.report.file_explorer;

import java.awt.Component;
import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.util.Property;

/**
 * JFCFolderExplorer
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JFCFolderExplorer {
	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public  static final int INVALID_DIRECTORY = -1;
	private volatile static JFCFolderExplorer instance = null;
	private JFileChooser mFileChooser = null;
	
	private JFCFolderExplorer() {
		
		mFileChooser = new JFileChooser();
		mFileChooser.setLocale(Locale.ENGLISH);
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mFileChooser);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mFileChooser.setMultiSelectionEnabled(false);
		mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		String strDir = Property.getInstance().getProperty(Property.DEFALT_REPORT_LOCATION);
		File curDir = new File(strDir); 
		
		if(curDir.exists())
			mFileChooser.setCurrentDirectory(curDir);
		
	}

	public static JFCFolderExplorer getInstance() {
		if(instance == null) {
			synchronized(JFCFolderExplorer.class) {
				if(instance == null) {
					instance = new JFCFolderExplorer();
				}
			}
		}
		return instance;
	}

	public int showSaveDialog() {
		Component parent = UISharedData.getInstance().getCurrentFrame();
		mFileChooser.setDialogTitle("Select Default Report Location");
		return mFileChooser.showDialog(parent, null);
	}

	public File getSelectedFile() {
		return mFileChooser.getSelectedFile();
	}

	public int showSaveDialog(String strCurDir) {

		File dir = new File(strCurDir);
		if(dir.isDirectory() == true) {
			mFileChooser.setCurrentDirectory(dir);
		} else {
			mFileChooser.setCurrentDirectory(null);
		}
		
		return this.showSaveDialog();
	}

	public int showBrowser(JFrame frame) {
		mFileChooser.setDialogTitle("Select Location");
		mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		return mFileChooser.showOpenDialog(frame);
	}

	public JFileChooser getJFileChooser() {
		return mFileChooser;
	}
}
