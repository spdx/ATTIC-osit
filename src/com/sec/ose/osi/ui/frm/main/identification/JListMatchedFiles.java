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
package com.sec.ose.osi.ui.frm.main.identification;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;

/**
 * JListMatchedFiles
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
@SuppressWarnings("unchecked")
public class JListMatchedFiles extends JList {
	private static Log log = LogFactory.getLog(JListMatchedFiles.class);

	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> pendingFileList = null;
	private ArrayList<String> identifiedFileList = null;

	public JListMatchedFiles() {
		
		super(new DefaultListModel());
		ListCellRenderer renderer = new IdentificationListCellRenderer();
		this.setCellRenderer(renderer);
		this.addListSelectionListener(new FileSelectionListener());
		
		
	}

	private void setListSelectedIndex(int index) {
		if(index >= 0) {
			this.setSelectedIndex(index);
			setListSelectedPath();
		} 
	}

	void setIdentificationList(ArrayList<String> pendingFilePath, ArrayList<String> IdentifiedFilePath) {
		
		Vector<String> filePathList=null;
		pendingFileList = pendingFilePath;
		identifiedFileList = IdentifiedFilePath;

			filePathList = new Vector<String>();
			
			for(String str:pendingFilePath){
				filePathList.add(str);
			}
			for(String str:IdentifiedFilePath){ 
				filePathList.add(str);
			}
			 
			this.removeAll();
			this.setListData(filePathList);

	}

	int getIndexOfFilePath(String selectedFilePath) {
		ListModel model = this.getModel();
		
		for(int i = 0; i < model.getSize(); i++) {
			if(selectedFilePath.equals(model.getElementAt(i))) {
				return i;
			}
		}
		return -1;
	}

	private boolean isExistInJList(String filePath) {
		ListModel model = this.getModel();
		
		for(int i = 0; i < model.getSize(); i++) {
			if(filePath.equals(model.getElementAt(i))) {
				return true;
			}
		}
		return false;
	}

	public void setSelectPointer(String filePath) {
		if(filePath != null && isExistInJList(filePath)) {
			try {
				this.setSelectedValue(filePath, true);
			} catch (Exception e) {
				log.error(this, e);
				BackgroundJobManager.getInstance().restartAllBackgroundThread();
			}
		} else {
			setListSelectedIndex(0);
		}
	}

	@SuppressWarnings("deprecation")
	private void setListSelectedPath() {

		ArrayList<String> selectedFiles = new ArrayList<String>();
		Object[] selected = this.getSelectedValues();
		if(selected == null || selected.length==0)
			return;
		
		for(int i=0; i<selected.length; i++) {
			Object file = selected[i];
			if(file != null)
				selectedFiles.add(file.toString());
		}

		int pathType = 0;
		if(selected.length <= 1) {
			pathType = SelectedFilePathInfo.SINGLE_FILE_TYPE;
		} else {
			pathType = SelectedFilePathInfo.MULTIPLE_FILE_TYPE;
		}
		
		IdentifyMediator.getInstance().setSelectedFiles(
				pathType, 
				selectedFiles);
	}

	@SuppressWarnings("deprecation")
	private int getSelectedCount() {
		return getSelectedValues().length;
	}
	
	/** File Selection Action **/ 
	class FileSelectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {

			if(IdentifyMediator.getInstance().getIndexOfTreeOrList() == JPanIdentifyMain.INDEX_LIST ) {

				log.debug("######## ListSelectionEvent ~~~~");
				int selectedCount = getSelectedCount();
				if(selectedCount > 1) {
					IdentifyMediator.getInstance().setOpenCodeMatchSourceView(SelectedFilePathInfo.MULTIPLE_FILE_TYPE);
				} else {
					IdentifyMediator.getInstance().setOpenCodeMatchSourceView(SelectedFilePathInfo.SINGLE_FILE_TYPE);
				}
				setListSelectedPath();
				
				String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
				IdentifyMediator.getInstance().refreshChildFrames(projectName);
			}
		}
		
		
	}
	
	

	class IdentificationListCellRenderer implements ListCellRenderer {
		
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
		      int index, boolean isSelected, boolean cellHasFocus) {
		    JLabel renderer = (JLabel) defaultRenderer
		    .getListCellRendererComponent(list, value, index, isSelected,
		    		cellHasFocus);
		    renderer.setEnabled(list.isEnabled());
	        renderer.setFont(list.getFont());
	        renderer.setOpaque(true);
		    renderer.setText((String)value);
		    
			ArrayList<String> alIdentifiedFiles = 
				MatchedInfoMgr.getInstance().getIdentifiedFilePathListByCurrentMatchType();
			if(alIdentifiedFiles == null) { 
				return renderer;
			}
			
		    if(alIdentifiedFiles.contains((String)value)){
		    	renderer.setForeground(ColorDic.IDENTIFIED_COLOR_BLUE);
		    } else {
		    	renderer.setForeground(ColorDic.PENDING_COLOR_BLACK);
		    }
		    return renderer;
		 }
	}

	public void changeSelectedIndex(int processType) {
		String settingPath = "";

		if(pendingFileList.size() <= 0 &&
		   identifiedFileList.size() <= 0) {
			IdentifyMediator.getInstance().changeFileNavigationPanel(JPanIdentifyMain.INDEX_TREE);
			return;
		}
		
		switch(processType) {
			case IdentificationConstantValue.IDENTIFY_TYPE:
				if(pendingFileList.size() <= 0) {
					settingPath = identifiedFileList.get(0);
				} else {
					settingPath = pendingFileList.get(0);
				}
				break;
			case IdentificationConstantValue.RESET_TYPE:
				if(identifiedFileList.size() <= 0) {
					settingPath = pendingFileList.get(0);
				} else {
					settingPath = identifiedFileList.get(0);
				}
				break;
		}
		
		setSelectedFile(settingPath);

	}

	public void setSelectedFile(String filePath) {

		int listIdx = getIndexOfFilePath(filePath);
		
		if(listIdx >= 0) {
			this.setSelectedIndex(listIdx);
		} else {

			setListSelectedIndex(0);
			log.debug("setSelectedFile");
			
		}
		
		this.ensureIndexIsVisible(this.getSelectedIndex());
	}

}
