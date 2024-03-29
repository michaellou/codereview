package com.dianping.codereview.maven.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Pom {
	private Dependency parent;

	private String svnPath;

	private String groupId;

	private String artifactId;

	private String version;

	private Packaging packaging;

	private String name;

	private Map<String, String> properties;

	private List<String> modules;

	private List<Dependency> dependencies;

	private Date created = new Date();

	private Dependency searchedDependency;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Packaging getPackaging() {
		return packaging;
	}

	public void setPackaging(Packaging packaging) {
		this.packaging = packaging;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public Dependency getParent() {
		return parent;
	}

	public void setParent(Dependency parent) {
		this.parent = parent;
	}

	public void addDependency(Dependency d) {
		if (this.dependencies == null) {
			this.dependencies = new ArrayList<Dependency>();
		}
		d.setSearchedPom(this);
		this.dependencies.add(d);
	}

	public void addModule(String module) {
		if (this.modules == null) {
			this.modules = new ArrayList<String>();
		}
		this.modules.add(module);
	}

	public String getSvnPath() {
		return svnPath;
	}

	public void setSvnPath(String svnPath) {
		this.svnPath = svnPath;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isDepended(String artifactId, String version) {
		if (this.dependencies == null || this.dependencies.size() == 0) {
			return false;
		}
		for (Dependency depend : this.dependencies) {
			if (artifactId.equals(depend.getArtifactId())) {
				if (version == null || version.equals(depend.getVersion())) {
					return true;
				}
			}
		}
		return false;
	}

	public Dependency getDependency(String artifactId) {
		if (this.dependencies == null || this.dependencies.size() == 0) {
			return null;
		}
		for (Dependency depend : this.dependencies) {
			if (artifactId.equals(depend.getArtifactId())) {
				return depend;
			}
		}
		return null;
	}

	public Dependency getSearchedDependency() {
		return searchedDependency;
	}

	public void setSearchedDependency(Dependency searched) {
		this.searchedDependency = searched;
	}

}
