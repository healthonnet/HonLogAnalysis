<g:applyLayout name="main">
<h2>nb of logs: ${nbTotal }</h2>

<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Nb. of terms by user', height:600, width:1000] }"></g:render>
</g:applyLayout>