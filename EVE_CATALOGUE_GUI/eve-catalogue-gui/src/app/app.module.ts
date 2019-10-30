import { BrowserModule } from '@angular/platform-browser';

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
import { FlexLayoutModule } from "@angular/flex-layout";
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle'
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTabsModule } from '@angular/material/tabs';
import { MatStepperModule } from '@angular/material/stepper'
import { MatFormFieldModule } from '@angular/material/form-field'
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CytoscapeModule } from 'ngx-cytoscape';
import { AppRoutes } from './app.routes';
import { SideTopBarComponent } from './side-top-bar/side-top-bar.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BlueprintsVsComponent } from './blueprints-vs/blueprints-vs.component';
import { BlueprintsEcComponent } from './blueprints-ec/blueprints-ec.component';
import { BlueprintsEComponent } from './blueprints-e/blueprints-e.component';
import { DescriptorsVsComponent } from './descriptors-vs/descriptors-vs.component';
import { DescriptorsEcComponent } from './descriptors-ec/descriptors-ec.component';
import { DescriptorsEComponent } from './descriptors-e/descriptors-e.component';
import { NfvNsComponent } from './nfv-ns/nfv-ns.component';
import { NfvVnfComponent } from './nfv-vnf/nfv-vnf.component';
import { NfvPnfComponent } from './nfv-pnf/nfv-pnf.component';
import { BlueprintsVsStepperComponent } from './blueprints-vs-stepper/blueprints-vs-stepper.component';
import { BlueprintsEStepperComponent } from './blueprints-e-stepper/blueprints-e-stepper.component';
import { BlueprintsEDetailsComponent } from './blueprints-e-details/blueprints-e-details.component';
import { LoginComponent } from './login/login.component';
import { BlueprintsGraphComponent } from './blueprints-graph/blueprints-graph.component';
import { BlueprintsVsDetailsComponent } from './blueprints-vs-details/blueprints-vs-details.component';
import { BlueprintsVsService } from './blueprints-vs.service';
import { MessagesComponent } from './messages/messages.component';
import { BlueprintsEcDetailsComponent } from './blueprints-ec-details/blueprints-ec-details.component';
import { BlueprintsEcStepperComponent } from './blueprints-ec-stepper/blueprints-ec-stepper.component';
import { BlueprintsTcComponent } from './blueprints-tc/blueprints-tc.component';
import { DescriptorsEStepperComponent } from './descriptors-e-stepper/descriptors-e-stepper.component';
import { DescriptorsTcComponent } from './descriptors-tc/descriptors-tc.component';
import { DescriptorsVsDetailsComponent } from './descriptors-vs-details/descriptors-vs-details.component';
import { DescriptorsEDetailsComponent } from './descriptors-e-details/descriptors-e-details.component';

@NgModule({
  declarations: [
    AppComponent,
    SideTopBarComponent,
    DashboardComponent,
    BlueprintsVsComponent,
    BlueprintsEcComponent,
    BlueprintsEComponent,
    DescriptorsVsComponent,
    DescriptorsEcComponent,
    DescriptorsEComponent,
    NfvNsComponent,
    NfvVnfComponent,
    NfvPnfComponent,
    BlueprintsVsStepperComponent,
    BlueprintsEStepperComponent,
    BlueprintsEDetailsComponent,
    LoginComponent,
    BlueprintsGraphComponent,
    BlueprintsVsDetailsComponent,
    MessagesComponent,
    BlueprintsEcDetailsComponent,
    BlueprintsEcStepperComponent,
    BlueprintsTcComponent,
    DescriptorsEStepperComponent,
    DescriptorsTcComponent,
    DescriptorsVsDetailsComponent,
    DescriptorsEDetailsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(AppRoutes),
    BrowserAnimationsModule,
    LayoutModule,
    FlexLayoutModule,
    MatToolbarModule,
    MatBadgeModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatSelectModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatMenuModule,
    MatGridListModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatTabsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatRadioModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatDialogModule,
    FormsModule,
    ReactiveFormsModule.withConfig({warnOnNgModelWithFormControl: 'never'}),
    HttpClientModule,
    CytoscapeModule
  ],
  providers: [BlueprintsVsService],
  bootstrap: [AppComponent]
})
export class AppModule { }