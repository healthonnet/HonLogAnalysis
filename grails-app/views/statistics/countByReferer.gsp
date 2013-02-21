<g:applyLayout name="main">
<h2># of unique referer URLs: ${nbTotal}</h2>
<g:render template="blocks/gv-barchart" model="${[countBy:countBy, title:'Count by most frequent referers'] }"></g:render>
</g:applyLayout>