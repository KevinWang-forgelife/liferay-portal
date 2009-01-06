/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.tools.servicebuilder;

import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ArrayUtil_IW;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.StringUtil_IW;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelHintsUtil;
import com.liferay.portal.tools.SourceFormatter;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.util.SetUtil;
import com.liferay.util.TextFormatter;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.util.xml.XMLFormatter;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.storage.Convention;
import de.hunsicker.jalopy.storage.ConventionKeys;
import de.hunsicker.jalopy.storage.Environment;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.DocumentException;

/**
 * <a href="ServiceBuilder.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Alexander Chow
 * @author Harry Mark
 * @author Tariq Dweik
 * @author Glenn Powell
 *
 */
public class ServiceBuilder {

	public static void main(String[] args) {
		InitUtil.initWithSpring();

		ServiceBuilder serviceBuilder = null;

		if (args.length == 7) {
			String fileName = args[0];
			String hbmFileName = args[1];
			String modelHintsFileName = args[2];
			String springFileName = args[3];
			String springBaseFileName = "";
			String springDynamicDataSourceFileName = "";
			String springHibernateFileName = "";
			String springInfrastructureFileName = "";
			String apiDir = args[5];
			String implDir = "src";
			String jsonFileName = args[6];
			String remotingFileName = "../tunnel-web/docroot/WEB-INF/remoting-servlet.xml";
			String sqlDir = "../sql";
			String sqlFileName = "portal-tables.sql";
			String sqlIndexesFileName = "indexes.sql";
			String sqlIndexesPropertiesFileName = "indexes.properties";
			String sqlSequencesFileName = "sequences.sql";
			boolean autoNamespaceTables = false;
			String beanLocatorUtil = "com.liferay.portal.kernel.bean.BeanLocatorUtil";
			String propsUtil = "com.liferay.portal.util.PropsUtil";
			String pluginName = "";
			String testDir = "";

			serviceBuilder = new ServiceBuilder(
				fileName, hbmFileName, modelHintsFileName, springFileName,
				springBaseFileName, springDynamicDataSourceFileName,
				springHibernateFileName, springInfrastructureFileName, apiDir,
				implDir, jsonFileName, remotingFileName, sqlDir, sqlFileName,
				sqlIndexesFileName, sqlIndexesPropertiesFileName,
				sqlSequencesFileName, autoNamespaceTables, beanLocatorUtil,
				propsUtil, pluginName, testDir);
		}
		else if (args.length == 0) {
			String fileName = System.getProperty("service.input.file");
			String hbmFileName = System.getProperty("service.hbm.file");
			String modelHintsFileName = System.getProperty("service.model.hints.file");
			String springFileName = System.getProperty("service.spring.file");
			String springBaseFileName = System.getProperty("service.spring.base.file");
			String springDynamicDataSourceFileName = System.getProperty("service.spring.dynamic.data.source.file");
			String springHibernateFileName = System.getProperty("service.spring.hibernate.file");
			String springInfrastructureFileName = System.getProperty("service.spring.infrastructure.file");
			String apiDir = System.getProperty("service.api.dir");
			String implDir = System.getProperty("service.impl.dir");
			String jsonFileName = System.getProperty("service.json.file");
			String remotingFileName = System.getProperty("service.remoting.file");
			String sqlDir = System.getProperty("service.sql.dir");
			String sqlFileName = System.getProperty("service.sql.file");
			String sqlIndexesFileName = System.getProperty("service.sql.indexes.file");
			String sqlIndexesPropertiesFileName = System.getProperty("service.sql.indexes.properties.file");
			String sqlSequencesFileName = System.getProperty("service.sql.sequences.file");
			boolean autoNamespaceTables = GetterUtil.getBoolean(System.getProperty("service.auto.namespace.tables"));
			String beanLocatorUtil = System.getProperty("service.bean.locator.util");
			String propsUtil = System.getProperty("service.props.util");
			String pluginName = System.getProperty("service.plugin.name");
			String testDir = System.getProperty("service.test.dir");

			serviceBuilder = new ServiceBuilder(
				fileName, hbmFileName, modelHintsFileName, springFileName,
				springBaseFileName, springDynamicDataSourceFileName,
				springHibernateFileName, springInfrastructureFileName, apiDir,
				implDir, jsonFileName, remotingFileName, sqlDir, sqlFileName,
				sqlIndexesFileName, sqlIndexesPropertiesFileName,
				sqlSequencesFileName, autoNamespaceTables, beanLocatorUtil,
				propsUtil, pluginName, testDir);
		}

		if (serviceBuilder == null) {
			System.out.println(
				"Please set these required system properties. Sample values are:\n" +
				"\n" +
				"\t-Dservice.input.file=${service.file}\n" +
				"\t-Dservice.hbm.file=src/META-INF/portal-hbm.xml\n" +
				"\t-Dservice.model.hints.file=src/META-INF/portal-model-hints.xml\n" +
				"\t-Dservice.spring.file=src/META-INF/portal-spring.xml\n" +
				"\t-Dservice.api.dir=${project.dir}/portal-service/src\n" +
				"\t-Dservice.impl.dir=src\n" +
				"\t-Dservice.json.file=${project.dir}/portal-web/docroot/html/js/liferay/service_unpacked.js\n" +
				"\t-Dservice.remoting.file=${project.dir}/tunnel-web/docroot/WEB-INF/remoting-servlet.xml\n" +
				"\t-Dservice.sql.dir=../sql\n" +
				"\t-Dservice.sql.file=portal-tables.sql\n" +
				"\t-Dservice.sql.indexes.file=indexes.sql\n" +
				"\t-Dservice.sql.indexes.properties.file=indexes.properties\n" +
				"\t-Dservice.sql.sequences.file=sequences.sql\n" +
				"\t-Dservice.bean.locator.util.package=com.liferay.portal.kernel.bean\n" +
				"\t-Dservice.props.util.package=com.liferay.portal.util\n" +
				"\n" +
				"You can also customize the generated code by overriding the default templates with these optional properties:\n" +
				"\n" +
				"\t-Dservice.tpl.bad_column_names=" + _TPL_ROOT + "bad_column_names.txt\n"+
				"\t-Dservice.tpl.bad_table_names=" + _TPL_ROOT + "bad_table_names.txt\n"+
				"\t-Dservice.tpl.base_mode_impl=" + _TPL_ROOT + "base_mode_impl.ftl\n"+
				"\t-Dservice.tpl.copyright.txt=copyright.txt\n"+
				"\t-Dservice.tpl.ejb_pk=" + _TPL_ROOT + "ejb_pk.ftl\n"+
				"\t-Dservice.tpl.exception=" + _TPL_ROOT + "exception.ftl\n"+
				"\t-Dservice.tpl.extended_model=" + _TPL_ROOT + "extended_model.ftl\n"+
				"\t-Dservice.tpl.extended_model_impl=" + _TPL_ROOT + "extended_model_impl.ftl\n"+
				"\t-Dservice.tpl.finder=" + _TPL_ROOT + "finder.ftl\n"+
				"\t-Dservice.tpl.finder_util=" + _TPL_ROOT + "finder_util.ftl\n"+
				"\t-Dservice.tpl.hbm_xml=" + _TPL_ROOT + "hbm_xml.ftl\n"+
				"\t-Dservice.tpl.json_js=" + _TPL_ROOT + "json_js.ftl\n"+
				"\t-Dservice.tpl.json_js_method=" + _TPL_ROOT + "json_js_method.ftl\n"+
				"\t-Dservice.tpl.model=" + _TPL_ROOT + "model.ftl\n"+
				"\t-Dservice.tpl.model_hints_xml=" + _TPL_ROOT + "model_hints_xml.ftl\n"+
				"\t-Dservice.tpl.model_impl=" + _TPL_ROOT + "model_impl.ftl\n"+
				"\t-Dservice.tpl.model_soap=" + _TPL_ROOT + "model_soap.ftl\n"+
				"\t-Dservice.tpl.persistence=" + _TPL_ROOT + "persistence.ftl\n"+
				"\t-Dservice.tpl.persistence_impl=" + _TPL_ROOT + "persistence_impl.ftl\n"+
				"\t-Dservice.tpl.persistence_util=" + _TPL_ROOT + "persistence_util.ftl\n"+
				"\t-Dservice.tpl.props=" + _TPL_ROOT + "props.ftl\n"+
				"\t-Dservice.tpl.remoting_xml=" + _TPL_ROOT + "remoting_xml.ftl\n"+
				"\t-Dservice.tpl.service=" + _TPL_ROOT + "service.ftl\n"+
				"\t-Dservice.tpl.service_base_impl=" + _TPL_ROOT + "service_base_impl.ftl\n"+
				"\t-Dservice.tpl.service_factory=" + _TPL_ROOT + "service_factory.ftl\n"+
				"\t-Dservice.tpl.service_http=" + _TPL_ROOT + "service_http.ftl\n"+
				"\t-Dservice.tpl.service_impl=" + _TPL_ROOT + "service_impl.ftl\n"+
				"\t-Dservice.tpl.service_json=" + _TPL_ROOT + "service_json.ftl\n"+
				"\t-Dservice.tpl.service_json_serializer=" + _TPL_ROOT + "service_json_serializer.ftl\n"+
				"\t-Dservice.tpl.service_soap=" + _TPL_ROOT + "service_soap.ftl\n"+
				"\t-Dservice.tpl.service_util=" + _TPL_ROOT + "service_util.ftl\n"+
				"\t-Dservice.tpl.spring_base_xml=" + _TPL_ROOT + "spring_base_xml.ftl\n"+
				"\t-Dservice.tpl.spring_dynamic_data_source_xml=" + _TPL_ROOT + "spring_dynamic_data_source_xml.ftl\n"+
				"\t-Dservice.tpl.spring_hibernate_xml=" + _TPL_ROOT + "spring_hibernate_xml.ftl\n"+
				"\t-Dservice.tpl.spring_infrastructure_xml=" + _TPL_ROOT + "spring_infrastructure_xml.ftl\n"+
				"\t-Dservice.tpl.spring_xml=" + _TPL_ROOT + "spring_xml.ftl\n"+
				"\t-Dservice.tpl.spring_xml_session=" + _TPL_ROOT + "spring_xml_session.ftl");
		}
	}

	public static void writeFile(File file, String content)
		throws IOException {

		writeFile(file, content, _AUTHOR);
	}

	public static void writeFile(File file, String content, String author)
		throws IOException {

		writeFile(file, content, author, null);
	}

