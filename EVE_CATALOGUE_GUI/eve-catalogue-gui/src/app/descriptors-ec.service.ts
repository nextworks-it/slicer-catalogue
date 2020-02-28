import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { EcDescriptorInfo} from './descriptors-ec/ec-descriptor-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsEcService {

  private baseUrl = environment.portalBaseUrl;
  private ctxDescriptorInfoUrl = 'ctxdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getCtxDescriptors(): Observable<EcDescriptorInfo[]> {
    return this.http.get<EcDescriptorInfo[]>(this.baseUrl + this.ctxDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ecDescriptorInfos - SUCCESS')),
        catchError(this.authService.handleError<EcDescriptorInfo[]>('getCtxDescriptors', []))
      );
  }

  getCtxDescriptor(ecDescriptorId: string): Observable<EcDescriptorInfo> {
    return this.http.get<EcDescriptorInfo>(this.baseUrl + this.ctxDescriptorInfoUrl + "/" + ecDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched ecDescriptorInfo - SUCCESS')),
        catchError(this.authService.handleError<EcDescriptorInfo>('getCtxBlueprint'))
      );
  }
}
