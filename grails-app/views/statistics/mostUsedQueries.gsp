<g:applyLayout name="main">
<h2>nb queries: ${nbTotal }</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Count by most used queries',height:3000, width:800] }"></g:render>
</g:applyLayout>
