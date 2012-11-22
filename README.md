HON Log Analysis
=========================

Developer
-----------

- William Belle
- Nolan Lawson
- Alexandre Masselot
- Daniela Sinjari

[Health On the Net Foundation][6]

License
-----------

[LGPL 3.0][8].

Summary
-----------

HON Log Analysis is a Grails web application that analyzes HON search logs and provides statistics and visualizations.

![Screenshot][3]

Build instructions
----------

If in a development environment, create a **myname**-dev.properties file with the necessary configuration, then use like a normal Grails project.

For building for a production environment, create or choose a configuration file and run e.g.

```
ant -Dgrails.home=$GRAILS_HOME -Dconfig.file=my-config-file.properties
```

This will build the file **hon-log.war**.

More screenshots
----------

![Screenshot][2]

![Screenshot][4]

[2]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot01.png
[3]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot02.png
[4]: https://raw.github.com/healthonnet/HonLogAnalysis/master/doc/screenshot03.png
[6]: http://www.hon.ch
[8]: http://www.gnu.org/copyleft/lesser.html
