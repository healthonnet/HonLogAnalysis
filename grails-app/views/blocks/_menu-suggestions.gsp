<g:form controller="suggestions" action="listQuery2">
	<select class="menu" name="search" id="suggestions-menu">
		<option ${params.search=="no" ? 'selected="selected"':''} value="no">Suggestions</option>
		<option ${params.search=="yes" ? 'selected="selected"':''} value="yes" action="listQuery2">Search</option>
	</select>
</g:form>

