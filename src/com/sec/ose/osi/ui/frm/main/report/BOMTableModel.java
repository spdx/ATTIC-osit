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
package com.sec.ose.osi.ui.frm.main.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * BOMTableModel
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class BOMTableModel extends DefaultTableModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final int COL_NO_WIDTH = 25;
	public static final int COL_CHECKED = 0;
	public static final int COL_COMPONENT = 1;
	public static final int COL_LICENSE = 2;
	public static final int COL_FILECOUNT = 3;
	public static final int NUM_OF_COL = 4;
	public static final int COL_CHECKED_SIZE = 60;
	public static final int COL_COMPONENT_SIZE = 250;
	public static final int COL_LICENSE_SIZE = 200;
	public static final int COL_FILECOUNT_SIZE = 70;
	private DefaultTableCellRenderer IdentificationTableRenderer =null;

	private static final String[] TITLE  = 
	{	"Checked",
		"Component",
		"License",
		"FileCount"
	};
	
	private ArrayList<BOMTableRow> rows;
	private JCheckBox checkboxObserver;

	@SuppressWarnings("unchecked")
	protected BOMTableModel(ArrayList<BOMTableRow> rows, JCheckBox checkbox) {
		super();
		IdentificationTableRenderer = new DefaultTableCellRenderer();
		IdentificationTableRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.rows = (ArrayList<BOMTableRow>) rows.clone();
		checkboxObserver = checkbox;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		if(getValueAt(0,c) == null)
			return null;
		return getValueAt(0, c).getClass();
	}
	
	public int getColumnCount() {
		
		return TITLE.length;
	}
	
	public int getRowCount() {
		if(rows == null)
			return 0; 
		
		return this.rows.size();
	}

	public String getColumnName(int colunm) {
		
		if(colunm < TITLE.length) {
			return TITLE[colunm];
		}
		else { 
			return "test";
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		
		if(col == COL_CHECKED)
			return true;
		
		return false;
	}
	
	public Object getValueAt(int row, int col) {
		
		if(rows == null)
			return null;
		
		if(rows.size() < row-1)
			return null;
		
		if(col > NUM_OF_COL-1 )
			return null;
		
		Object retValue=null;
		
		switch(col) {
		
			case COL_CHECKED:
				retValue = rows.get(row).isChecked();
				break;
				
			case COL_COMPONENT:
				retValue = rows.get(row).getComponentName();
				break;
				
			case COL_LICENSE:
				retValue = rows.get(row).getComponentLicense();
				break;
				
			case COL_FILECOUNT:
				retValue = rows.get(row).getFileCount();
				break;
		}
		
		return retValue;
	}
	
	public void setValueAt(Object value, int row, int col) {
		
		if(col != COL_CHECKED )
			return;
		
		if(rows == null)
			return;
		
		if(rows.size() < row-1)
			return;

		rows.get(row).setChecked((Boolean)value);

		if(isAllBOMChecked() == true) {
			checkboxObserver.setSelected(true);
		}
		else {
			checkboxObserver.setSelected(false);
		}
	}

	public boolean isAllBOMChecked() {
		
		for(int i=0; i<rows.size(); i++) {
			if (rows.get(i).isChecked() == false)
				return false;
		}
		
		return true;
	}

	public int getColumnWidth(int colIdx) {
		
		switch(colIdx) {
			case COL_CHECKED:
				return COL_CHECKED_SIZE;
				
			case COL_COMPONENT:
				return COL_COMPONENT_SIZE;
			
			case COL_LICENSE:
				return COL_LICENSE_SIZE;
				
			case COL_FILECOUNT:
				return COL_FILECOUNT_SIZE;
		}

		return 0;
	}
	
	private int[] columnWidth = { 60, 200, 200, 70};
    public void setColumnWidth(JTable table){
    	TableColumnModel cm = table.getColumnModel();

    	for(int i=0; i<columnWidth.length; i++){
    		if (i == 0 || i == columnWidth.length-1){
    			if(i == 3) {
    				cm.getColumn(i).setCellRenderer(IdentificationTableRenderer);
    			}
    			
    			cm.getColumn(i).setMinWidth(columnWidth[i]);
    			cm.getColumn(i).setMaxWidth(columnWidth[i]);
    		} 
    	}
    }

	public void setCheckboxObserver(JCheckBox checkboxObserver) {
		this.checkboxObserver = checkboxObserver;
	}

	public void setAllCheck(boolean value) {
		for(int i=0; i<rows.size(); i++) {
			this.setValueAt(value, i, COL_CHECKED);	
		}	
	}

	public Iterator<BOMTableRow> getRows() {
		return this.rows.iterator();
	}
	
}
