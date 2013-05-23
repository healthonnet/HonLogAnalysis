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
    def listQuery(String queryEntry, String language, int limit){

        String clause

        List sqlConstraints = []
        if (queryEntry){ sqlConstraints.add("search_log_line.orig_query LIKE :queryEntry ")
        }
        if (language){ sqlConstraints.add("search_log_line.language = :language")
        }

        clause = sqlConstraints.isEmpty() ? "" : ("WHERE " + sqlConstraints.join(" AND "))

        String query="""select distinct search_log_line.orig_query, search_log_line.language,
                    count(search_log_line.orig_query) as counter
            from search_log_line
            
            $clause
            
            group by search_log_line.orig_query
            order by counter desc
            """
        def db = new Sql(dataSource)

        //Sélection des listes (colonnes) que l'on veut afficher
        //TODO: ne codez pas en dur la limite
        return  db.rows(query, [queryEntry: queryEntry + "%", language: language], 0, limit).collect{
            [term: it[0], counter: it[2]]


        }
    }
}

