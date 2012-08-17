package org.hon.log.analysis.search.report

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.SearchLogLoaderService
import org.hon.log.analysis.search.query.Term
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource

class StatisticsServiceIntegrationTests extends GroovyTestCase {
	SearchLogLoaderService searchLogLoaderService
	StatisticsService statisticsService

	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_language_count() {
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.txt').file)

		Map m = statisticsService.countByLanguage();
		assert m.keySet() == ['en','de','it'] as Set
		assert m.size() == 3
		assert m.en == 12
	}
	
	void test_query_length() {
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.txt').file)
		Map m = statisticsService.countByQueryLength();
		assert m == [1:3, 2:3, 3:1]
	}

	void testMostUserTerms() {
		searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-1.txt').file)

		assert SearchLogLine.count() == 96
		assert Term.count() == 145

		Map tcount = statisticsService.mostUsedTerms([limit:2]);
		assert tcount == [syndrome:12, diseases:10]
	}

	void test_most_frequent_cooccurence() {
		searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-1.txt').file)
		
		Map m =statisticsService.mostFrequentTermCoOccurence(limit:2)
		assert m == ['appareil|digestif':4, 'appareil|maladie':4]

		
	}
	
	void test_countByCountry() {
		searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-1.txt').file)
		
		Map m =statisticsService.countByCountry()
		
		assert m.size() == 3
		assert m.totalCount == 96;
		assert m.ipsCount == 57
		assert m.countryCodeCounts == [DE:[2, 'Germany'], ES:[4, 'Spain'], US:[27, 'United States'], NO:[8, 'Norway'], 
			FR:[4, 'France'], BR:[9, 'Brazil'], CA:[10, 'Canada'], MQ:[3, 'Martinique'], VE:[1, 'Venezuela'], 
			TN:[8, 'Tunisia'], UA:[2, 'Ukraine'], AR:[5, 'Argentina'], EC:[2, 'Ecuador'], MA:[2, 'Morocco'], MX:[3, 'Mexico'], 
			PT:[1, 'Portugal'], RU:[1, 'Russian Federation'], PE:[3, 'Peru'], PH:[1, 'Philippines']]
	}
}
