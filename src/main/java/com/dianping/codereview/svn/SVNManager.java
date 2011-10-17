package com.dianping.codereview.svn;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		Collection<SVNDirEntry> entries = repository.getDir(path, -1, properties, (Collection<SVNDirEntry>) null);
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

}
