import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CtxBlueprintInfo } from './blueprints-ec/ctx-blueprint-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsEcService {

  private baseUrl = environment.portalBaseUrl;
  private ctxBlueprintUrl = 'ctxblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getCtxBlueprints(): Observable<CtxBlueprintInfo[]> {
    return this.http.get<CtxBlueprintInfo[]>(this.baseUrl + this.ctxBlueprintUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ctxBlueprints - SUCCESS')),
        catchError(this.authService.handleError<CtxBlueprintInfo[]>('getCtxBlueprints', []))
      );
  }

  getCtxBlueprint(ctxBlueprintId: string): Observable<CtxBlueprintInfo> {
    return this.http.get<CtxBlueprintInfo>(this.baseUrl + this.ctxBlueprintUrl + "/" + ctxBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ctxBlueprint - SUCCESS')),
        catchError(this.authService.handleError<CtxBlueprintInfo>('getCtxBlueprint'))
      );
  }

  postCtxBlueprint(onboardCtxBlueprintRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.ctxBlueprintUrl, onboardCtxBlueprintRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.authService.log(`added CTX Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
        catchError(this.authService.handleError<String>('postCtxBlueprint'))
      );
  }

  deleteCtxBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.ctxBlueprintUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.authService.log(`deleted CTX Blueprint w/ id=${blueprintId}`, 'SUCCESS', true)),
      catchError(this.authService.handleError<String>('deleteCtxBlueprint'))
    );
  }
}
