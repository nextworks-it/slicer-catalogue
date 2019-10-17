import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { VsBlueprintInfo } from './blueprints-vs/vs-blueprint-info';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsEcServiceService {

  private baseUrl = 'http://localhost:8082/portal/catalogue/';
  private ctxBlueprintUrl = 'ctxblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getCtxBlueprints(): Observable<Object[]> {
    return this.http.get<Object[]>(this.baseUrl + this.ctxBlueprintUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched ctxBlueprints', 'SUCCESS')),
        catchError(this.handleError<Object[]>('getCtxBlueprints', []))
      );
  }

  getCtxBlueprint(ctxBlueprintId: string): Observable<Object> {
    return this.http.get<Object>(this.baseUrl + this.ctxBlueprintUrl + "/" + ctxBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched ctxBlueprint', 'SUCCESS')),
        catchError(this.handleError<Object>('getCtxBlueprint'))
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
    this.messageService.add(`BluepritsVSService: ${message}`);
    this.openSnackBar(`BluepritsVSService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
