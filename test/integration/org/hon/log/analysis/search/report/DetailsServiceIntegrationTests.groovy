package org.hon.log.analysis.search.report

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.loader.SearchLogLoaderService
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;

class DetailsServiceIntegrationTests extends GroovyTestCase {
	SearchLogLoaderService searchLogLoaderService
	DetailsService detailsService
	def sessionFactory;

	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_group_by_user() {
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.txt').file)

		Map detByUser=detailsService.listByUser();
		//how many users from the log file
		assert detByUser.size() == 7
		//how many line for the user
		assert detByUser['2B09B2A6EDA8343B0B14DD2EB764ABA3'] == 3


	}

	void test_terms_by_user() {
		searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-1.txt').file)

		assert SearchLogLine.count() == 96;

		Map detByUser=detailsService.distinctTermsByUser();
		assert detByUser['84.49.130.205.1323558735943643'] == 19
		//how many distinct users from the log file
		assert detByUser.size() == 33



	}
}
