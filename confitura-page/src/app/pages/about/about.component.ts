import {Component, OnInit} from '@angular/core';
import {OrganizerService} from './organizer.service';
import {Observable} from 'rxjs';
import {Person} from './person.model';
import {PersonModalService} from '../../shared/person-modal/person-modal.service';
import {User} from '../../core/user/user.model';

@Component({
  templateUrl: './about.component.html',
  styleUrls: ['./about.scss'],

})
export class AboutComponent implements OnInit {
  organizers: Observable<Person[]> = null;
  volunteers: Observable<Person[]> = null;
  lat = 52.225101;
  lng = 20.962129;
  zoom = 17;


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
