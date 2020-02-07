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

import { Component, OnInit } from '@angular/core';
import {PersonModel} from '../../models/person.model';
import {PeopleService} from '../../services/people.service';
import {map, mergeMap} from 'rxjs/operators';

@Component({
  selector: 'app-people-main',
  templateUrl: './people-main.component.html',
  styleUrls: ['./people-main.component.scss']
})
export class PeopleMainComponent implements OnInit {
  public activePerson: PersonModel;
  public people: PersonModel[];

  public constructor(
    private readonly peopleService: PeopleService
  ) {
    this.people = [];
    this.clearActivePerson();
  }

  public ngOnInit(): void {
    this.peopleService.list()
      .subscribe(people => this.people = people);
  }

  public onEdit(person: PersonModel): void {
    this.activePerson = person;
  }

  public onDelete(person: PersonModel): void {
    if (confirm(`¿Estás seguro de que deseas eliminar a ${person.name} ${person.surname}?`)) {
      this.peopleService.delete(person)
        .pipe(
          mergeMap(() => this.peopleService.list())
        )
        .subscribe(people => this.people = people);
    }
  }

  public onCleanForm(): void {
    this.clearActivePerson();
  }

  public onModifyForm(person: PersonModel): void {
    if (person.id === undefined) {
      this.peopleService.create(person)
        .pipe(
          mergeMap(() => this.peopleService.list())
        )
        .subscribe(people => {
          this.people = people;
          this.clearActivePerson();
        });
    } else {
      this.peopleService.modify(person)
        .pipe(
          mergeMap(() => this.peopleService.list())
        )
        .subscribe(people => {
          this.people = people;
          this.clearActivePerson();
        });
    }
  }

  private clearActivePerson(): void {
    this.activePerson = { id: undefined, name: '', surname: '' };
  }
}
