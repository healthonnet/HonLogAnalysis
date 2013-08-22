package org.hon.log.analysis.search.report

import java.util.List;
import java.util.Map;
import org.exolab.castor.dsml.*
import org.exolab.castor.mapping.xml.Sql
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term
import com.maxmind.geoip.Location;


class SuggestionsService {
	static transactional = false
	javax.sql.DataSource dataSource;

	//Fonction permettant de verifier les requetes contenues dans les logs selon Solr avant de les proposer lors de l'autocompletion
	def checkallqueries(){

		def initial=new Sql(dataSource)

		//Remplissage de la table "allquerychecked" a partir de la table "search_log_line"
		initial.executeUpdate("""INSERT into
        allquerychecked (query, language, occurenceLog)
        select distinct LOWER(orig_query), language, count(orig_query) as counter
        from search_log_line
        group by orig_query""")

		def slurper   = new JsonSlurper()
		def db        = new Sql(dataSource)
		def solrProxy = "http://www.kaahe.org/en/utils/solr-proxy-dev.php"
		def language  = ['en', 'ar']

		language.each {

			//Recuperation des requetes arabes
			String queryArabic = """select query from allquerychecked 
				where language='""" + it + """'"""

			//Liste contenant les requetes arabes distinctes contenues dans search_log_line
			List queryInitialArabic=db.rows(queryArabic).collect{[term: it[0]]};
			//Pour verifier le contenu de la liste
			for (int i = 0; i <queryInitialArabic.size(); i++) {
				//Recuperation d'un terme
				def queryToVerify = queryInitialArabic[i].term
				//Pour enlever les espaces avant et apres le mot
				def queryToVerifyTreated = queryToVerify.trim()
				// encode q parameter (utile pour l'URL)
				queryToVerifyTreated = java.net.URLEncoder.encode(queryToVerifyTreated, "UTF-8")
				//Verification de la requete dans Solr
				def lien = solrProxy + "?rows=0&wt=json&defType=edismax&qf=text_"+ it +"%20title_"+ it +"&q=" + queryToVerifyTreated
				def resultat = slurper.parseText(lien.toURL().text)
				//Recuperation du nombre de presence de ce terme dans KAAHE
				def counterSolr= resultat.response.numFound
				println("query arabe a verifier: " + queryToVerify)
				println("compteur arabe de requete dans Solr: " + counterSolr)
				//Mise a jour de la valeur de l'occurence dans Solr dans la table "allquerychecked"
				db.executeUpdate('update allquerychecked set occurenceSolr = ? where query=?', [counterSolr, queryToVerify])
				//Mise a jour de la valeur de la pertinence dans la table "allquerychecked"
				if(counterSolr>0){
					db.executeUpdate('update allquerychecked set relevance = ? where query=?', ["Yes", queryToVerify])
				}else{
					db.executeUpdate('update allquerychecked set relevance = ? where query=?', ["No", queryToVerify])
				}
			}
		}
	}

	//Fonction permettant d'afficher les autosuggestions selon le input rentre par l'utilisateur
	def listQuery(String queryEntry, String language, int limit){
		String query
		def db = new Sql(dataSource)

		query="""select allquerychecked.query, allquerychecked.occurenceLog
            from allquerychecked
            WHERE allquerychecked.query LIKE :queryEntry AND allquerychecked.relevance='Yes'
            order by allquerychecked.occurenceLog desc
            """
		return  db.rows(query, [queryEntry: queryEntry + "%", language: language], 0, limit).collect{
			[term: it[0]]}

	}
}

