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
package com.sec.ose.osi.ui.frm.main.identification;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.sdk.protexsdk.codetree.CodeTreeAPIWrapper;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.frm.main.identification.common.SelectedFilePathInfo;

/**
 * JTreeAllFiles
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class JTreeAllFiles extends JTree {
	private static Log log = LogFactory.getLog(JTreeAllFiles.class);
	private static final long serialVersionUID = 1L;
	private HashMap<String,Integer> pendingFileCount = new HashMap<String,Integer>();	
	private CodeTreeModel codeTreeModel = null;
	private DefaultMutableTreeNode defaultMutableTreeNode = null;
	private DefaultTreeModel defaultTreeModel = null;

	public JTreeAllFiles() {
		
		codeTreeModel = new CodeTreeModel(CodeTreeAPIWrapper.getCodeTree(""));
		defaultMutableTreeNode = codeTreeModel.makeTree();
		defaultTreeModel = new DefaultTreeModel(defaultMutableTreeNode);

		this.setModel(defaultTreeModel);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setCellRenderer(new MyTreeRenderer());
		this.addTreeSelectionListener(new FileSelectionListener());
	}

	public void updateTreeUI(
			ArrayList<String> pendingFileList,
			ArrayList<String> identifiedFilePathList ) {
		
		log.debug("updateTreeUI executing...");
		
		if(pendingFileList.size() >= 0) {
			calculatePendingCount(pendingFileList);
		}
		
		String ProjectName = IdentifyMediator.getInstance().getSelectedProjectName();
		SelectedFilePathInfo selectedPaths = IdentifyMediator.getInstance().getSelectedFilePathInfo();
		
		codeTreeModel.setPartialCodeTree(CodeTreeAPIWrapper.getCodeTree(ProjectName));
		defaultMutableTreeNode = codeTreeModel.makeTree();
		defaultTreeModel.setRoot(defaultMutableTreeNode);
		defaultTreeModel.setAsksAllowsChildren(false);
		this.setModel(defaultTreeModel);

		log.debug("selectedPaths.getSelectedPath() : " + selectedPaths.getSelectedPath());
		setSelectedFile(selectedPaths.getSelectedPath(), pendingFileList, identifiedFilePathList);

		IdentifyMediator.getInstance().setHorizontalScrollBarValue(0); 
		
		this.repaint();
		
	}

	private void calculatePendingCount(ArrayList<String> fileListInfo) {
		pendingFileCount.clear();
		for(String filePath:fileListInfo) {
			pendingFileCount.put(filePath, -1);
			incrementPendingCount(filePath);
		}
	}

	private void incrementPendingCount(String filePath) {
		int index = filePath.lastIndexOf("/");
		if(index <= 0) return;
		
		String tempGetSubstr = filePath.substring(0, index);
		Integer curCount = (Integer)pendingFileCount.get(tempGetSubstr);
		if(curCount != null) {
			curCount += 1;
		} else {
			curCount = 1;
		}
		pendingFileCount.put(tempGetSubstr,curCount);
		incrementPendingCount(tempGetSubstr);
	}

	private void decrementPendingCount(String filePath) {
		
		int index = filePath.lastIndexOf("/");
		if(index <= 0) return;
		
		String tempGetSubstr = filePath.substring(0, index);
		Integer curCount = (Integer)pendingFileCount.get(tempGetSubstr);
		if(curCount != null) {
			if(curCount > 1) {
				curCount--;
				pendingFileCount.put(tempGetSubstr,curCount);
			} else {
				pendingFileCount.remove(tempGetSubstr);
			}
		} else {
			return;
		}
		decrementPendingCount(tempGetSubstr);
	}

	private TreePath getTreePathByFilePathString(DefaultMutableTreeNode rootNode, String selectedFilePath) {
		TreePath tp = null;
		if(selectedFilePath != null) {
			DefaultMutableTreeNode node = null;
			Enumeration<?> enumer = rootNode.breadthFirstEnumeration();
			String sFilePath = "";
			DefaultTreeModel m_model = new DefaultTreeModel(rootNode);
			TreeNode[] nodes = null;
			while(enumer.hasMoreElements()) {
				node = (DefaultMutableTreeNode) enumer.nextElement();
				if(node.getUserObject().equals("/")) continue;
				sFilePath = ((FileNodeInfo)node.getUserObject()).getFilePath();
				if(sFilePath.equals(selectedFilePath)) {
					nodes = m_model.getPathToRoot(node);
					tp = new TreePath(nodes);
					break;
				}
			}
		}
		return tp;
	}

	void markPendingFileToIdentified(
			Collection<String> updateIdentifiedTargetFilePaths) {
		// Tree Update
		for(String tmpIdentifiedFile:updateIdentifiedTargetFilePaths) {
			if(pendingFileCount.containsKey(tmpIdentifiedFile)) {
				pendingFileCount.remove(tmpIdentifiedFile);
				decrementPendingCount(tmpIdentifiedFile);
			}
		}
	}

	void markIdentifiedToPending(
			ArrayList<String> additionalResetFilePathList) {
		// Tree Update
		for(String tmpResetFile:additionalResetFilePathList) {
			if(pendingFileCount.get(tmpResetFile) == null) {
				pendingFileCount.put(tmpResetFile, -1);
				incrementPendingCount(tmpResetFile);
			}
		}
		
	}

	void setSelectedFile(
			String filePath, 
			ArrayList<String> pendingFileList, 
			ArrayList<String> identifiedFilePathList) {
		
		System.out.println("### selected path for tree : " + filePath);
		TreePath path = getTreePathByFilePathString(codeTreeModel.getRootNode(), filePath);
		if( path == null && pendingFileList!=null && pendingFileList.size() > 0){
			path = getTreePathByFilePathString(codeTreeModel.getRootNode(), pendingFileList.get(0));
		} else if (path == null && identifiedFilePathList!=null &&  identifiedFilePathList.size() > 0){
			path = getTreePathByFilePathString(codeTreeModel.getRootNode(), identifiedFilePathList.get(0));
		}
		this.scrollPathToVisible(path);
		this.setSelectionPath(path);
		 
	}

	class CodeTreeModel {

		PartialCodeTree partialCodeTree = null;
		private String rootPath = "";
		DefaultMutableTreeNode rootNode = null;
		public CodeTreeModel(PartialCodeTree codeTree) {
			setPartialCodeTree(codeTree);
		}
		
		public void setPartialCodeTree(PartialCodeTree codeTree) {
			if(codeTree !=null ) {
				this.partialCodeTree = codeTree;
				this.rootPath = partialCodeTree.getParentPath();
				if(this.rootPath.equals("/")) this.rootPath="";
			}
		}

		public DefaultMutableTreeNode makeTree() {
			
			String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
			
			String[] splitNodePath = null;
			FileNodeInfo rootFileNodeInfo = new FileNodeInfo(
					projectName, 
					this.rootPath, 
					SelectedFilePathInfo.PROJECT_TYPE);
			
			rootNode = new DefaultMutableTreeNode(rootFileNodeInfo);
			DefaultMutableTreeNode  parentNode = null;
		
			
			if(partialCodeTree==null)
				return null;
			
			List<CodeTreeNode> codeTreeNodeList= partialCodeTree.getNodes();
			Collections.sort(codeTreeNodeList, new NameAscCompare());
			
			for(CodeTreeNode tmpCodeTreeNode:codeTreeNodeList) {
				String nodePath = tmpCodeTreeNode.getName();
				String nodeType = tmpCodeTreeNode.getNodeType().toString();
				int pathType = SelectedFilePathInfo.INVALID_TYPE;
				
				if(IdentifyMediator.STR_FILE.equals(nodeType))
					pathType = SelectedFilePathInfo.SINGLE_FILE_TYPE;
				else if(IdentifyMediator.STR_FOLDER.equals(nodeType))
					pathType = SelectedFilePathInfo.FOLDER_TYPE;
				else if(IdentifyMediator.STR_PROJECT.equals(nodeType))		
					pathType = SelectedFilePathInfo.PROJECT_TYPE;
				
				
				if(nodePath.length() == 0) continue;
				
				splitNodePath = nodePath.split("/");
				parentNode = rootNode;

				for(int i=0; i<splitNodePath.length; i++) {
					DefaultMutableTreeNode tn = findTreeNode(parentNode, splitNodePath[i]);
					if(tn == null){
						addSubNode(splitNodePath, i, parentNode, nodePath, pathType);
						break;
					}
					else{
						parentNode = tn;
					}
				}
			}
				
			return rootNode;
		}
		
		@SuppressWarnings("unchecked")
		private DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode node, String findText){
			Enumeration e = node.children();
			
			DefaultMutableTreeNode findNode = null;
			
			while(e.hasMoreElements()){
				findNode = (DefaultMutableTreeNode)e.nextElement();
				Object oNode = findNode.getUserObject();
				FileNodeInfo oFindNodeInfo = (FileNodeInfo) oNode;
				
				if(oFindNodeInfo.toString().equals(findText)){
					return findNode;
				}
			}
			
			return null;
		}
		
		private void addSubNode(String[] splitNodePath, int sIdx, DefaultMutableTreeNode parentNode, String nodePath, int fileOrFolder){
			DefaultMutableTreeNode childNode = null;
			
			for(int i=sIdx; i<splitNodePath.length; i++) {
				childNode = new DefaultMutableTreeNode(new FileNodeInfo(splitNodePath[i], nodePath, fileOrFolder));
				parentNode.add(childNode);
				parentNode = childNode;
			}
		}
		
		private DefaultMutableTreeNode getRootNode() {
			return rootNode;
		}

	}
	
	class FileNodeInfo {
		
		private String fileName;
		private String filePath;
		private int pathType;
		
		public FileNodeInfo(String sFileName, String sFilePath, int pathType) {
			this.fileName = sFileName;
			this.filePath = sFilePath;
			this.pathType = pathType;
		}
		
		public String getFilePath() {
			return this.filePath;
		}
		
		public int getPathType() {
			return this.pathType;
		}
		
		public String toString() {
			return fileName;
		}
		
	}
	
	class MyTreeRenderer extends DefaultTreeCellRenderer {
		
		static final String ICON_PROJECT = "icon_project.png";
		static final String ICON_OPEN_FOLDER = "icon_folder_open.gif";
		static final String ICON_CLOSED_FOLDER = "icon_folder_closed.gif";
		static final String ICON_FILE = "icon_document.gif";
		
		private static final long serialVersionUID = 1L;

		ImageIcon project_icon = new ImageIcon(WindowUtil.class.getResource(ICON_PROJECT));
		ImageIcon folder_close_icon = new ImageIcon(WindowUtil.class.getResource(ICON_CLOSED_FOLDER));
		ImageIcon folder_open_icon = new ImageIcon(WindowUtil.class.getResource(ICON_OPEN_FOLDER));
		ImageIcon file_icon = new ImageIcon(WindowUtil.class.getResource(ICON_FILE));
		
		Font NO_DISCOVERY_FILE_FONT = new Font("SansSerif", Font.PLAIN, 12);
		Font DISCOVERY_FILE_FONT = new Font("SansSerif", Font.BOLD, 12);
		
		public Component getTreeCellRendererComponent(
				JTree tree, 
				Object value, 
				boolean sel, 
				boolean expanded, 
				boolean leaf, 
				int row, 
				boolean hasFocus) {

			JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			label.setFont(NO_DISCOVERY_FILE_FONT);
			label.setForeground(ColorDic.PENDING_COLOR_BLACK);
			
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			Object obj = treeNode.getUserObject();


			if( (obj instanceof FileNodeInfo) == false)
				return MyTreeRenderer.this;

			FileNodeInfo fileNodeInfo = (FileNodeInfo) obj;
		
			
			String path = fileNodeInfo.getFilePath();
			switch(fileNodeInfo.getPathType()) {
				case SelectedFilePathInfo.SINGLE_FILE_TYPE:
					label.setIcon(file_icon);
					break;
				case SelectedFilePathInfo.PROJECT_TYPE:
					label.setIcon(project_icon);
					break;
				case SelectedFilePathInfo.FOLDER_TYPE:
					label.setIcon(folder_close_icon);
					break;
			}
			Integer tempCount = (Integer)pendingFileCount.get(path);
			if(tempCount != null) {
				label.setFont(DISCOVERY_FILE_FONT);
				label.setForeground(ColorDic.PENDING_COLOR_BLACK);
				if(tempCount > 0) {
					label.setText(this.getText()+" ("+tempCount+")");
				}
			}
			
			ArrayList<String> alIdentifiedFiles = MatchedInfoMgr.getInstance().getIdentifiedFilePathListByCurrentMatchType();
			
			if(alIdentifiedFiles.contains(path)) {
				label.setFont(DISCOVERY_FILE_FONT);
				label.setForeground(ColorDic.IDENTIFIED_COLOR_BLUE);
			}
			
			return MyTreeRenderer.this;
		}
		
	} 
	
	class FileSelectionListener implements TreeSelectionListener {
		// Tree Selection
		public void valueChanged(TreeSelectionEvent e) {

			if(IdentifyMediator.getInstance().getIndexOfTreeOrList() == JPanIdentifyMain.INDEX_TREE) {
				log.debug("JTree Selection Event");
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				FileNodeInfo oFileNodeInfo = (FileNodeInfo) node.getUserObject();
				IdentifyMediator.getInstance().setOpenCodeMatchSourceView(oFileNodeInfo.getPathType());
				IdentifyMediator.getInstance().setSelectedFile(
						oFileNodeInfo.getPathType(), 
						oFileNodeInfo.getFilePath());
				String projectName = IdentifyMediator.getInstance().getSelectedProjectName();
				IdentifyMediator.getInstance().refreshChildFrames(projectName);
			}
		}

	}

	class NameAscCompare implements Comparator<CodeTreeNode> {
		@Override
		public int compare(CodeTreeNode arg0, CodeTreeNode arg1) {
			if(arg0.getNodeType() != arg1.getNodeType()) {
				return (arg0.getNodeType() == CodeTreeNodeType.FILE)?1:-1;
			} else {
				return arg0.getName().compareTo(arg1.getName());
			}
		}
	}
	
}
