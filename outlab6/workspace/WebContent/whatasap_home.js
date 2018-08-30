/**
 * Sample javascript file. Read the contents and understand them, 
 * then modify this file for your use case.
 */

var myTable;
$(document).ready(function() {
//	myTable = $("#usersTable").DataTable({
//        columns: [{data:"uid"}, {data:"name"}, {data:"phone"}]
//    });
	alert("here0");
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
	alert("here1");
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
    alert("here");
    $("#searchinput").val("");
    $("#searchinput").autocomplete({source : "AutoCompleteUser"});
}

function convDetails(other_id){
	alert("herecd");
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
	    	table += "<br>New message:<br><form id=\"newmsg\">" +
	    			"Message: <input id=\"msginputfield\" type=\"text\"><br><input type=\"submit\"></form>";
	    	
	    	$("#content").html(table);
	    	$("#newmsg").on('submit', function(){
//	    		alert('here');
//	    		console.log('here');
	    		createNewMessage(other_id);
//	    		alert('fjsdlkfsdjf');
	    		return false;
	    	});
	    }
	  };
	  xhttp.open("GET", "ConversationDetail?other_id=" + other_id, true);
	  xhttp.send();
}

function createNewMessage(other_id){
//	alert('jflksjfkl');
	var xhttp = new XMLHttpRequest();
//	alert('heredsfsdfs');
	var msg = $("#msginputfield").val();
//	alert(msg);
	xhttp.onreadystatechange = function(){
		if (this.readyState == 4 && this.status == 200) {
	    	var jsonobj = JSON.parse(this.responseText);
	    	if (jsonobj.status){
	    		convDetails(other_id);
//	    		return false;
	    	}
	    	else{
	    		var error = jsonobj.message;
	    		alert(error);
//	    		return false;
	    	}
	    }
	}
	xhttp.open("GET", "NewMessage?other_id=" + other_id + "&msg=" + msg, true);
	xhttp.send();
	return false;
}

function createConversation(other_id) {
	var str = "<form id=\"conv_form\">" +
		"Enter ID <input type=\"text\" id=\"other_id\">" +
		"<br><input type=\"submit\">" +
		"</form>";
	document.getElementById("content").innerHTML = str;
	$("#conv_form").submit(function(){
		var other_id = $("#other_id").val();
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
		    	var jsonobj = JSON.parse(this.responseText);
		    	if (jsonobj.status){
		    		convDetails(other_id);
//		    		return false;
		    	}
		    	else{
		    		var error = jsonobj.message;
		    		alert(error);
//		    		return false;
		    	}
		    }
		};
		xhttp.open("GET", "CreateConversation?other_id=" + other_id, true);
		xhttp.send();
	});
	$("#other_id").val("");
	$("#other_id").autocomplete({source : "AutoCompleteUser"});
	return false;
}

function searchformsubmit(){
	var input_val = $("#searchinput").val();
	convDetails(input_val);
	return;
}