<h2>Upload new log file</h2>
<g:uploadForm action="upload">
    <input type="file" name="logfile"/><g:select name="type" from="${loaderTypes }"/>
    <g:submitButton name="load"/>
</g:uploadForm>