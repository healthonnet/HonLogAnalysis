<g:applyLayout name="main">
<h2>Total Log Lines: ${nbTotal }</h2>
<h2>Total Unique IP Addresses: ${nbIp }</h2>

<g:render template="blocks/gv-geomap" model="${[countBy:countBy, title:'Count by country', height:600, width:1000] }"></g:render>
</g:applyLayout>