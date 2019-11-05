import { Routes } from '@angular/router';
import { PortalHomeComponent } from './portal-home/portal-home.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BlueprintsVsComponent } from './blueprints-vs/blueprints-vs.component';
import { BlueprintsTcComponent } from './blueprints-tc/blueprints-tc.component';
import { BlueprintsEcComponent } from './blueprints-ec/blueprints-ec.component';
import { BlueprintsEComponent } from './blueprints-e/blueprints-e.component';
import { DescriptorsVsComponent } from './descriptors-vs/descriptors-vs.component';
import { DescriptorsTcComponent } from './descriptors-tc/descriptors-tc.component';
import { DescriptorsEcComponent } from './descriptors-ec/descriptors-ec.component';
import { DescriptorsEComponent } from './descriptors-e/descriptors-e.component';
import { NfvNsComponent } from './nfv-ns/nfv-ns.component';
import { NfvPnfComponent } from './nfv-pnf/nfv-pnf.component';
import { NfvVnfComponent } from './nfv-vnf/nfv-vnf.component';
import { BlueprintsEDetailsComponent } from './blueprints-e-details/blueprints-e-details.component';
import { BlueprintsEcDetailsComponent } from './blueprints-ec-details/blueprints-ec-details.component';
import { BlueprintsVsDetailsComponent } from './blueprints-vs-details/blueprints-vs-details.component';
import { DescriptorsEDetailsComponent } from './descriptors-e-details/descriptors-e-details.component';
import { DescriptorsVsDetailsComponent } from './descriptors-vs-details/descriptors-vs-details.component';
import { LoginComponent } from './login/login.component';


export const AppRoutes: Routes = [
    { path: '', redirectTo: '/portal_home', pathMatch: 'full' },
    { path: 'portal_home', component: PortalHomeComponent},
    { path: 'dashboard', component: DashboardComponent },
    { path: 'blueprints_vs', component: BlueprintsVsComponent },
    { path: 'blueprints_tc', component: BlueprintsTcComponent},
    { path: 'blueprints_ec', component: BlueprintsEcComponent},
    { path: 'blueprints_exp', component: BlueprintsEComponent},
    { path: 'descriptors_vs', component: DescriptorsVsComponent},
    { path: 'descriptors_tc', component: DescriptorsTcComponent},
    { path: 'descriptors_ec', component: DescriptorsEcComponent},
    { path: 'descriptors_exp', component: DescriptorsEComponent},
    { path: 'nfv_ns', component: NfvNsComponent},
    { path: 'nfv_pnf', component: NfvPnfComponent},
    { path: 'nfv_vnf', component: NfvVnfComponent},
    { path: 'blueprints_e_details', component: BlueprintsEDetailsComponent},
    { path: 'blueprints_ec_details', component: BlueprintsEcDetailsComponent},
    { path: 'blueprints_vs_details', component: BlueprintsVsDetailsComponent },
    { path: 'descriptors_e_details', component: DescriptorsEDetailsComponent },
    { path: 'descriptors_vs_details', component: DescriptorsVsDetailsComponent },
    { path: 'login', component: LoginComponent},

    // otherwise redirect to home
    { path: '**', redirectTo: '/portal_home' }
];