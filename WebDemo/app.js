/*
	App configuration
*/

define([
	"libs/webix-mvc-core/core",
	"libs/webix-mvc-core/plugins/menu",
], function(
	core, menu
){

	//configuration
	var app = core.create({
		id:			"DataHub", //change this line!
		name:		"DataHub Demo!",
		version:	"0.1.0",
		debug:		true,
		start:		"/top/start"
	});

	app.use(menu);



	return app;
});