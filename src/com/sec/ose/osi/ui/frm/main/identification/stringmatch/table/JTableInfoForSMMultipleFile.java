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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

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
import com.sec.ose.osi.ui.frm.main.identification.common.MatchedInfoTableModel;

/**
 * JTableInfoForSMMultipleFile
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JTableInfoForSMMultipleFile extends JTableMatchedInfoForSM {
	private static Log log = LogFactory.getLog(JTableInfoForSMMultipleFile.class);
	
	private static final long serialVersionUID = 1L;
	protected TableModelForSMMultipleFile tableModelMultipleFile = null;

	TableRowSorter<MatchedInfoTableModel> sorter = new TableRowSorter<MatchedInfoTableModel>();
	
	public JTableInfoForSMMultipleFile() {
		tableModelMultipleFile = new TableModelForSMMultipleFile();

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		this.setModel(tableModelMultipleFile);
		this.setDefaultRenderer(this.getColumnClass(0),new IdentificationTableRendererForMultipleFile());
		this.setPreferredScrollableViewportSize(new Dimension(300, 200));
	}

	public void refresh(ISummaryInfo summaryInfo) {
		if(summaryInfo instanceof MultipleFileSummary == false) {
			return;
		}
		MultipleFileSummary multipleFileSummary = (MultipleFileSummary) summaryInfo;
		log.debug("Update String Match multipleFileSummary Table - size:"+multipleFileSummary.getStringMatchInforForMultipleFileSummarySize());
		
		tableModelMultipleFile.setMultipleFileSummary(multipleFileSummary);
		
		if(multipleFileSummary == null) {
			clear();
			return;
		}
		if (multipleFileSummary.getStringMatchInforForMultipleFileSummarySize()==0) {
			clear();
			return;
		}
		
		setModel(tableModelMultipleFile);
		tableModelMultipleFile.setColumnWidth(this);
		
		sorter.setModel(tableModelMultipleFile);
		sorter.toggleSortOrder(TableModelForSMMultipleFile.COL_STATUS);
		this.setRowSorter(sorter);
		this.setRowSelectionInterval(0, 0);
	}

	private void clear() {
		tableModelMultipleFile.setRowCount(0);
		setModel(tableModelMultipleFile);
		tableModelMultipleFile.fireTableDataChanged();
	}
	
	public static final int COL_SEARCH_NAME = 0;
	public static final int COL_LICENSE_NAME = 3;
	
	public String getSelectedSearchName() {
		if(getSelectedRow() < 0) return null;
		return (String)this.getValueAt(this.getSelectedRow(),TableModelForSMMultipleFile.COL_STRING_SEARCH);
	}
	
	public String getSelectedLicenseName() {
		if(getSelectedRow() < 0) return null;
		return (String)this.getValueAt(this.getSelectedRow(),TableModelForSMMultipleFile.COL_LICENSE_NAME);
	}
	
	public String getSelectedComponentName() {
		if(getSelectedRow() < 0) return null;
		return (String)this.getValueAt(this.getSelectedRow(),TableModelForSMMultipleFile.COL_COMPONENT_NAME);
	}
}

/**
 * IdentificationTableRendererForMultipleFile
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
class IdentificationTableRendererForMultipleFile extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JComponent comp = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value != null) {
			if(table.getColumnName(column).equals("Version")
					|| table.getColumnName(column).equals("Pending Hits")
					|| table.getColumnName(column).equals("Status")
					|| table.getColumnName(column).equals("Identified Hits")
					|| table.getColumnName(column).equals("Files")
					 ) {setHorizontalAlignment(SwingConstants.CENTER);
			} else {
						setHorizontalAlignment(SwingConstants.LEFT);
					}
			
			comp.setToolTipText(String.valueOf(value));
			if(table.getValueAt(row, TableModelForSMFile.COL_STATUS) != null &&
			   table.getValueAt(row, TableModelForSMFile.COL_STATUS).toString().equals("Identified")) {
				
				comp.setFont(new Font("Arial",Font.BOLD | Font.ITALIC, 12));
				comp.setForeground(new Color(20,20,20));
			} else if(table.getValueAt(row, TableModelForSMFile.COL_STATUS).toString().equals("Declared")) {
				comp.setForeground(new Color(150,150,150));
			} else {
				comp.setForeground(new Color(20,20,20));
			}
		} else {
			comp.setToolTipText(null);
		}
		return comp;
	}
}