	public static void writeFile(
			File file, String content, String author,
			Map<String, Object> jalopySettings)
		throws IOException {

		String packagePath = _getPackagePath(file);

		String className = file.getName();

		className = className.substring(0, className.length() - 5);

		content = SourceFormatter.stripImports(content, packagePath, className);

		File tempFile = new File("ServiceBuilder.temp");

		FileUtil.write(tempFile, content);

		// Beautify

		StringBuffer sb = new StringBuffer();

		Jalopy jalopy = new Jalopy();

		jalopy.setFileFormat(FileFormat.UNIX);
		jalopy.setInput(tempFile);
		jalopy.setOutput(sb);

		try {
			Jalopy.setConvention("../tools/jalopy.xml");
		}
		catch (FileNotFoundException fnne) {
		}

		try {
			Jalopy.setConvention("../../misc/jalopy.xml");
		}
		catch (FileNotFoundException fnne) {
		}

		if (jalopySettings == null) {
			jalopySettings = new HashMap<String, Object>();
		}

		Environment env = Environment.getInstance();

		// Author

		author = GetterUtil.getString(
			(String)jalopySettings.get("author"), author);

		env.set("author", author);

		// File name

		env.set("fileName", file.getName());

		Convention convention = Convention.getInstance();

		String classMask =
			"/**\n" +
			" * <a href=\"$fileName$.html\"><b><i>View Source</i></b></a>\n" +
			" *\n" +
			" * @author $author$\n" +
			" *\n" +
			"*/";

		convention.put(
			ConventionKeys.COMMENT_JAVADOC_TEMPLATE_CLASS,
			env.interpolate(classMask));

		convention.put(
			ConventionKeys.COMMENT_JAVADOC_TEMPLATE_INTERFACE,
			env.interpolate(classMask));

		jalopy.format();

		String newContent = sb.toString();

		/*
		// Remove blank lines after try {

		newContent = StringUtil.replace(newContent, "try {\n\n", "try {\n");

		// Remove blank lines after ) {

		newContent = StringUtil.replace(newContent, ") {\n\n", ") {\n");

		// Remove blank lines empty braces { }

		newContent = StringUtil.replace(newContent, "\n\n\t}", "\n\t}");

		// Add space to last }

		newContent = newContent.substring(0, newContent.length() - 2) + "\n\n}";
		*/

		// Write file if and only if the file has changed

		String oldContent = null;

		if (file.exists()) {

			// Read file

			oldContent = FileUtil.read(file);

			// Keep old version number

			int x = oldContent.indexOf("@version $Revision:");

			if (x != -1) {
				int y = oldContent.indexOf("$", x);
				y = oldContent.indexOf("$", y + 1);

				String oldVersion = oldContent.substring(x, y + 1);

				newContent = StringUtil.replace(
					newContent, "@version $Rev: $", oldVersion);
			}
		}
		else {
			newContent = StringUtil.replace(
				newContent, "@version $Rev: $", "@version $Revision: 1.183 $");
		}

		if (oldContent == null || !oldContent.equals(newContent)) {
			FileUtil.write(file, newContent);

			System.out.println("Writing " + file);

			// Workaround for bug with XJavaDoc

			file.setLastModified(
				System.currentTimeMillis() - (Time.SECOND * 5));
		}

		tempFile.deleteOnExit();
	}

	public ServiceBuilder(
		String fileName, String hbmFileName, String modelHintsFileName,
		String springFileName, String springBaseFileName,
		String springDynamicDataSourceFileName, String springHibernateFileName,
		String springInfrastructureFileName, String apiDir, String implDir,
		String jsonFileName, String remotingFileName, String sqlDir,
		String sqlFileName, String sqlIndexesFileName,
		String sqlIndexesPropertiesFileName, String sqlSequencesFileName,
		boolean autoNamespaceTables, String beanLocatorUtil, String propsUtil,
		String pluginName, String testDir) {

		new ServiceBuilder(
			fileName, hbmFileName, modelHintsFileName, springFileName,
			springBaseFileName, springDynamicDataSourceFileName,
			springHibernateFileName, springInfrastructureFileName, apiDir,
			implDir, jsonFileName, remotingFileName, sqlDir, sqlFileName,
			sqlIndexesFileName, sqlIndexesPropertiesFileName,
			sqlSequencesFileName, autoNamespaceTables, beanLocatorUtil,
			propsUtil, pluginName, testDir, true);
	}

