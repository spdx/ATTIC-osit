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
package com.sec.ose.osi.ui.frm.main.identification.stringmatch.table;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.data.match.StringMatchInfoForFolder;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;

/**
 * TableModelForSMMultipleFile
 * @author hankido.lee
 * 
 */
public class TableModelForSMMultipleFile extends MatchedInfoTableModel {
	private static final long serialVersionUID = 1L;
	public static final int COL_STRING_SEARCH = 0;
	public static final int COL_COMPONENT_NAME = 1;
	public static final int COL_VERSION_NAME = 2;
	public static final int COL_LICENSE_NAME = 3;
	public static final int COL_STATUS = 4;
	public static final int COL_FILES = 5;
	public static final int COL_PENDING_HITS = 6;
	public static final int COL_IDENTIFIED_HITS = 7;
	public static final int NOM_OF_COL = 8;

	public static final String[] columnNames  = 
		{"Search","Component Name","Version","License","Status","Files","Pending Hits","Identified Hits"};
	private int[] columnWidth = { 80, 80, 20, 80, 10, 5, 10, 25};
	
	private MultipleFileSummary multipleFileSummary = null;

	public TableModelForSMMultipleFile() {
		setType("StringSearchMultiFileTableModel");
	}
	
	public TableModelForSMMultipleFile(MultipleFileSummary multipleFileSummary) {
		this.multipleFileSummary = multipleFileSummary;
		setType("StringSearchMultiFileTableModel");
	}
	
	public void setMultipleFileSummary(MultipleFileSummary multipleFileSummary) {
		this.multipleFileSummary = multipleFileSummary;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    public void setColumnWidth(JTable table){
    	TableColumnModel tcm = table.getColumnModel();
    	for(int i=0; i<columnWidth.length; i++){
    		tcm.getColumn(i).setPreferredWidth(columnWidth[i]);
    	}
    }
	@Override
	public int getRowCount() {
		if(multipleFileSummary == null) return 0;
		return multipleFileSummary.getStringMatchInforForMultipleFileSummarySize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if(multipleFileSummary == null || multipleFileSummary.getStringMatchInforForMultipleFileSummarySize() <= 0) return null;
		
		StringMatchInfoForFolder tmpStringSearchFolderUnit = null;
		tmpStringSearchFolderUnit = multipleFileSummary.getStringSearchFolderUnit(rowIndex);

		if(tmpStringSearchFolderUnit != null){
			switch(columnIndex){
				case COL_STRING_SEARCH:
					return tmpStringSearchFolderUnit.getStringSearchName(); 
				case COL_COMPONENT_NAME:
					return tmpStringSearchFolderUnit.getComponentName();
				case COL_VERSION_NAME:
					return tmpStringSearchFolderUnit.getVersionName();
				case COL_LICENSE_NAME:
					return tmpStringSearchFolderUnit.getLicenseName();
				case COL_STATUS:
					if(tmpStringSearchFolderUnit.getPendingHits() > 0) {
						return "Pending";
					} else if (tmpStringSearchFolderUnit.getIdentifiedHits() > 0){
						return "Identified";
					} else {
						return "Declared";
					}
				case COL_FILES:
					return tmpStringSearchFolderUnit.getFileCount();
				case COL_PENDING_HITS:
					return tmpStringSearchFolderUnit.getPendingHits();
				case COL_IDENTIFIED_HITS:
					return tmpStringSearchFolderUnit.getIdentifiedHits();
			}
		}
		return null;
	}
}