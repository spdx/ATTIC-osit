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
package com.sec.ose.osi.ui.frm.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.match.MatchedInfoMgr;
import com.sec.ose.osi.data.project.OSIProjectInfoMgr;
import com.sec.ose.osi.sdk.SDKInterface;
import com.sec.ose.osi.sdk.protexsdk.discovery.ProjectDiscoveryControllerMap;
import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.ui.ApplicationCloseMgr;
import com.sec.ose.osi.ui.UISDKInterfaceManager;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui._util.WindowUtil;
import com.sec.ose.osi.ui.cache.CacheableMgr;
import com.sec.ose.osi.ui.dialog.about.JDlgAbout;
import com.sec.ose.osi.ui.frm.login.JFrmLogin;

/**
 * JTrayIconApp
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class JTrayIconApp {
	private static Log log = LogFactory.getLog(JTrayIconApp.class);
	
	public static final int DOUBLE_CLICK = 2;
	public static final int BEFORE_LOGIN_STATE = 0;
	public static final int AFTER_LOGIN_STATE = 1;

	private SystemTray mSystemTray = SystemTray.getSystemTray();
    private TrayIcon mTrayIcon = null;

    private JFrame currentFrame = null;
    private JFrame frmLogin = null;
    
    private String mTrayTitle = "";
    private SDKInterface mSDKInterface;

    public JTrayIconApp(String trayTitle, JFrame pFrmLogin) {
    	
    	mTrayTitle = trayTitle;
    	frmLogin = pFrmLogin;
    	mSDKInterface = UISDKInterfaceManager.getSDKInterface();
    	initTray();
    	
    }

    private void initTray()
    {
    	Image image = new ImageIcon(WindowUtil.class.getResource("icon.png")).getImage();

    	mTrayIcon = new TrayIcon(image, mTrayTitle, createPopupMenu(BEFORE_LOGIN_STATE));
    	mTrayIcon.setImageAutoSize(true);
    	mTrayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
    		
    		public void mousePressed(java.awt.event.MouseEvent e) {			
    			log.debug("mousePressed()");   			
    			int state = 0;    			
				currentFrame = UISharedData.getInstance().getCurrentFrame();				
				
				if(currentFrame == null) return;				
				if(currentFrame.getClass().equals(JFrmLogin.class)) {					
					state = BEFORE_LOGIN_STATE;					
				} else {					
					state = AFTER_LOGIN_STATE;
				}

				if(SwingUtilities.isRightMouseButton(e)) { 					
					mTrayIcon.setPopupMenu(createPopupMenu(state));			
				}

				if(e.getClickCount() >= DOUBLE_CLICK && !e.isConsumed()){ 				
					currentFrame.setVisible(true);
				}		
			}
		});

    	try 
    	{
			mSystemTray.add(mTrayIcon);
		} 
    	catch (AWTException e1) 
    	{
    		log.warn(e1.getMessage());
		}
    }

    private PopupMenu createPopupMenu(int state)
    {
        PopupMenu popupMenu = new PopupMenu("PopupMenu");

        MenuItem miLogOut = new MenuItem("LogOut");
        MenuItem miOpen = new MenuItem("Open");
        MenuItem miExit = new MenuItem("Exit");
        MenuItem miAbout = new MenuItem("About");
	    
        miLogOut.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				log.debug("actionPerformed() - LogOut");
				mEventHandler.handle(EventHandler.LOGOUT_MENU);
			}
        	
        });
        
        miOpen.addActionListener(new java.awt.event.ActionListener() {
        	
        	public void actionPerformed(java.awt.event.ActionEvent e) {
        		
        		log.debug("actionPerformed() - Open");
        		mEventHandler.handle(EventHandler.OPEN_MENU);
        	}

        });
        
        miAbout.addActionListener(new java.awt.event.ActionListener() {
        	
        	public void actionPerformed(java.awt.event.ActionEvent e) {
        		
        		log.debug("actionPerformed() - Help - SubSix");
        		mEventHandler.handle(EventHandler.HELP_ABOUT);
        	}
        	
        });
        
        miExit.addActionListener(new java.awt.event.ActionListener() {
        	
        	public void actionPerformed(java.awt.event.ActionEvent e) {
        		
        		log.debug("actionPerformed() - Exit");
        		mEventHandler.handle(EventHandler.EXIT_MENU);
        	}
        });

        switch(state) {
        
    		case BEFORE_LOGIN_STATE :
		        popupMenu.add(miOpen);
	   	     	popupMenu.addSeparator();
	   	     	popupMenu.add(miAbout);
	   	     	popupMenu.addSeparator();
	   	     	popupMenu.add(miExit);
	   	     	break;
	   	     	
        	case AFTER_LOGIN_STATE :
        		popupMenu.add(miLogOut);
    	        popupMenu.addSeparator();
    	        popupMenu.add(miOpen);
	   	     	popupMenu.addSeparator();
	   	     	popupMenu.add(miAbout);
       	     	popupMenu.addSeparator();
       	     	popupMenu.add(miExit);
       	     	break;
        }
        
        return popupMenu;
    }
    
    private EventHandler mEventHandler = new EventHandler();
    
    class EventHandler {
    	
    	protected static final int LOGOUT_MENU = 0;
    	protected static final int ANALYSIS_MENU = 1;
    	protected static final int OPEN_MENU = 2;
    	protected static final int HELP_ABOUT = 8;
    	protected static final int EXIT_MENU = 9;
    	
    	public void handle(int action) {
    		
    		switch(action) { 
    		
	    		case LOGOUT_MENU:
	    			CacheableMgr.getInstance().saveToCache();

	        		currentFrame.setVisible(false);
	        		currentFrame.dispose();
	        		currentFrame = null;

	        		mSystemTray.remove(mTrayIcon);
	        		mSDKInterface.discardAllUserData();

	        		BackgroundJobManager.getInstance().stopJoinAllBackgroudThread();

	        		OSIProjectInfoMgr.getInstance().clear();
	        		ProjectDiscoveryControllerMap.clear();
	        		MatchedInfoMgr.getInstance().resetForLogout();
	        		CacheableMgr.getInstance().removeAll();

					try {
						mSystemTray.add(mTrayIcon);
					} catch (AWTException e) {
						log.warn(e);
					}

	        		CacheableMgr.getInstance().loadFromCache();
	        		UISharedData.getInstance().setCurrentFrame(frmLogin);
	        		((JFrmLogin)frmLogin).setPasswordText("");
	        		frmLogin.setVisible(true);
	        		
	    			break;
	    			
	    		case OPEN_MENU:
	        		currentFrame.setVisible(true);	    			
	    			break;

	    		case HELP_ABOUT:
		    		log.debug("HELP_ABOUT click!!");
		    		JDlgAbout.getAboutDialog().setVisible(true);
		    		break;
		    		
	    		case EXIT_MENU:
					ApplicationCloseMgr.getInstance().exit();
	    			break;
    		}
    	}
    }
}
