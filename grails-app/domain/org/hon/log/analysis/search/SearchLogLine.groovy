package org.hon.log.analysis.search

import org.hon.log.analysis.search.query.Term
import ar.com.fdvs.dj.domain.AutoText
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment

class SearchLogLine {

	String source
	String sessionId
	String userId
	String remoteIp
	String queryId
	String origQuery
	int nbTerms
	List<String> topics
	String language
	Date date
	
	static belongsTo = LoadedFile	
	static hasMany = [terms:Term]
	static StopWordRemover stopWordsRemover=[:]
	
	//def static reportable = [:]
	def static reportable = [
		fileName:['DetailsReport'],
		title:['Details of query logs'],
		columns: ['remoteIp','date','origQuery', 'terms','nbTerms'],
		columnTitles: ['remoteIp':'Remote IP','date': 'Date', 'origQuery': 'Original Query', 'terms':'Terms','nbTerms': 'Nb. of terms'],
		groupColumns: ['remoteIp','date'],
		autoTexts: [new AutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y, AutoText.POSITION_FOOTER, HorizontalBandAlignment.buildAligment(AutoText.ALIGMENT_CENTER), (byte)0, 200, 200)]
		
]
	
	
	void setTermList(List<String> ts){
		stopWordsRemover.cleanTerms(ts).unique().each{ 
			Term t = Term.findByValue(it)?:new Term(value:it)
			addToTerms(t)
		}
		nbTerms=terms?.size()?:0
	}
	List getTermList(){
		terms*.value?.toList()
	}

	static transients = ['queryLength', 'termList']

	static constraints = {
		sessionId nullable:true
		userId nullable:true
		queryId nullable:true
		topics nullable:true
		language nullable:true
		remoteIp nullable:true
		origQuery(maxSize:1000)
	}
	
	
	String toString(){
		"$id\t$userId\t$terms\t$topics\t$origQuery"
	}
}
