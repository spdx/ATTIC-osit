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
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.util.Property;

/**
 * JFCExcelFileSaveExplorer
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JFCExcelFileSaveExplorer {
	
	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	private volatile static JFCExcelFileSaveExplorer instance = null;

	private JFCExcelFileSaveExplorer() {
		
		mFileChooser = new JFileChooser();
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mFileChooser);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mFileChooser.setMultiSelectionEnabled(false);
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		mFileChooser.setAcceptAllFileFilterUsed(false);
		
		mFileChooser.setFileFilter(
				new FileNameExtensionFilter("Exel file", "xlsx", "XLSX")
				);
		
		String strDir = Property.getInstance().getProperty(Property.DEFALT_REPORT_LOCATION);
		File curDir = new File(strDir); 
		
		if(curDir.exists())
			mFileChooser.setCurrentDirectory(curDir);

		mFileChooser.setPreferredSize(new Dimension(700,500));
	}

	public static JFCExcelFileSaveExplorer getInstance() {
		if(instance == null) {
			synchronized(JFCExcelFileSaveExplorer.class) {
				if(instance == null) {
					instance = new JFCExcelFileSaveExplorer();
				}
			}
		}
		return instance;
	}

	public String getSelectedFilePath() {
		
		String fileParentPath = mFileChooser.getSelectedFile().getParent();
		String fileName = mFileChooser.getSelectedFile().getName();
		
		int dotIdx = fileName.lastIndexOf('.');

		if(dotIdx < 0) {
			fileName += ".xlsx";
		}

		else {
			fileName = fileName.substring(0, dotIdx)+".xlsx";
		}
		
		File newFile = new File(fileParentPath, fileName);
		
		return newFile.getPath();
	}

	public int showSaveDialog(String strCurFile) {
		File selectedFile = new File(strCurFile);
		Component parent = UISharedData.getInstance().getCurrentFrame();
		
		if(selectedFile.isFile() == true) {
			mFileChooser.setCurrentDirectory(selectedFile.getParentFile());
			mFileChooser.setSelectedFile(selectedFile);
			
		}
		
		return mFileChooser.showDialog(parent, "Select");
	}

	public int showOpenDialog(String strCurFile) {
		File selectedFile = new File(strCurFile);
		if(selectedFile.isFile() == true) {
			mFileChooser.setCurrentDirectory(selectedFile.getParentFile());
			mFileChooser.setSelectedFile(selectedFile);
		}
		
		Component parent = UISharedData.getInstance().getCurrentFrame();
		
		int result;
		File selected = null;
		
		do {
			result =mFileChooser.showDialog(parent, "Open");;
			if(result == JFileChooser.CANCEL_OPTION)
				break;
			
			selected = mFileChooser.getSelectedFile();
		} while (selected == null || selected.exists() == false);
		
		return result;
	}

	private JFileChooser mFileChooser;
}
