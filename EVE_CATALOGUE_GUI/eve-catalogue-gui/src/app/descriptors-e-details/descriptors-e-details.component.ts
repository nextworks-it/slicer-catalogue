import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEDetailsDataSource, DescriptorsEDetailsItemKV } from './descriptors-e-details.datasource';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { ExpDescriptorInfo } from '../descriptors-e/exp-descriptor-info';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { DescriptorsVsService } from '../descriptors-vs.service';
import { VsDescriptorInfo } from '../descriptors-vs/vs-descriptor-info';
import { DescriptorsEcService } from '../descriptors-ec.service';
import { EcDescriptorInfo } from '../descriptors-ec/ec-descriptor-info';
import { TcDescriptorInfo } from '../descriptors-tc/tc-descriptor-info';
import { DescriptorsTcService } from '../descriptors-tc.service';


@Component({
  selector: 'app-descriptors-e-details',
  templateUrl: './descriptors-e-details.component.html',
  styleUrls: ['./descriptors-e-details.component.css']
})
export class DescriptorsEDetailsComponent implements OnInit {
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<DescriptorsEDetailsItemKV>;
  dataSource: DescriptorsEDetailsDataSource;

  tableData: DescriptorsEDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(
    private  blueprintsExpService: BlueprintsExpService,
    private  descriptorsExpService: DescriptorsExpService,
    private  descriptorsVsService: DescriptorsVsService,
    private  descriptorsEcService: DescriptorsEcService,
    private  descriptorsTcService: DescriptorsTcService
    ) {}

  ngOnInit() {
    var expdId = localStorage.getItem('expdId');
    this.dataSource = new DescriptorsEDetailsDataSource(this.tableData);
    this.getExpDescriptor(expdId);
  }

  getExpDescriptor(expdId: string) {
    this.descriptorsExpService.getExpDescriptor(expdId).subscribe((expDescriptorInfo: ExpDescriptorInfo) => {
      this.blueprintsExpService.getExpBlueprints().subscribe((expBlueprintInfo: ExpBlueprintInfo[]) => {
        this.descriptorsVsService.getVsDescriptors().subscribe((vsDescriptorInfo: VsDescriptorInfo[]) => {
          this.descriptorsEcService.getCtxDescriptors().subscribe((ecDescriptorsInfo: EcDescriptorInfo[]) => {
            this.descriptorsTcService.getTcDescriptors().subscribe((tcDescriptorsInfo: TcDescriptorInfo[]) => {

        //console.log(vsDescriptorInfo);

        this.tableData.push({key: "Name", value: [expDescriptorInfo['name']]});
        this.tableData.push({key: "Id", value: [expDescriptorInfo['expDescriptorId']]});
        this.tableData.push({key: "Version", value: [expDescriptorInfo['version']]});

        for (var i = 0; i < expBlueprintInfo.length; i++){
          if (expDescriptorInfo['expBlueprintId'] === expBlueprintInfo[i]['expBlueprintId']){
            this.tableData.push({key: "Experiment Blueprint", value: [expBlueprintInfo[i]['name']]});
          }
        }

        var values = [];
        for (var i = 0; i < vsDescriptorInfo.length; i++){
          if (expDescriptorInfo['vsDescriptorId'] === vsDescriptorInfo[i]['vsDescriptorId']){
            var paramList = new Map(Object.entries(vsDescriptorInfo[i]['qosParameters']));
            values.push("QoS parameters")
            paramList.forEach((value: string, key: string) => {
              values.push("- " + key + ": " + value);
            });
          }
        }
        //values.push("- aggiunta a mano: 50" );
        this.tableData.push({key: "Vertical Service Descriptor", value: values});





        var values = [];
        for(var j = 0; j < expDescriptorInfo['ctxDescriptorIds'].length; j++){
          for (var i = 0; i < ecDescriptorsInfo.length; i++){
            if (expDescriptorInfo['ctxDescriptorIds'][j] === ecDescriptorsInfo[i]['ctxDescriptorId'] ){
              var paramList = new Map(Object.entries(ecDescriptorsInfo[i]['ctxParameters']));
              values.push(ecDescriptorsInfo[i]['name'] + ": Parameters");
              paramList.forEach((value: string, key: string) => {
                values.push("- " + key + ": " + value);
              });
            }
          }
        }
        //values.push("- aggiunta a mano: 50" );
        this.tableData.push({key: "Experiment Context Descriptor", value: values});







        var values = [];
        for(var j = 0; j < expDescriptorInfo['testCaseDescriptorIds'].length; j++){
          for (var i = 0; i < tcDescriptorsInfo.length; i++){
            if (expDescriptorInfo['testCaseDescriptorIds'][j] === tcDescriptorsInfo[i]['testCaseDescriptorId'] ){
              var paramList = new Map(Object.entries(tcDescriptorsInfo[i]['userParameters']));
              values.push(tcDescriptorsInfo[i]['name'] + ": User parameters");
              paramList.forEach((value: string, key: string) => {
                values.push("- " + key + ": " + value);
              });
            }
          }
        }
        //values.push("- aggiunta a mano: 50" );
        this.tableData.push({key: "Test Case Descriptor", value: values});
        var values = [];

        if (expDescriptorInfo['kpiThresholds']) {
          var kpis = new Map(Object.entries(expDescriptorInfo['kpiThresholds']));
          kpis.forEach((value: string, key: string) => {
            values.push(key + ": " + value)
          });
          this.tableData.push({key: "KPIs Thresholds", value: values});
        }

        this.dataSource = new DescriptorsEDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.table.dataSource = this.dataSource;
      });
      });
    });
  });
});

  }
}
