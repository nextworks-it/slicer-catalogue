import { BrowserModule } from '@angular/platform-browser';

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
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
import { BlueprintsVsGraphComponent } from './blueprints-vs-graph/blueprints-vs-graph.component';
import { VsbGraphComponent } from './vsb-graph/vsb-graph.component';
import { BlueprintsVsService } from './blueprints-vs.service';
import { MessagesComponent } from './messages/messages.component';

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
    BlueprintsVsGraphComponent,
    VsbGraphComponent,
    MessagesComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(AppRoutes),
    BrowserAnimationsModule,
    LayoutModule,
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
    FormsModule,
    ReactiveFormsModule.withConfig({warnOnNgModelWithFormControl: 'never'}),
    HttpClientModule
  ],
  providers: [BlueprintsVsService],
  bootstrap: [AppComponent]
})
export class AppModule { }