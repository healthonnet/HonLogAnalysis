<g:form controller="suggestions" action="index">
    <input type="hidden" name="paction" value="listQuery">

    <select class="menu" name="language" id="suggestions-menu">
        <option ${!(params.language) ? 'selected="selected"':''}>Suggestions</option>
        <option ${params.language=="en" ? 'selected="selected"':''} value="en">English</option>
        <option ${params.language=="ar" ? 'selected="selected"':''} value="ar">Arabic</option>
    </select>

    <label>Query: </label>
    <input type="text" name="query" value="${params.query}" />

    <label>Display: </label>
    <select style="width: 60px;" name="display">
        <option ${!(params.display) ? 'selected="selected"':''} value="HTML">HTML</option>
        <option ${params.display=="XML" ? 'selected="selected"':''} value="XML">XML</option>
        <option ${params.display=="JSON" ? 'selected="selected"':''} value="JSON">JSON</option>
    </select>
    
    <label>Limit: </label>
    <select name="limit">
        <option ${!(params.limit) ? 'selected="selected"':''} value="10">10</option>
        <option ${params.limit=="50" ? 'selected="selected"':''} value="50">50</option>
        <option ${params.limit=="100" ? 'selected="selected"':''} value="100">100</option>
    </select>

</g:form>








