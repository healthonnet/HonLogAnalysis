package org.hon.log.analysis.search.loader

import java.io.File;

import grails.test.*

import org.hon.log.analysis.search.LoadedFile
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term;
import org.springframework.core.io.ClassPathResource

class SearchLogLoaderServiceIntegrationTests extends GroovyTestCase {
	SearchLogLoaderService searchLogLoaderService

	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_service_alive() {
		assert searchLogLoaderService
	}

	void test_loaders_list(){
		assert searchLogLoaderService.loaders.size() == 3
	}
	
	void test_check_loaded_files(){
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.txt').file)
		
		List lf = LoadedFile.list();
		assert lf.size() == 1
		LoadedFile f = lf[0]
		assert f.filename.endsWith('tel-1.txt')
		assert f.size() == 22
		assert f.loadedAt
		
		SearchLogLine.list().each{println it}
		
	}
	
	void test_dbs_loading() {

		int n = searchLogLoaderService.load('dbs', testFile('dbs-1.txt'), [origFile:'dbs-1.txt'])
		assert n == 11
		assert SearchLogLine.count() == 11

		LoadedFile loadedfile = LoadedFile.findByFilename('dbs-1.txt')
		assert loadedfile
		assert loadedfile.size() == 11

		SearchLogLine sll = SearchLogLine.list()[2]
		assert sll
		assert sll.terms.size()>0
	}

	void test_tel_loading() {
		int n = searchLogLoaderService.load('tel', testFile('tel-1.txt'))
		assert n == 22
		assert SearchLogLine.count() == 22
	}
	
	void test_hon_loading() {
		int n = searchLogLoaderService.load('hon', testFile('hon-1.txt'))
		assert n == 96
		assert SearchLogLine.count() == 96
	}
	
	void testNoDuplicateTerms() {
		
		// test to make sure terms aren't added more than once for the same string
		
		int n = searchLogLoaderService.load('hon', testFile('test_duplicates.txt'))
		
		assert n == 2
		assert SearchLogLine.count() == 2
		assert Term.count() == 2
		SearchLogLine.findAll().each() { searchLogLine ->
			assert searchLogLine.terms.size() == 2;
		}
	}

	void testDeleteById(){
		
		int nHon = searchLogLoaderService.load('hon', testFile('hon-1.txt'))
		int nTel = searchLogLoaderService.load('tel', testFile('tel-1.txt'))
		
		List lf = LoadedFile.list();
		assert lf.size() == 2
		LoadedFile f = lf[0]

		Map result = searchLogLoaderService.deleteById(f.id)
		assert result.filename.endsWith('hon-1.txt')
		assert result.deleted == nHon
		
		//Try to delete the same file twice
		Map result2 = searchLogLoaderService.deleteById(f.id)
		assert result2.deleted == 0
		
		//load the file list again and check if it decrease its size
		List lf2 = LoadedFile.list();
		assert lf2.size() == 1
	}
	
	void testDeleteAll(){
		
		searchLogLoaderService.load('hon', testFile('hon-1.txt'))
		searchLogLoaderService.load('tel', testFile('tel-1.txt'))
		searchLogLoaderService.load('tel', testFile('dbs-1.txt'))
		
		//Check if the files are loaded
		List lf = LoadedFile.list();
		assert lf.size() == 3
		
		int numberOfDeleted = searchLogLoaderService.deleteAll()
		assert numberOfDeleted == 3
		
		//check if the files were deleted
		List lfAfter = LoadedFile.list();
		assert lfAfter.size() == 0
		
	}
	
	File testFile(String filename){
		new ClassPathResource('resources/'+filename).file
	}
	
	void testSessions(){	
		searchLogLoaderService.load('hon', testFile('hon-session.txt'))
		List lf = SearchLogLine.list()
		assert lf[0].sessionId == lf[1].sessionId
		assert lf[1].sessionId == lf[2].sessionId
		assert lf[2].sessionId == lf[3].sessionId
		assert lf[3].sessionId != lf[4].sessionId
		assert lf[4].sessionId != lf[5].sessionId
		assert lf[3].sessionId != lf[5].sessionId		
	}
}
