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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PeopleRoutingModule} from './people-routing.module';
import {PeopleListComponent} from './components/people-list/people-list.component';
import {PeopleFormComponent} from './components/people-form/people-form.component';
import {PeopleMainComponent} from './components/people-main/people-main.component';
import {FormsModule} from '@angular/forms';

@NgModule({
  declarations: [
    PeopleFormComponent,
    PeopleListComponent,
    PeopleMainComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    PeopleRoutingModule
  ]
})
export class PeopleModule {
}
