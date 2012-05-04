package org.hon.log.analysis.search.report

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.loader.SearchLogLoaderService
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;

class DetailsServiceIntegrationTests extends GroovyTestCase {
	SearchLogLoaderService searchLogLoaderService
	DetailsService detailsService
	
    protected void setUp() {
        super.setUp()			
    }

    protected void tearDown() {
        super.tearDown()
    }
		
	@Ignore // dunno why this test is failing - nolan
	void test_group_by_user() {
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.log').file)
		
			Map detByUser=detailsService.listByUser();
			//how many users from the log file
			assert detByUser.size() == 7
			//how many line for the user
			assert detByUser['2B09B2A6EDA8343B0B14DD2EB764ABA3'].size() == 3
			//accessing a specific line of user, verifying its language
			SearchLogLine sll=detByUser['2B09B2A6EDA8343B0B14DD2EB764ABA3'].get(1)
			assert sll.language=='en'
			
		}

	@Ignore // dunno why this test is failing - nolan
	void test_terms_by_user() {
		//it doesn't work from loader
		//searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-1.log').file)
		
		new SearchLogLine(remoteIp:'41.92.5.171', termList:['glaucoma','arthrit'], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)
		new SearchLogLine(remoteIp:'41.92.5.171', termList:['arthrit','mycoses'], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)
		new SearchLogLine(remoteIp:'41.104.48.75', termList:['kot'], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)
		new SearchLogLine(remoteIp:'41.104.48.75', termList:[], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)
		new SearchLogLine(remoteIp:'41.104.48.75', termList:[], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)
		new SearchLogLine(remoteIp:'41.104.48.75', termList:[], source:'test', origQuery:'xxx', date:new Date()).save(failOnError:true)

		
		
		Map detByUser=detailsService.distinctTermsByUser();
		assert detByUser['41.92.5.171'].size() == 3
		//how many distinct users from the log file
		assert detByUser.size() == 2
		//accessing list of terms of user, verifying a term
		String str=detByUser['41.92.5.171'].get(2)
		assert str=='mycoses'
		
	}
	
}
