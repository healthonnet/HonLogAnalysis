<g:applyLayout name="main">
<br>
<h2>Details of the user: ${user}</h2>
<h3>Country: ${country}</h3>
 <div id="det">
<table cellpadding="0" cellspacing="0" border="1" id="table">
<thead>
	<tr>
		<th style="text-align:right; width: 100px;">Time elapsed:</th>
		<th>Query Terms:</th>
	</tr>
</thead>
<tbody>
<g:each in="${details.entrySet()}" var="details">
<tr>
<td style="text-align:right;">${details.key}</td>
<td>${details.value}</td>
</tr>

</g:each>
</tbody>
</table>
</div>
</g:applyLayout>