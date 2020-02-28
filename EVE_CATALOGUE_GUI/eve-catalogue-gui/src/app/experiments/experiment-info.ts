import { Execution } from './execution';

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
    name: string;
    executions: Execution[];
}
