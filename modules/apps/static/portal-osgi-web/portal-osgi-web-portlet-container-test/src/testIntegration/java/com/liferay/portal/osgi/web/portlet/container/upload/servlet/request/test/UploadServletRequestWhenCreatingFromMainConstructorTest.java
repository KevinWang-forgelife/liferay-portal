/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.osgi.web.portlet.container.upload.servlet.request.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.LiferayServletRequest;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.util.test.PortletContainerTestUtil;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class UploadServletRequestWhenCreatingFromMainConstructorTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_fileNameParameter = RandomTestUtil.randomString();
	}

	@Test
	public void testShouldAddProgressTrackerToSession() {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		HttpServletRequest mockHttpServletRequest =
			(HttpServletRequest)liferayServletRequest.getRequest();

		new UploadServletRequestImpl(mockHttpServletRequest);

		HttpSession mockHttpSession = mockHttpServletRequest.getSession();

		Assert.assertNotNull(
			mockHttpSession.getAttribute(ProgressTracker.PERCENT));
	}

	@Test
	public void testShouldNotPopulateParameters() {
		LiferayServletRequest liferayServletRequest =
			PortletContainerTestUtil.getMultipartRequest(
				_fileNameParameter, _BYTES);

		HttpServletRequest mockHttpServletRequest =
			(HttpServletRequest)liferayServletRequest.getRequest();

		UploadServletRequestImpl uploadServletRequest =
			new UploadServletRequestImpl(mockHttpServletRequest);

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequest.getMultipartParameterMap();

		Assert.assertNotNull(multipartParameterMap);
		Assert.assertTrue(
			multipartParameterMap.toString(), multipartParameterMap.isEmpty());

		Map<String, List<String>> regularParameterMap =
			uploadServletRequest.getRegularParameterMap();

		Assert.assertNotNull(regularParameterMap);
		Assert.assertTrue(
			regularParameterMap.toString(), regularParameterMap.isEmpty());
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	private static String _fileNameParameter;

}