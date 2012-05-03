package org.hon.log.analysis.search.loader.impl

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Map
import java.util.regex.Pattern

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst


/**
 * Analyzer for HON-style log lines.  Accepts logs in a format given by files such as znverdi.honsearch_log.20120325.
 * @author nolan
 *
 */
class HonLoaderService extends SearchLogLineLoaderAbst{

	static transactional = true

	/**
	 * 
	 <<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:45:54 +0100]>><<query=?search=Arthrogrypose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?browse+C05.651.102>>
	 <<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:46:12 +0100]>><<query=?search=Maladies+articulaires&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?browse+C05.550>>
	 <<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:46:45 +0100]>><<query=?search=Arthralgie&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?browse+C05.550.091>>
	 <<remoteIp=82.234.160.53>><<usertrack=->><<time=[29/Nov/2011:08:47:29 +0100]>><<query=?search=Arthrite&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?browse+C05.550.114>>
	 <<remoteIp=78.112.65.35>><<usertrack=->><<time=[29/Nov/2011:08:48:41 +0100]>><<query=?search=Syndrome+de+Claude+Bernard-Horner&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/RareDiseases/FR/C10.177.350.html>>
	 <<remoteIp=85.4.104.217>><<usertrack=->><<time=[29/Nov/2011:08:50:45 +0100]>><<query=?search=relation+aide&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_f?search>>
	 <<remoteIp=129.143.71.36>><<usertrack=->><<time=[29/Nov/2011:08:50:45 +0100]>><<query=?search=Krankheitszeichen+und+Symptome&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_de/C23.888.html>>
	 <<remoteIp=187.18.200.5>><<usertrack=187.18.200.5.1322553057659472>><<time=[29/Nov/2011:08:51:00 +0100]>><<query=?search=Nefropatias&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_pt/C12.777.419.html>>
	 <<remoteIp=187.18.200.5>><<usertrack=187.18.200.5.1322553057659472>><<time=[29/Nov/2011:08:51:41 +0100]>><<query=?search=Nefrocalcinose&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect_pt?browse+C12.777.419.590>>
	 <<remoteIp=85.4.104.217>><<usertrack=->><<time=[29/Nov/2011:08:51:45 +0100]>><<query=?search=relation&EXACT=0&TYPE=1&action=search>><<referer=http://debussy.hon.ch/cgi-bin/HONselect?search>>
	 */

	final String source = 'hon'
	final Pattern patternQuery = ~/\bsearch=([^&]+?)&/
	final Pattern patternEngine = ~/\bengine=([^&]+?)&/
	final Pattern patternBlock = ~/<<(\w+)=(.*?)>>/
	

	@Override
	public void init(Map options) {
	}

	@Override
	public boolean acceptLine(String line) {
		return patternQuery.matcher(line).find();
	}

	Map parseQuery(String q){
		q = findUrlDecodedIfRegexMatches(patternQuery, q);
		return [terms:q.split(/[\s,:;]+/).sort()]
	}
	
	private String findUrlDecodedIfRegexMatches(Pattern pattern, String input) {
		def matcher = pattern.matcher(input);
		if (matcher.find()) {
			return URLDecoder.decode(matcher.group(1), 'UTF-8');
		}
		return null;
	}

	@Override
	public SearchLogLine parseLine(String line) {
		Map myLine2Map = line2Map(line)
		
		DateFormat formatter = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss]",new Locale("en,EN"));
		Date date = (Date)formatter.parse(myLine2Map.time.replaceAll(/\s+[\+\-]\d+/, ''));

		String rawQuery = myLine2Map.query;
		String engine = findUrlDecodedIfRegexMatches(patternEngine, rawQuery);
		String query = findUrlDecodedIfRegexMatches(patternQuery, rawQuery);
		
		Map q = parseQuery(myLine2Map.query)
		new SearchLogLine(
				source:source,
				sessionId:myLine2Map.usertrack,
				userId:myLine2Map.usertrack,
				date: date,
				termList:q.terms,
				origQuery:query,
				engine : engine,
				remoteIp: myLine2Map.remoteIp
				)
	}

	/**
	 * convert each <<name=value>> from the line into map element
	 * @param line
	 * @return
	 */
	Map line2Map(String line){
		Map ret = [:]
		line.eachMatch(patternBlock){m->
			ret[m[1]]=m[2]
		}
		ret
	}
}
