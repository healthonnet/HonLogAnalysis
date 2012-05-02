package org.hon.log.analysis.search.loader.impl

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

class TelLoaderServiceTests extends GrailsUnitTestCase {
	TelLoaderService service = [:]

	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_source(){
		assert service.source == 'tel'
	}

	void test_accept_line(){
		assert !service.acceptLine("pipo")
		assert service.acceptLine('100778,guest,161.53,6DF31EF9B4CF4B7513B077217BC99C41,en,"(""science and nature"")",view_full,a0073,1,1,,"http://search.theeuropeanlibrary.org/portal/search/collections/a0073/(""science and nature"").query?position=1",2009-03-23 00:00:00.0')
	}

	void test_parse_query_basic(){
		def q = service.parseQuery('(""science and nature"")')
		assert q.terms == ['and', 'nature', 'science']
		assert !q.topics
	}

	void test_parse_query_multiple_terms_and_topics(){
		def q = service.parseQuery('(title all ""atlas"") and (creator all ""dufour"")')
		assert q.terms == ['atlas', 'dufour']
		assert q.topics==['all', 'creator', 'title']
	}
	
	void test_parse_query_single_term(){
		def q = service.parseQuery('(title all ""credit risk"")')
		assert q.terms == ['credit', 'risk']
		assert q.topics == ['all','title']
	}
	
	void test_parse_query_multiple_terms_single_topics(){
		def q = service.parseQuery('(title all ""atlas"") and (creator all ""atlas"")')
		assert q.terms == ['atlas']
		assert q.topics == ['all', 'creator', 'title']
	}

	void test_parse_log_line_basic() {
		mockDomain(SearchLogLine)
		mockDomain(Term)
		SearchLogLine sll = service.parseLine('100778,guest,161.53,6DF31EF9B4CF4B7513B077217BC99C41,en,"(""science and nature"")",view_full,a0073,1,1,,"http://search.theeuropeanlibrary.org/portal/search/collections/a0073/(""science and nature"").query?position=1",2009-03-23 00:00:00.0')
		assert sll
		assert sll.source == 'tel'
		assert sll.sessionId == '6DF31EF9B4CF4B7513B077217BC99C41'
		assert sll.userId == '6DF31EF9B4CF4B7513B077217BC99C41'
		assert sll.date
		assert sll.language == 'en'
		assert sll.origQuery == '(""science and nature"")'
		assert sll.terms.collect{"$it"}.sort() == ['nature', 'science']
		assert sll.remoteIp == '161.53'

	}
	
	void test_parse_log_line__multiple_terms_and_topics() {
		mockDomain(SearchLogLine)
		mockDomain(Term)
		SearchLogLine sll = service.parseLine('101595,guest,194.63,CF734832BBA23495C52652356E448B4B,en,"(title all ""atlas"") and (creator all ""dufour"")",view_full,a0071,6,3,,"http://search.theeuropeanlibrary.org/portal/search/collections/a0071/(title all ""atlas"") and (creator all ""dufour"").query?position=3",2009-03-23 00:00:00.0')
		assert sll
		assert sll.terms.collect{"$it"}.sort() == ['atlas', 'dufour']
		assert sll.topics==['all', 'creator', 'title']
	}
	
	void test_parse_log_line_single_term() {
		mockDomain(SearchLogLine)
		mockDomain(Term)
		SearchLogLine sll = service.parseLine('103272,guest,79.127,4979D27FBEEC2607758912BE74A12550,ru,"(title all ""credit risk"")",see_online,a0071,59,6,,http://othes.univie.ac.at/210/,2009-03-24 00:00:00.0')
		assert sll
		assert sll.language == 'ru'
		assert sll.terms.collect{"$it"}.sort() == ['credit', 'risk']
		assert sll.topics == ['all','title']
	}
}
