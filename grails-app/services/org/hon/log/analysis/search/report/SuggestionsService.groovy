package org.hon.log.analysis.search.report

import java.util.List;
import java.util.Map;
import org.exolab.castor.dsml.*
import org.exolab.castor.mapping.xml.Sql
import groovy.sql.Sql
import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term
import com.maxmind.geoip.Location;

class SuggestionsService {
    static transactional = false
    javax.sql.DataSource dataSource;

    //Fonction permettant d'afficher les autosuggestions selon le input rentré par l'utilisateur
    def listQuery(String queryEntry, String language){

        String clauseQuery = queryEntry ? "AND search_log_line.orig_query LIKE :queryEntry" : "";
        String clauseLang = language ? "AND search_log_line.language = :language" : "";

        String query = """
            select search_log_line.id, search_log_line.language,
                    search_log_line_terms.search_log_line_id, 
                    search_log_line_terms.term_id, search_log_line.orig_query, 
                    count(search_log_line_terms.term_id) as counter
            from search_log_line,search_log_line_terms
            where search_log_line_terms.search_log_line_id=search_log_line.id
 
            $clauseQuery
            $clauseLang

            group by search_log_line_terms.term_id
            order by counter desc
            """

        def db = new Sql(dataSource)
        //Sélection des listes (colonnes) que l'on veut afficher
        //TODO: ne codez pas en dur la limite 
        return  db.rows(query, [queryEntry: queryEntry + "%", language: language], 0, 100).collect{
            [term: it[4], counter: it[5]]
        }
    }
}

