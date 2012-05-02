package org.hon.log.analysis.search.loader

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term
import org.hon.log.analysis.search.report.StatisticsService

class StatisticsController {
	StatisticsService statisticsService

	def index = {
		if(params.paction){
			def paction = params.paction
			params.remove('paction')
			if(paction != 'index')
				redirect(action:paction)
		}
	}

	def countByLanguage = {
		[
					nbTotal:SearchLogLine.count(),
					countBy:googleVisualizationDataFromCount(statisticsService.countByLanguage(), [category:'language'])
				]
	}
	def countByQueryLength = {
		[
					nbTotal:SearchLogLine.count(),
					countBy:googleVisualizationDataFromCount(statisticsService.countByQueryLength(), [category:'query length'])
				]
	}
	def countByCountry = {
		Map m = statisticsService.countByCountry()
		
		[
					nbTotal:SearchLogLine.count(),
					nbIp:m.nbIp,
					countBy:googleVisualizationDataFromCount(m.count, [category:'country'])
				]
	}

	def mostUsedTerms = {
		[	
			countBy:googleVisualizationDataFromCount(statisticsService.mostUsedTerms(), [category:'terms']),
			nbTotal:Term.count()
		]
	}
	
	def countriesByTerm = {

		[
			terms:statisticsService.mostUsedTerms().collect {it.key},
			countBy:googleVisualizationDataFromCount(statisticsService.countriesByTerm(params.term), [category:'terms']),
			nbTotal:Term.count()
		]
		//render(view:"countriesByTerm", template:"blocks/gv-geomap", model: map)
	}

	def mostFrequentTermCoOccurence = {
		[
					countBy:googleVisualizationDataFromCount(statisticsService.mostFrequentTermCoOccurence(), [category:'terms co-occurence']),
					nbTotal:Term.count()
				]
	}

	
	
	private Map googleVisualizationDataFromCount(countBy, Map options){
		def ret =[:]
		ret.columns  = [
			[
				'string',
				options?.category?:'category'
			],
			[
				'number',
				options?.count?:'count'
			]
		]
		ret.values= []
		if(countBy in Map){
			countBy.each{k, v -> ret.values <<[k, v]}
		}else{
			countBy.eachWithIndex{v, i -> if(v) ret.values << [i, v]}
		}
		ret
	}
}
