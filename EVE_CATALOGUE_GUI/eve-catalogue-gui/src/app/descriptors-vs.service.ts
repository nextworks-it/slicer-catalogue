import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { VsDescriptorInfo } from './descriptors-vs/vs-descriptor-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsVsService {

  private baseUrl = environment.portalBaseUrl;
  private vsDescriptorInfoUrl = 'vsdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getVsDescriptors(): Observable<VsDescriptorInfo[]> {
    return this.http.get<VsDescriptorInfo[]>(this.baseUrl + this.vsDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched vsDescriptorInfos', 'SUCCESS')),
        catchError(this.handleError<VsDescriptorInfo[]>('getVsDescriptors', []))
      );
  }

  getVsDescriptor(vsDescriptorId: string): Observable<VsDescriptorInfo> {
    return this.http.get<VsDescriptorInfo>(this.baseUrl + this.vsDescriptorInfoUrl + "/" + vsDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => this.log('fetched vsDescriptorInfo', 'SUCCESS')),
        catchError(this.handleError<VsDescriptorInfo>('getVsBlueprint'))
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
    this.messageService.add(`DescriptorsVsService: ${message}`);
    this.openSnackBar(`DescriptorsVsService: ${message}`, action);
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
