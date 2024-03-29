package com.dianping.codereview.svn;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author sean.wang
 * @since Oct 14, 2011
 */
public class SVNManager {
	private final static Log log = LogFactory.getLog(SVNManager.class);

	private String host;

	private String username;

	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private SVNRepository repository;

	private boolean onlyTrunk = false;

	public SVNManager(String host, String username, String password) throws SVNException {
		this.host = host;
		this.username = username;
		this.password = password;
		this.initialize();
	}

	/**
	 * 初始化操作
	 * 
	 * @throws Exception
	 */
	private void initialize() throws SVNException {
		DAVRepositoryFactory.setup();
		repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(this.host));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.username, this.password);
		repository.setAuthenticationManager(authManager);
	}

	/**
	 * 从SVN服务器获取文件
	 * 
	 * @param filePath
	 *            相对于仓库根目录的路径
	 * @param outputStream
	 *            要输出的目标流，可以是文件流 FileOutputStream
	 * @param version
	 *            要checkout的版本号
	 * @return 返回checkout文件的版本号
	 * @throws Exception
	 *             可以自定义Exception
	 */
	public void getFile(String filePath, OutputStream outputStream, long version, SVNProperties properties) throws SVNException {
		repository.getFile(getPath(filePath), version, properties, outputStream);
	}

	/**
	 * 获取目录下的所有文件和子目录
	 * 
	 * @param res
	 *            包含目录参数的资源对象.参加{@link Resource#getPath()}
	 * @return 资源列表
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getChildren(Resource res, SVNProperties properties) throws SVNException {
		String path = getPath(res.getPath());
		Collection<SVNDirEntry> entries = null;
		try {
			entries = repository.getDir(path, -1, properties, (Collection<SVNDirEntry>) null);
		} catch (org.tmatesoft.svn.core.SVNAuthenticationException e) {
			return null;
		}
		List<Resource> result = new ArrayList<Resource>();
		for (SVNDirEntry entry : entries) {
			Resource resource = new Resource();
			resource.setName(entry.getName());
			resource.setPath(entry.getURL().getPath());
			resource.setFile(entry.getKind() == SVNNodeKind.FILE);
			result.add(resource);
		}
		return result;
	}

	public List<Resource> getDescendantFiles(String rootPath, String filename) throws SVNException {
		List<Resource> files = new ArrayList<Resource>();
		Resource root = new Resource();
		root.setPath(rootPath);
		fetchDir(root, files, filename);
		return files;
	}

	private void fetchDir(Resource dir, List<Resource> files, String filename) throws SVNException {
		List<Resource> children = getChildren(dir, null);
		if (children == null) {
			return;
		}
		for (Resource r : children) {
			if (r.isFile()) {
				if (onlyTrunk && !r.getPath().contains("trunk")) {
					continue;
				}
				if (filename == null || filename.equals(r.getName())) {
					log.debug("fetch file:" + r.getPath());
					files.add(r);
				}
			} else {
				if (onlyTrunk && ("branches".equals(r.getName()) || "tags".equals(r.getName()))) {
					continue;// ignore not trunk dir
				}
				fetchDir(r, files, filename);
			}
		}
	}

	public List<Resource> getPatternDescendantFiles(String rootPath, String filenamePattern) throws SVNException {
		List<Resource> files = new ArrayList<Resource>();
		Resource root = new Resource();
		root.setPath(rootPath);
		fetchPatternDir(null, root, files, filenamePattern);
		return files;
	}

	public void fetchPatternDir(Pattern pattern, Resource dir, List<Resource> files, String filenamePattern) throws SVNException {
		List<Resource> children = getChildren(dir, null);
		if (children == null) {
			return;
		}
		for (Resource r : children) {
			if (r.isFile()) {
				if (onlyTrunk && !r.getPath().contains("trunk")) {
					continue;
				}
				if (filenamePattern == null) {
					log.debug("no pattern, fetch file:" + r.getPath());
					files.add(r);
				} else {
					if (pattern == null) {
						pattern = Pattern.compile(filenamePattern);
					}
					Matcher matcher = pattern.matcher(r.getName());
					if (matcher.matches()) {
						log.debug("match pattern, fetch file:" + r.getPath());
						files.add(r);
					}
				}
			} else {
				if (onlyTrunk && ("branches".equals(r.getName()) || "tags".equals(r.getName()))) {
					continue;// ignore not trunk dir
				}
				fetchPatternDir(pattern, r, files, filenamePattern);
			}
		}
	}

	/**
	 * 判断svn上指定文件是否存在
	 * 
	 * @param filePath
	 * @param version
	 * @return
	 * @throws SVNException
	 */
	public boolean isFile(String filePath, long version) throws SVNException {
		SVNNodeKind node = repository.checkPath(getPath(filePath), version);
		if (node == null || node != SVNNodeKind.FILE) {
			return false;
		}
		return true;
	}

	public boolean isFile(Resource r, long version) throws SVNException {
		return isFile(r.getPath(), version);
	}

	private String getPath(String filePath) {
		return filePath.substring(1);
	}

	/**
	 * 递归判断svn上指定文件是否存在
	 * 
	 * @param entry
	 *            要判断的节点.参加{@link SVNDirEntry}
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public boolean containsSpecialFile(SVNDirEntry entry) throws SVNException {
		if (entry.getKind() == SVNNodeKind.FILE) {
			return true;
		} else if (entry.getKind() == SVNNodeKind.DIR) {
			Collection<SVNDirEntry> entries;
			String path = entry.getURL().getPath();
			entries = repository.getDir(path, -1, null, (Collection<SVNDirEntry>) null);
			for (SVNDirEntry unit : entries) {
				if (containsSpecialFile(unit)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public boolean isOnlyTrunk() {
		return onlyTrunk;
	}

	public void setOnlyTrunk(boolean onlyTrunk) {
		this.onlyTrunk = onlyTrunk;
	}

}
