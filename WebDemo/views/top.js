define([
	"app"
],function(app){

	var header = {
		type:"header", template:app.config.name
	};

	var menu = {
		view:"menu", id:"top:menu", 
		width:180, layout:"y", select:true,
		template:"<span class='webix_icon fa-#icon#'></span> #value# ",
		data:[
			{ value:"DashBoard", 		id:"start",		href:"#!/top/start", 		icon:"envelope-o" },
			{ value:"Upload", 			id:"uppoad",	href:"#!/top/upload", 		icon:"briefcase" },
			{ value:"View", 			id:"data",		href:"#!/top/data", 		icon:"briefcase" },
			{ value:"Private", 			id:"private",	href:"#!/top/private", 		icon:"lock" },
			{ value:"Data Sets ...", 	id:"dataset",	icon:"database",
				submenu:[ { value:"Data1", 			id:"data1",		href:"#!/top/start", 		icon:"folder" }, 
						  { value:"Data2", 			id:"data2",		href:"#!/top/start", 		icon:"folder", badge:"12" }, 
						  { value:"Data3", 			id:"data3",		href:"#!/top/start", 		icon:"folder" } 
						  ]
						}
		]
	};

	var ui = {
		type:"line", cols:[
			{ type:"clean", css:"app-left-panel",
				padding:10, margin:20, borderless:true, rows: [ header, menu ]},
			{ rows:[ { height:10}, 
				{ type:"clean", css:"app-right-panel", padding:4, rows:[
					{ $subview:true } 
				]}
			]}
		]
	};

	return {
		$ui: ui,
		$menu: "top:menu"
	};
});
