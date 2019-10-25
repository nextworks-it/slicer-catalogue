export class ExpDescriptorInfo {
    expDescriptorId: string;
    name: string;
    version: string;
    expBlueprintId: string;
    vsDescriptorId: string;
    ctxDescriptorIds: string[];
    testCaseDescriptorIds: string[];
    kpiThresholds: Map<string, string>;
    isPublic: boolean;
    tenantId: string;
}