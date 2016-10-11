define([],function(){

     var d_ui = {
        //container:"box",
        view:"datatable",
        autoConfig:true,
        url:"http://localhost:4568/upload_response?docname=cars.csv.json"
	};

	return {
		$ui: d_ui,
		$menu: "top:data"
		//$oninit:function(view){
		//	view.parse(records.data);
		//}
	};
	
});
