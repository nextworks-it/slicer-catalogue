import { Injectable } from '@angular/core';

export class User {
  roles: string[];
  name: string;
  email: string;
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  _user: User;

  constructor() { }

  setUser(user: User) {
    this._user = user;
  }

  getUser(): User {
    return this._user;
  }
}
