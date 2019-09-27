import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BlueprintsVsComponent } from './blueprints-vs/blueprints-vs.component';
import { BlueprintsEcComponent } from './blueprints-ec/blueprints-ec.component';
import { BlueprintsEComponent } from './blueprints-e/blueprints-e.component';
import { DescriptorsVsComponent } from './descriptors-vs/descriptors-vs.component';
import { DescriptorsEcComponent } from './descriptors-ec/descriptors-ec.component';
import { DescriptorsEComponent } from './descriptors-e/descriptors-e.component';
import { NfvNsComponent } from './nfv-ns/nfv-ns.component';
import { NfvPnfComponent } from './nfv-pnf/nfv-pnf.component';
import { NfvVnfComponent } from './nfv-vnf/nfv-vnf.component';
import { BlueprintsEDetailsComponent } from './blueprints-e-details/blueprints-e-details.component'
import { VsbGraphComponent } from './vsb-graph/vsb-graph.component'
import { LoginComponent } from './login/login.component';


export const AppRoutes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'blueprints_vs', component: BlueprintsVsComponent },
    { path: 'blueprints_ec', component: BlueprintsEcComponent},
    { path: 'blueprints_exp', component: BlueprintsEComponent},
    { path: 'descriptors_vs', component: DescriptorsVsComponent},
    { path: 'descriptors_ec', component: DescriptorsEcComponent},
    { path: 'descriptors_exp', component: DescriptorsEComponent},
    { path: 'nfv_ns', component: NfvNsComponent},
    { path: 'nfv_pnf', component: NfvPnfComponent},
    { path: 'nfv_vnf', component: NfvVnfComponent},
    { path: 'blueprints_e_details', component: BlueprintsEDetailsComponent},
    { path: 'blueprints_vs_graph', component: VsbGraphComponent },
    { path: 'login', component: LoginComponent},

    // otherwise redirect to home
    { path: '**', redirectTo: '/dashboard' }
];