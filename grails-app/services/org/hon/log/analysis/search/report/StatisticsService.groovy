package org.hon.log.analysis.search.report


import java.util.Map

import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

import com.maxmind.geoip.Location;

class StatisticsService {
	GeoIpService geoIpService
	static transactional = false

	/**
	 * 
	 * @return the total number of queries done counted by language
	 */
	Map countByLanguage() {
		def ret=[:]
		SearchLogLine.withCriteria {
			projections {
				groupProperty("language")
				countDistinct("id", 'cptTerms');
			}
			order('cptTerms', 'desc')
		}.each{
			ret[it[0]]=it[1]
		}
		ret
	}
	/**
	 * 
	 * @return the total number of queries counted by query length (number of terms)
	 */
	List countByQueryLength() {
		def m = SearchLogLine.withCriteria {
			projections {
				groupProperty("nbTerms")
				countDistinct("origQuery");
			}
			order('nbTerms', 'asc')
		}
		def ret = []
		m.each{ ret[it[0]]=it[1] }
		ret
	}

	/**
	 *
	 * list the most used terms
	 *
	 * options.limit the max number terms (50 default)
	 *
	 * @return a sort map term->count
	 */
	Map mostUsedTerms(options = [:]){
		def l = Term.list().sort({-it.searchLogLines.size()})
		
		int lim = options.limit?:50
		int min =-1
		if(l.size()>lim){
			min = l[lim-1].size()
		}
		Map tcount = [:]
		l.grep({it.size()>=min}).each{
			tcount[it.value]=it.size()
		}
		tcount
		
	}

	Map countriesByTerm(term){
		if (term==null || term=="")
		return null
		println term
			Map ret=[:]
			List temp=[]
			SearchLogLine.list().each{SearchLogLine sll ->
				if(sll.termList.contains(term) && !temp.contains(sll.remoteIp)){
					Location location = geoIpService.getLocation(sll.remoteIp)
					temp<<sll.remoteIp
					String country=location?.countryName?:'unknown';
					if(ret[country]==null)
						ret[country]=1;
					else
						ret[country]++
					}
					
				}		
			
		ret
	}
	
	Map mostFrequentTermCoOccurence(options=[:]){
		Map count = [:]
		SearchLogLine.list().each{SearchLogLine sll ->
			List terms = sll.termList.sort();
			int max= terms.size()-1
			terms.eachWithIndex{t1, i ->
				if(i==max){
					return
				}
				((i+1)..max).each{ j ->
					String t2=terms[j]
					String k="$t1|$t2";
					if(count[k]){
						count[k]++
					}else{
						count[k]=1
					}
				}
			}
		}
		keepMostFamous(count.sort{-it.value}, options.limit?:50)
	}

	Map countByCountry(options=[:]){
		Map count=[:]
		Map cache=[:]
		int nbIp = 0
		SearchLogLine.list().each{SearchLogLine sll ->
			if (!sll.remoteIp || sll.remoteIp=='-')
				return null
			if(!cache[sll.remoteIp]){
				Location location = geoIpService.getLocation(sll.remoteIp)
				cache[sll.remoteIp]=location?.countryCode?:'unknown'
				nbIp++
			}
			String cc = cache[sll.remoteIp]
			if(cc == 'unknown'){
				return
			}
			count[cc]=count[cc]?(count[cc]+1):1;
		}
		[nbIp:nbIp, count:count]
	}


	/**
	 * remove all map member with values with are below the limit^th threshold
	 * @param m
	 * @param limit
	 * @return
	 */
	Map keepMostFamous(Map m, int limit){
		if(m.size()<=limit){
			return m
		}
		def lim = m.values().toList()[limit-1]
		Set remove = []
		m.each{t, n ->
			if(n<lim)
				remove.add(t)
		}
		remove.each{m.remove(it)}
		m
	}
}
