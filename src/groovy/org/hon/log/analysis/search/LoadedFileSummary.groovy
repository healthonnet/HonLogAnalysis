package org.hon.log.analysis.search

/**
 * Summarized version of a LoadedFile object that can be fetched more quickly from the database because it's not having
 * to load all the SearchLogLine objects when doing a simple count of them
 * @author nolan
 *
 */
class LoadedFileSummary {
		String filename
		Date loadedAt
		int size
		int id
}
