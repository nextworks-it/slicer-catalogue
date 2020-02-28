import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ExpDescriptorInfo } from './descriptors-e/exp-descriptor-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsExpService {

  private baseUrl = environment.portalBaseUrl;
  private expDescriptorInfoUrl = 'expdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient, 
    private authService: AuthService) { }

  getExpDescriptors(): Observable<ExpDescriptorInfo[]> {
    return this.http.get<ExpDescriptorInfo[]>(this.baseUrl + this.expDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expDescriptorInfos - SUCCESS')),
        catchError(this.authService.handleError<ExpDescriptorInfo[]>('getExpDescriptors', []))
      );
  }

  getExpDescriptor(expDescriptorId: string): Observable<ExpDescriptorInfo> {
    return this.http.get<ExpDescriptorInfo>(this.baseUrl + this.expDescriptorInfoUrl + "/" + expDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched expDescriptorInfo - SUCCESS')),
        catchError(this.authService.handleError<ExpDescriptorInfo>('getExpDescriptor'))
      );
  }

  postExpDescriptor(onBoardExpRequest: Object): Observable<String> {
    return this.http.post(this.baseUrl + this.expDescriptorInfoUrl, onBoardExpRequest, this.httpOptions)
      .pipe(
        tap((descriptorId: String) => this.authService.log(`added Exp Descriptor w/ id=${descriptorId}`, 'SUCCESS', true)),
        catchError(this.authService.handleError<String>('postExpDescriptor'))
      );
  }

  deleteExpDescriptor(descriptorId: string): Observable<String> {
    return this.http.delete(this.baseUrl + this.expDescriptorInfoUrl + '/' + descriptorId, this.httpOptions)
    .pipe(
      tap((result: String) => this.authService.log(`deleted Exp Descriptor w/ id=${descriptorId}`, 'SUCCESS', true)),
      catchError(this.authService.handleError<String>('deleteExpDescriptor'))
    );
  }
}
