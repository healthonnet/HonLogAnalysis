HON Log Analysis
=========================

Developers
-----------

- William Belle
- Nolan Lawson
- Alexandre Masselot
- João Palotti
- Daniela Sinjari
- Samia Chahlal

[Health On the Net Foundation][6]

License
-----------

[LGPL 3.0][8].

Summary
-----------

HON Log Analysis is a Grails web application that analyzes HON search logs and provides statistics and visualizations.

![Screenshot][3]

Configuration
--------------

The webapp is mainly configured using a config.properties file bundled into the WAR file. The configuration file specifies the webapp home directory and the MySQL database location. The build.xml task will handle the bundling of various configuration files for you. A configuration file might looks like this:

```
# location of the Tomcat webapp
grails.serverURL=http://mycoolsite.com/hon-log

# MySQL configuration
dataSource.url=jdbc:mysql://127.0.0.1:3306/hon_log?useUnicode=yes&characterEncoding=UTF-8
dataSource.username=hon_log
dataSource.password=hon_log

# default filedir to use for the bulk uploading if none is specified
honlogDefault.filedir=/path/to/my/logs/dir

# default filter to use for the bulk uploading if none is specified
honlogDefault.filter=engine=myFilterText
```

Persistence
------------

HON Log Analysis uses a MySQL database to store loaded files, log lines, and query terms. It is assumed that the database is in UTF-8 format, so since MySQL defaults to Latin-1, be sure to create the database using:

```sql
create database hon_log character set utf8 collate utf8_general_ci;
create user 'hon_log'@'localhost' identified by 'hon_log';
grant all on hon_log.* to 'hon_log'@'localhost' identified by 'hon_log';
```

All changes to the database are handled by [Liquibase][12] using the [database-migration][13] plugin.
Place changesets in the ```grails-app/migrations``` folder.

Build instructions
----------

If in a development environment, create a **myname**-dev.properties file with the necessary configuration, then use like a normal Grails project.

For building for a production environment, create or choose a configuration file and run e.g.

```
ant -Dgrails.home=$GRAILS_HOME -Dconfig.file=my-config-file.properties
```

This will build the file **hon-log.war**.

Log Analysis
--------------

The app analyzes three types of logs: "hon," "db," and "tel". Below are some examples of each.

