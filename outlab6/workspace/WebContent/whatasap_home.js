/**
 * Sample javascript file. Read the contents and understand them, 
 * then modify this file for your use case.
 */

var myTable;
$(document).ready(function() {
//	myTable = $("#usersTable").DataTable({
//        columns: [{data:"uid"}, {data:"name"}, {data:"phone"}]
//    });
	
	originalTable();
    
    
    
    //load div contents asynchronously, with a call back function
//    alert("Page loaded. Click to load div contents.");
//	$("#content").load("content.html", function(response){
//		//callback function
////		alert("Div loaded. Size of content: " + response.length + " characters.");
//	});
});

function loadTableAsync() {
//    myTable.ajax.url("UsersInfo").load();
}

function originalTable(){
	$("#content").html("<table id=\"usersTable\"><thead>" +
    		"<tr><th>User ID</th><th>Last timestamp</th><th>Number of messages</th>" +
    		"</tr></thead></table>");
    
    
    myTable = $("#usersTable").DataTable({
    	"ajax" : "AllConversations",
    	"columns" : [
    		{"data" : "uid"},
    		{"data" : "last_timestamp"},
    		{"data" : "num_msgs"}
    	]
    });
    
    $('#usersTable tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
            myTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
        var other_id = myTable.row(this).data()["uid"];
        convDetails(other_id);
//        $('#content').html(myTable.row(this).data()["uid"]);
    } );
}

function convDetails(other_id){
	var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
	    if (this.readyState == 4 && this.status == 200) {
	    	var jsonobj = JSON.parse(this.responseText);
	    	var table = "<table><thead><tr><th>Text</th><th>Posted by</th><th>Timestamp</th></thead></tr>";
	    	for (data in jsonobj.data){
	    	  table += "<tr>";
	      	  table += "<td>" + jsonobj.data[data].text + "</td>";
	      	  table += "<td>" + jsonobj.data[data].uid + "</td>";
	      	  table += "<td>" + jsonobj.data[data].timestamp + "</td>";
	      	  table += "</tr>";
	    	}
	    	table += "</table>";
	    	$("#content").html(table);
	    }
	  };
	  xhttp.open("GET", "ConversationDetail?other_id=" + other_id, true);
	  xhttp.send();
}