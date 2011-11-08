/**
 * 
 */
package com.dianping.codereview.maven;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dianping.codereview.maven.domain.Dependency;
import com.dianping.codereview.maven.domain.Packaging;
import com.dianping.codereview.maven.domain.Pom;
import com.dianping.codereview.maven.domain.Scope;

/**
 * @author sean.wang
 * @since Nov 3, 2011
 */
public class MavenFileAnalyzer {

	public static Pom analyze(File file) throws MavenPomFormatInvalidException {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			throw new MavenPomFormatInvalidException(e);
		}
		return parse(document);
	}

	public static Pom analyze(InputStream is) throws MavenPomFormatInvalidException {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(is);
		} catch (DocumentException e) {
			throw new MavenPomFormatInvalidException(e);
		}
		return parse(document);
	}

	@SuppressWarnings("rawtypes")
	private static Pom parse(Document document) throws MavenPomFormatInvalidException {
		Element rootElement = document.getRootElement();
		if (!"project".equals(rootElement.getName())) {
			throw new MavenPomFormatInvalidException();
		}
		final Pom pom = new Pom();
		for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			String eleName = element.getName();
			if ("parent".equals(eleName)) {
				parseParent(pom, element);
			} else if ("groupId".equals(eleName)) {
				pom.setGroupId(element.getTextTrim());
			} else if ("artifactId".equals(eleName)) {
				pom.setArtifactId(element.getTextTrim());
			} else if ("version".equals(eleName)) {
				pom.setVersion(element.getTextTrim());
			} else if ("name".equals(eleName)) {
				pom.setName(element.getTextTrim());
			} else if ("packaging".equals(eleName)) {
				String packName = element.getTextTrim();
				if ("pom".equals(packName)) {
					pom.setPackaging(Packaging.POM);
				} else if ("jar".equals(packName)) {
					pom.setPackaging(Packaging.JAR);
				} else if ("war".equals(packName)) {
					pom.setPackaging(Packaging.WAR);
				}
			} else if ("dependencies".equals(eleName)) {
				parseDependencies(pom, element.elementIterator());
			} else if ("dependencyManagement".equals(eleName)) {
				parseDependencies(pom, ((Element) element.elements().get(0)).elementIterator());
			} else if ("properties".equals(eleName)) {
			} else if ("modules".equals(eleName)) {
				parseModules(pom, (Element) element);
			}
		}
		return pom;
	}

	@SuppressWarnings("rawtypes")
	private static void parseParent(Pom pom, Element element) {
		Dependency parent = new Dependency();
		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element dele = (Element) i.next();
			String deleName = dele.getName();
			if ("groupId".equals(deleName)) {
				parent.setGroupId(dele.getTextTrim());
			} else if ("artifactId".equals(deleName)) {
				parent.setArtifactId(dele.getTextTrim());
			} else if ("version".equals(deleName)) {
				parent.setVersion(dele.getTextTrim());
			}
		}
		pom.setParent(parent);
	}

	@SuppressWarnings("rawtypes")
	private static void parseModules(Pom pom, Element element) {
		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element de = (Element) i.next();
			pom.addModule(de.getTextTrim());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void parseDependencies(Pom pom, Iterator i) {
		for (; i.hasNext();) {
			Dependency d = new Dependency();
			pom.addDependency(d);
			Element de = (Element) i.next();
			for (Iterator j = de.elementIterator(); j.hasNext();) {
				Element dele = (Element) j.next();
				String deleName = dele.getName();
				if ("groupId".equals(deleName)) {
					d.setGroupId(dele.getTextTrim());
				} else if ("artifactId".equals(deleName)) {
					d.setArtifactId(dele.getTextTrim());
				} else if ("version".equals(deleName)) {
					d.setVersion(dele.getTextTrim());
				} else if ("scope".equals(deleName)) {
					String value = dele.getTextTrim();
					if ("test".equals(value)) {
						d.setScope(Scope.TEST);
					} else if ("provided".equals(value)) {
						d.setScope(Scope.PROVIDED);
					} else if ("runtime".equals(value)) {
						d.setScope(Scope.RUNTIME);
					} else if ("compile".equals(value)) {
						d.setScope(Scope.COMPILE);
					} else if ("system".equals(value)) {
						d.setScope(Scope.SYSTEM);
					} else if ("import".equals(value)) {
						d.setScope(Scope.IMPORT);
					}
				}
			}
		}
	}

}
