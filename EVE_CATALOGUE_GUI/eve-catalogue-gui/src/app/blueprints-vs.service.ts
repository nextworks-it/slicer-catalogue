import { Injectable } from '@angular/core';
import { Observable, } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { VsBlueprintInfo } from './blueprints-vs/vs-blueprint-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsVsService {

  private baseUrl = environment.portalBaseUrl;
  private vsBlueprintInfoUrl = 'vsblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient, 
    private authService: AuthService) { }

  getVsBlueprints(): Observable<VsBlueprintInfo[]> {
    return this.http.get<VsBlueprintInfo[]>(this.baseUrl + this.vsBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched vsBlueprintInfos - SUCCESS')),
        catchError(this.authService.handleError<VsBlueprintInfo[]>('getVsBlueprints', []))
      );
  }

  getVsBlueprint(vsBlueprintId: string): Observable<VsBlueprintInfo> {
    return this.http.get<VsBlueprintInfo>(this.baseUrl + this.vsBlueprintInfoUrl + "/" + vsBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched vsBlueprintInfo - SUCCESS')),
        catchError(this.authService.handleError<VsBlueprintInfo>('getVsBlueprint'))
      );
  }

  postVsBlueprint(onBoardVsRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.vsBlueprintInfoUrl, onBoardVsRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.authService.log(`added VS Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
        catchError(this.authService.handleError<String>('postVsBlueprint'))
      );
  }

  deleteVsBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.vsBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.authService.log(`deleted VS Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
      catchError(this.authService.handleError<String>('deleteVsBlueprint'))
    );
  }
}
