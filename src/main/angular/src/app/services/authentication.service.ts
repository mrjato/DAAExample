/*
 * DAA Example
 *
 * Copyright (C) 2019 - Miguel Reboiro-Jato.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {UserModel} from '../models';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private static readonly USER_STORAGE_KEY = 'user';

  private readonly _loggedUser$: BehaviorSubject<UserModel | null>;

  private static clearCredentials(): void {
    localStorage.removeItem(AuthenticationService.USER_STORAGE_KEY);
  }

  private static loadCredentials(): UserModel | null {
    const serializedCredentials = localStorage.getItem(AuthenticationService.USER_STORAGE_KEY);

    if (serializedCredentials !== null) {
      return JSON.parse(serializedCredentials);
    } else {
      return null;
    }
  }

  private static storeCredentials(login: string, password: string): void {
    const credentials: UserModel = {login, password};

    localStorage.setItem(AuthenticationService.USER_STORAGE_KEY, JSON.stringify(credentials));
  }

  public constructor(
    private readonly http: HttpClient
  ) {
    this._loggedUser$ = new BehaviorSubject<UserModel | null>(null);
  }

  public get loggedUser$(): Observable<UserModel | null> {
    return this._loggedUser$.asObservable();
  }

  public get loggedUser(): UserModel | null {
    return this._loggedUser$.value;
  }

  public isLogged(): boolean {
    return this.loggedUser !== null;
  }

  public logout(): void {
    AuthenticationService.clearCredentials();
    this._loggedUser$.next(null);
  }

  public tryLoginWithStoredCredentials(): Observable<UserModel | null> {
    const credentials = AuthenticationService.loadCredentials();

    if (credentials !== null) {
      return this.tryLogin(credentials);
    } else {
      return of<UserModel | null>(null);
    }
  }

  public tryLogin(login: UserModel): Observable<UserModel | null>;
  public tryLogin(login: string, password: string): Observable<UserModel | null>;

  public tryLogin(login: string | UserModel, password?: string): Observable<UserModel> {
    let userLogin: string;
    let userPassword: string;

    if (typeof login === 'string') {
      if (password === undefined) {
        throw new TypeError('password can\'t be null when login is string');
      }

      userLogin = login;
      userPassword = password;
    } else {
      userLogin = login.login;
      userPassword = login.password;
    }
    AuthenticationService.storeCredentials(userLogin, userPassword);

    const headers = new HttpHeaders()
      .set('Authorization', 'Basic ' + btoa(userLogin + ':' + userPassword));

    return this.http.get<never>(`${environment.restApi}/users/${userLogin}`, {
      headers: headers
    })
      .pipe(
        tap(user => this._loggedUser$.next({
          login: userLogin,
          password: userPassword
        })),
        catchError(error => {
          this._loggedUser$.next(null);
          throw error;
        })
      );
  }
}
