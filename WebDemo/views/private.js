define([],function(){


     var p_ui = {
                view:"tree",
                activeTitle:true,
                url: "http://localhost:4568/upload_response?docname=cars.csv.json",
                datatype:"json"
               /* data: [
                    { id:"1", open:true, value:"The Shawshank Redemption", data:[
                        { id:"1.1", value:"Part 1" },
                        { id:"1.2", value:"Part 2" },
                        { id:"1.3", value:"Part 3" }
                    ]},
                    { id:"2", value:"The Godfather", data:[
                        { id:"2.1", value:"Part 1" },
                        { id:"2.2", value:"Part 2" }
                    ]}
                ] */
		}

	return {
		$ui: p_ui,
		$menu: "top:private"

	}
	
});