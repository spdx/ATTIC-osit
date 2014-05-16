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
package com.sec.ose.osi.ui.frm.main.manage.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.manage.AddProjectTableModel;
import com.sec.ose.osi.ui.frm.main.manage.JPanManageMain;
import com.sec.ose.osi.ui.frm.main.report.JPanReportMain;

/**
 * JDlgProjectAdd
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JDlgProjectAdd extends JDialog implements ActionListener{
	private static Log log = LogFactory.getLog(JDlgProjectAdd.class);
	
	private static final long serialVersionUID = 1L;
	private JButton jButtonOK = null;
	private JButton jButtonCancel = null;
	private JPanel jPanelPjtList = null;
	private JLabel jLabelFilter = null;
	private JTextField jTextFieldFilter = null;
	private JScrollPane jScrollPanePjtList = null;
	private JTable jTablePjtList = null;
	private JPanel jPanelButton = null;
	private AddProjectTableModel projectListTableModel = null;
	
	private TableRowSorter<AddProjectTableModel> sorter;
	private JPanManageMain jPanProjectMain;
	private JPanReportMain jPanReportMain;

	/**
	 * This is the default constructor
	 */
	public JDlgProjectAdd(JFrame frame) {
		super(frame,"Add Project",true);
		this.setMinimumSize(new Dimension(600,300));
		this.setResizable(true);
        this.setIconImage(new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage());
        this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				projectListTableModel.recoveySelectedIndex();
			}
		});

		initialize();
	}
	
	public void setVisible(boolean visible) {
		getJTablePjtList();			// for refresh
		super.setVisible(visible);
	}

	public void setManageMain(JPanManageMain p){
		this.jPanProjectMain = p;
	}

	public void setReportMain(JPanReportMain jPanReportMain) {
		this.jPanReportMain = jPanReportMain;
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("OK")){
			projectListTableModel.saveSelectedIndex();
			if(jPanProjectMain != null){
				jPanProjectMain.updateProjectList();
			}
			if(jPanReportMain != null){
				jPanReportMain.resetProjectList();
			}

		} else {
			projectListTableModel.recoveySelectedIndex();
		}
		setVisible(false);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 1;
		gridBagConstraints10.anchor = GridBagConstraints.NORTH;
		gridBagConstraints10.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints10.fill = GridBagConstraints.NONE;
		gridBagConstraints10.gridy = 0;
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 0;
		gridBagConstraints9.insets = new Insets(10, 10, 10, 0);
		gridBagConstraints9.fill = GridBagConstraints.BOTH;
		gridBagConstraints9.weightx = 0.1;
		gridBagConstraints9.weighty = 0.1;
		gridBagConstraints9.gridheight = 1;
		gridBagConstraints9.gridy = 0;
		
		JPanel p = new JPanel();
		p.setSize(600, 300);
		p.setLayout(new GridBagLayout());
		p.add(getJPanelPjtList(), gridBagConstraints9);
		p.add(getJPanelButton(), gridBagConstraints10);
		
		getContentPane().add(p);
	}

	/**
	 * This method initializes jButtonOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton("OK");
			jButtonOK.addActionListener(this);
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jPanelPjtList	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelPjtList() {
		if (jPanelPjtList == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.weighty = 0.1;
			gridBagConstraints3.insets = new Insets(0, 10, 10, 10);
			gridBagConstraints3.ipadx = 0;
			gridBagConstraints3.weightx = 0.1;
			gridBagConstraints3.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(10, 10, 10, 0);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 0.1;
			gridBagConstraints1.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.ipady = 0;
			gridBagConstraints1.gridx = 1;
			jLabelFilter = new JLabel();
			jLabelFilter.setText("Filter :");
			jPanelPjtList = new JPanel();
			jPanelPjtList.setLayout(new GridBagLayout());
			jPanelPjtList.setBorder(BorderFactory.createTitledBorder(null, "Project List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelPjtList.add(jLabelFilter, gridBagConstraints2);
			jPanelPjtList.add(getJTextFieldFilter(), gridBagConstraints1);
			jPanelPjtList.add(getJScrollPanePjtList(), gridBagConstraints3);
		}
		return jPanelPjtList;
	}

	/**
	 * This method initializes jTextFieldFilter	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFilter() {
		if (jTextFieldFilter == null) {
			jTextFieldFilter = new JTextField();
			jTextFieldFilter.setPreferredSize(new Dimension(300, 22));
			jTextFieldFilter.getDocument().addDocumentListener(
					new DocumentListener() {
						public void changedUpdate(DocumentEvent e) {
							newFilter();
						}
						public void insertUpdate(DocumentEvent e) {
							newFilter();
						}
						public void removeUpdate(DocumentEvent e) {
							newFilter();
						}
					});
		}
		return jTextFieldFilter;
	}

    private void newFilter() {
        RowFilter<AddProjectTableModel, Object> rf = null;
        try {
         	String[] texts = jTextFieldFilter.getText().split(" ");
         	List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(2);
         	for(String text:texts) {

         		final String key = text;

         		RowFilter<Object, Object> containsKeywordFilter = new RowFilter<Object, Object>() {
         			
         			private String keyword = key.toLowerCase();
         			public boolean include(Entry<? extends Object, ? extends Object> entry) {

         				String value = entry.getStringValue(projectListTableModel.COL_PROJECT_NAME);
         				if(value==null)
         					return false;
     				
         				value = value.toLowerCase();
         				if(value.contains(keyword))
         					return true;
         				
         				return false;
         			}
         		};
         		filters.add(containsKeywordFilter);

         	}
            rf = RowFilter.andFilter(filters);
        } catch (java.util.regex.PatternSyntaxException e) {
        	log.warn(e);
            return;
        }
        sorter.setRowFilter(rf);
    }

	/**
	 * This method initializes jScrollPanePjtList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPanePjtList() {
		if (jScrollPanePjtList == null) {
			jScrollPanePjtList = new JScrollPane();
			jScrollPanePjtList.setViewportView(getJTablePjtList());
		}
		return jScrollPanePjtList;
	}

	/**
	 * This method initializes jTablePjtList	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTablePjtList() {
		if (jTablePjtList == null) {
			jTablePjtList = new JTable();
			jTablePjtList.addMouseListener(new ProjectTableClickAction());
		}
		projectListTableModel = new AddProjectTableModel();
		jTablePjtList.setModel(projectListTableModel);
			
		projectListTableModel.setColumnWidth(jTablePjtList);
		projectListTableModel.setColumnView(jTablePjtList);
		

		sorter = new TableRowSorter<AddProjectTableModel>(projectListTableModel);
		sorter.toggleSortOrder(projectListTableModel.COL_PROJECT_NAME);
		jTablePjtList.setRowSorter(sorter);

		
		return jTablePjtList;
	}
	
	class ProjectTableClickAction extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int col = jTablePjtList.getSelectedColumn();
		
			if(col == projectListTableModel.COL_ISSELECT){
				projectListTableModel.checkSelectAll(jTablePjtList);
			}
		}
	}
	
	/**
	 * This method initializes jPanelButton	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelButton() {
		if (jPanelButton == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(0, 10, 10, 10);
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			jPanelButton = new JPanel();
			jPanelButton.setLayout(new GridBagLayout());
			jPanelButton.add(getJButtonOK(), gridBagConstraints);
			jPanelButton.add(getJButtonCancel(), gridBagConstraints4);
		}
		return jPanelButton;
	}

}


