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
package com.sec.ose.osi.ui.frm.main.manage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.ProjectAnalysisInfo;
import com.sec.ose.osi.thread.job.analysis.AnalysisMonitorThread;
import com.sec.ose.osi.thread.job.identify.data.IdentifiedController;
import com.sec.ose.osi.ui.frm.main.report.file_explorer.JFCFolderExplorer;

/**
 * FileBrowser
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class FileBrowser extends JPanel implements ActionListener{
	private static Log log = LogFactory.getLog(FileBrowser.class);
	
	private static final long serialVersionUID = -9193893193113041608L;
	JLabel txtLocation = null;
	JButton btnBrowse = null;
	
	Dimension txtSize = new Dimension(100,20);
	Dimension btnSize = new Dimension(85,20);
	
	FileBrowser fb = null;
	String sDefaultPath = System.getProperty("user.home");
	

	JFrame frmOwner = null;
	Color selected_color = new Color(184,207,229);

	ManagedProjectTableModel projectModel;
	OSIProjectInfo projectInfo;
	ProjectAnalysisInfo mManagedprojectInfo;
	
	public FileBrowser(){
		txtLocation = new JLabel();
		setLocationSize(txtSize);
		txtLocation.setOpaque(true);
		txtLocation.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		txtLocation.setBackground(Color.white);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(this);
		setButtonSize(btnSize);

		Border boder = BorderFactory.createEmptyBorder(2,2,2,2);

		this.setLayout(new BorderLayout(2,2));
		this.add(BorderLayout.CENTER, txtLocation);
		this.add(BorderLayout.EAST, btnBrowse);
		this.setBackground(Color.white);
		this.setBorder(boder);
	}
	public void setOwner(JFrame f){
		this.frmOwner = f;
	}
	
	public void setDefaultLocation(String sPath){
		sDefaultPath = sPath;
	}
	
	public void actionPerformed(ActionEvent e){
		
		JFCFolderExplorer explorer = JFCFolderExplorer.getInstance();
		JFileChooser chooser = explorer.getJFileChooser();
		String sFileLoc = getFileLocation();
		
		if( (sFileLoc == null) || (sFileLoc.length() == 0) ){
			chooser.setCurrentDirectory(new java.io.File(sDefaultPath));
		}
		else{
			chooser.setCurrentDirectory(new java.io.File(sFileLoc));
		}
		
		int result = explorer.showBrowser(frmOwner);
		String path = "";
		if(result == JFileChooser.APPROVE_OPTION){
			path = chooser.getSelectedFile().getAbsolutePath();
			if(projectInfo != null)  projectInfo.setSourcePath(path);

			setFileLocation(path);
			
			if(projectModel != null) projectModel.forceRefreshTable();
		}
		
		if(sFileLoc == null || !sFileLoc.equals(path)) {
			log.debug("}}}}}}BEFORE{{{{{{ project status will change ---> "+projectInfo.getProjectName() + " : " + IdentifiedController.getProjectStatus(projectInfo.getProjectName()) + " : " + projectInfo.isSourcePathChange());
			projectInfo.setSourcePathChange(true);
			projectInfo.setAnalyzeTarget(true);
			projectModel.setProjectAnalysisStatus(projectInfo.getProjectName(), ProjectAnalysisInfo.STATUS_READY);
			IdentifiedController.setProjectStatus(projectInfo.getProjectName(), AnalysisMonitorThread.STATUS_READY);
			log.debug("}}}}}}AFTER{{{{{{ project status changed ---> "+projectInfo.getProjectName() +  " : " + IdentifiedController.getProjectStatus(projectInfo.getProjectName()) + " : " + projectInfo.isSourcePathChange());
		} else {
			projectInfo.setSourcePathChange(false);
		}
	}

	public void setText(String text){
		btnBrowse.setText(text);
	}

	public String getText(){
		return btnBrowse.getText();
	}
	
	public String getFileLocation(){
		return txtLocation.getText();
	}

	public void setFileLocation(String path){
		txtLocation.setText(path);
		
		boolean isValid = isFileCheck(path);
		
		if(mManagedprojectInfo != null) mManagedprojectInfo.setLocationValid(isValid);
		
		showLocationValid(isValid);
	}
	public void setTableModel(ManagedProjectTableModel model){
		this.projectModel = model;
	}
	
	public void setProjectInfo(OSIProjectInfo item){
		this.projectInfo = item;
		this.mManagedprojectInfo = item.getProjectAnalysisInfo();
		
		setFileLocation(item.getSourcePath());
	}

	public void setLocationSize(Dimension d){
		txtSize = d;
		txtLocation.setPreferredSize(txtSize);
		setSize();
	}
	
	public void setButtonSize(Dimension d){
		btnSize = d;
		btnBrowse.setPreferredSize(btnSize);
		setSize();
	}
	
	public void setBackColor(Color c){
		this.setBackground(c);
		txtLocation.setBackground(c);
	}

	private void setSize(){
		this.setPreferredSize(new Dimension(txtSize.width+btnSize.width, btnSize.height));
	}
	
	public void addActionListener(ActionListener l){
		this.btnBrowse.addActionListener(l);
	}
	
	private boolean isFileCheck(String sourcePath){
		if(sourcePath == null) return false;
		if( sourcePath.length() == 0 ) return false;		
		
		File f = new File(sourcePath);
		if(f.exists() == false || f.isDirectory() == false ) return false;
		
		return true;
	}
	
	private void showLocationValid(boolean isValid){
		if(isValid){
			txtLocation.setBackground(Color.white);
			this.setToolTipText(null);
		}
		else {
			txtLocation.setBackground(new Color(0xFF, 0xAA, 0xAF));
			this.setToolTipText("Invalid Source Path.");
		}
	}
}

class FileBrowseCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 4693568826719869132L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
   	 FileBrowser lblComponent = (FileBrowser)value;
		 
		 if(isSelected) lblComponent.setBackColor(lblComponent.selected_color);
		 
		 return lblComponent;
	 }
}

class FileBrowseCellEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID = -3198708589362237485L;
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		return (FileBrowser)value;
	}
	public Object getCellEditorValue(){
		return null;
	}
	public void addCellEditorListener(CellEditorListener l){}
	public void removeCellEditorListener(CellEditorListener l){}
}


