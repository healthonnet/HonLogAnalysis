<g:form controller="suggestions" action="index">
	<input type="hidden" name="paction" value="listQuery">
	<select class="menu" name="language" id="suggestions-menu">

		<option ${!(params.language) ? 'selected="selected"':''}>Suggestions</option>
		<option ${params.language=="en" ? 'selected="selected"':''} value="en">English</option>
		<option ${params.language=="ar" ? 'selected="selected"':''} value="ar">Arabic</option>

	</select>



	<%--Rajouter controller="suggestions" permet d'aller à la méthode "listQueryBeginWithC" depuis n'importe quelle page où on se trouve--%>
	<label>Query: </label>
	<input type="text" name="query" value="${params.query}" />
</g:form>






