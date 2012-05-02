<g:applyLayout name="main">
<h2>nb terms: ${nbTotal }</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Count by most used terms',height:3000, width:800] }"></g:render>
</g:applyLayout>