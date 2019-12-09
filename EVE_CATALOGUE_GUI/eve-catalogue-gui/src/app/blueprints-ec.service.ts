import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CtxBlueprintInfo } from './blueprints-ec/ctx-blueprint-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsEcService {

  private baseUrl = environment.portalBaseUrl;
  private ctxBlueprintUrl = 'ctxblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getCtxBlueprints(): Observable<CtxBlueprintInfo[]> {
    return this.http.get<CtxBlueprintInfo[]>(this.baseUrl + this.ctxBlueprintUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ctxBlueprints - SUCCESS')),
        catchError(this.handleError<CtxBlueprintInfo[]>('getCtxBlueprints', []))
      );
  }

  getCtxBlueprint(ctxBlueprintId: string): Observable<CtxBlueprintInfo> {
    return this.http.get<CtxBlueprintInfo>(this.baseUrl + this.ctxBlueprintUrl + "/" + ctxBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ctxBlueprint - SUCCESS')),
        catchError(this.handleError<CtxBlueprintInfo>('getCtxBlueprint'))
      );
  }

  postCtxBlueprint(onboardCtxBlueprintRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.ctxBlueprintUrl, onboardCtxBlueprintRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.log(`added CTX Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postCtxBlueprint'))
      );
  }

  deleteCtxBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.ctxBlueprintUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted CTX Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteCtxBlueprint'))
    );
  }

  /**
 * Handle Http operation that failed.
 * Let the app continue.
 * @param operation - name of the operation that failed
 * @param result - optional value to return as the observable result
 */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`, 'FAILED');

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a BlueprintsVSService message with the MessageService */
  private log(message: string, action: string) {
    this.messageService.add(`BluepritsECService: ${message}`);
    this.openSnackBar(`BluepritsECService: ${message}`, action);
    window.location.reload();
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
