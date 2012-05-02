<g:applyLayout name="main">
<h2>Nb of logs: ${nbTotal }</h2>

<g:render template="blocks/gv-table" model="${[userTerms:userList, height:600, width:900] }"></g:render>
</g:applyLayout>