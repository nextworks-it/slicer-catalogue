import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExpDescriptorInfo } from './descriptors-e/exp-descriptor-info';
import { environment } from './environments/environments';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsExpService {

  private baseUrl = environment.portalBaseUrl;
  private expDescriptorInfoUrl = 'expdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient, private messageService: MessageService, private _snackBar: MatSnackBar) { }

  getExpDescriptors(): Observable<ExpDescriptorInfo[]> {
    return this.http.get<ExpDescriptorInfo[]>(this.baseUrl + this.expDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expDescriptorInfos - SUCCESS')),
        catchError(this.handleError<ExpDescriptorInfo[]>('getExpDescriptors', []))
      );
  }

  getExpDescriptor(expDescriptorId: string): Observable<ExpDescriptorInfo> {
    return this.http.get<ExpDescriptorInfo>(this.baseUrl + this.expDescriptorInfoUrl + "/" + expDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expDescriptorInfo - SUCCESS')),
        catchError(this.handleError<ExpDescriptorInfo>('getExpBlueprint'))
      );
  }

  postExpDescriptor(onBoardExpRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.expDescriptorInfoUrl, onBoardExpRequest, this.httpOptions)
      .pipe(
        tap((descriptorId: String) => this.log(`added Exp Descriptor w/ id=${descriptorId}`, 'SUCCESS')),
        catchError(this.handleError<String>('postExpBlueprint'))
      );
  }

  deleteExpDescriptor(descriptorId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.expDescriptorInfoUrl + '/' + descriptorId, this.httpOptions)
    .pipe(
      tap((result: String) => this.log(`deleted Exp Descriptor w/ id=${descriptorId}`, 'SUCCESS')),
      catchError(this.handleError<String>('deleteExpDescriptor'))
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
    this.messageService.add(`DescriptorsExpService: ${message}`);
    this.openSnackBar(`DescriptorsExpService: ${message}`, action);
    window.location.reload();
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
  }
}
