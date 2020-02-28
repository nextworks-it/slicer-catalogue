import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ExpBlueprintInfo } from './blueprints-e/exp-blueprint-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsExpService {

  private baseUrl = environment.portalBaseUrl;
  private expBlueprintInfoUrl = 'expblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getExpBlueprints(): Observable<ExpBlueprintInfo[]> {
    return this.http.get<ExpBlueprintInfo[]>(this.baseUrl + this.expBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expBlueprintInfos - SUCCESS')),
        catchError(this.authService.handleError<ExpBlueprintInfo[]>('getExpBlueprints', []))
      );
  }

  getExpBlueprint(expBlueprintId: string): Observable<ExpBlueprintInfo> {
    return this.http.get<ExpBlueprintInfo>(this.baseUrl + this.expBlueprintInfoUrl + "/" + expBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expBlueprintInfo - SUCCESS')),
        catchError(this.authService.handleError<ExpBlueprintInfo>('getExpBlueprint'))
      );
  }

  postExpBlueprint(onBoardExpRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.expBlueprintInfoUrl, onBoardExpRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.authService.log(`added Exp Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
        catchError(this.authService.handleError<String>('postExpBlueprint'))
      );
  }

  deleteExpBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.expBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.authService.log(`deleted Exp Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
      catchError(this.authService.handleError<String>('deleteExpBlueprint'))
    );
  }
}
