import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TcBlueprintInfo } from './blueprints-tc/tc-blueprint-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsTcService {

  private baseUrl = environment.portalBaseUrl;
  private tcBlueprintInfoUrl = 'testcaseblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getTcBlueprints(): Observable<TcBlueprintInfo[]> {
    return this.http.get<TcBlueprintInfo[]>(this.baseUrl + this.tcBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcBlueprintInfos - SUCCESS')),
        catchError(this.authService.handleError<TcBlueprintInfo[]>('getTcBlueprints', []))
      );
  }

  getTcBlueprint(tcBlueprintId: string): Observable<TcBlueprintInfo> {
    return this.http.get<TcBlueprintInfo>(this.baseUrl + this.tcBlueprintInfoUrl + "/" + tcBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcBlueprintInfo - SUCCESS')),
        catchError(this.authService.handleError<TcBlueprintInfo>('getTcBlueprint'))
      );
  }

  postTcBlueprint(onBoardTcRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.tcBlueprintInfoUrl, onBoardTcRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.authService.log(`added TC Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
        catchError(this.authService.handleError<String>('postTcBlueprint'))
      );
  }

  deleteTcBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.tcBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.authService.log(`deleted TC Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
      catchError(this.authService.handleError<String>('deleteTcBlueprint'))
    );
  }
}
