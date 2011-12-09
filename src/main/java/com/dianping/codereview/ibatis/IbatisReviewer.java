/**
 * 
 */
package com.dianping.codereview.ibatis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.CachedSvnReviewer;
import com.dianping.codereview.ibatis.domain.Sql;
import com.dianping.codereview.ibatis.domain.SqlMap;
import com.dianping.codereview.svn.Resource;

/**
 * @author sean.wang
 * @since Oct 14, 2011
 */
public class IbatisReviewer extends CachedSvnReviewer {
	private final static Log log = LogFactory.getLog(IbatisReviewer.class);

	private Map<String, List<Sql>> sqlCache;

	public List<Sql> fetchSqlsFromSvn(String path) throws SVNException {
		Resource res = new Resource();
		res.setName(path);
		res.setPath(path);
		List<Sql> sqls = new ArrayList<Sql>();
		if (svnManager.isFile(res, -1)) {
			fetchFile(res, sqls);
		} else {
			fetchDir(res, sqls);
		}
		return sqls;
	}

	private void fetchDir(Resource dir, List<Sql> sqls) throws SVNException {
		List<Resource> children = svnManager.getChildren(dir, null);
		if (children == null) {
			return;
		}
		for (Resource r : children) {
			if (r.isFile()) {
				fetchFile(r, sqls);
			} else {
				fetchDir(r, sqls);
			}
		}
	}

	public void fetchFile(Resource r, List<Sql> sqls) throws SVNException {
		if (r.getName().endsWith(".xml")) {
			log.info("fetch file:" + r.getPath());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			svnManager.getFile(r.getPath(), out, -1, null);
			SqlMap sqlmap = null;
			try {
				sqlmap = IbatisFileAnalyzer.analyze(new ByteArrayInputStream(out.toByteArray()));
			} catch (IbatisFileFormatInvalidException e) {
				log.info("not ibatis file:" + r.getPath());
			}
			if (sqlmap != null && sqlmap.getSqls() != null) {
				for (Sql sql : sqlmap.getSqls()) {
					sql.setPath(r.getPath());
					sqls.add(sql);
				}
			}
		}
	}

	public List<Sql> fetchSqlsFromCache() throws SVNException {
		Resource res = new Resource();
		res.setName(this.svnRootDir);
		res.setPath(this.svnRootDir);
		List<Sql> sqls = new ArrayList<Sql>();
		if (svnManager.isFile(res, -1)) {
			fetchFileFromCache(res, sqls);
		} else {
			fetchDirSqlsFromCache(res, sqls);
		}
		return sqls;
	}

	public void fetchDirSqlsFromCache(Resource dir, List<Sql> sqls) throws SVNException {
		List<Resource> children = svnManager.getChildren(dir, null);
		if (children == null) {
			return;
		}
		for (Resource r : children) {
			if (r.isFile()) {
				fetchFileFromCache(r, sqls);
			} else {
				fetchDirSqlsFromCache(r, sqls);
			}
		}
	}

	public void fetchFileFromCache(Resource file, List<Sql> sqls) throws SVNException {
		List<Sql> list = this.sqlCache.get(file.getPath());
		if (list != null) {
			log.info("fetch file:" + file.getPath());
			sqls.addAll(list);
		}
	}

	@Override
	protected void cacheFiles() throws SVNException {
		Map<String, List<Sql>> cache = new HashMap<String, List<Sql>>();
		List<Resource> xmlFiles = this.svnManager.getPatternDescendantFiles(svnRootDir, ".*\\.xml");
		List<Sql> sqls = new ArrayList<Sql>();
		for (Resource xml : xmlFiles) {
			this.fetchFile(xml, sqls);
		}
		for (Sql sql : sqls) {
			String filePath = sql.getPath();
			List<Sql> fileSqls = cache.get(filePath);
			if (fileSqls == null) {
				fileSqls = new ArrayList<Sql>();
				cache.put(filePath, fileSqls);
			}
			fileSqls.add(sql);
		}
		this.sqlCache = cache;
	}

}
