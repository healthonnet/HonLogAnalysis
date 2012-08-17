package org.hon.log.analysis.search.migration

import java.sql.PreparedStatement
import java.sql.ResultSet

import com.maxmind.geoip.LookupService
import com.maxmind.geoip.RegionName
import com.maxmind.geoip.TimeZone
import com.maxmind.geoip.Location

//import org.grails.geoip.service.GeoIpService;

import liquibase.change.custom.CustomSqlChange
import liquibase.database.Database
import liquibase.database.DatabaseConnection
import liquibase.statement.SqlStatement
import liquibase.statement.core.InsertStatement;
import liquibase.statement.core.UpdateStatement;
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

/**
 * Performs the migration into the ip_address table, adding in a country name for each one from the GeoIpService.
 * @author nolan
 *
 */
class IpAddressToLocationCustomSqlChange implements CustomSqlChange {

	// defined as geoip.data.resource in Config.groovy, need to re-specify here because no spring injection is possible
	private static final DATA_FILE_LOCATION = "/GeoLiteCity.dat";

	/**
	 * For each IP address, look up the location using the location lookup service, then put it in the ip addresses table.
	 * 
	 */
	public SqlStatement[] generateStatements(Database database) {

		LookupService lookupService = createLookupService();

		DatabaseConnection connection = database.getConnection();
		List updateStatements = [];
		PreparedStatement statement = connection.prepareStatement("select id, value from ip_address");
		ResultSet resultSet = statement.executeQuery();
		Map countries = [:];
		def countryIdCounter = 0;
		while (resultSet.next()) {
			int ipAddressId = resultSet.getInt(1);
			String ipAddressValue = resultSet.getString(2);

			Location location = getLocation(lookupService, ipAddressValue);
			String countryName = location?.countryName?:'unknown';
			String countryCode = location?.countryCode?:'unknown';
			
			// need to unique-ify the countries
			def currentCountryId;
			if (!countries[countryCode]) {
				currentCountryId = ++countryIdCounter;
				countries[countryCode] = [countryName, currentCountryId];
			} else {
			    currentCountryId = countries[countryCode][1];
			}

			UpdateStatement updateStatement = new UpdateStatement(null,'ip_address')
			updateStatement.addNewColumnValue("country_id",currentCountryId);
			updateStatement.setWhereClause("id=$ipAddressId")
			updateStatements.add(updateStatement)
		}
		
		def insertStatements = countries.collect { key, value ->
	            InsertStatement insertStatement = new InsertStatement(null, 'country')
				insertStatement.addColumnValue('country_code', key)
				insertStatement.addColumnValue('country_name', value[0])
				insertStatement.addColumnValue('id', value[1])
				return insertStatement
			 }
		
		// do inserts before updates
		List resultStatements = []
		resultStatements.addAll(insertStatements)
		resultStatements.addAll(updateStatements)
		
		return resultStatements;
	}

	
	
	public String getConfirmationMessage()  {
		"Successfully found all country codes for the ip addresses"
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

	
	/**
	 * We have to do all this crap manually, because I couldn't figure out how to get Spring to load the 
	 * GeoIpService.  Too bad.  - Nolan
	 * @return
	 */
	def createLookupService() {

		String fileName = getClass().getResource(DATA_FILE_LOCATION).getFile();
		LookupService lookupService = new LookupService(fileName);
		return lookupService
	}
	
	def getLocation(LookupService lookupService, String ip) {
		def location = lookupService.getLocation(ip);
		if (location) {
			location.regionName = RegionName.regionNameByCode(location.countryCode, location.region)
			location.timezone = TimeZone.timeZoneByCountryAndRegion(location.countryCode, location.region)
		}
		return location
	}

}
