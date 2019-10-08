import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { VsBlueprintInfo } from './blueprints-vs/vs-blueprint-info';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsVsService {

  private baseUrl = 'http://localhost:8082/portal/catalogue/';
  private vsBlueprintInfoUrl = 'vsblueprint';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  /*getVsBlueprints(): Observable<VsBlueprintInfo[]> {
    return this.http.get<VsBlueprintInfo[]>(this.baseUrl + this.vsBlueprintInfoUrl);
  }*/

  getVsBlueprints(): Observable<VsBlueprintInfo[]> {
    return this.http.get<VsBlueprintInfo[]>(this.baseUrl + this.vsBlueprintInfoUrl);
  }

  postVsBlueprint(data) {
    this.http.post(this.baseUrl + this.vsBlueprintInfoUrl, data);
  }
}
