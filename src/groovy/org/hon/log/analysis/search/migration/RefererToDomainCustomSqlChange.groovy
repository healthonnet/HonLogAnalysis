package org.hon.log.analysis.search.migration

import liquibase.change.custom.CustomSqlChange
import liquibase.database.Database
import liquibase.database.DatabaseConnection
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import liquibase.statement.SqlStatement
import liquibase.statement.core.InsertStatement
import liquibase.statement.core.UpdateStatement
import org.hon.log.analysis.search.util.URLUtil
import com.google.common.base.Preconditions;

import java.sql.PreparedStatement
import java.sql.ResultSet

import com.maxmind.geoip.LookupService
import com.maxmind.geoip.RegionName
import com.maxmind.geoip.TimeZone
import com.maxmind.geoip.Location

//import org.grails.geoip.service.GeoIpService;
/**
 * Performs the migration into the referer table, adding in a domain name for each one.
 * @author nolan
 *
 */
class RefererToDomainCustomSqlChange implements CustomSqlChange {

	/**
	 * For each referer, look up the domain and insert it into the domain table.
	 * 
	 */
	public SqlStatement[] generateStatements(Database database) {


		DatabaseConnection connection = database.getConnection();
		List updateStatements = [];
		PreparedStatement statement = connection.prepareStatement("select id, url from referer");
		ResultSet resultSet = statement.executeQuery();

        Map domains = [:];
		def domainIdCounter = 0;
		while (resultSet.next()) {
			int refererId = resultSet.getInt(1);
			String refererUrl = Preconditions.checkNotNull(resultSet.getString(2),
                    "referer with id $refererId is null!");

            String domain = URLUtil.getDomainFromUrl(refererUrl)

            if (domain == null) { // invalid domain
                continue;
            }

			// need to unique-ify the countries
			def currentDomainId = domains[domain];
			if (!currentDomainId) {
                currentDomainId = ++domainIdCounter;
				domains[domain] = currentDomainId;
			}

			UpdateStatement updateStatement = new UpdateStatement(null,'referer')
			updateStatement.addNewColumnValue("domain_id",currentDomainId);
			updateStatement.setWhereClause("id=$refererId")
			updateStatements.add(updateStatement)
		}
		
		def insertStatements = domains.collect { value, id ->
	            InsertStatement insertStatement = new InsertStatement(null, 'domain')
				insertStatement.addColumnValue('value', value)
				insertStatement.addColumnValue('id', id)
				return insertStatement
        }
		
		// do inserts before updates
		List resultStatements = []
		resultStatements.addAll(insertStatements)
		resultStatements.addAll(updateStatements)
		
		return resultStatements;
	}

	
	
	public String getConfirmationMessage()  {
		"Successfully inserted all the domains for the referers"
	}

	public void setFileOpener(ResourceAccessor resourceAccessor) {
		// do nothing
	}

	public void setUp() {
		// do nothing
	}

	public ValidationErrors validate(Database database) {
		// do nothing
		return null;
	}
}
