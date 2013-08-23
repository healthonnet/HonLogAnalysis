<g:applyLayout name="main">
	<br>
	<h2>
		Nb. of queries:
		${nbTotal}
	</h2>
	<br>

	<div id="query">
		<table style="width: 600px;" cellpadding="0" cellspacing="0"
			border="0" id="table">
			<%--			Ligne avec les intitulés des colonnes du tableau--%>
			<thead>
				<tr>
					<th>Query</th>
					<th style="text-align: center">Occurrence of query</th>
				</tr>
			</thead>

			<tbody>
				<%--			Pour trier les requêtes selon le nombre d'occurences de chacunes dans l'ordre décroissant --%>
				<g:each in="${queryList}" var="query">
					<tr>
						<td>
							<%--						Première colonne: affichage des clés--%> 
							${query.term}
						</td>
						<td style="text-align: center">
							<%--                      Deuxième colonne: affichage des valeurs--%>
							${query.counter}
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</g:applyLayout>
