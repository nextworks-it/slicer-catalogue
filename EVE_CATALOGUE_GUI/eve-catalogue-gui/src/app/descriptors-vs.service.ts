import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { VsDescriptorInfo } from './descriptors-vs/vs-descriptor-info';
import { environment } from './environments/environments';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DescriptorsVsService {

  private baseUrl = environment.portalBaseUrl;
  private vsDescriptorInfoUrl = 'vsdescriptor';

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient,
    private authService: AuthService) { }

  getVsDescriptors(): Observable<VsDescriptorInfo[]> {
    return this.http.get<VsDescriptorInfo[]>(this.baseUrl + this.vsDescriptorInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched vsDescriptorInfos - SUCCESS')),
        catchError(this.authService.handleError<VsDescriptorInfo[]>('getVsDescriptors', []))
      );
  }

  getVsDescriptor(vsDescriptorId: string): Observable<VsDescriptorInfo> {
    return this.http.get<VsDescriptorInfo>(this.baseUrl + this.vsDescriptorInfoUrl + "/" + vsDescriptorId, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched vsDescriptorInfo - SUCCESS')),
        catchError(this.authService.handleError<VsDescriptorInfo>('getVsBlueprint'))
      );
  }
}
