import { Injectable } from '@angular/core';
import { BlueprintsVsDataSource, BlueprintsVsItem } from './blueprints-vs/blueprints-vs-datasource';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsVsService {

  constructor() { }

  getVsBlueprints(): BlueprintsVsDataSource {

    return new BlueprintsVsDataSource();
  }
}
