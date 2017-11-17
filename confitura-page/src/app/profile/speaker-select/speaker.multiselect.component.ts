import {Component, forwardRef, Input} from '@angular/core';
import {User} from '../../pages/profile/user.model';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {UserService} from '../../pages/profile/user.service';

/* based on https://blog.thoughtram.io/angular/2016/07/27/custom-form-controls-in-angular-2.html */
@Component({
  selector: 'cf-speakers-select',
  templateUrl: './speaker.multiselect.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SpeakerSelectComponent),
      multi: true
    }
  ]
})
export class SpeakerSelectComponent implements ControlValueAccessor {
  speakers: User[] = [];
  query = '';
  @Input() value: User[] = [];
  @Input() ownerId: string;

  constructor(private userService: UserService) {
  }

  search(query: string) {
    this.query = query;
    if (query.length >= 5) {
      this.userService
        .find(this.query)
        .subscribe(users => {
          this.speakers = users
            .filter(this.notAnOwner)
            .filter(this.notYetSelected);

        });
    } else {
      this.speakers = [];
    }
  }


  writeValue(value: any): void {
    if (value !== null && value !== undefined) {
      this.value = value;
    }
  }

  addUser(user: User) {
    const index = this.findUser(user);
    if (index < 0) {
      this.value.push(user);
      this.propagateChange(this.value);
      this.query = '';
      this.speakers = [];
    }
  }

  removeUser(user: User) {
    const index = this.findUser(user);
    if (index > -1) {
      this.value.splice(index, 1);
      this.propagateChange(this.value);
    }
  }

  private findUser(user: User) {
    let index = -1;
    this.value.forEach((v, i) => {
      if (v.id === user.id) {
        index = i;
      }
    });
    return index;
  }


  propagateChange(_: any) {
  }

  registerOnChange(fn: any) {
    this.propagateChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  notYetSelected(user: User) {
    return this.value.map(speaker => speaker.id).indexOf(user.id) === -1;
  }

  notAnOwner(user: User) {
    return user.id !== this.ownerId;
  }
}
