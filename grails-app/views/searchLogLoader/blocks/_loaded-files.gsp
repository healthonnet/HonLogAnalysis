<h2>Loaded files</h2>

<table>
	<thead>
		<tr>
            <th></th>
            <th class="left">file name</th>
            <th>size</th>
            <th class="left">date</th>
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