/**
 * 
 */
package com.dianping.codereview;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.svn.SVNManager;

/**
 * @author sean.wang
 * @since Dec 9, 2011
 */
public abstract class CachedSvnReviewer {
	private final static Log log = LogFactory.getLog(CachedSvnReviewer.class);

	private volatile boolean firstInit = false;

	protected SVNManager svnManager;

	protected String svnUrl;

	protected String username;

	protected String password;

	protected String svnRootDir;

	public void setSvnRootDir(String svnRootDir) {
		this.svnRootDir = svnRootDir;
	}

	public void setSvnUrl(String host) {
		this.svnUrl = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void init() throws SVNException, FormatInvalidException {
		initSvn();
		Runnable r = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						cacheFiles();
						firstInit = true;
						Thread.sleep(60 * 1000);
					} catch (Exception e) {
						log.error("", e);
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}

	/**
	 * @throws SVNException
	 */
	public void initSvn() throws SVNException {
		this.svnManager = new SVNManager(svnUrl, username, password);
		this.svnManager.setOnlyTrunk(true);
	}

	public boolean isFirstInit() {
		return firstInit;
	}

	protected abstract void cacheFiles() throws SVNException, FormatInvalidException;
}
