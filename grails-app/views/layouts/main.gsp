<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="search logs analyzer" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

        <script type="text/javascript" src="http://www.google.com/jsapi"></script>

        <g:layoutHead />

        <r:require modules="core"/>
        <r:layoutResources/>
        
    </head>
    <body>
    
        <div class="span-24">
            <div class="span-5">
            <div class="span-5">
                <g:render template="/blocks/menu-log-files"/>
            </div>
            </div>
            <div class="span-5">
                <g:render template="/blocks/menu-statistics"/>
            </div>
            <div class="span-5">
                <g:render template="/blocks/menu-details"/>
            </div>
        </div>
        <div  class="span-24">
        <g:layoutBody />
        </div>

        <div class="footer span-24">
            <a href='http://github.com/healthonnet/HonLogAnalysis' target='_blank'>HON Log Analysis</a> version
            <g:meta name="app.version" />, built on <a href='http://grails.org' target='_blank'>Grails</a>
            <g:meta name="app.grails.version" />. <a href='http://www.hon.ch/Global/privacy_policy.html'
                 target='_blank'>See our privacy policy.</a>
        </div>


        <r:require modules="core"/>
        <r:layoutResources/>



    </body>
</html>