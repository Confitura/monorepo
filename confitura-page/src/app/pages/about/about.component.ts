import {Component, OnInit} from '@angular/core';
import {OrganizerService} from './organizer.service';
import {Observable} from 'rxjs/Observable';
import {Person} from './person.model';
import {User} from '../profile/user.model';
import {PersonModalService} from '../../shared/person-modal/person-modal.service';

@Component({
  templateUrl: './about.component.html',
  styleUrls: ['./about.scss'],

})
export class AboutComponent implements OnInit {
  organizers: Observable<Person[]> = null;
  volunteers: Observable<Person[]> = null;

  constructor(private service: OrganizerService,
              private personModalService: PersonModalService) {

  }

  ngOnInit(): void {
    this.organizers = this.service.getAllOrganizers();
    this.volunteers = this.service.getAllVolunteers();
  }

  show(user: User) {
    this.personModalService.showFor(user);
  }


}
