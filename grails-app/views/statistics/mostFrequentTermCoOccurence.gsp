<g:applyLayout name="main">
<h2>Nb terms: ${nbTotal }</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Most frequent term co-occurence',height:2000, width:800] }"></g:render>
</g:applyLayout>