	public ServiceBuilder(
		String fileName, String hbmFileName, String modelHintsFileName,
		String springFileName, String springBaseFileName,
		String springDynamicDataSourceFileName, String springHibernateFileName,
		String springInfrastructureFileName, String apiDir, String implDir,
		String jsonFileName, String remotingFileName, String sqlDir,
		String sqlFileName, String sqlIndexesFileName,
		String sqlIndexesPropertiesFileName, String sqlSequencesFileName,
		boolean autoNamespaceTables, String beanLocatorUtil, String propsUtil,
		String pluginName, String testDir, boolean build) {

		_tplBadColumnNames = _getTplProperty(
			"bad_column_names", _tplBadColumnNames);
		_tplBadTableNames = _getTplProperty(
			"bad_table_names", _tplBadTableNames);
		_tplEjbPk = _getTplProperty("ejb_pk", _tplEjbPk);
		_tplException = _getTplProperty("exception", _tplException);
		_tplExtendedModel = _getTplProperty(
			"extended_model", _tplExtendedModel);
		_tplExtendedModelImpl = _getTplProperty(
			"extended_model_impl", _tplExtendedModelImpl);
		_tplFinder = _getTplProperty("finder", _tplFinder);
		_tplFinderUtil = _getTplProperty("finder_util", _tplFinderUtil);
		_tplHbmXml = _getTplProperty("hbm_xml", _tplHbmXml);
		_tplJsonJs = _getTplProperty("json_js", _tplJsonJs);
		_tplJsonJsMethod = _getTplProperty("json_js_method", _tplJsonJsMethod);
		_tplModel = _getTplProperty("model", _tplModel);
		_tplModelClp = _getTplProperty("model", _tplModelClp);
		_tplModelHintsXml = _getTplProperty(
			"model_hints_xml", _tplModelHintsXml);
		_tplModelImpl = _getTplProperty("model_impl", _tplModelImpl);
		_tplModelSoap = _getTplProperty("model_soap", _tplModelSoap);
		_tplPersistence = _getTplProperty("persistence", _tplPersistence);
		_tplPersistenceImpl = _getTplProperty(
			"persistence_impl", _tplPersistenceImpl);
		_tplPersistenceUtil = _getTplProperty(
			"persistence_util", _tplPersistenceUtil);
		_tplProps = _getTplProperty("props", _tplProps);
		_tplRemotingXml = _getTplProperty("remoting_xml", _tplRemotingXml);
		_tplService = _getTplProperty("service", _tplService);
		_tplServiceBaseImpl = _getTplProperty(
			"service_base_impl", _tplServiceBaseImpl);
		_tplServiceClp = _getTplProperty("service_clp", _tplServiceClp);
		_tplServiceClpSerializer = _getTplProperty(
			"service_clp_serializer", _tplServiceClpSerializer);
		_tplServiceFactory = _getTplProperty(
			"service_factory", _tplServiceFactory);
		_tplServiceHttp = _getTplProperty("service_http", _tplServiceHttp);
		_tplServiceImpl = _getTplProperty("service_impl", _tplServiceImpl);
		_tplServiceJson = _getTplProperty("service_json", _tplServiceJson);
		_tplServiceJsonSerializer = _getTplProperty(
			"service_json_serializer", _tplServiceJsonSerializer);
		_tplServiceSoap = _getTplProperty("service_soap", _tplServiceSoap);
		_tplServiceUtil = _getTplProperty("service_util", _tplServiceUtil);
		_tplSpringBaseXml = _getTplProperty(
			"spring_base_xml", _tplSpringBaseXml);
		_tplSpringDynamicDataSourceXml = _getTplProperty(
			"spring_dynamic_data_source_xml", _tplSpringDynamicDataSourceXml);
		_tplSpringHibernateXml = _getTplProperty(
			"spring_hibernate_xml", _tplSpringHibernateXml);
		_tplSpringInfrastructureXml = _getTplProperty(
			"spring_infrastructure_xml", _tplSpringInfrastructureXml);
		_tplSpringXml = _getTplProperty("spring_xml", _tplSpringXml);

		try {
			_badTableNames = SetUtil.fromString(StringUtil.read(
				getClass().getClassLoader(), _tplBadTableNames));
			_badColumnNames = SetUtil.fromString(StringUtil.read(
				getClass().getClassLoader(), _tplBadColumnNames));
			_hbmFileName = hbmFileName;
			_modelHintsFileName = modelHintsFileName;
			_springFileName = springFileName;
			_springBaseFileName = springBaseFileName;
			_springDynamicDataSourceFileName = springDynamicDataSourceFileName;
			_springHibernateFileName = springHibernateFileName;
			_springInfrastructureFileName = springInfrastructureFileName;
			_apiDir = apiDir;
			_implDir = implDir;
			_jsonFileName = jsonFileName;
			_remotingFileName = remotingFileName;
			_sqlDir = sqlDir;
			_sqlFileName = sqlFileName;
			_sqlIndexesFileName = sqlIndexesFileName;
			_sqlIndexesPropertiesFileName = sqlIndexesPropertiesFileName;
			_sqlSequencesFileName = sqlSequencesFileName;
			_autoNamespaceTables = autoNamespaceTables;
			_beanLocatorUtil = beanLocatorUtil;
			_beanLocatorUtilShortName = _beanLocatorUtil.substring(
				_beanLocatorUtil.lastIndexOf(".") + 1);
			_propsUtil = propsUtil;
			_pluginName = GetterUtil.getString(pluginName);
			_testDir = testDir;

			Document doc = SAXReaderUtil.read(new File(fileName), true);

			Element root = doc.getRootElement();

			String packagePath = root.attributeValue("package-path");

			_outputPath =
				_implDir + "/" + StringUtil.replace(packagePath, ".", "/");

			_serviceOutputPath =
				_apiDir + "/" + StringUtil.replace(packagePath, ".", "/");

			if (Validator.isNotNull(_testDir)) {
				_testOutputPath =
					_testDir + "/" + StringUtil.replace(packagePath, ".", "/");
			}

			_packagePath = packagePath;

			Element author = root.element("author");

			if (author != null) {
				_author = author.getText();
			}
			else {
				_author = _AUTHOR;
			}

			Element portlet = root.element("portlet");
			Element namespace = root.element("namespace");

			if (portlet != null) {
				_portletName = portlet.attributeValue("name");

				_portletShortName = portlet.attributeValue("short-name");

				_portletPackageName =
					TextFormatter.format(_portletName, TextFormatter.B);

				_outputPath += "/" + _portletPackageName;

				_serviceOutputPath += "/" + _portletPackageName;

				_testOutputPath += "/" + _portletPackageName;

				_packagePath += "." + _portletPackageName;
			}
			else {
				_portletShortName = namespace.getText();
			}

			_portletShortName = _portletShortName.trim();

			if (!Validator.isChar(_portletShortName)) {
				throw new RuntimeException(
					"The namespace element must be a valid keyword");
			}

			_ejbList = new ArrayList<Entity>();
			_entityMappings = new HashMap<String, EntityMapping>();

			List<Element> entities = root.elements("entity");

			Iterator<Element> itr1 = entities.iterator();

			while (itr1.hasNext()) {
				Element entityEl = itr1.next();

				String ejbName = entityEl.attributeValue("name");

				String table = entityEl.attributeValue("table");

				if (Validator.isNull(table)) {
					table = ejbName;

					if (_badTableNames.contains(ejbName)) {
						table += StringPool.UNDERLINE;
					}

					if (_autoNamespaceTables) {
						table =
							_portletShortName + StringPool.UNDERLINE + ejbName;
					}
				}

				boolean uuid = GetterUtil.getBoolean(
					entityEl.attributeValue("uuid"), false);
				boolean localService = GetterUtil.getBoolean(
					entityEl.attributeValue("local-service"), false);
				boolean remoteService = GetterUtil.getBoolean(
					entityEl.attributeValue("remote-service"), true);
				String persistenceClass = GetterUtil.getString(
					entityEl.attributeValue("persistence-class"),
					_packagePath + ".service.persistence." + ejbName +
						"PersistenceImpl");

				String finderClass = "";

				if (FileUtil.exists(
					_outputPath + "/service/persistence/" + ejbName +
						"FinderImpl.java")) {

					finderClass =
						_packagePath + ".service.persistence." + ejbName +
							"FinderImpl";
				}

				String dataSource = entityEl.attributeValue("data-source");
				String sessionFactory = entityEl.attributeValue(
					"session-factory");
				String txManager = entityEl.attributeValue(
					"tx-manager");
				boolean cacheEnabled = GetterUtil.getBoolean(
					entityEl.attributeValue("cache-enabled"), true);

				List<EntityColumn> pkList = new ArrayList<EntityColumn>();
				List<EntityColumn> regularColList =
					new ArrayList<EntityColumn>();
				List<EntityColumn> collectionList =
					new ArrayList<EntityColumn>();
				List<EntityColumn> columnList = new ArrayList<EntityColumn>();

				List<Element> columns = entityEl.elements("column");

				if (uuid) {
					Element column = SAXReaderUtil.createElement("column");

					column.addAttribute("name", "uuid");
					column.addAttribute("type", "String");

					columns.add(0, column);
				}

				Iterator<Element> itr2 = columns.iterator();

				while (itr2.hasNext()) {
					Element column = itr2.next();

					String columnName = column.attributeValue("name");

					String columnDBName = column.attributeValue("db-name");

					if (Validator.isNull(columnDBName)) {
						columnDBName = columnName;

						if (_badColumnNames.contains(columnName)) {
							columnDBName += StringPool.UNDERLINE;
						}
					}

					String columnType = column.attributeValue("type");
					boolean primary = GetterUtil.getBoolean(
						column.attributeValue("primary"), false);
					String collectionEntity = column.attributeValue("entity");
					String mappingKey = column.attributeValue("mapping-key");

					String mappingTable = column.attributeValue(
						"mapping-table");

					if (Validator.isNotNull(mappingTable)) {
						if (_badTableNames.contains(mappingTable)) {
							mappingTable += StringPool.UNDERLINE;
						}

						if (_autoNamespaceTables) {
							mappingTable =
								_portletShortName + StringPool.UNDERLINE +
									mappingTable;
						}
					}

					String idType = column.attributeValue("id-type");
					String idParam = column.attributeValue("id-param");
					boolean convertNull = GetterUtil.getBoolean(
						column.attributeValue("convert-null"), true);

					EntityColumn col = new EntityColumn(
						columnName, columnDBName, columnType, primary,
						collectionEntity, mappingKey, mappingTable, idType,
						idParam, convertNull);

					if (primary) {
						pkList.add(col);
					}

					if (columnType.equals("Collection")) {
						collectionList.add(col);
					}
					else {
						regularColList.add(col);
					}

					columnList.add(col);

					if (Validator.isNotNull(collectionEntity) &&
						Validator.isNotNull(mappingTable)) {

						EntityMapping entityMapping = new EntityMapping(
							mappingTable, ejbName, collectionEntity);

						int ejbNameWeight = StringUtil.startsWithWeight(
							mappingTable, ejbName);
						int collectionEntityWeight =
							StringUtil.startsWithWeight(
								mappingTable, collectionEntity);

						if ((ejbNameWeight > collectionEntityWeight) ||
							((ejbNameWeight == collectionEntityWeight) &&
							 (ejbName.compareTo(collectionEntity) > 0))) {

							_entityMappings.put(mappingTable, entityMapping);
						}
					}
				}

				EntityOrder order = null;

				Element orderEl = entityEl.element("order");

				if (orderEl != null) {
					boolean asc = true;

					if ((orderEl.attribute("by") != null) &&
						(orderEl.attributeValue("by").equals("desc"))) {

						asc = false;
					}

					List<EntityColumn> orderColsList =
						new ArrayList<EntityColumn>();

					order = new EntityOrder(asc, orderColsList);

					List<Element> orderCols = orderEl.elements("order-column");

					Iterator<Element> itr3 = orderCols.iterator();

					while (itr3.hasNext()) {
						Element orderColEl = itr3.next();

						String orderColName =
							orderColEl.attributeValue("name");
						boolean orderColCaseSensitive = GetterUtil.getBoolean(
							orderColEl.attributeValue("case-sensitive"),
							true);

						boolean orderColByAscending = asc;

						String orderColBy = GetterUtil.getString(
							orderColEl.attributeValue("order-by"));

						if (orderColBy.equals("asc")) {
							orderColByAscending = true;
						}
						else if (orderColBy.equals("desc")) {
							orderColByAscending = false;
						}

						EntityColumn col = Entity.getColumn(
							orderColName, columnList);

						col = (EntityColumn)col.clone();

						col.setCaseSensitive(orderColCaseSensitive);
						col.setOrderByAscending(orderColByAscending);

						orderColsList.add(col);
					}
				}

				List<EntityFinder> finderList = new ArrayList<EntityFinder>();

				List<Element> finders = entityEl.elements("finder");

				if (uuid) {
					Element finderEl = SAXReaderUtil.createElement("finder");

					finderEl.addAttribute("name", "Uuid");
					finderEl.addAttribute("return-type", "Collection");

					Element finderColEl = finderEl.addElement("finder-column");

					finderColEl.addAttribute("name", "uuid");

					finders.add(0, finderEl);

					if (columnList.contains(new EntityColumn("groupId"))) {
						finderEl = SAXReaderUtil.createElement("finder");

						finderEl.addAttribute("name", "UUID_G");
						finderEl.addAttribute("return-type", ejbName);

						finderColEl = finderEl.addElement("finder-column");

						finderColEl.addAttribute("name", "uuid");

						finderColEl = finderEl.addElement("finder-column");

						finderColEl.addAttribute("name", "groupId");

						finders.add(1, finderEl);
					}
				}

				itr2 = finders.iterator();

				while (itr2.hasNext()) {
					Element finderEl = itr2.next();

					String finderName = finderEl.attributeValue("name");
					String finderReturn =
						finderEl.attributeValue("return-type");
					String finderWhere =
						finderEl.attributeValue("where");
					boolean finderDBIndex = GetterUtil.getBoolean(
						finderEl.attributeValue("db-index"), true);

					List<EntityColumn> finderColsList =
						new ArrayList<EntityColumn>();

					List<Element> finderCols = finderEl.elements(
						"finder-column");

					Iterator<Element> itr3 = finderCols.iterator();

					while (itr3.hasNext()) {
						Element finderColEl = itr3.next();

						String finderColName =
							finderColEl.attributeValue("name");

						boolean finderColCaseSensitive = GetterUtil.getBoolean(
							finderColEl.attributeValue("case-sensitive"),
							true);

						String finderColComparator = GetterUtil.getString(
							finderColEl.attributeValue("comparator"), "=");

						EntityColumn col = Entity.getColumn(
							finderColName, columnList);

						col = (EntityColumn)col.clone();

						col.setCaseSensitive(finderColCaseSensitive);
						col.setComparator(finderColComparator);

						finderColsList.add(col);
					}

					finderList.add(
						new EntityFinder(
							finderName, finderReturn, finderColsList,
							finderWhere, finderDBIndex));
				}

				List<Entity> referenceList = new ArrayList<Entity>();

				if (build) {
					List<Element> references = entityEl.elements("reference");

					itr2 = references.iterator();

					while (itr2.hasNext()) {
						Element reference = itr2.next();

						String refPackage =
							reference.attributeValue("package-path");
						String refEntity = reference.attributeValue("entity");

						referenceList.add(
							getEntity(refPackage + "." + refEntity));
					}
				}

				List<String> txRequiredList = new ArrayList<String>();

				itr2 = entityEl.elements("tx-required").iterator();

				while (itr2.hasNext()) {
					Element txRequiredEl = itr2.next();

					String txRequired = txRequiredEl.getText();

					txRequiredList.add(txRequired);
				}

				_ejbList.add(
					new Entity(
						_packagePath, _portletName, _portletShortName, ejbName,
						table, uuid, localService, remoteService,
						persistenceClass, finderClass, dataSource,
						sessionFactory, txManager, cacheEnabled, pkList,
						regularColList, collectionList, columnList, order,
						finderList, referenceList, txRequiredList));
			}

			List<String> exceptionList = new ArrayList<String>();

			if (root.element("exceptions") != null) {
				List<Element> exceptions =
					root.element("exceptions").elements("exception");

				itr1 = exceptions.iterator();

				while (itr1.hasNext()) {
					Element exception = itr1.next();

					exceptionList.add(exception.getText());
				}
			}

			if (build) {
				for (int x = 0; x < _ejbList.size(); x++) {
					Entity entity = _ejbList.get(x);

					System.out.println("Building " + entity.getName());

					if (true ||
						entity.getName().equals("EmailAddress") ||
						entity.getName().equals("User")) {

						if (entity.hasColumns()) {
							_createHbm(entity);
							_createHbmUtil(entity);

							_createPersistenceImpl(entity);
							_createPersistence(entity);
							_createPersistenceUtil(entity);

							if (Validator.isNotNull(_testDir)) {
								_createPersistenceTest(entity);
							}

							_createModelImpl(entity);
							_createExtendedModelImpl(entity);

							_createModel(entity);
							_createExtendedModel(entity);

							_createModelSoap(entity);

							_createModelClp(entity);

							_createPool(entity);

							if (entity.getPKList().size() > 1) {
								_createEJBPK(entity);
							}
						}

						_createFinder(entity);
						_createFinderUtil(entity);

						if (entity.hasLocalService()) {
							_createServiceBaseImpl(entity, _SESSION_TYPE_LOCAL);
							_createServiceImpl(entity, _SESSION_TYPE_LOCAL);
							_createService(entity, _SESSION_TYPE_LOCAL);
							_createServiceFactory(entity, _SESSION_TYPE_LOCAL);
							_createServiceUtil(entity, _SESSION_TYPE_LOCAL);

							_createServiceClp(entity, _SESSION_TYPE_LOCAL);
						}

						if (entity.hasRemoteService()) {
							_createServiceBaseImpl(
								entity, _SESSION_TYPE_REMOTE);
							_createServiceImpl(entity, _SESSION_TYPE_REMOTE);
							_createService(entity, _SESSION_TYPE_REMOTE);
							_createServiceFactory(entity, _SESSION_TYPE_REMOTE);
							_createServiceUtil(entity, _SESSION_TYPE_REMOTE);

							_createServiceClp(entity, _SESSION_TYPE_REMOTE);

							if (Validator.isNotNull(_jsonFileName)) {
								_createServiceHttp(entity);
								_createServiceJson(entity);

								if (entity.hasColumns()) {
									_createServiceJsonSerializer(entity);
								}

								_createServiceSoap(entity);
							}
						}
					}
				}

				_createHbmXml();
				_createModelHintsXml();
				_createSpringXml();

				_createServiceClpSerializer();

				if (Validator.isNotNull(_jsonFileName)) {
					_createJsonJs();
				}

				if (Validator.isNotNull(_remotingFileName)) {
					_createRemotingXml();
				}

				_createSQLIndexes();
				_createSQLTables();
				_createSQLSequences();

				_createExceptions(exceptionList);

				_createProps();
				_createSpringBaseXml();
				_createSpringDynamicDataSourceXml();
				_createSpringHibernateXml();
				_createSpringInfrastructureXml();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClassName(Type type) {
		int dimensions = type.getDimensions();
		String name = type.getValue();

		if (dimensions > 0) {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < dimensions; i++) {
				sb.append("[");
			}

			if (name.equals("boolean")) {
				return sb.toString() + "Z";
			}
			else if (name.equals("byte")) {
				return sb.toString() + "B";
			}
			else if (name.equals("char")) {
				return sb.toString() + "C";
			}
			else if (name.equals("double")) {
				return sb.toString() + "D";
			}
			else if (name.equals("float")) {
				return sb.toString() + "F";
			}
			else if (name.equals("int")) {
				return sb.toString() + "I";
			}
			else if (name.equals("long")) {
				return sb.toString() + "J";
			}
			else if (name.equals("short")) {
				return sb.toString() + "S";
			}
			else {
				return sb.toString() + "L" + name + ";";
			}
		}

		return name;
	}

	public String getCreateTableSQL(Entity entity) {
		String createTableSQL = _getCreateTableSQL(entity);

		createTableSQL = StringUtil.replace(createTableSQL, "\n", "");
		createTableSQL = StringUtil.replace(createTableSQL, "\t", "");
		createTableSQL = createTableSQL.substring(
			0, createTableSQL.length() - 1);

		return createTableSQL;
	}

	public String getDimensions(String dims) {
		return getDimensions(Integer.parseInt(dims));
	}

	public String getDimensions(int dims) {
		String dimensions = "";

		for (int i = 0; i < dims; i++) {
			dimensions += "[]";
		}

		return dimensions;
	}

	public Entity getEntity(String name) throws IOException {
		Entity entity = _entityPool.get(name);

		if (entity != null) {
			return entity;
		}

		int pos = name.lastIndexOf(".");

		if (pos == -1) {
			pos = _ejbList.indexOf(new Entity(name));

			entity = _ejbList.get(pos);

			_entityPool.put(name, entity);

			return entity;
		}
		else {
			String refPackage = name.substring(0, pos);
			String refPackageDir = StringUtil.replace(refPackage, ".", "/");
			String refEntity = name.substring(pos + 1, name.length());
			String refFileName =
				_implDir + "/" + refPackageDir + "/service.xml";

			File refFile = new File(refFileName);

			boolean useTempFile = false;

			if (!refFile.exists()) {
				refFileName = Time.getTimestamp();
				refFile = new File(refFileName);

				ClassLoader classLoader = getClass().getClassLoader();

				FileUtil.write(
					refFileName,
					StringUtil.read(
						classLoader, refPackageDir + "/service.xml"));

				useTempFile = true;
			}

			ServiceBuilder serviceBuilder = new ServiceBuilder(
				refFileName, _hbmFileName, _modelHintsFileName, _springFileName,
				_springBaseFileName, _springDynamicDataSourceFileName,
				_springHibernateFileName, _springInfrastructureFileName,
				_apiDir, _implDir, _jsonFileName, _remotingFileName, _sqlDir,
				_sqlFileName, _sqlIndexesFileName,
				_sqlIndexesPropertiesFileName, _sqlSequencesFileName,
				_autoNamespaceTables, _beanLocatorUtil, _propsUtil, _pluginName,
				_testDir, false);

			entity = serviceBuilder.getEntity(refEntity);

			entity.setPortalReference(useTempFile);

			_entityPool.put(name, entity);

			if (useTempFile) {
				refFile.deleteOnExit();
			}

			return entity;
		}
	}

	public Entity getEntityByGenericsName(String genericsName) {
		try {
			String name = genericsName.substring(1, genericsName.length() - 1);

			name = StringUtil.replace(name, ".model.", ".");

			return getEntity(name);
		}
		catch (Exception e) {
			return null;
		}
	}

	public Entity getEntityByParameterTypeValue(String parameterTypeValue) {
		try {
			String name = parameterTypeValue;

			name = StringUtil.replace(name, ".model.", ".");

			return getEntity(name);
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getGeneratorClass(String idType) {
		if (Validator.isNull(idType)) {
			idType = "assigned";
		}

		return idType;
	}

	public String getNoSuchEntityException(Entity entity) {
		String noSuchEntityException = entity.getName();

		if (Validator.isNull(entity.getPortletShortName()) ||
			noSuchEntityException.startsWith(entity.getPortletShortName())) {

			noSuchEntityException = noSuchEntityException.substring(
				entity.getPortletShortName().length());
		}

		noSuchEntityException = "NoSuch" + noSuchEntityException;

		return noSuchEntityException;
	}

	public String getPrimitiveObj(String type) {
		if (type.equals("boolean")) {
			return "Boolean";
		}
		else if (type.equals("double")) {
			return "Double";
		}
		else if (type.equals("float")) {
			return "Float";
		}
		else if (type.equals("int")) {
			return "Integer";
		}
		else if (type.equals("long")) {
			return "Long";
		}
		else if (type.equals("short")) {
			return "Short";
		}
		else {
			return type;
		}
	}

	public String getPrimitiveObjValue(String colType) {
		if (colType.equals("Boolean")) {
			return ".booleanValue()";
		}
		else if (colType.equals("Double")) {
			return ".doubleValue()";
		}
		else if (colType.equals("Float")) {
			return ".floatValue()";
		}
		else if (colType.equals("Integer")) {
			return ".intValue()";
		}
		else if (colType.equals("Long")) {
			return ".longValue()";
		}
		else if (colType.equals("Short")) {
			return ".shortValue()";
		}

		return StringPool.BLANK;
	}

	public String getSqlType(String model, String field, String type) {
		if (type.equals("boolean") || type.equals("Boolean")) {
			return "BOOLEAN";
		}
		else if (type.equals("double") || type.equals("Double")) {
			return "DOUBLE";
		}
		else if (type.equals("float") || type.equals("Float")) {
			return "FLOAT";
		}
		else if (type.equals("int") || type.equals("Integer")) {
			return "INTEGER";
		}
		else if (type.equals("long") || type.equals("Long")) {
			return "BIGINT";
		}
		else if (type.equals("short") || type.equals("Short")) {
			return "INTEGER";
		}
		else if (type.equals("Date")) {
			return "TIMESTAMP";
		}
		else if (type.equals("String")) {
			Map<String, String> hints = ModelHintsUtil.getHints(model, field);

			if (hints != null) {
				int maxLength = GetterUtil.getInteger(hints.get("max-length"));

				if (maxLength == 2000000) {
					return "CLOB";
				}
			}

			return "VARCHAR";
		}
		else {
			return null;
		}
	}

	public boolean hasEntityByGenericsName(String genericsName) {
		if (Validator.isNull(genericsName)) {
			return false;
		}

		if (genericsName.indexOf(".model.") == -1) {
			return false;
		}

		if (getEntityByGenericsName(genericsName) == null) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean hasEntityByParameterTypeValue(String parameterTypeValue) {
		if (Validator.isNull(parameterTypeValue)) {
			return false;
		}

		if (parameterTypeValue.indexOf(".model.") == -1) {
			return false;
		}

		if (getEntityByParameterTypeValue(parameterTypeValue) == null) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean isCustomMethod(JavaMethod method) {
		String methodName = method.getName();

		if (methodName.equals("afterPropertiesSet") ||
			methodName.equals("equals") ||
			methodName.equals("getClass") ||
			methodName.equals("hashCode") ||
			methodName.equals("notify") ||
			methodName.equals("notifyAll") ||
			methodName.equals("toString") ||
			methodName.equals("wait")) {

			return false;
		}
		else if (methodName.equals("getPermissionChecker")) {
			return false;
		}
		else if (methodName.equals("getUser") &&
				 method.getParameters().length == 0) {

			return false;
		}
		else if (methodName.equals("getUserId") &&
				 method.getParameters().length == 0) {

			return false;
		}
		else if ((methodName.endsWith("Finder")) &&
				 (methodName.startsWith("get") ||
				  methodName.startsWith("set"))) {

			return false;
		}
		else if ((methodName.endsWith("Persistence")) &&
				 (methodName.startsWith("get") ||
				  methodName.startsWith("set"))) {

			return false;
		}
		else if ((methodName.endsWith("Service")) &&
				 (methodName.startsWith("get") ||
				  methodName.startsWith("set"))) {

			return false;
		}
		else {
			return true;
		}
	}

	public boolean isDuplicateMethod(
		JavaMethod method, Map<String, Object> tempMap) {

		StringBuilder sb = new StringBuilder();

		sb.append("isDuplicateMethod ");
		sb.append(method.getReturns().getValue());
		sb.append(method.getReturnsGenericsName());
		sb.append(getDimensions(method.getReturns().getDimensions()));
		sb.append(StringPool.SPACE);
		sb.append(method.getName());
		sb.append(StringPool.OPEN_PARENTHESIS);

		JavaParameter[] parameters = method.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			JavaParameter javaParameter = parameters[i];

			sb.append(javaParameter.getType().getValue());
			sb.append(javaParameter.getGenericsName());
			sb.append(getDimensions(javaParameter.getType().getDimensions()));

			if ((i + 1) != parameters.length) {
				sb.append(StringPool.COMMA);
			}
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		String key = sb.toString();

		if (tempMap.containsKey(key)) {
			return true;
		}
		else {
			tempMap.put(key, key);

			return false;
		}
	}

	public boolean isPersistenceReadOnlyMethod(JavaMethod method) {
		return isReadOnlyMethod(
			method, null,
			PropsValues.SERVICE_BUILDER_PERSISTENCE_READ_ONLY_PREFIXES);
	}

	public boolean isServiceReadOnlyMethod(
		JavaMethod method, List<String> txRequiredList) {

		return isReadOnlyMethod(
			method, txRequiredList,
			PropsValues.SERVICE_BUILDER_SERVICE_READ_ONLY_PREFIXES);
	}

	public boolean isReadOnlyMethod(
		JavaMethod method, List<String> txRequiredList, String[] prefixes) {

		String methodName = method.getName();

		if (isTxRequiredMethod(method, txRequiredList)) {
			return false;
		}

		for (String prefix : prefixes) {
			if (methodName.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	public boolean isSoapMethod(JavaMethod method) {
		String returnValueName = method.getReturns().getValue();

		if (returnValueName.startsWith("java.io") ||
			returnValueName.equals("java.util.Map") ||
			returnValueName.equals("java.util.Properties") ||
			returnValueName.startsWith("javax")) {

			return false;
		}

		JavaParameter[] parameters = method.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			JavaParameter javaParameter = parameters[i];

			String parameterTypeName =
				javaParameter.getType().getValue() +
					_getDimensions(javaParameter.getType());

			if (parameterTypeName.equals(
					"com.liferay.portal.theme.ThemeDisplay") ||
				parameterTypeName.equals(
					"com.liferay.portlet.PortletPreferencesImpl") ||
				parameterTypeName.startsWith("java.io") ||
				//parameterTypeName.startsWith("java.util.List") ||
				//parameterTypeName.startsWith("java.util.Locale") ||
				parameterTypeName.startsWith("java.util.Map") ||
				parameterTypeName.startsWith("java.util.Properties") ||
				parameterTypeName.startsWith("javax")) {

				return false;
			}
		}

		return true;
	}

	public boolean isTxRequiredMethod(
		JavaMethod method, List<String> txRequiredList) {

		if (txRequiredList == null) {
			return false;
		}

		String methodName = method.getName();

		for (String txRequired : txRequiredList) {
			if (methodName.equals(txRequired)) {
				return true;
			}
		}

		return false;
	}

	private static String _getPackagePath(File file) {
		String fileName = StringUtil.replace(file.toString(), "\\", "/");

		int x = fileName.indexOf("src/");

		if (x == -1) {
			x = fileName.indexOf("test/");
		}

		int y = fileName.lastIndexOf("/");

		fileName = fileName.substring(x + 4, y);

		return StringUtil.replace(fileName, "/", ".");
	}

	private void _createEJBPK(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplEjbPk, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/persistence/" +
				entity.getPKClassName() + ".java");

		writeFile(ejbFile, content, _author);
	}

	private void _createExceptions(List<String> exceptions) throws Exception {
		for (int i = 0; i < _ejbList.size(); i++) {
			Entity entity = _ejbList.get(i);

			if (entity.hasColumns()) {
				exceptions.add(getNoSuchEntityException(entity));
			}
		}

		for (String exception : exceptions) {
			File exceptionFile = new File(
				_serviceOutputPath + "/" + exception + "Exception.java");

			if (!exceptionFile.exists()) {
				Map<String, Object> context = _getContext();

				context.put("exception", exception);

				String content = _processTemplate(_tplException, context);

				FileUtil.write(exceptionFile, content);
			}

			if (!_serviceOutputPath.equals(_outputPath)) {
				exceptionFile = new File(
					_outputPath + "/" + exception + "Exception.java");

				if (exceptionFile.exists()) {
					System.out.println("Relocating " + exceptionFile);

					exceptionFile.delete();
				}
			}
		}
	}

	private void _createExtendedModel(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/model/impl/" + entity.getName() + "Impl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplExtendedModel, context);

		// Write file

		File modelFile = new File(
			_serviceOutputPath + "/model/" + entity.getName() + ".java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(modelFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			modelFile = new File(
				_outputPath + "/model/" + entity.getName() + ".java");

			if (modelFile.exists()) {
				System.out.println("Relocating " + modelFile);

				modelFile.delete();
			}
		}
	}

	private void _createExtendedModelImpl(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplExtendedModelImpl, context);

		// Write file

		File modelFile = new File(
			_outputPath + "/model/impl/" + entity.getName() + "Impl.java");

		if (!modelFile.exists()) {
			writeFile(modelFile, content, _author);
		}
	}

	private void _createFinder(Entity entity) throws Exception {
		if (!entity.hasFinderClass()) {
			return;
		}

		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/persistence/" + entity.getName() +
				"FinderImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplFinder, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/persistence/" + entity.getName() +
				"Finder.java");

		writeFile(ejbFile, content, _author);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/persistence/" + entity.getName() +
					"Finder.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createFinderUtil(Entity entity) throws Exception {
		if (!entity.hasFinderClass()) {
			return;
		}

		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/persistence/" + entity.getName() +
				"FinderImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplFinderUtil, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/persistence/" + entity.getName() +
				"FinderUtil.java");

		writeFile(ejbFile, content, _author);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/persistence/" + entity.getName() +
					"FinderUtil.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createHbm(Entity entity) {
		File ejbFile = new File(
			_outputPath + "/service/persistence/" + entity.getName() +
				"HBM.java");

		if (ejbFile.exists()) {
			System.out.println("Removing deprecated " + ejbFile);

			ejbFile.delete();
		}
	}

	private void _createHbmUtil(Entity entity) {
		File ejbFile = new File(
			_outputPath + "/service/persistence/" + entity.getName() +
				"HBMUtil.java");

		if (ejbFile.exists()) {
			System.out.println("Removing deprecated " + ejbFile);

			ejbFile.delete();
		}
	}

	private void _createHbmXml() throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entities", _ejbList);

		// Content

		String content = _processTemplate(_tplHbmXml, context);

		File xmlFile = new File(_hbmFileName);

		if (!xmlFile.exists()) {
			String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n" +
				"\n" +
				"<hibernate-mapping default-lazy=\"false\" auto-import=\"false\">\n" +
				"</hibernate-mapping>";

			FileUtil.write(xmlFile, xml);
		}

		String oldContent = FileUtil.read(xmlFile);
		String newContent = _fixHbmXml(oldContent);

		int firstClass = newContent.indexOf(
			"<class name=\"" + _packagePath + ".model.impl.");
		int lastClass = newContent.lastIndexOf(
			"<class name=\"" + _packagePath + ".model.impl.");

		if (firstClass == -1) {
			int x = newContent.indexOf("</hibernate-mapping>");

			if (x != -1) {
				newContent =
					newContent.substring(0, x) + content +
					newContent.substring(x, newContent.length());
			}
		}
		else {
			firstClass = newContent.lastIndexOf("<class", firstClass) - 1;
			lastClass = newContent.indexOf("</class>", lastClass) + 9;

			newContent =
				newContent.substring(0, firstClass) + content +
					newContent.substring(lastClass, newContent.length());
		}

		newContent = _formatXml(newContent);

		if (!oldContent.equals(newContent)) {
			FileUtil.write(xmlFile, newContent);
		}
	}

	private void _createJsonJs() throws Exception {
		StringBuilder content = new StringBuilder();

		if (_ejbList.size() > 0) {
			content.append(_processTemplate(_tplJsonJs));
		}

		for (int i = 0; i < _ejbList.size(); i++) {
			Entity entity = _ejbList.get(i);

			if (entity.hasRemoteService()) {
				JavaClass javaClass = _getJavaClass(
					_outputPath + "/service/http/" + entity.getName() +
						"ServiceJSON.java");

				JavaMethod[] methods = _getMethods(javaClass);

				Set<String> jsonMethods = new LinkedHashSet<String>();

				for (int j = 0; j < methods.length; j++) {
					JavaMethod javaMethod = methods[j];

					String methodName = javaMethod.getName();

					if (javaMethod.isPublic()) {
						jsonMethods.add(methodName);
					}
				}

				if (jsonMethods.size() > 0) {
					Map<String, Object> context = _getContext();

					context.put("entity", entity);
					context.put("methods", jsonMethods);

					content.append("\n\n");
					content.append(_processTemplate(_tplJsonJsMethod, context));
				}
			}
		}

		File jsonFile = new File(_jsonFileName);

		if (!jsonFile.exists()) {
			FileUtil.write(jsonFile, "");
		}

		String oldContent = FileUtil.read(jsonFile);
		String newContent = new String(oldContent);

		int oldBegin = oldContent.indexOf(
			"Liferay.Service." + _portletShortName);

		int oldEnd = oldContent.lastIndexOf(
			"Liferay.Service." + _portletShortName);

		oldEnd = oldContent.indexOf("};", oldEnd);

		int newBegin = newContent.indexOf(
			"Liferay.Service." + _portletShortName);

		int newEnd = newContent.lastIndexOf(
			"Liferay.Service." + _portletShortName);

		newEnd = newContent.indexOf("};", newEnd);

		if (newBegin == -1) {
			newContent = oldContent + "\n\n" + content.toString().trim();
		}
		else {
			newContent =
				newContent.substring(0, oldBegin) + content.toString().trim() +
					newContent.substring(oldEnd + 2, newContent.length());
		}

		if (!oldContent.equals(newContent)) {
			FileUtil.write(jsonFile, newContent);
		}
	}

	private void _createModel(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplModel, context);

		// Write file

		File modelFile = new File(
			_serviceOutputPath + "/model/" + entity.getName() + "Model.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(modelFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			modelFile = new File(
				_outputPath + "/model/" + entity.getName() + "Model.java");

			if (modelFile.exists()) {
				System.out.println("Relocating " + modelFile);

				modelFile.delete();
			}
		}
	}

	private void _createModelClp(Entity entity) throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		JavaClass javaClass = _getJavaClass(
			_outputPath + "/model/impl/" + entity.getName() + "Impl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplModelClp, context);

		// Write file

		File modelFile = new File(
			_serviceOutputPath + "/model/" + entity.getName() + "Clp.java");

		writeFile(modelFile, content, _author);
	}

	private void _createModelHintsXml() throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entities", _ejbList);

		// Content

		String content = _processTemplate(_tplModelHintsXml, context);

		File xmlFile = new File(_modelHintsFileName);

		if (!xmlFile.exists()) {
			String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"\n" +
				"<model-hints>\n" +
				"</model-hints>";

			FileUtil.write(xmlFile, xml);
		}

		String oldContent = FileUtil.read(xmlFile);
		String newContent = new String(oldContent);

		int firstModel = newContent.indexOf(
			"<model name=\"" + _packagePath + ".model.");
		int lastModel = newContent.lastIndexOf(
			"<model name=\"" + _packagePath + ".model.");

		if (firstModel == -1) {
			int x = newContent.indexOf("</model-hints>");

			newContent =
				newContent.substring(0, x) + content +
					newContent.substring(x, newContent.length());
		}
		else {
			firstModel = newContent.lastIndexOf("<model", firstModel) - 1;
			lastModel = newContent.indexOf("</model>", lastModel) + 9;

			newContent =
				newContent.substring(0, firstModel) + content +
				newContent.substring(lastModel, newContent.length());
		}

		newContent = _formatXml(newContent);

		if (!oldContent.equals(newContent)) {
			FileUtil.write(xmlFile, newContent);
		}
	}

	private void _createModelImpl(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplModelImpl, context);

		// Write file

		File modelFile = new File(
			_outputPath + "/model/impl/" + entity.getName() + "ModelImpl.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(modelFile, content, _author, jalopySettings);
	}

	private void _createModelSoap(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplModelSoap, context);

		// Write file

		File modelFile = new File(
			_serviceOutputPath + "/model/" + entity.getName() + "Soap.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(modelFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			modelFile = new File(
				_outputPath + "/model/" + entity.getName() + "Soap.java");

			if (modelFile.exists()) {
				System.out.println("Relocating " + modelFile);

				modelFile.delete();
			}
		}
	}

	private void _createPersistence(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/persistence/" + entity.getName() +
				"PersistenceImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplPersistence, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/persistence/" + entity.getName() +
				"Persistence.java");

		writeFile(ejbFile, content, _author);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/persistence/" + entity.getName() +
					"Persistence.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createPersistenceImpl(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put(
			"referenceList", _mergeReferenceList(entity.getReferenceList()));

		// Content

		String content = _processTemplate(_tplPersistenceImpl, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/persistence/" + entity.getName() +
				"PersistenceImpl.java");

		writeFile(ejbFile, content, _author);
	}

	private void _createPersistenceTest(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplPersistenceTest, context);

		// Write file

		File ejbFile = new File(
			_testOutputPath + "/service/persistence/" + entity.getName() +
				"PersistenceTest.java");

		writeFile(ejbFile, content, _author);
	}

	private void _createPersistenceUtil(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/persistence/" + entity.getName() +
				"PersistenceImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplPersistenceUtil, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/persistence/" + entity.getName() +
				"Util.java");

		writeFile(ejbFile, content, _author);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/persistence/" + entity.getName() +
					"Util.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createPool(Entity entity) {
		File ejbFile = new File(
			_outputPath + "/service/persistence/" + entity.getName() +
				"Pool.java");

		if (ejbFile.exists()) {
			System.out.println("Removing deprecated " + ejbFile);

			ejbFile.delete();
		}
	}

	private void _createProps() throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		// Content

		File propsFile = new File(_implDir + "/service.properties");

		long buildNumber = 1;

		if (propsFile.exists()) {
			Properties props = PropertiesUtil.load(FileUtil.read(propsFile));

			buildNumber = GetterUtil.getLong(
				props.getProperty("build.number")) + 1;
		}

		Map<String, Object> context = _getContext();

		context.put("buildNumber", new Long(buildNumber));
		context.put("currentTimeMillis", new Long(System.currentTimeMillis()));

		String content = _processTemplate(_tplProps, context);

		// Write file

		FileUtil.write(propsFile, content, true);
	}

	private void _createRemotingXml() throws Exception {
		StringBuilder sb = new StringBuilder();

		Document doc = SAXReaderUtil.read(new File(_springFileName), true);

		Iterator<Element> itr = doc.getRootElement().elements(
			"bean").iterator();

		while (itr.hasNext()) {
			Element beanEl = itr.next();

			String beanId = beanEl.attributeValue("id");

			if (beanId.endsWith("ServiceFactory") &&
				!beanId.endsWith("LocalServiceFactory")) {

				String entityName = beanId;

				entityName = StringUtil.replace(entityName, ".service.", ".");

				int pos = entityName.indexOf("LocalServiceFactory");

				if (pos == -1) {
					pos = entityName.indexOf("ServiceFactory");
				}

				entityName = entityName.substring(0, pos);

				Entity entity = getEntity(entityName);

				String serviceName = beanId.substring(0, beanId.length() - 7);

				String serviceMapping = serviceName;

				serviceMapping = StringUtil.replace(
					serviceMapping, ".service.", ".service.spring.");
				serviceMapping = StringUtil.replace(
					serviceMapping, StringPool.PERIOD, StringPool.UNDERLINE);

				Map<String, Object> context = _getContext();

				context.put("entity", entity);
				context.put("serviceName", serviceName);
				context.put("serviceMapping", serviceMapping);

				sb.append(_processTemplate(_tplRemotingXml, context));
			}
		}

		File outputFile = new File(_remotingFileName);

		if (!outputFile.exists()) {
			return;
		}

		String content = FileUtil.read(outputFile);
		String newContent = content;

		int x = content.indexOf("<bean ");
		int y = content.lastIndexOf("</bean>") + 8;

		if (x != -1) {
			newContent =
				content.substring(0, x - 1) + sb.toString() +
					content.substring(y, content.length());
		}
		else {
			x = content.indexOf("</beans>");

			if (x != -1) {
				newContent =
					content.substring(0, x) + sb.toString() +
						content.substring(x, content.length());
			}
			else {
				x = content.indexOf("<beans/>");
				y = x + 8;

				newContent =
					content.substring(0, x) + "<beans>" + sb.toString() +
						"</beans>" + content.substring(y, content.length());
			}
		}

		newContent = _formatXml(newContent);

		if (!content.equals(newContent)) {
			FileUtil.write(outputFile, newContent);

			System.out.println(outputFile.toString());
		}
	}

	private void _createService(Entity entity, int sessionType)
		throws Exception {

		String serviceComments = "This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.";

		JavaClass javaClass = _getJavaClass(_outputPath + "/service/impl/" + entity.getName() + (sessionType != _SESSION_TYPE_REMOTE ? "Local" : "") + "ServiceImpl.java");

		JavaMethod[] methods = _getMethods(javaClass);

		if (sessionType == _SESSION_TYPE_LOCAL) {
			if (javaClass.getSuperClass().getValue().endsWith(
					entity.getName() + "LocalServiceBaseImpl")) {

				JavaClass parentJavaClass = _getJavaClass(
					_outputPath + "/service/base/" + entity.getName() +
						"LocalServiceBaseImpl.java");

				JavaMethod[] parentMethods = parentJavaClass.getMethods();

				JavaMethod[] allMethods = new JavaMethod[parentMethods.length + methods.length];

				ArrayUtil.combine(parentMethods, methods, allMethods);

				methods = allMethods;
			}

			serviceComments = "This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.";
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", methods);
		context.put("sessionTypeName",_getSessionTypeName(sessionType));
		context.put("serviceComments", serviceComments);

		// Content

		String content = _processTemplate(_tplService, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "Service.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/" + entity.getName() +
					_getSessionTypeName(sessionType) + "Service.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createServiceBaseImpl(Entity entity, int sessionType)
		throws Exception {

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("sessionTypeName",_getSessionTypeName(sessionType));
		context.put(
			"referenceList", _mergeReferenceList(entity.getReferenceList()));

		// Content

		String content = _processTemplate(_tplServiceBaseImpl, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/base/" + entity.getName() +
				_getSessionTypeName(sessionType) + "ServiceBaseImpl.java");

		writeFile(ejbFile, content, _author);
	}

	private void _createServiceClp(Entity entity, int sessionType)
		throws Exception {

		if (Validator.isNull(_pluginName)) {
			return;
		}

		JavaClass javaClass = _getJavaClass(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "Service.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		// Content

		String content = _processTemplate(_tplServiceClp, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "ServiceClp.java");

		writeFile(ejbFile, content, _author);
	}

	private void _createServiceClpSerializer() throws Exception {
		if (Validator.isNull(_pluginName)) {
			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entities", _ejbList);

		// Content

		String content = _processTemplate(_tplServiceClpSerializer, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/ClpSerializer.java");

		writeFile(ejbFile, content);
	}

	private void _createServiceFactory(Entity entity, int sessionType)
		throws Exception {

		if (Validator.isNotNull(_pluginName)) {
			FileUtil.delete(
				_serviceOutputPath + "/service/" + entity.getName() +
					_getSessionTypeName(sessionType) + "ServiceFactory.java");

			FileUtil.delete(
				_outputPath + "/service/" + entity.getName() +
					_getSessionTypeName(sessionType) + "ServiceFactory.java");

			return;
		}

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		// Content

		String content = _processTemplate(_tplServiceFactory, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "ServiceFactory.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/" + entity.getName() +
					_getSessionTypeName(sessionType) + "ServiceFactory.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createServiceHttp(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/impl/" + entity.getName() +
				"ServiceImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));
		context.put("hasHttpMethods", new Boolean(_hasHttpMethods(javaClass)));

		// Content

		String content = _processTemplate(_tplServiceHttp, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/http/" + entity.getName() +
				"ServiceHttp.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);
	}

	private void _createServiceImpl(Entity entity, int sessionType)
		throws Exception {

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		// Content

		String content = _processTemplate(_tplServiceImpl, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/impl/" + entity.getName() +
				_getSessionTypeName(sessionType) + "ServiceImpl.java");

		if (!ejbFile.exists()) {
			writeFile(ejbFile, content, _author);
		}
	}

	private void _createServiceJson(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/impl/" + entity.getName() +
				"ServiceImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplServiceJson, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/http/" + entity.getName() +
				"ServiceJSON.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);
	}

	private void _createServiceJsonSerializer(Entity entity) throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entity", entity);

		// Content

		String content = _processTemplate(_tplServiceJsonSerializer, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/http/" + entity.getName() +
				"JSONSerializer.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);
	}

	private void _createServiceSoap(Entity entity) throws Exception {
		JavaClass javaClass = _getJavaClass(
			_outputPath + "/service/impl/" + entity.getName() +
				"ServiceImpl.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));

		// Content

		String content = _processTemplate(_tplServiceSoap, context);

		// Write file

		File ejbFile = new File(
			_outputPath + "/service/http/" + entity.getName() +
				"ServiceSoap.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);
	}

	private void _createServiceUtil(Entity entity, int sessionType)
		throws Exception {

		JavaClass javaClass = _getJavaClass(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "Service.java");

		Map<String, Object> context = _getContext();

		context.put("entity", entity);
		context.put("methods", _getMethods(javaClass));
		context.put("sessionTypeName", _getSessionTypeName(sessionType));

		// Content

		String content = _processTemplate(_tplServiceUtil, context);

		// Write file

		File ejbFile = new File(
			_serviceOutputPath + "/service/" + entity.getName() +
				_getSessionTypeName(sessionType) + "ServiceUtil.java");

		Map<String, Object> jalopySettings = new HashMap<String, Object>();

		jalopySettings.put("keepJavadoc", Boolean.TRUE);

		writeFile(ejbFile, content, _author, jalopySettings);

		if (!_serviceOutputPath.equals(_outputPath)) {
			ejbFile = new File(
				_outputPath + "/service/" + entity.getName() +
					_getSessionTypeName(sessionType) + "ServiceUtil.java");

			if (ejbFile.exists()) {
				System.out.println("Relocating " + ejbFile);

				ejbFile.delete();
			}
		}
	}

	private void _createSpringBaseXml() throws Exception {
		if (Validator.isNull(_springBaseFileName)) {
			return;
		}

		// Content

		String content = _processTemplate(_tplSpringBaseXml);

		// Write file

		File ejbFile = new File(_springBaseFileName);

		FileUtil.write(ejbFile, content, true);

		if (Validator.isNotNull(_pluginName)) {
			FileUtil.delete(
				"docroot/WEB-INF/src/META-INF/data-source-spring.xml");
			FileUtil.delete("docroot/WEB-INF/src/META-INF/misc-spring.xml");
		}
	}

	private void _createSpringDynamicDataSourceXml() throws Exception {
		if (Validator.isNull(_springDynamicDataSourceFileName)) {
			return;
		}

		// Content

		String content = _processTemplate(_tplSpringDynamicDataSourceXml);

		// Write file

		File ejbFile = new File(_springDynamicDataSourceFileName);

		FileUtil.write(ejbFile, content, true);
	}

	private void _createSpringHibernateXml() throws Exception {
		if (Validator.isNull(_springHibernateFileName)) {
			return;
		}

		// Content

		String content = _processTemplate(_tplSpringHibernateXml);

		// Write file

		File ejbFile = new File(_springHibernateFileName);

		FileUtil.write(ejbFile, content, true);
	}

	private void _createSpringInfrastructureXml() throws Exception {
		if (Validator.isNull(_springInfrastructureFileName)) {
			return;
		}

		// Content

		String content = _processTemplate(_tplSpringInfrastructureXml);

		// Write file

		File ejbFile = new File(_springInfrastructureFileName);

		FileUtil.write(ejbFile, content, true);
	}

	private void _createSpringXml() throws Exception {
		Map<String, Object> context = _getContext();

		context.put("entities", _ejbList);

		// Content

		String content = _processTemplate(_tplSpringXml, context);

		File xmlFile = new File(_springFileName);

		if (!xmlFile.exists()) {
			String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"\n" +
				"<beans\n" +
				"\tdefault-init-method=\"afterPropertiesSet\"\n" +
				"\txmlns=\"http://www.springframework.org/schema/beans\"\n" +
				"\txmlns:aop=\"http://www.springframework.org/schema/aop\"\n" +
				"\txmlns:tx=\"http://www.springframework.org/schema/tx\"\n" +
				"\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"\txsi:schemaLocation=\"http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd\"\n" +
				">\n" +
				"</beans>";

			FileUtil.write(xmlFile, xml);
		}

		String oldContent = FileUtil.read(xmlFile);
		String newContent = _fixSpringXml(oldContent);

		int x = oldContent.indexOf("<beans");
		int y = oldContent.lastIndexOf("</beans>");

		int firstSession = newContent.indexOf(
			"<bean id=\"" + _packagePath + ".service.", x);

		int lastSession = newContent.lastIndexOf(
			"<bean id=\"" + _packagePath + ".service.", y);

		if ((firstSession == -1) || (firstSession > y)) {
			x = newContent.indexOf("</beans>");

			newContent =
				newContent.substring(0, x) + content +
				newContent.substring(x, newContent.length());
		}
		else {
			firstSession = newContent.lastIndexOf("<bean", firstSession) - 1;
			lastSession = newContent.indexOf("</bean>", lastSession) + 8;

			newContent =
				newContent.substring(0, firstSession) + content +
				newContent.substring(lastSession, newContent.length());
		}

		newContent = _formatXml(newContent);

		if (!oldContent.equals(newContent)) {
			FileUtil.write(xmlFile, newContent);
		}
	}

	private void _createSQLIndexes() throws IOException {
		if (!FileUtil.exists(_sqlDir)) {
			return;
		}

		// indexes.sql

		File sqlFile = new File(_sqlDir + "/" + _sqlIndexesFileName);

		if (!sqlFile.exists()) {
			FileUtil.write(sqlFile, "");
		}

		Map<String, String> indexSQLs = new TreeMap<String, String>();

		BufferedReader br = new BufferedReader(new FileReader(sqlFile));

		while (true) {
			String indexSQL = br.readLine();

			if (indexSQL == null) {
				break;
			}

			if (Validator.isNotNull(indexSQL.trim())) {
				int pos = indexSQL.indexOf(" on ");

				String indexSpec = indexSQL.substring(pos + 4);

				indexSQLs.put(indexSpec, indexSQL);
			}
		}

		br.close();

		// indexes.properties

		File propsFile = new File(
			_sqlDir + "/" + _sqlIndexesPropertiesFileName);

		if (!propsFile.exists()) {
			FileUtil.write(propsFile, "");
		}

		Map<String, String> indexProps = new TreeMap<String, String>();

		br = new BufferedReader(new FileReader(propsFile));

		while (true) {
			String indexMapping = br.readLine();

			if (indexMapping == null) {
				break;
			}

			if (Validator.isNotNull(indexMapping.trim())) {
				String[] splitIndexMapping = indexMapping.split("\\=");

				indexProps.put(splitIndexMapping[1], splitIndexMapping[0]);
			}
		}

		br.close();

		// indexes.sql

		for (int i = 0; i < _ejbList.size(); i++) {
			Entity entity = _ejbList.get(i);

			if (!entity.isDefaultDataSource()) {
				continue;
			}

			List<EntityFinder> finderList = entity.getFinderList();

			for (int j = 0; j < finderList.size(); j++) {
				EntityFinder finder = finderList.get(j);

				if (finder.isDBIndex()) {
					StringBuilder sb = new StringBuilder();

					sb.append(entity.getTable() + " (");

					List<EntityColumn> finderColsList = finder.getColumns();

					for (int k = 0; k < finderColsList.size(); k++) {
						EntityColumn col = finderColsList.get(k);

						sb.append(col.getDBName());

						if ((k + 1) != finderColsList.size()) {
							sb.append(", ");
						}
					}

					sb.append(");");

					String indexSpec = sb.toString();

					String indexHash =
						Integer.toHexString(indexSpec.hashCode()).toUpperCase();

					String indexName = "IX_" + indexHash;

					sb = new StringBuilder();

					sb.append("create index " + indexName + " on ");
					sb.append(indexSpec);

					indexSQLs.put(indexSpec, sb.toString());

					String finderName =
						entity.getTable() + StringPool.PERIOD +
							finder.getName();

					indexProps.put(finderName, indexName);
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		Iterator<String> itr = indexSQLs.values().iterator();

		String prevEntityName = null;

		while (itr.hasNext()) {
			String indexSQL = itr.next();

			String entityName = indexSQL.split(" ")[4];

			if ((prevEntityName != null) &&
				(!prevEntityName.equals(entityName))) {

				sb.append("\n");
			}

			sb.append(indexSQL);

			if (itr.hasNext()) {
				sb.append("\n");
			}

			prevEntityName = entityName;
		}

		FileUtil.write(sqlFile, sb.toString(), true);

		// indexes.properties

		sb = new StringBuilder();

		itr = indexProps.keySet().iterator();

		prevEntityName = null;

		while (itr.hasNext()) {
			String finderName = itr.next();

			String indexName = indexProps.get(finderName);

			String entityName = finderName.split("\\.")[0];

			if ((prevEntityName != null) &&
				(!prevEntityName.equals(entityName))) {

				sb.append("\n");
			}

			sb.append(indexName + StringPool.EQUAL + finderName);

			if (itr.hasNext()) {
				sb.append("\n");
			}

			prevEntityName = entityName;
		}

		FileUtil.write(propsFile, sb.toString(), true);
	}

	private void _createSQLMappingTables(
			File sqlFile, String newCreateTableString,
			EntityMapping entityMapping, boolean addMissingTables)
		throws IOException {

		if (!sqlFile.exists()) {
			FileUtil.write(sqlFile, StringPool.BLANK);
		}

		String content = FileUtil.read(sqlFile);

		int x = content.indexOf(
			_SQL_CREATE_TABLE + entityMapping.getTable() + " (");
		int y = content.indexOf(");", x);

		if (x != -1) {
			String oldCreateTableString = content.substring(x + 1, y);

			if (!oldCreateTableString.equals(newCreateTableString)) {
				content =
					content.substring(0, x) + newCreateTableString +
						content.substring(y + 2, content.length());

				FileUtil.write(sqlFile, content);
			}
		}
		else if (addMissingTables) {
			StringBuilder sb = new StringBuilder();

			BufferedReader br = new BufferedReader(new StringReader(content));

			String line = null;
			boolean appendNewTable = true;

			while ((line = br.readLine()) != null) {
				if (appendNewTable && line.startsWith(_SQL_CREATE_TABLE)) {
					x = _SQL_CREATE_TABLE.length();
					y = line.indexOf(" ", x);

					String tableName = line.substring(x, y);

					if (tableName.compareTo(entityMapping.getTable()) > 0) {
						sb.append(newCreateTableString + "\n\n");

						appendNewTable = false;
					}
				}

				sb.append(line);
				sb.append("\n");
			}

			if (appendNewTable) {
				sb.append("\n" + newCreateTableString);
			}

			br.close();

			FileUtil.write(sqlFile, sb.toString(), true);
		}
	}

	private void _createSQLSequences() throws IOException {
		if (!FileUtil.exists(_sqlDir)) {
			return;
		}

		File sqlFile = new File(_sqlDir + "/" + _sqlSequencesFileName);

		if (!sqlFile.exists()) {
			FileUtil.write(sqlFile, "");
		}

		Set<String> sequenceSQLs = new TreeSet<String>();

		BufferedReader br = new BufferedReader(new FileReader(sqlFile));

		while (true) {
			String sequenceSQL = br.readLine();

			if (sequenceSQL == null) {
				break;
			}

			if (Validator.isNotNull(sequenceSQL)) {
				sequenceSQLs.add(sequenceSQL);
			}
		}

		br.close();

		for (int i = 0; i < _ejbList.size(); i++) {
			Entity entity = _ejbList.get(i);

			if (!entity.isDefaultDataSource()) {
				continue;
			}

			List<EntityColumn> columnList = entity.getColumnList();

			for (int j = 0; j < columnList.size(); j++) {
				EntityColumn column = columnList.get(j);

				if ("sequence".equals(column.getIdType())) {
					StringBuilder sb = new StringBuilder();

					String sequenceName = column.getIdParam();

					if (sequenceName.length() > 30) {
						sequenceName = sequenceName.substring(0, 30);
					}

					sb.append("create sequence " + sequenceName + ";");

					String sequenceSQL = sb.toString();

					if (!sequenceSQLs.contains(sequenceSQL)) {
						sequenceSQLs.add(sequenceSQL);
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		Iterator<String> itr = sequenceSQLs.iterator();

		while (itr.hasNext()) {
			String sequenceSQL = itr.next();

			sb.append(sequenceSQL);

			if (itr.hasNext()) {
				sb.append("\n");
			}
		}

		FileUtil.write(sqlFile, sb.toString(), true);
	}

	private void _createSQLTables() throws IOException {
		if (!FileUtil.exists(_sqlDir)) {
			return;
		}

		File sqlFile = new File(_sqlDir + "/" + _sqlFileName);

		if (!sqlFile.exists()) {
			FileUtil.write(sqlFile, StringPool.BLANK);
		}

		for (int i = 0; i < _ejbList.size(); i++) {
			Entity entity = _ejbList.get(i);

			if (!entity.isDefaultDataSource()) {
				continue;
			}

			String createTableSQL = _getCreateTableSQL(entity);

			if (Validator.isNotNull(createTableSQL)) {
				_createSQLTables(sqlFile, createTableSQL, entity, true);

				File updateSQLFile = new File(
					_sqlDir + "/update-5.1.2-5.2.0.sql");

				if (updateSQLFile.exists()) {
					_createSQLTables(
						updateSQLFile, createTableSQL, entity, false);
				}
			}
		}

		for (Map.Entry<String, EntityMapping> entry :
				_entityMappings.entrySet()) {

			EntityMapping entityMapping = entry.getValue();

			String createMappingTableSQL = _getCreateMappingTableSQL(
				entityMapping);

			if (Validator.isNotNull(createMappingTableSQL)) {
				_createSQLMappingTables(
					sqlFile, createMappingTableSQL, entityMapping, true);
			}
		}
	}

	private void _createSQLTables(
			File sqlFile, String newCreateTableString, Entity entity,
			boolean addMissingTables)
		throws IOException {

		if (!sqlFile.exists()) {
			FileUtil.write(sqlFile, StringPool.BLANK);
		}

		String content = FileUtil.read(sqlFile);

		int x = content.indexOf(_SQL_CREATE_TABLE + entity.getTable() + " (");
		int y = content.indexOf(");", x);

		if (x != -1) {
			String oldCreateTableString = content.substring(x + 1, y);

			if (!oldCreateTableString.equals(newCreateTableString)) {
				content =
					content.substring(0, x) + newCreateTableString +
						content.substring(y + 2, content.length());

				FileUtil.write(sqlFile, content);
			}
		}
		else if (addMissingTables) {
			StringBuilder sb = new StringBuilder();

			BufferedReader br = new BufferedReader(new StringReader(content));

			String line = null;
			boolean appendNewTable = true;

			while ((line = br.readLine()) != null) {
				if (appendNewTable && line.startsWith(_SQL_CREATE_TABLE)) {
					x = _SQL_CREATE_TABLE.length();
					y = line.indexOf(" ", x);

					String tableName = line.substring(x, y);

					if (tableName.compareTo(entity.getTable()) > 0) {
						sb.append(newCreateTableString + "\n\n");

						appendNewTable = false;
					}
				}

				sb.append(line);
				sb.append("\n");
			}

			if (appendNewTable) {
				sb.append("\n" + newCreateTableString);
			}

			br.close();

			FileUtil.write(sqlFile, sb.toString(), true);
		}
	}

	private String _fixHbmXml(String content) throws IOException {
		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new StringReader(content));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.startsWith("\t<class name=\"")) {
				line = StringUtil.replace(
					line,
					new String[] {
						".service.persistence.", "HBM\" table=\""
					},
					new String[] {
						".model.", "\" table=\""
					});

				if (line.indexOf(".model.impl.") == -1) {
					line = StringUtil.replace(
						line,
						new String[] {
							".model.", "\" table=\""
						},
						new String[] {
							".model.impl.", "Impl\" table=\""
						});
				}
			}

			sb.append(line);
			sb.append('\n');
		}

		br.close();

		return sb.toString().trim();
	}

	private String _fixSpringXml(String content) {
		return StringUtil.replace(content, ".service.spring.", ".service.");
	}

	private String _formatXml(String xml)
		throws DocumentException, IOException {

		String doctype = null;

		int x = xml.indexOf("<!DOCTYPE");

		if (x != -1) {
			int y = xml.indexOf(">", x) + 1;

			doctype = xml.substring(x, y);

			xml = xml.substring(0, x) + "\n" + xml.substring(y);
		}

		xml = StringUtil.replace(xml, '\r', "");
		xml = XMLFormatter.toString(xml);
		xml = StringUtil.replace(xml, "\"/>", "\" />");

		if (Validator.isNotNull(doctype)) {
			x = xml.indexOf("?>") + 2;

			xml = xml.substring(0, x) + "\n" + doctype + xml.substring(x);
		}

		return xml;
	}

	private Map<String, Object> _getContext() throws TemplateModelException {
		BeansWrapper wrapper = BeansWrapper.getDefaultInstance();

		TemplateHashModel staticModels = wrapper.getStaticModels();

		Map<String, Object> context = new HashMap<String, Object>();

		context.put("hbmFileName", _hbmFileName);
		context.put("modelHintsFileName", _modelHintsFileName);
		context.put("springFileName", _springFileName);
		context.put("springBaseFileName", _springBaseFileName);
		context.put("springHibernateFileName", _springHibernateFileName);
		context.put("springInfrastructureFileName", _springInfrastructureFileName);
		context.put("apiDir", _apiDir);
		context.put("implDir", _implDir);
		context.put("jsonFileName", _jsonFileName);
		context.put("sqlDir", _sqlDir);
		context.put("sqlFileName", _sqlFileName);
		context.put("beanLocatorUtil", _beanLocatorUtil);
		context.put("beanLocatorUtilShortName", _beanLocatorUtilShortName);
		context.put("propsUtil", _propsUtil);
		context.put("portletName", _portletName);
		context.put("portletShortName", _portletShortName);
		context.put("portletPackageName", _portletPackageName);
		context.put("outputPath", _outputPath);
		context.put("serviceOutputPath", _serviceOutputPath);
		context.put("packagePath", _packagePath);
		context.put("pluginName", _pluginName);
		context.put("serviceBuilder", this);

		context.put("arrayUtil", ArrayUtil_IW.getInstance());
		context.put(
			"modelHintsUtil",
			staticModels.get("com.liferay.portal.model.ModelHintsUtil"));
		context.put("stringUtil", StringUtil_IW.getInstance());
		context.put("system", staticModels.get("java.lang.System"));
		context.put("tempMap", wrapper.wrap(new HashMap<String, Object>()));
		context.put(
			"validator",
			staticModels.get("com.liferay.portal.kernel.util.Validator"));

		return context;
	}

	private String _getCreateMappingTableSQL(EntityMapping entityMapping)
		throws IOException {

		Entity[] entities = new Entity[2];

		for (int i = 0; i < entities.length; i++) {
			entities[i] = getEntity(entityMapping.getEntity(i));

			if (entities[i] == null) {
				return null;
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(_SQL_CREATE_TABLE + entityMapping.getTable() + " (\n");

		for (Entity entity : entities) {
			List<EntityColumn> pkList = entity.getPKList();

			for (int i = 0; i < pkList.size(); i++) {
				EntityColumn col = pkList.get(i);

				String colName = col.getName();
				String colType = col.getType();

				sb.append("\t" + col.getDBName());
				sb.append(" ");

				if (colType.equalsIgnoreCase("boolean")) {
					sb.append("BOOLEAN");
				}
				else if (colType.equalsIgnoreCase("double") ||
						 colType.equalsIgnoreCase("float")) {

					sb.append("DOUBLE");
				}
				else if (colType.equals("int") ||
						 colType.equals("Integer") ||
						 colType.equalsIgnoreCase("short")) {

					sb.append("INTEGER");
				}
				else if (colType.equalsIgnoreCase("long")) {
					sb.append("LONG");
				}
				else if (colType.equals("String")) {
					Map<String, String> hints = ModelHintsUtil.getHints(
						_packagePath + ".model." + entity.getName(), colName);

					int maxLength = 75;

					if (hints != null) {
						maxLength = GetterUtil.getInteger(
							hints.get("max-length"), maxLength);
					}

					if (maxLength < 4000) {
						sb.append("VARCHAR(" + maxLength + ")");
					}
					else if (maxLength == 4000) {
						sb.append("STRING");
					}
					else if (maxLength > 4000) {
						sb.append("TEXT");
					}
				}
				else if (colType.equals("Date")) {
					sb.append("DATE null");
				}
				else {
					sb.append("invalid");
				}

				if (col.isPrimary()) {
					sb.append(" not null");
				}

				sb.append(",\n");
			}
		}

		sb.append("\tprimary key (");

		for (int i = 0; i < entities.length; i++) {
			Entity entity = entities[i];

			List<EntityColumn> pkList = entity.getPKList();

			for (int j = 0; j < pkList.size(); j++) {
				EntityColumn col = pkList.get(j);

				String colName = col.getName();

				if ((i != 0) || (j != 0)) {
					sb.append(", ");
				}

				sb.append(colName);
			}
		}

		sb.append(")\n");
		sb.append(");");

		return sb.toString();
	}

	private String _getCreateTableSQL(Entity entity) {
		List<EntityColumn> pkList = entity.getPKList();
		List<EntityColumn> regularColList = entity.getRegularColList();

		if (regularColList.size() == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(_SQL_CREATE_TABLE + entity.getTable() + " (\n");

		for (int i = 0; i < regularColList.size(); i++) {
			EntityColumn col = regularColList.get(i);

			String colName = col.getName();
			String colType = col.getType();
			String colIdType = col.getIdType();

			sb.append("\t" + col.getDBName());
			sb.append(" ");

			if (colType.equalsIgnoreCase("boolean")) {
				sb.append("BOOLEAN");
			}
			else if (colType.equalsIgnoreCase("double") ||
					 colType.equalsIgnoreCase("float")) {

				sb.append("DOUBLE");
			}
			else if (colType.equals("int") ||
					 colType.equals("Integer") ||
					 colType.equalsIgnoreCase("short")) {

				sb.append("INTEGER");
			}
			else if (colType.equalsIgnoreCase("long")) {
				sb.append("LONG");
			}
			else if (colType.equals("String")) {
				Map<String, String> hints = ModelHintsUtil.getHints(
					_packagePath + ".model." + entity.getName(), colName);

				int maxLength = 75;

				if (hints != null) {
					maxLength = GetterUtil.getInteger(
						hints.get("max-length"), maxLength);
				}

				if (maxLength < 4000) {
					sb.append("VARCHAR(" + maxLength + ")");
				}
				else if (maxLength == 4000) {
					sb.append("STRING");
				}
				else if (maxLength > 4000) {
					sb.append("TEXT");
				}
			}
			else if (colType.equals("Date")) {
				sb.append("DATE null");
			}
			else {
				sb.append("invalid");
			}

			if (col.isPrimary()) {
				sb.append(" not null");

				if (!entity.hasCompoundPK()) {
					sb.append(" primary key");
				}
			}
			else if (colType.equals("String")) {
				sb.append(" null");
			}

			if (Validator.isNotNull(colIdType) &&
				colIdType.equals("identity")) {

				sb.append(" IDENTITY");
			}

			if (((i + 1) != regularColList.size()) ||
				(entity.hasCompoundPK())) {

				sb.append(",");
			}

			sb.append("\n");
		}

		if (entity.hasCompoundPK()) {
			sb.append("\tprimary key (");

			for (int j = 0; j < pkList.size(); j++) {
				EntityColumn pk = pkList.get(j);

				sb.append(pk.getDBName());

				if ((j + 1) != pkList.size()) {
					sb.append(", ");
				}
			}

			sb.append(")\n");
		}

		sb.append(");");

		return sb.toString();
	}

	private String _getDimensions(Type type) {
		String dimensions = "";

		for (int i = 0; i < type.getDimensions(); i++) {
			dimensions += "[]";
		}

		return dimensions;
	}

	private JavaClass _getJavaClass(String fileName) throws IOException {
		int pos = fileName.indexOf(_implDir + "/");

		if (pos != -1) {
			pos += _implDir.length();
		}
		else {
			pos = fileName.indexOf(_apiDir + "/") + _apiDir.length();
		}

		String srcFile = fileName.substring(pos + 1, fileName.length());
		String className = StringUtil.replace(
			srcFile.substring(0, srcFile.length() - 5), "/", ".");

		JavaDocBuilder builder = new JavaDocBuilder();

		File file = new File(fileName);

		if (!file.exists()) {
			return null;
		}

		builder.addSource(file);

		return builder.getClassByName(className);
	}

	private JavaMethod[] _getMethods(JavaClass javaClass) {
		JavaMethod[] methods = javaClass.getMethods();

		for (JavaMethod method : methods) {
			Arrays.sort(method.getExceptions());
		}

		return methods;
	}

	private String _getSessionTypeName(int sessionType) {
		if (sessionType == _SESSION_TYPE_LOCAL) {
			return "Local";
		}
		else {
			return "";
		}
	}

	private String _getTplProperty(String key, String defaultValue) {
		return System.getProperty("service.tpl." + key, defaultValue);
	}

	private boolean _hasHttpMethods(JavaClass javaClass) {
		JavaMethod[] methods = _getMethods(javaClass);

		for (int i = 0; i < methods.length; i++) {
			JavaMethod javaMethod = methods[i];

			if (!javaMethod.isConstructor() && javaMethod.isPublic() &&
				isCustomMethod(javaMethod)) {

				return true;
			}
		}

		return false;
	}

	private List<Entity> _mergeReferenceList(List<Entity> referenceList) {
		List<Entity> list = new ArrayList<Entity>(
			_ejbList.size() + referenceList.size());

		list.addAll(_ejbList);
		list.addAll(referenceList);

		return list;
	}

	private String _processTemplate(String name) throws Exception {
		return _processTemplate(name, _getContext());
	}

	private String _processTemplate(String name, Map<String, Object> context)
		throws Exception {

		return FreeMarkerUtil.process(name, context);
	}

	private static final String _AUTHOR = "Brian Wing Shun Chan";

	private static final int _SESSION_TYPE_REMOTE = 0;

	private static final int _SESSION_TYPE_LOCAL = 1;

	private static final String _SQL_CREATE_TABLE = "create table ";

	private static final String _TPL_ROOT =
		"com/liferay/portal/tools/servicebuilder/dependencies/";

	private String _tplBadColumnNames = _TPL_ROOT + "bad_column_names.txt";
	private String _tplBadTableNames = _TPL_ROOT + "bad_table_names.txt";
	private String _tplEjbPk = _TPL_ROOT + "ejb_pk.ftl";
	private String _tplException = _TPL_ROOT + "exception.ftl";
	private String _tplExtendedModel = _TPL_ROOT + "extended_model.ftl";
	private String _tplExtendedModelImpl =
		_TPL_ROOT + "extended_model_impl.ftl";
	private String _tplFinder = _TPL_ROOT + "finder.ftl";
	private String _tplFinderUtil = _TPL_ROOT + "finder_util.ftl";
	private String _tplHbmXml = _TPL_ROOT + "hbm_xml.ftl";
	private String _tplJsonJs = _TPL_ROOT + "json_js.ftl";
	private String _tplJsonJsMethod = _TPL_ROOT + "json_js_method.ftl";
	private String _tplModel = _TPL_ROOT + "model.ftl";
	private String _tplModelClp = _TPL_ROOT + "model_clp.ftl";
	private String _tplModelHintsXml = _TPL_ROOT + "model_hints_xml.ftl";
	private String _tplModelImpl = _TPL_ROOT + "model_impl.ftl";
	private String _tplModelSoap = _TPL_ROOT + "model_soap.ftl";
	private String _tplPersistence = _TPL_ROOT + "persistence.ftl";
	private String _tplPersistenceImpl = _TPL_ROOT + "persistence_impl.ftl";
	private String _tplPersistenceTest = _TPL_ROOT + "persistence_test.ftl";
	private String _tplPersistenceUtil = _TPL_ROOT + "persistence_util.ftl";
	private String _tplProps = _TPL_ROOT + "props.ftl";
	private String _tplRemotingXml = _TPL_ROOT + "remoting_xml.ftl";
	private String _tplService = _TPL_ROOT + "service.ftl";
	private String _tplServiceBaseImpl = _TPL_ROOT + "service_base_impl.ftl";
	private String _tplServiceClp = _TPL_ROOT + "service_clp.ftl";
	private String _tplServiceClpSerializer =
		_TPL_ROOT + "service_clp_serializer.ftl";
	private String _tplServiceFactory = _TPL_ROOT + "service_factory.ftl";
	private String _tplServiceHttp = _TPL_ROOT + "service_http.ftl";
	private String _tplServiceImpl = _TPL_ROOT + "service_impl.ftl";
	private String _tplServiceJson = _TPL_ROOT + "service_json.ftl";
	private String _tplServiceJsonSerializer =
		_TPL_ROOT + "service_json_serializer.ftl";
	private String _tplServiceSoap = _TPL_ROOT + "service_soap.ftl";
	private String _tplServiceUtil = _TPL_ROOT + "service_util.ftl";
	private String _tplSpringBaseXml = _TPL_ROOT + "spring_base_xml.ftl";
	private String _tplSpringDynamicDataSourceXml =
		_TPL_ROOT + "spring_dynamic_data_source_xml.ftl";
	private String _tplSpringHibernateXml =
		_TPL_ROOT + "spring_hibernate_xml.ftl";
	private String _tplSpringInfrastructureXml =
		_TPL_ROOT + "spring_infrastructure_xml.ftl";
	private String _tplSpringXml = _TPL_ROOT + "spring_xml.ftl";
	private Set<String> _badTableNames;
	private Set<String> _badColumnNames;
	private String _hbmFileName;
	private String _modelHintsFileName;
	private String _springFileName;
	private String _springBaseFileName;
	private String _springDynamicDataSourceFileName;
	private String _springHibernateFileName;
	private String _springInfrastructureFileName;
	private String _apiDir;
	private String _implDir;
	private String _jsonFileName;
	private String _remotingFileName;
	private String _sqlDir;
	private String _sqlFileName;
	private String _sqlIndexesFileName;
	private String _sqlIndexesPropertiesFileName;
	private String _sqlSequencesFileName;
	private boolean _autoNamespaceTables;
	private String _beanLocatorUtil;
	private String _beanLocatorUtilShortName;
	private String _propsUtil;
	private String _pluginName;
	private String _testDir;
	private String _author;
	private String _portletName = StringPool.BLANK;
	private String _portletShortName = StringPool.BLANK;
	private String _portletPackageName = StringPool.BLANK;
	private String _outputPath;
	private String _serviceOutputPath;
	private String _testOutputPath;
	private String _packagePath;
	private List<Entity> _ejbList;
	private Map<String, EntityMapping> _entityMappings;
	private Map<String, Entity> _entityPool = new HashMap<String, Entity>();

}