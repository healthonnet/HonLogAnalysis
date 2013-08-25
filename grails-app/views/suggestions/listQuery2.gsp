<g:applyLayout name="main">
    <resource:include components="autoComplete, font" autoComplete="[skin: null]" />
    <link rel="stylesheet"
        href="${resource(dir: 'css', file: 'richui_autocomplete.css')}"
        type="text/css">
        
<g:form class="kaahe_form" action="checkQuery" method="GET">
        <div class="span-9">
        <g:submitButton name="Update queries" action="${createLinkTo('dir':'suggestions/checkQuery')}"/>
        </div>
</g:form>
  
  <g:form class="kaahe_form" action="listQuery" method="GET">
		<input type="hidden" name="paction" value="listQuery">
		
<%--		Afin de sauter une ligne--%>
		<br/>
        <br/>

		<h3>Search with criterias: </h3>
		
		<div class="span-50">
			<div class="span-6">
				<label>Language: </label> <select name="language" id="lang">
					<option ${params.language=="en" ? 'selected="selected"':''} value="en">English</option>
					<option ${params.language=="ar" ? 'selected="selected"':''} value="ar">Arabic</option>
				</select>
			</div>

			<div class="span-6">
				<label>Limit: </label> <select name="limit">
					<option ${!(params.limit) ? 'selected="selected"':''} value="10">10</option>
					<option ${params.limit=="50" ? 'selected="selected"':''} value="50">50</option>
					<option ${params.limit=="100" ? 'selected="selected"':''} value="100">100</option>
				</select>
			</div>

			<div class="span-6">
				<label>Display: </label> <select style="width: 60px;" name="display">
					<option ${!(params.display) ? 'selected="selected"':''} value="HTML">HTML</option>
					<option ${params.display=="XML" ? 'selected="selected"':''} value="XML">XML</option>
					<option ${params.display=="JSON" ? 'selected="selected"':''} value="JSON">JSON</option>
				</select>
			</div>
			
			<div class="span-9">
                <label>Query: </label> 
                <input type="text" id="queryWithoutAutosuggestions" name="queryWithoutAutosuggestions" autocomplete="off">
                <g:submitButton name="Search" action="${createLinkTo('dir': 'suggestions/listQuery')}"/>
            </div>


			<div class="span-20">
		      <h4>_____________________________</h4>
		      <h3>Search with autosuggestions and redirection toward KAAHE: </h3>
		    </div>
		    		   			
            <div class="span-10">
                <label>Choix du site KAAHE: </label> <select name="site">
                    <option ${params.site=="web" ? 'selected="selected"':''} value="web">Version Web</option>
                    <option ${params.site=="mobile" ? 'selected="selected"':''} value="mobile">Version mobile</option>
                </select>
            </div>
				
		    <div class="span-20">
			     <label>Enter your query: </label>
			     <input type="hidden" name="paction" value="listAutosuggestions">
			     <richui:autoComplete id="relation-input" name="q"
					action="${createLinkTo('dir': 'suggestions/listAutosuggestions')}"
					onItemSelect="\$('.kaahe_form').submit()" />
			     <g:submitButton name="Search2" value="Search"/>
<%--					Ajout d'un paramètre supplémentaire--%>
<%--Lien de l'idée pour ajouter le paramètre: http://grails.1312388.n4.nabble.com/RichUI-Autocomplete-with-additional-parameters-td1368356.html--%>
			         <script type="text/javascript">
			         autoCompleteDataSource.scriptQueryAppend="language="+$("#lang").val();
				    </script>
		  </div>
</div>
</g:form>
</g:applyLayout>