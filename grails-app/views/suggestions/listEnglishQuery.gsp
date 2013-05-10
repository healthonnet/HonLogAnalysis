<g:applyLayout name="main">
	<br>
	<h2>
		Nb. of queries:
		${nbTotalEnglishQuery }
	</h2>
	<br>

	<div id="query">
		<table style="width: 600px;" cellpadding="0" cellspacing="0"
			border="0" id="table">
			<%--			Ligne avec les intitulés des colonnes du tableau--%>
			<thead>
				<tr>
					<th>Query</th>
				</tr>
			</thead>

			<tbody>
				<%--			Pour trier les requêtes selon le nombre d'occurences de chacunes dans l'ordre décroissant --%>
				<g:each in="${EnglishQueryList.entrySet()}" var="query">
					<tr>
						<td>
							<%--						Première colonne: affichage des clés--%> 
							${query.key}
						</td>
						<td style="text-align: center">
							<%--                      Deuxième colonne: affichage des valeurs--%>
							${query.value}
						</td>
					</tr>
				</g:each>
			</tbody>

		</table>
	</div>
</g:applyLayout>