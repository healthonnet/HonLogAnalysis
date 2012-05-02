package org.hon.log.analysis.search.loader.impl

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Map
import java.util.regex.Pattern

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst


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
	Pattern patternQuery = ~/.*\bsearch=(.*?)&(EXACT|action=).*/
	Pattern patternBlock= ~/<<(\w+)=(.*?)>>/
//	Pattern patternRejectReferer = ~/.*<<referer=[^>]+\/(Selection\/|RareDiseases\/|HONselect\w*\?browse).*/
	Pattern patternRejectReferer = ~/.*<<referer=[^>]+(Selection\w*\/|RareDiseases\/|HONselect\w*\?browse).*/
	//Pattern patternRejectReferer = ~/.*<<referer=.*browse.*/
	

	@Override
	public void init(Map options) {
	}

	@Override
	public boolean acceptLine(String line) {
		if(line.indexOf("engine=honSelect")<0)
			return false
		if(line.indexOf("search=")<0)
			return false
		def m = patternRejectReferer.matcher(line)
		if(m.matches()){
			return false
		}
		return true
	}

	Map parseQuery(String q){
		def m = patternQuery.matcher(q)
		if(! m.matches()){
			return null
		}
		q=m[0][1]
		String qStr = URLDecoder.decode(q,'UTF-8')
		return [terms:qStr.split(/[\s,:;]+/).sort()]
	}

	@Override
	public SearchLogLine parseLine(String line) {
		Map els = line2Map(line)
		
		DateFormat formatter = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss]",new Locale("en,EN"));
		Date dt = (Date)formatter.parse(els.time.replaceAll(/\s+[\+\-]\d+/, ''));

		Map q = parseQuery(els.query)
		SearchLogLine sll=new SearchLogLine(
				source:source,
				sessionId:els.usertrack,
				userId:els.usertrack,
				date: dt,
				termList:q.terms,
				origQuery:els.query,
				remoteIp: els.remoteIp
				)
		sll
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
