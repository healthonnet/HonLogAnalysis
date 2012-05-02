<g:applyLayout name="main">
<h2>nb queries: ${nbTotal }</h2>

<g:render template="blocks/gv-piechart" model="${[countBy:countBy, title:'Count By Language'] }"></g:render>
</g:applyLayout>