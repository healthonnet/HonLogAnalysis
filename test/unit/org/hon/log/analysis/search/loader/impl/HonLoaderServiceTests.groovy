package org.hon.log.analysis.search.loader.impl

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

class HonLoaderServiceTests extends GrailsUnitTestCase {
	HonLoaderService service = [:]
	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_accept_line() {
		//only accept line with action=search
		assert service.acceptLine('<<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:45:54 +0100]>><<query=?engine=honSelect&search=Arthrogrypose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>')
		
		//reject logline from honCode
		assert !service.acceptLine('<<remoteIp=91.180.82.31>><<usertrack=->><<time=[19/Dec/2011:15:44:21 +0100]>><<query=?engine=honCodeSearch&q=la%2Bprostate&language=fr&action=search>><<referer=http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&lr=lang_fr&hl=fr&cof=FORID%3A11&q=la+prostate>>')

		//empty search
		assert !service.acceptLine('<<remoteIp=195.189.142.231>><<usertrack=195.189.142.231.1323565174598994>><<time=[11/Dec/2011:01:59:35 +0100]>><<query=?engine=honSelect&action=search>><<referer=->>')

		//reject logline from db-style
		assert !service.acceptLine('119.159.11.181 - - [03/May/2012:17:36:33 +0200] "GET /~honlogs/logger.png?engine=honSelect&search=Genitalia%2C+Female&EXACT=0&TYPE=1&action=search HTTP/1.1" 200 6133');
		
	}

	void test_line2map(){
		String line = '<<remoteIp=195.189.142.231>><<usertrack=195.189.142.231.1323565174598994>><<time=[11/Dec/2011:01:59:35 +0100]>><<query=?engine=honSelect&action=search>><<referer=->>'
		Map m = service.line2Map(line)
		assert m.size() == 5
	}
	
	void test_parse_query(){
		def q = service.parseQuery('search=Arthrogrypose&EXACT=0&TYPE=1&action=search')
		assert q
		assert q.terms==['Arthrogrypose']
	}
	
	void test_parse_line_basic(){
		mockDomain(Term)
		mockDomain(SearchLogLine)
		SearchLogLine sll = service.parseLine('<<remoteIp=187.18.200.5>><<usertrack=187.18.200.5.1322553057659472>><<time=[29/Nov/2011:08:51:41 +0100]>><<query=?engine=honSelect&search=Nefrocalcinose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_pt?browse+C12.777.419.590>>')
		assert sll
		assert sll.terms.collect{"$it"} == ['nefrocalcinose']
		assert sll.source == 'hon'
		assert sll.userId == '187.18.200.5.1322553057659472'
		
		def now = Date.parse("yyyy-MM-dd h:m:s", "2011-11-29 8:51:41")
		assert sll.date == now
		
	}
	
	void test_parse_line_basic_2(){
		mockDomain(Term)
		mockDomain(SearchLogLine)
		SearchLogLine sll = service.parseLine('<<remoteIp=129.143.71.36>><<usertrack=->><<time=[29/Nov/2011:08:50:45 +0100]>><<query=?engine=honSelect&search=Krankheitszeichen+und+Symptome&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_de/C23.888.html>>')
		assert sll
		assert sll.terms.collect{"$it"}.sort() == ['krankheitszeichen','symptome','und']
		assert sll.source == 'hon'
		assert sll.userId == '-'
		
		def now = Date.parse("yyyy-MM-dd h:m:s", "2011-11-29 8:50:45")
		assert sll.date == now
		
	}
	
	void test_multi_terms(){
		mockDomain(SearchLogLine)
		mockDomain(Term)
		String str = '<<remoteIp=108.195.226.117>><<usertrack=->><<time=[11/Dec/2011:23:09:58 +0100]>><<query=?engine=honSelect&search=Cushing+Syndrome&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>>>'
		assert service.acceptLine(str)
		SearchLogLine sll = service.parseLine(str)
		assert sll
		assert sll.terms.collect{"$it"}.sort() == ['cushing', 'syndrome']
		assert sll.remoteIp == '108.195.226.117'
	}

	void test_multi_terms_encoded(){
		mockDomain(SearchLogLine)
		mockDomain(Term)
		
		String str = '<<remoteIp=88.74.122.100>><<usertrack=88.74.122.100.1323558003433727>><<time=[11/Dec/2011:00:00:04 +0100]>><<query=?engine=honSelect&search=Genitalkrankheiten%2C+m%C3%A4nnliche&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>>>'
		assert service.acceptLine(str)
		SearchLogLine sll = service.parseLine(str)

		assert sll
		assert sll.terms.size() == 2
		Term.list().each{println it}
		assert sll.terms.collect{"$it"}.sort() == ['genitalkrankheiten', 'm' + ((char)0344) + 'nnliche']
	}
	
	void test_problem_lines(){
		mockDomain(SearchLogLine)
		mockDomain(Term)
		
		checkLine('<<remoteIp=99.237.143.212>><<usertrack=99.237.143.212.1322781922269303>><<time=[02/Dec/2011:00:25:24 +0100]>><<query=?engine=honSelect&search=Crohn+Disease&action=search>><<referer=http://www.hon.ch/HONselect/RareDiseases/EN/C06.405.205.731.500.html>>')
		checkLine('<<remoteIp=41.141.213.111>><<usertrack=41.141.213.111.1322862572791662>><<time=[02/Dec/2011:22:49:36 +0100]>><<query=?engine=honSelect&search=Glandes+bulbo-ur%C3%A9trales&EXACT=0&TYPE=1&action=search>><<referer=http://129.195.254.166/cgi-bin/HONselect_f?browse+A05.360.444.123>>')
	}
	
	void test_problem_line2() {
		def line = '<<remoteIp=189.127.148.93>><<usertrack=189.127.148.93.1322251209304264>><<time=[25/Nov/2011:21:00:14 +0100]>><<query=?search=Doen%C3%A7as+do+Sistema+End%C3%B3crino&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_pt/C19.html>>';
		
		service.parseLine(line)
	}

	private void checkLine(String str){
		
	}	
}
