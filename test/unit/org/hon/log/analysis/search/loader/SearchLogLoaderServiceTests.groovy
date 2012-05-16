package org.hon.log.analysis.search.loader

import grails.test.*

import java.io.File

import org.hon.log.analysis.search.LoadedFile
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.impl.DbsLoaderService
import org.hon.log.analysis.search.loader.impl.HonLoaderService;
import org.hon.log.analysis.search.loader.impl.TelLoaderService
import org.hon.log.analysis.search.query.Term
import org.springframework.core.io.ClassPathResource

class SearchLogLoaderServiceTests extends GrailsUnitTestCase {
	SearchLogLoaderService searchLogLoaderService
	protected void setUp() {
		super.setUp()
		mockLogging(SearchLogLoaderService)
		searchLogLoaderService =[:]

		searchLogLoaderService.loaders=[:]
		[
			new DbsLoaderService(),
			new TelLoaderService(),
			new HonLoaderService(),
		].each{
			searchLogLoaderService.loaders[it.source]=it
		}
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_count_loaders() {
		assert searchLogLoaderService.loaders.size() == 3
	}

	void test_loader_detection_ok(){
		assert searchLogLoaderService.getLoaderService('dbs');
		assert searchLogLoaderService.getLoaderService('tel');
	}

}
