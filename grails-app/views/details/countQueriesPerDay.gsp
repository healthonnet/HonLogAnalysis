<g:applyLayout name="main"><br>
<h3>Total nb of query logs: ${nbTotal }</h3>
<h3>Total nb. of queries: ${total }</h3>
<h3>Averaged nb. of queries per day: ${average }</h3>

<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Nb. of queries per day', height:600, width:1000] }"></g:render>
</g:applyLayout>