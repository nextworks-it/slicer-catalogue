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
export class BlueprintsVsService {

  private baseUrl = 'http://localhost:8082/portal/catalogue/';
  private vsBlueprintInfoUrl = 'vsblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  /*getVsBlueprints(): Observable<VsBlueprintInfo[]> {
    return this.http.get<VsBlueprintInfo[]>(this.baseUrl + this.vsBlueprintInfoUrl);
  }*/

  getVsBlueprints(): Observable<VsBlueprintInfo[]> {
    return this.http.get<VsBlueprintInfo[]>(this.baseUrl + this.vsBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched vsBlueprintInfos', 'SUCCESS')),
        catchError(this.handleError<VsBlueprintInfo[]>('getVsBlueprints', []))
      );
  }

  getVsBlueprint(): Observable<VsBlueprintInfo> {
    return this.http.get<VsBlueprintInfo>(this.baseUrl + this.vsBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched vsBlueprintInfo', 'SUCCESS')),
        catchError(this.handleError<VsBlueprintInfo>('getVsBlueprint'))
      );
  }

  postVsBlueprint(onBoardVsRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.vsBlueprintInfoUrl, onBoardVsRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.log(`added VS Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postVsBlueprint'))
      );
  }

  deleteVsBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.vsBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted VS Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteVsBlueprint'))
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
