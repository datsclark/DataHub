{
  "workflowURI" : "workflow://workflows/web_demo_curate",
  "steps" : [ {
    "name" : "readData",
    "description" : "Read data from incoming CSV file and curate into columns",
    "executable" : "dp_java_invocable://datahub.ReadDataStep",
    "transitions" : [ {
      "name" : "ok",
      "targetStep" : "writeData"
    }, {
      "name" : "error",
      "targetStep" : "$FAIL"
    } ]
  },
  {
    "name" : "writeData",
    "description" : "Read data from incoming CSV file and curate into columns",
    "executable" : "dp_java_invocable://datahub.WriteDataStep",
    "transitions" : [ {
      "name" : "ok",
      "targetStep" : "$RETURN"
    }, {
      "name" : "error",
      "targetStep" : "$FAIL"
    } ]
  } ],
  "startStep" : "readData",
  "jarUriDependencies" : [
    "jar://workflows/*"
  ],
  "category" : "datahub"
}
