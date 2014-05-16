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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.ManagedProjectInfoMap;
import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.data.project.ProjectAnalysisInfo;
import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * ManagedProjectTableModel
 * @author suhyun47.kim, sjh.yoo, ytaek.kim, hankido.lee
 * 
 */
public class ManagedProjectTableModel extends AbstractTableModel {
	private static Log log = LogFactory.getLog(ManagedProjectTableModel.class);
	private static final long serialVersionUID = -4343258026130786714L;

	private static ManagedProjectInfoMap managedProjectsInfo = OSIProjectInfoMgr.getInstance().getManagedProjectInfo();

	public static final int STATUS_NONE = 0;
	public static final int STATUS_EXISTED = 1;
	public static final int STATUS_CLONED = 2;
	public static final int STATUS_NEW =3;
	
	public static final int COL_DELETE = 0;
	public static final int COL_PROJECT_NAME = 1;
	public static final int COL_ANALYZE_TARGET = 2;
	public static final int COL_ANALYZE_STATUS = 3;
	public static final int COL_SOURCE_LOCATION = 4;
	
	private String[] columnNames = {"", "Project Name", "Analysis Target", "Analyze Status", "Source Location"};
	private int[] columnWidth = { 30, 180, 120, 100, 250};
	private int col_width;
	
	private CheckBoxHeader analyzeHeader;
	private boolean isHeaderWork = false;
	
	public ManagedProjectTableModel(){
		reload();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(managedProjectsInfo.size() > 0) {
			OSIProjectInfo pi = managedProjectsInfo.getProjectInfo(rowIndex);
			if(pi==null)
				return null;
			
			ProjectAnalysisInfo mpi = pi.getProjectAnalysisInfo();
			if(mpi == null)
				return null;
			
			switch(columnIndex) {
				case COL_DELETE:
					return new ImageIcon(WindowUtil.class.getResource("btn_delete.gif"));
				case COL_PROJECT_NAME:
					return pi.getProjectName();
				case COL_ANALYZE_TARGET:
					if(!pi.isLocationValid()){
						return false;
					}
					return pi.isAnalyzeTarget();
				case COL_ANALYZE_STATUS:
					if(pi.getStatusIcon() != null){
						StatusIcon si = (StatusIcon)pi.getStatusIcon();
						si.setStatus(pi.getAnalysisStatus());
						return si;
					}
					break;
				case COL_SOURCE_LOCATION:
					if(pi.getFileComp() != null){
						return (FileBrowser)pi.getFileComp();
					}
					break;
			}
		}
		return null;
	}
	
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    	OSIProjectInfo pi = managedProjectsInfo.getProjectInfo(rowIndex);
    	if(pi == null) return;
		ProjectAnalysisInfo mpi = pi.getProjectAnalysisInfo();

		switch(columnIndex) {
			case COL_PROJECT_NAME:
				break;
			case COL_ANALYZE_TARGET:
				if(pi.isLocationValid()) pi.setAnalyzeTarget((Boolean)value);
				break;
			case COL_ANALYZE_STATUS:
				mpi.setAnalysisStatus((Integer)value);
				break;
			case COL_SOURCE_LOCATION:
				mpi.setFileComp(value); //for View
				break;
		}

