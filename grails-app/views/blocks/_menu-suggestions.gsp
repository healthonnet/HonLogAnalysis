<g:form controller="suggestions">
	<select class="menu" name="paction" id="suggestions-menu">
		<option value="suggestions" selected="selected">Suggestions</option>
		<option value="listEnglishQuery">English</option>
		<option value="listArabicQuery">Arabic</option>
	</select>
</g:form>

<%--Pour afficher le message indiquant que la requête a été bien entrée--%>
${flash.message}

<%--Rajouter controller="suggestions" permet d'aller à la méthode "listQueryBeginWithC" depuis n'importe quelle page où on se trouve--%>
<g:form controller="suggestions" action="listQueryBeginWithC">
	<label>Query: </label>
	<input type="text" name="query" />
</g:form>





