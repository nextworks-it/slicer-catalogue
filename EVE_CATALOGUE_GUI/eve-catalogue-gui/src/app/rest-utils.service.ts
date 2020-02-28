import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RestUtilsService {

  constructor(private messageService: MessageService,
    private _snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router) { }

    /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      if (error.status == 401 && operation != 'refresh') {
        this.authService.refresh({
          access_token: localStorage.getItem('token'),
          refresh_token: localStorage.getItem('refreshtoken')
        }).subscribe(token => {
          if (token) {
            this.log('Token successfully refreshed, try again.', 'SUCCESS', true);
          }
        });
      } else {
        if (error.status == 401) {
          // TODO: better job of transforming error for user consumption
          this.log(`${operation} failed: ${error.message}`, 'FAILED', false);
          localStorage.removeItem('username');
          localStorage.removeItem('role');
          localStorage.removeItem('roles');
          localStorage.removeItem('token');
          localStorage.removeItem('refreshtoken');
          this.router.navigate(['/login']).then(() => {
            window.location.reload();
          });
        } else {
          this.log(`${operation} failed: ${error.message}`, 'FAILED', true);
        }        
      }   

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a Service message with the MessageService */
  log(message: string, action: string, reload: boolean) {
    this.messageService.add(`BluepritsTcService: ${message}`);
    this.openSnackBar(`BluepritsTcService: ${message}`, action, reload);
  }

  openSnackBar(message: string, action: string, reload: boolean) {
    this._snackBar.open(message, action, {
      duration: 5000,
    });
    if (reload)
      window.location.reload();
  }
}
