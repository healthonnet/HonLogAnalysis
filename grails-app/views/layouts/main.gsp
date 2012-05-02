<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="search logs analyzer" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:javascript library="jquery" plugin="jquery"/>
        <r:require module="jquery-ui"/>
        <r:require module="blueprint"/>
        <r:layoutResources/>
        
        
        <script src="${g.resource(dir:'js/jquery-ui', file:'ui.selectmenu.js')}"></script>
        <link href="${g.resource(dir:'css/jquery-ui', file:'ui.selectmenu.css')}" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="http://www.google.com/jsapi"></script>
        
        <g:layoutHead />
        
    </head>
    <body>
          <r:layoutResources/>
    
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
        
        <jq:jquery>
            $('select.menu').selectmenu({style:'dropdown'});
            $('select.menu').change(function(){
                $(this).parent().submit();
            });
        </jq:jquery>
    </body>
</html>