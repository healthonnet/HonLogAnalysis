<h2>Load files in bulk</h2>
<fieldset class="form">
	<g:form action="bulkLoad" method="GET">
		<div class="fieldcontain">
			<label for="filedir">Enter local directory containing log files (default: ${grailsApplication.config.honlogDefault.filedir}):</label>
			<g:textField name="filedir" value="${params.filedir}" />
			<br/><label for="filedir">Enter filter (default: ${grailsApplication.config.honlogDefault.filter}):</label>
			<g:textField name="filter" value="${params.filter}" />
			
			<br/><input type="submit" name="doAction" value="Do It!" />
		</div>
	</g:form>
</fieldset>
<pre>
	${output}
</pre>