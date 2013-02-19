<h2>Loaded files</h2>

<table>
	<thead>
		<tr>
            <th></th>
            <th>File name</th>
            <th style="text-align: center;">Size</th>
            <th>Date</th>
		</tr>
	</thead>
	<tbody>
	   <g:each in="${loadedFiles }" var="f">
	       <tr>
	           <td><g:link action="delete" id="${f.id }">delete</g:link></td>
               <td>${f.filename }</td>
               <td class="center">${f.size }</td>
               <td>${f.loadedAt }</td>
	       </tr>
	   </g:each>
	</tbody>
</table>

<h3> <g:link action="deleteAll"> Delete all files</g:link> </h3>