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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.sec.ose.osi.ui._util.WindowUtil;

/**
 * StatusIcon
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public class StatusIcon extends JPanel{
	private static final long serialVersionUID = -8360815358329074498L;

	ImageIcon[] arrIcons = new ImageIcon[]{new ImageIcon(WindowUtil.class.getResource("ico_R.gif")), 
										new ImageIcon(WindowUtil.class.getResource("ico_O.gif")), 
										new ImageIcon(WindowUtil.class.getResource("ico_B.gif"))
	};
	
	String[]  arrStatus = {"Ready","Processing","Complete"};
	
	JLabel lblIcon = new JLabel();
	JLabel lblTitle = new JLabel();
	
	Color selected_color = new Color(184,207,229);
	
	public StatusIcon(){
		this.setBackground(Color.white);
		
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 2);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 0;
		
		this.setLayout(new GridBagLayout());
		this.add(lblIcon, gridBagConstraints1);
		this.add(lblTitle, gridBagConstraints2);
	}

	public StatusIcon(int status){
		this();
		setStatus(status);
	}
	
	public void setStatus(int status){
		lblIcon.setIcon(arrIcons[status]);
		lblTitle.setText(arrStatus[status]);
	}
	
	public String getStatusText(){
		return lblTitle.getText();
	}
}

/**
 * StatusIconCellRenderer
 * @author suhyun47.kim, sjh.yoo
 * 
 */
class StatusIconCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		 StatusIcon lblComponent = (StatusIcon)value;
		 return lblComponent;
	 }
}
