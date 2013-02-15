<g:applyLayout name="main">
<h2>nb queries: ${nbTotal }</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Count By Query Length'] }"></g:render>
</g:applyLayout>