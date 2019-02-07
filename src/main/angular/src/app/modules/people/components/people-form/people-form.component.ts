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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PersonModel} from '../../models/person.model';

@Component({
  selector: 'app-people-form',
  templateUrl: './people-form.component.html',
  styleUrls: ['./people-form.component.scss']
})
export class PeopleFormComponent {
  public activePerson: PersonModel;

  @Output()
  public readonly modify: EventEmitter<PersonModel>;
  @Output()
  public readonly clean: EventEmitter<never>;

  public name: string;
  public surname: string;

  public constructor() {
    this.modify = new EventEmitter<PersonModel>();
    this.clean = new EventEmitter<never>();
  }

  @Input()
  public set person(person: PersonModel) {
    this.activePerson = person;
    this.name = person.name;
    this.surname = person.surname;
  }

  public get person(): PersonModel {
    return this.activePerson;
  }

  public onModify() {
    this.modify.emit({
      id: this.person.id,
      name: this.name,
      surname: this.surname
    });
  }

  public onClean() {
    this.clean.emit();
  }
}
