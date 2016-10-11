{
  "workflowURI" : "workflow://workflows/netcaprepo",
  "steps" : [ {
    "name" : "readData",
    "description" : "Read data in",
    "executable" : "dp_java_invocable://netcaprepo.ReadDataStep",
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
    "description" : "Write data to blob repos and structured repos",
    "executable" : "dp_java_invocable://netcaprepo.WriteDataStep",
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
