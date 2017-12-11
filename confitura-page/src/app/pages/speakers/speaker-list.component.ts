import {Component} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {User} from '../../core/user/user.model';
import {UserService} from '../../core/user/user.service';

@Component({
  templateUrl: './speaker-list.component.html',
  styleUrls: ['./speaker-list.component.scss']

})
export class SpeakerListComponent {
  list: Observable<User[]>;

  constructor(private service: UserService) {
    this.list = this.service.getAllSpeakers();
  }
}
