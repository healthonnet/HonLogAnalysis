<g:applyLayout name="main">
<h2>nb queries: ${nbTotal }</h2>
<h2>nb IP addresses: ${nbIp }</h2>

<g:render template="blocks/gv-geomap" model="${[countBy:countBy, title:'Count by country', height:600, width:1000] }"></g:render>
</g:applyLayout>