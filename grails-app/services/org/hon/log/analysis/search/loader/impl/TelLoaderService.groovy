package org.hon.log.analysis.search.loader.impl

import org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst
import java.util.Map
import java.util.regex.Pattern

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst

class TelLoaderService extends SearchLogLineLoaderAbst{
	final String source = 'tel'

	//100778,guest,161.53,6DF31EF9B4CF4B7513B077217BC99C41,en,"(""science and nature"")",view_full,a0073,1,1,,"http://search.theeuropeanlibrary.org/portal/search/collections/a0073/(""science and nature"").query?position=1",2009-03-23 00:00:00.0
	Pattern patternLine = ~/(\d+),(\w*),([\d\.]+),(\w+),(\w+),"(\(.*\))",(view_full|see_online),\w+,(\d+),(\d+).*/
	Pattern patternQuery = ~/^\(""(.*)""\)$/ 
	Pattern patternQueryTopics = ~/\(([\w+\s]+?) ""(.*?)""\)/ 
	//def  pattern = ~/([0-9]+).*/
	
	@Override
	public void init(Map options) {

	}

	@Override
	public boolean acceptLine(String line) {
		return line.contains(',view_full,') || line.contains(',see_online');
	}
	
	Map parseQuery(String q){
		def mq = patternQuery.matcher(q)
		if(mq.matches()){
			return [terms:mq[0][1].split(/\s+/).sort()]
		}
		mq = patternQueryTopics.matcher(q)
		if(mq.matches()){
			List topics=[]
			HashSet qset = new HashSet()
	        mq.each{match->
				match[1].split(/\s+/).each{topics.add(it)}
				qset.addAll(match[2].split(/\s+/))
			}
				return [topics:topics.sort().unique(), terms:qset.toList().sort()]
		}
		
		[terms:q.split(/\s+/).sort()]

	}

	@Override
	public SearchLogLine parseLine(String line) {
		def m = patternLine.matcher(line)

		if (!m.matches()) return null;
		
		String qStr = m[0][6]
		Map q = parseQuery(qStr)
		SearchLogLine sll = new SearchLogLine(
				source:source,
				sessionId:m[0][4],
				userId:m[0][4],
				language:m[0][5],
				origQuery:qStr,
				termList:q.terms,
				date:new Date(m[0][1].toInteger()),
				topics:q.topics,
				)
		
		associateSearchLogLineWithRemoteIp(sll, m[0][3])
		return sll
	}

	static transactional = true

}
