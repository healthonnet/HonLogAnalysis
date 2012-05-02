package org.hon.log.analysis.search.loader

import org.hon.log.analysis.search.SearchLogLine;

abstract class SearchLogLineLoaderAbst {
	
	/**
	 * init this parser instance (date shift, etc)
	 * @param options
	 */
	abstract void init(Map options)
	
	/**
	 * 
	 * @return the source it is supposed to parse "MedHunt", "Tel" etc.
	 */
	abstract String getSource()
	
	/**
	 * 
	 * @param line
	 * @return true if the line is to be parsed
	 */
	abstract boolean acceptLine(String line)
	
	/**
	 * return a new {@link SearchLogLine} object from a given line
	 * @param line
	 * @return
	 */
	abstract SearchLogLine parseLine(String line)

}