        fireTableCellUpdated(rowIndex, columnIndex);
    }
	
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return managedProjectsInfo.size();
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == COL_SOURCE_LOCATION){
			return true;
		}
		else if(col == COL_ANALYZE_TARGET ){
			OSIProjectInfo pi = managedProjectsInfo.getProjectInfo(row);
			if(pi == null) return false;
			return pi.isLocationValid();
        }
		else {
            return false;
        }
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex){
		
		if(getValueAt(0,columnIndex) != null)
			return getValueAt(0,columnIndex).getClass();
		
		return null;
	}
	
	public OSIProjectInfo getProjectInfo(String projectName) {
		return managedProjectsInfo.getProjectInfo(projectName);
	}

	public void reload() {
		
		OSIProjectInfoMgr.getInstance().rebuildManagedProjectInfo();
		fireTableDataChanged();
	}

	public void setProjectAnalysisStatus(String projectName, int status) {
		
		OSIProjectInfo pi = managedProjectsInfo.getProjectInfo(projectName);
		if(pi == null) return;
		ProjectAnalysisInfo mpi = pi.getProjectAnalysisInfo();
		mpi.setAnalysisStatus(status);

		fireTableDataChanged();
	}

	public Collection<OSIProjectInfo> getManagedProjects() {
		return managedProjectsInfo.getProjects();
	}
	
	public void syncronizeFileLocation(){
		for(OSIProjectInfo pi:managedProjectsInfo.getProjects()) {
			pi.setSourcePath(((FileBrowser)pi.getFileComp()).getFileLocation());
  		}
	}
	
	public ArrayList<OSIProjectInfo> getAnalysisProjects() {
		syncronizeFileLocation();
		ArrayList<OSIProjectInfo> AnalysisProjects = new ArrayList<OSIProjectInfo>();
		
		for(OSIProjectInfo pi:managedProjectsInfo.getProjects()) {
			log.debug("analyze target: "+pi.isAnalyzeTarget());
			if(pi.isAnalyzeTarget()) {
				AnalysisProjects.add(pi);
			}
		}
		return AnalysisProjects;
	}

    public void addRow(OSIProjectInfo projectInfo) {
    	managedProjectsInfo.putProjectInfo(projectInfo.getProjectName(), projectInfo); 
    	fireTableDataChanged();
    }
    
    public void deleteRow(String projectName) {
    	managedProjectsInfo.removeProjectInfo(projectName);
    	fireTableDataChanged();
    }
    
    public void forceRefreshTable(){
    	fireTableDataChanged();
    }

    public void setColumnWidth(JTable table){
    	TableColumnModel cm = table.getColumnModel();

    	col_width = 0;
    	for(int i=0; i<columnWidth.length; i++){
    		cm.getColumn(i).setPreferredWidth(columnWidth[i]);
    		col_width += columnWidth[i];
    	}
    }
    
    public void resetTableHaderSize(JTable table, int panel_size){
    	int chg_w = (panel_size - col_width - 20)/2;
     	
    	if(panel_size != col_width ){
    		TableColumnModel cm = table.getColumnModel();
    		cm.getColumn(COL_PROJECT_NAME).setPreferredWidth(columnWidth[COL_PROJECT_NAME] + chg_w);
    		cm.getColumn(COL_SOURCE_LOCATION).setPreferredWidth(columnWidth[COL_SOURCE_LOCATION] + chg_w);
    	}
    }
 
    public void setColumnType(JTable table){
       	table.setShowVerticalLines(false);
       	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
       	table.setRowSelectionAllowed(false);
 		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 		table.setRowHeight(30);
       	
       	JTableHeader header = table.getTableHeader();
    	header.setPreferredSize(new java.awt.Dimension(table.getTableHeader().getWidth(),30));
    	header.setFont(new Font("Arial",Font.BOLD, 12));
    	header.setReorderingAllowed(false);
    	
   
    	TableColumnModel cm = table.getColumnModel();
    	TableColumn col = null;

    	analyzeHeader = new CheckBoxHeader(new CheckboxHeaderItemListener(table, COL_ANALYZE_TARGET),"Analyze Target");
		col = cm.getColumn(COL_ANALYZE_TARGET);
	    col.setHeaderRenderer(analyzeHeader);   
	   
	    JCheckBox chkbox = new JCheckBox();
		chkbox.setBackground(Color.white);
	    chkbox.setHorizontalAlignment(JLabel.CENTER);
	    col.setCellRenderer(new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 1L;

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    		ManagedProjectTableModel model = (ManagedProjectTableModel)table.getModel();
	    		
	    		String sPrjName = (String)table.getValueAt(row, ManagedProjectTableModel.COL_PROJECT_NAME);
	    		OSIProjectInfo item = model.getProjectInfo(sPrjName);
	    		
	     		JCheckBox chkbox = new JCheckBox();
	    		chkbox.setSelected(((Boolean)value).booleanValue());
	    		if(item != null) {
	    			chkbox.setEnabled(item.isLocationValid());
	    		}
	    		chkbox.setHorizontalAlignment(JLabel.CENTER);
	     		chkbox.setBackground(Color.white);
	     		
	     		return (Component)chkbox;
	    	}
	    });
	    col.setCellEditor(new DefaultCellEditor(chkbox));
		
		col = cm.getColumn(COL_ANALYZE_STATUS);
		col.setCellRenderer(new StatusIconCellRenderer());
	    
	    col = cm.getColumn(COL_SOURCE_LOCATION);
		col.setCellRenderer(new FileBrowseCellRenderer());
		col.setCellEditor(new FileBrowseCellEditor());
    }
    
    public void checkAnalyzeAll(JTable table){
    	boolean isAllChecked = false;
    	
    	int analyzedProjectCount = 0;
    	int validProjectCount = 0;
    	int checkBoxSelectedCount = 0;
    	
    	for(OSIProjectInfo pi:managedProjectsInfo.getProjects()) {
			if(pi.isAnalyzed())  analyzedProjectCount++;
			if(pi.isLocationValid()) validProjectCount++;
			if(pi.isAnalyzeTarget()) checkBoxSelectedCount++;
		}
    	
    	if( (analyzedProjectCount > 0) && 
    	    (validProjectCount == checkBoxSelectedCount) ){
    		
    		isAllChecked = true;
    	}
    	
    	isHeaderWork = true;
    	analyzeHeader.setSelected(isAllChecked);
    	((JTableHeader)table.getTableHeader()).repaint(); 
    	isHeaderWork = false;
     }
    
    public boolean isHeaderWork(){
    	return isHeaderWork;
    }
}

/**
 * CheckboxHeaderItemListener
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
class CheckboxHeaderItemListener implements ItemListener{   
	JTable table;
	int column;
	
	public CheckboxHeaderItemListener(JTable table, int column){
		this.table = table;
		this.column = column;
	}
	
	public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source instanceof AbstractButton == false) return;
        
        boolean checked = e.getStateChange() == ItemEvent.SELECTED;
        if(checked == false){
        	TableModel tm = table.getModel();
        	if(tm instanceof AddProjectTableModel){
       		 	AddProjectTableModel model = (AddProjectTableModel)tm;
       		 	if(model.isHeaderWork()) return;
        	}
        	else if(tm instanceof ManagedProjectTableModel){
        		 ManagedProjectTableModel model = (ManagedProjectTableModel)tm;
            	 if(model.isHeaderWork()) return;
        	}
        }
        
        
        for(int x = 0, y = table.getRowCount(); x < y; x++)
        {
        	table.setValueAt(checked,x,column);
        }
   	}
}