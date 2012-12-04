package org.hon.log.analysis.search.loader.impl

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Map
import java.util.regex.Pattern

import org.grails.geoip.service.GeoIpService;
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
	final Pattern patternQuery = ~/\b(?:q|search)=([^&]+?)&/
	final Pattern patternEngine = ~/\bengine=([^&]+?)&/
	final Pattern patternBlock = ~/<<(\w+)=(.*?)>>/
	final Pattern patternDateCleanup = ~/\s+[\+\-]\d+/
	
	// DateFormat is not threadsafe
	final ThreadLocal<DateFormat> localDateFormat = new ThreadLocal<DateFormat>(){
		
		protected DateFormat initialValue() {
			return new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss]",new Locale("en,EN"));
		}
	};
	
	

	@Override
	public void init(Map options) {
	}

	@Override
	public boolean acceptLine(String line) {
		// the first test distinguishes from db-style log lines,
		// the second test tests if we can parse the query
		return line.contains("<<usertrack=") && patternQuery.matcher(line).find();
	}

	Map parseQuery(String q){
		return parseQuery(q, null);
	}
	
	Map parseQuery(String q, String engine){
		q = findUrlDecodedIfRegexMatches(patternQuery, q, engine);
		return [terms:q.split(/[\s,:;]+/).sort()]
	}
	
	private String findUrlDecodedIfRegexMatches(Pattern pattern, String input) {
		return findUrlDecodedIfRegexMatches(pattern, input, null);
	}
	
	private String findUrlDecodedIfRegexMatches(Pattern pattern, String input, String engineType) {
		def matcher = pattern.matcher(input);
		if (matcher.find()) {
			def unescapedQuery = matcher.group(1);
			if ("honCodeSearch".equals(engineType)) {
				// honCodeSearch is fucked up and url-encoded the query TWICE, just to piss me off - Nolan
				unescapedQuery = URLDecoder.decode(unescapedQuery, 'UTF-8');
			}
			return URLDecoder.decode(unescapedQuery, 'UTF-8');
		}
		return null;
	}

	@Override
	public SearchLogLine parseLine(String line) {
		Map myLine2Map = line2Map(line)
		
		DateFormat formatter = localDateFormat.get();
		Date date = (Date)formatter.parse(myLine2Map.time.replaceAll(patternDateCleanup, ''));

		String rawQuery = myLine2Map.query;
		String engine = findUrlDecodedIfRegexMatches(patternEngine, rawQuery);
		String query = findUrlDecodedIfRegexMatches(patternQuery, rawQuery);
		
		Map parsedQuery = parseQuery(myLine2Map.query, engine)
		
		def searchLogLine = new SearchLogLine(
				source:source,
				sessionId:myLine2Map.usertrack,
				userId:myLine2Map.usertrack,
				date: date,
				termList:parsedQuery.terms,
				origQuery:query,
				engine : engine,
				)
		
		associateSearchLogLineWithRemoteIp(searchLogLine, myLine2Map.remoteIp);
		
		return searchLogLine
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
