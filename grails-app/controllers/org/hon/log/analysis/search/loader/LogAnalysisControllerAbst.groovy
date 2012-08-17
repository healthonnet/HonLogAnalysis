package org.hon.log.analysis.search.loader

import java.util.Map;

abstract class LogAnalysisControllerAbst {
	protected Map googleVisualizationDataFromCount(Map countBy, Map options){
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
		if (options?.label) {
			ret.columns.add(			[
				'string',
				options?.label?:'label'
			]);
		}
		ret.values= []
		if(countBy in Map){
			if (options?.label) {
				countBy.each{k, v -> ret.values <<[k, v[0], v[1]]}
			} else {
				countBy.each{k, v -> ret.values <<[k, v]}
			}
		}else{
			countBy.eachWithIndex{v, i -> if(v) ret.values << [i, v]}
		}
		ret
	}
	
	protected Map googleVisualizationDetails(Map countBy, Map options){
		def ret =[:]
		ret.columns  = [
			[
				'string',
				options?.category?:'User'
			],
			[
				'string',
				options?.terms?:'Terms'
			]
		]
		ret.values= []
		if(countBy in Map){
			String temp;
			countBy.each{k, v ->
			   temp=''
			   if(!v)
				   return
				   
			   v.each{String str->
				   temp+=str+', '
				   }
			   temp=temp[0..-3]
			   
			
				ret.values <<[k, temp]}
		}else{
		
			countBy.eachWithIndex{v, i -> if(v) ret.values << [i, '', v]}
		}
		ret
	}
}