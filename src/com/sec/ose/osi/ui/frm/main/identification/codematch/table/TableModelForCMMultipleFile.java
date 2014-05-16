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
package com.sec.ose.osi.ui.frm.main.identification.codematch.table;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.sec.ose.osi.data.match.CodeMatchInfoForFolder;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;

/**
 * TableModelForCMMultipleFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class TableModelForCMMultipleFile extends MatchedInfoTableModel {
	private static final long serialVersionUID = -1443073839841458065L;

	public static final int COL_COMPONENT_NAME = 0;
	public static final int COL_VERSION_NAME = 1;
	public static final int COL_LICENSE_NAME = 2;
	public static final int COL_STATUS = 3;
	public static final int COL_PENDING = 4;
	public static final int COL_IDENTIFIED = 5;
	public static final int NOM_OF_COL = 6;
	
	private static final String[] columnNames  = 
		{"Component","Version","License","status","Pending","Identified"};
	private int[] columnWidth = { 80, 20, 80, 10, 10, 10};

	private MultipleFileSummary mMultipleFileSummary = null;

	public TableModelForCMMultipleFile() {
		setType("CodeMatchMultiFileTableModel");
	}

	public TableModelForCMMultipleFile(MultipleFileSummary multipleFileSummary) {
		this.mMultipleFileSummary  = multipleFileSummary;
		setType("CodeMatchFolderTableModel");
	}
	
	public void setCodeMatchFolder(MultipleFileSummary multipleFileSummary) {
		this.mMultipleFileSummary  = multipleFileSummary;
	}
	
	public static DefaultTableModel getDefaultTableModel() {
		return new DefaultTableModel(columnNames,0);
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
		if(mMultipleFileSummary == null) return 0;
		return mMultipleFileSummary.getCodeMatchInforForMultipleFileSummarySize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(mMultipleFileSummary == null) return null;
		CodeMatchInfoForFolder tmpCodeMatchUnit = null;
		tmpCodeMatchUnit = mMultipleFileSummary.getCodeMatchMultipleFileUnit(rowIndex);
		if(tmpCodeMatchUnit != null){
			switch(columnIndex){
				case COL_COMPONENT_NAME:
					if(tmpCodeMatchUnit.getIdentifiedComponentName() != null && !tmpCodeMatchUnit.getIdentifiedComponentName().equals(tmpCodeMatchUnit.getComponentName())) {
						return tmpCodeMatchUnit.getIdentifiedComponentName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getComponentName() + DCCodeMatch.END_ORIGIN; 
					}
					return tmpCodeMatchUnit.getComponentName();
				case COL_VERSION_NAME:
					if(tmpCodeMatchUnit.getIdentifiedVersionName() != null && !tmpCodeMatchUnit.getIdentifiedVersionName().equals(tmpCodeMatchUnit.getVersionName())) {
						if(tmpCodeMatchUnit.getVersionName().length() == 0) return "Unspecified" + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getVersionName() + DCCodeMatch.END_ORIGIN;
						return tmpCodeMatchUnit.getIdentifiedVersionName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getVersionName() + DCCodeMatch.END_ORIGIN; 
					}
					if(tmpCodeMatchUnit.getVersionName().length() == 0) return "Unspecified";
					return tmpCodeMatchUnit.getVersionName();
				case COL_LICENSE_NAME:
					if(tmpCodeMatchUnit.getIdentifiedLicenseName() != null && !tmpCodeMatchUnit.getIdentifiedLicenseName().equals(tmpCodeMatchUnit.getLicenseName())) {
						return tmpCodeMatchUnit.getIdentifiedLicenseName() + DCCodeMatch.START_ORIGIN+tmpCodeMatchUnit.getLicenseName() + DCCodeMatch.END_ORIGIN; 
					}
					return tmpCodeMatchUnit.getLicenseName();
				case COL_STATUS:
					if(tmpCodeMatchUnit.getPendingSnippetCount() > 0) {
						return "Pending";
					} else if (tmpCodeMatchUnit.getIdentifiedSnippetCount() > 0){
						return "Identified";
					} else if (tmpCodeMatchUnit.getDeclaredSnippetCount() > 0){
						return "Declared";
					} else {
						return "Rejected";
					}
				case COL_PENDING:
					return tmpCodeMatchUnit.getPendingSnippetCount();
				case COL_IDENTIFIED:
					return tmpCodeMatchUnit.getIdentifiedSnippetCount();
			}
		}
		return null;
	}

}
