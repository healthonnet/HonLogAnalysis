package org.hon.log.analysis.search.loader

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term
import org.hon.log.analysis.search.report.StatisticsService

class StatisticsController extends LogAnalysisControllerAbst {
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
		Map result = statisticsService.countByCountry()	
		[
			nbTotal:result.totalCount,
			countBy:googleVisualizationDataFromCount(result.countryCodeCounts, [category:'country', label:'country name'])
		]
	}

	def countByReferer = {
		Map result = statisticsService.countByReferer()
		[
			nbTotal:result.totalCount,
			countBy:googleVisualizationDataFromCount(result.refererCounts, [category:'referer'])
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
			countBy:googleVisualizationDataFromCount(statisticsService.countriesByTerm(params.term), [category:'terms',label:'country name']),
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
	
	def countBySession = {
		Map result = statisticsService.countBySession()
		[
			nbTotal:result.totalCount,
			countBy:googleVisualizationDataFromCount(result.sessionCounts, [category:'session']),
		]
	}
	
	def mostUsedQueries = {
		Map result = statisticsService.countByQueries()
		[
			countBy:googleVisualizationDataFromCount(result.queryCounts, [category:'queries']),
			nbTotal:result.totalCount
		]
	}
}
