import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExperimentInfo } from './experiments/experiment-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class ExperimentsService {

  private baseUrl = environment.lcmBaseUrl;
  private experimentInfoUrl = 'experiment';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getExperiments(): Observable<ExperimentInfo[]> {
    return this.http.get<ExperimentInfo[]>(this.baseUrl + this.experimentInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched experimentInfos', 'SUCCESS')),
        catchError(this.handleError<ExperimentInfo[]>('getExperiments', []))
      );
  }

  getExperiment(experimentId: string, expDId): Observable<ExperimentInfo[]> {
    var requestParams = '';
    if (experimentId != null) {
      requestParams += '?expId=' + experimentId;
    }
    if (expDId != null) {
      requestParams += '?expDId=' + expDId;
    }
    return this.http.get<ExperimentInfo[]>(this.baseUrl + this.experimentInfoUrl + requestParams, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched experimentInfos', 'SUCCESS')),
        catchError(this.handleError<ExperimentInfo[]>('getExperiment', []))
      );
  }

  postExperiment(expRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.experimentInfoUrl, expRequest, this.httpOptions)
      .pipe(
        tap((experimentId: String) => this.log(`created Experiment w/ id=${experimentId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postExperiment'))
      );
  }

  deleteExperiment(experimentId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.experimentInfoUrl + '/' + experimentId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted Experiment w/ id=${experimentId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteExperiment'))
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
    this.messageService.add(`ExperimentsService: ${message}`);
    this.openSnackBar(`ExperimentsService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
