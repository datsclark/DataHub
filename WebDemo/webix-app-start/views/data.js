define([
	"models/records"
],function(records){

	var ui = {
		view:"datatable", autoConfig:true
	};

	var	grida = {
				
				view:"datatable",
				columns:[
					{ id:"rank",	header:"", css:"rank",  		width:50},
					{ id:"title",	header:"Film title",width:200},
					{ id:"year",	header:"Released" , width:80},
					{ id:"votes",	header:"Votes", 	width:100}
				],
				autoheight:true,
				autowidth:true,
				data: [
					{ id:1, title:"The Shawshank Redemption", year:1994, votes:678790, rating:9.2, rank:1},
					{ id:2, title:"The Godfather", year:1972, votes:511495, rating:9.2, rank:2}
				]
			};	
			
			

	return {
		$ui: grida,
		$oninit:function(view){
			view.parse(records.data);
		}
	};
	
});
