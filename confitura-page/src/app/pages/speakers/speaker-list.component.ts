import {Component} from '@angular/core';
import {UserService} from '../profile/user.service';
import {Observable} from 'rxjs/Observable';
import {User} from '../profile/user.model';


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
