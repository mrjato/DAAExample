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

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UnauthenticatedGuard} from './guards/unauthenticated.guard';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {LoginPanelComponent} from './components/login-panel/login-panel.component';
import {MainPanelComponent} from './components/main-panel/main-panel.component';

const routes: Routes = [
  {
    path: 'welcome',
    pathMatch: 'full',
    component: LoginPanelComponent,
    canActivate: [UnauthenticatedGuard]
  },
  {
    path: '',
    component: MainPanelComponent,
    canActivate: [AuthenticatedGuard],
    children: [
      {
        path: '',
        redirectTo: 'people',
        pathMatch: 'full'
      },
      {
        path: 'people',
        loadChildren: () => import('./modules/people/people.module').then(m => m.PeopleModule)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
