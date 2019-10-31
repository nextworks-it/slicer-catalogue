export class VsDescriptorInfo {
    vsDescriptorId: string;
    name: string;
    version: string;
    vsBlueprintId: string;
    sst: string;
    managementType: string;
    qosParameters: Map<string, string>;
    serviceConstraints: Object[];        
    sla: Map<string, string>;
}