Type "hon" (HON's main log type):
```
<<remoteIp=88.74.122.100>><<usertrack=88.74.122.100.1323558003433727>><<time=[11/Dec/2011:00:00:04 +0100]>><<query=?engine=honSelect&search=Genitalkrankheiten%2C+m%C3%A4nnliche&EXACT=0&TYPE=1&action=search>><<referer=http://www.hon.ch/HONselect/Selection_de/C12.294.html>>
```

Type "db":
```
2007-01-01 00:05:50 GET /portal/telstat.php sesid=rmmthflrghp6mava5j7v6po4e3&lang=en&query=("boys%20be")&action=search_sim&colid=&objurl= 70.180 Mozilla/4.0+(compatible;+MSIE+7.0;+Windows+NT+5.1;+.NET+CLR+1.1.4322) cTargets=collections:a0000,collections:a0037,collections:a0200,collections:a0141,collections:a0010,collections:a0035,collections:a0086,collections:a0132,collections:a0067,collections:a0001,collections:a0062,collections:a0130,collections:a0163,collections:a0211,collections:a0194,collections:a0075,collections:a0073,collections:a0066;+TELSESSID=rmmthflrghp6mava5j7v6po4e3;+AreCookiesEnabled=299;+cTargetsThemes=theme0;+lastviewed=0 http://www.theeuropeanlibrary.org/portal/index.html
```

Type "tel":
```
100001,guest,194.171,2B09B2A6EDA8343B0B14DD2EB764ABA3,en,"(""cowboy"")",view_full,a0056,1,1,,"http://search.theeuropeanlibrary.org/portal/search/collections/a0056/(""cowboy"").query?position=1",2009-03-23 00:00:00.0
```

When uploading a log file, click to Log Files -> Upload New -> and then specify the file type when you upload.

Uploading multiple files at once
---------------------------------

![Screenshot][1]

To "bulk upload" multiple files, choose a directory that is on the local machine that contains log files. Currently, bulk uploading only supports "hon"-type log files, so all other log file types will be ignored. If a log with the same filename as a previously-loaded log file is found, it is ignored.

When you know the directory on the local machine, click to Log Files -> Bulk Load -> and then enter the directory name. If there are a lot of files, this could take a long time. Currently the webapp does not print progress to the page itself, but while the page is loading, you can do:

```
tail -f <tomcat_directory>/logs/catalina.out
```

from the shell to see the progress of each log file. You'll see output like this:

```
2012-05-16 09:51:01,913 [http-8097-6] INFO  loader.SearchLogLoaderService  - Loading znverdi.honsearch_log.20120509...
2012-05-16 10:02:57,699 [http-8097-6] INFO  loader.SearchLogLoaderService  - Loaded 3051 lines in znverdi.honsearch_log.20120509 in 715785 ms.
2012-05-16 10:02:57,777 [http-8097-6] INFO  loader.SearchLogLoaderService  - Loading znverdi.honsearch_log.20120510...
2012-05-16 10:14:58,208 [http-8097-6] INFO  loader.SearchLogLoaderService  - Loaded 2796 lines in znverdi.honsearch_log.20120510 in 720431 ms.
2012-05-16 10:14:58,341 [http-8097-6] INFO  loader.SearchLogLoaderService  - Loading znverdi.honsearch_log.20120511...
```

The loading will continue even if you close the window that you originally did the request in. Expect a year's worth of logs to take about 12 hours to complete.

WARNING: this loading process is not concurrency-safe, because the Hibernate optimistic locking (with the "version" column) was removed to improve performance. So only one person should load files at the same time!

You can also optionally specify a "filter," which will simply check each log line and only include log lines that contain the filter.

Defaults for "filter" and "filedir" are specified in the configuration file; if these are left blank, then the defaults will be used.

This service can also be called from the command line using e.g.:

```
curl 'http://localhost:8080/hon-log/load/bulkLoad?filedir=&filter=&doAction=true
```

Exporting the data
-------------------

To export a table of term + count to csv format, log into MySQL and do:

```sql
select t.value,count(*) as count
   into outfile '/tmp/terms_with_counts.csv' 
   fields terminated by ',' optionally enclosed by '"' 
   lines terminated by '\n' 
   from term t, search_log_line_terms sllt 
   where t.id=sllt.term_id 
   group by t.id having count(*) >= 10 
   order by count desc ;
```

To get the raw queries and their counts, log into MySQL and do:

```sql
select orig_query,count(*) as count 
   into outfile '/tmp/raw_queries_with_counts.csv' 
   fields terminated by ',' optionally enclosed by '"' 
   lines terminated by '\n' 
   from search_log_line 
   group by orig_query having count(*) >= 2 
   order by count desc ;
```

To get raw queries and their counts based on the number of terms, you can run:

```sql
select orig_query, count(*) from (select orig_query
   from search_log_line sll, search_log_line_terms sllt
   where sllt.search_log_line_id = sll.id
   group by sll.id having count(sllt.term_id) = 3
) queries
group by orig_query having count(*) >= 2
order by count(*) desc
```

(to get e.g. all queries with 3 tokens that appear at least twice)

More screenshots
----------

![Screenshot][2]

![Screenshot][4]

Third-party credits
-------------------

Thanks to [Pastel SVG Icons][14] for the graphics.

[1]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/multi_upload_screenshot.png
[2]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot01.png
[3]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot02.png
[4]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot03.png
[6]: http://www.hon.ch
[8]: http://www.gnu.org/copyleft/lesser.html
[12]: http://www.liquibase.org/
[13]: http://grails.org/plugin/database-migration
[14]: http://codefisher.org/pastel-svg/
[15]: http://famfamfam.com/
