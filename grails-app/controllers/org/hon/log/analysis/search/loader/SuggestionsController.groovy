package org.hon.log.analysis.search.loader

import org.hon.log.analysis.*
import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.report.DetailsService;
import org.hon.log.analysis.search.report.SuggestionsService;
import grails.converters.*
import groovy.json.JsonSlurper
import java.lang.Object
import java.util.Map;
import java.*
import org.hon.log.analysis.search.report.StatisticsService;
import org.hon.log.analysis.search.query.Term

class SuggestionsController  {
    
    SuggestionsService  suggestionsService

    //Pour permettre la redirection vers la bonne page
    def index = {
        // TODO: don't repeat this logic for every controller
        if(params.paction){
            def paction = params.paction
            params.remove('paction')
            if(paction != 'index')
                redirect(action:paction, params: [query:params.query,language:params.language, display:params.display, limit:params.limit])
        }
    }

    def listQuery2={
    }

    //Fonction permettant de donner la liste des termes présents selon le mode d'affichage choisi
    def listQuery(){   
        if(params.q ==""){
        def listResult = suggestionsService.listQuery(params.queryWithoutAutosuggestions, params.language, params.limit.toInteger());
        //Choix du paramètre d'affichage: XML, JSON ou HTML
        if(params.display=="XML"){
            //Pour avoir de l'UTF8 pour l'affichage de l'écriture arabe
            response.setCharacterEncoding('UTF-8')
            render listResult as XML
        }else if (params.display=="JSON"){
            render listResult as JSON
        }else if (params.display=="HTML"){
            [   queryList:listResult,
                nbTotal:listResult.size()
            ]
        }
        }else{
            redirection()
            }
    }

    // Fonction permettant de fournir la liste des autosuggestions correspondant à la requête entrée dans le input
    def listAutosuggestions(){              
        def limit = params.limit ? params.limit.toInteger() : 5;
        def lang = params.languageKAAHE
        def q = params.q ?: params.query;
        def listResult = suggestionsService.listQuery(q, lang, limit);
		
		if ( params.wt == "json" ){
			render listResult as JSON
		} else {
	        //Proposition des requêtes
	        render (contentType: "text/xml") {
	            results() {
	                listResult.each { suggestion ->
	                    result(){ name(suggestion.term)
                            name(suggestion.counter)
                        }
	                }
	            }
	        };
		}
    }

    // Fonction permettant la redirection vers le site mobile de KAAHE ou le site web de KAAHE lors de lq sélection d'une autosuggestions dans le input de HONLogAnalysis
    def redirection(){
        def site= params.site
        def query=params.q
        def lang = params.language
        def lien 
        //Cas du site web
        if(site=="web"){
             lien= "http://kaahe.org/cgi/search.pl?chng_search=on&searchword="+ query.encodeAsURL() +"&l="+ lang + "&task=search&option=com_search&Itemid=5&searchwordprob=" + query.encodeAsURL() + "+"
        //Cas de la version mobile du site
        }else if(site=="mobile"){
             lien= "http://m.kaahe.org/fx/index_"+lang+".html"+"#"+lang+"_search.html?rl="+lang+"&search=" + query.encodeAsURL()
        }
        redirect(uri:lien)
    } 
    
    // Fonction permettant de mettre à jour la vérification de la pertinence des requêtes contenues dans les logs selon Solr
    def checkQuery(){     
        def list= suggestionsService.checkallqueries();
    }

}
