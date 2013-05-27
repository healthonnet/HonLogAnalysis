<!DOCTYPE html>
<html>
<head>
<title>HON Log Analysis Search</title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<script type="text/javascript" src="http://www.google.com/jsapi"></script>

<r:require modules="core" />
<r:layoutResources />

</head>

<body>
   <g:form controller="suggestions" action="index">
	<div class="span-24 contents">
		<h1>HON Log Analysis</h1>

		<div class="span-24">
			<div class="span-6">
					<label>Language: </label>      
					<input type="hidden" name="paction" value="listQuery">
					<select name="language" id="suggestions-menu">
						<option ${params.language=="en" ? 'selected="selected"':''} value="en">English</option>
						<option ${params.language=="ar" ? 'selected="selected"':''} value="ar">Arabic</option>
					</select>
			</div>

            <div class="span-6">
                <label>Limit: </label> 
                <select name="limit">
                    <option ${!(params.limit) ? 'selected="selected"':''} value="10">10</option>
                    <option ${params.limit=="50" ? 'selected="selected"':''} value="50">50</option>
                    <option ${params.limit=="100" ? 'selected="selected"':''} value="100">100</option>
                </select>
            </div>

            <div class="span-6">
		         <label>Display: </label> 
		         <select
					style="width: 60px;" name="display">
					<option ${!(params.display) ? 'selected="selected"':''} value="HTML">HTML</option>
					<option ${params.display=="XML" ? 'selected="selected"':''} value="XML">XML</option>
					<option ${params.display=="JSON" ? 'selected="selected"':''} value="JSON">JSON</option>
				</select>
			</div>
		 
            
            <div class="span-9">
                <label>Query: </label> 
                <input type="text" name="query" > 
                <g:submitButton name="Search" />
            </div>  
       </div>
    </div>
   </g:form>
</body>