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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * CheckBoxHeader
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public class CheckBoxHeader extends JPanel implements TableCellRenderer, MouseListener {   
	private static final long serialVersionUID = 401036596197054434L;
	protected CheckBoxHeader rendererComponent;   
	protected JCheckBox chkbox;
	protected int column;   
	protected boolean mousePressed = false;   
	String sTitle = "Check All";
	
	public CheckBoxHeader(ItemListener itemListener, String text) {   
		chkbox = new JCheckBox(text);   
		chkbox.addItemListener(itemListener);  
		sTitle = text;
		
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
		
		rendererComponent = this;
		rendererComponent.setLayout(new GridBagLayout());
		rendererComponent.add( chkbox, gridBagConstraints1);
	}

	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {   
		if (table != null) {   
		  JTableHeader header = table.getTableHeader();   
		  
		  if (header != null) {
			rendererComponent.setForeground(header.getForeground());   
			rendererComponent.setBackground(header.getBackground());   
			rendererComponent.setFont(header.getFont());
		    
		    
		    header.addMouseListener(this);   
		  }   
		}
		chkbox.setText(sTitle);

		setColumn(column);   
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));   
		
		return rendererComponent;   
	}   
	
	 public void setSelected(boolean b){
		chkbox.setSelected(b) ;
	}

	protected void setColumn(int column) {   
		this.column = column;   
	}   
	public int getColumn() {   
		return column;   
	}   
	protected void handleClickEvent(MouseEvent e) {   
		if (mousePressed) {   
		  mousePressed=false;   
		  
		  JTableHeader header = (JTableHeader)(e.getSource());   
		  JTable tableView = header.getTable();   
		  TableColumnModel columnModel = tableView.getColumnModel();   
		  
		  int viewColumn = columnModel.getColumnIndexAtX(e.getX());   
		  int column = tableView.convertColumnIndexToModel(viewColumn);   
		
		  if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {   
			  chkbox.doClick();
		  }   
		}   
	}   
	public void mouseClicked(MouseEvent e) {   
		handleClickEvent(e);   
		
		((JTableHeader)e.getSource()).repaint();   
	}   
	public void mousePressed(MouseEvent e) {   
		mousePressed = true;   
	}   
	public void mouseReleased(MouseEvent e) {   
	}   
	public void mouseEntered(MouseEvent e) {   
	}   
	public void mouseExited(MouseEvent e) {   
	}   
}
