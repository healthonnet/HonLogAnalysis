package org.hon.log.analysis.search.loader.impl

import grails.test.*

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

class DbsLoaderServiceTests extends GrailsUnitTestCase {
	DbsLoaderService service = [:]
	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_accept_line() {
		//only accept line with action=search_sim
		assert service.acceptLine('2006-12-31 23:25:45 GET /portal/telstat.php sesid=k3qjenkg9upbh7kvk8g4ogf8j0&lang=en&query=("von%20paradis")&action=search_sim&colid=collections:a0000&objurl= 86.138 Mozilla/4.0+(compatible;+MSIE+7.0;+Windows+NT+5.1;+InfoPath.1) __utma=94440793.1815055979.1166448885.1166487603.1166495572.6;+__utmz=94440793.1166487603.5.5.utmccn=(organic)|utmcsr=yahoo|utmctr=EUROPEAN+MUSIC+LIBRARIES|utmcmd=organic;+cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=k3qjenkg9upbh7kvk8g4ogf8j0;+AreCookiesEnabled=529;+cTargetsThemes=theme0;+lastviewed=0 http://www.theeuropeanlibrary.org/portal/index.htm')
		assert !service.acceptLine('2006-12-31 23:04:38 GET /portal/telstat.php sesid=d551tvd9legbq3rh4l23rjkgh7&lang=en&query=(%22pferd%22)&action=view_brief&colid=collections:a0000&objurl= 71.34 Mozilla/5.0+(Windows;+U;+Windows+NT+5.2;+en-US;+rv:1.8.1.1)+Gecko/20061204+Firefox/2.0.0.1 cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=d551tvd9legbq3rh4l23rjkgh7;+AreCookiesEnabled=889;+cTargetsThemes=theme0;+lastviewed=0 - ')

		assert !service.acceptLine('2006-12-31 23:03:40 GET /Index.html - 74.6 Mozilla/5.0+(compatible;+Yahoo!+Slurp;+http://help.yahoo.com/help/us/ysearch/slurp) - - ')
	}

	void test_parse_line_basic(){
		mockDomain(SearchLogLine)
		mockDomain(Term)
		SearchLogLine sll = service.parseLine('2006-12-31 23:25:45 GET /portal/telstat.php sesid=k3qjenkg9upbh7kvk8g4ogf8j0&lang=en&query=("von%20paradis")&action=view_brief&colid=collections:a0000&objurl= 86.138 Mozilla/4.0+(compatible;+MSIE+7.0;+Windows+NT+5.1;+InfoPath.1) __utma=94440793.1815055979.1166448885.1166487603.1166495572.6;+__utmz=94440793.1166487603.5.5.utmccn=(organic)|utmcsr=yahoo|utmctr=EUROPEAN+MUSIC+LIBRARIES|utmcmd=organic;+cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=k3qjenkg9upbh7kvk8g4ogf8j0;+AreCookiesEnabled=529;+cTargetsThemes=theme0;+lastviewed=0 http://www.theeuropeanlibrary.org/portal/index.htm')
		assert sll
		assert sll.source == 'dbs' 
		assert sll.date == Date.parse("y-M-d H:m:s",'2006-12-31 23:25:45')
		assert sll.language == 'en'
		assert sll.origQuery == '"von paradis"'
		assert sll.terms.collect{"$it"}.sort() == ['paradis', 'von']
	}

	void test_parse_line_query_encoded_terms(){
		mockDomain(SearchLogLine)
		mockDomain(Term)
		SearchLogLine sll = service.parseLine('2006-12-31 23:58:25 GET /portal/telstat.php sesid=fd0g2clmp2t6h81h8f11r5eha3&lang=en&query=(%22adhd%22)&action=view_brief&colid=collections:a0000&objurl= 84.249 Mozilla/5.0+(Windows;+U;+Windows+NT+5.1;+fi;+rv:1.8.1.1)+Gecko/20061204+Firefox/2.0 cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=fd0g2clmp2t6h81h8f11r5eha3;+AreCookiesEnabled=674;+cTargetsThemes=theme0;+lastviewed=0 http://www.theeuropeanlibrary.org/portal/index.html ')
		assert sll
		assert sll.terms.collect{"$it"}.sort() == ['adhd']
	}
}
