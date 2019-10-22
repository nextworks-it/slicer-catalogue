export class ExpDescriptorInfo {
    expDescriptorId: String;
    name: String;
    version: String;
    expBlueprintId: String;
    vsDescriptorId: String;
    ctxDescriptorIds: String[];
    testCaseDescriptorIds: String[];
    kpiThresholds: Map<String, String>;
    isPublic: Boolean;
    tenantId: String;
}