export class TcBlueprintInfo {
    testCaseBlueprintId: string;
    version: string;
    name: string;
    testCaseBlueprint: {
        version: string,
        name: string;
        description: string;
        testcaseBlueprintId: string;
        script: string;
        userParameters: Map<string, string>;
        infrastructureParameters: Map<string, string>;
    };
    activeTcdId: string[]
}