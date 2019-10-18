export class TcBlueprintInfo {
    testCaseBlueprintId: String;
    version: String;
    name: String;
    testCaseBlueprint: {
        version: String,
        name: String;
        description: String;
        testcaseBlueprintId: String;
        script: String;
        userParameters: Map<String, String>;
        infrastructureParameters: Map<String, String>;
    };
    activeTcdId: String[]
}