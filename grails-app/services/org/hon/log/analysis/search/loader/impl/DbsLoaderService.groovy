package org.hon.log.analysis.search.loader.impl

import java.util.Map
import java.util.regex.Pattern

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst

class DbsLoaderService extends SearchLogLineLoaderAbst{
	//2006-12-31 23:04:24 GET /portal/telstat.php sesid=d551tvd9legbq3rh4l23rjkgh7&lang=en&query=(%22pferd%22)&action=search_sim&colid=&objurl= 71.34 Mozilla/5.0+(Windows;+U;+Windows+NT+5.2;+en-US;+rv:1.8.1.1)+Gecko/20061204+Firefox/2.0.0.1 cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=d551tvd9legbq3rh4l23rjkgh7;+AreCookiesEnabled=889;+cTargetsThemes=theme0 -

	final String source = 'dbs'
	Pattern patternLine = ~/^(\d{4}\-\d{2}\-\d{2} \d{2}:\d{2}:\d{2}) GET .* sesid=(\w+)&lang=(\w*)&query=\((.*)\)&action=.*/
	Pattern patternQuery = ~/^"(.*)"$/

	@Override
	public void init(Map options) {

	}

	@Override
	public boolean acceptLine(String line) {
		return line.contains('GET /portal/telstat.php') && line.contains('action=search_sim');
	}

	@Override
	public SearchLogLine parseLine(String line) {
		def m = patternLine.matcher(line)

		if (!m.matches()) return null;

		String qStr = URLDecoder.decode(m[0][4],'UTF-8')
		def mq = patternQuery.matcher(qStr)
		List terms
		if(mq.matches()){
			terms = mq[0][1].split(/\s+/).sort()
		}else{
			terms = qStr.split(/\s+/).sort()
		}


		SearchLogLine sll = [
					source:source,
					date: Date.parse("y-M-d H:m:s", m[0][1]),
					sessionId:m[0][2],
					userId:m[0][2],
					language:m[0][3],
					origQuery: qStr,
					termList:terms
				]
		sll
	}

	static transactional = true

}
