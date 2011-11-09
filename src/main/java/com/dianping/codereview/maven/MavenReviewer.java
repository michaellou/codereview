package com.dianping.codereview.maven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;

import com.dianping.codereview.maven.domain.Dependency;
import com.dianping.codereview.maven.domain.Pom;
import com.dianping.codereview.svn.Resource;
import com.dianping.codereview.svn.SVNManager;

public class MavenReviewer {
	private final static Log log = LogFactory.getLog(MavenReviewer.class);

	private Map<String, List<Pom>> beDependencies;

	private String svnUrl;

	private String username;

	private String password;

	private String svnRootDir;

	private SVNManager svnManager;

	public void setBeDependencies(Map<String, List<Pom>> antiDependencies) {
		this.beDependencies = antiDependencies;
	}

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

	private volatile boolean firstInit = false;

	private Map<String, List<Pom>> cache;

	public void init() throws SVNException, MavenPomFormatInvalidException {
		this.svnManager = new SVNManager(svnUrl, username, password);
		this.svnManager.setOnlyTrunk(true);
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

	public boolean isFirstInit() {
		return firstInit;
	}

	public List<Dependency> searchBeDepended(String artifactId, String version) {
		return this.searchBeDepended(artifactId, null, version);
	}

	public List<Dependency> searchBeDepended(String artifactId, String groupId, String version) {
		if (this.beDependencies == null || this.beDependencies.size() == 0) {
			return null;
		}
		List<Dependency> list = new ArrayList<Dependency>();
		Set<Entry<String, List<Pom>>> entrySet = this.beDependencies.entrySet();
		for (Entry<String, List<Pom>> entry : entrySet) {
			String dependArtifactId = entry.getKey();
			if (artifactId == null || dependArtifactId.contains(artifactId)) {
				List<Pom> pomList = entry.getValue();
				for (Pom pom : pomList) {
					Dependency dependency = pom.getDependency(dependArtifactId);
					if (groupId == null || (dependency.getGroupId() != null && dependency.getGroupId().contains(groupId))) {
						if (version == null) {
							String dependedVersion = dependency.getVersion();
							Dependency dep = new Dependency();
							dep.setArtifactId(dependArtifactId);
							dep.setVersion(dependedVersion);
							dep.setSearchedPom(pom);
							list.add(dep);
							log.debug("add:" + dep.getArtifactId() + ":" + dep.getVersion() + " bedepend: " + pom.getArtifactId());
						} else if (pom.isDepended(dependArtifactId, version)) {
							Dependency dep = new Dependency();
							dep.setArtifactId(dependArtifactId);
							dep.setVersion(version);
							dep.setSearchedPom(pom);
							list.add(dep);
						}
					}
				}
			}
		}
		return list;
	}

	public void dumpBeDepended() {
		if (this.beDependencies == null || this.beDependencies.size() == 0) {
			return;
		}
		Set<Entry<String, List<Pom>>> entrySet = this.beDependencies.entrySet();
		for (Entry<String, List<Pom>> entry : entrySet) {
			String dependArtifactId = entry.getKey();
			System.out.println(dependArtifactId);
			List<Pom> pomList = entry.getValue();
			for (Pom pom : pomList) {
				Dependency dependency = pom.getDependency(dependArtifactId);
				System.out.println("--" + pom.getArtifactId());
				System.out.println("----" + dependency.getGroupId() + "," + dependency.getArtifactId() + "," + dependency.getVersion());
			}
		}
	}

	public List<Dependency> searchDepended(String artifactId, String version) {
		return this.searchDepended(artifactId, null, version);
	}

	public List<Dependency> searchDepended(String artifactId, String groupId, String version) {
		if (this.cache == null || this.cache.size() == 0) {
			return null;
		}
		Set<Entry<String, List<Pom>>> entrySet = this.cache.entrySet();
		List<Dependency> list = new ArrayList<Dependency>();
		for (Entry<String, List<Pom>> entry : entrySet) {
			if (artifactId == null || entry.getKey().contains(artifactId)) {
				List<Pom> pomList = entry.getValue();
				for (Pom pom : pomList) {
					if (groupId == null || (pom.getGroupId() != null && pom.getGroupId().contains(groupId))) {
						if (version == null || pom.getVersion().equals(version)) {
							if (pom.getDependencies() != null) {
								list.addAll(pom.getDependencies());
							}
						}
					}
				}
			}
		}
		return list;
	}

	private void cacheFiles() throws SVNException, MavenPomFormatInvalidException {
		Map<String, List<Pom>> antiCache = new HashMap<String, List<Pom>>();
		Map<String, List<Pom>> cache = new HashMap<String, List<Pom>>();
		List<Resource> ress = this.svnManager.getDescendantFiles(this.svnRootDir, "pom.xml");
		for (Resource res : ress) {
			log.debug("start cache file:" + res.getPath());
			String path = res.getPath();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			this.svnManager.getFile(path, out, -1, null);
			Pom pom = MavenFileAnalyzer.analyze(new ByteArrayInputStream(out.toByteArray()));
			pom.setSvnPath(path);
			List<Pom> depends = cache.get(pom.getArtifactId());
			if (depends == null) {
				depends = new ArrayList<Pom>();
				cache.put(pom.getArtifactId(), depends);
			}
			depends.add(pom);
			this.cache = cache;
			List<Dependency> dependencies = pom.getDependencies();
			if (dependencies != null) {
				for (Dependency dependency : dependencies) {
					String artifactId = dependency.getArtifactId();
					List<Pom> antiDepends = antiCache.get(artifactId);
					if (antiDepends == null) {
						antiDepends = new ArrayList<Pom>();
						antiCache.put(artifactId, antiDepends);
					}
					antiDepends.add(pom);
				}
			}
			this.beDependencies = antiCache;
		}
	}

}
