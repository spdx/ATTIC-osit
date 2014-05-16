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

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.project.OSIProjectInfo;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.util.tools.DateUtil;

/**
 * AddProjectTableModel
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class AddProjectTableModel extends AbstractTableModel {
	private static Log log = LogFactory.getLog(AddProjectTableModel.class);
	
	private static final long serialVersionUID = 1L;
	public final int COL_ISSELECT = 0;
	public final int COL_PROJECT_NAME = 1;
	public final int COL_ANALYZE_DATE = 2;

	private String[] columnNames  =  {"", "Project Name", "Analyze Date"};
	private int[] columnWidth = { 30, 250, 160};

	private ArrayList<OSIProjectInfo> projectsListInfo;
	private Vector<Boolean> tmpSelectedIndex = null;
	
	private boolean isHeaderWork = false;
	private CheckBoxHeader searchHeader;
	
	public AddProjectTableModel() {
		
		log.debug("ProjectListTableModel is created");

		projectsListInfo = new ArrayList<OSIProjectInfo>();
		Collection<OSIProjectInfo> allprojects = OSIProjectInfoMgr.getInstance().getAllProjects();
		for(OSIProjectInfo item: allprojects) {
			if(item.isDeleteProject() == false) {
				projectsListInfo.add(item);
			}
		}

		tmpSelectedIndex = new Vector<Boolean>();
    	for(OSIProjectInfo item:projectsListInfo) {
    		tmpSelectedIndex.add(item.isManaged());
    	}
	}

	public void recoveySelectedIndex() {
		int cnt = projectsListInfo.size();
    	for(int i=0;i<cnt;i++) {
    		projectsListInfo.get(i).setManaged(tmpSelectedIndex.get(i));
    	}
	}

	public void saveSelectedIndex() {
		ArrayList<OSIProjectInfo> loadProjectsList = new ArrayList<OSIProjectInfo>();
    	for(OSIProjectInfo pi:projectsListInfo) {
    		if(pi.isManaged()) {
    			if(!pi.isLoaded()) {
    				pi = ProjectAPIWrapper.loadAnalysisInfo(pi);
    				loadProjectsList.add(pi);
    			}
    			pi.setProjectAnalysisInfo(pi.isAnalyzed());
    		}
    	}

    	while(loadProjectsList.size() > 0) {
			for(int i=loadProjectsList.size()-1;i>=0;i--) {
				if(loadProjectsList.get(i).isLoaded()) {
					loadProjectsList.get(i).getProjectAnalysisInfo().setAnalysisStatus(loadProjectsList.get(i).isAnalyzed());
					loadProjectsList.remove(i);
				}
			}
		}
	}
	
    public void setColumnWidth(JTable table){
    	TableColumnModel cm = table.getColumnModel();

    	for(int i=0; i<columnWidth.length; i++){
    		cm.getColumn(i).setPreferredWidth(columnWidth[i]);
    	}
    }
    
    public void setColumnView(JTable table){
       	table.setShowVerticalLines(false);
       	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       	table.setColumnSelectionAllowed(false);
       	table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
 		table.setRowHeight(30);
 		
      	JTableHeader header = table.getTableHeader();
    	header.setPreferredSize(new java.awt.Dimension(table.getTableHeader().getWidth(),30));
    	header.setFont(new Font("Arial",Font.BOLD, 12));
    	header.setReorderingAllowed(false);
 		
 		TableColumnModel cm = table.getColumnModel();
    	TableColumn col = null;
    	
		searchHeader = new CheckBoxHeader(new CheckboxHeaderItemListener(table, COL_ISSELECT),"");
		col = cm.getColumn(COL_ISSELECT);
		col.setHeaderRenderer(searchHeader);
	}
    
    public void checkSelectAll(JTable table){
    	boolean isAllChecked = false;
    	
    	int select_cnt = 0;
    	for(OSIProjectInfo pi:projectsListInfo) {
			if(pi.isManaged()) select_cnt ++;
		}
    	
    	if(select_cnt == projectsListInfo.size()){
    		isAllChecked = true;
    	}
    	
    	isHeaderWork = true;
    	searchHeader.setSelected(isAllChecked);
    	((JTableHeader)table.getTableHeader()).repaint(); 
    	isHeaderWork = false;
     }
    
    public boolean isHeaderWork(){
    	return isHeaderWork;
    }
    
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return projectsListInfo.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(projectsListInfo != null && projectsListInfo.size() > 0) {
			OSIProjectInfo pi = projectsListInfo.get(rowIndex);
			if(pi != null){
				switch(columnIndex){
					case COL_ISSELECT:
						return Boolean.valueOf(pi.isManaged());
					case COL_PROJECT_NAME:
						return pi.getProjectName();
					case COL_ANALYZE_DATE:
						return transTimeFormat(pi.getLastAnalyzedDate());
				}
			}
		}
		return null;
	}
	
    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    	OSIProjectInfo pi = projectsListInfo.get(rowIndex);
    	
    	switch(columnIndex) {
    		case COL_ISSELECT:
    			pi.setManaged((Boolean) value);
    			break;
    	}
    	
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
	
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex){

		if(getValueAt(0,columnIndex) != null)
			return getValueAt(0,columnIndex).getClass();
		
		return null;
	}
	
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COL_ISSELECT) {
            return true;
        } else {
            return false;
        }
    }
    
	private String transTimeFormat(long time) {
		if (time < 0) {
			return "Now Loading..."; 
		} else if (time == 0){
			return "Not Yet Analyzed...";
		}
		return DateUtil.getFormatingTime("%1$tb %1$te, %1$tY %1$tI:%1$tM:%1$tS %1$tp",time);
	}
}

