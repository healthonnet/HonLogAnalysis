<g:applyLayout name="main">
<head>
<title>countries by term</title>
</head>
<br>
<div>To obtain the country origins of a term,  please select a term below:</div>
    
<g:form controller="statistics" action="countriesByTerm">
<br>Ordered by most used terms:
<g:select id="termSelection"
		  name="term"
          from="${terms}"
          value="${params.term}"
          noSelection="['':'Please Select...']"    
          />
<g:submitButton name="search" value="Search" />
</g:form>

<g:render template="blocks/gv-geomap" model="${[countBy:countBy, title:'Count by country', height:600, width:1000] }"></g:render>
</g:applyLayout>