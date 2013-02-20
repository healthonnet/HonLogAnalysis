<g:applyLayout name="main">
<h2>nb sessions: ${nbTotal }</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Count By Sessions'] }"></g:render>
</g:applyLayout>