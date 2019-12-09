export class ExperimentInfo {
    experimentId: string;
    tenantId: string;
    status: string;
    experimentDescriptorId: string;
    lcTicketId: string;
    openTicketIds: string[];
    targetSites: string[];
    timeslot: {
        startTime: string;
        stopTime: string;
    };
    nfvNsInstanceId: string;
    executionId: string;
    executionStatus: string;
    errorMessage: string;
}