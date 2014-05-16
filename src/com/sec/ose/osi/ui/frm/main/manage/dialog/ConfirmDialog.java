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

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * ConfirmDialog
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class ConfirmDialog extends JOptionPane {
	private static final long serialVersionUID = 6080129093921353224L;
	private static Font fontType = new Font("Dialog", 0, 12);

    public static int show(Component parent, Object message, String title, int option) {
        return showConfirmDialog(parent, message, title, option);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
        return showConfirmDialog(parentComponent, message, title, optionType, QUESTION_MESSAGE);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) {
        return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) {
    	String[] buttons = {"OK", "Cancel"};
        return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, buttons, null);
    }

    @SuppressWarnings("deprecation")
	public static int showOptionDialog(Component parentComponent, Object message,String title, int optionType,
                                       int messageType, Icon icon,Object[] options, Object initialValue) {
        JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
        pane.setInitialValue(initialValue);
        JDialog dialog = pane.createDialog(parentComponent, title);
        recursivelySetFont(pane);
        pane.selectInitialValue();
        dialog.show();

        Object selectedValue = pane.getValue();

        if(selectedValue == null)
            return CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return((Integer)selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    private static void recursivelySetFont(JComponent parent) {
        parent.setFont(fontType);
        parent.setOpaque(true);

        Component[] subComponents = parent.getComponents();
        for(int i = 0; i < subComponents.length; i++) {
            recursivelySetFont(((JComponent)subComponents[i]));
        }
    }
}
