import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MessageService } from './message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from './environments/environments';
import { Router } from '@angular/router';

import * as jwt_decode from "jwt-decode";

export class Token {
  access_token: string;
  refresh_token: string;
  username: string;
}

export class Role {
  id: string;
  name: string;
  clientRole: boolean;
  composite: boolean;
  containerId: string;
  description: string;
}

export class RoleDetails {
  details: Role[];
}

export class RegistrationDetails {
  details: {
    user_id: string;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = environment.rbacBaseUrl;
  private registerUrl = "register"
  private rolesInfoUrl = "realmroles";

  httpOptions = {
    headers: new HttpHeaders(
      { 'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      })
  };

  constructor(private http: HttpClient, 
    private messageService: MessageService, 
    private _snackBar: MatSnackBar, 
    private router: Router) { }


  registerUser(email: string, username: string, firstName: string, 
    lastName: string, password: string, role: Role): Observable<RegistrationDetails> {
    let data = {
        "email": email, 
        "username": username, 
        "firstName": firstName, 
        "lastName": lastName,
        "password": password,
        "roles": [{id: role.id, name: role.name, clientRole: role.clientRole}]
    };
    
    return this.http.post<RegistrationDetails>(this.baseUrl + this.registerUrl, data, this.httpOptions)
        .pipe(
            tap((data: RegistrationDetails) => {
                this.log(`login w/ id=${email}`, 'SUCCESS', false);
                return data;
            }),
            catchError(this.handleError<RegistrationDetails>('registerUser'))
    );        
  }

  getRoles(): Observable<RoleDetails> {
    return this.http.get<RoleDetails>(this.baseUrl + this.rolesInfoUrl, this.httpOptions)
      .pipe(
        tap(_ => console.log('fetched rolesDetails - SUCCESS')),
        catchError(this.handleError<RoleDetails>('getExpDescriptor'))
      );
  }
  

  login(loginInfo: Object, redirection: string): Observable<Token> {
    return this.http.post(this.baseUrl + 'login', loginInfo, this.httpOptions)
      .pipe(
        tap((token: Token) => 
        {
          this.log(`login w/ id=${loginInfo['email']}`, 'SUCCESS', false); 
          localStorage.setItem('token', token.access_token); 
          localStorage.setItem('refreshtoken', token.refresh_token);
          localStorage.setItem('logged', 'true')
          this.parseToken(token.access_token);
          this.router.navigate([redirection]).then(() => {
            window.location.reload();
          });
        }),
        catchError(this.handleError<Token>('login'))
      );
  }

  refresh(refreshInfo: Object): Observable<Token> {
    return this.http.post(this.baseUrl + 'refreshtoken', refreshInfo, this.httpOptions)
      .pipe(
        tap((token: Token) => 
        {
          localStorage.setItem('token', token.access_token); 
          localStorage.setItem('refreshtoken', token.refresh_token);
          this.log(`refresh login`, 'SUCCESS', false); 
        }),
        catchError(this.handleError<Token>('refresh'))
      );
  }

  logout(redirection: string): Observable<Token> {
    return this.http.get(this.baseUrl + 'logout', this.httpOptions)
      .pipe(
        tap((token: Token) => 
        {
          this.log(`logout`, 'SUCCESS', false);
          localStorage.removeItem('username');
          localStorage.removeItem('role');
          localStorage.removeItem('roles');
          localStorage.removeItem('token');
          localStorage.removeItem('refreshtoken');
          localStorage.setItem('logged', 'false')
          this.router.navigate([redirection]).then(() => {
            window.location.reload();
          });
        }),
        catchError(this.handleError<Token>('logout'))
      );
  }

  parseToken(token: string) {
    var decodedToken = jwt_decode(token);
    console.log(JSON.stringify(decodedToken, null, 4));

    var username = decodedToken['preferred_username'];
    var name = decodedToken['name'];
    var email = decodedToken['email'];
    var roles = decodedToken['realm_access']['roles'];

    //this.usersService.setUser({roles, name, email, username});

    localStorage.setItem('roles', roles.toString());
    localStorage.setItem('username', username);
  }

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

      if (error.status == 401 /*|| error.status == 400*/) {
        console.log("401 after " + operation);
        if (operation.indexOf('refresh') >= 0 || operation.indexOf('login') >= 0) {
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
          this.log(`${operation} failed: ${error.message}`, 'FAILED', false);
          this.refresh({
            access_token: localStorage.getItem('token'),
            refresh_token: localStorage.getItem('refreshtoken')
          }).subscribe(token => {
            if (token) {
              console.log('Token successfully refreshed after 401');
            }
          });
        }
        
      } else {
        if (error.status == 400) {
          if (operation.indexOf('refresh') >= 0 || operation.indexOf('login') >= 0) {
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
          }
        } else {
          console.log(error.status + " after " + operation);
          this.log(`${operation} failed: ${error.message}`, 'FAILED', false);      
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
      duration: 0,
    }).afterDismissed().subscribe(() => {
      console.log('The snack-bar was dismissed');
      if (reload)
        window.location.reload();
    });  
  }
}
