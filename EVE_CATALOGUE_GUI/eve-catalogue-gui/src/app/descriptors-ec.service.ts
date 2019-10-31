import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EcDescriptorInfo} from './descriptors-ec/ec-descriptor-info';
import { environment } from './environments/environments';


@Injectable({
  providedIn: 'root'
})
export class DescriptorsEcService {

  private baseUrl = environment.portalBaseUrl;
  private ctxDescriptorInfoUrl = 'ctxdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getCtxDescriptors(): Observable<EcDescriptorInfo[]> {
    return this.http.get<EcDescriptorInfo[]>(this.baseUrl + this.ctxDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched ecDescriptorInfos', 'SUCCESS')),
        catchError(this.handleError<EcDescriptorInfo[]>('getCtxDescriptors', []))
      );
  }

  getCtxDescriptor(ecDescriptorId: string): Observable<EcDescriptorInfo> {
    return this.http.get<EcDescriptorInfo>(this.baseUrl + this.ctxDescriptorInfoUrl + "/" + ecDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched ecDescriptorInfo', 'SUCCESS')),
        catchError(this.handleError<EcDescriptorInfo>('getCtxBlueprint'))
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
    this.messageService.add(`DescriptorsEcService: ${message}`);
    this.openSnackBar(`DescriptorsEcService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
