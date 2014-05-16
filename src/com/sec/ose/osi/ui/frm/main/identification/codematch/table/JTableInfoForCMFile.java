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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.CodeMatchInfo;
import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.data.match.ISummaryInfo;
import com.sec.ose.osi.sdk.protexsdk.discovery.DCCodeMatch;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.ui.frm.main.identification.IdentifyMediator;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.ui.frm.main.identification.common.JTableMatchedInfo;
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;
import com.sec.ose.osi.util.tools.Tools;

/**
 * JTableInfoForCMFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JTableInfoForCMFile extends JTableMatchedInfo{
	private static Log log = LogFactory.getLog(JTableInfoForCMFile.class);

	private static final long serialVersionUID = 5028431690721405767L;
	public static final int COL_COMPONENT = 0;
	public static final int COL_VERSION = 1;
	public static final int COL_LICENSE = 2;

	private IdentificationTableRendererFile renderer = new IdentificationTableRendererFile();
	private SelectedComponentInfo selectedComponentInfo = new SelectedComponentInfo();
	
	final TableModelForCMFile FILE_TABLE_MODEL = new TableModelForCMFile();
	final TableRowSorter<MatchedInfoTableModel> FILE_TABLE_SORTER = new TableRowSorter<MatchedInfoTableModel>();
	
	public JTableInfoForCMFile() {

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setModel(FILE_TABLE_MODEL);
		this.setDefaultRenderer(this.getColumnClass(0),
				renderer);
		
		initSorter();
	}

	private void initSorter() {
		FILE_TABLE_SORTER.setModel(FILE_TABLE_MODEL);
		FILE_TABLE_SORTER.setComparator(TableModelForCMFile.COL_PERCENTAGE,	IntegerComparatorFile.getInstance());
		FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMFile.COL_PERCENTAGE);
		FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMFile.COL_STATUS);
		
	}

	private void callSorter() {
		FILE_TABLE_SORTER.setComparator(TableModelForCMFile.COL_PERCENTAGE,	IntegerComparatorFile.getInstance());
		FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMFile.COL_COMPONENT_NAME);
		FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMFile.COL_PERCENTAGE);
		FILE_TABLE_SORTER.toggleSortOrder(TableModelForCMFile.COL_STATUS);
	}
	
	public void refresh(ISummaryInfo summaryInfo) {
		log.debug("Update Code Match Table - FileSummary");
		if(summaryInfo instanceof FileSummary == false) {
			return;
		}
		FileSummary fileSummary = (FileSummary) summaryInfo;
		FILE_TABLE_MODEL.setCodeMatchFile(fileSummary);

		if (fileSummary == null || fileSummary.getCodeMatchInfoListSize() == 0) {
			clear(true);
			return;
		}

		setModel(FILE_TABLE_MODEL);
		FILE_TABLE_MODEL.setColumnWidth(this);

		remakeTableModelFile(FILE_TABLE_MODEL, fileSummary);

	}
	
	public void refreshForMultipleFile(MultipleFileSummary multipleFileSummary) {

	}
	
	private void clear(boolean isFile) {
		FILE_TABLE_MODEL.setRowCount(0);
		setModel(FILE_TABLE_MODEL);
		FILE_TABLE_MODEL.fireTableDataChanged();
	}

	public SelectedComponentInfo getHoldingPointer() {
		return this.selectedComponentInfo;
	}

	private void remakeTableModelFile(
			MatchedInfoTableModel matchedInfoTableModel,
			FileSummary fileSummary ) {

		try {
			DCCodeMatch codeMatchDiscoveryController = 
				(DCCodeMatch) ProjectDiscoveryControllerMap.getDiscoveryController(
						IdentifyMediator.getInstance().getSelectedProjectName(),
						IdentificationConstantValue.CODE_MATCH_TYPE);

			String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
			String identifiedStringSearchLicense =codeMatchDiscoveryController.getStringSearchLicense(projectName, fileSummary.getFullPath());

			FILE_TABLE_SORTER.setModel(matchedInfoTableModel);
			callSorter();
			RowFilter<MatchedInfoTableModel, Object> rowFilter = 
				getRowFilterForStringSearchLicense(identifiedStringSearchLicense, fileSummary);
			FILE_TABLE_SORTER.setRowFilter(rowFilter);	
			renderer.setIdentifiedStringSearchLicense(identifiedStringSearchLicense);
			this.setRowSorter(FILE_TABLE_SORTER);
			rememberSelectedComponentPointer();
			
			int selectedRow = loadSelectedComponentPointer(selectedComponentInfo);
			int rowCount = this.getRowCount();
			if(selectedRow < rowCount) {
				this.setRowSelectionInterval(selectedRow, selectedRow);
				String selectedLicenseName = (String) this.getValueAt(0, TableModelForCMFile.COL_LICENSE_NAME);
				IdentifyMediator.getInstance().setSelectedLicenseName(selectedLicenseName);
				log.debug("remakeTableModelFile() - selectedLicenseName : " + selectedLicenseName);
			} else {
				log.debug("selectedRow >= RowCountOfCodeMatchTable");
			}

		
		} catch (Exception e) {
			
			log.error("Code Match Table UI ERROR :" + fileSummary.getFullPath());
			log.error(e);
			BackgroundJobManager.getInstance().restartAllBackgroundThread();
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

    private RowFilter<MatchedInfoTableModel, Object> getRowFilterForStringSearchLicense
    	(String stringSearchLicense, FileSummary fileSummary) {
    	RowFilter<MatchedInfoTableModel, Object> rowFilter = null;
    	
    	for(CodeMatchInfo codeMatchInfo: fileSummary.getCodeMatchInfoList()) {
    		if(codeMatchInfo.getIdentifiedInfo() != null)
    			return null;
    	}
    	
    	if(stringSearchLicense == null || stringSearchLicense.length() == 0)
    		return null;
    	
    	boolean isFilterNeeded = false;
    	for(CodeMatchInfo tmpCodeMatchUnit: fileSummary.getCodeMatchInfoList()) {
			if(stringSearchLicense.equals(tmpCodeMatchUnit.getLicenseName())
			   && tmpCodeMatchUnit.getPercentage() == 100
			) {
				isFilterNeeded = true;
				break;
			}
		}
		
		if(isFilterNeeded == false)
			return null;

    	
        try {
         	List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(2);

     		final String key = stringSearchLicense;

     		RowFilter<Object, Object> containsKeywordFilter = new RowFilter<Object, Object>() {
     			
     			private String keyword = key.toLowerCase();
     			public boolean include(Entry<? extends Object, ? extends Object> entry) {

     				String value = entry.getStringValue(TableModelForCMFile.COL_LICENSE_NAME);
     				if(value==null)
     					return false;
     				value = value.toLowerCase();
     				if(value.contains(keyword))
     					return true;
     				
     				return false;
     			}
     		};
       		filters.add(containsKeywordFilter);

            rowFilter = RowFilter.andFilter(filters);
        } catch (java.util.regex.PatternSyntaxException e) {
        	log.warn(e);
            return null;
        }
        return rowFilter; 
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

/**
 * IdentificationTableRendererFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
class IdentificationTableRendererFile extends DefaultTableCellRenderer {
	private static Log log = LogFactory.getLog(IdentificationTableRendererFile.class);
	
	private String identifiedStringSearchLicense = null;
	
	private static final long serialVersionUID = -573584914996670949L;

	private static final Color NORMAL_COLOR = new Color(20, 20, 20);

	private static final Color GRAY_COLOR = new Color(130, 130, 130);

	public void setIdentifiedStringSearchLicense(String identifiedStringSearchLicense) {
		this.identifiedStringSearchLicense = identifiedStringSearchLicense;
	}
	
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
			case TableModelForCMFile.COL_COMPONENT_NAME:
			case TableModelForCMFile.COL_LICENSE_NAME:
				setHorizontalAlignment(SwingConstants.LEFT);
				break;		
				
			case  TableModelForCMFile.COL_VERSION_NAME:
			case  TableModelForCMFile.COL_USAGE:
			case  TableModelForCMFile.COL_STATUS:
			case  TableModelForCMFile.COL_PERCENTAGE:
			case TableModelForCMFile.COL_MATCHED_FILE:
				setHorizontalAlignment(SwingConstants.CENTER);
				break;
		}
		
		if(table.getValueAt(row,TableModelForCMFile.COL_STATUS) == null) return comp;
		String status = table.getValueAt(row,TableModelForCMFile.COL_STATUS).toString();
		String licenseName = table.getValueAt(row, TableModelForCMFile.COL_LICENSE_NAME).toString();
		
		if(status.equals(AbstractMatchInfo.IDENTIFIED)) {
			
			comp.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
			comp.setForeground(NORMAL_COLOR);
			
		} else if ( (status.equals(AbstractMatchInfo.REJECTED)) ||
				    (status.equals(AbstractMatchInfo.DECLARED)) ) {
			
			comp.setForeground(new Color(150, 150, 150));
			
		} else if (status.equals(AbstractMatchInfo.PENDING)) {
			
			if(identifiedStringSearchLicense != null && !identifiedStringSearchLicense.equals("")) {
				String currentRowLicense = ""+licenseName;
				if(identifiedStringSearchLicense.equals(currentRowLicense)) {
					comp.setForeground(NORMAL_COLOR);
				}
				else {
					comp.setForeground(GRAY_COLOR);
				} 
			} else {
				comp.setForeground(NORMAL_COLOR);
			}
		}
		
		return comp;
	}
}

/**
 * IntegerComparatorFile
 * @author suhyun47.kim, hankido.lee
 * 
 */
class IntegerComparatorFile implements Comparator<Object> {
	
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