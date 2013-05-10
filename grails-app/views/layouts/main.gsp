<!DOCTYPE html>
<html>
<head>
<title><g:layoutTitle default="HON Log Analysis" /></title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

<script type="text/javascript" src="http://www.google.com/jsapi"></script>

<g:layoutHead />

<r:require modules="core" />
<r:layoutResources />

</head>
<body>
	<div class="span-24 contents">

		<h1>HON Log Analysis</h1>

		<div class="span-24">
			<div class="span-7">
				<g:render template="/blocks/menu-log-files" />
			</div>
			<div class="span-7">
				<g:render template="/blocks/menu-statistics" />
			</div>
			<div class="span-7">
				<g:render template="/blocks/menu-details" />
			</div>
			<div class="span-5">
				<g:render template="/blocks/menu-suggestions" />
			</div>
			<div id="about-this-app" 
				style="height: 28px; display: table">
				<h4 style="display: table-cell; vertical-align: middle;">
					<a href='http://github.com/healthonnet/HonLogAnalysis#readme'
						target='_blank' title='About HON Log Analysis'>About</a>
				</h4>
			</div>
		</div>
		<div class="span-24">
			<g:layoutBody />
		</div>

		<div class="footer span-24">
			<a href='http://github.com/healthonnet/HonLogAnalysis'
				target='_blank'>HON Log Analysis</a> version
			<g:meta name="app.version" />
			, built on <a href='http://grails.org' target='_blank'>Grails</a>
			<g:meta name="app.grails.version" />
			. <a href='http://www.hon.ch/Global/privacy_policy.html'
				target='_blank'>See our privacy policy.</a>
		</div>

	</div>

	<r:require modules="core" />
	<r:layoutResources />



</body>
</html>