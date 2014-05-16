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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.data.match.ISummaryInfo;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;
import com.sec.ose.osi.util.tools.Tools;

/**
 * JTableInfoForCMMultipleFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JTableInfoForCMMultipleFile extends JTableMatchedInfo{
	private static Log log = LogFactory.getLog(JTableInfoForCMMultipleFile.class);


	private static final long serialVersionUID = 5028431690721405767L;
	public static final int COL_COMPONENT = 0;
	public static final int COL_VERSION = 1;
	public static final int COL_LICENSE = 2;

	private IdentificationTableRendererMultipleFile renderer = new IdentificationTableRendererMultipleFile();
	private SelectedComponentInfo selectedComponentInfo = new SelectedComponentInfo();
	
	final TableModelForCMMultipleFile MULTIPLE_FILE_TABLE_MODEL = new TableModelForCMMultipleFile();
	final TableRowSorter<MatchedInfoTableModel> MULTIPLE_FILE_TABLE_SORTER = new TableRowSorter<MatchedInfoTableModel>();
	
	public JTableInfoForCMMultipleFile() {

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setModel(MULTIPLE_FILE_TABLE_MODEL);
		this.setDefaultRenderer(this.getColumnClass(0),
				renderer);
		
		initSorter();
	}

	private void initSorter() {

		MULTIPLE_FILE_TABLE_SORTER.setModel(MULTIPLE_FILE_TABLE_MODEL);
		MULTIPLE_FILE_TABLE_SORTER.setComparator(TableModelForCMMultipleFile.COL_PENDING, IntegerComparatorFolder.getInstance());
		MULTIPLE_FILE_TABLE_SORTER.setComparator(TableModelForCMMultipleFile.COL_IDENTIFIED, IntegerComparatorFolder.getInstance());
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_COMPONENT_NAME);
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_PENDING);
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_STATUS);
		
	}
	
	private void callSorter() {
			
		MULTIPLE_FILE_TABLE_SORTER.setComparator(TableModelForCMMultipleFile.COL_PENDING, IntegerComparatorFolder.getInstance());
		MULTIPLE_FILE_TABLE_SORTER.setComparator(TableModelForCMMultipleFile.COL_IDENTIFIED, IntegerComparatorFolder.getInstance());
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_COMPONENT_NAME);
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_PENDING);
		MULTIPLE_FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMMultipleFile.COL_STATUS);
	}
	
	public void refresh(ISummaryInfo summaryInfo) {
		log.debug("Update Code Match Table - multipleFileSummary");
		if(summaryInfo instanceof MultipleFileSummary == false) {
			return;
		}
		MultipleFileSummary multipleFileSummary = (MultipleFileSummary) summaryInfo;
		if ( multipleFileSummary == null ||
			 multipleFileSummary.getCodeMatchInforForMultipleFileSummarySize() == 0 ) {
			clear(false);
			return;
		}
		
		MULTIPLE_FILE_TABLE_MODEL.setCodeMatchFolder(multipleFileSummary);
		setModel(MULTIPLE_FILE_TABLE_MODEL);
		MULTIPLE_FILE_TABLE_MODEL.setColumnWidth(this);

		remakeTableModelMultipleFolder(MULTIPLE_FILE_TABLE_MODEL);

	}
	
	private void clear(boolean isFile) {
		MULTIPLE_FILE_TABLE_MODEL.setRowCount(0);
		setModel(MULTIPLE_FILE_TABLE_MODEL);
		MULTIPLE_FILE_TABLE_MODEL.fireTableDataChanged();
	}

	public SelectedComponentInfo getHoldingPointer() {
		return this.selectedComponentInfo;
	}
	
	private void remakeTableModelMultipleFolder(MatchedInfoTableModel matchedInfoTableModel) {
		
		MULTIPLE_FILE_TABLE_SORTER.setModel(matchedInfoTableModel);
		callSorter();
		try {
			this.setRowSorter(MULTIPLE_FILE_TABLE_SORTER);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error on remakeTableModelFolder: "+e.getMessage());
		}

		rememberSelectedComponentPointer();
		int selectedRow = loadSelectedComponentPointer(selectedComponentInfo);
		int rowCount = this.getRowCount();
		if(selectedRow < rowCount) {
			this.setRowSelectionInterval(selectedRow, selectedRow);
		} else {
			log.debug("selectedRow >= rowCount");
		}
		
	}

	private void rememberSelectedComponentPointer() {
		
		int row = this.getSelectedRow();
		if(row < 0)
			return;
		
		String componentName = String.valueOf(this.getValueAt(row, TableModelForCMFile.COL_COMPONENT_NAME));
		String versionName = String.valueOf(this.getValueAt(row, TableModelForCMFile.COL_VERSION_NAME));
		String matchedFileName = String.valueOf(this.getValueAt(row, TableModelForCMFile.COL_MATCHED_FILE));
		
		
		this.getHoldingPointer().setHoldingPointerValue(
				componentName, 
				versionName, 
				matchedFileName,
				SelectedComponentInfo.TYPE_FILE
				);
	}

	private int loadSelectedComponentPointer(SelectedComponentInfo holdingPointer) {
		
		String curComponentName = holdingPointer.getComponentNameForHoldingPointer();
		String curVersionName = holdingPointer.getVersionNameForHoldingPointer();
		String curMatchedFile = holdingPointer.getMatchedFileForHoldingPointer();
		int    type = holdingPointer.getHoldingTableType();
		
		int totalRowNum = getRowCount();
		int selectedRow = 0;
		
		for (int curRow = 0; curRow < totalRowNum; curRow++) {
			
			String displayedComponentName = (String) getValueAt(curRow, TableModelForCMFile.COL_COMPONENT_NAME);
			String componentName = DCCodeMatch.getOriginValue(displayedComponentName);
			
			String displayedVersionName = (String)getValueAt(curRow, TableModelForCMFile.COL_VERSION_NAME);
			String versionName = DCCodeMatch.getOriginValue(displayedVersionName);
			
			String matchedFile = null;
			
			if(type ==SelectedComponentInfo.TYPE_FILE)
				matchedFile = (String) getValueAt(curRow, TableModelForCMFile.COL_MATCHED_FILE);
			else
				matchedFile = curMatchedFile;

			if (curComponentName.equals(componentName)
					&& curVersionName.equals(versionName)
					&& curMatchedFile.equals(matchedFile)) {

				selectedRow = curRow;
				break;
			}
		}
		
		if(selectedRow >= totalRowNum)
			return 0;
		return selectedRow;
	}
}

class IdentificationTableRendererMultipleFile extends DefaultTableCellRenderer {
	private static Log log = LogFactory.getLog(IdentificationTableRendererMultipleFile.class);

	private static final long serialVersionUID = -573584914996670949L;
	private static final Color NORMAL_COLOR = new Color(20, 20, 20);
	private static final Color GRAY_COLOR = new Color(130, 130, 130);

	synchronized public Component getTableCellRendererComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) {
		
		JComponent comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value == null) {
			comp.setToolTipText(null);
			return comp;
		}
		
		comp.setToolTipText(String.valueOf(value));

		if(row >= table.getRowCount()) {
			log.debug("row > table.getRowCount()");
			return comp;
		}
		
		switch(column) {
		
			case TableModelForCMFolder.COL_COMPONENT_NAME:
			case TableModelForCMFolder.COL_LICENSE_NAME:
				setHorizontalAlignment(SwingConstants.LEFT);
				break;		
				
			case  TableModelForCMFolder.COL_VERSION_NAME:
			case  TableModelForCMFolder.COL_IDENTIFIED:
			case  TableModelForCMFolder.COL_STATUS:
			case  TableModelForCMFolder.COL_PENDING:
				setHorizontalAlignment(SwingConstants.CENTER);
				break;
		}
		int pendingHits = 0;
		int identifiedHits = 0;
		
		if(table.getValueAt(row, TableModelForCMFolder.COL_PENDING) != null) {
			pendingHits = Tools.transStringToInteger(table.getValueAt(row, TableModelForCMFolder.COL_PENDING).toString());
		}
		if(table.getValueAt(row, TableModelForCMFolder.COL_IDENTIFIED) != null) {
			identifiedHits = Tools.transStringToInteger(table.getValueAt(row, TableModelForCMFolder.COL_IDENTIFIED).toString());
		}
		if (identifiedHits > 0 && pendingHits == 0) {
			comp.setFont(new Font("Arial", Font.BOLD, 12));
			comp.setForeground(NORMAL_COLOR);
		} else if (identifiedHits == 0 && pendingHits == 0) {
			comp.setForeground(GRAY_COLOR);
		} else {
			comp.setForeground(NORMAL_COLOR);
		}
		
		return comp;
	}
}

class IntegerComparatorMultipleFile implements Comparator<Object> {
	private volatile static IntegerComparatorMultipleFile instance = null;
	
	public int compare(Object o1, Object o2) {
		int s1 = Tools.transStringToInteger(o1.toString());
		int s2 = Tools.transStringToInteger(o2.toString());

		if (s1 > s2) {
			return -1;
		} else if (s1 < s2) {
			return 1;
		} else
			return 0;
	}

	public static IntegerComparatorMultipleFile getInstance() {
		if(instance == null) {
			synchronized(IntegerComparatorMultipleFile.class) {
				if(instance == null) {
					instance = new IntegerComparatorMultipleFile();
				}
			}
		}
		return instance;
	}
}