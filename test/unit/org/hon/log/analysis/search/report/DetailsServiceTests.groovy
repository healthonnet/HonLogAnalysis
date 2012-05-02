package org.hon.log.analysis.search.report

import org.hon.log.analysis.search.SearchLogLine;

import grails.test.*
import groovy.time.*;

class DetailsServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }


	void test_session_length_duration() {
		def stD = Date.parse("yyyy-MM-dd h:m:s", "2011-11-29 8:51:41")
		def eD = Date.parse("yyyy-MM-dd h:m:s", "2011-11-30 10:58:41")
		println "date: ${stD} , end:${eD}"
		
		TimeDuration duration=TimeCategory.minus(eD,stD)
		println duration
		assert duration.toString()==new TimeDuration(1,2,7,0,0).toString()
	}
}
