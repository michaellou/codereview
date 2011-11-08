/**
 * 
 */
package com.dianping.codereview.maven.domain;

/**
 * @author sean.wang
 * @since Nov 3, 2011
 */
public class Dependency {

	private String groupId;

	private String artifactId;

	private String version;

	private Scope scope;

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

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

}
