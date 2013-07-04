<g:applyLayout name="main">
    <resource:include components="autoComplete, font" autoComplete="[skin: null]" />
    <link rel="stylesheet"
        href="${resource(dir: 'css', file: 'richui_autocomplete.css')}"
        type="text/css">
  
  <g:form class="kaahe_form" action="checkQuery" method="GET">
		<input type="hidden" name="paction" value="checkQuery">
		
		<%--      Afin de sauter une ligne--%>
        <br/>

		<div class="span-9">
			<h3>Updating... </h3>
			<SCRIPT LANGUAGE="JavaScript"> alert('Update is complete!');
			</SCRIPT>
		</div>

	</g:form>
</g:applyLayout>