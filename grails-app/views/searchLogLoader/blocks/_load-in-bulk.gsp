<h2>Load files in bulk</h2>
<fieldset class="form">
	<g:form action="bulkLoad" method="GET" class="bulk-load-form">
		<div class="fieldcontain">
			<label for="filedir">Enter local directory containing log files (default: ${grailsApplication.config.honlogDefault.filedir}):</label>
			<g:textField name="filedir" value="${params.filedir}" />
			<br/><label for="filedir">Enter filter (default: ${grailsApplication.config.honlogDefault.filter}):</label>
			<g:textField name="filter" value="${params.filter}" />
			
			<br/><input type="submit" name="doAction" value="Do It!" />
		</div>
	</g:form>
    <div class="iframe-container" style="text-align:center;">
    <iframe class="iframe" id="bulk-load-frame" src="" width="90%" height="200" style="display:none;"></iframe>
    </div>
</fieldset>
<r:script>
(function($){
    HonLog.bulkLoad();
})(jQuery);
</r:script>