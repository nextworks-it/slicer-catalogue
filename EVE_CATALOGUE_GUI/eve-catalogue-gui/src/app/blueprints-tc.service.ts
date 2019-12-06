import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TcBlueprintInfo } from './blueprints-tc/tc-blueprint-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class BlueprintsTcService {

  private baseUrl = environment.portalBaseUrl;
  private tcBlueprintInfoUrl = 'testcaseblueprint';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getTcBlueprints(): Observable<TcBlueprintInfo[]> {
    return this.http.get<TcBlueprintInfo[]>(this.baseUrl + this.tcBlueprintInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcBlueprintInfos - SUCCESS')),
        catchError(this.handleError<TcBlueprintInfo[]>('getTcBlueprints', []))
      );
  }

  getTcBlueprint(tcBlueprintId: string): Observable<TcBlueprintInfo> {
    return this.http.get<TcBlueprintInfo>(this.baseUrl + this.tcBlueprintInfoUrl + "/" + tcBlueprintId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcBlueprintInfo - SUCCESS')),
        catchError(this.handleError<TcBlueprintInfo>('getTcBlueprint'))
      );
  }

  postTcBlueprint(onBoardTcRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.tcBlueprintInfoUrl, onBoardTcRequest, this.httpOptions)
      .pipe(
        tap((blueprintId: String) => this.log(`added TC Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postTcBlueprint'))
      );
  }

  deleteTcBlueprint(blueprintId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.tcBlueprintInfoUrl + '/' + blueprintId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted TC Blueprint w/ id=${blueprintId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteTcBlueprint'))
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
    this.messageService.add(`BluepritsTcService: ${message}`);
    this.openSnackBar(`BluepritsTcService: ${message}`, action);
    window.location.reload();
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
