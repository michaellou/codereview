/**
 * 
 */
package com.dianping.codereview.ibatis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.ibatis.domain.Sql;
import com.dianping.codereview.ibatis.domain.SqlMap;
import com.dianping.codereview.svn.Resource;
import com.dianping.codereview.svn.SVNManager;



/**
 * @author sean.wang
 * @since Oct 14, 2011
 */
public class IbatisReviewer {
	private final static Log log = LogFactory.getLog(IbatisReviewer.class);

	public static List<Sql> fetchSqlsFromSvn(String svnUrl, String path, String username, String password) throws SVNException {
		SVNManager svnManager = new SVNManager(svnUrl, username, password);
		Resource res = new Resource();
		res.setPath(path);
		List<Sql> sqls = new ArrayList<Sql>();
		fetchDir(svnManager, res, sqls);
		return sqls;
	}

	private static void fetchDir(SVNManager svnManager, Resource dir, List<Sql> sqls) throws SVNException {
		List<Resource> children = svnManager.getChildren(dir);
		for (Resource r : children) {
			if (r.isFile()) {
				if (r.getName().endsWith(".xml")) {
					log.info("fetch file:" + r.getPath());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					String path = r.getPath().substring(1);
					svnManager.getFile(path, out, -1);
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
			} else {
				fetchDir(svnManager, r, sqls);
			}
		}
	}

}
