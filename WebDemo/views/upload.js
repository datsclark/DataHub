define([],function(){

	var webSocket = new WebSocket("ws://localhost:4568/echo/");

	webSocket.onopen = function () { 
		webSocket.send("startup");

	};

	webSocket.onclose = function () {
		webix.message({expire:-1, text:"WebSocket connection closed."}); 
	};

	webSocket.onmessage = function (msg) { readMsg(msg); };

	function sendMessage(text) {
		
	    if (message !== "") {
	    	//webix.message("File uploaded is:  "+ text);
	        webSocket.send(message);
	        //document.getElementById("message").value = "";
	    }
	}

	function readMsg(msg) {
	    var data = JSON.parse(msg.data);
	    if (typeof data.hashCode !== 'undefined') {
  			//webix.message({expire:-1, text:"hashCode is " + data.hashCode});
  			$$('myform').setValues({ session:data.hashCode}, true);
		}

	    if (data.getData == "ok") {
	    	
	    	webix.message("Processing ... ");
	    	addData(data.docName);
	    } 
	}

	function addData(dname) {
		var duri = "http://localhost:4568/upload_response?docname=" + dname;
		webix.message("URI: " + duri);
		$$("my_dt").load(duri);
		//$$("my_dt").adjust();
	}


			function save_form(){

				

				//send files to server side
				$$("upl1").send(function(){
					//getting file properties
					$$('upl1').files.data.each(function(obj){
						var status = obj.status;
						var iscsv = obj.csv;
						var name = obj.name;
						if(status=='server'){
							var sname = obj.sname; //came from upload script
							webix.message("Upload: "+status+" for "+ name+" stored as "+sname );
						}
						else{
							webix.message("Upload: "+status+" for "+ name);
						}

						if (iscsv == "YES") {
							$$('myform').setValues({ "csvflag":"YES"}, true);
						} else {
							$$('myform').setValues({ "csvflag":"NO"}, true);
						}

				});

					//after that send form
					webix.ajax().post(
						"http://localhost:4568/upload_save", 
						$$("myform").getValues(), 
						function(text){
							//show server side response
							webix.message(text);
						}
					);
				});
			}

			var u_ui = {
				view:"form", 
				id:"myform",
				elements:[
					{ 
						view: "uploader", value: 'Upload file', 
						multiple:false, autosend:false,
						id:"upl1", name:"files",
						link:"mylist",  upload:"http://localhost:4568/upload" 
					},
					{
					 	view:"list",  id:"mylist", type:"uploader",
						autoheight:true, borderless:true	
					},
					{ view:"button", label:"Save", type:"form", click:save_form },
					/*{
						view:"uploader", upload:"http://localhost:4568/upload",
						id:"upl1", name:"files",
						value:"Add documents", 
						link:"doclist", autosend:false
					},
					{ view:"list", scroll:false, id:"doclist", type:"uploader" },
					*/
					{  view:"datatable", id:"my_dt",
					    	autowidth:true, 
					    	autoConfig:true, 
					    	resizeColumn: {headerOnly:true},
					    	//resizeRow: {headerOnly:true} , 
					    	adjust:true,
					    	datatype:"json" 
					    	}

				]
			};

			

	return {
		$ui: u_ui,
		$menu: "top:upload"
		//$oninit:function(view){
		//	$$('myform').setValues({ session:data.hashCode}, true);
		//}

		
	}
	
});
