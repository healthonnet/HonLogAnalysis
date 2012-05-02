package org.hon.log.analysis.search.report

import grails.test.*

import org.hon.log.analysis.search.LoadedFile;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term


class StatisticsServiceTests extends GrailsUnitTestCase {

	StatisticsService service = [:]


	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_keep_most_famous(){
		Map m =[
					a:10,
					b:8,
					c:7,
					d:7,
					e:5,
					f:4
				]

		service.keepMostFamous(m, 3)
		assert m.size()==4
		assert m == [
			a:10,
			b:8,
			c:7,
			d:7,
		]
	}
	


}
