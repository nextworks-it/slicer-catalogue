import { Component, OnInit } from '@angular/core';
import { ExpDescriptorInfo } from '../descriptors-e/exp-descriptor-info';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { ExperimentsService } from '../experiments.service';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';

export interface ViewValue {
  value: string;
  viewValue: string;
  item: Object;
}

@Component({
  selector: 'app-descriptors-e-scheduler',
  templateUrl: './descriptors-e-scheduler.component.html',
  styleUrls: ['./descriptors-e-scheduler.component.css']
})
export class DescriptorsESchedulerComponent implements OnInit {

  scheduleFormGroup: FormGroup;
  timeSlotStart = new FormControl((new Date()).toISOString());
  timeSlotEnd = new FormControl((new Date()).toISOString());

  expDescriptors: ViewValue[] = [];
  availableSites: string[] = [];
  start_date: string;
  end_date: string;

  constructor(private _formBuilder: FormBuilder,
    private router: Router,
    private descriptorsExpService: DescriptorsExpService,
    private blueprintsExpService: BlueprintsExpService,
    private experimentService: ExperimentsService) { }

  ngOnInit() {
    this.scheduleFormGroup = this._formBuilder.group({
      name: ['', Validators.required],
      expDescriptorId: ['', Validators.required],
      timeSlotStart: ['', Validators.required],
      timeSlotEnd: ['', Validators.required],
      targetSite: ['', Validators.required]
    });
    this.getExpDescriptors();
  }

  getExpDescriptors() {
    this.descriptorsExpService.getExpDescriptors().subscribe((expDescriptorsInfos: ExpDescriptorInfo[]) =>
      {
        //console.log(expDescriptorsInfos);

        for (var i = 0; i < expDescriptorsInfos.length; i++) {
          this.expDescriptors.push({value: expDescriptorsInfos[i]['expDescriptorId'], viewValue: expDescriptorsInfos[i]['name'], item: expDescriptorsInfos[i]});
        }
      });
  }

  getStartDate(event: any) {
    const data = event;
    const formattedDate = data.getDate() + '-' + (data.getMonth() + 1) + '-' + data.getFullYear();
    this.start_date = formattedDate;
    //console.log(this.start_date);
  }

  getEndDate(event: any) {
    const data = event;
    const formattedDate = data.getDate() + '-' + (data.getMonth() + 1) + '-' + data.getFullYear();
    this.end_date = formattedDate;
    //console.log(this.end_date);
  }

  onExpDSelected(event: any) {
    var selectedDescriptor = event.value;

    for (var i = 0; i < this.expDescriptors.length; i++) {
      if (this.expDescriptors[i]['value'] == selectedDescriptor) {
        var expD = this.expDescriptors[i].item;
        var expBId = expD['expBlueprintId'];
        this.getExpBlueprint(expBId);
      }
    }
  }

  getExpBlueprint(expBId: string) {
    this.blueprintsExpService.getExpBlueprint(expBId).subscribe(expBlueprintInfo => {
      console.log(expBlueprintInfo['expBlueprint']['sites']);
      this.availableSites = expBlueprintInfo['expBlueprint']['sites'];
    });
  }

  scheduleExperiment() {
    var executionName = this.scheduleFormGroup.get('name').value;
    var expDescriptorId = this.scheduleFormGroup.get('expDescriptorId').value;
    var startDate = this.scheduleFormGroup.get('timeSlotStart').value;
    var endDate = this.scheduleFormGroup.get('timeSlotEnd').value;
    var targetSite = this.scheduleFormGroup.get('targetSite').value
;
    var scheduleExperimentRequest = JSON.parse('{}');
    scheduleExperimentRequest['experimentName'] = executionName;
    scheduleExperimentRequest['experimentDescriptorId'] = expDescriptorId;
    scheduleExperimentRequest['proposedTimeSlot'] = {};
    scheduleExperimentRequest['proposedTimeSlot']['startTime'] = startDate;
    scheduleExperimentRequest['proposedTimeSlot']['stopTime'] = endDate;
    scheduleExperimentRequest['targetSites'] = [targetSite];

    console.log(JSON.stringify(scheduleExperimentRequest, null, 4));

    this.experimentService.postExperiment(scheduleExperimentRequest, '/experiments')
          .subscribe(experimentId => {
            console.log("Experiment with id " + experimentId)
            if (experimentId != null) {
              this.router.navigate(['/experiments']);
            }
          });
  }
}
