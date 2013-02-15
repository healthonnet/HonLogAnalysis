package org.hon.log.analysis.search.loader.impl

import grails.test.*

import org.hon.log.analysis.search.Country
import org.hon.log.analysis.search.IpAddress
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

class HonLoaderServiceTests extends GrailsUnitTestCase {
	HonLoaderService service = [:]
	protected void setUp() {
		super.setUp()
		mockDomain(Term)
		mockDomain(SearchLogLine)
		mockDomain(IpAddress)
		mockDomain(Country)
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_accept_line() {
		//only accept line with action=search
		assert service.acceptLine('<<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:45:54 +0100]>><<query=?engine=honSelect&search=Arthrogrypose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>')
		
		//accept logline from honCode
		assert service.acceptLine('<<remoteIp=91.180.82.31>><<usertrack=->><<time=[19/Dec/2011:15:44:21 +0100]>><<query=?engine=honCodeSearch&q=la%2Bprostate&language=fr&action=search>><<referer=http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&lr=lang_fr&hl=fr&cof=FORID%3A11&q=la+prostate>>')

		//empty search
		assert !service.acceptLine('<<remoteIp=195.189.142.231>><<usertrack=195.189.142.231.1323565174598994>><<time=[11/Dec/2011:01:59:35 +0100]>><<query=?engine=honSelect&action=search>><<referer=->>')

		//reject logline from db-style
		assert !service.acceptLine('119.159.11.181 - - [03/May/2012:17:36:33 +0200] "GET /~honlogs/logger.png?engine=honSelect&search=Genitalia%2C+Female&EXACT=0&TYPE=1&action=search HTTP/1.1" 200 6133');
		
		// accept euhp line
		assert service.acceptLine('<<remoteIp=194.230.70.132>><<usertrack=129.195.199.181.1339488644214776>><<time=[17/Jul/2012:08:20:47 +0200]>><<query=?engine=euhpSearch&q=asthma+&facet=%2Beuportal_site_facet%3Atrue%2B!has_video_facet%3Atrue%2B!is_feed_facet%3Atrue%2B!is_event_facet%3Atrue&page=null&click=&link=undefined&action=autocomplete-search>><<referer=http://localhost:9090/hon-search/euhp>>');
		
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
		SearchLogLine sll = service.parseLine('<<remoteIp=187.18.200.5>><<usertrack=187.18.200.5.1322553057659472>><<time=[29/Nov/2011:08:51:41 +0100]>><<query=?engine=honSelect&search=Nefrocalcinose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_pt?browse+C12.777.419.590>>')
		assert sll
		assert sll.terms.collect{"$it"} == ['nefrocalcinose']
		assert sll.source == 'hon'
		assert sll.userId == '187.18.200.5.1322553057659472'
		
		def now = Date.parse("yyyy-MM-dd h:m:s", "2011-11-29 8:51:41")
		assert sll.date == now
		
	}
	
	void test_parse_line_basic_2(){
		SearchLogLine sll = service.parseLine('<<remoteIp=129.143.71.36>><<usertrack=->><<time=[29/Nov/2011:08:50:45 +0100]>><<query=?engine=honSelect&search=Krankheitszeichen+und+Symptome&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_de/C23.888.html>>')
		assert sll
		assert sll.terms.collect{"$it"}.sort() == ['krankheitszeichen','symptome','und']
		assert sll.source == 'hon'
		assert sll.userId == '-'
		
		def now = Date.parse("yyyy-MM-dd h:m:s", "2011-11-29 8:50:45")
		assert sll.date == now
		
	}
	
	void test_multi_terms(){
		String str = '<<remoteIp=108.195.226.117>><<usertrack=->><<time=[11/Dec/2011:23:09:58 +0100]>><<query=?engine=honSelect&search=Cushing+Syndrome&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>>>'
		assert service.acceptLine(str)
		SearchLogLine sll = service.parseLine(str)
		assert sll	
		assert sll.terms.collect{"$it"}.sort() == ['cushing', 'syndrome']
		assert sll.ipAddress.value == '108.195.226.117'
	}

	void test_multi_terms_encoded(){
		
		String str = '<<remoteIp=88.74.122.100>><<usertrack=88.74.122.100.1323558003433727>><<time=[11/Dec/2011:00:00:04 +0100]>><<query=?engine=honSelect&search=Genitalkrankheiten%2C+m%C3%A4nnliche&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>>>'
		assert service.acceptLine(str)
		SearchLogLine sll = service.parseLine(str)

		assert sll
		assert sll.terms.size() == 2
		Term.list().each{println it}
		assert sll.terms.collect{"$it"}.sort() == ['genitalkrankheiten', 'm' + ((char)0344) + 'nnliche']
	}

	
	void test_problem_line2() {
		def line = '<<remoteIp=189.127.148.93>><<usertrack=189.127.148.93.1322251209304264>><<time=[25/Nov/2011:21:00:14 +0100]>><<query=?search=Doen%C3%A7as+do+Sistema+End%C3%B3crino&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_pt/C19.html>>';
		
		service.parseLine(line)
	}
    
    void testIssue1() {
        
        // Fixes for GitHub issue 1: https://github.com/healthonnet/HonLogAnalysis/issues/1
        
        // honCodeSearch line that causes the error
        def badLine = "<<remoteIp=88.186.40.171>><<usertrack=->><<time=[18/Apr/2012:22:32:54 +0200]>><<query=?engine=honCodeSearch&q=%25%2Bdes%2Bpatients%2Bsouffrent%2Bde%2Btroubles%2Bpsychique&language=fr&action=search>><<referer=http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&lr=lang_fr&hl=fr&cof=FORID%3A11&q=%+des+patients+souffrent+de+troubles+psychique>>";
        
        // honCodeSearch line that works fine
        def goodLine = "<<remoteIp=41.229.150.82>><<usertrack=41.229.150.82.1327263689308128>><<time=[08/Feb/2012:00:08:57 +0100]>><<query=?engine=honCodeSearch&q=%2B%2B%2Brefus%2Bde%2Bsoins&language=fr&action=search>><<referer=http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&q=+++refus+de+soins&sa=Chercher&lr=lang_fr&hl=fr&cof=FORID%3A11>>"
        
        // euhp line, just to check
        def euhpLine = "<<remoteIp=129.195.199.181>><<usertrack=129.195.199.181.1342697225579459>><<time=[11/Jan/2013:11:07:42 +0100]>><<query=?engine=euhpSearch&q=pillule+generation&facet=%2BdocType%3Ahtml%2Beuportal_site_facet%3Atrue%2Bis_feed_facet%3Atrue&page=null&click=&link=undefined&action=search>><<referer=http://comp.hon.ch/hon-search/?q=pillule%20generation&start=0&fq=docType:html&fq=euportal_site_facet:true&fq=is_feed_facet:true&group.field=domain&overrideQ=&searchLanguage=fr>>"
        
        // hon select line, just to check
        def honSelectLine = "<<remoteIp=190.246.196.206>><<usertrack=->><<time=[15/Jan/2013:13:57:49 +0100]>><<query=?engine=honSelect&search=S%C3%ADndrome+de+Goldenhar&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/RareDiseases/SP/C05.116.099.370.231.576.410.html>>"
        
        checkLine(badLine, "honCodeSearch", "% des patients souffrent de troubles psychique",
            "http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&lr=lang_fr&hl=fr&cof=FORID%3A11&q=%+des+patients+souffrent+de+troubles+psychique"
            )
        
        checkLine(goodLine, "honCodeSearch", "   refus de soins",
            "http://www.hon.ch/HONcode/Search/search_f.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&q=+++refus+de+soins&sa=Chercher&lr=lang_fr&hl=fr&cof=FORID%3A11"
            )
        
        checkLine(euhpLine, "euhpSearch", "pillule generation", 
            "http://comp.hon.ch/hon-search/?q=pillule%20generation&start=0&fq=docType:html&fq=euportal_site_facet:true&fq=is_feed_facet:true&group.field=domain&overrideQ=&searchLanguage=fr"
			)
        
        checkLine(honSelectLine, "honSelect", "S\u00EDndrome de Goldenhar",
            "http://www.hon.ch/HONselect/RareDiseases/SP/C05.116.099.370.231.576.410.html"
            )
    }
    
    void testProperEscaping() {
        // some lines are still not properly escaped
        def line = "<<remoteIp=192.35.79.70>><<usertrack=192.35.79.70.1324061914127740>><<time=[16/Dec/2011:22:26:44 +0100]>><<query=?engine=honCodeSearch&q=enuresis%2Bin%2Badults%2B%2528and%2Bchildren%2529%253A%2Breviewed%2B&language=en&action=search>><<referer=http://www.hon.ch/HONcode/Search/search.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&q=enuresis+in+adults+%28and+children%29%3A+reviewed+&sa=Search&lr=lang_en&hl=en&cof=FORID%3A11>>"
        checkLine(line, "honCodeSearch", "enuresis in adults (and children): reviewed ",
            "http://www.hon.ch/HONcode/Search/search.html?cref=http%3A%2F%2Fwww.hon.ch%2FCSE%2FHONCODE%2Fcontextlink.xml&q=enuresis+in+adults+%28and+children%29%3A+reviewed+&sa=Search&lr=lang_en&hl=en&cof=FORID%3A11"
            )
    }

	private void checkLine(String line, String expectedEngine, String expectedOrigQuery, String expectedSource){
        
		SearchLogLine parsedLine = service.parseLine(line)
        
        assert parsedLine.engine == expectedEngine
        assert parsedLine.origQuery == expectedOrigQuery
        assert parsedLine.referer == expectedSource        
	}	
}
