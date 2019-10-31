import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TcDescriptorInfo} from './descriptors-tc/tc-descriptor-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsTcService {

  private baseUrl = environment.portalBaseUrl;
  private tcDescriptorInfoUrl = 'testcasedescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getTcDescriptors(): Observable<TcDescriptorInfo[]> {
    return this.http.get<TcDescriptorInfo[]>(this.baseUrl + this.tcDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched tcDescriptorInfos', 'SUCCESS')),
        catchError(this.handleError<TcDescriptorInfo[]>('getTcDescriptors', []))
      );
  }

  getTcDescriptor(tcDescriptorId: string): Observable<TcDescriptorInfo> {
    return this.http.get<TcDescriptorInfo>(this.baseUrl + this.tcDescriptorInfoUrl + "/" + tcDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched tcDescriptorInfo', 'SUCCESS')),
        catchError(this.handleError<TcDescriptorInfo>('getTcBlueprint'))
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
    this.messageService.add(`DescriptorsTcService: ${message}`);
    this.openSnackBar(`DescriptorsTcService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
