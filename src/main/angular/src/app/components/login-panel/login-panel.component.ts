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

import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../services/authentication.service';
import {UserModel} from '../../models';

@Component({
  selector: 'app-login-panel',
  templateUrl: './login-panel.component.html',
  styleUrls: ['./login-panel.component.scss']
})
export class LoginPanelComponent {
  public login: string;
  public password: string;

  public constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly router: Router
  ) {
    this.login = '';
    this.password = '';
  }

  public onLogin(): void {
    const credentials: UserModel = {
      login: this.login,
      password: this.password
    };

    this.authenticationService.tryLogin(credentials)
      .subscribe(() => this.router.navigate(['/']));
  }

}
