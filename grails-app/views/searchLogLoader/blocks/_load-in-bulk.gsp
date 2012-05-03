<h2>Load files in bulk</h2>
<fieldset class="form">
	<g:form action="bulkLoad" method="GET">
		<div class="fieldcontain">
			<label for="filedir">Enter local directory containing log files:</label>
			<g:textField name="filedir" value="${params.filedir}" />
			<g:submitButton name="Load" id="go"/>
		</div>
	</g:form>
</fieldset>
<pre>
	${output}
</pre>