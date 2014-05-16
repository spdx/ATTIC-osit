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
package com.sec.ose.osi.ui.frm.main.report.project;

import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.DefaultListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ProjectListModel
 * @author suhyun47.kim, hankido.lee
 * 
 */
@SuppressWarnings("unchecked")
public class ProjectListModel extends DefaultListModel implements Cloneable {
	private static Log log = LogFactory.getLog(ProjectListModel.class);
	private static final long serialVersionUID = 1L;

	private TreeSet<String> mDisplayedTreeSet = new TreeSet<String>() ;		// Displayed projectNames
	private TreeSet<String> mMemento ;

	public void createMemento() {
		mMemento = (TreeSet<String>) mDisplayedTreeSet.clone();
	}

	public void restoreMemento() {
		mDisplayedTreeSet = (TreeSet<String>) mMemento.clone();
		refresh();
	}

	protected void refresh() {

		super.removeAllElements();
		Iterator<String> keys = mDisplayedTreeSet.iterator();
		while(keys.hasNext()) {
			String curProjectName = keys.next();
			super.addElement(curProjectName);
		}
	}
	
	
	public void refreshJList() {
		super.removeAllElements();
		Iterator<String> keys = mDisplayedTreeSet.iterator();
		while(keys.hasNext()) {
			String curProjectName = keys.next();
			super.addElement(curProjectName);
		}
	}
	
	public void addElement(Object objProjectName) {
		
		addElement(objProjectName, true);		
	}

	public void addElement(Object objProjectName, boolean refresh) {
		String projectName = objProjectName.toString();
		if(isExistedElements(projectName) == true) {
			log.debug("****addElement****true");
			return;
		}
		
		// for keeping alphabet order
		mDisplayedTreeSet.add(projectName);
		
		if(refresh == true)
			refresh();
	}
	
	public void clear() {
		super.clear();
		this.mDisplayedTreeSet.clear();
	}	
	
	public boolean removeElement(Object objProjectName) {
		
		return removeElement(objProjectName, true);
		
	}

	public boolean removeElement(Object objProjectName, boolean refresh) {
		
		String projectName = objProjectName.toString();
		if(isExistedElements(projectName) == false) {
			return false;
		}
		
		mDisplayedTreeSet.remove(projectName);
		
		if(refresh == true)
			refresh();
		
		return true;
		
	}

	public boolean isExistedElements(String projectName) {
		log.debug("****isExistedElements****"+mDisplayedTreeSet.contains(projectName));

		return mDisplayedTreeSet.contains(projectName);
	}	
	
	public String toString() {
		return "tree: "+this.mDisplayedTreeSet.toString();
	}

	public void setElements(Iterator<String> projectList) {
		
		if(projectList == null)
			return;
		
		this.mDisplayedTreeSet.clear();
		while(projectList.hasNext()) {
			this.mDisplayedTreeSet.add(projectList.next());
		}

		refresh();

	}

	public void removeElements(Iterator<String> projectList) {
		
		if(projectList == null)
			return;
		
		while(projectList.hasNext()) {
			this.mDisplayedTreeSet.remove(projectList.next());
		}

		refresh();

	}

	public Iterator<String> getAllElements() {
		
		return this.mDisplayedTreeSet.iterator();

	}
}