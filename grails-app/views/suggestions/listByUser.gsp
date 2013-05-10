<%--<g:applyLayout name="main">--%>
<%--<h2>Test:</h2>--%>
<%--<h3>Hello world!!!</h3>--%>
<%----%>
<%--</g:applyLayout>--%>

<%--<h3>Total nb of query logs: ${nbTotal }</h3>--%>
<%--<h3>Total nb. of queries: ${total }</h3>--%>
<%--<h3>Averaged nb. of queries per day: ${average }</h3>--%>
<%----%>
<%----%>
<%--</g:applyLayout>--%>


<g:applyLayout name="main">
	<br>
	<h2>Nb. of queries per user</h2>
	<br>
	<h3>
		Nb of logs:
		${nbTotal }
	</h3>
	<h3>
		Nb. of distinct userId found in logs:
		${nbUserId}
	</h3>


	<div id="users">
		<table style="width: 600px;" cellpadding="0" cellspacing="0"
			border="0" id="table">
			<thead>
				<tr>
					<th>User</th>
					<th style="text-align: center">Nb. of queries</th>
					<th>Details</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${userList.entrySet()}" var="user">
					<tr>
						<td>
							${user.key}
						</td>
						<td style="text-align: center">
							${user.value}
						</td>
						<td><g:link controller="details" action="listDetailsByUser"
								id="${user.key}">View Details</g:link></td>
					</tr>

				</g:each>
			</tbody>
		</table>
	</div>
</g:applyLayout>