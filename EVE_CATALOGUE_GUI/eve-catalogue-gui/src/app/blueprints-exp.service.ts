import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExpBlueprintInfo } from './blueprints-e/exp-blueprint-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsExpService {

  private baseUrl = environment.portalBaseUrl;
  private expBlueprintInfoUrl = 'expblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getExpBlueprints(): Observable<ExpBlueprintInfo[]> {
    return this.http.get<ExpBlueprintInfo[]>(this.baseUrl + this.expBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched expBlueprintInfos', 'SUCCESS')),
        catchError(this.handleError<ExpBlueprintInfo[]>('getExpBlueprints', []))
      );
  }

  getExpBlueprint(expBlueprintId: string): Observable<ExpBlueprintInfo> {
    return this.http.get<ExpBlueprintInfo>(this.baseUrl + this.expBlueprintInfoUrl + "/" + expBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched expBlueprintInfo', 'SUCCESS')),
        catchError(this.handleError<ExpBlueprintInfo>('getExpBlueprint'))
      );
  }

  postExpBlueprint(onBoardExpRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.expBlueprintInfoUrl, onBoardExpRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.log(`added Exp Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postExpBlueprint'))
      );
  }

  deleteExpBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.expBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted Exp Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteExpBlueprint'))
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
    this.messageService.add(`BluepritsExpService: ${message}`);
    this.openSnackBar(`BluepritsExpService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
