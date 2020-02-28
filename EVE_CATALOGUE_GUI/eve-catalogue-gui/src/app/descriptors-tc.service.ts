import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TcDescriptorInfo} from './descriptors-tc/tc-descriptor-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsTcService {

  private baseUrl = environment.portalBaseUrl;
  private tcDescriptorInfoUrl = 'testcasedescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getTcDescriptors(): Observable<TcDescriptorInfo[]> {
    return this.http.get<TcDescriptorInfo[]>(this.baseUrl + this.tcDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcDescriptorInfos - SUCCESS')),
        catchError(this.authService.handleError<TcDescriptorInfo[]>('getTcDescriptors', []))
      );
  }

  getTcDescriptor(tcDescriptorId: string): Observable<TcDescriptorInfo> {
    return this.http.get<TcDescriptorInfo>(this.baseUrl + this.tcDescriptorInfoUrl + "/" + tcDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched tcDescriptorInfo - SUCCESS')),
        catchError(this.authService.handleError<TcDescriptorInfo>('getTcBlueprint'))
      );
  }
}
